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
package org.eclipse.cbi.p2repo.aggregator.analyzer.impl;

import java.util.Date;
import java.util.regex.Pattern;

import org.eclipse.cbi.p2repo.aggregator.AggregatorPackage;
import org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerFactory;
import org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage;
import org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution;
import org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository;
import org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.Project;
import org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution;
import org.eclipse.cbi.p2repo.p2.P2Package;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AnalyzerPackageImpl extends EPackageImpl implements AnalyzerPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass analysisEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass contributionAnalysisEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass installableUnitAnalysisEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass requirementAnalysisEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass requirementResolutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass capabilityAnalysisEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass capabilityResolutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass projectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gitRepositoryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType patternEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType uriEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType dateEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private AnalyzerPackageImpl() {
		super(eNS_URI, AnalyzerFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link AnalyzerPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static AnalyzerPackage init() {
		if (isInited)
			return (AnalyzerPackage) EPackage.Registry.INSTANCE.getEPackage(AnalyzerPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredAnalyzerPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		AnalyzerPackageImpl theAnalyzerPackage = registeredAnalyzerPackage instanceof AnalyzerPackageImpl
				? (AnalyzerPackageImpl) registeredAnalyzerPackage
				: new AnalyzerPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		AggregatorPackage.eINSTANCE.eClass();
		P2Package.eINSTANCE.eClass();
		XMLTypePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theAnalyzerPackage.createPackageContents();

		// Initialize created meta-data
		theAnalyzerPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theAnalyzerPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(AnalyzerPackage.eNS_URI, theAnalyzerPackage);
		return theAnalyzerPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAnalysis() {
		return analysisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_ReleaseDate() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_Exclusion() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_ShowTags() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_IgnoreMajorVersionDuplicates() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_Tags() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_Levels() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_LayoutWidth() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_LayoutHeight() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnalysis_GitRepositoryFilters() {
		return (EAttribute) analysisEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAnalysis_Aggregation() {
		return (EReference) analysisEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAnalysis_Contributions() {
		return (EReference) analysisEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getContributionAnalysis() {
		return contributionAnalysisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContributionAnalysis_Enabled() {
		return (EAttribute) contributionAnalysisEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContributionAnalysis_Label() {
		return (EAttribute) contributionAnalysisEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContributionAnalysis_Dominant() {
		return (EAttribute) contributionAnalysisEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContributionAnalysis_Match() {
		return (EAttribute) contributionAnalysisEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContributionAnalysis_LastModified() {
		return (EAttribute) contributionAnalysisEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContributionAnalysis_Rank() {
		return (EAttribute) contributionAnalysisEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContributionAnalysis_Tags() {
		return (EAttribute) contributionAnalysisEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getContributionAnalysis_Analysis() {
		return (EReference) contributionAnalysisEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getContributionAnalysis_Contribution() {
		return (EReference) contributionAnalysisEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getContributionAnalysis_InstallableUnits() {
		return (EReference) contributionAnalysisEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getContributionAnalysis_Projects() {
		return (EReference) contributionAnalysisEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInstallableUnitAnalysis() {
		return installableUnitAnalysisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInstallableUnitAnalysis_Contribution() {
		return (EReference) installableUnitAnalysisEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInstallableUnitAnalysis_InstallableUnit() {
		return (EReference) installableUnitAnalysisEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInstallableUnitAnalysis_Requirements() {
		return (EReference) installableUnitAnalysisEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInstallableUnitAnalysis_Capabilities() {
		return (EReference) installableUnitAnalysisEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRequirementAnalysis() {
		return requirementAnalysisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequirementAnalysis_Requirement() {
		return (EReference) requirementAnalysisEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequirementAnalysis_InstallableUnit() {
		return (EReference) requirementAnalysisEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequirementAnalysis_Resolutions() {
		return (EReference) requirementAnalysisEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRequirementResolution() {
		return requirementResolutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequirementResolution_Requirement() {
		return (EReference) requirementResolutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequirementResolution_InstallableUnit() {
		return (EReference) requirementResolutionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequirementResolution_Capability() {
		return (EReference) requirementResolutionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCapabilityAnalysis() {
		return capabilityAnalysisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCapabilityAnalysis_InstallableUnit() {
		return (EReference) capabilityAnalysisEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCapabilityAnalysis_Capability() {
		return (EReference) capabilityAnalysisEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCapabilityAnalysis_Resolutions() {
		return (EReference) capabilityAnalysisEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCapabilityResolution() {
		return capabilityResolutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCapabilityResolution_Capability() {
		return (EReference) capabilityResolutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCapabilityResolution_Requirement() {
		return (EReference) capabilityResolutionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProject() {
		return projectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProject_Name() {
		return (EAttribute) projectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProject_Site() {
		return (EAttribute) projectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProject_Version() {
		return (EAttribute) projectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProject_News() {
		return (EAttribute) projectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProject_ReleaseDate() {
		return (EAttribute) projectEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProject_Rank() {
		return (EAttribute) projectEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProject_Tags() {
		return (EAttribute) projectEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProject_Contribution() {
		return (EReference) projectEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProject_Parent() {
		return (EReference) projectEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProject_Repositories() {
		return (EReference) projectEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProject_Subprojects() {
		return (EReference) projectEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGitRepository() {
		return gitRepositoryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGitRepository_URI() {
		return (EAttribute) gitRepositoryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGitRepository_LastModified() {
		return (EAttribute) gitRepositoryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGitRepository_Project() {
		return (EReference) gitRepositoryEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPattern() {
		return patternEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getURI() {
		return uriEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getDate() {
		return dateEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AnalyzerFactory getAnalyzerFactory() {
		return (AnalyzerFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		analysisEClass = createEClass(ANALYSIS);
		createEAttribute(analysisEClass, ANALYSIS__RELEASE_DATE);
		createEAttribute(analysisEClass, ANALYSIS__EXCLUSION);
		createEAttribute(analysisEClass, ANALYSIS__SHOW_TAGS);
		createEAttribute(analysisEClass, ANALYSIS__IGNORE_MAJOR_VERSION_DUPLICATES);
		createEAttribute(analysisEClass, ANALYSIS__TAGS);
		createEAttribute(analysisEClass, ANALYSIS__LEVELS);
		createEAttribute(analysisEClass, ANALYSIS__LAYOUT_WIDTH);
		createEAttribute(analysisEClass, ANALYSIS__LAYOUT_HEIGHT);
		createEAttribute(analysisEClass, ANALYSIS__GIT_REPOSITORY_FILTERS);
		createEReference(analysisEClass, ANALYSIS__AGGREGATION);
		createEReference(analysisEClass, ANALYSIS__CONTRIBUTIONS);

		contributionAnalysisEClass = createEClass(CONTRIBUTION_ANALYSIS);
		createEAttribute(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__ENABLED);
		createEAttribute(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__LABEL);
		createEAttribute(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__DOMINANT);
		createEAttribute(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__MATCH);
		createEAttribute(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__LAST_MODIFIED);
		createEAttribute(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__RANK);
		createEAttribute(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__TAGS);
		createEReference(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__ANALYSIS);
		createEReference(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__CONTRIBUTION);
		createEReference(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__INSTALLABLE_UNITS);
		createEReference(contributionAnalysisEClass, CONTRIBUTION_ANALYSIS__PROJECTS);

		installableUnitAnalysisEClass = createEClass(INSTALLABLE_UNIT_ANALYSIS);
		createEReference(installableUnitAnalysisEClass, INSTALLABLE_UNIT_ANALYSIS__CONTRIBUTION);
		createEReference(installableUnitAnalysisEClass, INSTALLABLE_UNIT_ANALYSIS__INSTALLABLE_UNIT);
		createEReference(installableUnitAnalysisEClass, INSTALLABLE_UNIT_ANALYSIS__REQUIREMENTS);
		createEReference(installableUnitAnalysisEClass, INSTALLABLE_UNIT_ANALYSIS__CAPABILITIES);

		requirementAnalysisEClass = createEClass(REQUIREMENT_ANALYSIS);
		createEReference(requirementAnalysisEClass, REQUIREMENT_ANALYSIS__INSTALLABLE_UNIT);
		createEReference(requirementAnalysisEClass, REQUIREMENT_ANALYSIS__REQUIREMENT);
		createEReference(requirementAnalysisEClass, REQUIREMENT_ANALYSIS__RESOLUTIONS);

		requirementResolutionEClass = createEClass(REQUIREMENT_RESOLUTION);
		createEReference(requirementResolutionEClass, REQUIREMENT_RESOLUTION__REQUIREMENT);
		createEReference(requirementResolutionEClass, REQUIREMENT_RESOLUTION__INSTALLABLE_UNIT);
		createEReference(requirementResolutionEClass, REQUIREMENT_RESOLUTION__CAPABILITY);

		capabilityAnalysisEClass = createEClass(CAPABILITY_ANALYSIS);
		createEReference(capabilityAnalysisEClass, CAPABILITY_ANALYSIS__INSTALLABLE_UNIT);
		createEReference(capabilityAnalysisEClass, CAPABILITY_ANALYSIS__CAPABILITY);
		createEReference(capabilityAnalysisEClass, CAPABILITY_ANALYSIS__RESOLUTIONS);

		capabilityResolutionEClass = createEClass(CAPABILITY_RESOLUTION);
		createEReference(capabilityResolutionEClass, CAPABILITY_RESOLUTION__CAPABILITY);
		createEReference(capabilityResolutionEClass, CAPABILITY_RESOLUTION__REQUIREMENT);

		projectEClass = createEClass(PROJECT);
		createEAttribute(projectEClass, PROJECT__NAME);
		createEAttribute(projectEClass, PROJECT__SITE);
		createEAttribute(projectEClass, PROJECT__VERSION);
		createEAttribute(projectEClass, PROJECT__NEWS);
		createEAttribute(projectEClass, PROJECT__RELEASE_DATE);
		createEAttribute(projectEClass, PROJECT__RANK);
		createEAttribute(projectEClass, PROJECT__TAGS);
		createEReference(projectEClass, PROJECT__CONTRIBUTION);
		createEReference(projectEClass, PROJECT__PARENT);
		createEReference(projectEClass, PROJECT__REPOSITORIES);
		createEReference(projectEClass, PROJECT__SUBPROJECTS);

		gitRepositoryEClass = createEClass(GIT_REPOSITORY);
		createEAttribute(gitRepositoryEClass, GIT_REPOSITORY__URI);
		createEAttribute(gitRepositoryEClass, GIT_REPOSITORY__LAST_MODIFIED);
		createEReference(gitRepositoryEClass, GIT_REPOSITORY__PROJECT);

		// Create data types
		patternEDataType = createEDataType(PATTERN);
		uriEDataType = createEDataType(URI);
		dateEDataType = createEDataType(DATE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage) EPackage.Registry.INSTANCE
				.getEPackage(XMLTypePackage.eNS_URI);
		AggregatorPackage theAggregatorPackage = (AggregatorPackage) EPackage.Registry.INSTANCE
				.getEPackage(AggregatorPackage.eNS_URI);
		P2Package theP2Package = (P2Package) EPackage.Registry.INSTANCE.getEPackage(P2Package.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(analysisEClass, Analysis.class, "Analysis", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAnalysis_ReleaseDate(), this.getDate(), "releaseDate", null, 0, 1, Analysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnalysis_Exclusion(), this.getPattern(), "exclusion", null, 0, 1, Analysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnalysis_ShowTags(), theXMLTypePackage.getBoolean(), "showTags", null, 0, 1, Analysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnalysis_IgnoreMajorVersionDuplicates(), theXMLTypePackage.getBoolean(),
				"ignoreMajorVersionDuplicates", null, 0, 1, Analysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnalysis_Tags(), ecorePackage.getEString(), "tags", null, 0, -1, Analysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnalysis_Levels(), ecorePackage.getEInt(), "levels", null, 0, -1, Analysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnalysis_LayoutWidth(), ecorePackage.getEInt(), "layoutWidth", null, 0, 1, Analysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnalysis_LayoutHeight(), ecorePackage.getEInt(), "layoutHeight", null, 0, 1, Analysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnalysis_GitRepositoryFilters(), ecorePackage.getEString(), "gitRepositoryFilters", null, 0,
				-1, Analysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getAnalysis_Aggregation(), theAggregatorPackage.getAggregation(), null, "aggregation", null, 1,
				1, Analysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAnalysis_Contributions(), this.getContributionAnalysis(),
				this.getContributionAnalysis_Analysis(), "contributions", null, 0, -1, Analysis.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);

		addEOperation(analysisEClass, null, "reconcile", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(contributionAnalysisEClass, ContributionAnalysis.class, "ContributionAnalysis", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getContributionAnalysis_Enabled(), theXMLTypePackage.getBoolean(), "enabled", "true", 0, 1,
				ContributionAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContributionAnalysis_Label(), ecorePackage.getEString(), "label", null, 0, 1,
				ContributionAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContributionAnalysis_Dominant(), theXMLTypePackage.getBoolean(), "dominant", null, 0, 1,
				ContributionAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContributionAnalysis_Match(), this.getPattern(), "match", null, 0, 1,
				ContributionAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContributionAnalysis_LastModified(), ecorePackage.getELong(), "lastModified",
				"-9223372036854775808", 0, 1, ContributionAnalysis.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContributionAnalysis_Rank(), ecorePackage.getEInt(), "rank", null, 0, 1,
				ContributionAnalysis.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getContributionAnalysis_Tags(), ecorePackage.getEString(), "tags", null, 0, -1,
				ContributionAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getContributionAnalysis_Analysis(), this.getAnalysis(), this.getAnalysis_Contributions(),
				"analysis", null, 0, 1, ContributionAnalysis.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE,
				!IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getContributionAnalysis_Contribution(), theAggregatorPackage.getContribution(), null,
				"contribution", null, 1, 1, ContributionAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getContributionAnalysis_InstallableUnits(), this.getInstallableUnitAnalysis(),
				this.getInstallableUnitAnalysis_Contribution(), "installableUnits", null, 0, -1,
				ContributionAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getContributionAnalysis_Projects(), this.getProject(), null, "projects", null, 0, -1,
				ContributionAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(contributionAnalysisEClass, this.getProject(), "getAllProjects", 0, -1, IS_UNIQUE, IS_ORDERED);

		initEClass(installableUnitAnalysisEClass, InstallableUnitAnalysis.class, "InstallableUnitAnalysis",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInstallableUnitAnalysis_Contribution(), this.getContributionAnalysis(),
				this.getContributionAnalysis_InstallableUnits(), "contribution", null, 0, 1,
				InstallableUnitAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInstallableUnitAnalysis_InstallableUnit(), theP2Package.getIInstallableUnit(), null,
				"installableUnit", null, 1, 1, InstallableUnitAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInstallableUnitAnalysis_Requirements(), this.getRequirementAnalysis(),
				this.getRequirementAnalysis_InstallableUnit(), "requirements", null, 0, -1,
				InstallableUnitAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInstallableUnitAnalysis_Capabilities(), this.getCapabilityAnalysis(),
				this.getCapabilityAnalysis_InstallableUnit(), "capabilities", null, 0, -1,
				InstallableUnitAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(requirementAnalysisEClass, RequirementAnalysis.class, "RequirementAnalysis", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRequirementAnalysis_InstallableUnit(), this.getInstallableUnitAnalysis(),
				this.getInstallableUnitAnalysis_Requirements(), "installableUnit", null, 0, 1,
				RequirementAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRequirementAnalysis_Requirement(), theP2Package.getIRequirement(), null, "requirement", null,
				1, 1, RequirementAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRequirementAnalysis_Resolutions(), this.getRequirementResolution(),
				this.getRequirementResolution_Requirement(), "resolutions", null, 0, -1, RequirementAnalysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(requirementResolutionEClass, RequirementResolution.class, "RequirementResolution", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRequirementResolution_Requirement(), this.getRequirementAnalysis(),
				this.getRequirementAnalysis_Resolutions(), "requirement", null, 0, 1, RequirementResolution.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRequirementResolution_InstallableUnit(), this.getInstallableUnitAnalysis(), null,
				"installableUnit", null, 1, 1, RequirementResolution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRequirementResolution_Capability(), this.getCapabilityAnalysis(), null, "capability", null, 0,
				1, RequirementResolution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(capabilityAnalysisEClass, CapabilityAnalysis.class, "CapabilityAnalysis", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCapabilityAnalysis_InstallableUnit(), this.getInstallableUnitAnalysis(),
				this.getInstallableUnitAnalysis_Capabilities(), "installableUnit", null, 0, 1, CapabilityAnalysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapabilityAnalysis_Capability(), theP2Package.getIProvidedCapability(), null, "capability",
				null, 1, 1, CapabilityAnalysis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapabilityAnalysis_Resolutions(), this.getCapabilityResolution(),
				this.getCapabilityResolution_Capability(), "resolutions", null, 0, -1, CapabilityAnalysis.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(capabilityResolutionEClass, CapabilityResolution.class, "CapabilityResolution", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCapabilityResolution_Capability(), this.getCapabilityAnalysis(),
				this.getCapabilityAnalysis_Resolutions(), "capability", null, 0, 1, CapabilityResolution.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapabilityResolution_Requirement(), this.getRequirementAnalysis(), null, "requirement", null,
				1, 1, CapabilityResolution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(projectEClass, Project.class, "Project", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProject_Name(), ecorePackage.getEString(), "name", null, 0, 1, Project.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProject_Site(), this.getURI(), "site", null, 0, 1, Project.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProject_Version(), this.getURI(), "version", null, 0, 1, Project.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProject_News(), this.getURI(), "news", null, 0, 1, Project.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProject_ReleaseDate(), this.getDate(), "releaseDate", null, 0, 1, Project.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProject_Rank(), ecorePackage.getEInt(), "rank", null, 0, 1, Project.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProject_Tags(), ecorePackage.getEString(), "tags", null, 0, -1, Project.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProject_Contribution(), this.getContributionAnalysis(), null, "contribution", null, 0, 1,
				Project.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getProject_Parent(), this.getProject(), this.getProject_Subprojects(), "parent", null, 0, 1,
				Project.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProject_Repositories(), this.getGitRepository(), this.getGitRepository_Project(),
				"repositories", null, 0, -1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProject_Subprojects(), this.getProject(), this.getProject_Parent(), "subprojects", null, 0,
				-1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(projectEClass, this.getProject(), "getAllProjects", 0, -1, IS_UNIQUE, IS_ORDERED);

		addEOperation(projectEClass, this.getAnalysis(), "getAnalysis", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(gitRepositoryEClass, GitRepository.class, "GitRepository", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGitRepository_URI(), this.getURI(), "uRI", null, 0, 1, GitRepository.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGitRepository_LastModified(), ecorePackage.getELong(), "lastModified", "-9223372036854775808",
				0, 1, GitRepository.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getGitRepository_Project(), this.getProject(), this.getProject_Repositories(), "project", null,
				0, 1, GitRepository.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize data types
		initEDataType(patternEDataType, Pattern.class, "Pattern", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class, "URI", IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(dateEDataType, Date.class, "Date", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
		addAnnotation(getAnalysis_Tags(), source, new String[] { "kind", "element", "name", "tag" });
		addAnnotation(getAnalysis_GitRepositoryFilters(), source,
				new String[] { "kind", "element", "name", "gitRepositoryFilter" });
		addAnnotation(getAnalysis_Contributions(), source, new String[] { "name", "contribution", "kind", "element" });
		addAnnotation(getContributionAnalysis_Tags(), source, new String[] { "kind", "element", "name", "tag" });
		addAnnotation(getContributionAnalysis_InstallableUnits(), source,
				new String[] { "kind", "element", "name", "installableUnit" });
		addAnnotation(getContributionAnalysis_Projects(), source,
				new String[] { "kind", "element", "name", "project" });
		addAnnotation(getInstallableUnitAnalysis_Requirements(), source,
				new String[] { "kind", "element", "name", "requirement" });
		addAnnotation(getInstallableUnitAnalysis_Capabilities(), source,
				new String[] { "kind", "element", "name", "capability" });
		addAnnotation(getRequirementAnalysis_Resolutions(), source,
				new String[] { "kind", "element", "name", "resolution" });
		addAnnotation(getProject_Tags(), source, new String[] { "kind", "element", "name", "tag" });
		addAnnotation(getProject_Repositories(), source, new String[] { "kind", "element", "name", "repository" });
		addAnnotation(getProject_Subprojects(), source, new String[] { "kind", "element", "name", "subproject" });
		addAnnotation(getGitRepository_URI(), source, new String[] { "name", "uri" });
	}

} //AnalyzerPackageImpl
