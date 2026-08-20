/**
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.cbi.p2repo.aggregator.engine.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Scm;
import org.eclipse.cbi.p2repo.aggregator.Aggregation;
import org.eclipse.cbi.p2repo.aggregator.AggregatorFactory;
import org.eclipse.cbi.p2repo.aggregator.Architecture;
import org.eclipse.cbi.p2repo.aggregator.Contribution;
import org.eclipse.cbi.p2repo.aggregator.MavenDependencyItem;
import org.eclipse.cbi.p2repo.aggregator.MavenDependencyMapping;
import org.eclipse.cbi.p2repo.aggregator.MavenItem;
import org.eclipse.cbi.p2repo.aggregator.MavenMapping;
import org.eclipse.cbi.p2repo.aggregator.OperatingSystem;
import org.eclipse.cbi.p2repo.aggregator.StatusCode;
import org.eclipse.cbi.p2repo.aggregator.VersionFormat;
import org.eclipse.cbi.p2repo.aggregator.WindowSystem;
import org.eclipse.cbi.p2repo.aggregator.util.GeneralUtils;
import org.eclipse.cbi.p2repo.aggregator.util.InstallableUnitUtils;
import org.eclipse.cbi.p2repo.p2.maven.POM;
import org.eclipse.cbi.p2repo.p2.maven.util.VersionUtil;
import org.eclipse.cbi.p2repo.util.LogUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.equinox.internal.p2.metadata.IRequiredCapability;
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.internal.p2.metadata.RequiredCapability;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.ICopyright;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IInstallableUnitFragment;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.ITouchpointData;
import org.eclipse.equinox.p2.metadata.ITouchpointType;
import org.eclipse.equinox.p2.metadata.IUpdateDescriptor;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.metadata.expression.IFilterExpression;
import org.eclipse.equinox.p2.metadata.expression.IMatchExpression;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 * @author Filip Hrbek (filip.hrbek@cloudsmith.com)
 *
 */
// Implement parent subtraction on all relevant getters (as an example, dependencies are implemented that way)
// However, repo optimization is not implemented yet, so it does not make much sense to do it now

public class InstallableUnitMapping implements IInstallableUnit {
	public enum Type {
		TOP, GROUP, IU, PROXY;
	}

	private static final Pattern IU_PROPERTY_SUBSTITUTION_PATTERN = Pattern.compile("\\$(.*)\\$");

	private static final Set<String> KNOWN_OSGI_WS, KNOWN_OSGI_OS, KNOWN_OSGI_ARCH;
	static {
		Set<String> values = new HashSet<>();
		for (WindowSystem ws : WindowSystem.values())
			values.add(ws.getLiteral());
		KNOWN_OSGI_WS = Collections.unmodifiableSet(values);
		values = new HashSet<>();
		for (OperatingSystem os : OperatingSystem.values())
			values.add(os.getLiteral());
		KNOWN_OSGI_OS = Collections.unmodifiableSet(values);
		values = new HashSet<>();
		for (Architecture arch : Architecture.values())
			values.add(arch.getLiteral());
		KNOWN_OSGI_ARCH = Collections.unmodifiableSet(values);
	}

	private static final Map<String, String> OSGI_OS_TO_MAVEN_FAMILY_MAP = Map.of(//
			Platform.OS_LINUX, "linux", //
			Platform.OS_MACOSX, "mac", //
			Platform.OS_WIN32, "windows" //
	);

	private static final Map<String, Set<String>> OSGI_ARCH_TO_MAVEN_ARCH_MAP = Map.of(//
			Platform.ARCH_AARCH64, Set.of("aarch64"), //
			Platform.ARCH_RISCV64, Set.of("riscv64"), //
			Platform.ARCH_X86_64, new TreeSet<>(Set.of("x86_64", "amd64")), //
			Platform.ARCH_PPC64LE, Set.of("ppc64le") //
	);

	private static final Version DUMMY_VERSION = Version.parseVersion("1");

	private static final Pattern VERSION_EXPRESSION_PATTERN = Pattern
			.compile("(major|minor|micro|[.-]qualifier)(?:(\\+|-)([0-9]+))?");

	private static final Pattern VERSION_PATTERN = Pattern
			.compile("([0-9]+)(?:\\.([0-9]+)(?:\\.([0-9]+)(?:([.-])(.*))?)?)?");

	private Type type;

	private IInstallableUnit installableUnit;

	private List<MavenMapping> mappings;

	private List<MavenDependencyMapping> dependencyMappings;

	private MavenItem mapped;

	private Set<MavenMapping> usedMavenMappings = new LinkedHashSet<MavenMapping>();

	private InstallableUnitMapping parent;

