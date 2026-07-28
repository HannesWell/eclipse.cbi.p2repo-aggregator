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
package org.eclipse.cbi.p2repo.aggregator.analyzer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerFactory
 * @model kind="package"
 * @generated
 */
public interface AnalyzerPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "analyzer";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://www.eclipse.org/cbi/p2repo/2021/aggregator/analyzer";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "analyzer";

	/**
	 * The package content type ID.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eCONTENT_TYPE = "org.eclipse.cbi.p2repo.aggregator.analyzer";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AnalyzerPackage eINSTANCE = org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalysisImpl <em>Analysis</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalysisImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getAnalysis()
	 * @generated
	 */
	int ANALYSIS = 0;

	/**
	 * The feature id for the '<em><b>Release Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__RELEASE_DATE = 0;

	/**
	 * The feature id for the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__EXCLUSION = 1;

	/**
	 * The feature id for the '<em><b>Show Tags</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__SHOW_TAGS = 2;

	/**
	 * The feature id for the '<em><b>Ignore Major Version Duplicates</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__IGNORE_MAJOR_VERSION_DUPLICATES = 3;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__TAGS = 4;

	/**
	 * The feature id for the '<em><b>Levels</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__LEVELS = 5;

	/**
	 * The feature id for the '<em><b>Layout Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__LAYOUT_WIDTH = 6;

	/**
	 * The feature id for the '<em><b>Layout Height</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__LAYOUT_HEIGHT = 7;

	/**
	 * The feature id for the '<em><b>Git Repository Filters</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__GIT_REPOSITORY_FILTERS = 8;

	/**
	 * The feature id for the '<em><b>Aggregation</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__AGGREGATION = 9;

	/**
	 * The feature id for the '<em><b>Contributions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS__CONTRIBUTIONS = 10;

	/**
	 * The number of structural features of the '<em>Analysis</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANALYSIS_FEATURE_COUNT = 11;

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.ContributionAnalysisImpl <em>Contribution Analysis</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.ContributionAnalysisImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getContributionAnalysis()
	 * @generated
	 */
	int CONTRIBUTION_ANALYSIS = 1;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__ENABLED = 0;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__LABEL = 1;

	/**
	 * The feature id for the '<em><b>Dominant</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__DOMINANT = 2;

	/**
	 * The feature id for the '<em><b>Match</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__MATCH = 3;

	/**
	 * The feature id for the '<em><b>Last Modified</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__LAST_MODIFIED = 4;

	/**
	 * The feature id for the '<em><b>Rank</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__RANK = 5;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__TAGS = 6;

	/**
	 * The feature id for the '<em><b>Analysis</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__ANALYSIS = 7;

	/**
	 * The feature id for the '<em><b>Contribution</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__CONTRIBUTION = 8;

	/**
	 * The feature id for the '<em><b>Installable Units</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__INSTALLABLE_UNITS = 9;

	/**
	 * The feature id for the '<em><b>Projects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS__PROJECTS = 10;

	/**
	 * The number of structural features of the '<em>Contribution Analysis</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTION_ANALYSIS_FEATURE_COUNT = 11;

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.InstallableUnitAnalysisImpl <em>Installable Unit Analysis</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.InstallableUnitAnalysisImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getInstallableUnitAnalysis()
	 * @generated
	 */
	int INSTALLABLE_UNIT_ANALYSIS = 2;

	/**
	 * The feature id for the '<em><b>Contribution</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTALLABLE_UNIT_ANALYSIS__CONTRIBUTION = 0;

	/**
	 * The feature id for the '<em><b>Installable Unit</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTALLABLE_UNIT_ANALYSIS__INSTALLABLE_UNIT = 1;

	/**
	 * The feature id for the '<em><b>Requirements</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTALLABLE_UNIT_ANALYSIS__REQUIREMENTS = 2;

	/**
	 * The feature id for the '<em><b>Capabilities</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTALLABLE_UNIT_ANALYSIS__CAPABILITIES = 3;

	/**
	 * The number of structural features of the '<em>Installable Unit Analysis</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTALLABLE_UNIT_ANALYSIS_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.RequirementAnalysisImpl <em>Requirement Analysis</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.RequirementAnalysisImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getRequirementAnalysis()
	 * @generated
	 */
	int REQUIREMENT_ANALYSIS = 3;

	/**
	 * The feature id for the '<em><b>Installable Unit</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIREMENT_ANALYSIS__INSTALLABLE_UNIT = 0;

	/**
	 * The feature id for the '<em><b>Requirement</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIREMENT_ANALYSIS__REQUIREMENT = 1;

	/**
	 * The feature id for the '<em><b>Resolutions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIREMENT_ANALYSIS__RESOLUTIONS = 2;

	/**
	 * The number of structural features of the '<em>Requirement Analysis</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIREMENT_ANALYSIS_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.RequirementResolutionImpl <em>Requirement Resolution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.RequirementResolutionImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getRequirementResolution()
	 * @generated
	 */
	int REQUIREMENT_RESOLUTION = 4;

	/**
	 * The feature id for the '<em><b>Requirement</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIREMENT_RESOLUTION__REQUIREMENT = 0;

	/**
	 * The feature id for the '<em><b>Installable Unit</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIREMENT_RESOLUTION__INSTALLABLE_UNIT = 1;

	/**
	 * The feature id for the '<em><b>Capability</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIREMENT_RESOLUTION__CAPABILITY = 2;

	/**
	 * The number of structural features of the '<em>Requirement Resolution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIREMENT_RESOLUTION_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.CapabilityAnalysisImpl <em>Capability Analysis</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.CapabilityAnalysisImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getCapabilityAnalysis()
	 * @generated
	 */
	int CAPABILITY_ANALYSIS = 5;

	/**
	 * The feature id for the '<em><b>Installable Unit</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_ANALYSIS__INSTALLABLE_UNIT = 0;

	/**
	 * The feature id for the '<em><b>Capability</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_ANALYSIS__CAPABILITY = 1;

	/**
	 * The feature id for the '<em><b>Resolutions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_ANALYSIS__RESOLUTIONS = 2;

	/**
	 * The number of structural features of the '<em>Capability Analysis</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_ANALYSIS_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.CapabilityResolutionImpl <em>Capability Resolution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.CapabilityResolutionImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getCapabilityResolution()
	 * @generated
	 */
	int CAPABILITY_RESOLUTION = 6;

	/**
	 * The feature id for the '<em><b>Capability</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_RESOLUTION__CAPABILITY = 0;

	/**
	 * The feature id for the '<em><b>Requirement</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_RESOLUTION__REQUIREMENT = 1;

	/**
	 * The number of structural features of the '<em>Capability Resolution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_RESOLUTION_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.ProjectImpl <em>Project</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.ProjectImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getProject()
	 * @generated
	 */
	int PROJECT = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Site</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__SITE = 1;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__VERSION = 2;

	/**
	 * The feature id for the '<em><b>News</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__NEWS = 3;

	/**
	 * The feature id for the '<em><b>Release Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__RELEASE_DATE = 4;

	/**
	 * The feature id for the '<em><b>Rank</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__RANK = 5;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__TAGS = 6;

	/**
	 * The feature id for the '<em><b>Contribution</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__CONTRIBUTION = 7;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__PARENT = 8;

	/**
	 * The feature id for the '<em><b>Repositories</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__REPOSITORIES = 9;

	/**
	 * The feature id for the '<em><b>Subprojects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__SUBPROJECTS = 10;

	/**
	 * The number of structural features of the '<em>Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_FEATURE_COUNT = 11;

	/**
	 * The meta object id for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.GitRepositoryImpl <em>Git Repository</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.GitRepositoryImpl
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getGitRepository()
	 * @generated
	 */
	int GIT_REPOSITORY = 8;

	/**
	 * The feature id for the '<em><b>URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GIT_REPOSITORY__URI = 0;

	/**
	 * The feature id for the '<em><b>Last Modified</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GIT_REPOSITORY__LAST_MODIFIED = 1;

	/**
	 * The feature id for the '<em><b>Project</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GIT_REPOSITORY__PROJECT = 2;

	/**
	 * The number of structural features of the '<em>Git Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GIT_REPOSITORY_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '<em>Pattern</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.regex.Pattern
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getPattern()
	 * @generated
	 */
	int PATTERN = 9;

	/**
	 * The meta object id for the '<em>URI</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.URI
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getURI()
	 * @generated
	 */
	int URI = 10;

	/**
	 * The meta object id for the '<em>Date</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.Date
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getDate()
	 * @generated
	 */
	int DATE = 11;

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis <em>Analysis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Analysis</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis
	 * @generated
	 */
	EClass getAnalysis();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getReleaseDate <em>Release Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Release Date</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getReleaseDate()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_ReleaseDate();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getExclusion <em>Exclusion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusion</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getExclusion()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_Exclusion();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#isShowTags <em>Show Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Show Tags</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#isShowTags()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_ShowTags();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#isIgnoreMajorVersionDuplicates <em>Ignore Major Version Duplicates</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore Major Version Duplicates</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#isIgnoreMajorVersionDuplicates()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_IgnoreMajorVersionDuplicates();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Tags</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getTags()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_Tags();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLevels <em>Levels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Levels</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLevels()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_Levels();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLayoutWidth <em>Layout Width</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Layout Width</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLayoutWidth()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_LayoutWidth();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLayoutHeight <em>Layout Height</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Layout Height</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLayoutHeight()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_LayoutHeight();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getGitRepositoryFilters <em>Git Repository Filters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Git Repository Filters</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getGitRepositoryFilters()
	 * @see #getAnalysis()
	 * @generated
	 */
	EAttribute getAnalysis_GitRepositoryFilters();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getAggregation <em>Aggregation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Aggregation</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getAggregation()
	 * @see #getAnalysis()
	 * @generated
	 */
	EReference getAnalysis_Aggregation();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getContributions <em>Contributions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Contributions</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getContributions()
	 * @see #getAnalysis()
	 * @generated
	 */
	EReference getAnalysis_Contributions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis <em>Contribution Analysis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Contribution Analysis</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis
	 * @generated
	 */
	EClass getContributionAnalysis();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#isEnabled <em>Enabled</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enabled</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#isEnabled()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EAttribute getContributionAnalysis_Enabled();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getLabel()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EAttribute getContributionAnalysis_Label();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#isDominant <em>Dominant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dominant</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#isDominant()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EAttribute getContributionAnalysis_Dominant();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getMatch <em>Match</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Match</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getMatch()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EAttribute getContributionAnalysis_Match();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getLastModified <em>Last Modified</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last Modified</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getLastModified()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EAttribute getContributionAnalysis_LastModified();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getRank <em>Rank</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rank</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getRank()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EAttribute getContributionAnalysis_Rank();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Tags</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getTags()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EAttribute getContributionAnalysis_Tags();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getAnalysis <em>Analysis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Analysis</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getAnalysis()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EReference getContributionAnalysis_Analysis();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getContribution <em>Contribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Contribution</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getContribution()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EReference getContributionAnalysis_Contribution();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getInstallableUnits <em>Installable Units</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Installable Units</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getInstallableUnits()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EReference getContributionAnalysis_InstallableUnits();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getProjects <em>Projects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Projects</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getProjects()
	 * @see #getContributionAnalysis()
	 * @generated
	 */
	EReference getContributionAnalysis_Projects();

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis <em>Installable Unit Analysis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Installable Unit Analysis</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis
	 * @generated
	 */
	EClass getInstallableUnitAnalysis();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis#getContribution <em>Contribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Contribution</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis#getContribution()
	 * @see #getInstallableUnitAnalysis()
	 * @generated
	 */
	EReference getInstallableUnitAnalysis_Contribution();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis#getInstallableUnit <em>Installable Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Installable Unit</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis#getInstallableUnit()
	 * @see #getInstallableUnitAnalysis()
	 * @generated
	 */
	EReference getInstallableUnitAnalysis_InstallableUnit();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis#getRequirements <em>Requirements</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Requirements</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis#getRequirements()
	 * @see #getInstallableUnitAnalysis()
	 * @generated
	 */
	EReference getInstallableUnitAnalysis_Requirements();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis#getCapabilities <em>Capabilities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Capabilities</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis#getCapabilities()
	 * @see #getInstallableUnitAnalysis()
	 * @generated
	 */
	EReference getInstallableUnitAnalysis_Capabilities();

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis <em>Requirement Analysis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Requirement Analysis</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis
	 * @generated
	 */
	EClass getRequirementAnalysis();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis#getRequirement <em>Requirement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Requirement</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis#getRequirement()
	 * @see #getRequirementAnalysis()
	 * @generated
	 */
	EReference getRequirementAnalysis_Requirement();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis#getInstallableUnit <em>Installable Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Installable Unit</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis#getInstallableUnit()
	 * @see #getRequirementAnalysis()
	 * @generated
	 */
	EReference getRequirementAnalysis_InstallableUnit();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis#getResolutions <em>Resolutions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resolutions</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis#getResolutions()
	 * @see #getRequirementAnalysis()
	 * @generated
	 */
	EReference getRequirementAnalysis_Resolutions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution <em>Requirement Resolution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Requirement Resolution</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution
	 * @generated
	 */
	EClass getRequirementResolution();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution#getRequirement <em>Requirement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Requirement</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution#getRequirement()
	 * @see #getRequirementResolution()
	 * @generated
	 */
	EReference getRequirementResolution_Requirement();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution#getInstallableUnit <em>Installable Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Installable Unit</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution#getInstallableUnit()
	 * @see #getRequirementResolution()
	 * @generated
	 */
	EReference getRequirementResolution_InstallableUnit();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution#getCapability <em>Capability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Capability</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution#getCapability()
	 * @see #getRequirementResolution()
	 * @generated
	 */
	EReference getRequirementResolution_Capability();

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis <em>Capability Analysis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Capability Analysis</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis
	 * @generated
	 */
	EClass getCapabilityAnalysis();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis#getInstallableUnit <em>Installable Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Installable Unit</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis#getInstallableUnit()
	 * @see #getCapabilityAnalysis()
	 * @generated
	 */
	EReference getCapabilityAnalysis_InstallableUnit();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis#getCapability <em>Capability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Capability</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis#getCapability()
	 * @see #getCapabilityAnalysis()
	 * @generated
	 */
	EReference getCapabilityAnalysis_Capability();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis#getResolutions <em>Resolutions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resolutions</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis#getResolutions()
	 * @see #getCapabilityAnalysis()
	 * @generated
	 */
	EReference getCapabilityAnalysis_Resolutions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution <em>Capability Resolution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Capability Resolution</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution
	 * @generated
	 */
	EClass getCapabilityResolution();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution#getCapability <em>Capability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Capability</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution#getCapability()
	 * @see #getCapabilityResolution()
	 * @generated
	 */
	EReference getCapabilityResolution_Capability();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution#getRequirement <em>Requirement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Requirement</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution#getRequirement()
	 * @see #getCapabilityResolution()
	 * @generated
	 */
	EReference getCapabilityResolution_Requirement();

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project <em>Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Project</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project
	 * @generated
	 */
	EClass getProject();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getName()
	 * @see #getProject()
	 * @generated
	 */
	EAttribute getProject_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getSite <em>Site</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Site</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getSite()
	 * @see #getProject()
	 * @generated
	 */
	EAttribute getProject_Site();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getVersion()
	 * @see #getProject()
	 * @generated
	 */
	EAttribute getProject_Version();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getNews <em>News</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>News</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getNews()
	 * @see #getProject()
	 * @generated
	 */
	EAttribute getProject_News();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getReleaseDate <em>Release Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Release Date</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getReleaseDate()
	 * @see #getProject()
	 * @generated
	 */
	EAttribute getProject_ReleaseDate();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getRank <em>Rank</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rank</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getRank()
	 * @see #getProject()
	 * @generated
	 */
	EAttribute getProject_Rank();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Tags</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getTags()
	 * @see #getProject()
	 * @generated
	 */
	EAttribute getProject_Tags();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getContribution <em>Contribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Contribution</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getContribution()
	 * @see #getProject()
	 * @generated
	 */
	EReference getProject_Contribution();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getParent()
	 * @see #getProject()
	 * @generated
	 */
	EReference getProject_Parent();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getRepositories <em>Repositories</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Repositories</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getRepositories()
	 * @see #getProject()
	 * @generated
	 */
	EReference getProject_Repositories();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getSubprojects <em>Subprojects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Subprojects</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.Project#getSubprojects()
	 * @see #getProject()
	 * @generated
	 */
	EReference getProject_Subprojects();

	/**
	 * Returns the meta object for class '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository <em>Git Repository</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Git Repository</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository
	 * @generated
	 */
	EClass getGitRepository();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository#getURI <em>URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>URI</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository#getURI()
	 * @see #getGitRepository()
	 * @generated
	 */
	EAttribute getGitRepository_URI();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository#getLastModified <em>Last Modified</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last Modified</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository#getLastModified()
	 * @see #getGitRepository()
	 * @generated
	 */
	EAttribute getGitRepository_LastModified();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository#getProject <em>Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Project</em>'.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository#getProject()
	 * @see #getGitRepository()
	 * @generated
	 */
	EReference getGitRepository_Project();

	/**
	 * Returns the meta object for data type '{@link java.util.regex.Pattern <em>Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Pattern</em>'.
	 * @see java.util.regex.Pattern
	 * @model instanceClass="java.util.regex.Pattern"
	 * @generated
	 */
	EDataType getPattern();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>URI</em>'.
	 * @see org.eclipse.emf.common.util.URI
	 * @model instanceClass="org.eclipse.emf.common.util.URI"
	 * @generated
	 */
	EDataType getURI();

	/**
	 * Returns the meta object for data type '{@link java.util.Date <em>Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Date</em>'.
	 * @see java.util.Date
	 * @model instanceClass="java.util.Date"
	 * @generated
	 */
	EDataType getDate();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AnalyzerFactory getAnalyzerFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalysisImpl <em>Analysis</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalysisImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getAnalysis()
		 * @generated
		 */
		EClass ANALYSIS = eINSTANCE.getAnalysis();

		/**
		 * The meta object literal for the '<em><b>Release Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__RELEASE_DATE = eINSTANCE.getAnalysis_ReleaseDate();

		/**
		 * The meta object literal for the '<em><b>Exclusion</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__EXCLUSION = eINSTANCE.getAnalysis_Exclusion();

		/**
		 * The meta object literal for the '<em><b>Show Tags</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__SHOW_TAGS = eINSTANCE.getAnalysis_ShowTags();

		/**
		 * The meta object literal for the '<em><b>Ignore Major Version Duplicates</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__IGNORE_MAJOR_VERSION_DUPLICATES = eINSTANCE.getAnalysis_IgnoreMajorVersionDuplicates();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__TAGS = eINSTANCE.getAnalysis_Tags();

		/**
		 * The meta object literal for the '<em><b>Levels</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__LEVELS = eINSTANCE.getAnalysis_Levels();

		/**
		 * The meta object literal for the '<em><b>Layout Width</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__LAYOUT_WIDTH = eINSTANCE.getAnalysis_LayoutWidth();

		/**
		 * The meta object literal for the '<em><b>Layout Height</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__LAYOUT_HEIGHT = eINSTANCE.getAnalysis_LayoutHeight();

		/**
		 * The meta object literal for the '<em><b>Git Repository Filters</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANALYSIS__GIT_REPOSITORY_FILTERS = eINSTANCE.getAnalysis_GitRepositoryFilters();

		/**
		 * The meta object literal for the '<em><b>Aggregation</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANALYSIS__AGGREGATION = eINSTANCE.getAnalysis_Aggregation();

		/**
		 * The meta object literal for the '<em><b>Contributions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANALYSIS__CONTRIBUTIONS = eINSTANCE.getAnalysis_Contributions();

		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.ContributionAnalysisImpl <em>Contribution Analysis</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.ContributionAnalysisImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getContributionAnalysis()
		 * @generated
		 */
		EClass CONTRIBUTION_ANALYSIS = eINSTANCE.getContributionAnalysis();

		/**
		 * The meta object literal for the '<em><b>Enabled</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTION_ANALYSIS__ENABLED = eINSTANCE.getContributionAnalysis_Enabled();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTION_ANALYSIS__LABEL = eINSTANCE.getContributionAnalysis_Label();

		/**
		 * The meta object literal for the '<em><b>Dominant</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTION_ANALYSIS__DOMINANT = eINSTANCE.getContributionAnalysis_Dominant();

		/**
		 * The meta object literal for the '<em><b>Match</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTION_ANALYSIS__MATCH = eINSTANCE.getContributionAnalysis_Match();

		/**
		 * The meta object literal for the '<em><b>Last Modified</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTION_ANALYSIS__LAST_MODIFIED = eINSTANCE.getContributionAnalysis_LastModified();

		/**
		 * The meta object literal for the '<em><b>Rank</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTION_ANALYSIS__RANK = eINSTANCE.getContributionAnalysis_Rank();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTION_ANALYSIS__TAGS = eINSTANCE.getContributionAnalysis_Tags();

		/**
		 * The meta object literal for the '<em><b>Analysis</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTRIBUTION_ANALYSIS__ANALYSIS = eINSTANCE.getContributionAnalysis_Analysis();

		/**
		 * The meta object literal for the '<em><b>Contribution</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTRIBUTION_ANALYSIS__CONTRIBUTION = eINSTANCE.getContributionAnalysis_Contribution();

		/**
		 * The meta object literal for the '<em><b>Installable Units</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTRIBUTION_ANALYSIS__INSTALLABLE_UNITS = eINSTANCE.getContributionAnalysis_InstallableUnits();

		/**
		 * The meta object literal for the '<em><b>Projects</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTRIBUTION_ANALYSIS__PROJECTS = eINSTANCE.getContributionAnalysis_Projects();

		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.InstallableUnitAnalysisImpl <em>Installable Unit Analysis</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.InstallableUnitAnalysisImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getInstallableUnitAnalysis()
		 * @generated
		 */
		EClass INSTALLABLE_UNIT_ANALYSIS = eINSTANCE.getInstallableUnitAnalysis();

		/**
		 * The meta object literal for the '<em><b>Contribution</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INSTALLABLE_UNIT_ANALYSIS__CONTRIBUTION = eINSTANCE.getInstallableUnitAnalysis_Contribution();

		/**
		 * The meta object literal for the '<em><b>Installable Unit</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INSTALLABLE_UNIT_ANALYSIS__INSTALLABLE_UNIT = eINSTANCE.getInstallableUnitAnalysis_InstallableUnit();

		/**
		 * The meta object literal for the '<em><b>Requirements</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INSTALLABLE_UNIT_ANALYSIS__REQUIREMENTS = eINSTANCE.getInstallableUnitAnalysis_Requirements();

		/**
		 * The meta object literal for the '<em><b>Capabilities</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INSTALLABLE_UNIT_ANALYSIS__CAPABILITIES = eINSTANCE.getInstallableUnitAnalysis_Capabilities();

		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.RequirementAnalysisImpl <em>Requirement Analysis</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.RequirementAnalysisImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getRequirementAnalysis()
		 * @generated
		 */
		EClass REQUIREMENT_ANALYSIS = eINSTANCE.getRequirementAnalysis();

		/**
		 * The meta object literal for the '<em><b>Requirement</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUIREMENT_ANALYSIS__REQUIREMENT = eINSTANCE.getRequirementAnalysis_Requirement();

		/**
		 * The meta object literal for the '<em><b>Installable Unit</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUIREMENT_ANALYSIS__INSTALLABLE_UNIT = eINSTANCE.getRequirementAnalysis_InstallableUnit();

		/**
		 * The meta object literal for the '<em><b>Resolutions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUIREMENT_ANALYSIS__RESOLUTIONS = eINSTANCE.getRequirementAnalysis_Resolutions();

		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.RequirementResolutionImpl <em>Requirement Resolution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.RequirementResolutionImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getRequirementResolution()
		 * @generated
		 */
		EClass REQUIREMENT_RESOLUTION = eINSTANCE.getRequirementResolution();

		/**
		 * The meta object literal for the '<em><b>Requirement</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUIREMENT_RESOLUTION__REQUIREMENT = eINSTANCE.getRequirementResolution_Requirement();

		/**
		 * The meta object literal for the '<em><b>Installable Unit</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUIREMENT_RESOLUTION__INSTALLABLE_UNIT = eINSTANCE.getRequirementResolution_InstallableUnit();

		/**
		 * The meta object literal for the '<em><b>Capability</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUIREMENT_RESOLUTION__CAPABILITY = eINSTANCE.getRequirementResolution_Capability();

		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.CapabilityAnalysisImpl <em>Capability Analysis</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.CapabilityAnalysisImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getCapabilityAnalysis()
		 * @generated
		 */
		EClass CAPABILITY_ANALYSIS = eINSTANCE.getCapabilityAnalysis();

		/**
		 * The meta object literal for the '<em><b>Installable Unit</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY_ANALYSIS__INSTALLABLE_UNIT = eINSTANCE.getCapabilityAnalysis_InstallableUnit();

		/**
		 * The meta object literal for the '<em><b>Capability</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY_ANALYSIS__CAPABILITY = eINSTANCE.getCapabilityAnalysis_Capability();

		/**
		 * The meta object literal for the '<em><b>Resolutions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY_ANALYSIS__RESOLUTIONS = eINSTANCE.getCapabilityAnalysis_Resolutions();

		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.CapabilityResolutionImpl <em>Capability Resolution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.CapabilityResolutionImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getCapabilityResolution()
		 * @generated
		 */
		EClass CAPABILITY_RESOLUTION = eINSTANCE.getCapabilityResolution();

		/**
		 * The meta object literal for the '<em><b>Capability</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY_RESOLUTION__CAPABILITY = eINSTANCE.getCapabilityResolution_Capability();

		/**
		 * The meta object literal for the '<em><b>Requirement</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY_RESOLUTION__REQUIREMENT = eINSTANCE.getCapabilityResolution_Requirement();

		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.ProjectImpl <em>Project</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.ProjectImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getProject()
		 * @generated
		 */
		EClass PROJECT = eINSTANCE.getProject();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROJECT__NAME = eINSTANCE.getProject_Name();

		/**
		 * The meta object literal for the '<em><b>Site</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROJECT__SITE = eINSTANCE.getProject_Site();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROJECT__VERSION = eINSTANCE.getProject_Version();

		/**
		 * The meta object literal for the '<em><b>News</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROJECT__NEWS = eINSTANCE.getProject_News();

		/**
		 * The meta object literal for the '<em><b>Release Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROJECT__RELEASE_DATE = eINSTANCE.getProject_ReleaseDate();

		/**
		 * The meta object literal for the '<em><b>Rank</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROJECT__RANK = eINSTANCE.getProject_Rank();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROJECT__TAGS = eINSTANCE.getProject_Tags();

		/**
		 * The meta object literal for the '<em><b>Contribution</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROJECT__CONTRIBUTION = eINSTANCE.getProject_Contribution();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROJECT__PARENT = eINSTANCE.getProject_Parent();

		/**
		 * The meta object literal for the '<em><b>Repositories</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROJECT__REPOSITORIES = eINSTANCE.getProject_Repositories();

		/**
		 * The meta object literal for the '<em><b>Subprojects</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROJECT__SUBPROJECTS = eINSTANCE.getProject_Subprojects();

		/**
		 * The meta object literal for the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.impl.GitRepositoryImpl <em>Git Repository</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.GitRepositoryImpl
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getGitRepository()
		 * @generated
		 */
		EClass GIT_REPOSITORY = eINSTANCE.getGitRepository();

		/**
		 * The meta object literal for the '<em><b>URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GIT_REPOSITORY__URI = eINSTANCE.getGitRepository_URI();

		/**
		 * The meta object literal for the '<em><b>Last Modified</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GIT_REPOSITORY__LAST_MODIFIED = eINSTANCE.getGitRepository_LastModified();

		/**
		 * The meta object literal for the '<em><b>Project</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GIT_REPOSITORY__PROJECT = eINSTANCE.getGitRepository_Project();

		/**
		 * The meta object literal for the '<em>Pattern</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.regex.Pattern
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getPattern()
		 * @generated
		 */
		EDataType PATTERN = eINSTANCE.getPattern();

		/**
		 * The meta object literal for the '<em>URI</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.URI
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getURI()
		 * @generated
		 */
		EDataType URI = eINSTANCE.getURI();

		/**
		 * The meta object literal for the '<em>Date</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.Date
		 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.impl.AnalyzerPackageImpl#getDate()
		 * @generated
		 */
		EDataType DATE = eINSTANCE.getDate();

	}

} //AnalyzerPackage
