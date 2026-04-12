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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes.Name;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.model.io.DefaultModelWriter;
import org.eclipse.cbi.p2repo.aggregator.Aggregation;
import org.eclipse.cbi.p2repo.aggregator.Contribution;
import org.eclipse.cbi.p2repo.aggregator.MavenItem;
import org.eclipse.cbi.p2repo.aggregator.VersionFormat;
import org.eclipse.cbi.p2repo.aggregator.impl.MavenMappingImpl;
import org.eclipse.cbi.p2repo.p2.maven.MavenMetadata;
import org.eclipse.cbi.p2repo.p2.maven.metadata.MetaData;
import org.eclipse.cbi.p2repo.p2.maven.metadata.MetadataFactory;
import org.eclipse.cbi.p2repo.p2.maven.metadata.Snapshot;
import org.eclipse.cbi.p2repo.p2.maven.metadata.SnapshotVersion;
import org.eclipse.cbi.p2repo.p2.maven.metadata.SnapshotVersionsType;
import org.eclipse.cbi.p2repo.p2.maven.metadata.Versioning;
import org.eclipse.cbi.p2repo.p2.maven.metadata.VersionsType;
import org.eclipse.cbi.p2repo.p2.maven.util.DigestUtil;
import org.eclipse.cbi.p2repo.p2.maven.util.VersionUtil;
import org.eclipse.cbi.p2repo.p2.util.IUUtils;
import org.eclipse.cbi.p2repo.util.ExceptionUtils;
import org.eclipse.cbi.p2repo.util.LogUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.equinox.internal.p2.metadata.BasicVersion;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Filip Hrbek (filip.hrbek@cloudsmith.com)
 *
 */
public class MavenManager {

	private static final String BUNDLE_ID = FrameworkUtil.getBundle(MavenManager.class).getSymbolicName();

	private static final org.osgi.framework.Version BUNDLE_VERSION = FrameworkUtil.getBundle(MavenManager.class)
			.getVersion();

	static class MavenMetadataHelper {
		private String groupId;

		private String artifactId;

		private List<Version> versionList;
		private Map<Version, Boolean> snapshots;

		private VersionFormat versionFormat;

		private boolean finalized;

		private boolean hasSources;

		MavenMetadataHelper(String groupId, String artifactId, VersionFormat versionFormat, boolean hasSources) {
			this.groupId = groupId;
			this.artifactId = artifactId;
			this.versionList = new ArrayList<Version>();
			this.snapshots = new HashMap<>();
			this.versionFormat = versionFormat;
			this.hasSources = hasSources;
		}

		public void addVersion(Version version, boolean isSnapshot) {
			if (finalized) {
				throw new Error("Version added after finalization");
			}

			versionList.add(version);
			snapshots.put(version, isSnapshot);
		}

		private void finalizeMetadata() {
			Collections.sort(versionList);
			finalized = true;
		}

		public String getArtifactId() {
			return artifactId;
		}

		public String getGroupId() {
			return groupId;
		}

		public String getRelativePath() {
			return groupId.replace('.', '/') + "/" + artifactId;
		}

		public Version getRelease() {
			finalizeMetadata();
			Version[] versions = versionList.toArray(new Version[versionList.size()]);
			for (int idx = versions.length - 1; idx >= 0; idx--) {
				String qualifier = null;
				try {
					if (versions[idx] instanceof BasicVersion)
						qualifier = ((BasicVersion) versions[idx]).getQualifier();
				} catch (UnsupportedOperationException e) {
					// ignore
				}

				if (qualifier != null && qualifier.length() > 0
						&& (qualifier.charAt(0) == 'R' || qualifier.charAt(0) == 'M'))
					continue;
				if (snapshots.get(versions[idx]) == Boolean.TRUE)
					continue;
				if (versions[idx].toString().contains(VersionUtil.DASH_SNAPSHOT)) // fade out name based recognition of snapshot?
					continue;

				return versions[idx];
			}

			return null;
		}

		public Version getLatest() {
			Version[] sortable = versionList.toArray(new Version[versionList.size()]);
			Arrays.sort(sortable);
			return sortable[sortable.length - 1];
		}

		public VersionFormat getVersionFormat() {
			return versionFormat;
		}