	private List<InstallableUnitMapping> children = new ArrayList<InstallableUnitMapping>();

	private List<InstallableUnitMapping> siblings = new ArrayList<InstallableUnitMapping>();

	private boolean transientFlag;

	private IArtifactKey mainArtifact;

	private Contribution contribution;

	private VersionFormat versionFormat;

	private int mavenBuildNumber;

	private boolean hasSources;

	private IQueryable<IInstallableUnit> index;

	private Set<IInstallableUnit> aggregatedIUs;

	private Map<MavenItem, Version> mavenItemVersions = new HashMap<MavenItem, Version>();

	public InstallableUnitMapping(Contribution contribution, IInstallableUnit iu, List<MavenMapping> mappings,
			List<MavenDependencyMapping> dependencyMappings, IQueryable<IInstallableUnit> index,
			Set<IInstallableUnit> aggregatedIUs) {
		this.contribution = contribution;
		this.index = index;
		this.aggregatedIUs = aggregatedIUs;
		this.mappings = new ArrayList<MavenMapping>(mappings.size() + 1);
		this.mappings.addAll(mappings);
		this.mappings.add(MavenMapping.DEFAULT_MAPPING);

		this.dependencyMappings = new ArrayList<MavenDependencyMapping>(dependencyMappings.size() + 2);
		this.dependencyMappings.addAll(dependencyMappings);

		MavenDependencyMapping defaultDependencyMapping = AggregatorFactory.eINSTANCE.createMavenDependencyMapping();
		defaultDependencyMapping.setNamespacePattern("org\\.eclipse\\.equinox\\.p2\\.iu|osgi\\.bundle");
		defaultDependencyMapping.setNamePattern(".*");
		defaultDependencyMapping.setGroupId("*");
		defaultDependencyMapping.setArtifactId("*");
		defaultDependencyMapping.setVersionRangePattern("(.*)");
		defaultDependencyMapping.setVersionRangeTemplate("$1");
		this.dependencyMappings.add(defaultDependencyMapping);

		MavenDependencyMapping terminalDependencyMapping = AggregatorFactory.eINSTANCE.createMavenDependencyMapping();
		terminalDependencyMapping.setNamespacePattern(".*");
		terminalDependencyMapping.setNamePattern(".*");
		terminalDependencyMapping.setGroupId("!");
		terminalDependencyMapping.setArtifactId("!");
		this.dependencyMappings.add(terminalDependencyMapping);

		for (MavenMapping mapping : mappings)
			if (mapping.getStatus().getCode() != StatusCode.OK)
				throw new RuntimeException("Invalid maven mapping: " + mapping.toString());

		for (MavenDependencyMapping mapping : dependencyMappings)
			if (mapping.getStatus().getCode() != StatusCode.OK)
				throw new RuntimeException("Invalid maven dependency mapping: " + mapping.toString());

		switch (iu.getArtifacts().size()) {
			case 1:
				mainArtifact = iu.getArtifacts().iterator().next();
				// no break here, we really want to continue initialization
			case 0:
				type = Type.IU;
				installableUnit = iu;
				break;
			default:
				// We have more than one artifact - we need to make a proxy depending on the artifacts
				InstallableUnitOverrider proxy = new InstallableUnitOverrider(iu);
				List<IRequirement> dependencies = new ArrayList<IRequirement>(iu.getArtifacts().size());
				int idx = 0;
				for (IArtifactKey artifact : iu.getArtifacts()) {
					String genId = artifact.getId() + ".artifact-" + (idx + 1);
					dependencies.add(new RequiredCapability(NAMESPACE_IU_ID, genId,
							new VersionRange(iu.getVersion(), true, iu.getVersion(), true), null, 1, 1, true, null));
					InstallableUnitOverrider sibling = new InstallableUnitOverrider(iu);
					sibling.overrideId(genId);
					sibling.overrideArtifacts(Collections.singletonList(artifact));

					siblings.add(new InstallableUnitMapping(contribution, sibling, List.of(), List.of(), index,
							aggregatedIUs));
				}

				proxy.overrideRequirements(dependencies);
				proxy.overrideArtifacts(Collections.<IArtifactKey>emptyList());
				installableUnit = proxy;
		}
		Aggregation aggregation = (Aggregation) ((EObject) contribution).eContainer().eContainer();
		versionFormat = aggregation.getVersionFormat();
		mavenBuildNumber = aggregation.getMavenBuildNumber();
	}

	public InstallableUnitMapping(String name) {
		String groupId = name;
		if (groupId == null)
			groupId = "_top";
		String artifactId = name == null ? "_common" : "_group-common";

		type = name == null ? Type.TOP : Type.GROUP;

		InstallableUnit installableUnit = new InstallableUnit();
		installableUnit.setId(artifactId);
		installableUnit.setVersion(DUMMY_VERSION);

		this.installableUnit = installableUnit;
		mapped = AggregatorFactory.eINSTANCE.createMavenItem();
		mapped.setGroupId(groupId);
		mapped.setArtifactId(artifactId);
	}

