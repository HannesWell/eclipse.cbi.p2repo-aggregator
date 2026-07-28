/**
 * Copyright (c) 2021 Eclipse Foundation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.cbi.p2repo.aggregator.analyzer.presentation.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.eclipse.cbi.p2repo.aggregator.Aggregation;
import org.eclipse.cbi.p2repo.aggregator.Contribution;
import org.eclipse.cbi.p2repo.aggregator.MappedRepository;
import org.eclipse.cbi.p2repo.aggregator.MappedUnit;
import org.eclipse.cbi.p2repo.aggregator.MavenMapping;
import org.eclipse.cbi.p2repo.aggregator.MetadataRepositoryReference;
import org.eclipse.cbi.p2repo.aggregator.ValidationSet;
import org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerFactory;
import org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution;
import org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution;
import org.eclipse.cbi.p2repo.aggregator.analyzer.presentation.AggregationAnalyzerEditorPlugin;
import org.eclipse.cbi.p2repo.aggregator.analyzer.util.AnalyzerUtil;
import org.eclipse.cbi.p2repo.aggregator.engine.Builder;
import org.eclipse.cbi.p2repo.aggregator.engine.Builder.ActionType;
import org.eclipse.cbi.p2repo.aggregator.engine.maven.InstallableUnitMapping;
import org.eclipse.cbi.p2repo.aggregator.engine.maven.MavenManager;
import org.eclipse.cbi.p2repo.aggregator.p2.util.MetadataRepositoryResourceFactoryImpl;
import org.eclipse.cbi.p2repo.aggregator.p2view.util.MetadataRepositoryStructuredViewBuilder;
import org.eclipse.cbi.p2repo.aggregator.provider.MavenMappingItemProvider;
import org.eclipse.cbi.p2repo.aggregator.util.InstallableUnitUtils;
import org.eclipse.cbi.p2repo.p2.InstallableUnit;
import org.eclipse.cbi.p2repo.p2.MetadataRepository;
import org.eclipse.cbi.p2repo.p2.P2Factory;
import org.eclipse.cbi.p2repo.p2.impl.MetadataRepositoryImpl;
import org.eclipse.cbi.p2repo.p2.maven.metadata.MetaData;
import org.eclipse.cbi.p2repo.p2.maven.metadata.MetadataPackage;
import org.eclipse.cbi.p2repo.p2.maven.metadata.Versioning;
import org.eclipse.cbi.p2repo.p2.maven.metadata.VersionsType;
import org.eclipse.cbi.p2repo.p2.maven.util.VersionUtil;
import org.eclipse.cbi.p2repo.p2.util.P2Bridge;
import org.eclipse.cbi.p2repo.p2.util.P2Utils;
import org.eclipse.cbi.p2repo.util.MonitorUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.equinox.internal.p2.metadata.IRequiredCapability;
import org.eclipse.equinox.internal.p2.metadata.RequiredPropertiesMatch;
import org.eclipse.equinox.internal.p2.metadata.ResolvedInstallableUnit;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.metadata.expression.IFilterExpression;
import org.eclipse.equinox.p2.metadata.expression.IMatchExpression;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;

public class AnalyzeHandler extends BaseHandler {

	public static final URI ANALYSIS_RESULT = URI.createURI("readonly://result.aggran");

	public static final URI DUPLICATE_ANALYSIS_RESULT = URI.createURI("readonly://duplicate.result.aggran");

	public static final URI STRICT_REQUIREMENT_ANALYSIS_RESULT = URI
			.createURI("readonly://strict.requirement.result.aggran");

	public static final URI EXACT_REQUIREMENT_ANALYSIS_RESULT = URI
			.createURI("readonly://exact.requirement.result.aggran");

	public static String AGGREGATE_REPOSITORY_LOCATION = "readonly://aggregation.result.p2";

	public static String STRUCTURED_AGGREGATE_REPOSITORY_LOCATION = "readonly://aggregation.structured.result.p2";

	public static final URI STRUCTURED_AGGREGATE_REPOSITORY = (URI
			.createURI("p2aggr:p2:" + STRUCTURED_AGGREGATE_REPOSITORY_LOCATION));

	public static final Comparator<InstallableUnitAnalysis> INSTALLABLE_UNIT_ANALYIS_COMPARATOR = new Comparator<InstallableUnitAnalysis>() {
		public int compare(InstallableUnitAnalysis o1, InstallableUnitAnalysis o2) {
			return o1.getInstallableUnit().compareTo(o2.getInstallableUnit());
		}
	};

	@Override
	protected Activity createActivity(Analysis analysis) {
		return new Analyzer(analysis);
	}

	public static class Analyzer extends Activity {
		private Analysis originalAnalysis;
		private Analysis result;
		private ExecutorService executor;
		private Resource structuredAggregateMetadataRepositoryResource;
		private Builder builder;
		private List<InstallableUnitAnalysis> duplicates;
		private List<InstallableUnitAnalysis> strictRequirements;
		private List<InstallableUnitAnalysis> exactRequirements;
		private MetadataRepository aggregateMetadataRepository;

		public Analyzer(Analysis analysis) {
			this.originalAnalysis = analysis;
		}

		@Override
		public boolean isEnabled() {
			return getBuildModelLocation() != null;
		}

		public Analysis getOriginalAnalysis() {
			return originalAnalysis;
		}

		public URI getBuildModelLocation() {
			Aggregation aggregation = originalAnalysis.getAggregation();
			URI uri = EcoreUtil.getURI((EObject) aggregation).trimFragment();
			URI resolvedURI = CommonPlugin.resolve(uri);
			if (resolvedURI.isFile()) {
				return resolvedURI;
			}
			return null;
		}

		private Builder getBuilder(IProgressMonitor monitor) throws Exception {
			if (builder == null) {
				URI resolvedURI = getBuildModelLocation();
				if (resolvedURI != null) {
					builder = new Builder();
					builder.setAction(ActionType.VALIDATE);
					builder.setAnalyzeMaven(true);
					builder.setBuildModelLocation(new File(resolvedURI.toFileString()));
					builder.setProvisioningAgent(P2Utils.getDefaultProvisioningAgent());
					builder.run(false, monitor);
				}
			}
			return builder;
		}

		@Override
		protected void perform(IProgressMonitor monitor) throws Exception {
			result = analyze(monitor);
			duplicates = analyzeDuplicates(result);
			strictRequirements = analyzeStrictRequirements(result);
			exactRequirements = analyzeExactRequirements(result);
		}

		@Override
		protected void asyncExec() {
			Resource resource = ((EObject) getOriginalAnalysis()).eResource();
			if (resource != null) {
				ResourceSet resourceSet = resource.getResourceSet();
				EList<Resource> resources = resourceSet.getResources();

				XMLResource resultResource = (XMLResource) resourceSet.getResourceFactoryRegistry()
						.getFactory(ANALYSIS_RESULT).createResource(ANALYSIS_RESULT);
				resultResource.getContents().add(result);
				resources.add(resultResource);

				resources.add(structuredAggregateMetadataRepositoryResource);

				Resource duplicateResultResource = resourceSet.getResourceFactoryRegistry()
						.getFactory(DUPLICATE_ANALYSIS_RESULT).createResource(DUPLICATE_ANALYSIS_RESULT);
				duplicateResultResource.getContents().addAll(duplicates);
				resources.add(duplicateResultResource);

				Resource strictRequirementsResultResource = resourceSet.getResourceFactoryRegistry()
						.getFactory(STRICT_REQUIREMENT_ANALYSIS_RESULT)
						.createResource(STRICT_REQUIREMENT_ANALYSIS_RESULT);
				strictRequirementsResultResource.getContents().addAll(strictRequirements);
				resources.add(strictRequirementsResultResource);

				Resource exactRequirementsResultResource = resourceSet.getResourceFactoryRegistry()
						.getFactory(EXACT_REQUIREMENT_ANALYSIS_RESULT)
						.createResource(EXACT_REQUIREMENT_ANALYSIS_RESULT);
				exactRequirementsResultResource.getContents().addAll(exactRequirements);
				resources.add(exactRequirementsResultResource);
			}
		}

		private void gatherPOMs(SubMonitor monitor, Map<IInstallableUnit, InstallableUnit> modeledIUs,
				InstallableUnitMapping iuMapping, Pattern exclusion, Set<MavenMapping> mavenMappings,
				Map<String, Set<String>> localArtifactVersions, Collection<Future<?>> metadata,
				List<Runnable> versionRangeAnalyzers) {
			try {
				IInstallableUnit genuine = iuMapping.getGenuine();
				InstallableUnit iu = modeledIUs.get(genuine);
				if (iu != null && (exclusion == null || !exclusion.matcher(iu.getId()).matches())) {
					EMap<String, String> properties = iu.getPropertyMap();
					if (!properties.containsKey("maven-pom")) {
						ResourceSet resourceSet = ((EObject) genuine).eResource().getResourceSet();

						Model model = iuMapping.asPOM();
						mavenMappings.addAll(iuMapping.getUsedMavenMappings());
						String groupId = model.getGroupId();
						if (Boolean.FALSE) {
							String groupIdProperty = properties.get("maven-groupId");
							if (groupIdProperty != null && !groupIdProperty.equals(groupId)) {
								System.err.println(
										"- " + iu.getId() + " " + iu.getVersion().toString().replaceAll("\\.v.*", "")
												+ " " + "_" + groupId + "_" + " -> " + "**" + groupIdProperty + "**");
							}
						}
						String groupURL = getURL(groupId);
						URI groupURI = URI.createURI(groupURL);
						metadata.add(getExecutor().submit(() -> {
							if (!monitor.isCanceled()) {
								monitor.subTask("Analyzing Maven mappings " + groupURI);
								String groupFolderContent;
								synchronized (groupURI) {
									groupFolderContent = getContentOrEmpty(groupURI);
								}
								synchronized (properties) {
									properties.put("maven-group-exists",
											Objects.toString(!groupFolderContent.isEmpty()));
								}
							}
						}));

						String artifactId = model.getArtifactId();
						String artifactURL = getURL(groupId, artifactId);
						String artifactMetadataURL = artifactURL + "maven-metadata.xml";
						URI artifactMetadataURI = URI.createURI(artifactMetadataURL);
						metadata.add(getExecutor().submit(() -> {
							if (!monitor.isCanceled()) {
								monitor.subTask("Analyzing Maven mappings " + artifactMetadataURL);
								MetaData metaData = getMetaData(resourceSet, artifactMetadataURI);
								String version = model.getVersion();

								synchronized (metadata) {
									localArtifactVersions.computeIfAbsent(groupId + ":" + artifactId,
											key -> new LinkedHashSet<String>()).add(version);
								}

								String plainVersion = VersionUtil.versionNotAsSnapshot(version);
								String artifactPOMURL = artifactURL + plainVersion + "/" + artifactId + "-"
										+ plainVersion + ".pom";
								URI artifactPOMURI = URI.createURI(artifactPOMURL);
								String artifactPOM;
								synchronized (artifactPOMURI) {
									artifactPOM = getContentOrEmpty(artifactPOMURI);
								}

								synchronized (properties) {
									properties.put("maven-artifact-exists", Objects.toString(metaData != null));
									properties.put("maven-artifact-version-exists",
											Objects.toString(!artifactPOM.isEmpty()));
									properties.put("maven-pom", MavenManager.toXML(model));
								}
							}
						}));

						versionRangeAnalyzers.add(() -> {
							List<Dependency> dependencies = model.getDependencies();
							List<String> dependencyMapping = new ArrayList<String>();
							List<String> dependencyResolutions = new ArrayList<String>();
							for (Dependency dependency : dependencies) {
								InputLocation location = dependency.getLocation("");
								int lineNumber = location.getLineNumber();
								dependencyMapping.add(Integer.toString(lineNumber));

								org.apache.maven.artifact.versioning.VersionRange range;
								try {
									String dependencyVersionRange = dependency.getVersion()
											.replace(VersionUtil.DASH_SNAPSHOT, "");
									range = org.apache.maven.artifact.versioning.VersionRange
											.createFromVersionSpec(dependencyVersionRange);
								} catch (InvalidVersionSpecificationException e) {
									AggregationAnalyzerEditorPlugin.INSTANCE.log(e);
									dependencyResolutions.add("**invalid-version-range");
									continue;
								}

								String unresolved = "**" + range;
								String dependencyResolution = unresolved;
								String dependencyGroupId = dependency.getGroupId();
								String dependencyArtifactId = dependency.getArtifactId();
								URI dependencyMetadataURL = URI.createURI(
										getURL(dependencyGroupId, dependencyArtifactId) + "maven-metadata.xml");
								MetaData dependencyMetaData = getMetaData(resourceSet, dependencyMetadataURL);
								ArtifactVersion matchVersion = null;
								if (dependencyMetaData != null) {
									Versioning versioning = dependencyMetaData.getVersioning();
									VersionsType versionsType = versioning.getVersions();
									List<ArtifactVersion> artifactVersions = versionsType.getVersion().stream()
											.map(it -> new DefaultArtifactVersion(it)).collect(Collectors.toList());
									matchVersion = range.matchVersion(artifactVersions);
									if (matchVersion != null) {
										dependencyResolution = "^" + matchVersion.toString();
									}
								}

								if (matchVersion == null) {
									Set<String> localVersions = localArtifactVersions
											.get(dependencyGroupId + ":" + resolveOSGiPlatform(dependencyArtifactId));
									if (localVersions != null) {
										List<ArtifactVersion> versions = localVersions.stream().map(
												it -> new DefaultArtifactVersion(VersionUtil.versionNotAsSnapshot(it)))
												.collect(Collectors.toList());
										matchVersion = range.matchVersion(versions);
										if (matchVersion != null) {
											dependencyResolution = "~" + matchVersion.toString();
										}
									}
								}

								if (dependencyMetaData == null && !dependencyResolution.startsWith("~")) {
									dependencyResolution += " **" + dependencyMetadataURL;
								}

								dependencyResolutions.add(dependencyResolution);
							}

							properties.put("maven-dependency-requirements", String.join(" ", dependencyMapping));
							properties.put("maven-dependency-resolutions", String.join(" ", dependencyResolutions));
						});
					}
				}
			} catch (RuntimeException ex) {
				AggregationAnalyzerEditorPlugin.INSTANCE.log(ex);
			}

			List<InstallableUnitMapping> siblings = iuMapping.getSiblings();
			for (InstallableUnitMapping sibling : siblings) {
				gatherPOMs(monitor, modeledIUs, sibling, exclusion, mavenMappings, localArtifactVersions, metadata,
						versionRangeAnalyzers);
			}

			List<InstallableUnitMapping> children = iuMapping.getChildren();
			for (InstallableUnitMapping childMapping : children) {
				gatherPOMs(monitor, modeledIUs, childMapping, exclusion, mavenMappings, localArtifactVersions, metadata,
						versionRangeAnalyzers);
			}
		}

		private Resource getMetaDataResource(ResourceSet resourceSet, URI mavenMetadataURI) {
			Resource resource = null;
			synchronized (resourceSet) {
				resource = resourceSet.getResource(mavenMetadataURI, false);
				if (resource == null) {
					resource = resourceSet.createResource(mavenMetadataURI, MetadataPackage.eCONTENT_TYPE);
					String artifactMetadata = getContentOrEmpty(mavenMetadataURI);
					try {
						resource.load(new URIConverter.ReadableInputStream(artifactMetadata), null);
					} catch (IOException e) {
					}
				}
			}
			return resource;
		}

		private MetaData getMetaData(ResourceSet resourceSet, URI mavenMetadataURI) {
			Resource resource = getMetaDataResource(resourceSet, mavenMetadataURI);
			EList<EObject> contents = resource.getContents();
			if (!contents.isEmpty()) {
				EObject eObject = contents.get(0);
				EList<EObject> eContents = eObject.eContents();
				if (!eContents.isEmpty()) {
					EObject root = eContents.get(0);
					if (root instanceof MetaData) {
						return (MetaData) root;
					}
				}
			}
			return null;
		}

		private List<InstallableUnitAnalysis> analyzeDuplicates(Analysis analysis) {
			Map<String, List<InstallableUnitAnalysis>> ius = new TreeMap<>();
			analysis.getContributions().stream().flatMap(c -> c.getInstallableUnits().stream()).forEach(iu -> {
				IInstallableUnit installableUnit = iu.getInstallableUnit();
				if (!installableUnit.getArtifacts().isEmpty()) {
					String id = installableUnit.getId();
					installableUnit.getVersion();
					ius.computeIfAbsent(id, i -> new ArrayList<>()).add(iu);
				}
			});

			var ignoreMajorVersionDuplicates = analysis.isIgnoreMajorVersionDuplicates();
			ius.entrySet().removeIf(e -> {
				var iuVersions = e.getValue();
				if (iuVersions.size() == 1) {
					return true;
				}

				if (!ignoreMajorVersionDuplicates) {
					return false;
				}

				var versions = new TreeMap<Comparable<?>, List<InstallableUnitAnalysis>>();
				for (var iuVersion : iuVersions) {
					Version version = iuVersion.getInstallableUnit().getVersion();
					if (version.getSegmentCount() < 1) {
						return false;
					}
					versions.computeIfAbsent(version.getSegment(0), it -> new ArrayList<>()).add(iuVersion);
				}

				return !versions.values().stream().anyMatch(it -> it.size() > 1);
			});

			Comparator<InstallableUnitAnalysis> compareVersion = new Comparator<InstallableUnitAnalysis>() {
				@Override
				public int compare(InstallableUnitAnalysis o1, InstallableUnitAnalysis o2) {
					return o1.getInstallableUnit().getVersion().compareTo(o2.getInstallableUnit().getVersion());
				}
			};
			ius.values().forEach(l -> l.sort(compareVersion));

			return ius.values().stream().flatMap(list -> list.stream()).map(i -> computeStrictlyRequiredBy(i))
					.collect(Collectors.toList());
		}

		private List<InstallableUnitAnalysis> analyzeStrictRequirements(Analysis analysis) {
			List<InstallableUnitAnalysis> result = analysis.getContributions().stream()
					.flatMap(c -> c.getInstallableUnits().stream()).map(i -> computeStrictlyRequiredBy(i))
					.collect(Collectors.toList());
			result.sort(INSTALLABLE_UNIT_ANALYIS_COMPARATOR);
			return result;
		}

		public static InstallableUnitAnalysis computeStrictlyRequiredBy(InstallableUnitAnalysis element) {
			InstallableUnitAnalysis copy = EcoreUtil.copy(element);
			copy.getRequirements().clear();
			for (CapabilityAnalysis capabilityAnalysis : copy.getCapabilities()) {
				EList<CapabilityResolution> resolutions = capabilityAnalysis.getResolutions();
				List<CapabilityResolution> toRemove = new ArrayList<>();
				for (CapabilityResolution capabilityResolution : resolutions) {
					RequirementAnalysis requirement = capabilityResolution.getRequirement();
					EList<RequirementResolution> resolutions2 = requirement.getResolutions();
					boolean hasNonOptionalResolution = false;
					for (RequirementResolution requirementResolution : resolutions2) {
						if (requirementResolution.getInstallableUnit() != element) {
							toRemove.add(capabilityResolution);
							break;
						} else if (requirementResolution.getRequirement().getRequirement().getMin() != 0) {
							hasNonOptionalResolution = true;
						}
					}

					if (!hasNonOptionalResolution) {
						toRemove.add(capabilityResolution);
					}
				}

				resolutions.removeAll(toRemove);
			}

			copy.getCapabilities().removeIf(c -> c.getResolutions().isEmpty());
			return copy;
		}

		private List<InstallableUnitAnalysis> analyzeExactRequirements(Analysis analysis) {
			List<InstallableUnitAnalysis> result = analysis.getContributions().stream()
					.flatMap(c -> c.getInstallableUnits().stream()).map(i -> computeExactlyRequiredBy(i))
					.collect(Collectors.toList());
			result.sort(INSTALLABLE_UNIT_ANALYIS_COMPARATOR);
			return result;
		}

		public static InstallableUnitAnalysis computeExactlyRequiredBy(InstallableUnitAnalysis element) {
			InstallableUnitAnalysis copy = EcoreUtil.copy(element);
			copy.getRequirements().clear();
			for (CapabilityAnalysis capabilityAnalysis : copy.getCapabilities()) {
				EList<CapabilityResolution> resolutions = capabilityAnalysis.getResolutions();
				List<CapabilityResolution> toRemove = new ArrayList<>();
				for (CapabilityResolution capabilityResolution : resolutions) {
					RequirementAnalysis requirement = capabilityResolution.getRequirement();
					if (requirement.getRequirement() instanceof IRequiredCapability requiredCapability) {
						VersionRange range = requiredCapability.getRange();
						if (!range.getMinimum().equals(range.getMaximum())) {
							toRemove.add(capabilityResolution);
						}
					} else {
						toRemove.add(capabilityResolution);
					}
				}
				resolutions.removeAll(toRemove);
			}

			copy.getCapabilities().removeIf(c -> c.getResolutions().isEmpty());
			return copy;
		}

		public Analysis analyze(IProgressMonitor monitor) throws Exception {
			SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
			Builder builder = getBuilder(subMonitor.newChild(90));
			try {
				if (builder != null) {
					subMonitor.setTaskName("Analyzing Aggregation");
					Aggregation builderAggregation = builder.getAggregation();

					subMonitor.subTask("Collecting contributions");
					subMonitor.worked(1);

					Set<IInstallableUnit> allUnitsToAggregate = new TreeSet<>();
					for (ValidationSet validationSet : builderAggregation.getValidationSets(true)) {
						allUnitsToAggregate.addAll(builder.getUnitsToAggregate(validationSet).stream()
								.map(iu -> iu instanceof ResolvedInstallableUnit
										? ((ResolvedInstallableUnit) iu).getOriginal()
										: iu)
								.collect(Collectors.toSet()));
					}

					aggregateMetadataRepository = P2Factory.eINSTANCE.createMetadataRepository();
					((MetadataRepositoryImpl) aggregateMetadataRepository)
							.setLocation(new java.net.URI(AGGREGATE_REPOSITORY_LOCATION));

					Map<IInstallableUnit, InstallableUnit> modeledIUs = allUnitsToAggregate.stream()
							.collect(Collectors.toMap(Function.identity(), iu -> P2Bridge.importToModel(iu)));
					aggregateMetadataRepository.getInstallableUnits().addAll(new TreeSet<>(modeledIUs.values()));

					IQueryable<IInstallableUnit> queriable = InstallableUnitUtils.getIndex(modeledIUs.values());

					Set<URI> contributionsWithDuplicateRepositories = new HashSet<>();
					Map<java.net.URI, URI> owningContributions = new HashMap<>();
					EList<Contribution> allContributions = builderAggregation.getAllContributions(true);

					Map<URI, Set<IInstallableUnit>> allContributionIUs = new HashMap<>();
					Map<URI, Set<IInstallableUnit>> allIncludedIUs = new HashMap<>();

					ExecutorService executor = getExecutor();

					subMonitor.worked(1);
					subMonitor.subTask("Analyzing contributions");

					for (Contribution contribution : allContributions) {
						Set<IInstallableUnit> contributionIUs = new TreeSet<>();
						Set<IInstallableUnit> includedIUs = new TreeSet<>();
						URI contributionURI = EcoreUtil.getURI((EObject) contribution);
						allContributionIUs.put(contributionURI, contributionIUs);
						allIncludedIUs.put(contributionURI, includedIUs);

						EList<MappedRepository> repositories = contribution.getRepositories(true);
						for (MappedRepository mappedRepository : repositories) {
							MetadataRepository metadataRepository = mappedRepository.getMetadataRepository();
							EList<IInstallableUnit> installableUnits = metadataRepository.getInstallableUnits();
							contributionIUs.addAll(installableUnits.stream().map(iu -> modeledIUs.get(iu))
									.filter(Objects::nonNull).collect(Collectors.toSet()));

							java.net.URI location = metadataRepository.getLocation();
							URI otherContribution = owningContributions.put(location, contributionURI);
							if (otherContribution != null) {
								contributionsWithDuplicateRepositories.add(contributionURI);
								contributionsWithDuplicateRepositories.add(otherContribution);
							}

							EList<MappedUnit> units = mappedRepository.getUnits(true);
							for (MappedUnit mappedUnit : units) {
								Set<IInstallableUnit> ius = query(queriable,
										QueryUtil.createIUQuery(mappedUnit.getName(), mappedUnit.getVersionRange()));
								if (!ius.isEmpty()) {
									for (IInstallableUnit iu : ius) {
										collectChildren(queriable, includedIUs, iu);
									}
								}
							}
						}
					}

					subMonitor.worked(1);

					Analysis analysis = EcoreUtil.copy(originalAnalysis);
					Pattern exclusion = analysis.getExclusion();

					InstallableUnitMapping iuMapping = builder.getInstallableUnitMapping();
					if (iuMapping != null) {
						subMonitor.subTask("Analyzing Maven mappings");

						Set<MavenMapping> usedMavenMappings = new LinkedHashSet<MavenMapping>();
						List<Runnable> versionRangeAnalyzers = new ArrayList<Runnable>();
						Map<String, Set<String>> localArtifactVersions = new TreeMap<String, Set<String>>();
						List<Future<?>> metadata = new ArrayList<Future<?>>();
						SubMonitor pomAnalyzer = subMonitor.newChild(1);
						gatherPOMs(pomAnalyzer, modeledIUs, iuMapping, exclusion, usedMavenMappings,
								localArtifactVersions, metadata, versionRangeAnalyzers);

						pomAnalyzer.setWorkRemaining(metadata.size());
						for (Future<?> future : metadata) {
							future.get();
							pomAnalyzer.worked(1);
						}

						MonitorUtils.testCancelStatus(monitor);
						versionRangeAnalyzers.forEach(Runnable::run);

						Set<MavenMapping> mavenMappings = new LinkedHashSet<MavenMapping>();
						for (TreeIterator<EObject> eAllContents = ((EObject) builder.getAggregation())
								.eAllContents(); eAllContents.hasNext();) {
							EObject eObject = eAllContents.next();
							if (eObject instanceof MavenMapping) {
								mavenMappings.add((MavenMapping) eObject);
							}
						}

						mavenMappings.removeAll(usedMavenMappings);

						if (!mavenMappings.isEmpty()) {
							MavenMappingItemProvider mavenMappingItemProvider = new MavenMappingItemProvider(null);
							for (MavenMapping unusedMavenMapping : mavenMappings) {
								AggregationAnalyzerEditorPlugin.INSTANCE.log(new Status(IStatus.WARNING,
										AggregationAnalyzerEditorPlugin.INSTANCE.getSymbolicName(),
										"Unused Mapping " + mavenMappingItemProvider.getText(unusedMavenMapping)));
							}
						}
					}

					Set<IInstallableUnit> remainingIUs = modeledIUs.keySet();

					Map<IInstallableUnit, InstallableUnitAnalysis> usedIUs = new LinkedHashMap<>();

					subMonitor.worked(1);
					subMonitor.subTask("Partitioning contributions");

					MonitorUtils.testCancelStatus(monitor);
					Map<RequirementAnalysis, Future<Set<IInstallableUnit>>> dependencies = new LinkedHashMap<>();
					EList<ContributionAnalysis> contributions = analysis.getContributions();
					for (boolean all : new boolean[] { false, true }) {
						for (ContributionAnalysis contributionAnalysis : contributions) {
							MonitorUtils.testCancelStatus(monitor);
							Contribution contribution = contributionAnalysis.getContribution();
							URI contributionURI = contribution == null ? null
									: CommonPlugin.asLocalURI(EcoreUtil.getURI((EObject) contribution));
							boolean duplicated = contributionsWithDuplicateRepositories.contains(contributionURI);
							Pattern match = contributionAnalysis.getMatch();
							Set<IInstallableUnit> ius = (duplicated || match != null
									? all ? allContributionIUs : allIncludedIUs
									: allContributionIUs).get(contributionURI);
							if (ius == null && match != null) {
								ius = new LinkedHashSet<>(modeledIUs.values());
							}
							if (ius != null) {
								EList<InstallableUnitAnalysis> installableUnits = contributionAnalysis
										.getInstallableUnits();
								for (IInstallableUnit iu : ius) {
									String id = iu.getId();
									if ((match == null || match.matcher(id).matches()) && remainingIUs.remove(iu)
											&& (exclusion == null || !exclusion.matcher(id).matches())) {
										InstallableUnitAnalysis installableUnitAnalysis = AnalyzerFactory.eINSTANCE
												.createInstallableUnitAnalysis();
										installableUnitAnalysis.setInstallableUnit(iu);
										usedIUs.put(iu, installableUnitAnalysis);
										installableUnits.add(installableUnitAnalysis);

										EList<RequirementAnalysis> requirements = installableUnitAnalysis
												.getRequirements();

										for (IRequirement requirement : iu.getRequirements()) {
											RequirementAnalysis requirementAnalysis = AnalyzerFactory.eINSTANCE
													.createRequirementAnalysis();
											requirementAnalysis.setRequirement(requirement);
											requirements.add(requirementAnalysis);

											IMatchExpression<IInstallableUnit> matches = requirement.getMatches();
											dependencies.put(requirementAnalysis, executor.submit(
													() -> query(queriable, QueryUtil.createMatchQuery(matches))));
										}

										ECollections.sort(requirements,
												RequirementAnalysis.REQUIREMENT_ANALYSIS_COMPARATOR);
									}
								}

								ECollections.sort(installableUnits,

										InstallableUnitAnalysis.INSTALLABLE_UNIT_ANALYIS_COMPARATOR);
							}
						}
					}

					subMonitor.worked(1);
					subMonitor.subTask("Analyzing dependencies");

					Map<InstallableUnitAnalysis, Set<RequirementResolution>> dependants = new LinkedHashMap<>();
					for (Map.Entry<RequirementAnalysis, Future<Set<IInstallableUnit>>> entry : dependencies
							.entrySet()) {
						MonitorUtils.testCancelStatus(monitor);
						RequirementAnalysis requirementAnalysis = entry.getKey();
						Set<IInstallableUnit> ius = entry.getValue().get();
						Set<RequirementResolution> resolutions = ius.stream() //
								.map(iu -> usedIUs.get(iu)).filter(Objects::nonNull) //
								.map(iu -> {
									RequirementResolution requirementResolution = AnalyzerFactory.eINSTANCE
											.createRequirementResolution();
									requirementResolution.setInstallableUnit(iu);
									dependants.computeIfAbsent(iu, i -> new LinkedHashSet<>())
											.add(requirementResolution);

									return requirementResolution;
								}).collect(Collectors.toSet());
						requirementAnalysis.getResolutions().addAll(resolutions);
					}

					subMonitor.worked(1);
					subMonitor.subTask("Building dependency results");

					for (Map.Entry<InstallableUnitAnalysis, Set<RequirementResolution>> entry : dependants.entrySet()) {
						MonitorUtils.testCancelStatus(monitor);
						InstallableUnitAnalysis installableUnitAnalysis = entry.getKey();
						EList<CapabilityAnalysis> capabilities = installableUnitAnalysis.getCapabilities();
						Set<RequirementResolution> requirementResolutions = entry.getValue();

						IInstallableUnit iu = installableUnitAnalysis.getInstallableUnit();
						for (IProvidedCapability capability : iu.getProvidedCapabilities()) {
							CapabilityAnalysis capabilityAnalysis = AnalyzerFactory.eINSTANCE
									.createCapabilityAnalysis();
							capabilityAnalysis.setCapability(capability);
							capabilities.add(capabilityAnalysis);
							EList<CapabilityResolution> resolutions = capabilityAnalysis.getResolutions();

							String namespace = capability.getNamespace();
							String name = capability.getName();
							for (Iterator<RequirementResolution> it = requirementResolutions.iterator(); it
									.hasNext();) {
								RequirementResolution requirementResolution = it.next();
								RequirementAnalysis requirementAnalysis = requirementResolution.getRequirement();
								IRequirement requirement = requirementAnalysis.getRequirement();
								if (requirement instanceof IRequiredCapability) {
									IRequiredCapability requiredCapability = (IRequiredCapability) requirement;
									String requiredNamespace = requiredCapability.getNamespace();
									String requiredName = requiredCapability.getName();
									if (requiredNamespace.equals(namespace) && requiredName.equals(name)) {
										CapabilityResolution capabilityResolution = AnalyzerFactory.eINSTANCE
												.createCapabilityResolution();
										capabilityResolution.setRequirement(requirementAnalysis);
										resolutions.add(capabilityResolution);
										requirementResolution.setCapability(capabilityAnalysis);
										it.remove();
									}
								} else {
									IMatchExpression<IInstallableUnit> match = requirement.getMatches();
									if (RequiredPropertiesMatch.isPropertiesMatchRequirement(match)) {
										String requiredNamespace = RequiredPropertiesMatch.extractNamespace(match);
										if (namespace.equals(requiredNamespace)) {
											IFilterExpression propertiesMatch = RequiredPropertiesMatch
													.extractPropertiesMatch(match);
											if (propertiesMatch.match(capability.getProperties())) {
												CapabilityResolution capabilityResolution = AnalyzerFactory.eINSTANCE
														.createCapabilityResolution();
												capabilityResolution.setRequirement(requirementAnalysis);
												resolutions.add(capabilityResolution);
												requirementResolution.setCapability(capabilityAnalysis);
												it.remove();
											}
										}
									}
								}
							}

							ECollections.sort(resolutions, CapabilityResolution.CAPABILITY_RESOLUTION_COMPARATOR);
						}

						if (!requirementResolutions.isEmpty()) {
							System.err.println("###" + requirementResolutions);
						}
					}

					Map<URI, ContributionAnalysis> contributionsMap = analysis.getContributions().stream()
							.filter(it -> it.getContribution() != null)
							.collect(Collectors.toMap(
									it -> CommonPlugin.resolve(EcoreUtil.getURI((EObject) it.getContribution())),
									Function.identity()));
					Map<IInstallableUnit, InstallableUnit> usedIUsMap = usedIUs.keySet().stream()
							.collect(Collectors.toMap(Function.identity(), InstallableUnit.class::cast));
					for (MetadataRepositoryReference repository : builderAggregation
							.getAllMetadataRepositoryReferences(true)) {
						MetadataRepository metadataRepository = repository.getMetadataRepository();
						EObject eContainer = ((EObject) repository).eContainer();
						if (eContainer instanceof Contribution) {
							Contribution contribution = (Contribution) eContainer;
							ContributionAnalysis contributionAnalysis = contributionsMap
									.get(EcoreUtil.getURI((EObject) contribution));

							int index = contribution.getRepositories().indexOf(repository);
							String location = metadataRepository.getLocation().toString();

							for (IInstallableUnit iu : metadataRepository.getInstallableUnits()) {
								InstallableUnit usedIU = usedIUsMap.get(iu);
								if (usedIU != null) {
									usedIU.getPropertyMap().put(contributionAnalysis.getLabel() + '[' + index + ']',
											location);
								}
							}
						}
					}

					aggregateMetadataRepository.getInstallableUnits().retainAll(usedIUs.keySet());

					structuredAggregateMetadataRepositoryResource = new MetadataRepositoryResourceFactoryImpl()
							.createResource(STRUCTURED_AGGREGATE_REPOSITORY);
					structuredAggregateMetadataRepositoryResource.getContents()
							.add((EObject) MetadataRepositoryStructuredViewBuilder.create(aggregateMetadataRepository));

					ECollections.sort(analysis.getContributions(), AnalyzerUtil.CONTRIBUTION_ANALYSIS_COMPARATOR);

					subMonitor.worked(1);
					subMonitor.subTask("Background tasks completed");

					return analysis;
				}

				return null;
			} finally {
				shutDownExecutor();
			}
		}

		private ExecutorService getExecutor() {
			if (executor == null) {
				executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			}

			return executor;
		}

		private void shutDownExecutor() throws InterruptedException {
			if (executor != null) {
				executor.shutdown();
				executor.awaitTermination(10, TimeUnit.MINUTES);
				executor = null;
			}
		}

		private Set<IInstallableUnit> query(IQueryable<IInstallableUnit> queriable, IQuery<IInstallableUnit> query) {
			IQueryResult<IInstallableUnit> queryResult = queriable.query(query, new NullProgressMonitor());
			Set<IInstallableUnit> result = new TreeSet<IInstallableUnit>();
			for (IInstallableUnit iu : queryResult) {
				result.add(iu);
			}

			return result;
		}

		private void collectChildren(IQueryable<IInstallableUnit> queriable, Set<IInstallableUnit> allIUs,
				IInstallableUnit iu) {
			if (allIUs.add(iu)) {
				for (IRequirement requirement : iu.getRequirements()) {
					if (requirement instanceof IRequiredCapability) {
						IRequiredCapability requiredCapability = (IRequiredCapability) requirement;
						if (IInstallableUnit.NAMESPACE_IU_ID.equals(requiredCapability.getNamespace())) {
							VersionRange range = requiredCapability.getRange();
							Version minimum = range.getMinimum();
							if (minimum.equals(range.getMaximum())) {
								Set<IInstallableUnit> result = query(queriable,
										QueryUtil.createIUQuery(requiredCapability.getName(), minimum));
								if (result.isEmpty()) {
									if (requirement.getMin() > 0) {
										System.err.println("### not found " + requirement + " in " + queriable);
									}
								} else {
									IInstallableUnit requiredIU = result.iterator().next();
									collectChildren(queriable, allIUs, requiredIU);
								}
							}
						}
					}
				}
			}
		}
	}

	public static final String OSGI_PLATFORM = Platform.getOS() + '.' + Platform.getWS() + '.' + Platform.getOSArch();

	public static String getURL(String groupId) {
		return "https://repo1.maven.org/maven2/" + groupId.replace('.', '/') + "/";
	}

	public static String getURL(String groupId, String artifactId) {
		return getURL(groupId) + resolveOSGiPlatform(artifactId) + "/";
	}

	public static String resolveOSGiPlatform(String artifactId) {
		return artifactId.replace("${osgi.platform}", OSGI_PLATFORM);
	}
}
