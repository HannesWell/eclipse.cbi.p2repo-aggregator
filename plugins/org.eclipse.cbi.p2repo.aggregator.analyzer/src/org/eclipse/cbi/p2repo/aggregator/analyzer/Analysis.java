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

import java.util.Date;
import java.util.regex.Pattern;

import org.eclipse.cbi.p2repo.aggregator.Aggregation;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Analysis</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The root analysis instance that references the aggregaton being analyzed.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getReleaseDate <em>Release Date</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getExclusion <em>Exclusion</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#isShowTags <em>Show Tags</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#isIgnoreMajorVersionDuplicates <em>Ignore Major Version Duplicates</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getTags <em>Tags</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLevels <em>Levels</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLayoutWidth <em>Layout Width</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLayoutHeight <em>Layout Height</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getGitRepositoryFilters <em>Git Repository Filters</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getAggregation <em>Aggregation</em>}</li>
 *   <li>{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getContributions <em>Contributions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis()
 * @model
 * @generated
 */
public interface Analysis extends EObject {
	/**
	 * Returns the value of the '<em><b>Release Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The date of the next scheduled release
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Release Date</em>' attribute.
	 * @see #setReleaseDate(Date)
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_ReleaseDate()
	 * @model dataType="org.eclipse.cbi.p2repo.aggregator.analyzer.Date"
	 * @generated
	 */
	Date getReleaseDate();

	/**
	 * Sets the value of the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getReleaseDate <em>Release Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Release Date</em>' attribute.
	 * @see #getReleaseDate()
	 * @generated
	 */
	void setReleaseDate(Date value);

	/**
	 * Returns the value of the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A pattern that matches all installable units that will be omitted from analysis
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Exclusion</em>' attribute.
	 * @see #setExclusion(Pattern)
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_Exclusion()
	 * @model dataType="org.eclipse.cbi.p2repo.aggregator.analyzer.Pattern"
	 * @generated
	 */
	Pattern getExclusion();

	/**
	 * Sets the value of the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getExclusion <em>Exclusion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusion</em>' attribute.
	 * @see #getExclusion()
	 * @generated
	 */
	void setExclusion(Pattern value);

	/**
	 * Returns the value of the '<em><b>Show Tags</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to show the tags as children in the editor.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Show Tags</em>' attribute.
	 * @see #setShowTags(boolean)
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_ShowTags()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 * @generated
	 */
	boolean isShowTags();

	/**
	 * Sets the value of the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#isShowTags <em>Show Tags</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Show Tags</em>' attribute.
	 * @see #isShowTags()
	 * @generated
	 */
	void setShowTags(boolean value);

	/**
	 * Returns the value of the '<em><b>Ignore Major Version Duplicates</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to ignore duplicate IUs that differ by their major version.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ignore Major Version Duplicates</em>' attribute.
	 * @see #setIgnoreMajorVersionDuplicates(boolean)
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_IgnoreMajorVersionDuplicates()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 * @generated
	 */
	boolean isIgnoreMajorVersionDuplicates();

	/**
	 * Sets the value of the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#isIgnoreMajorVersionDuplicates <em>Ignore Major Version Duplicates</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ignore Major Version Duplicates</em>' attribute.
	 * @see #isIgnoreMajorVersionDuplicates()
	 * @generated
	 */
	void setIgnoreMajorVersionDuplicates(boolean value);

	/**
	 * Returns the value of the '<em><b>Tags</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Tags that may be used with contribution analysis items
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Tags</em>' attribute list.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_Tags()
	 * @model extendedMetaData="kind='element' name='tag'"
	 * @generated
	 */
	EList<String> getTags();

	/**
	 * Returns the value of the '<em><b>Levels</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Levels</em>' attribute list.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_Levels()
	 * @model
	 * @generated
	 */
	EList<Integer> getLevels();

	/**
	 * Returns the value of the '<em><b>Layout Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Layout Width</em>' attribute.
	 * @see #setLayoutWidth(int)
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_LayoutWidth()
	 * @model
	 * @generated
	 */
	int getLayoutWidth();

	/**
	 * Sets the value of the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLayoutWidth <em>Layout Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Layout Width</em>' attribute.
	 * @see #getLayoutWidth()
	 * @generated
	 */
	void setLayoutWidth(int value);

	/**
	 * Returns the value of the '<em><b>Layout Height</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Layout Height</em>' attribute.
	 * @see #setLayoutHeight(int)
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_LayoutHeight()
	 * @model
	 * @generated
	 */
	int getLayoutHeight();

	/**
	 * Sets the value of the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getLayoutHeight <em>Layout Height</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Layout Height</em>' attribute.
	 * @see #getLayoutHeight()
	 * @generated
	 */
	void setLayoutHeight(int value);

	/**
	 * Returns the value of the '<em><b>Git Repository Filters</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Git Repository Filters</em>' attribute list.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_GitRepositoryFilters()
	 * @model extendedMetaData="kind='element' name='gitRepositoryFilter'"
	 * @generated
	 */
	EList<String> getGitRepositoryFilters();

	/**
	 * Returns the value of the '<em><b>Aggregation</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The aggregation being analyzed.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Aggregation</em>' reference.
	 * @see #setAggregation(Aggregation)
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_Aggregation()
	 * @model required="true"
	 * @generated
	 */
	Aggregation getAggregation();

	/**
	 * Sets the value of the '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis#getAggregation <em>Aggregation</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Aggregation</em>' reference.
	 * @see #getAggregation()
	 * @generated
	 */
	void setAggregation(Aggregation value);

	/**
	 * Returns the value of the '<em><b>Contributions</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getAnalysis <em>Analysis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The analyzed contributions.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Contributions</em>' containment reference list.
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage#getAnalysis_Contributions()
	 * @see org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis#getAnalysis
	 * @model opposite="analysis" containment="true"
	 *        extendedMetaData="name='contribution' kind='element'"
	 * @generated
	 */
	EList<ContributionAnalysis> getContributions();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void reconcile();

} // Analysis