	public InstallableUnitMapping(VersionFormat versionFormat) {
		this((String) null);
		this.versionFormat = versionFormat;
	}

	static final Set<String> namespaces = new HashSet<String>();

	public Model asPOM() {
		return (asPOM(Map.of()));
	}

	public Model asPOM(Map<String, String> manifest) {
		Model pom = new Model();
		if (parent != null && !parent.isTransient()) {
			Parent newParent = new Parent();
			newParent.setGroupId(parent.map().getGroupId());
			newParent.setArtifactId(parent.map().getArtifactId());
			newParent.setVersion(parent.getVersionString(false, -1));
			pom.setParent(newParent);
		}
		pom.setModelVersion(POM.MODEL_VERSION);
		MavenItem mavenItem = map();
		String thisGroupId = mavenItem.getGroupId();
		pom.setGroupId(thisGroupId);
		String thisArtifactId = mavenItem.getArtifactId();
		pom.setArtifactId(thisArtifactId);
		if (getMainArtifact() == null) {
			pom.setPackaging("pom");
		}

		if (getVersion() != null && !getVersion().equals(Version.emptyVersion)) {
			pom.setVersion(getVersionString(false, -1));
		}

		Set<String> dependencies = new HashSet<String>();
		List<IRequirement> myRequirements = new ArrayList<>(installableUnit.getRequirements());
		Collection<IRequirement> requirements = getRequirements();
		List<Dependency> result = new ArrayList<>();
		for (IRequirement requirement : requirements) {
			if (requirement.getMax() != 0 && requirement instanceof IRequiredCapability) {
				IRequiredCapability req = (IRequiredCapability) requirement;

				MavenDependencyItem dependencyItem = mapRequirement(req);
				String dependecyItemGroupId = dependencyItem.getGroupId();
				if ("!".equals(dependecyItemGroupId)) {
					continue;
				}

				InputLocation inputLocation = new InputLocation(myRequirements.indexOf(req), 0, null);
				String mappedVersionRange = trimOrNull(dependencyItem.getMappedVersionRange());

				Collection<MavenItem> dependencyMappings = map(mavenItem, req, dependecyItemGroupId,
						dependencyItem.getArtifactId());
				for (MavenItem dependencyMapping : dependencyMappings) {
					String groupId = dependencyMapping.getGroupId();
					String artifactId = dependencyMapping.getArtifactId();
					if (!dependencies.add(groupId + ":" + artifactId)) {
						continue;
					}

					Dependency dependency = new Dependency();
					dependency.setLocation("", inputLocation);
					dependency.setGroupId(groupId);
					dependency.setArtifactId(artifactId);
					dependency.setVersion(getVersionRange(dependencyMapping, mappedVersionRange));
					dependency.setOptional(req.getMin() == 0);

					Map<String, String> nativeDetails = getNativeDetails(artifactId, req.getFilter());
					if (nativeDetails == null || nativeDetails.isEmpty()) {
						result.add(dependency);
					} else {
						for (Profile profile : getProfiles(pom, nativeDetails)) {
							profile.addDependency(dependency.clone());
						}
					}
				}
			}
		}

		pom.setDependencies(result);

		Map<String, String> iuProperties = installableUnit.getProperties();
		String name = resolveProperty(iuProperties, IInstallableUnit.PROP_NAME, installableUnit.getId());
		if (name != null && !name.isBlank()) {
			pom.setName(name);
		}

		String description = resolveProperty(iuProperties, IInstallableUnit.PROP_DESCRIPTION, installableUnit.getId());
		if (description != null && !description.isBlank()) {
			pom.setDescription(description);
		} else if (name != null && !name.isBlank()) {
			pom.setDescription(name);
		}

		List<License> mavenLicenses = new ArrayList<>();
		installableUnit.getLicenses().stream() //
				.filter(license -> license.getLocation() != null || license.getBody() != null).map(license -> {
					License mavenLicense = new License();
					if (license.getLocation() != null) {
						mavenLicense.setUrl(license.getLocation().toString());
					}
					mavenLicense.setComments(license.getBody());
					return mavenLicense;
				}).forEach(mavenLicenses::add);

		ICopyright iuCopyright = installableUnit.getCopyright();
		if (iuCopyright != null && (iuCopyright.getLocation() != null || iuCopyright.getBody() != null)) {
			org.apache.maven.model.License copyright = new org.apache.maven.model.License();
			if (iuCopyright.getLocation() != null) {
				copyright.setUrl(iuCopyright.getLocation().toString());
			}
			copyright.setComments(iuCopyright.getBody());
			mavenLicenses.add(copyright);
		}
		pom.setLicenses(mavenLicenses);

		String eclipseSourceReferences = manifest.get("Eclipse-SourceReferences");
		if (eclipseSourceReferences != null) {
			new MavenManager.SourceReferenceAnalyzer(eclipseSourceReferences) {
				private Scm scm;

				public void handleSourceReference(String value, String path, String commitId) {
					scm = new Scm();
					scm.setConnection(value);
					scm.setDeveloperConnection(value);
					if (commitId != null) {
						scm.setTag(commitId);
					}
					pom.setScm(scm);
				}

				public void handleGitHubSourceReference(String org, String repo, String path, String commitId) {
					String baseURL = createGitHubURL(org, repo);
					pom.setUrl(baseURL);

					String url = baseURL + "tree/" + commitId + "/" + path;

					scm.setUrl(url);

					IssueManagement issueManagement = new IssueManagement();
					issueManagement.setSystem("GitHub");
					issueManagement.setUrl(baseURL + "issues");
					pom.setIssueManagement(issueManagement);

					Developer developer = new Developer();
					developer.setUrl(baseURL + "graphs/contributors");
					pom.setDevelopers(List.of(developer));

					Organization organization = new Organization();
					if (org.startsWith("eclipse-") || org.equals("eclipse")) {
						organization.setName("Eclipse Foundation");
						organization.setUrl("https://www.eclipse.org");
					} else {
						organization.setName(org);
						organization.setUrl("https://github.com/" + org);
					}
					pom.setOrganization(organization);
				}
			}.analyze();
		}

		String bundleLicense = manifest.get(Constants.BUNDLE_LICENSE);
		if (bundleLicense != null) {
			try {
				ManifestElement[] bundleLicenseElements;
				bundleLicenseElements = ManifestElement.parseHeader("Bundle-License", bundleLicense);
				List<License> licenses = new ArrayList<>();
				for (var bundleLicenseElement : bundleLicenseElements) {
					String value = bundleLicenseElement.getValue();
					String descriptionAttribute = bundleLicenseElement.getAttribute("description");
					String linkAttribute = bundleLicenseElement.getAttribute("link");
					License license = new License();
					license.setName(value);
					license.setDistribution("repo");
					license.setComments(descriptionAttribute);
					license.setUrl(linkAttribute);
					licenses.add(license);
				}
				if (!licenses.isEmpty()) {
					pom.setLicenses(licenses);
				}
			} catch (BundleException e) {
				throw new RuntimeException(e);
			}
		}

		return pom;
	}