		public List<Version> getVersions() {
			finalizeMetadata();
			return versionList;
		}

		public boolean hasSources() {
			return hasSources;
		}
	}

	static class SourceReferenceAnalyzer {
		private static final Pattern GITHUB_SCM_PATTERN = Pattern
				.compile("scm:git:https://github.com/(?<org>[^/]+)/(?<repo>[^/]+?)(.git)?");

		private static final Pattern OLD_ECLIPSE_SCM_PATTERN = Pattern
				.compile("scm:git:https://git.eclipse.org/c/(?<org>[^/]+)/(?<repo>[^/]+?)(.git)?");

		private final String eclipseSourceReferences;

		public SourceReferenceAnalyzer(String eclipseSourceReferences) {
			this.eclipseSourceReferences = eclipseSourceReferences;
		}

		public void analyze() {
			if (eclipseSourceReferences == null) {
				return;
			}

			try {
				ManifestElement[] eclipseSourceReferenceElements = ManifestElement
						.parseHeader("Eclipse-SourceReferences", eclipseSourceReferences);
				for (ManifestElement eclipseSourceReferenceElement : eclipseSourceReferenceElements) {
					String path = eclipseSourceReferenceElement.getAttribute("path");
					String commitId = eclipseSourceReferenceElement.getAttribute("commitId");
					String value = eclipseSourceReferenceElement.getValue();

					Matcher oldEclipseSCMMatcher = OLD_ECLIPSE_SCM_PATTERN.matcher(value);
					if (oldEclipseSCMMatcher.matches()) {
						String org = oldEclipseSCMMatcher.group("org");
						String repo = oldEclipseSCMMatcher.group("repo");
						String url = "https://github.com/eclipse-" + org + "/" + repo + ".git";
						try (InputStream input = new URL(url).openStream()) {
						} catch (IOException e) {
							url = null;
						}
						if (url == null && repo.startsWith("org.eclipse.")) {
							url = "https://github.com/eclipse-" + org + "/" + repo.replace("org.eclipse.", "") + ".git";
							try (InputStream input = new URL(url).openStream()) {
							} catch (IOException e) {
								url = null;
							}
						}
						if (url != null) {
							value = "scm:git:" + url;
						}
					}

					handleSourceReference(value, path, commitId);

					Matcher matcher = GITHUB_SCM_PATTERN.matcher(value);
					if (matcher.matches()) {
						String org = matcher.group("org");
						String repo = matcher.group("repo");
						handleGitHubSourceReference(org, repo, path, commitId);
					}

					// It doesn't make sense for there to be more than one.
					break;
				}
			} catch (BundleException e) {
				throw new RuntimeException(e);
			}
		}

		public void handleSourceReference(String scm, String path, String commitId) {
		}

		public void handleGitHubSourceReference(String org, String repo, String path, String commitId) {
		}

		public String createGitHubURL(String org, String repo) {
			return "https://github.com/" + org + '/' + repo + '/';
		}
	}

	private static void addMappingRule(List<String[]> mappingRulesList, InstallableUnitMapping iu)
			throws CoreException {
		if (iu.getMainArtifact() != null) {
			IArtifactKey artifact = iu.getMainArtifact();
			mappingRulesList.add(new String[] {
					"(& (classifier=" + IUUtils.encodeFilterValue(artifact.getClassifier()) + ")(id="
							+ IUUtils.encodeFilterValue(artifact.getId()) + ")(version="
							+ IUUtils.encodeFilterValue(iu.getVersion().toString()) + "))",
					"${repoUrl}/" + iu.getRelativeFullPath() });
		}
	}

	private static URI createArtifactURI(URI root, InstallableUnitMapping iu) throws CoreException {
		if (iu.getMainArtifact() != null)
			return URI.createURI(root.toString() + "/" + iu.getRelativeFullPath());

		return null;
	}

