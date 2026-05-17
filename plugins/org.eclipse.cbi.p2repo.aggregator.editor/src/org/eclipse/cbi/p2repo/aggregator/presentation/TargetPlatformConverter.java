/**
 * Copyright (c) 2026 Eclipse contributors and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.cbi.p2repo.aggregator.presentation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.cbi.p2repo.aggregator.AggregatorFactory;
import org.eclipse.cbi.p2repo.aggregator.AggregatorPackage;
import org.eclipse.cbi.p2repo.aggregator.MappedRepository;
import org.eclipse.cbi.p2repo.aggregator.MappedUnit;
import org.eclipse.cbi.p2repo.aggregator.util.ResourceUtils;
import org.eclipse.cbi.p2repo.p2.util.P2Utils;
import org.eclipse.cbi.p2repo.util.IOUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

public final class TargetPlatformConverter {
	private TargetPlatformConverter() {
	}

	public static final Comparator<MappedUnit> MAPPED_UNIT_COMPARATOR = (o1, o2) -> {
		var result = o1.getName().compareTo(o2.getName());
		if (result == 0) {
			var vr1 = o1.getVersionRange();
			var vr2 = o2.getVersionRange();
			result = vr1.getMinimum().compareTo(vr2.getMinimum());
			if (result == 0) {
				result = vr1.getMaximum().compareTo(vr2.getMaximum());
			}
		}
		return result;
	};

	public static List<Action> createAddtionalActions(EditingDomain domain, List<?> selection) {
		var result = new ArrayList<Action>();
		if (selection.size() == 1) {
			var aggregation = ResourceUtils.getAggregation(domain.getResourceSet());
			if (aggregation != null && selection.get(0) instanceof AnyType anyType
					&& "target".equals(anyType.eResource().getURI().fileExtension())
					&& "target".equals(anyType.eClass().getName())) {

				var repositoryLocations = new TreeSet<String>();
				var units = new TreeSet<MappedUnit>(MAPPED_UNIT_COMPARATOR);
				var locationToRepositoryLocations = new LinkedHashMap<EObject, Set<String>>();
				var locationToUnits = new LinkedHashMap<EObject, Set<MappedUnit>>();

				for (var eAllContents = anyType.eAllContents(); eAllContents.hasNext();) {
					var content = eAllContents.next();
					switch (content.eContainmentFeature().getName()) {
						case "repository": {
							var location = get(content, "location");
							if (location != null) {
								repositoryLocations.add(location);
							}
							locationToRepositoryLocations.computeIfAbsent(content.eContainer(), it -> new TreeSet<>())
									.add(location);
							break;
						}
						case "unit": {
							var id = get(content, "id");
							if (id != null) {
								var unit = id.endsWith(".feature.group") ? AggregatorFactory.eINSTANCE.createFeature()
										: AggregatorFactory.eINSTANCE.createBundle();
								unit.setName(id);
								var version = get(content, "version");
								unit.setVersionRange(
										version == null ? VersionRange.emptyRange : VersionRange.create(version));
								units.add(unit);
								locationToUnits.computeIfAbsent(content.eContainer(),
										it -> new TreeSet<>(MAPPED_UNIT_COMPARATOR)).add(unit);
							}
							break;
						}
					}
				}

				if (!repositoryLocations.isEmpty() && !units.isEmpty()) {
					var mappedRepositories = new ArrayList<MappedRepository>();
					IRunnableWithProgress operation = monitor -> {
						Path agentLocation = null;
						try {
							agentLocation = Files.createTempDirectory("cbi-convert-target-platform");
							var agent = P2Utils.createDedicatedProvisioningAgent(agentLocation.toUri());
							var manager = P2Utils.getRepositoryManager(agent, IMetadataRepositoryManager.class);
							for (var location : repositoryLocations) {
								if (monitor.isCanceled()) {
									break;
								}

								var repository = manager.loadRepository(URI.create(location), monitor);
								var mappedRepository = AggregatorFactory.eINSTANCE.createMappedRepository();
								mappedRepository.setLocation(location);
								mappedRepositories.add(mappedRepository);

								var containingLocations = locationToRepositoryLocations.entrySet().stream()
										.filter(it -> it.getValue().contains(location)).toList();
								var unitsToProcess = containingLocations.size() == 1
										&& containingLocations.get(0).getValue().size() == 1
												? locationToUnits.get(containingLocations.get(0).getKey())
												: units;
								for (var unit : unitsToProcess) {
									var resolvedUnits = repository
											.query(QueryUtil.createIUQuery(unit.getName(), unit.getVersionRange()),
													null)
											.toSet();
									var unitCopy = (MappedUnit) EcoreUtil.copy((EObject) unit);
									if (resolvedUnits.isEmpty()) {
										// If there is only one repository for this location, then add it anyway, but log a problem.
										if (unitsToProcess != units) {
											// Log a problem.
											AggregatorEditorPlugin.INSTANCE.log("Unit " + unit.getName() + ":"
													+ unit.getVersionRange() + " cannot be resolved in " + location);
											mappedRepository.addUnit(unitCopy);
										}
									} else {
										mappedRepository.addUnit(unitCopy);
									}
								}
							}
						} catch (Exception ex) {
							throw new InvocationTargetException(ex);
						} finally {
							if (agentLocation != null) {
								try {
									IOUtils.delete(agentLocation);
								} catch (IOException e) {
									// Ignore
								}
							}
						}
					};

					var validationSet = AggregatorFactory.eINSTANCE.createValidationSet();
					validationSet.setLabel(anyType.eResource().getURI().lastSegment());
					var contribution = AggregatorFactory.eINSTANCE.createContribution();
					contribution.setLabel(get(anyType, "name"));
					validationSet.getContributions().add(contribution);

					result.add(new Action("Convert to new Validation Set") {
						@Override
						public void run() {
							try {
								new ProgressMonitorDialog(
										PlatformUI.getWorkbench().getModalDialogShellProvider().getShell()).run(true,
												true, operation);
								contribution.getRepositories().addAll(mappedRepositories);
								var command = AddCommand.create(domain, aggregation,
										AggregatorPackage.Literals.AGGREGATION__VALIDATION_SETS, validationSet);
								domain.getCommandStack().execute(command);
							} catch (InvocationTargetException e) {
								AggregatorEditorPlugin.INSTANCE.log(e);
							} catch (InterruptedException e) {
								// Ignore
							}
						}
					});
				}
			}
		}

		return result;
	}

	private static String get(EObject eObject, String attribute) {
		if (eObject instanceof AnyType anyType) {
			for (var entry : anyType.getAnyAttribute()) {
				if (attribute.equals(entry.getEStructuralFeature().getName())) {
					return entry.getValue().toString();
				}
			}
		}
		return null;
	}
}