	@Override
	public int compareTo(IInstallableUnit other) {
		return installableUnit.compareTo(other);
	}

	private List<Profile> getProfiles(Model pom, Map<String, String> nativeDependencies) {
		String os = nativeDependencies.get("osgi.os");
		String family = OSGI_OS_TO_MAVEN_FAMILY_MAP.get(os);
		if (family == null) {
			throw new IllegalArgumentException("Unsupported: osgi.os=" + os);
		}
		String arch = nativeDependencies.get("osgi.arch");
		Set<String> mavenArchs = OSGI_ARCH_TO_MAVEN_ARCH_MAP.get(arch);
		if (mavenArchs == null) {
			throw new IllegalArgumentException("Unsupported: osgi.arch=" + arch);
		}

		List<Profile> profiles = new ArrayList<>();
		LOOP: for (String mavenArch : mavenArchs) {
			String id = family + '.' + mavenArch;
			for (Profile profile : pom.getProfiles()) {
				if (profile.getId().equals(mavenArch)) {
					profiles.add(profile);
					continue LOOP;
				}
			}

			Profile profile = new Profile();
			profile.setId(id);
			Activation activation = new Activation();
			profile.setActivation(activation);
			ActivationOS activationOS = new ActivationOS();
			activationOS.setFamily(family);
			activationOS.setArch(mavenArch);
			activation.setOs(activationOS);
			pom.addProfile(profile);
			profiles.add(profile);
			pom.getProfiles().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
		}

		return profiles;
	}

	private String resolveProperty(Map<String, String> iuProperties, String key, String iuId) {
		String value = iuProperties.get(key);
		if (value != null) {
			if (value.startsWith("%")) {
				String localizedKey = "df_LT." + value.substring(1);
				String localizedValue = iuProperties.get(localizedKey);

				if (localizedValue != null) {
					value = localizedValue;
				} else {
					System.out.println("WARNING: Unresolved localized value in unit " + iuId + ": " + value);
				}
			}
		}

		return trimOrNull(value);
	}

