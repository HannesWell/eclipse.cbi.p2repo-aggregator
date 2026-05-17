/**
 * Copyright (c) 2022 Eclipse contributors and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.cbi.p2repo.aggregator.p2view.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cbi.p2repo.aggregator.Aggregation;
import org.eclipse.cbi.p2repo.aggregator.AggregatorFactory;
import org.eclipse.cbi.p2repo.aggregator.InstallableUnitType;
import org.eclipse.cbi.p2repo.aggregator.MetadataRepositoryReference;
import org.eclipse.cbi.p2repo.aggregator.Property;
import org.eclipse.cbi.p2repo.aggregator.p2view.Bundle;
import org.eclipse.cbi.p2repo.aggregator.p2view.Categories;
import org.eclipse.cbi.p2repo.aggregator.p2view.Category;
import org.eclipse.cbi.p2repo.aggregator.p2view.Feature;
import org.eclipse.cbi.p2repo.aggregator.p2view.Fragment;
import org.eclipse.cbi.p2repo.aggregator.p2view.IUPresentation;
import org.eclipse.cbi.p2repo.aggregator.p2view.MetadataRepositoryStructuredView;
import org.eclipse.cbi.p2repo.aggregator.p2view.OtherIU;
import org.eclipse.cbi.p2repo.aggregator.p2view.P2viewFactory;
import org.eclipse.cbi.p2repo.aggregator.p2view.Product;
import org.eclipse.cbi.p2repo.aggregator.p2view.RepositoryReferences;
import org.eclipse.cbi.p2repo.aggregator.util.InstallableUnitUtils;
import org.eclipse.cbi.p2repo.aggregator.util.ResourceUtils;
import org.eclipse.cbi.p2repo.aggregator.util.TwoColumnMatrix;
import org.eclipse.cbi.p2repo.p2.MetadataRepository;
import org.eclipse.cbi.p2repo.p2.impl.InstallableUnitImpl;
import org.eclipse.cbi.p2repo.p2.util.IUUtils;
import org.eclipse.cbi.p2repo.p2.util.P2Utils;
import org.eclipse.cbi.p2repo.p2.util.RepositoryTranslationSupport;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.equinox.internal.p2.metadata.IRequiredCapability;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.IRepositoryReference;

public class MetadataRepositoryStructuredViewBuilder {
	private final Resource resource;
	private final MetadataRepositoryStructuredView repoView;
	private final MetadataRepository repository;
	private final TwoColumnMatrix<IUPresentation, Object[]> allIUMatrix;

	public static MetadataRepositoryStructuredView create(MetadataRepository repository) {
		MetadataRepositoryStructuredView repoView = P2viewFactory.eINSTANCE
				.createMetadataRepositoryStructuredView(repository);
		new MetadataRepositoryStructuredViewBuilder(null, repoView, repository, new TwoColumnMatrix<>())
				.buildStructuredView();
		return repoView;
	}

	public static void build(Resource resource, MetadataRepositoryStructuredView repoView,
			MetadataRepository repository, TwoColumnMatrix<IUPresentation, Object[]> allIUMatrix) {
		new MetadataRepositoryStructuredViewBuilder(resource, repoView, repository, allIUMatrix).buildStructuredView();
	}

	private MetadataRepositoryStructuredViewBuilder(Resource resource, MetadataRepositoryStructuredView repoView,
			MetadataRepository repository, TwoColumnMatrix<IUPresentation, Object[]> allIUMatrix) {
		this.resource = resource;
		this.repoView = repoView;
		this.repository = repository;
		this.allIUMatrix = allIUMatrix;
	}

	private void buildStructuredView() {
		repoView.setName(repository.getName());
		repoView.setInstallableUnitList(P2viewFactory.eINSTANCE.createInstallableUnits());

		Map<String, Map<Version, IUPresentation>> iuMap = new HashMap<String, Map<Version, IUPresentation>>();
		List<Category> categories = new ArrayList<Category>();
		List<Feature> features = new ArrayList<Feature>();
		List<Product> products = new ArrayList<Product>();
		List<Bundle> bundles = new ArrayList<Bundle>();
		List<Fragment> fragments = new ArrayList<Fragment>();
		List<OtherIU> miscellaneous = new ArrayList<OtherIU>();
		List<IUPresentation> allIUs = repoView.getInstallableUnitList().getAllIUs();

		for (IInstallableUnit iu : repository.getInstallableUnits()) {
			IUPresentation iuPresentation;

			switch (InstallableUnitUtils.getType(iu)) {
				case CATEGORY:
					iuPresentation = P2viewFactory.eINSTANCE.createCategory(iu);
					if (!((Category) iuPresentation).isNested())
						categories.add((Category) iuPresentation);
					break;
				case FEATURE:
					iuPresentation = P2viewFactory.eINSTANCE.createFeature(iu);
					features.add((Feature) iuPresentation);
					break;
				case PRODUCT:
					iuPresentation = P2viewFactory.eINSTANCE.createProduct(iu);
					products.add((Product) iuPresentation);
					break;
				case BUNDLE:
					iuPresentation = P2viewFactory.eINSTANCE.createBundle(iu);
					bundles.add((Bundle) iuPresentation);
					break;
				case FRAGMENT:
					iuPresentation = P2viewFactory.eINSTANCE.createFragment(iu);
					fragments.add((Fragment) iuPresentation);
					break;
				default:
					iuPresentation = P2viewFactory.eINSTANCE.createOtherIU(iu);
					miscellaneous.add((OtherIU) iuPresentation);
			}

			iuPresentation.setId(iu.getId());
			iuPresentation.setVersion(iu.getVersion());
			iuPresentation.setFilter(P2Utils.filterToString(iu.getFilter()));

			String name = RepositoryTranslationSupport
					.getInstance((MetadataRepository) ((InstallableUnitImpl) iu).eContainer())
					.getIUProperty(iu, IInstallableUnit.PROP_NAME);
			if (name == null || name.length() == 0)
				iuPresentation.setName(iu.getId());
			else
				iuPresentation.setName(name);

			if (name != null && name.startsWith("%"))
				name = null;

			if (InstallableUnitUtils.getType(iu) == InstallableUnitType.CATEGORY || iu.getVersion() == null)
				iuPresentation.setLabel(name != null && name.length() > 0 ? name : iu.getId());
			else
				iuPresentation.setLabel(iu.getId() + " / " + IUUtils.stringifyVersion(iu.getVersion())
						+ (name != null && name.length() > 0 ? " (" + name + ")" : ""));
			iuPresentation.setDescription(RepositoryTranslationSupport
					.getInstance((MetadataRepository) ((InstallableUnitImpl) iu).eContainer())
					.getIUProperty(iu, IInstallableUnit.PROP_DESCRIPTION));

			Map<Version, IUPresentation> versionMap = iuMap.get(iu.getId());
			if (versionMap == null)
				iuMap.put(iu.getId(), versionMap = new HashMap<Version, IUPresentation>());
			versionMap.put(iu.getVersion(), iuPresentation);

			allIUs.add(iuPresentation);
		}

		if (categories.size() > 0) {
			Collections.sort(categories, IUPresentation.COMPARATOR);
			repoView.getInstallableUnitList().getNotNullCategoryContainer().getCategories().addAll(categories);

			addIUsToMap(repoView.getInstallableUnitList().getCategoryContainer(), categories);
		}
		if (features.size() > 0) {
			Collections.sort(features, IUPresentation.COMPARATOR);
			repoView.getInstallableUnitList().getNotNullFeatureContainer().getFeatures().addAll(features);

			addIUsToMap(repoView.getInstallableUnitList().getFeatureContainer(), features);
		}
		if (products.size() > 0) {
			Collections.sort(products, IUPresentation.COMPARATOR);
			repoView.getInstallableUnitList().getNotNullProductContainer().getProducts().addAll(products);

			addIUsToMap(repoView.getInstallableUnitList().getProductContainer(), products);
		}
		if (bundles.size() > 0) {
			Collections.sort(bundles, IUPresentation.COMPARATOR);
			repoView.getInstallableUnitList().getNotNullBundleContainer().getBundles().addAll(bundles);

			addIUsToMap(repoView.getInstallableUnitList().getBundleContainer(), bundles);
		}
		if (fragments.size() > 0) {
			Collections.sort(fragments, IUPresentation.COMPARATOR);
			repoView.getInstallableUnitList().getNotNullFragmentContainer().getFragments().addAll(fragments);

			addIUsToMap(repoView.getInstallableUnitList().getFragmentContainer(), fragments);
		}
		if (miscellaneous.size() > 0) {
			Collections.sort(miscellaneous, IUPresentation.COMPARATOR);
			repoView.getInstallableUnitList().getNotNullMiscellaneousContainer().getOthers().addAll(miscellaneous);

			addIUsToMap(repoView.getInstallableUnitList().getMiscellaneousContainer(), miscellaneous);
		}

		Categories categoryContainer = repoView.getInstallableUnitList().getCategoryContainer();
		if (categoryContainer != null)
			for (Category category : categoryContainer.getCategories()) {
				Set<Category> visited = new HashSet<Category>();
				exploreCategory(category, iuMap, visited);
			}

		List<Property> propList = new ArrayList<Property>();
		for (Map.Entry<String, String> property : repository.getPropertyMap())
			propList.add(AggregatorFactory.eINSTANCE.createProperty(property.getKey(), property.getValue()));
		if (propList.size() > 0) {
			repoView.setProperties(P2viewFactory.eINSTANCE.createProperties());
			Collections.sort(propList);
			repoView.getProperties().getPropertyList().addAll(propList);
		}

		List<IRepositoryReference> references = repository.getReferences();
		if (references.size() > 0) {
			RepositoryReferences repoRefs = P2viewFactory.eINSTANCE.createRepositoryReferences();
			repoView.setRepositoryReferences(repoRefs);
			repoRefs.getRepositoryReferences().addAll(references);
		}

		repoView.setLoaded(true);

		Aggregation aggregation = ResourceUtils.getAggregation(getResourceSet());
		if (aggregation != null) {
			for (MetadataRepositoryReference mdrReference : aggregation.getAllMetadataRepositoryReferences(true)) {
				String refLocation = mdrReference.getLocation();
				if (refLocation != null && refLocation.endsWith("/"))
					refLocation = refLocation.substring(0, refLocation.length() - 1);
				if (repository.getLocation().toString().equals(refLocation))
					// force notification by formal setting the value to current value
					// once the adapter (if exists) receives the notification, it will take care of
					// refreshing labels and content of itself and its parents
					mdrReference.setLocation(mdrReference.getLocation());
			}
		}
	}

	protected Resource getResource() {
		return resource;
	}

	protected ResourceSet getResourceSet() {
		Resource resource = getResource();
		return resource == null ? null : resource.getResourceSet();
	}

	protected void addIUsToMap(Object container, List<? extends IUPresentation> iuPresentations) {
		Object[] treePath = new Object[4];
		treePath[0] = getResource();
		treePath[1] = repoView;
		treePath[2] = repoView.getInstallableUnitList();
		treePath[3] = container;

		for (IUPresentation iup : iuPresentations)
			allIUMatrix.add(iup, treePath);
	}

	protected void exploreCategory(Category category, Map<String, Map<Version, IUPresentation>> iuMap,
			Set<Category> visited) {
		visited.add(category);

		List<Category> categories = new ArrayList<Category>();
		List<Feature> features = new ArrayList<Feature>();
		List<Product> products = new ArrayList<Product>();
		List<Bundle> bundles = new ArrayList<Bundle>();
		List<Fragment> fragments = new ArrayList<Fragment>();

		int idx = allIUMatrix.indexOf(category);
		Object[] oldTreePath = allIUMatrix.getValue(idx);
		int len = oldTreePath.length;
		Object[] categoryTreePath = new Object[len + 1];
		System.arraycopy(oldTreePath, 0, categoryTreePath, 0, len);
		categoryTreePath[len] = category;

		for (IRequirement requirement : category.getInstallableUnit().getRequirements()) {
			for (IUPresentation iuPresentation : findMatchingIUs(requirement, iuMap)) {
				if (visited.contains(iuPresentation))
					// there's a recursion
					continue;

				allIUMatrix.add(++idx, iuPresentation, categoryTreePath);
				if (iuPresentation instanceof Category) {
					categories.add((Category) iuPresentation);
					exploreCategory((Category) iuPresentation, iuMap, visited);
				} else if (iuPresentation instanceof Feature)
					features.add((Feature) iuPresentation);
				else if (iuPresentation instanceof Product)
					products.add((Product) iuPresentation);
				else if (iuPresentation instanceof Fragment)
					fragments.add((Fragment) iuPresentation);
				else if (iuPresentation instanceof Bundle)
					bundles.add((Bundle) iuPresentation);
			}
		}

		if (categories.size() > 0) {
			Collections.sort(categories, IUPresentation.COMPARATOR);
			category.getNotNullCategoryContainer().getCategories().addAll(categories);
		}
		if (features.size() > 0) {
			Collections.sort(features, IUPresentation.COMPARATOR);
			category.getNotNullFeatureContainer().getFeatures().addAll(features);
		}
		if (products.size() > 0) {
			Collections.sort(products, IUPresentation.COMPARATOR);
			category.getNotNullProductContainer().getProducts().addAll(products);
		}
		if (bundles.size() > 0) {
			Collections.sort(bundles, IUPresentation.COMPARATOR);
			category.getNotNullBundleContainer().getBundles().addAll(bundles);
		}
		if (fragments.size() > 0) {
			Collections.sort(fragments, IUPresentation.COMPARATOR);
			category.getNotNullFragmentContainer().getFragments().addAll(fragments);
		}
	}

	private List<IUPresentation> findMatchingIUs(IRequirement rq, Map<String, Map<Version, IUPresentation>> iuMap) {
		List<IUPresentation> found = new ArrayList<IUPresentation>();
		if (rq instanceof IRequiredCapability) {
			Map<Version, IUPresentation> vps = iuMap.get(((IRequiredCapability) rq).getName());
			if (vps != null) {
				for (Map.Entry<Version, IUPresentation> vp : vps.entrySet()) {
					if (vp.getValue().getInstallableUnit().satisfies(rq)) {
						found.add(vp.getValue());
					}
				}
				if (!found.isEmpty()) {
					return found;
				}
			}
		}
		for (Map<Version, IUPresentation> vps : iuMap.values())
			for (Map.Entry<Version, IUPresentation> vp : vps.entrySet())
				if (vp.getValue().getInstallableUnit().satisfies(rq))
					found.add(vp.getValue());
		return found;
	}

}