	private static void createCheckSum(URI fileUri, URIConverter uriConverter, MessageDigest[] digests)
			throws CoreException {
		InputStream is = null;
		PrintWriter digestWriter = null;
		try {
			is = uriConverter.createInputStream(fileUri);
			for (MessageDigest digest : digests)
				digest.reset();

			byte[] buffer = new byte[4096];
			int read;
			while ((read = is.read(buffer)) != -1)
				for (MessageDigest digest : digests)
					digest.update(buffer, 0, read);
			is.close();

			for (MessageDigest digest : digests) {
				byte[] result = digest.digest();

				URI digestUri = URI.createURI(fileUri.toString() + "." + digest.getAlgorithm().toLowerCase());
				digestWriter = new PrintWriter(uriConverter.createOutputStream(digestUri));
				for (byte b : result)
					digestWriter.printf("%02x", Byte.valueOf(b));
				digestWriter.close();
			}
		} catch (IOException e) {
			throw ExceptionUtils.fromMessage(e, "Error creating digest for %s", fileUri.toString());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// ignore
				}
			if (digestWriter != null)
				digestWriter.close();
		}
	}

	private static Map<String, String> getManifest(URI fileURI, URIConverter uriConverter) {
		try (var inputStream = uriConverter
				.createInputStream(URI.createURI("archive:" + fileURI + "!/META-INF/MANIFEST.MF"))) {
			Map<String, String> manifest = ManifestElement.parseBundleManifest(inputStream);
			if (Boolean.FALSE) {
				getLicenseFromAbout(fileURI, manifest, uriConverter);
			}
			getLicenseFromGitHubRepository(manifest, uriConverter);
			getLicenseFromLICENSE(fileURI, manifest, uriConverter);
			return manifest;
		} catch (IOException | BundleException e) {
			return Map.of();
		}
	}

	private static void getLicenseFromLICENSE(URI fileUri, Map<String, String> manifest, URIConverter uriConverter) {
		if (manifest.get(Constants.BUNDLE_LICENSE) == null) {
			try (var inputStream = uriConverter
					.createInputStream(URI.createURI("archive:" + fileUri + "!/META-INF/LICENSE"))) {
				String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				Pattern apache20Pattern = Pattern.compile(
						"^\\s*Apache License\\s*Version\\s+2.0,\\s*January\\s*2004\\s+http://www.apache.org/licenses/");
				if (apache20Pattern.matcher(content).find()) {
					manifest.put(Constants.BUNDLE_LICENSE,
							"Apache-2.0;link=\"https://www.apache.org/licenses/LICENSE-2.0.txt\"");
				}
			} catch (IOException e) {
				//$FALL-THROUGH$
			}
		}
	}

	private static void getLicenseFromAbout(URI fileUri, Map<String, String> manifest, URIConverter uriConverter) {
		try (var inputStream = uriConverter.createInputStream(URI.createURI("archive:" + fileUri + "!/about.html"))) {
			String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			Pattern HREF_PATTERN = Pattern.compile("<a[^>]*href=[\"'>]([^\"'>]+)[\"'>]");
			System.err.println("about> " + fileUri);
			for (Matcher matcher = HREF_PATTERN.matcher(content); matcher.find();) {
				System.err.println("about>url> " + matcher.group(1));
			}
		} catch (IOException e) {
			System.err.println("about!> " + fileUri);
		}
	}

	private static void getLicenseFromGitHubRepository(Map<String, String> manifest, URIConverter uriConverter) {
		String eclipseSourceReferences = manifest.get("Eclipse-SourceReferences");
		if (eclipseSourceReferences != null && manifest.get(Constants.BUNDLE_LICENSE) == null) {
			new SourceReferenceAnalyzer(eclipseSourceReferences) {
				public void handleGitHubSourceReference(String org, String repo, String path, String commitId) {
					// Access the raw github license content required by the EDP using the tag, e.g.,
					// https://raw.githubusercontent.com/eclipse-emf/org.eclipse.emf/1d0383954386cab3166f245e5a80d83a38344420/LICENSE
					URI rawLicenceURI = URI.createURI(
							"https://raw.githubusercontent.com/" + org + '/' + repo + "/" + commitId + "/LICENSE");
					URI rawLicenceURIMaster = URI
							.createURI("https://raw.githubusercontent.com/" + org + '/' + repo + "/master/LICENSE");
					URI rawLicenceURIMain = URI
							.createURI("https://raw.githubusercontent.com/" + org + '/' + repo + "/main/LICENSE");
					for (URI uri : new URI[] { rawLicenceURI, rawLicenceURIMaster, rawLicenceURIMain }) {
						try (InputStream licenseInputStream = uriConverter.createInputStream(uri)) {
							String license = new String(licenseInputStream.readAllBytes(), StandardCharsets.UTF_8);
							if (license.startsWith("Eclipse Public License - v 2.0")) {
								manifest.put(Constants.BUNDLE_LICENSE,
										"EPL-2.0;link=\"https://www.eclipse.org/legal/epl-2.0/\"");
								return;
							}
						} catch (IOException e) {
						}
					}
				}
			}.analyze();
		}
	}

	public static MavenRepositoryHelper createMavenStructure(List<InstallableUnitMapping> ius, Aggregation aggregation)
			throws CoreException {
		List<String[]> mappingRulesList = new ArrayList<String[]>();

		// Initialize with standard rules for packed artifacts (which are not usable for maven anyway)
		mappingRulesList.add(new String[] { "(& (classifier=osgi.bundle) (format=packed))",
				"${repoUrl}/p2.packed/plugins/${id}_${version}.jar.pack.gz" });
		mappingRulesList.add(new String[] { "(& (classifier=org.eclipse.update.feature) (format=packed))",
				"${repoUrl}/p2.packed/features/${id}_${version}.jar.pack.gz" });

		Map<String, List<InstallableUnitMapping>> groupMap = new HashMap<String, List<InstallableUnitMapping>>();

		for (InstallableUnitMapping iu : ius) {
			String groupId = iu.map().getGroupId();
			List<InstallableUnitMapping> group = groupMap.get(groupId);
			if (group == null)
				groupMap.put(groupId, group = new ArrayList<InstallableUnitMapping>());
			group.add(iu);
		}

		InstallableUnitMapping top = new InstallableUnitMapping(aggregation.getVersionFormat());
		top.setTransient(true);
		addMappingRule(mappingRulesList, top);
		// FIXME: rule uses mavenized name, OK?

		for (Map.Entry<String, List<InstallableUnitMapping>> entry : groupMap.entrySet()) {
			InstallableUnitMapping group = new InstallableUnitMapping(entry.getKey());
			addMappingRule(mappingRulesList, group);

			// This is a place where we can find common-in-group properties
			// and store them in the group IU mapping.
			// This is left out for now to make the whole thing functional,
			// but can be done later to optimize the maven structure...

			for (InstallableUnitMapping iu : entry.getValue()) {
				addMappingRule(mappingRulesList, iu);
				iu.setParent(group);

				// original IUs with more than 1 artifact have generated siblings
				for (InstallableUnitMapping sibling : iu.getSiblings()) {
					addMappingRule(mappingRulesList, sibling);
					sibling.setParent(group);
				}
			}

			group.setParent(top);
			group.setTransient(true);
		}

		return new MavenRepositoryHelper(top, mappingRulesList.toArray(new String[mappingRulesList.size()][]));
	}

	private static URI createPomURI(URI root, InstallableUnitMapping iu) throws CoreException {
		return URI.createURI(root.toString() + "/" + iu.getRelativePath() + "/" + iu.getPomName());
	}

	private static URI createXmlURI(URI root, MavenMetadataHelper md) throws CoreException {
		return URI.createURI(root.toString() + "/" + md.getRelativePath() + "/maven-metadata.xml");
	}

	private static URI createSnapshotXmlURI(URI root, MavenMetadataHelper md, Version version) throws CoreException {
		String versionDir = VersionUtil.getVersionString(version, VersionFormat.MAVEN_SNAPSHOT, false, -1);
		return URI.createURI(root.toString() + "/" + md.getRelativePath() + "/" + versionDir + "/maven-metadata.xml");
	}

	public static void saveMetadata(URI root, InstallableUnitMapping iu, Map<Contribution, List<String>> errors,
			int buildNumber, boolean validateNexusPublishingRequirements) throws CoreException {
		URIConverter uriConverter = new ExtensibleURIConverterImpl() {
			Map<URI, Object> cache = new HashMap<URI, Object>();

			@Override
			public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
				// We might load the license file from a git repository many, many times.
				if ("https".equals(uri.scheme())) {
					Object value = cache.get(uri);
					if (value instanceof byte[] bytes) {
						return new ByteArrayInputStream(bytes);
					}

					if (value instanceof IOException e) {
						throw e;
					}

					try (InputStream inputStream = super.createInputStream(uri, options)) {
						byte[] bytes = inputStream.readAllBytes();
						cache.put(uri, bytes);
						return new ByteArrayInputStream(bytes);
					} catch (IOException e) {
						cache.put(uri, e);
						throw e;
					}
				}
				return super.createInputStream(uri, options);
			}
		};
		Map<String, MavenMetadataHelper> metadataCollector = new HashMap<String, MavenMetadataHelper>();

		savePOMs(root, iu, uriConverter, DigestUtil.MESSAGE_DIGESTERS, metadataCollector, errors,
				validateNexusPublishingRequirements);
		saveXMLs(root, uriConverter, DigestUtil.MESSAGE_DIGESTERS, metadataCollector, buildNumber);
	}

	private static void savePOMs(URI root, InstallableUnitMapping iu, URIConverter uriConverter,
			MessageDigest[] digests, Map<String, MavenMetadataHelper> metadataCollector,
			Map<Contribution, List<String>> errors, boolean validateNexusPublishingRequirements) throws CoreException {
		if (!iu.isTransient()) {
			boolean isSource = iu.isSourceArtifact();
			URI artifactUri = createArtifactURI(root, iu);
			if (!isSource) {
				URI pomUri = createPomURI(root, iu);
				try {
					Model pom = iu.asPOM(getManifest(artifactUri, uriConverter));
					// https://central.sonatype.org/publish/requirements/
					if (validateNexusPublishingRequirements) {
						URI sourceArtifactURI = URI
								.createURI(artifactUri.toString().replaceAll("\\.jar$", "-sources.jar"));
						if (!uriConverter.exists(sourceArtifactURI, Map.of())
								&& hasClassFiles(artifactUri, uriConverter)) {
							throw new CoreException(new Status(IStatus.ERROR, MavenManager.class, iu
									+ " contains .class files and is missing a corresponding source bundle artifact."));
						}

						Set<String> missingRequiredElements = new TreeSet<>();
						if (pom.getName() == null) {
							missingRequiredElements.add("name");
						}
						if (pom.getDescription() == null) {
							missingRequiredElements.add("description");
						}
						if (pom.getUrl() == null) {
							missingRequiredElements.add("url");
						}
						if (pom.getLicenses().isEmpty()) {
							missingRequiredElements.add("licenses");
						}
						if (pom.getDevelopers().isEmpty()) {
							missingRequiredElements.add("developers");
						}
						Scm scm = pom.getScm();
						if (scm == null) {
							missingRequiredElements.add("scm");
						} else {
							if (scm.getConnection() == null) {
								missingRequiredElements.add("scm.connection");
							}
							if (scm.getDeveloperConnection() == null) {
								missingRequiredElements.add("scm.developerConnection");
							}
							if (scm.getUrl() == null) {
								missingRequiredElements.add("scm.url");
							}
						}

						if (!missingRequiredElements.isEmpty()) {
							throw new CoreException(new Status(IStatus.ERROR, MavenManager.class,
									iu + " missing " + missingRequiredElements));
						}
					}
					new DefaultModelWriter().write(new File(java.net.URI.create(pomUri.toString())), Map.of(), pom);
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, MavenManager.class, e.getMessage(), e));
				}
				createCheckSum(pomUri, uriConverter, digests);
			}

			if (artifactUri != null) {
				try {
					createCheckSum(artifactUri, uriConverter, digests);
				} catch (CoreException e) {
					Contribution contrib = iu.getContribution();
					List<String> contribErrors = errors.get(contrib);
					if (contribErrors == null)
						errors.put(contrib, contribErrors = new ArrayList<String>());
					StringBuilder msgBuilder = new StringBuilder(e.getMessage());
					if (e.getCause() != null) {
						msgBuilder.append(':');
						msgBuilder.append(' ');
						msgBuilder.append(e.getCause().toString());
					}
					String msg = msgBuilder.toString();
					LogUtils.error(msg);
					contribErrors.add(msg);
				}
			}

			if (isSource) {
				URI javadocArfiactURI = URI
						.createURI(artifactUri.toString().replaceAll("-sources\\.jar$", "-javadoc.jar"));
				createJavadocJar(javadocArfiactURI, uriConverter);
				createCheckSum(javadocArfiactURI, uriConverter, digests);
				return; // don't add source jar to metadataCollector
			}

			String key = iu.map().getGroupId() + "/" + iu.map().getArtifactId();
			MavenMetadataHelper md = metadataCollector.get(key);
			if (md == null) {
				md = new MavenMetadataHelper(iu.map().getGroupId(), iu.map().getArtifactId(), iu.getVersionFormat(),
						iu.hasSources());
				metadataCollector.put(key, md);
			}
			md.addVersion(iu.getMappedVersion(), iu.getVersionFormat() == VersionFormat.MAVEN_SNAPSHOT);
			// will potentially be mapped to the global VersionFormat during saveXMLs
		}

		for (InstallableUnitMapping child : iu.getChildren()) {
			if (child.isSourceArtifact()) {
				markSiblingHasSources(child, iu.getChildren());
			}
		}

		for (InstallableUnitMapping child : iu.getChildren()) {
			savePOMs(root, child, uriConverter, digests, metadataCollector, errors,
					validateNexusPublishingRequirements);
		}
	}

	private static boolean hasClassFiles(URI artifactURI, URIConverter uriConverter) {
		try (JarInputStream inputStream = new JarInputStream(uriConverter.createInputStream(artifactURI))) {
			for (ZipEntry nextEntry = inputStream.getNextEntry(); nextEntry != null; nextEntry = inputStream
					.getNextEntry()) {
				String name = nextEntry.getName();
				if (name.endsWith(".class")) {
					return true;
				}
				if (name.endsWith(".jar")) {
					try (JarInputStream nestedInputStream = new JarInputStream(inputStream)) {
						for (ZipEntry nestedNextEntry = nestedInputStream
								.getNextEntry(); nestedNextEntry != null; nestedNextEntry = nestedInputStream
										.getNextEntry()) {
							if (nestedNextEntry.getName().endsWith(".class")) {
								return true;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return false;
	}

	private static void createJavadocJar(URI javadocArfiactURI, URIConverter uriConverter) throws CoreException {
		URI binaryArfiactURI = URI.createURI(javadocArfiactURI.toString().replaceAll("-javadoc\\.jar$", ".jar"));
		String link = new SourceReferenceAnalyzer(
				getManifest(binaryArfiactURI, uriConverter).get("Eclipse-SourceReferences")) {
			private String url;

			@Override
			public void handleGitHubSourceReference(String org, String repo, String path, String commitId) {
				String baseURL = createGitHubURL(org, repo);
				url = baseURL + "tree/" + commitId + "/" + path;
			}

			String getURL() {
				analyze();
				return url;
			}
		}.getURL();

		String readmeContent = link == null ? //
				"Documentation is available via the source jar "
						+ javadocArfiactURI.lastSegment().replaceAll("-javadoc\\.jar$", "-sources.jar")
				: "Documentation available at " + link;

		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Name.MANIFEST_VERSION, "1");
		manifest.getMainAttributes().putValue("Created-By", BUNDLE_ID + " " + BUNDLE_VERSION);
		try (JarOutputStream out = new JarOutputStream(uriConverter.createOutputStream(javadocArfiactURI), manifest)) {
			out.putNextEntry(new ZipEntry("README.txt"));
			out.write(readmeContent.getBytes(StandardCharsets.UTF_8));
			out.closeEntry();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, MavenManager.class, e.getMessage(), e));
		}
	}

	public static void markSiblingHasSources(InstallableUnitMapping sourcesIU, List<InstallableUnitMapping> siblings)
			throws CoreException {
		// find the matching classifier-less sibling IU:
		MavenItem sourcesItem = sourcesIU.map();
		String artifactId = sourcesItem.getArtifactId();
		String groupId = sourcesItem.getGroupId();
		for (InstallableUnitMapping siblingMapping : siblings) {
			if (siblingMapping == sourcesIU)
				continue;
			MavenItem sibling = siblingMapping.map();
			if (sibling.getGroupId().equals(groupId) && sibling.getArtifactId().equals(artifactId)
					&& sibling.getClassifier() == null) {
				siblingMapping.setHasSources(true);
				break;
			}
		}
	}

	private static void saveXMLs(URI root, URIConverter uriConverter, MessageDigest[] digests,
			Map<String, MavenMetadataHelper> metadataCollector, int buildNumber) throws CoreException {
		String timestamp = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", new Date());
		String timestampDot = String.format("%1$tY%1$tm%1$td.%1$tH%1$tM%1$tS", new Date());

		for (MavenMetadataHelper mdh : metadataCollector.values()) {
			mdh.finalizeMetadata();
			VersionFormat versionFormat = mdh.getVersionFormat();

			MavenMetadata mdConainter = new MavenMetadata();
			MetaData md = mdConainter.getMetaData();
			md.setGroupId(mdh.getGroupId());
			md.setArtifactId(mdh.getArtifactId());
			Versioning versioning = MetadataFactory.eINSTANCE.createVersioning();
			md.setVersioning(versioning);
			versioning.setLastUpdated(timestamp);
			Version release = mdh.getRelease();
			if (release != null)
				versioning.setRelease(VersionUtil.getVersionString(release, versionFormat, false, -1));
			versioning.setLatest(VersionUtil.getVersionString(mdh.getLatest(), versionFormat, false, -1));
			VersionsType versions = MetadataFactory.eINSTANCE.createVersionsType();
			versioning.setVersions(versions);
			List<String> versionList = versions.getVersion();
			mdh.getVersions().stream().map(v -> VersionUtil.getVersionString(v, versionFormat, false, -1)) //
					.distinct() // avoid duplicates
					.forEach(versionList::add);

			URI xmlUri = createXmlURI(root, mdh);
			mdConainter.save(xmlUri);
			createCheckSum(xmlUri, uriConverter, digests);

			if (versionFormat == VersionFormat.MAVEN_SNAPSHOT) {
				for (Version version : mdh.getVersions()) {
					mdConainter = new MavenMetadata();
					md = mdConainter.getMetaData();
					md.setGroupId(mdh.getGroupId());
					md.setArtifactId(mdh.getArtifactId());
					String versionString = VersionUtil.getVersionString(version, versionFormat, false, -1);
					md.setVersion(versionString);

					versioning = MetadataFactory.eINSTANCE.createVersioning();
					md.setVersioning(versioning);
					versioning.setLastUpdated(timestamp);

					Snapshot snapshot = MetadataFactory.eINSTANCE.createSnapshot();
					versioning.setSnapshot(snapshot);
					snapshot.setTimestamp(timestampDot);
					snapshot.setBuildNumber(buildNumber);

					SnapshotVersionsType snapVersions = MetadataFactory.eINSTANCE.createSnapshotVersionsType();
					versioning.setSnapshotVersions(snapVersions);
					EList<SnapshotVersion> snapVersionList = snapVersions.getSnapshotVersion();
					String snapshotVersion = VersionUtil.getVersionString(version, VersionFormat.MAVEN_SNAPSHOT, true,
							buildNumber);
					snapVersionList.add(createSnapshotVersion(null, "jar", snapshotVersion, timestamp));
					snapVersionList.add(createSnapshotVersion(null, "pom", snapshotVersion, timestamp));
					if (mdh.hasSources()) {
						snapVersionList.add(createSnapshotVersion(MavenMappingImpl.MAVEN_SOURCES_CLASSIFIER, "jar",
								snapshotVersion, timestamp));
					}
					xmlUri = createSnapshotXmlURI(root, mdh, version);
					mdConainter.save(xmlUri);
					createCheckSum(xmlUri, uriConverter, digests);
				}
			}
		}
	}

	public static SnapshotVersion createSnapshotVersion(String classifier, String extension, String version,
			String updated) {
		SnapshotVersion snapshotVersion = MetadataFactory.eINSTANCE.createSnapshotVersion();
		snapshotVersion.setExtension(extension);
		snapshotVersion.setValue(version);
		snapshotVersion.setUpdated(updated);
		if (classifier != null) {
			snapshotVersion.setClassifier(classifier);
		}
		return snapshotVersion;
	}

	public static String toXML(Model model) {
		StringWriter out = new StringWriter();
		try {
			new DefaultModelWriter().write(out, Map.of(), model);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toString();
	}
}