	private Map<String, String> getNativeDetails(String artifactId, IMatchExpression<IInstallableUnit> filter) {
		if (artifactId == null || filter == null)
			return null;
		String[] tokens = artifactId.split("\\.");
		if (tokens.length < 4)
			return null;
		Map<String, String> declared = new HashMap<>();
		int platformStart = tokens.length - 3;

		String token = tokens[platformStart];
		if (!KNOWN_OSGI_WS.contains(token))
			return null;
		declared.put("osgi.ws", token);

		token = tokens[platformStart + 1];
		if (!KNOWN_OSGI_OS.contains(token))
			return null;
		declared.put("osgi.os", token);

		token = tokens[platformStart + 2];
		if (!KNOWN_OSGI_ARCH.contains(token))
			return null;
		declared.put("osgi.arch", token);

		if (!matchesFilter(filter, declared))
			return null;

		return declared;
	}

	private String getArtifactFileName() throws CoreException {
		return getFileName(null);
	}

	@Override
	public Collection<IArtifactKey> getArtifacts() {
		return installableUnit.getArtifacts();
	}

	public List<InstallableUnitMapping> getChildren() {
		return children;
	}

	public final Contribution getContribution() {
		return contribution;
	}

	@Override
	public ICopyright getCopyright() {
		return installableUnit.getCopyright();
	}

	@Override
	public ICopyright getCopyright(String locale) {
		return installableUnit.getCopyright(locale);
	}

	private String getFileName(String extension) throws CoreException {
		String fileId = mapped.getArtifactId();
		StringBuilder fileName = new StringBuilder(fileId);
		fileName.append('-');
		fileName.append(getVersionString(true, mavenBuildNumber));
		if (isSourceArtifact())
			fileName.append('-').append(mapped.getClassifier());

		if (extension == null) {
			if (!(getMainArtifact() != null && "binary".equals(getMainArtifact().getClassifier()))) {
				fileName.append(".jar");
			}
		} else {
			fileName.append('.');
			fileName.append(extension);
		}

		return fileName.toString();
	}

	@Override
	public IMatchExpression<IInstallableUnit> getFilter() {
		return installableUnit.getFilter();
	}

	@Override
	public Collection<IInstallableUnitFragment> getFragments() {
		return installableUnit.getFragments();
	}

	@Override
	public String getId() {
		return installableUnit.getId();
	}

	@Override
	public Collection<ILicense> getLicenses() {
		return installableUnit.getLicenses();
	}

	@Override
	public Collection<ILicense> getLicenses(String locale) {
		return installableUnit.getLicenses(locale);
	}

	public IArtifactKey getMainArtifact() {
		return mainArtifact;
	}

	public Version getMappedVersion() {
		if (this.mapped != null) {
			String mappedVersion = this.mapped.getMappedVersion();
			if (mappedVersion != null)
				return VersionUtil.mappedVersion(mappedVersion, this.versionFormat);
		}
		return installableUnit.getVersion();
	}

	@Override
	public Collection<IRequirement> getMetaRequirements() {
		return installableUnit.getMetaRequirements();
	}

	public InstallableUnitMapping getParent() {
		return parent;
	}

	public String getPomName() throws CoreException {
		return getFileName("pom");
	}

	@Override
	public Map<String, String> getProperties() {
		return installableUnit.getProperties();
	}

	@Override
	public String getProperty(String key) {
		return installableUnit.getProperty(key);
	}

	@Override
	public String getProperty(String key, String locale) {
		return installableUnit.getProperty(key, locale);
	}

	@Override
	public Collection<IProvidedCapability> getProvidedCapabilities() {
		return installableUnit.getProvidedCapabilities();
	}

	public String getRelativeFullPath() throws CoreException {
		return getRelativePath() + "/" + getArtifactFileName();
	}

	public String getRelativePath() throws CoreException {
		String artifactId = map().getArtifactId();
		String versionPath = getVersionString(false, -1);
		return map().getGroupId().replace('.', '/') + "/" + artifactId + "/" + versionPath;
	}

	@Override
	public Collection<IRequirement> getRequirements() {
		if (parent != null) {
			Collection<IRequirement> parentRequirements = parent.installableUnit.getRequirements();
			if (!parentRequirements.isEmpty()) {
				Set<IRequirement> requirements = new LinkedHashSet<IRequirement>(parentRequirements);
				requirements.addAll(installableUnit.getRequirements());
				return requirements;
			}
		}

		return installableUnit.getRequirements();
	}

	public List<InstallableUnitMapping> getSiblings() {
		return siblings;
	}

	@Override
	public Collection<ITouchpointData> getTouchpointData() {
		return installableUnit.getTouchpointData();
	}

	@Override
	public ITouchpointType getTouchpointType() {
		return installableUnit.getTouchpointType();
	}

	public Type getType() {
		return type;
	}

	@Override
	public IUpdateDescriptor getUpdateDescriptor() {
		return installableUnit.getUpdateDescriptor();
	}

	@Override
	public Version getVersion() {
		return installableUnit.getVersion();
	}

	/**
	 * @return the version format to be used when generating for Maven.
	 */
	public VersionFormat getVersionFormat() {
		return versionFormat;
	}

	public String getVersionString(boolean fineGrained, int mavenBuildNumber) {
		if (this.mapped != null && this.mapped.getMappedVersion() != null) {
			String mappedVersion = this.mapped.getMappedVersion();
			if (!fineGrained && versionFormat == VersionFormat.MAVEN_SNAPSHOT)
				return VersionUtil.versionAsSnapshot(mappedVersion);
			return mappedVersion;
		}
		return VersionUtil.getVersionString(getVersion(), versionFormat, fineGrained, mavenBuildNumber);
	}

	public String getVersionStringForDependency(MavenItem dependencyMapping, Version dependencyVersion) {
		MavenMapping mavenMapping = dependencyMapping.getMavenMapping();
		if (mavenMapping != null) {
			String mappedVersion = mavenMapping.mapVersion(dependencyVersion);
			if (mappedVersion != null && !"$maven-version$".equals(mappedVersion))
				return mappedVersion;
		}
		return VersionUtil.getVersionString(dependencyVersion, versionFormat, false, -1);
	}

	public String getVersionRange(MavenItem mavenItem, String versionRange) {
		if (versionRange != null) {
			Matcher matcher = VERSION_EXPRESSION_PATTERN.matcher(versionRange);
			if (matcher.find()) {
				StringBuilder result = new StringBuilder();
				String dependencyVersion = mavenItem.getMappedVersion();
				if (dependencyVersion == null) {
					dependencyVersion = mavenItemVersions.get(mavenItem).toString();
				}
				Matcher versionMatcher = VERSION_PATTERN.matcher(dependencyVersion);
				if (!versionMatcher.matches()) {
					throw new IllegalStateException("The version '" + dependencyVersion
							+ "' does not conform to the expected pattern:" + VERSION_PATTERN);
				}

				String major = versionMatcher.group(1);
				String minor = versionMatcher.group(2);
				String micro = versionMatcher.group(3);
				String qualifierType = versionMatcher.group(4);
				String qualifier = versionMatcher.group(5);
				String effectiveQualifier = mavenItem.getMavenMapping().isSnapshot()
						? "-".equals(qualifierType) ? qualifier : "SNAPSHOT"
						: ".".equals(qualifierType) ? qualifier : null;

				do {
					String operator = matcher.group(2);
					String operand = matcher.group(3);
					String versionSegment = null;
					switch (matcher.group(1)) {
						case "major": {
							versionSegment = getVersionSegment(major, operator, operand);
							break;
						}
						case "minor": {
							versionSegment = getVersionSegment(minor, operator, operand);
							break;
						}
						case "micro": {
							versionSegment = getVersionSegment(micro, operator, operand);
							break;
						}
						case "-qualifier": {
							if ("-".equals(qualifierType) || "SNAPSHOT".equals(effectiveQualifier)) {
								versionSegment = getVersionSegment(effectiveQualifier, operator, operand);
								if (versionSegment != null) {
									versionSegment = "-" + versionSegment;
								}
							}
							break;
						}
						case ".qualifier": {
							if (".".equals(qualifierType)) {
								versionSegment = getVersionSegment(effectiveQualifier, operator, operand);
								if (versionSegment != null) {
									versionSegment = "." + versionSegment;
								}
							}
							break;
						}
					}
					matcher.appendReplacement(result,
							versionSegment == null ? "" : Matcher.quoteReplacement(versionSegment));
					if (result.length() > 0) {
						char c = result.charAt(result.length() - 1);
						if (c == '.' || c == '-') {
							result.setLength(result.length() - 1);
						}
					}
				} while (matcher.find());
				matcher.appendTail(result);
				return result.toString();
			}
		}

		VersionRange reqRange = versionRange == null ? null : VersionRange.create(versionRange);
		if (reqRange != null && !reqRange.equals(VersionRange.emptyRange)) {
			StringBuilder versionRangeString = new StringBuilder();
			Version low = reqRange.getMinimum();
			Version high = reqRange.getMaximum();
			String lowDependencyVersion = getVersionStringForDependency(mavenItem, low);
			if (reqRange.getIncludeMinimum() && Version.MAX_VERSION.equals(high)) {
				versionRangeString.append("[").append(lowDependencyVersion).append(",)");
			} else {
				versionRangeString.append(reqRange.getIncludeMinimum() ? '[' : '(');
				versionRangeString.append(lowDependencyVersion);
				String highDependencyVersion = getVersionStringForDependency(mavenItem, high);
				if (!lowDependencyVersion.equals(highDependencyVersion)) {
					versionRangeString.append(',');
					versionRangeString.append(highDependencyVersion);
				}
				versionRangeString.append(reqRange.getIncludeMaximum() ? ']' : ')');
			}
			return versionRangeString.toString();
		} else {
			return "[0.0,)";
		}
	}

	private static String getVersionSegment(String value, String operator, String operand) {
		if (value != null && operator != null) {
			switch (operator) {
				case "+": {
					return Integer.toString(Math.max(0, Integer.parseInt(value) + Integer.parseInt(operand)));
				}
				case "-": {
					return Integer.toString(Math.max(0, Integer.parseInt(value) - Integer.parseInt(operand)));
				}
			}
		}

		return value;
	}

	@Override
	public boolean isResolved() {
		return installableUnit.isResolved();
	}

	@Override
	public boolean isSingleton() {
		return installableUnit.isSingleton();
	}

	public boolean isSourceArtifact() {
		// consult p2 metadata:
		if (!InstallableUnitUtils.isSourceBundle(this.installableUnit))
			return false;
		return map().isSources();
	}

	public boolean isTransient() {
		return transientFlag;
	}

	public MavenItem map() {
		if (mapped == null) {
			mapped = mapIU(installableUnit);
		}
		return mapped;
	}

	private MavenDependencyItem mapRequirement(IRequiredCapability requirement) {
		String iuId = installableUnit.getId();
		String namespace = requirement.getNamespace();
		String name = requirement.getName();
		VersionRange range = requirement.getRange();
		for (MavenDependencyMapping mapping : dependencyMappings) {
			MavenDependencyItem map = mapping.map(iuId, namespace, name, range);
			if (map != null) {
				return map;
			}
		}

		throw new IllegalStateException(
				"We should never get to this point because the terminal mapping will match all requirements");
	}

	private MavenItem mapIU(IInstallableUnit iu) {
		String id = iu.getId();
		Version version = iu.getVersion();
		for (MavenMapping mapping : mappings) {
			MavenItem mavenItem = mapping.map(id, version);
			if (mavenItem != null) {
				String groupId = iu.getProperty("maven-groupId");
				String artifactId = iu.getProperty("maven-artifactId");
				if (groupId != null && groupId.equals(mapping.getGroupId()) && artifactId != null
						&& artifactId.equals(mavenItem.getArtifactId())) {
					LogUtils.info("The mapping is identical to the p2 metadata: " + id + " => "
							+ GeneralUtils.toString(mapping));
				}

				if (!update(mavenItem.getArtifactId(), mavenItem::setArtifactId, iu)
						|| !update(mavenItem.getGroupId(), mavenItem::setGroupId, iu)
						|| !update(mavenItem.getMappedVersion(), mavenItem::setMappedVersion, iu)) {
					continue;
				}

				if (iu == installableUnit) {
					if (this.versionFormat != VersionFormat.MAVEN_SNAPSHOT) {
						if (mapping.isSnapshot()) {
							versionFormat = VersionFormat.MAVEN_SNAPSHOT; // override global format
						} else { // fade out the following workaround?
							String mappedVersion = mavenItem.getMappedVersion();
							if (mappedVersion != null && mappedVersion.contains(VersionUtil.DASH_SNAPSHOT)) {
								versionFormat = VersionFormat.MAVEN_SNAPSHOT; // override global format
							}
						}
					}
				}

				usedMavenMappings.add(mapping);

				mavenItemVersions.put(mavenItem, version);

				return mavenItem;
			}
		}

		throw new IllegalStateException(
				"We should never get to this point because the default mapping will match all IUs");
	}

	private Set<IInstallableUnit> resolveMapping(IRequiredCapability requirement) {
		IQueryResult<IInstallableUnit> result = index.query(QueryUtil.createMatchQuery(requirement.getMatches()),
				new NullProgressMonitor());
		Set<IInstallableUnit> ius = result.toSet();
		if (ius.size() > 1) {
			Set<IInstallableUnit> onlyAggregatedIUs = new LinkedHashSet<IInstallableUnit>(ius);
			onlyAggregatedIUs.retainAll(aggregatedIUs);
			if (!onlyAggregatedIUs.isEmpty()) {
				ius = onlyAggregatedIUs;
			}

			if (ius.size() > 1) {
				Set<String> ids = new HashSet<String>();
				ius.removeIf(iu -> !ids.add(iu.getId()));

				if (ius.size() > 1) {
					Optional<IInstallableUnit> jre = ius.stream().filter(iu -> "a.jre.javase".equals(iu.getId()))
							.findFirst();
					if (jre.isPresent()) {
						return Collections.singleton(jre.get());
					}

					if ("java.package".equals(requirement.getNamespace())) {
						String name = requirement.getName();
						Set<IInstallableUnit> filteredIUs = new LinkedHashSet<IInstallableUnit>(ius);
						// Remove each requirement that is satisfied by one of the IU's own capabilities.
						filteredIUs.removeIf(iu -> {
							for (IRequirement r : iu.getRequirements()) {
								if (r instanceof IRequiredCapability) {
									IRequiredCapability requiredCapability = (IRequiredCapability) r;
									if (requiredCapability.getMin() != 0
											&& "java.package".equals(requiredCapability.getNamespace())
											&& name.equals(requiredCapability.getName())) {
										return true;
									}
								}
							}
							return false;
						});

						if (!filteredIUs.isEmpty()) {
							ius = filteredIUs;
						}
					}
				}
			}
		}
		return ius;
	}

	private Collection<MavenItem> map(MavenItem containingMavenItem, IRequiredCapability requirement,
			String targetGroupId, String targetArtifactId) {
		Set<IInstallableUnit> ius = resolveMapping(requirement);
		if (ius.isEmpty()) {
			if (requirement.getMin() == 0) {
				return Collections.emptyList();
			}
			throw new IllegalStateException("Unresolved requirement " + requirement);
		}

		String groupId = containingMavenItem.getGroupId();
		String artifactId = containingMavenItem.getArtifactId();
		MavenItem[] mavenItems = ius.stream().map(this::mapIU)
				.filter(it -> !(groupId.equals(it.getGroupId()) && artifactId.equals(it.getArtifactId()))
						&& !"a.jre.javase".equals(it.getArtifactId())
						&& ("*".equals(targetGroupId) || targetGroupId.equals(it.getGroupId())
								&& targetArtifactId.equals(it.getArtifactId())))
				.toArray(MavenItem[]::new);

		if (mavenItems.length > 1) {
			StringBuilder info = new StringBuilder();
			IInstallableUnit containingIU = (IInstallableUnit) ((EObject) requirement).eContainer();
			info.append("Multiple requirement resolutions: " + containingIU.getId() + " -> " + requirement + "\n");
			for (MavenItem mavenItem : mavenItems) {
				info.append("  " + mavenItem.getGroupId() + ":" + mavenItem.getArtifactId() + "\n");
			}
			LogUtils.info(info.toString());
		}

		return Arrays.asList(mavenItems);
	}

	private boolean update(String value, Consumer<String> set, IInstallableUnit iu) {
		if (value != null) {
			String key = getPropertyKey(value);
			if (key != null) {
				String propertyValue = iu.getProperty(key);
				if (propertyValue != null) {
					set.accept(propertyValue);
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private String getPropertyKey(String value) {
		if (value != null) {
			Matcher matcher = IU_PROPERTY_SUBSTITUTION_PATTERN.matcher(value);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	private boolean matchesFilter(IMatchExpression<IInstallableUnit> filter, Map<String, String> map) {
		for (Object object : filter.getParameters()) {
			if (object instanceof IFilterExpression) {
				return ((IFilterExpression) object).match(map);
			}
		}
		return false;
	}

	public boolean satisfies(IRequiredCapability candidate) {
		return installableUnit.satisfies(candidate);
	}

	@Override
	public boolean satisfies(IRequirement candidate) {
		return installableUnit.satisfies(candidate);
	}

	public void setParent(InstallableUnitMapping parent) {
		if (this.parent != null)
			this.parent.children.remove(this);
		(this.parent = parent).children.add(this);
	}

	public void setTransient(boolean isTransient) {
		transientFlag = isTransient;
	}

	private String trimOrNull(String value) {
		if (value != null) {
			value = value.trim();
			if (value.length() == 0)
				value = null;
		}
		return value;
	}

	@Override
	public IInstallableUnit unresolved() {
		return installableUnit.unresolved();
	}

	public void setHasSources(boolean flag) {
		this.hasSources = flag;
	}

	public boolean hasSources() {
		return hasSources;
	}

	public IInstallableUnit getGenuine() {
		return installableUnit;
	}

	public Set<MavenMapping> getUsedMavenMappings() {
		return usedMavenMappings;
	}

	@Override
	public String toString() {
		return "InstallableUnitMapping [installableUnit="
				+ (installableUnit == null ? "null" : installableUnit.getId() + "/" + installableUnit.getVersion())
				+ ", versionFormat=" + versionFormat + "]";
	}
}
