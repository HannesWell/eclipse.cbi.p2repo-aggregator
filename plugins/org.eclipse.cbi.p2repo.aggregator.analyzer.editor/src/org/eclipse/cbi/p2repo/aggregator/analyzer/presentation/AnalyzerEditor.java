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
package org.eclipse.cbi.p2repo.aggregator.analyzer.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.batik.svggen.DefaultExtensionHandler;
import org.apache.batik.svggen.SVGGraphics2D;
import org.eclipse.cbi.p2repo.aggregator.Contribution;
import org.eclipse.cbi.p2repo.aggregator.InstallableUnitType;
import org.eclipse.cbi.p2repo.aggregator.analyzer.Analysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.AnalyzerPackage;
import org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.CapabilityResolution;
import org.eclipse.cbi.p2repo.aggregator.analyzer.ContributionAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.GitRepository;
import org.eclipse.cbi.p2repo.aggregator.analyzer.InstallableUnitAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.Project;
import org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementAnalysis;
import org.eclipse.cbi.p2repo.aggregator.analyzer.RequirementResolution;
import org.eclipse.cbi.p2repo.aggregator.analyzer.presentation.handlers.AnalyzeHandler;
import org.eclipse.cbi.p2repo.aggregator.analyzer.presentation.handlers.AnalyzeHandler.Analyzer;
import org.eclipse.cbi.p2repo.aggregator.analyzer.presentation.handlers.BaseHandler;
import org.eclipse.cbi.p2repo.aggregator.analyzer.presentation.handlers.LastModifiedHandler;
import org.eclipse.cbi.p2repo.aggregator.analyzer.presentation.handlers.LastModifiedHandler.LastModifiedUpdater;
import org.eclipse.cbi.p2repo.aggregator.analyzer.provider.AnalysisItemProvider;
import org.eclipse.cbi.p2repo.aggregator.analyzer.provider.AnalyzerItemProviderAdapterFactory;
import org.eclipse.cbi.p2repo.aggregator.analyzer.provider.ContributionAnalysisItemProvider;
import org.eclipse.cbi.p2repo.aggregator.analyzer.util.AnalyzerUtil;
import org.eclipse.cbi.p2repo.aggregator.p2view.IUPresentation;
import org.eclipse.cbi.p2repo.aggregator.p2view.MetadataRepositoryStructuredView;
import org.eclipse.cbi.p2repo.aggregator.p2view.P2viewPackage;
import org.eclipse.cbi.p2repo.aggregator.p2view.ProvidedCapabilityWrapper;
import org.eclipse.cbi.p2repo.aggregator.p2view.RequirementWrapper;
import org.eclipse.cbi.p2repo.aggregator.p2view.provider.P2viewItemProviderAdapterFactory;
import org.eclipse.cbi.p2repo.aggregator.provider.AggregatorItemProviderAdapterFactory;
import org.eclipse.cbi.p2repo.aggregator.util.InstallableUnitUtils;
import org.eclipse.cbi.p2repo.p2.MetadataRepository;
import org.eclipse.cbi.p2repo.p2.maven.pom.Activation;
import org.eclipse.cbi.p2repo.p2.maven.pom.ActivationOS;
import org.eclipse.cbi.p2repo.p2.maven.pom.DependenciesType;
import org.eclipse.cbi.p2repo.p2.maven.pom.Dependency;
import org.eclipse.cbi.p2repo.p2.maven.pom.LicensesType;
import org.eclipse.cbi.p2repo.p2.maven.pom.Model;
import org.eclipse.cbi.p2repo.p2.maven.pom.Profile;
import org.eclipse.cbi.p2repo.p2.maven.pom.ProfilesType;
import org.eclipse.cbi.p2repo.p2.maven.pom.util.PomResourceFactoryImpl;
import org.eclipse.cbi.p2repo.p2.provider.MetadataRepositoryItemProvider;
import org.eclipse.cbi.p2repo.p2.provider.P2ItemProviderAdapterFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.ui.MarkerHelper;
import org.eclipse.emf.common.ui.editor.ProblemEditorPart;
import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.SegmentSequence;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.DelegatingWrapperItemProvider;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProvider;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.tree.TreeFactory;
import org.eclipse.emf.edit.tree.TreeNode;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider.StyledLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;
import org.eclipse.emf.edit.ui.provider.DelegatingStyledCellLabelProvider;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;
import org.eclipse.emf.edit.ui.provider.ExtendedColorRegistry;
import org.eclipse.emf.edit.ui.provider.ExtendedFontRegistry;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.edit.ui.provider.UnwrappingSelectionProvider;
import org.eclipse.emf.edit.ui.util.EditUIMarkerHelper;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.emf.edit.ui.util.FindAndReplaceTarget;
import org.eclipse.emf.edit.ui.util.IRevertablePart;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.interfaces.EntityLayout;

/**
 * This is an example of a Analyzer model editor.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class AnalyzerEditor extends MultiPageEditorPart implements IEditingDomainProvider, ISelectionProvider,
		IMenuListener, IViewerProvider, IGotoMarker, IRevertablePart {

	private static final boolean GMF_AVAIBLE = isGMFAvailable();

	/**
	 * This keeps track of the editing domain that is used to track all changes to the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AdapterFactoryEditingDomain editingDomain;

	/**
	 * This is the one adapter factory used for providing views of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory adapterFactory;

	/**
	 * This is the content outline page.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IContentOutlinePage contentOutlinePage;

	/**
	 * This is a kludge...
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IStatusLineManager contentOutlineStatusLineManager;

	/**
	 * This is the content outline page's viewer.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TreeViewer contentOutlineViewer;

	/**
	 * This is the property sheet page.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected List<PropertySheetPage> propertySheetPages = new ArrayList<>();

	/**
	 * This is the viewer that shadows the selection in the content outline.
	 * The parent relation must be correctly defined for this to work.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TreeViewer selectionViewer;

	/**
	 * This keeps track of the active content viewer, which may be either one of the viewers in the pages or the content outline viewer.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Viewer currentViewer;

	/**
	 * This listens to which ever viewer is active.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ISelectionChangedListener selectionChangedListener;

	/**
	 * This keeps track of all the {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are listening to this editor.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<ISelectionChangedListener> selectionChangedListeners = new ArrayList<>();

	/**
	 * This keeps track of the selection of the editor as a whole.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ISelection editorSelection = StructuredSelection.EMPTY;

	/**
	 * The MarkerHelper is responsible for creating workspace resource markers presented
	 * in Eclipse's Problems View.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MarkerHelper markerHelper = new EditUIMarkerHelper();

	/**
	 * This listens for when the outline becomes active
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IPartListener partListener = new IPartListener() {
		@Override
		public void partActivated(IWorkbenchPart p) {
			if (p instanceof ContentOutline) {
				if (((ContentOutline) p).getCurrentPage() == contentOutlinePage) {
					getActionBarContributor().setActiveEditor(AnalyzerEditor.this);

					setCurrentViewer(contentOutlineViewer);
				}
			} else if (p instanceof PropertySheet) {
				if (propertySheetPages.contains(((PropertySheet) p).getCurrentPage())) {
					getActionBarContributor().setActiveEditor(AnalyzerEditor.this);
					handleActivate();
				}
			} else if (p == AnalyzerEditor.this) {
				handleActivate();
			}
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart p) {
			// Ignore.
		}

		@Override
		public void partClosed(IWorkbenchPart p) {
			// Ignore.
		}

		@Override
		public void partDeactivated(IWorkbenchPart p) {
			// Ignore.
		}

		@Override
		public void partOpened(IWorkbenchPart p) {
			// Ignore.
		}
	};

	/**
	 * Resources that have been removed since last activation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Resource> removedResources = new ArrayList<>();

	/**
	 * Resources that have been changed since last activation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Resource> changedResources = new ArrayList<>();

	/**
	 * Resources that have been saved.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Resource> savedResources = new ArrayList<>();

	/**
	 * Map to store the diagnostic associated with a resource.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Map<Resource, Diagnostic> resourceToDiagnosticMap = new LinkedHashMap<>();

	/**
	 * Controls whether the problem indication should be updated.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected boolean updateProblemIndication = true;

	/**
	 * Adapter used to update the problem indication when resources are demanded loaded.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EContentAdapter problemIndicationAdapter = new EContentAdapter() {
		protected boolean dispatching;

		@Override
		public void notifyChanged(Notification notification) {
			if (notification.getNotifier() instanceof Resource) {
				switch (notification.getFeatureID(Resource.class)) {
					case Resource.RESOURCE__IS_LOADED:
					case Resource.RESOURCE__ERRORS:
					case Resource.RESOURCE__WARNINGS: {
						Resource resource = (Resource) notification.getNotifier();
						Diagnostic diagnostic = analyzeResourceProblems(resource, null);
						if (diagnostic.getSeverity() != Diagnostic.OK) {
							resourceToDiagnosticMap.put(resource, diagnostic);
						} else {
							resourceToDiagnosticMap.remove(resource);
						}
						dispatchUpdateProblemIndication();
						break;
					}
					default:
						break;
				}
			} else {
				super.notifyChanged(notification);
			}
		}

		protected void dispatchUpdateProblemIndication() {
			if (updateProblemIndication && !dispatching) {
				dispatching = true;
				getSite().getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						dispatching = false;
						updateProblemIndication();
					}
				});
			}
		}

		@Override
		protected void setTarget(Resource target) {
			basicSetTarget(target);
		}

		@Override
		protected void unsetTarget(Resource target) {
			basicUnsetTarget(target);
			resourceToDiagnosticMap.remove(target);
			dispatchUpdateProblemIndication();
		}
	};

	/**
	 * This listens for workspace changes.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			try {
				class ResourceDeltaVisitor implements IResourceDeltaVisitor {
					protected ResourceSet resourceSet = editingDomain.getResourceSet();
					protected Collection<Resource> changedResources = new ArrayList<>();
					protected Collection<Resource> removedResources = new ArrayList<>();

					@Override
					public boolean visit(final IResourceDelta delta) {
						if (delta.getResource().getType() == IResource.FILE) {
							if (delta.getKind() == IResourceDelta.REMOVED
									|| delta.getKind() == IResourceDelta.CHANGED) {
								final Resource resource = resourceSet.getResource(
										URI.createPlatformResourceURI(delta.getFullPath().toString(), true), false);
								if (resource != null) {
									if (delta.getKind() == IResourceDelta.REMOVED) {
										removedResources.add(resource);
									} else {
										if ((delta.getFlags() & IResourceDelta.MARKERS) != 0) {
											DiagnosticDecorator.DiagnosticAdapter.update(resource,
													markerHelper.getMarkerDiagnostics(resource,
															(IFile) delta.getResource(), false));
										}
										if ((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
											if (!savedResources.remove(resource)) {
												changedResources.add(resource);
											}
										}
									}
								}
							}
							return false;
						}

						return true;
					}

					public Collection<Resource> getChangedResources() {
						return changedResources;
					}

					public Collection<Resource> getRemovedResources() {
						return removedResources;
					}
				}

				final ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
				delta.accept(visitor);

				if (!visitor.getRemovedResources().isEmpty()) {
					getSite().getShell().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							removedResources.addAll(visitor.getRemovedResources());
							if (!isDirty()) {
								getSite().getPage().closeEditor(AnalyzerEditor.this, false);
							}
						}
					});
				}

				if (!visitor.getChangedResources().isEmpty()) {
					getSite().getShell().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							changedResources.addAll(visitor.getChangedResources());
							if (getSite().getPage().getActiveEditor() == AnalyzerEditor.this) {
								handleActivate();
							}
						}
					});
				}
			} catch (CoreException exception) {
				AggregationAnalyzerEditorPlugin.INSTANCE.log(exception);
			}
		}
	};

	protected Map<Control, Viewer> viewers = new LinkedHashMap<>();

	protected List<Function<IStructuredSelection, ShowInAction>> showInActionFactories = new ArrayList<>();

	private TreeViewer analysisViewer;

	private TreeViewer duplicatesViewer;

	private TreeViewer strictRequirementsViewer;

	private TreeViewer exactRequirementsViewer;

	private TreeViewer repositoryViewer;

	private TreeViewer dependentIUsViewer;

	private TreeViewer requiredIUsViewer;

	private TreeViewer namespaceViewer;

	private TreeViewer packageViewer;

	private TreeViewer mavenViewer;

	private GraphViewer graphViewer;

	private AtomicReference<AnalyzeHandler.Analyzer> analyzer;

	/**
	 * This creates a model editor.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AnalyzerEditor() {
		super();
		initializeEditingDomain();
	}

	protected void setActivePage(Viewer viewer) {
		int pageIndex = 0;
		for (Viewer otherViewer : viewers.values()) {
			if (otherViewer == viewer) {
				setActivePage(pageIndex);
				return;
			}
			++pageIndex;
		}
	}

	protected void handleActivate() {
		handleActivateGen();
		setReadOnly(false);
	}

	/**
	 * Handles activation of the editor or it's associated views.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void handleActivateGen() {
		// Recompute the read only state.
		//
		if (editingDomain.getResourceToReadOnlyMap() != null) {
			editingDomain.getResourceToReadOnlyMap().clear();

			// Refresh any actions that may become enabled or disabled.
			//
			setSelection(getSelection());
		}

		if (!removedResources.isEmpty()) {
			if (handleDirtyConflict()) {
				getSite().getPage().closeEditor(AnalyzerEditor.this, false);
			} else {
				removedResources.clear();
				changedResources.clear();
				savedResources.clear();
			}
		} else if (!changedResources.isEmpty()) {
			changedResources.removeAll(savedResources);
			handleChangedResources();
			changedResources.clear();
			savedResources.clear();
		}
	}

	/**
	 * Handles what to do with changed resources on activation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void handleChangedResources() {
		if (!changedResources.isEmpty() && (!isDirty() || handleDirtyConflict())) {
			ResourceSet resourceSet = editingDomain.getResourceSet();
			if (isDirty()) {
				changedResources.addAll(resourceSet.getResources());
			}
			editingDomain.getCommandStack().flush();

			updateProblemIndication = false;
			for (Resource resource : changedResources) {
				if (resource.isLoaded()) {
					resource.unload();
					try {
						resource.load(resourceSet.getLoadOptions());
					} catch (IOException exception) {
						if (!resourceToDiagnosticMap.containsKey(resource)) {
							resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
						}
					}
				}
			}

			if (AdapterFactoryEditingDomain.isStale(editorSelection)) {
				setSelection(StructuredSelection.EMPTY);
			}

			updateProblemIndication = true;
			updateProblemIndication();
		}
	}

	/**
	 * Updates the problems indication with the information described in the specified diagnostic.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void updateProblemIndication() {
		if (updateProblemIndication) {
			BasicDiagnostic diagnostic = new BasicDiagnostic(Diagnostic.OK,
					"org.eclipse.cbi.p2repo.aggregator.analyzer.editor", 0, null,
					new Object[] { editingDomain.getResourceSet() });
			for (Diagnostic childDiagnostic : resourceToDiagnosticMap.values()) {
				if (childDiagnostic.getSeverity() != Diagnostic.OK) {
					diagnostic.add(childDiagnostic);
				}
			}

			int lastEditorPage = getPageCount() - 1;
			if (lastEditorPage >= 0 && getEditor(lastEditorPage) instanceof ProblemEditorPart) {
				((ProblemEditorPart) getEditor(lastEditorPage)).setDiagnostic(diagnostic);
				if (diagnostic.getSeverity() != Diagnostic.OK) {
					setActivePage(lastEditorPage);
				}
			} else if (diagnostic.getSeverity() != Diagnostic.OK) {
				ProblemEditorPart problemEditorPart = new ProblemEditorPart();
				problemEditorPart.setDiagnostic(diagnostic);
				problemEditorPart.setMarkerHelper(markerHelper);
				try {
					addPage(++lastEditorPage, problemEditorPart, getEditorInput());
					setPageText(lastEditorPage, problemEditorPart.getPartName());
					setActivePage(lastEditorPage);
					showTabs();
				} catch (PartInitException exception) {
					AggregationAnalyzerEditorPlugin.INSTANCE.log(exception);
				}
			}

			if (markerHelper.hasMarkers(editingDomain.getResourceSet())) {
				try {
					markerHelper.updateMarkers(diagnostic);
				} catch (CoreException exception) {
					AggregationAnalyzerEditorPlugin.INSTANCE.log(exception);
				}
			}
		}
	}

	/**
	 * Shows a dialog that asks if conflicting changes should be discarded.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected boolean handleDirtyConflict() {
		return MessageDialog.openQuestion(getSite().getShell(), getString("_UI_FileConflict_label"),
				getString("_WARN_FileConflict"));
	}

	protected void initializeEditingDomain() {
		initializeEditingDomainGen();
		adapterFactory.insertAdapterFactory(new ResourceItemProviderAdapterFactory() {
			@Override
			public Adapter createResourceAdapter() {
				return new ResourceItemProvider(this) {
					@Override
					public Collection<?> getChildren(Object object) {
						Collection<?> children = super.getChildren(object);
						URI uri = ((Resource) object).getURI();
						if (!children.isEmpty() && !uri.equals(AnalyzeHandler.DUPLICATE_ANALYSIS_RESULT)
								&& !uri.equals(AnalyzeHandler.STRICT_REQUIREMENT_ANALYSIS_RESULT)
								&& !uri.equals(AnalyzeHandler.EXACT_REQUIREMENT_ANALYSIS_RESULT)) {
							return Collections.singleton(children.iterator().next());
						}
						return children;
					}
				};
			}
		});
		adapterFactory
				.insertAdapterFactory(new org.eclipse.cbi.p2repo.aggregator.p2.provider.P2ItemProviderAdapterFactory());
		editingDomain.setResourceToReadOnlyMap(new HashMap<>());
	}

	/**
	 * This sets up the editing domain for the model editor.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void initializeEditingDomainGen() {
		// Create an adapter factory that yields item providers.
		//
		adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new AnalyzerItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new AggregatorItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new P2viewItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new P2ItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		// Create the command stack that will notify this editor as commands are executed.
		//
		BasicCommandStack commandStack = new BasicCommandStack() {
			@Override
			public void execute(Command command) {
				// Cancel live validation before executing a command that will trigger a new round of validation.
				//
				if (!(command instanceof AbstractCommand.NonDirtying)) {
					DiagnosticDecorator.cancel(editingDomain);
				}
				super.execute(command);
			}
		};

		// Add a listener to set the most recent command's affected objects to be the selection of the viewer with focus.
		//
		commandStack.addCommandStackListener(new CommandStackListener() {
			@Override
			public void commandStackChanged(final EventObject event) {
				getContainer().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						firePropertyChange(IEditorPart.PROP_DIRTY);

						// Try to select the affected objects.
						//
						Command mostRecentCommand = ((CommandStack) event.getSource()).getMostRecentCommand();
						if (mostRecentCommand != null) {
							setSelectionToViewer(mostRecentCommand.getAffectedObjects());
						}
						for (Iterator<PropertySheetPage> i = propertySheetPages.iterator(); i.hasNext();) {
							PropertySheetPage propertySheetPage = i.next();
							if (propertySheetPage.getControl() == null || propertySheetPage.getControl().isDisposed()) {
								i.remove();
							} else {
								propertySheetPage.refresh();
							}
						}
					}
				});
			}
		});

		// Create the editing domain with a special command stack.
		//
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<>());
	}

	/**
	 * This is here for the listener to be able to call it.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void firePropertyChangeGen(int action) {
		super.firePropertyChange(action);
	}

	@Override
	protected void firePropertyChange(int action) {
		firePropertyChangeGen(action);
		if (action == IEditorPart.PROP_DIRTY) {
			doReset(false);
		}
	}

	/**
	 * This sets the selection into whichever viewer is active.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSelectionToViewer(Collection<?> collection) {
		final Collection<?> theSelection = collection;
		// Make sure it's okay.
		//
		if (theSelection != null && !theSelection.isEmpty()) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// Try to select the items in the current content viewer of the editor.
					//
					if (currentViewer != null) {
						currentViewer.setSelection(new StructuredSelection(theSelection.toArray()), true);
					}
				}
			};
			getSite().getShell().getDisplay().asyncExec(runnable);
		}
	}

	/**
	 * This returns the editing domain as required by the {@link IEditingDomainProvider} interface.
	 * This is important for implementing the static methods of {@link AdapterFactoryEditingDomain}
	 * and for supporting {@link org.eclipse.emf.edit.ui.action.CommandAction}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EditingDomain getEditingDomain() {
		return editingDomain;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public class ReverseAdapterFactoryContentProvider extends AdapterFactoryContentProvider {
		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public ReverseAdapterFactoryContentProvider(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		@Override
		public Object[] getElements(Object object) {
			Object parent = super.getParent(object);
			return (parent == null ? Collections.EMPTY_SET : Collections.singleton(parent)).toArray();
		}

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		@Override
		public Object[] getChildren(Object object) {
			Object parent = super.getParent(object);
			return (parent == null ? Collections.EMPTY_SET : Collections.singleton(parent)).toArray();
		}

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		@Override
		public boolean hasChildren(Object object) {
			Object parent = super.getParent(object);
			return parent != null;
		}

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		@Override
		public Object getParent(Object object) {
			return null;
		}
	}

	/**
	 * This makes sure that one content viewer, either for the current page or the outline view, if it has focus,
	 * is the current one.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCurrentViewer(Viewer viewer) {
		// If it is changing...
		//
		if (currentViewer != viewer) {
			if (selectionChangedListener == null) {
				// Create the listener on demand.
				//
				selectionChangedListener = new ISelectionChangedListener() {
					// This just notifies those things that are affected by the section.
					//
					@Override
					public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
						setSelection(selectionChangedEvent.getSelection());
					}
				};
			}

			// Stop listening to the old one.
			//
			if (currentViewer != null) {
				currentViewer.removeSelectionChangedListener(selectionChangedListener);
			}

			// Start listening to the new one.
			//
			if (viewer != null) {
				viewer.addSelectionChangedListener(selectionChangedListener);
			}

			// Remember it.
			//
			currentViewer = viewer;

			// Set the editors selection based on the current viewer's selection.
			//
			setSelection(currentViewer == null ? StructuredSelection.EMPTY : currentViewer.getSelection());
		}
	}

	/**
	 * This returns the viewer as required by the {@link IViewerProvider} interface.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Viewer getViewer() {
		return currentViewer;
	}

	/**
	 * This creates a context menu for the viewer and adds a listener as well registering the menu for extension.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createContextMenuForGen(StructuredViewer viewer) {
		MenuManager contextMenu = new MenuManager("#PopUp");
		contextMenu.add(new Separator("additions"));
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(this);
		Menu menu = contextMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(contextMenu, new UnwrappingSelectionProvider(viewer));

		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] transfers = new Transfer[] { LocalTransfer.getInstance(), LocalSelectionTransfer.getTransfer(),
				FileTransfer.getInstance() };
		viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(viewer));
		viewer.addDropSupport(dndOperations, transfers, new EditingDomainViewerDropAdapter(editingDomain, viewer));
	}

	protected void createContextMenuFor(StructuredViewer viewer) {
		createContextMenuForGen(viewer);

		// Override the above adding a text transfer.
		DragSource dragSource = (DragSource) viewer.getControl().getData(DND.DRAG_SOURCE_KEY);
		dragSource.dispose();
		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] transfers = new Transfer[] { LocalTransfer.getInstance(), LocalSelectionTransfer.getTransfer(),
				TextTransfer.getInstance() };

		IBaseLabelProvider baseLabelProvider = viewer.getLabelProvider();
		viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(viewer) {
			@Override
			public void dragSetData(DragSourceEvent event) {
				super.dragSetData(event);

				if (selection instanceof IStructuredSelection structuredSelection
						&& TextTransfer.getInstance().isSupportedType(event.dataType)
						&& baseLabelProvider instanceof ILabelProvider labelProvider) {
					event.data = structuredSelection.stream().map(labelProvider::getText)
							.collect(Collectors.joining("\n"));
				}
			}
		});

		viewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent event) {
				if (event.button == 1) {
					showPropertiesView();
				}
			}
		});
	}

	protected Analysis getAnalysis() {
		EList<Resource> resources = editingDomain.getResourceSet().getResources();
		if (!resources.isEmpty()) {
			return getAnalysis(resources.get(0));
		}
		return null;
	}

	protected Analysis getAnalysis(Resource resource) {
		Analysis analysis = (Analysis) EcoreUtil.getObjectByType(resource.getContents(),
				AnalyzerPackage.Literals.ANALYSIS);
		return analysis;
	}

	/**
	 * This is the method called to load a resource into the editing domain's resource set based on the editor's input.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createModel() {
		URI resourceURI = EditUIUtil.getURI(getEditorInput(), editingDomain.getResourceSet().getURIConverter());
		Exception exception = null;
		Resource resource = null;
		try {
			// Load the resource through the editing domain.
			//
			resource = editingDomain.getResourceSet().getResource(resourceURI, true);
		} catch (Exception e) {
			exception = e;
			resource = editingDomain.getResourceSet().getResource(resourceURI, false);
		}

		Diagnostic diagnostic = analyzeResourceProblems(resource, exception);
		if (diagnostic.getSeverity() != Diagnostic.OK) {
			resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
		}
		editingDomain.getResourceSet().eAdapters().add(problemIndicationAdapter);
	}

	/**
	 * Returns a diagnostic describing the errors and warnings listed in the resource
	 * and the specified exception (if any).
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Diagnostic analyzeResourceProblems(Resource resource, Exception exception) {
		boolean hasErrors = !resource.getErrors().isEmpty();
		if (hasErrors || !resource.getWarnings().isEmpty()) {
			BasicDiagnostic basicDiagnostic = new BasicDiagnostic(hasErrors ? Diagnostic.ERROR : Diagnostic.WARNING,
					"org.eclipse.cbi.p2repo.aggregator.analyzer.editor", 0,
					getString("_UI_CreateModelError_message", resource.getURI()),
					new Object[] { exception == null ? (Object) resource : exception });
			basicDiagnostic.merge(EcoreUtil.computeDiagnostic(resource, true));
			return basicDiagnostic;
		} else if (exception != null) {
			return new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.cbi.p2repo.aggregator.analyzer.editor", 0,
					getString("_UI_CreateModelError_message", resource.getURI()), new Object[] { exception });
		} else {
			return Diagnostic.OK_INSTANCE;
		}
	}

	protected void initialize(Analysis analysis) {
		updateProblemIndication = false;

		// Reconcile the GenModel using a ChangeCommand.
		class ReconcileCommand extends ChangeCommand {
			public ReconcileCommand(Analysis analysis) {
				super(analysis);
				setLabel(AggregationAnalyzerEditorPlugin.INSTANCE.getString("_UI_ReconcileCommand_name"));
				setDescription(AggregationAnalyzerEditorPlugin.INSTANCE.getString("_UI_ReconcileCommand_description"));
			}

			@Override
			protected void doExecute() {
				((Analysis) notifier).reconcile();
			}

			public boolean hasChanges() {
				return changeDescription != null && !changeDescription.getObjectChanges().isEmpty();
			}
		}

		ReconcileCommand reconcileCommand = new ReconcileCommand(analysis);
		CommandStack commandStack = editingDomain.getCommandStack();
		commandStack.execute(reconcileCommand);

		// If the reconcile operation produced no changes, the command is a no-op so we can remove it from the command stack.
		// This way the resource is only dirty if it's actually been reconciled in a way that changes the structure.
		if (!reconcileCommand.hasChanges()) {
			commandStack.undo();
		}
	}

	@Override
	public void createPages() {

		createPagesGen();

		selectionViewer.setLabelProvider(createLabelProvider(selectionViewer));

		viewers.put(selectionViewer.getControl(), selectionViewer);
		showInActionFactories.add(selection -> new ShowInAction(getPageText(0), selection, selectionViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof ContributionAnalysis) {
					setTarget(element);
				}
			}
		});

		ResourceSet resourceSet = editingDomain.getResourceSet();
		Analysis analysis = getAnalysis();
		analyzer = new AtomicReference<>();
		if (analysis != null) {
			analyzer.set(new AnalyzeHandler.Analyzer(analysis));
			initialize(analysis);
		}

		analysisViewer = createViewerWithBreadcrumb(composite -> createAnalysisViewer(composite));
		duplicatesViewer = createViewerWithBreadcrumb(composite -> createDuplicatesViewer(composite));
		strictRequirementsViewer = createViewerWithBreadcrumb(composite -> createStrictRequirementsViewer(composite));
		exactRequirementsViewer = createViewerWithBreadcrumb(composite -> createExactRequirementsViewer(composite));

		dependentIUsViewer = createDependentIUsViewer();
		requiredIUsViewer = createRequiredIUsViewer();
		namespaceViewer = createNamespaceViewer();
		packageViewer = createPackageViewer();
		mavenViewer = createViewerWithBreadcrumb(composite -> createMavenViewer(composite));
		repositoryViewer = createRepositoryReviewer();
		graphViewer = createGraphViewer();

		resourceSet.eAdapters().add(new AdapterImpl() {
			@Override
			public void notifyChanged(Notification notification) {
				if (notification.getEventType() == Notification.ADD) {
					Resource resource = (Resource) notification.getNewValue();
					URI uri = resource.getURI();
					if (uri.equals(AnalyzeHandler.STRICT_REQUIREMENT_ANALYSIS_RESULT)) {
						strictRequirementsViewer.setInput(resource);
					} else if (uri.equals(AnalyzeHandler.EXACT_REQUIREMENT_ANALYSIS_RESULT)) {
						exactRequirementsViewer.setInput(resource);
					} else if (uri.equals(AnalyzeHandler.DUPLICATE_ANALYSIS_RESULT)) {
						duplicatesViewer.setInput(resource);
					} else if (uri.equals(AnalyzeHandler.ANALYSIS_RESULT)) {
						analysisViewer.setInput(resource);
						analysisViewer.setSelection(new StructuredSelection(resource.getContents().get(0)));
						namespaceViewer.setInput(resource);
						packageViewer.setInput(resource);
						graphViewer.setInput(resource);
						if (analyzer.getAndSet(null) != null) {
							setActivePage(analysisViewer);
						}

						Analysis analysis = getAnalysis(resource);
						if (analysis != null) {
							dependentIUsViewer
									.setInput(new ItemProvider(Collections.singleton(createIUDependents(analysis))));
							requiredIUsViewer
									.setInput(new ItemProvider(Collections.singleton(createIURequirements(analysis))));
						}
					} else if (uri.equals(AnalyzeHandler.STRUCTURED_AGGREGATE_REPOSITORY)) {
						MetadataRepositoryStructuredView metadataRepositoryStructuredView = (MetadataRepositoryStructuredView) EcoreUtil
								.getObjectByType(resource.getContents(),
										P2viewPackage.Literals.METADATA_REPOSITORY_STRUCTURED_VIEW);
						repositoryViewer.setInput(metadataRepositoryStructuredView.getInstallableUnitList());
						mavenViewer.setInput(metadataRepositoryStructuredView.getMetadataRepository());
					}
				}
				setReadOnly(false);
			}
		});

		showTabs();

		selectionViewer.setAutoExpandLevel(2);
		selectionViewer.setInput(analysis.eResource());

		Object[] elements = ((IStructuredContentProvider) selectionViewer.getContentProvider())
				.getElements(selectionViewer.getInput());
		if (elements.length > 0) {
			selectionViewer.setSelection(new StructuredSelection(elements[0]), true);
		}

		addPageChangedListener(event -> {
			Viewer viewer = viewers.get(event.getSelectedPage());
			setCurrentViewer(viewer);
			if (viewer != null && viewer != selectionViewer) {
				analyzeIfNeeded();
			}
		});
	}

	private <R extends StructuredViewer> R createViewerWithBreadcrumb(Function<Composite, R> viewerCreator) {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		composite.setBackground(selectionViewer.getControl().getBackground());
		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		R viewer = viewerCreator.apply(composite);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		StructuredViewer breadcrumbViewer = createBreadcrumbViewer(composite, viewer);
		breadcrumbViewer.getControl().moveAbove(viewer.getControl());
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		return viewer;
	}

	protected StructuredViewer createBreadcrumbViewer(Composite parent, StructuredViewer treeViewer) {
		try {
			Object dummyInput = new Object();

			parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

			BiConsumer<StructuredViewer, Object> viewerConfigurator = (viewer, input) -> {

				ITreeContentProvider contentProvider = (ITreeContentProvider) treeViewer.getContentProvider();
				ITreeContentProvider delegate = new ITreeContentProvider() {
					@Override
					public boolean hasChildren(Object element) {
						return contentProvider.hasChildren(element);
					}

					@Override
					public Object getParent(Object element) {
						if (element == treeViewer.getInput()) {
							return null;
						}
						return contentProvider.getParent(element);
					}

					@Override
					public Object[] getElements(Object inputElement) {
						return contentProvider.getElements(inputElement);
					}

					@Override
					public Object[] getChildren(Object parentElement) {
						return contentProvider.getElements(parentElement);
					}
				};
				viewer.setContentProvider(delegate);

				class MyLabelProvider extends AdapterFactoryLabelProvider implements IStyledLabelProvider {
					public MyLabelProvider(AdapterFactory adapterFactory) {
						super(adapterFactory);
					}
				}

				IBaseLabelProvider viewerLabelProvider = treeViewer.getLabelProvider();
				IStyledLabelProvider styledLabelProvider = viewerLabelProvider instanceof DelegatingStyledCellLabelProvider
						? ((DelegatingStyledCellLabelProvider) viewerLabelProvider).getStyledStringProvider()
						: new MyLabelProvider(adapterFactory);
				CellLabelProvider labelProvider = new DelegatingStyledCellLabelProvider(styledLabelProvider);
				viewer.setLabelProvider(labelProvider);

				viewer.setInput(input);
			};

			StructuredViewer breadCrumbViewer = BreadCrumbSupport.createBreadCrumbViewer(parent, SWT.HORIZONTAL,
					viewerConfigurator);

			viewerConfigurator.accept(breadCrumbViewer, dummyInput);

			treeViewer.addPostSelectionChangedListener(event -> {
				Object element = event.getStructuredSelection().getFirstElement();
				if (element != null) {
					breadCrumbViewer.setInput(element);
				}
			});

			breadCrumbViewer.addOpenListener(event -> {
				Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (element != null) {
					treeViewer.setSelection(new StructuredSelection(element), true);
					setActivePage(treeViewer);
				}
			});

			return breadCrumbViewer;
		} catch (Throwable ex) {
			ex.printStackTrace();
			// Ignore
			return null;
		}
	}

	private Object createIURequirements(Analysis analysis) {
		TreeNode root = TreeFactory.eINSTANCE.createTreeNode();
		root.setData(analysis);
		List<TreeNode> children = analysis.getContributions().stream().map(ContributionAnalysis::getInstallableUnits)
				.flatMap(Collection::stream).map(iu -> {
					TreeNode iuNode = TreeFactory.eINSTANCE.createTreeNode();
					iuNode.setData(iu);
					List<TreeNode> grandChildren = iu.getRequirements().stream()
							.map(RequirementAnalysis::getResolutions).flatMap(Collection::stream)
							.map(RequirementResolution::getInstallableUnit).distinct().map(r -> {
								TreeNode requiredNode = TreeFactory.eINSTANCE.createTreeNode();
								requiredNode.setData(r);
								return requiredNode;
							}).collect(Collectors.toList());
					sort(grandChildren);
					iuNode.getChildren().addAll(grandChildren);
					return iuNode;
				}).collect(Collectors.toList());
		sort(children);
		root.getChildren().addAll(children);
		return root;
	}

	private Object createIUDependents(Analysis analysis) {
		TreeNode root = TreeFactory.eINSTANCE.createTreeNode();
		root.setData(analysis);
		List<TreeNode> children = analysis.getContributions().stream().map(ContributionAnalysis::getInstallableUnits)
				.flatMap(Collection::stream).map(iu -> {
					TreeNode iuNode = TreeFactory.eINSTANCE.createTreeNode();
					iuNode.setData(iu);
					List<TreeNode> grandChildren = iu.getCapabilities().stream().map(CapabilityAnalysis::getResolutions)
							.flatMap(Collection::stream).map(cr -> cr.getRequirement().getInstallableUnit())
							.filter(r -> r != iu).distinct().map(r -> {
								TreeNode dependentNode = TreeFactory.eINSTANCE.createTreeNode();
								dependentNode.setData(r);
								return dependentNode;
							}).collect(Collectors.toList());
					sort(grandChildren);
					iuNode.getChildren().addAll(grandChildren);
					return iuNode;
				}).collect(Collectors.toList());
		sort(children);
		root.getChildren().addAll(children);
		return root;
	}

	private static void sort(List<TreeNode> children) {
		children.sort((c1, c2) -> ((InstallableUnitAnalysis) c1.getData()).getInstallableUnit()
				.compareTo(((InstallableUnitAnalysis) c2.getData()).getInstallableUnit()));

	}

	protected TreeViewer createTreeViewer(Composite parent, String title, IDoubleClickListener doubleClickListener,
			IMenuListener menuListener) {
		Tree tree = new Tree(parent, SWT.MULTI);
		TreeViewer treeViewer = new TreeViewer(tree);

		treeViewer.setAutoExpandLevel(2);
		treeViewer.setUseHashlookup(true);
		ComposedAdapterFactory composedAdapterFactory = new ComposedAdapterFactory(
				new AdapterFactory[] { new AnalyzerItemProviderAdapterFactory() {
					@Override
					public Adapter createAnalysisAdapter() {
						if (analysisItemProvider == null) {
							analysisItemProvider = new AnalysisItemProvider(this) {
								@Override
								public java.util.Collection<? extends EStructuralFeature> getChildrenFeatures(
										Object object) {
									Collection<? extends EStructuralFeature> childrenFeatures = super.getChildrenFeatures(
											object);
									childrenFeatures.remove(AnalyzerPackage.Literals.ANALYSIS__TAGS);
									return childrenFeatures;
								}
							};
						}
						return analysisItemProvider;
					}

					@Override
					public Adapter createContributionAnalysisAdapter() {
						if (contributionAnalysisItemProvider == null) {
							contributionAnalysisItemProvider = new ContributionAnalysisItemProvider(this) {
								@Override
								public java.util.Collection<? extends EStructuralFeature> getChildrenFeatures(
										Object object) {
									Collection<? extends EStructuralFeature> childrenFeatures = super.getChildrenFeatures(
											object);
									childrenFeatures.remove(AnalyzerPackage.Literals.CONTRIBUTION_ANALYSIS__PROJECTS);
									return childrenFeatures;
								}
							};
						}

						return contributionAnalysisItemProvider;
					}
				}, adapterFactory });
		AdapterFactoryContentProvider contentProvider = new AdapterFactoryContentProviderWithoutSearchProperties(
				composedAdapterFactory);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(createLabelProvider(treeViewer));

		new AdapterFactoryTreeEditor(treeViewer.getTree(), adapterFactory);
		new ColumnViewerInformationControlToolTipSupport(treeViewer,
				new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, treeViewer));

		if (doubleClickListener != null) {
			treeViewer.addDoubleClickListener(doubleClickListener);
		}

		createContextMenuFor(treeViewer);
		if (menuListener != null) {
			MenuManager menuManager = (MenuManager) treeViewer.getControl().getMenu().getData(MenuManager.MANAGER_KEY);
			menuManager.addMenuListener(menuListener);
		}

		Control control = parent == getContainer() ? tree : parent;
		int pageIndex = addPage(control);

		setPageText(pageIndex, title);
		viewers.put(control, treeViewer);
		return treeViewer;
	}

	protected ILabelProvider createLabelProvider(StructuredViewer viewer) {
		return new DecoratingColumLabelProvider(new AdapterFactoryLabelProvider.ColorProvider(adapterFactory, viewer),
				new DiagnosticDecorator(editingDomain, viewer,
						AggregationAnalyzerEditorPlugin.getPlugin().getDialogSettings())) {
			@Override
			public String getToolTipText(Object element) {
				String toolTip = AnalyzerEditor.getToolTip(element);
				if (toolTip != null) {
					String[] lines = toolTip.split("\r?\n");
					return String.join("<br>", lines).replaceAll(" ", "&nbsp;");
				}
				return super.getToolTipText(element);
			}
		};

	}

	protected TreeViewer createDuplicatesViewer(Composite parent) {
		return createTreeViewer(parent, "Duplicate IUs", null, null);
	}

	protected TreeViewer createStrictRequirementsViewer(Composite parent) {
		showInActionFactories
				.add(selection -> new ShowInAction("Strict Requirement IUs", selection, strictRequirementsViewer) {
					@Override
					protected void handleSelection(Object element) {
						if (element instanceof IInstallableUnit) {
							setTarget(element);
						} else if (element instanceof ContributionAnalysis || element instanceof InstallableUnitAnalysis
								|| element instanceof CapabilityAnalysis || element instanceof CapabilityResolution) {
							setTarget(element);
						} else if (element instanceof IUPresentation) {
							setTarget(((IUPresentation) element).getInstallableUnit());
						} else if (element instanceof RequirementResolution requirementResolution) {
							setTarget(requirementResolution.getInstallableUnit());
						}
					}
				});

		return createTreeViewer(parent, "Strict Requirement IUs", event -> {
			ISelection selection = event.getSelection();
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof CapabilityResolution) {
				CapabilityResolution capabilityResolution = (CapabilityResolution) element;
				IInstallableUnit installableUnit = capabilityResolution.getRequirement().getInstallableUnit()
						.getInstallableUnit();
				for (EObject eObject : capabilityResolution.eResource().getContents()) {
					if (((InstallableUnitAnalysis) eObject).getInstallableUnit() == installableUnit) {
						setExpandedSelection(strictRequirementsViewer, new StructuredSelection(eObject));
						break;
					}
				}
			}
		}, manager -> {
		});
	}

	protected TreeViewer createExactRequirementsViewer(Composite parent) {
		showInActionFactories
				.add(selection -> new ShowInAction("Exact Requirement IUs", selection, exactRequirementsViewer) {
					@Override
					protected void handleSelection(Object element) {
						if (element instanceof IInstallableUnit) {
							setTarget(element);
						} else if (element instanceof ContributionAnalysis || element instanceof InstallableUnitAnalysis
								|| element instanceof CapabilityAnalysis || element instanceof CapabilityResolution) {
							setTarget(element);
						} else if (element instanceof IUPresentation) {
							setTarget(((IUPresentation) element).getInstallableUnit());
						} else if (element instanceof RequirementResolution requirementResolution) {
							setTarget(requirementResolution.getInstallableUnit());
						}
					}
				});

		return createTreeViewer(parent, "Exact Requirement IUs", event -> {
			ISelection selection = event.getSelection();
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof CapabilityResolution) {
				CapabilityResolution capabilityResolution = (CapabilityResolution) element;
				IInstallableUnit installableUnit = capabilityResolution.getRequirement().getInstallableUnit()
						.getInstallableUnit();
				for (EObject eObject : capabilityResolution.eResource().getContents()) {
					if (((InstallableUnitAnalysis) eObject).getInstallableUnit() == installableUnit) {
						setExpandedSelection(exactRequirementsViewer, new StructuredSelection(eObject));
						break;
					}
				}
			}
		}, manager -> {
		});
	}

	private void selectOpposite(RequirementResolution requirementResolution) {
		InstallableUnitAnalysis sourceInstallableUnit = requirementResolution.getRequirement().getInstallableUnit();
		InstallableUnitAnalysis installableUnit = requirementResolution.getInstallableUnit();
		CapabilityAnalysis capabilityAnalysis = requirementResolution.getCapability();
		if (capabilityAnalysis != null) {
			Optional<CapabilityResolution> capabilityResoltuion = capabilityAnalysis.getResolutions().stream()
					.filter(rc -> rc.getRequirement().getInstallableUnit() == sourceInstallableUnit).findFirst();
			analysisViewer.setSelection(new StructuredSelection(
					capabilityResoltuion.isPresent() ? capabilityResoltuion.get() : capabilityAnalysis), true);
		} else {
			analysisViewer.setSelection(new StructuredSelection(installableUnit), true);
		}
	}

	private void selectOpposite(CapabilityResolution capabilityResolution) {
		RequirementAnalysis requirement = capabilityResolution.getRequirement();
		Optional<RequirementResolution> requirementResolution = requirement.getResolutions().stream()
				.filter(rc -> rc.getRequirement() == requirement).findFirst();
		analysisViewer.setSelection(
				new StructuredSelection(requirementResolution.isPresent() ? requirementResolution.get() : requirement));
	}

	protected TreeViewer createAnalysisViewer(Composite parent) {
		showInActionFactories.add(selection -> new ShowInAction("Contributed IUs", selection, analysisViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof IInstallableUnit || element instanceof ContributionAnalysis
						|| element instanceof InstallableUnitAnalysis || element instanceof RequirementAnalysis
						|| element instanceof CapabilityAnalysis || element instanceof CapabilityResolution) {
					setTarget(element);
				} else if (element instanceof IUPresentation) {
					setTarget(((IUPresentation) element).getInstallableUnit());
				} else if (element instanceof RequirementWrapper) {
					setTarget(((RequirementWrapper) element).getGenuine());
				} else if (element instanceof ProvidedCapabilityWrapper) {
					setTarget(((ProvidedCapabilityWrapper) element).getGenuine());
				} else if (element instanceof RequirementResolution requirementResolution) {
					setTarget(requirementResolution.getInstallableUnit());
				}
			}
		});

		return createTreeViewer(parent, "Contributed IUs", event -> {
			ISelection selection = event.getSelection();
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof CapabilityResolution) {
				CapabilityResolution capabilityResolution = (CapabilityResolution) element;
				selectOpposite(capabilityResolution);
			} else if (element instanceof RequirementResolution) {
				RequirementResolution requirementResolution = (RequirementResolution) element;
				selectOpposite(requirementResolution);
			}
		}, null);
	}

	protected TreeViewer createDependentIUsViewer() {
		TreeViewer iuDependenciesViewer = createTreeViewer(getContainer(), "Dependent IUs", event -> {
			ISelection selection = event.getSelection();
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) element;
				TreeNode parent = treeNode.getParent();
				if (parent != null) {
					TreeNode root = parent.getParent();
					if (root != null) {
						EObject data = treeNode.getData();
						root.getChildren().stream().filter(it -> it.getData() == data).findFirst().ifPresent(it -> {
							setExpandedSelection(AnalyzerEditor.this.dependentIUsViewer, new StructuredSelection(it));
						});
					}
				}
			}
		}, null);

		showInActionFactories.add(selection -> new ShowInAction("Dependent IUs", selection, iuDependenciesViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof InstallableUnitAnalysis && element instanceof IInstallableUnit) {
					setTarget(element);
				} else if (element instanceof IUPresentation) {
					setTarget(((IUPresentation) element).getInstallableUnit());
				} else if (element instanceof RequirementResolution requirementResolution) {
					setTarget(requirementResolution.getInstallableUnit());
				}
			}
		});

		return iuDependenciesViewer;
	}

	protected TreeViewer createRequiredIUsViewer() {
		TreeViewer requiredIUsViewer = createTreeViewer(getContainer(), "Required IUs", event -> {
			ISelection selection = event.getSelection();
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) element;
				TreeNode parent = treeNode.getParent();
				if (parent != null) {
					TreeNode root = parent.getParent();
					if (root != null) {
						EObject data = treeNode.getData();
						root.getChildren().stream().filter(it -> it.getData() == data).findFirst().ifPresent(it -> {
							setExpandedSelection(AnalyzerEditor.this.requiredIUsViewer, new StructuredSelection(it));
						});
					}
				}
			}
		}, null);

		showInActionFactories.add(selection -> new ShowInAction("Required IUs", selection, requiredIUsViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof InstallableUnitAnalysis || element instanceof IInstallableUnit) {
					setTarget(element);
				} else if (element instanceof IUPresentation) {
					setTarget(((IUPresentation) element).getInstallableUnit());
				} else if (element instanceof RequirementResolution requirementResolution) {
					setTarget(requirementResolution.getInstallableUnit());
				} else if (element instanceof RequirementResolution requirementResolution) {
					setTarget(requirementResolution.getInstallableUnit());
				}
			}
		});

		return requiredIUsViewer;
	}

	protected TreeViewer createRepositoryReviewer() {
		showInActionFactories.add(selection -> new ShowInAction("Aggregated Repository", selection, repositoryViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof IInstallableUnit) {
					setTarget(element);
				} else if (element instanceof InstallableUnitAnalysis) {
					setTarget(((InstallableUnitAnalysis) element).getInstallableUnit());
				} else if (element instanceof RequirementAnalysis) {
					setTarget(((RequirementAnalysis) element).getRequirement());
				} else if (element instanceof CapabilityAnalysis) {
					setTarget(((CapabilityAnalysis) element).getCapability());
				}
			}
		});
		return createTreeViewer(getContainer(), "Aggregated Repository", null, null);
	}

	protected TreeViewer createPackageViewer() {
		TreeViewer packageViewer = createTreeViewer(getContainer(), "Packages", null, null);
		packageViewer.setAutoExpandLevel(3);

		showInActionFactories.add(selection -> new ShowInAction("Packages", selection, packageViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof ContributionAnalysis || element instanceof InstallableUnitAnalysis
						|| element instanceof IUPresentation || element instanceof IInstallableUnit) {
					setTarget(element);
				} else if (element instanceof RequirementResolution requirementResolution) {
					setTarget(requirementResolution.getInstallableUnit());
				}
			}
		});

		AnalyzerItemProviderAdapterFactory analyzerItemProviderAdapterFactory = new AnalyzerItemProviderAdapterFactory() {
			@Override
			public Adapter createContributionAnalysisAdapter() {
				return new ContributionAnalysisItemProvider(this) {
					private List<ItemProvider> children;

					@Override
					public Collection<?> getChildren(Object object) {
						if (children == null) {
							Map<String, Set<CapabilityAnalysis>> javaPackages = new TreeMap<>();
							ContributionAnalysis contributionAnalysis = (ContributionAnalysis) object;
							for (InstallableUnitAnalysis iu : contributionAnalysis.getInstallableUnits()) {
								for (CapabilityAnalysis capabilityAnalysis : iu.getCapabilities()) {
									IProvidedCapability capability = capabilityAnalysis.getCapability();
									if ("java.package".equals(capability.getNamespace())) {
										javaPackages.computeIfAbsent(capability.getName(), key -> new LinkedHashSet<>())
												.add(capabilityAnalysis);
									}
								}
							}

							Set<SegmentSequence> shortNamespaces = new LinkedHashSet<>();
							Set<SegmentSequence> namespaces = new LinkedHashSet<>();
							Set<SegmentSequence> longIDs = Set.of(SegmentSequence.create(".", "org.eclipse.emf"));
							List<SegmentSequence> ids = javaPackages.keySet().stream()
									.map(it -> SegmentSequence.create(".", it)).collect(Collectors.toList());
							for (SegmentSequence id : ids) {
								int segmentCount = id.segmentCount();
								if (segmentCount >= 3) {
									SegmentSequence prefix = getPrefix(id, 3);
									if (namespaces.add(prefix) || longIDs.contains(prefix)) {
										if (segmentCount > 3) {
											if (longIDs.contains(prefix) && !ids.contains(prefix)) {
												shortNamespaces.add(prefix);
												namespaces.add(getPrefix(id, 4));
											} else {
												String nextSegment = id.segment(3);
												boolean hasCommonNextSegment = true;
												for (SegmentSequence otherID : ids) {
													int otherSegmentCount = otherID.segmentCount();
													if (otherSegmentCount >= 3) {
														SegmentSequence otherPrefix = getPrefix(otherID, 3);
														if (otherPrefix.equals(prefix)) {
															if (otherSegmentCount == 3
																	|| !otherID.segment(3).equals(nextSegment)) {
																hasCommonNextSegment = false;
																break;
															}
														}
													}
												}
												if (hasCommonNextSegment) {
													shortNamespaces.add(prefix);
													namespaces.add(getPrefix(id, 4));
												}
											}
										}
									}
								} else {
									namespaces.add(id);
								}
							}

							namespaces.removeAll(shortNamespaces);

							children = namespaces.stream().map(namespace -> {
								int segmentCount = namespace.segmentCount();
								List<ItemProvider> children = javaPackages.entrySet().stream().filter(entry -> {
									SegmentSequence id = SegmentSequence.create(".", entry.getKey());
									return id.segmentCount() >= segmentCount
											&& getPrefix(id, segmentCount).equals(namespace);
								}).map(Map.Entry::getValue).flatMap(Collection::stream).map(capability -> {
									AdapterFactoryItemDelegator itemDelegator = getItemDelegator();
									IProvidedCapability providedCapability = capability.getCapability();
									IInstallableUnit installableUnit = capability.getInstallableUnit()
											.getInstallableUnit();
									String contributingID = installableUnit.getId();
									Set<CapabilityAnalysis> others = javaPackages
											.get(capability.getCapability().getName());
									Set<CapabilityAnalysis> contributors = InstallableUnitUtils
											.getType(installableUnit) == InstallableUnitType.FRAGMENT
													? Set.of()
													: others.stream()
															.filter(it -> it.getCapability().equals(providedCapability)
																	&& !contributingID.equals(it.getInstallableUnit()
																			.getInstallableUnit().getId()))
															.filter(it -> InstallableUnitUtils.getType(it
																	.getInstallableUnit()
																	.getInstallableUnit()) != InstallableUnitType.FRAGMENT)
															.collect(Collectors.toSet());

									// This is useful for producing the list for this issue:
									// https://github.com/eclipse-platform/eclipse.platform.releng.aggregator/issues/2044#issuecomment-2127010148
									if (Boolean.FALSE && contributors.size() >= 1) {
										String text = itemDelegator.getText(capability) + " <-- "
												+ itemDelegator.getText(capability.getInstallableUnit());
										text = text.replace("Java Package ", "");
										if (text.contains("swt")) {
											text.toString();
										}
										System.err.println(text);
									}

									class MyItemProvider extends ItemProvider implements IWrapperItemProvider {
										public MyItemProvider() {
											super(itemDelegator.getText(capability) + " \u2190 "
													+ itemDelegator.getText(capability.getInstallableUnit())
													+ (contributors.size() >= 1 ? " **" : ""),
													itemDelegator.getImage(capability));
										}

										@Override
										public Object getValue() {
											return capability;
										}

										@Override
										public Object getOwner() {
											return object;
										}

										@Override
										public EStructuralFeature getFeature() {
											return null;
										}

										@Override
										public int getIndex() {
											return 0;
										}

										@Override
										public void setIndex(int index) {

										}
									}
									return new MyItemProvider();
								}).collect(Collectors.toList());

								Comparator<String> comparator = CommonPlugin.INSTANCE.getComparator();
								children.sort((i1, i2) -> comparator.compare(i1.getText(), i2.getText()));

								return new ItemProvider(adapterFactory, namespace.toString(),
										ExtendedImageRegistry.INSTANCE
												.getImageDescriptor(AggregationAnalyzerEditorPlugin.INSTANCE
														.getImage("full/obj16/namespace")),
										contributionAnalysis, children);
							}).collect(Collectors.toList());

							Comparator<String> comparator = CommonPlugin.INSTANCE.getComparator();

							children.sort((i1, i2) -> comparator.compare(i1.getText(), i2.getText()));
						}
						return children;
					}
				};
			}

			private SegmentSequence getPrefix(SegmentSequence id, int length) {
				return SegmentSequence.create(".", id.subSegments(0, length));
			}
		};

		ComposedAdapterFactory composedAdapterFactory = new ComposedAdapterFactory(
				new AdapterFactory[] { analyzerItemProviderAdapterFactory, adapterFactory });
		packageViewer.setContentProvider(new AdapterFactoryContentProvider(composedAdapterFactory));
		packageViewer
				.setLabelProvider(new AdapterFactoryLabelProvider.ColorProvider(composedAdapterFactory, packageViewer));

		return packageViewer;
	}

	protected TreeViewer createNamespaceViewer() {
		TreeViewer namespaceViewer = createTreeViewer(getContainer(), "Contributed Namespaces", null, null);
		namespaceViewer.setAutoExpandLevel(3);

		showInActionFactories.add(selection -> new ShowInAction("Contributed Namespaces", selection, namespaceViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof ContributionAnalysis || element instanceof InstallableUnitAnalysis
						|| element instanceof IUPresentation || element instanceof IInstallableUnit) {
					setTarget(element);
				} else if (element instanceof RequirementResolution requirementResolution) {
					setTarget(requirementResolution.getInstallableUnit());
				}
			}
		});

		AnalyzerItemProviderAdapterFactory analyzerItemProviderAdapterFactory = new AnalyzerItemProviderAdapterFactory() {
			@Override
			public Adapter createContributionAnalysisAdapter() {
				return new ContributionAnalysisItemProvider(this) {
					private List<ItemProvider> children;

					@Override
					public Collection<?> getChildren(Object object) {
						if (children == null) {
							ContributionAnalysis contributionAnalysis = (ContributionAnalysis) object;
							Set<SegmentSequence> shortNamespaces = new LinkedHashSet<>();
							Set<SegmentSequence> namespaces = new LinkedHashSet<>();
							Set<SegmentSequence> longIDs = Set.of(SegmentSequence.create(".", "org.eclipse.emf"));
							List<SegmentSequence> ids = contributionAnalysis.getInstallableUnits().stream()
									.map(iu -> getID(iu)).collect(Collectors.toList());
							for (SegmentSequence id : ids) {
								int segmentCount = id.segmentCount();
								if (segmentCount >= 3) {
									SegmentSequence prefix = getPrefix(id, 3);
									if (namespaces.add(prefix) || longIDs.contains(prefix)) {
										if (segmentCount > 3) {
											if (longIDs.contains(prefix) && !ids.contains(prefix)) {
												shortNamespaces.add(prefix);
												namespaces.add(getPrefix(id, 4));
											} else {
												String nextSegment = id.segment(3);
												boolean hasCommonNextSegment = true;
												for (SegmentSequence otherID : ids) {
													int otherSegmentCount = otherID.segmentCount();
													if (otherSegmentCount >= 3) {
														SegmentSequence otherPrefix = getPrefix(otherID, 3);
														if (otherPrefix.equals(prefix)) {
															if (otherSegmentCount == 3
																	|| !otherID.segment(3).equals(nextSegment)) {
																hasCommonNextSegment = false;
																break;
															}
														}
													}
												}
												if (hasCommonNextSegment) {
													shortNamespaces.add(prefix);
													namespaces.add(getPrefix(id, 4));
												}
											}
										}
									}
								} else {
									namespaces.add(id);
								}
							}

							namespaces.removeAll(shortNamespaces);

							children = namespaces.stream().map(namespace -> {
								List<Object> children = contributionAnalysis.getInstallableUnits().stream()
										.filter(iu -> {
											int segmentCount = namespace.segmentCount();
											SegmentSequence id = getID(iu);
											return id.segmentCount() >= segmentCount
													&& getPrefix(id, segmentCount).equals(namespace);
										}).map(iu -> {
											AdapterFactoryItemDelegator itemDelegator = getItemDelegator();
											class MyItemProvider extends ItemProvider implements IWrapperItemProvider {
												public MyItemProvider() {
													super(itemDelegator.getText(iu), itemDelegator.getImage(iu));
												}

												@Override
												public Object getValue() {
													return iu;
												}

												@Override
												public Object getOwner() {
													return object;
												}

												@Override
												public EStructuralFeature getFeature() {
													return null;
												}

												@Override
												public int getIndex() {
													return 0;
												}

												@Override
												public void setIndex(int index) {

												}
											}
											return new MyItemProvider();
										}).collect(Collectors.toList());

								return new ItemProvider(adapterFactory, namespace.toString(),
										ExtendedImageRegistry.INSTANCE
												.getImageDescriptor(AggregationAnalyzerEditorPlugin.INSTANCE
														.getImage("full/obj16/namespace")),
										contributionAnalysis, children);
							}).collect(Collectors.toList());

							Comparator<String> comparator = CommonPlugin.INSTANCE.getComparator();

							children.sort((i1, i2) -> comparator.compare(i1.getText(), i2.getText()));

						}
						return children;
					}
				};
			}

			private SegmentSequence getPrefix(SegmentSequence id, int length) {
				return SegmentSequence.create(".", id.subSegments(0, length));
			}

			private SegmentSequence getID(InstallableUnitAnalysis iu) {
				return SegmentSequence.create(".", iu.getInstallableUnit().getId());
			}
		};

		ComposedAdapterFactory composedAdapterFactory = new ComposedAdapterFactory(
				new AdapterFactory[] { analyzerItemProviderAdapterFactory, adapterFactory });
		namespaceViewer.setContentProvider(new AdapterFactoryContentProvider(composedAdapterFactory));
		namespaceViewer.setLabelProvider(
				new AdapterFactoryLabelProvider.ColorProvider(composedAdapterFactory, namespaceViewer));

		return namespaceViewer;
	}

	private interface WithLink {
		String getURL();
	}

	protected TreeViewer createMavenViewer(Composite parent) {
		showInActionFactories.add(selection -> new ShowInAction("Maven", selection, mavenViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof IInstallableUnit) {
					setTarget(element);
				} else if (element instanceof IWrapperItemProvider) {
					setTarget(((IWrapperItemProvider) element).getValue());
				} else if (element instanceof IUPresentation) {
					setTarget(((IUPresentation) element).getInstallableUnit());
				} else if (element instanceof InstallableUnitAnalysis) {
					setTarget(((InstallableUnitAnalysis) element).getInstallableUnit());
				} else if (element instanceof RequirementAnalysis) {
					setTarget(((RequirementAnalysis) element).getRequirement());
				} else if (element instanceof CapabilityAnalysis) {
					setTarget(((CapabilityAnalysis) element).getCapability());
				}
			}
		});

		Map<Model, Object> modelParents = new HashMap<>();
		Map<Dependency, IRequirement> dependencyRequirements = new HashMap<>();
		Map<Dependency, String> dependencyResolutions = new HashMap<>();
		P2ItemProviderAdapterFactory p2ItemProviderAdapterFactory = new P2ItemProviderAdapterFactory() {
			@Override
			public Adapter createMetadataRepositoryAdapter() {
				if (metadataRepositoryItemProvider == null) {
					metadataRepositoryItemProvider = new MetadataRepositoryItemProvider(this) {
						private Object cachedObject;
						private List<ItemProvider> children;

						@Override
						public Collection<?> getChildren(Object object) {
							if (children == null || cachedObject != object) {
								cachedObject = object;
								modelParents.clear();

								Map<IInstallableUnit, Model> poms = new HashMap<>();
								MetadataRepository metadataRepository = (MetadataRepository) object;
								EList<IInstallableUnit> installableUnits = metadataRepository.getInstallableUnits();
								Map<String, Set<IInstallableUnit>> groups = new TreeMap<>();
								for (IInstallableUnit iu : installableUnits) {
									Map<String, String> properties = iu.getProperties();
									String groupId = null;
									String pom = properties.get("maven-pom");
									if (pom != null) {
										Resource resource = new PomResourceFactoryImpl()
												.createResource(URI.createURI("readonly:"));
										try {
											resource.load(new URIConverter.ReadableInputStream(pom), null);
											Model model = (Model) resource.getContents().get(0).eContents().get(0);
											groupId = model.getGroupId();
											poms.put(iu, model);

											List<IRequirement> requirements = new ArrayList<>(iu.getRequirements());
											DependenciesType dependencies = model.getDependencies();
											if (dependencies != null) {
												EList<Dependency> dependencyList = dependencies.getDependency();
												int dependencyIndex = 0;
												String[] resolutions = properties.get("maven-dependency-resolutions")
														.split(" ");
												for (int requirementIndex : Arrays
														.stream(properties.get("maven-dependency-requirements")
																.split(" "))
														.map(Integer::parseInt).toArray(Integer[]::new)) {
													Dependency dependency = dependencyList.get(dependencyIndex);
													if (requirementIndex != -1) {
														dependencyRequirements.put(dependency,
																requirements.get(requirementIndex));
													}

													dependencyResolutions.put(dependency, resolutions[dependencyIndex]);

													++dependencyIndex;
												}
											}
										} catch (IOException e) {
											AggregationAnalyzerEditorPlugin.INSTANCE.log(e);
										}
									}

									if (groupId == null) {
										groupId = properties.get("maven-groupId");
									}
									if (groupId == null) {
										groupId = "";
									}
									add(groups, groupId, iu);
								}

								children = groups.entrySet().stream().map(entry -> {
									String groupId = entry.getKey();
									Set<IInstallableUnit> ius = entry.getValue();

									String groupIdLabel = "true"
											.equals(ius.iterator().next().getProperty("maven-group-exists")) ? groupId
													: groupId + " **";

									class GroupItemProvider extends ItemProvider implements WithLink {
										public GroupItemProvider(AdapterFactory adapterFactory) {
											super(adapterFactory, groupIdLabel,
													ExtendedImageRegistry.INSTANCE
															.getImageDescriptor(AggregationAnalyzerEditorPlugin.INSTANCE
																	.getImage("full/obj16/namespace")),
													metadataRepository);
										}

										@Override
										public String getURL() {
											return AnalyzeHandler.getURL(groupId.toString());
										}
									}

									GroupItemProvider groupItemProvider = new GroupItemProvider(parentAdapterFactory);
									groupItemProvider.getChildren().addAll(ius.stream().map(iu -> {
										class DelegatingWrapperItemProviderWithLink
												extends DelegatingWrapperItemProvider implements WithLink {
											public DelegatingWrapperItemProviderWithLink(
													AdapterFactory adapterFactory) {
												super(iu, groupItemProvider, null, -1, adapterFactory);
											}

											private String getArtifactId() {
												Model model = poms.get(iu);
												Map<String, String> properties = iu.getProperties();
												String artifactId = model == null ? properties.get("maven-artifactId")
														: model.getArtifactId();
												if (artifactId == null) {
													artifactId = iu.getId();
												}
												return artifactId;
											}

											@Override
											public String getText(Object object) {
												Model model = poms.get(iu);
												Map<String, String> properties = iu.getProperties();
												String artifactId = getArtifactId();

												String classifier = properties.get("maven-classifier");
												if (classifier == null && InstallableUnitUtils.isSourceBundle(iu)) {
													classifier = "source";
												}
												if (classifier != null) {
													artifactId += ":" + classifier;
												}

												String version = model == null ? properties.get("maven-version")
														: model.getVersion();
												if (version != null) {
													artifactId += " / " + version;
												}

												boolean artifactExists = "true"
														.equals(properties.get("maven-artifact-exists"));
												boolean artifactVersionExists = "true"
														.equals(properties.get("maven-artifact-version-exists"));

												if (!artifactExists) {
													artifactId += " **";
												} else if (!artifactVersionExists) {
													artifactId += " *";
												}

												return artifactId;
											}

											@Override
											public boolean hasChildren(Object object) {
												return poms.get(iu) != null;
											}

											@Override
											public Collection<?> getChildren(Object object) {
												Model model = poms.get(iu);
												return model == null ? Collections.emptyList()
														: Collections.singletonList(model);
											}

											@Override
											public String getURL() {
												return AnalyzeHandler.getURL(groupId, getArtifactId());
											}
										}

										DelegatingWrapperItemProviderWithLink result = new DelegatingWrapperItemProviderWithLink(
												getRootAdapterFactory());
										modelParents.put(poms.get(iu), result);
										return result;
									}).collect(Collectors.toList()));

									return groupItemProvider;
								}).collect(Collectors.toList());

								children.sort((i1, i2) -> CommonPlugin.INSTANCE.getComparator().compare(i1.getText(),
										i2.getText()));
							}
							return children;
						}
					};
				}

				return metadataRepositoryItemProvider;
			}
		};

		Styler errorStyler = StyledString.createColorRegistryStyler(JFacePreferences.ERROR_COLOR, null);

		ReflectiveItemProviderAdapterFactory reflectiveItemProviderAdapterFactory = new ReflectiveItemProviderAdapterFactory() {
			{
				supportedTypes.add(IItemStyledLabelProvider.class);
				class MyReflectiveItemProvider extends ReflectiveItemProvider implements IItemStyledLabelProvider {
					public MyReflectiveItemProvider(AdapterFactory adapterFactory) {
						super(adapterFactory);
					}

					@Override
					public String getText(Object object) {
						return getStyledText(object).toString();
					}

					@Override
					public Object getStyledText(Object object) {
						if (object instanceof Model) {
							Model model = (Model) object;
							String name = model.getName();
							StyledString result = new StyledString();
							if (name != null) {
								result.append(name);
							}

							String modelGroupId = model.getGroupId();
							Object parent = AdapterFactoryEditingDomain.unwrap(modelParents.get(model));
							if (parent instanceof IInstallableUnit) {
								IInstallableUnit iu = (IInstallableUnit) parent;
								String groupId = iu.getProperty("maven-groupId");
								if (!Objects.equals(modelGroupId, groupId)) {
									result.append(" ::", errorStyler);
									result.append(Objects.toString(groupId), errorStyler);
								}
							}
							return result;
						} else if (object instanceof LicensesType) {
							return "<licences>";
						} else if (object instanceof ProfilesType) {
							return "<profiles>";
						} else if (object instanceof DependenciesType) {
							return "<dependencies>";
						} else if (object instanceof Profile profile) {
							return profile.getId();
						} else if (object instanceof Activation) {
							return "<activation>";
						} else if (object instanceof ActivationOS activationOS) {
							List<String> attributeValues = new ArrayList<>();
							for (EAttribute eAttribute : activationOS.eClass().getEAllAttributes()) {
								if (activationOS.eIsSet(eAttribute)) {
									attributeValues.add(eAttribute.getName() + "=" + activationOS.eGet(eAttribute));
								}
							}
							return String.join(", ", attributeValues);
						} else if (object instanceof Dependency) {
							Dependency dependency = (Dependency) object;
							String groupId = dependency.getGroupId();
							String artifactId = dependency.getArtifactId();
							String version = dependency.getVersion();
							String classifier = dependency.getClassifier();
							String scope = dependency.getScope();
							String type = dependency.isSetType() ? dependency.getType() : null;
							String optional = dependency.isOptional() ? "?" : null;
							StyledString separator = new StyledString().append(" ")
									.append(":", StyledString.DECORATIONS_STYLER).append(" ");
							IRequirement requirement = dependencyRequirements.get(dependency);
							String string = requirement == null ? null : requirement.toString();
							String resolution = dependencyResolutions.get(dependency);
							Collector<String, StyledString, StyledString> collector = new Collector<>() {

								boolean first = true;

								@Override
								public Supplier<StyledString> supplier() {
									return StyledString::new;
								}

								@Override
								public BiConsumer<StyledString, String> accumulator() {
									return (s, e) -> {
										if (first) {
											first = false;
										} else {
											s.append(separator);
										}
										if (e == groupId || e == string) {
											s.append(e, StyledString.QUALIFIER_STYLER);
										} else if (e == resolution && resolution.contains("**")) {
											s.append(e, errorStyler);
										} else if (e == version || e == resolution) {
											s.append(e, StyledString.COUNTER_STYLER);
										} else {
											s.append(e);
										}
									};
								}

								@Override
								public BinaryOperator<StyledString> combiner() {
									return (left, right) -> left.append(right);
								}

								@Override
								public Function<StyledString, StyledString> finisher() {
									return Function.identity();
								}

								@Override
								public Set<Characteristics> characteristics() {
									return Set.of(Characteristics.UNORDERED);
								}
							};

							return Arrays
									.asList(new String[] { groupId, artifactId, version, classifier, scope, type,
											optional, string, resolution })
									.stream().filter(it -> it != null && !it.isBlank()).collect(collector);
						}
						return super.getText(object);
					}

					@Override
					public Object getImage(Object object) {
						if (object instanceof Model) {
							return AggregationAnalyzerEditorPlugin.INSTANCE.getImage("full/obj16/pom");
						} else if (object instanceof LicensesType || object instanceof DependenciesType
								|| object instanceof Profile || object instanceof ProfilesType
								|| object instanceof Activation || object instanceof ActivationOS) {
							return AggregationAnalyzerEditorPlugin.INSTANCE.getImage("full/obj16/element");
						} else if (object instanceof Dependency) {
							return AggregationAnalyzerEditorPlugin.INSTANCE.getImage("full/obj16/dependency");
						}
						return super.getImage(object);
					}

					@Override
					public Command createCommand(Object object, EditingDomain domain,
							Class<? extends Command> commandClass, CommandParameter commandParameter) {
						return UnexecutableCommand.INSTANCE;
					}

					@Override
					protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
					}

					@Override
					public Object getParent(Object object) {
						Object parent = modelParents.get(object);
						return parent != null ? parent : super.getParent(object);
					}
				}
				reflectiveItemProviderAdapter = new MyReflectiveItemProvider(this);
			}

			@Override
			public Adapter createAdapter(Notifier target) {
				return super.createAdapter(target);
			}

		};

		ComposedAdapterFactory composedAdapterFactory = new ComposedAdapterFactory(new AdapterFactory[] {
				reflectiveItemProviderAdapterFactory, p2ItemProviderAdapterFactory, adapterFactory });
		Runnable setLabelProvider = () -> {
			StyledLabelProvider labelProvider = new StyledLabelProvider(composedAdapterFactory, mavenViewer);
			CellLabelProvider labelProvider2 = new DelegatingStyledCellLabelProvider(labelProvider);
			mavenViewer.setLabelProvider(labelProvider2);
		};

		mavenViewer = createTreeViewer(parent, "Maven", null, menuManager -> {
			String label = AggregationAnalyzerEditorPlugin.INSTANCE.getString("_UI_RefreshViewer_menu_item");
			for (IContributionItem item : menuManager.getItems()) {
				if (item instanceof ActionContributionItem) {
					IAction action = ((ActionContributionItem) item).getAction();
					if (label.equals(action.getText())) {
						menuManager.insertAfter("ui-actions", new Action(label) {
							@Override
							public void run() {
								setLabelProvider.run();
								action.run();
							}
						});
						menuManager.remove(item);
						break;
					}
				}
			}
		});
		mavenViewer.setContentProvider(new AdapterFactoryContentProvider(composedAdapterFactory));
		setLabelProvider.run();

		return mavenViewer;
	}

	private static class Link {
		public static final Link NONE = new Link(null, null, null);

		private ContributionAnalysis source;
		private ContributionAnalysis destination;
		private Set<RequirementResolution> dependencies;

		public Link(ContributionAnalysis source, ContributionAnalysis destination,
				Set<RequirementResolution> dependencies) {
			this.source = source;
			this.destination = destination;
			this.dependencies = dependencies;
		}

		public ContributionAnalysis getSource() {
			return source;
		}

		public ContributionAnalysis getDestination() {
			return destination;
		}

		public Set<RequirementResolution> getDependencies() {
			return dependencies;
		}
	}

	private GraphViewer createGraphViewer() {
		GraphViewer graphViewer = new GraphViewer(getContainer(), SWT.NONE);
		Graph graph = graphViewer.getGraphControl();
		graph.setDynamicLayout(false);
		int pageIndex = addPage(graph);

		showInActionFactories.add(selection -> new ShowInAction("Dependencies", selection, graphViewer) {
			@Override
			protected void handleSelection(Object element) {
				if (element instanceof ContributionAnalysis) {
					setTarget(element);
				}
			}
		});

		List<Object> selection = new ArrayList<>();
		graphViewer.addPostSelectionChangedListener(it -> {
			selection.clear();
			List<?> list = it.getStructuredSelection().toList();
			selection.addAll(list);
			graphViewer.refresh(true);
		});

		Map<ContributionAnalysis, AtomicInteger> usageCounts = new HashMap<>();
		AtomicLong oldestLastModified = new AtomicLong(System.currentTimeMillis());

		class MyContentProvider implements IGraphEntityRelationshipContentProvider {

			private Analysis analysis;

			final Map<Map<ContributionAnalysis, ContributionAnalysis>, Link> links = new HashMap<>();

			@Override
			public Object[] getElements(Object inputElement) {
				Analysis analysis = getAnalysis((Resource) inputElement);
				if (analysis != null) {
					// filter disabled contributions.
					List<ContributionAnalysis> contributions = analysis.getContributions().stream()
							.filter(it -> it.getContribution() == null || it.getContribution().isEnabled()).toList();
					if (this.analysis != analysis) {
						// Populate the usage counts.
						this.analysis = analysis;
						usageCounts.clear();
						for (ContributionAnalysis source : contributions) {
							long lastModified = source.getLastModified();
							if (lastModified != Long.MIN_VALUE) {
								oldestLastModified.set(Math.min(lastModified, oldestLastModified.get()));
							}

							for (ContributionAnalysis destination : contributions) {
								getRelationships(source, destination);
							}
						}
					}
					return contributions.toArray();
				}
				return new Object[0];
			}

			@Override
			public Object[] getRelationships(Object source, Object destination) {
				return getRelationships((ContributionAnalysis) source, (ContributionAnalysis) destination);
			}

			public Collection<? extends Object> getSources(Object destination) {
				Set<ContributionAnalysis> result = links.entrySet().stream()
						.filter(entry -> entry.getValue() != Link.NONE).map(Map.Entry::getKey).map(Map::entrySet)
						.flatMap(Set::stream).filter(entry -> entry.getValue() == destination).map(Map.Entry::getKey)
						.collect(Collectors.toSet());
				return result;
			}

			public Collection<? extends Object> getDestinations(Object source) {
				Set<ContributionAnalysis> result = links.entrySet().stream()
						.filter(entry -> entry.getValue() != Link.NONE).map(Map.Entry::getKey).map(Map::entrySet)
						.flatMap(Set::stream).filter(entry -> entry.getKey() == source).map(Map.Entry::getValue)
						.collect(Collectors.toSet());
				return result;
			}

			public Object[] getRelationships(ContributionAnalysis source, ContributionAnalysis destination) {
				AtomicInteger usageCount = usageCounts.computeIfAbsent(destination, x -> new AtomicInteger());
				if (source != destination) {
					Link link = links.computeIfAbsent(Collections.singletonMap(source, destination), it -> {
						Set<RequirementResolution> dependencies = source.getInstallableUnits().stream()
								.map(InstallableUnitAnalysis::getRequirements)//
								.flatMap(Collection::stream)
								.filter(r -> r.getRequirement().getMin() > 0 || r.getRequirement().isGreedy())//
								.map(r -> {
									// If any resolution is a dominator, then return only that resolution.
									List<RequirementResolution> resolutions = r.getResolutions();
									for (RequirementResolution requirementResolution : resolutions) {
										if (requirementResolution.getInstallableUnit().getContribution().isDominant()) {
											return Collections.singleton(requirementResolution);
										}
									}
									return resolutions;
								}).flatMap(Collection::stream)
								.filter(iu -> iu.getInstallableUnit().getContribution() == destination)
								.collect(Collectors.toSet());
						if (!dependencies.isEmpty()) {
							usageCount.incrementAndGet();
							return new Link(source, destination, dependencies);
						}

						return Link.NONE;
					});
					if (link != Link.NONE) {
						return new Object[] { link };
					}
				}
				return new Object[0];
			}
		}

		MyContentProvider contentProvider = new MyContentProvider();

		graph.addMouseListener(MouseListener.mouseDoubleClickAdapter(it -> {
			for (Object object : selection) {
				if (object instanceof Link) {
					Link link = (Link) object;
					analysisViewer.setSelection(new StructuredSelection(link.getDependencies().toArray()), true);
					setActivePage(analysisViewer);
					return;
				}
			}

			Set<Object> moreSelection = new LinkedHashSet<>();
			boolean shift = (it.stateMask & SWT.SHIFT) != 0;
			if (shift) {
				Set<?> collect = selection.stream().map(contentProvider::getSources).flatMap(Collection::stream)
						.collect(Collectors.toSet());
				moreSelection.addAll(collect);
			} else {
				Set<?> collect = selection.stream().map(contentProvider::getDestinations).flatMap(Collection::stream)
						.collect(Collectors.toSet());
				moreSelection.addAll(collect);
			}

			if (!moreSelection.isEmpty()) {
				moreSelection.addAll(selection);
				graphViewer.setSelection(new StructuredSelection(moreSelection.toArray()), true);
			}
		}));

		Font font = graph.getFont();

		Color black = graph.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		Color green = graph.getDisplay().getSystemColor(SWT.COLOR_GREEN);
		Color blue = graph.getDisplay().getSystemColor(SWT.COLOR_BLUE);

		long now = System.currentTimeMillis();

		class MyLabelProvider extends AdapterFactoryLabelProvider.FontProvider
				implements IConnectionStyleProvider, IEntityStyleProvider {
			public MyLabelProvider(AdapterFactory adapterFactory) {
				super(adapterFactory, font);
			}

			@Override
			public Image getImage(Object object) {
				if (object instanceof Link) {
					return null;
				}
				return super.getImage(object);
			}

			@Override
			public String getText(Object object) {
				if (object instanceof Link) {
					return null;
				}

				String text = super.getText(object);
				if (object instanceof ContributionAnalysis) {
					text += '\u200b';
					ContributionAnalysis contributionAnalysis = (ContributionAnalysis) object;
					Float ageInDays = AnalyzerUtil.getAgeInDays(contributionAnalysis);
					Float gitRepositoryAgeInDays = AnalyzerUtil.getGitRepositoryAgeInDays(contributionAnalysis);
					if (ageInDays != null) {
						text += " [" + (int) (float) ageInDays
								+ (gitRepositoryAgeInDays == null ? "" : "/" + (int) (float) gitRepositoryAgeInDays)
								+ "]";
					} else if (gitRepositoryAgeInDays != null) {
						text += " [-/" + (int) (float) gitRepositoryAgeInDays + "]";
					}

					Contribution contribution = contributionAnalysis.getContribution();
					if (contribution != null && !contribution.isEnabled()) {
						return text + " - x";
					}
				}

				return text + " - " + usageCounts.get(object);
			}

			@Override
			public int getConnectionStyle(Object rel) {
				return ZestStyles.CONNECTIONS_DIRECTED;
			}

			@Override
			public Font getFont(Object element) {
				Font font = super.getFont(element);
				AtomicInteger usageCount = usageCounts.get(element);
				if (usageCount != null) {
					int count = usageCount.get();
					if (count == 0) {
						return ExtendedFontRegistry.INSTANCE.getFont(defaultFont, URI.createURI("font:////italic"));
					}

					return ExtendedFontRegistry.INSTANCE.getFont(defaultFont,
							URI.createURI("font:///+" + count + "/normal"));
				}
				return font;
			}

			@Override
			public Color getColor(Object rel) {
				if (rel instanceof Link) {
					Link link = (Link) rel;
					if (selection.contains(link.getSource())) {
						return green;
					}
					if (selection.contains(link.getDestination())) {
						return blue;
					}
				}
				return null;
			}

			@Override
			public Color getHighlightColor(Object rel) {
				if (selection.contains(rel)) {
					return black;
				}
				return null;
			}

			@Override
			public int getLineWidth(Object rel) {
				return 0;
			}

			@Override
			public IFigure getTooltip(Object entity) {
				String toolTip = AnalyzerEditor.getToolTip(entity);
				if (toolTip != null) {
					Label label = new Label(toolTip.strip());
					label.setBorder(new MarginBorder(5));
					return label;
				}
				return null;
			}

			@Override
			public Color getNodeHighlightColor(Object entity) {
				return null;
			}

			@Override
			public Color getBorderColor(Object entity) {
				return getBackgroundColour(entity);
			}

			@Override
			public Color getBorderHighlightColor(Object entity) {
				return null;
			}

			@Override
			public int getBorderWidth(Object entity) {
				if (Boolean.TRUE) {
					return 0;
				}
				AtomicInteger count = usageCounts.get(entity);
				return count == null ? 0 : count.get();
			}

			@Override
			public Color getBackgroundColour(Object entity) {
				if (entity instanceof ContributionAnalysis) {
					Contribution contribution = ((ContributionAnalysis) entity).getContribution();
					if (contribution != null && !contribution.isEnabled()) {
						RGB rgb = graph.LIGHT_BLUE.getRGB();
						return ExtendedColorRegistry.INSTANCE.getColor(null, null, URI.createURI("color://rgb/" + //
								(rgb.red + (255 - rgb.red) * 7 / 8) + "/" + //
								(rgb.blue + (255 - rgb.blue) * 2 / 3) + "/" + //
								(rgb.green + (255 - rgb.green) * 2 / 3)));
					}

				}
				return null;
			}

			@Override
			public Color getForegroundColour(Object entity) {
				if (entity instanceof ContributionAnalysis) {
					long lastModified = ((ContributionAnalysis) entity).getLastModified();
					if (lastModified != Long.MIN_VALUE) {
						long oldest = Math.max(0, now - oldestLastModified.get());
						if (oldest != 0) {
							long delta = oldest - Math.max(0, now - lastModified);
							float brightness = 1.0f - ((float) delta) / oldest;
							return ExtendedColorRegistry.INSTANCE.getColor(null, null, URI.createURI(
									"color://hsb/360/1.0/" + String.format(Locale.ROOT, "%.5f", brightness)));
						}
					}
					return black;
				}

				return null;
			}

			@Override
			public boolean fisheyeNode(Object entity) {
				return false;
			}
		}

		MyLabelProvider labelProvider = new MyLabelProvider(adapterFactory);
		graphViewer.setLabelProvider(labelProvider);

		graphViewer.setContentProvider(contentProvider);

		class MyLayout extends AbstractLayoutAlgorithm {

			@Override
			public void applyLayout(boolean clean) {
				double boundsWidth = context.getBounds().width;
				EntityLayout[] entities = context.getEntities();

				Map<Integer, Set<EntityLayout>> rows = new TreeMap<>(Comparator.reverseOrder());
				int[] ranges = contentProvider.analysis.getLevels().stream().mapToInt(Integer::intValue).toArray();

				for (EntityLayout node : entities) {
					int usageCount = getUsageCount(node);
					int groupCount = usageCount;
					for (int range : ranges) {
						if (usageCount > range) {
							groupCount = range;
							break;
						}
					}

					add(rows, groupCount, node);
				}

				List<List<EntityLayout>> partitionedRows = new ArrayList<>();
				for (Collection<? extends Collection<EntityLayout>> collection : List.of(rows.values(),
						partitionedRows)) {
					final double heightPadding = 30.0;
					final double spacing = 10.0;
					double rowHeight = 0.0;
					double offset = 10.0;
					double width = 0.0;

					for (Collection<EntityLayout> nodes : collection) {
						List<EntityLayout> nodeList = new ArrayList<>(nodes);
						nodeList.sort(new Comparator<EntityLayout>() {
							Comparator<String> comparator = CommonPlugin.INSTANCE.getComparator();

							@Override
							public int compare(EntityLayout o1, EntityLayout o2) {
								int result = Integer.compare(getUsageCount(o2), getUsageCount(o1));
								if (result == 0) {
									result = comparator.compare(labelProvider.getText(o1), labelProvider.getText(o2));
								}
								return result;
							}
						});

						double fullRowWidth = spacing;
						for (EntityLayout node : nodeList) {
							double widthInLayout = node.getSize().width;
							fullRowWidth += widthInLayout + spacing;
						}

						double padding = Math.max(0, (boundsWidth - fullRowWidth) / (nodes.size() + 1));

						if (rowHeight > 0) {
							offset += rowHeight + heightPadding;
						}
						rowHeight = 0.0;
						double rowWidth = spacing + padding;
						List<EntityLayout> nodesInCurrentRow = new ArrayList<>();
						partitionedRows.add(nodesInCurrentRow);
						for (EntityLayout node : nodeList) {
							DisplayIndependentDimension size = node.getSize();
							double heightInLayout = size.height;
							rowHeight = Math.max(rowHeight, heightInLayout);
							double widthInLayout = size.width;
							if (rowWidth + widthInLayout > boundsWidth) {
								offset += rowHeight + heightPadding;
								nodesInCurrentRow = new ArrayList<>();
								partitionedRows.add(nodesInCurrentRow);
								rowWidth = spacing + padding;
							}

							nodesInCurrentRow.add(node);
							node.setLocation(rowWidth, offset);
							rowWidth += widthInLayout + spacing + padding;
						}

						width = Math.max(width, rowWidth);
					}

					partitionedRows = new ArrayList<>();
				}
			}

			private int getUsageCount(EntityLayout entity) {
				Item[] items = entity.getItems();
				Object data = items[0].getData();
				int usageCount = usageCounts.get(data).get();
				return usageCount;
			}
		}

		graph.setLayoutAlgorithm(new MyLayout(), true);

		setPageText(pageIndex, "Dependencies");

		MenuManager contextMenu = new MenuManager("#PopUp");
		contextMenu.add(new Separator("additions"));
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(this);
		Menu menu = contextMenu.createContextMenu(graphViewer.getControl());
		graphViewer.getControl().setMenu(menu);

		contextMenu.addMenuListener(manager -> {
			Action layout = new Action("Layout") {
				@Override
				public void run() {
					graphViewer.applyLayout();
				}
			};
			Action export = new Action("Export to SVG") {
				@Override
				public void run() {
					export(graph, getAnalysis());
				}
			};
			export.setEnabled(GMF_AVAIBLE);

			manager.insertAfter("additions", new Separator());
			manager.insertAfter("additions", export);
			manager.insertAfter("additions", layout);
		});

		viewers.put(graphViewer.getControl(), graphViewer);
		return graphViewer;
	}

	/**
	 * This is the method used by the framework to install your own controls.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPagesGen() {
		// Creates the model from the editor input
		//
		createModel();

		// Only creates the other pages if there is something that can be edited
		//
		if (!getEditingDomain().getResourceSet().getResources().isEmpty()) {
			// Create a page for the selection tree view.
			//
			Tree tree = new Tree(getContainer(), SWT.MULTI);
			selectionViewer = new TreeViewer(tree);
			setCurrentViewer(selectionViewer);

			selectionViewer.setUseHashlookup(true);
			selectionViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
			selectionViewer.setLabelProvider(new DecoratingColumLabelProvider(
					new AdapterFactoryLabelProvider.ColorProvider(adapterFactory, selectionViewer),
					new DiagnosticDecorator(editingDomain, selectionViewer,
							AggregationAnalyzerEditorPlugin.getPlugin().getDialogSettings())));
			selectionViewer.setInput(editingDomain.getResourceSet());
			selectionViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet().getResources().get(0)),
					true);

			new AdapterFactoryTreeEditor(selectionViewer.getTree(), adapterFactory);
			new ColumnViewerInformationControlToolTipSupport(selectionViewer,
					new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, selectionViewer));

			createContextMenuFor(selectionViewer);
			int pageIndex = addPage(tree);
			setPageText(pageIndex, getString("_UI_SelectionPage_label"));

			getSite().getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!getContainer().isDisposed()) {
						setActivePage(0);
					}
				}
			});
		}

		// Ensures that this editor will only display the page's tab
		// area if there are more than one page
		//
		getContainer().addControlListener(new ControlAdapter() {
			boolean guard = false;

			@Override
			public void controlResized(ControlEvent event) {
				if (!guard) {
					guard = true;
					hideTabs();
					guard = false;
				}
			}
		});

		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				updateProblemIndication();
			}
		});
	}

	protected void showPropertiesView() {
		try {
			getEditorSite().getPage().showView("org.eclipse.ui.views.PropertySheet", null, IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException exception) {
			AggregationAnalyzerEditorPlugin.INSTANCE.log(exception);
		}
	}

	protected void hideTabs() {
	}

	/**
	 * If there is just one page in the multi-page editor part,
	 * this hides the single tab at the bottom.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void hideTabsGen() {
		if (getPageCount() <= 1) {
			setPageText(0, "");
			if (getContainer() instanceof CTabFolder) {
				Point point = getContainer().getSize();
				Rectangle clientArea = getContainer().getClientArea();
				getContainer().setSize(point.x, 2 * point.y - clientArea.height - clientArea.y);
			}
		}
	}

	/**
	 * If there is more than one page in the multi-page editor part,
	 * this shows the tabs at the bottom.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void showTabs() {
		if (getPageCount() > 1) {
			setPageText(0, getString("_UI_SelectionPage_label"));
			if (getContainer() instanceof CTabFolder) {
				Point point = getContainer().getSize();
				Rectangle clientArea = getContainer().getClientArea();
				getContainer().setSize(point.x, clientArea.height + clientArea.y);
			}
		}
	}

	/**
	 * This is used to track the active viewer.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void pageChange(int pageIndex) {
		super.pageChange(pageIndex);

		if (contentOutlinePage != null) {
			handleContentOutlineSelection(contentOutlinePage.getSelection());
		}
	}

	/**
	 * This is how the framework determines which interfaces we implement.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public <T> T getAdapter(Class<T> key) {
		if (key.equals(IContentOutlinePage.class)) {
			return showOutlineView() ? key.cast(getContentOutlinePage()) : null;
		} else if (key.equals(IPropertySheetPage.class)) {
			return key.cast(getPropertySheetPage());
		} else if (key.equals(IGotoMarker.class)) {
			return key.cast(this);
		} else if (key.equals(IFindReplaceTarget.class)) {
			return FindAndReplaceTarget.getAdapter(key, this, AggregationAnalyzerEditorPlugin.getPlugin());
		} else {
			return super.getAdapter(key);
		}
	}

	/**
	 * This accesses a cached version of the content outliner.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IContentOutlinePage getContentOutlinePage() {
		if (contentOutlinePage == null) {
			// The content outline is just a tree.
			//
			class MyContentOutlinePage extends ContentOutlinePage {
				@Override
				public void createControl(Composite parent) {
					super.createControl(parent);
					contentOutlineViewer = getTreeViewer();
					contentOutlineViewer.addSelectionChangedListener(this);

					// Set up the tree viewer.
					//
					contentOutlineViewer.setUseHashlookup(true);
					contentOutlineViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
					contentOutlineViewer.setLabelProvider(new DecoratingColumLabelProvider(
							new AdapterFactoryLabelProvider.ColorProvider(adapterFactory, contentOutlineViewer),
							new DiagnosticDecorator(editingDomain, contentOutlineViewer,
									AggregationAnalyzerEditorPlugin.getPlugin().getDialogSettings())));
					contentOutlineViewer.setInput(editingDomain.getResourceSet());

					new ColumnViewerInformationControlToolTipSupport(contentOutlineViewer,
							new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, contentOutlineViewer));

					// Make sure our popups work.
					//
					createContextMenuFor(contentOutlineViewer);

					if (!editingDomain.getResourceSet().getResources().isEmpty()) {
						// Select the root object in the view.
						//
						contentOutlineViewer.setSelection(
								new StructuredSelection(editingDomain.getResourceSet().getResources().get(0)), true);
					}
				}

				@Override
				public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager,
						IStatusLineManager statusLineManager) {
					super.makeContributions(menuManager, toolBarManager, statusLineManager);
					contentOutlineStatusLineManager = statusLineManager;
				}

				@Override
				public void setActionBars(IActionBars actionBars) {
					super.setActionBars(actionBars);
					getActionBarContributor().shareGlobalActions(this, actionBars);
				}
			}

			contentOutlinePage = new MyContentOutlinePage();

			// Listen to selection so that we can handle it is a special way.
			//
			contentOutlinePage.addSelectionChangedListener(new ISelectionChangedListener() {
				// This ensures that we handle selections correctly.
				//
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					handleContentOutlineSelection(event.getSelection());
				}
			});
		}

		return contentOutlinePage;
	}

	/**
	 * This accesses a cached version of the property sheet.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPropertySheetPage getPropertySheetPage() {
		PropertySheetPage propertySheetPage = new ExtendedPropertySheetPage(editingDomain,
				ExtendedPropertySheetPage.Decoration.LIVE,
				AggregationAnalyzerEditorPlugin.getPlugin().getDialogSettings(), 1, true) {
			@Override
			public void setSelectionToViewer(List<?> selection) {
				AnalyzerEditor.this.setSelectionToViewer(selection);
				AnalyzerEditor.this.setFocus();
			}

			@Override
			public void setActionBars(IActionBars actionBars) {
				super.setActionBars(actionBars);
				getActionBarContributor().shareGlobalActions(this, actionBars);
			}
		};
		propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory));
		propertySheetPages.add(propertySheetPage);

		return propertySheetPage;
	}

	/**
	 * This deals with how we want selection in the outliner to affect the other views.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void handleContentOutlineSelection(ISelection selection) {
		if (selectionViewer != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
			Iterator<?> selectedElements = ((IStructuredSelection) selection).iterator();
			if (selectedElements.hasNext()) {
				// Get the first selected element.
				//
				Object selectedElement = selectedElements.next();

				ArrayList<Object> selectionList = new ArrayList<>();
				selectionList.add(selectedElement);
				while (selectedElements.hasNext()) {
					selectionList.add(selectedElements.next());
				}

				// Set the selection to the widget.
				//
				selectionViewer.setSelection(new StructuredSelection(selectionList));
			}
		}
	}

	/**
	 * This is for implementing {@link IEditorPart} and simply tests the command stack.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isDirty() {
		return ((BasicCommandStack) editingDomain.getCommandStack()).isSaveNeeded();
	}

	/**
	 * This is for implementing {@link IRevertablePart}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void doRevertGen() {
		DiagnosticDecorator.cancel(editingDomain);

		ResourceSet resourceSet = editingDomain.getResourceSet();
		List<Resource> resources = resourceSet.getResources();
		List<Resource> unloadedResources = new ArrayList<>();
		updateProblemIndication = false;
		for (int i = 0; i < resources.size(); ++i) {
			Resource resource = resources.get(i);
			if (resource.isLoaded()) {
				resource.unload();
				unloadedResources.add(resource);
			}
		}

		resourceToDiagnosticMap.clear();
		for (Resource resource : unloadedResources) {
			try {
				resource.load(resourceSet.getLoadOptions());
			} catch (IOException exception) {
				if (!resourceToDiagnosticMap.containsKey(resource)) {
					resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
				}
			}
		}

		editingDomain.getCommandStack().flush();

		if (AdapterFactoryEditingDomain.isStale(editorSelection)) {
			setSelection(StructuredSelection.EMPTY);
		}

		updateProblemIndication = true;
		updateProblemIndication();
	}

	@Override
	public void doRevert() {
		doReset(true);
	}

	protected void doReset(boolean revert) {
		resetAnalysis();
		if (revert) {
			doRevertGen();
		}
		analyzer = new AtomicReference<>();
		Analysis analysis = getAnalysis();
		if (analysis != null) {
			analyzer.set(new AnalyzeHandler.Analyzer(analysis));
		}
		setActivePage(selectionViewer);
	}

	protected void analyzeIfNeeded() {
		Analyzer analyzer = this.analyzer.getAndSet(null);
		if (analyzer != null && analyzer.getBuildModelLocation() != null) {
			Shell shell = analysisViewer.getControl().getShell();
			BaseHandler.checkStaleCache(shell);
			LastModifiedUpdater lastModifiedUpdater = new LastModifiedHandler.LastModifiedUpdater(
					analyzer.getOriginalAnalysis());
			lastModifiedUpdater.perform(shell);
			analyzer.perform(shell);
		}
	}

	protected void resetAnalysis() {
		ResourceSet resourceSet = editingDomain.getResourceSet();
		EList<Resource> resources = resourceSet.getResources();
		for (Iterator<Resource> it = resources.iterator(); it.hasNext();) {
			Resource resource = it.next();
			String scheme = resource.getURI().scheme();
			if ("readonly".equals(scheme) || "p2aggr".equals(scheme)) {
				resource.getContents().clear();
				it.remove();
			}
		}
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		setReadOnly(true);
		doSaveGen(progressMonitor);
	}

	private void setReadOnly(boolean clean) {
		ResourceSet resourceSet = editingDomain.getResourceSet();
		URIConverter uriConverter = resourceSet.getURIConverter();
		List<Resource> resources = resourceSet.getResources();
		Map<Resource, Boolean> resourceToReadOnlyMap = editingDomain.getResourceToReadOnlyMap();
		for (int i = 1; i < resources.size(); ++i) {
			Resource resource = resources.get(i);
			if (clean && !uriConverter.exists(resource.getURI(), Map.of())) {
				resource.unload();
				resources.remove(i--);
			} else {
				resourceToReadOnlyMap.put(resource, true);
			}
		}
	}

	/**
	 * This is for implementing {@link IEditorPart} and simply saves the model file.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void doSaveGen(IProgressMonitor progressMonitor) {
		// Save only resources that have actually changed.
		//
		final Map<Object, Object> saveOptions = new HashMap<>();
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		saveOptions.put(Resource.OPTION_LINE_DELIMITER, Resource.OPTION_LINE_DELIMITER_UNSPECIFIED);

		// Do the work within an operation because this is a long running activity that modifies the workbench.
		//
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
			// This is the method that gets invoked when the operation runs.
			//
			@Override
			public void execute(IProgressMonitor monitor) {
				// Save the resources to the file system.
				//
				boolean first = true;
				List<Resource> resources = editingDomain.getResourceSet().getResources();
				for (int i = 0; i < resources.size(); ++i) {
					Resource resource = resources.get(i);
					if ((first || !resource.getContents().isEmpty() || isPersisted(resource))
							&& !editingDomain.isReadOnly(resource)) {
						try {
							long timeStamp = resource.getTimeStamp();
							resource.save(saveOptions);
							if (resource.getTimeStamp() != timeStamp) {
								savedResources.add(resource);
							}
						} catch (Exception exception) {
							resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
						}
						first = false;
					}
				}
			}
		};

		updateProblemIndication = false;
		try {
			// This runs the options, and shows progress.
			//
			new ProgressMonitorDialog(getSite().getShell()).run(true, false, operation);

			// Refresh the necessary state.
			//
			((BasicCommandStack) editingDomain.getCommandStack()).saveIsDone();
			firePropertyChange(IEditorPart.PROP_DIRTY);
		} catch (Exception exception) {
			// Something went wrong that shouldn't.
			//
			AggregationAnalyzerEditorPlugin.INSTANCE.log(exception);
		}
		updateProblemIndication = true;
		updateProblemIndication();
	}

	/**
	 * This returns whether something has been persisted to the URI of the specified resource.
	 * The implementation uses the URI converter from the editor's resource set to try to open an input stream.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected boolean isPersisted(Resource resource) {
		boolean result = false;
		try {
			InputStream stream = editingDomain.getResourceSet().getURIConverter().createInputStream(resource.getURI());
			if (stream != null) {
				result = true;
				stream.close();
			}
		} catch (IOException e) {
			// Ignore
		}
		return result;
	}

	/**
	 * This always returns true because it is not currently supported.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * This also changes the editor's input.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void doSaveAs() {
		SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
		saveAsDialog.open();
		IPath path = saveAsDialog.getResult();
		if (path != null) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (file != null) {
				doSaveAs(URI.createPlatformResourceURI(file.getFullPath().toString(), true), new FileEditorInput(file));
			}
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void doSaveAs(URI uri, IEditorInput editorInput) {
		(editingDomain.getResourceSet().getResources().get(0)).setURI(uri);
		setInputWithNotify(editorInput);
		setPartName(editorInput.getName());
		IProgressMonitor progressMonitor = getActionBars().getStatusLineManager() != null
				? getActionBars().getStatusLineManager().getProgressMonitor()
				: new NullProgressMonitor();
		doSave(progressMonitor);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void gotoMarker(IMarker marker) {
		List<?> targetObjects = markerHelper.getTargetObjects(editingDomain, marker);
		if (!targetObjects.isEmpty()) {
			setSelectionToViewer(targetObjects);
		}
	}

	/**
	 * This is called during startup.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) {
		setSite(site);
		setInputWithNotify(editorInput);
		setPartName(editorInput.getName());
		site.setSelectionProvider(this);
		site.getPage().addPartListener(partListener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener,
				IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFocus() {
		getControl(getActivePage()).setFocus();
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to return this editor's overall selection.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ISelection getSelection() {
		return editorSelection;
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to set this editor's overall selection.
	 * Calling this result will notify the listeners.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSelection(ISelection selection) {
		editorSelection = selection;

		for (ISelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
		setStatusLineManager(selection);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStatusLineManager(ISelection selection) {
		IStatusLineManager statusLineManager = currentViewer != null && currentViewer == contentOutlineViewer
				? contentOutlineStatusLineManager
				: getActionBars().getStatusLineManager();

		if (statusLineManager != null) {
			if (selection instanceof IStructuredSelection) {
				Collection<?> collection = ((IStructuredSelection) selection).toList();
				switch (collection.size()) {
					case 0: {
						statusLineManager.setMessage(getString("_UI_NoObjectSelected"));
						break;
					}
					case 1: {
						String text = new AdapterFactoryItemDelegator(adapterFactory)
								.getText(collection.iterator().next());
						statusLineManager.setMessage(getString("_UI_SingleObjectSelected", text));
						break;
					}
					default: {
						statusLineManager
								.setMessage(getString("_UI_MultiObjectSelected", Integer.toString(collection.size())));
						break;
					}
				}
			} else {
				statusLineManager.setMessage("");
			}
		}
	}

	/**
	 * This looks up a string in the plugin's plugin.properties file.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static String getString(String key) {
		return AggregationAnalyzerEditorPlugin.INSTANCE.getString(key);
	}

	/**
	 * This looks up a string in plugin.properties, making a substitution.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static String getString(String key, Object s1) {
		return AggregationAnalyzerEditorPlugin.INSTANCE.getString(key, new Object[] { s1 });
	}

	/**
	 * This implements {@link org.eclipse.jface.action.IMenuListener} to help fill the context menus with contributions from the Edit menu.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void menuAboutToShowGen(IMenuManager menuManager) {
		((IMenuListener) getEditorSite().getActionBarContributor()).menuAboutToShow(menuManager);
	}

	@Override
	public void menuAboutToShow(IMenuManager menuManager) {
		menuAboutToShowGen(menuManager);
		if (currentViewer instanceof StructuredViewer) {
			menuManager.insertAfter("edit", new Separator("show-in-end"));
			menuManager.insertAfter("edit", new Separator("show-in"));
			createShowInBrowserActions(((StructuredViewer) currentViewer).getStructuredSelection(), menuManager);
			createShowInActions((StructuredViewer) currentViewer, menuManager);
		}
	}

	protected void createShowInActions(StructuredViewer viewer, IMenuManager manager) {
		IStructuredSelection selection = viewer.getStructuredSelection();
		MenuManager showInMenu = new MenuManager("Show In");
		manager.insertAfter("show-in", showInMenu);
		for (Function<IStructuredSelection, ShowInAction> factory : showInActionFactories) {
			final ShowInAction action = factory.apply(selection);
			if (action.getViewer() != currentViewer) {
				showInMenu.add(action);
			}
		}
	}

	protected void createShowInBrowserActions(IStructuredSelection selection, IMenuManager manager) {
		MenuManager showInBrowserMenu = new MenuManager("Show In Browser");
		manager.insertAfter("show-in", showInBrowserMenu);
		for (Object object : selection.toArray()) {
			handleShowInBrowser(object, showInBrowserMenu);
		}
	}

	protected void handleShowInBrowser(Object object, IMenuManager showInBrowserMenu) {
		if (object instanceof Dependency) {
			Dependency dependency = (Dependency) object;
			addShowInBrowserAction(
					URI.createURI(
							AnalyzeHandler.getURL(dependency.getGroupId().toString(), dependency.getArtifactId())),
					showInBrowserMenu);
		} else if (object instanceof EObject) {
			EObject eObject = (EObject) object;
			handleShowInBrowser(eObject, showInBrowserMenu);
			showInBrowserMenu.add(new Separator());
			for (EObject eContent : eObject.eContents()) {
				handleShowInBrowser(eContent, showInBrowserMenu);
				showInBrowserMenu.add(new Separator());
			}
		} else if (object instanceof WithLink) {
			String url = ((WithLink) object).getURL();
			if (url != null) {
				addShowInBrowserAction(URI.createURI(url), showInBrowserMenu);
			}
		}
	}

	protected void handleShowInBrowser(EObject eObject, IMenuManager showInBrowserMenu) {
		for (EAttribute eAttribute : eObject.eClass().getEAllAttributes()) {
			if (eAttribute.getEType().getInstanceClass() == URI.class) {
				URI uri = (URI) eObject.eGet(eAttribute);
				if (uri != null) {
					addShowInBrowserAction(uri, showInBrowserMenu);
				}
			}
		}
	}

	protected void addShowInBrowserAction(URI uri, IMenuManager showInBrowserMenu) {
		showInBrowserMenu.add(new Action(uri.toString()) {
			@Override
			public void run() {
				String url = uri.toString();
				if (url.startsWith("https://git.eclipse.org/r/")) {
					url = url.replace("/r/", "/c/") + ".git";
				}
				openInBrowser(url);
			}
		});
	}

	public String getDependenciesSVG() {
		return convert(graphViewer.getGraphControl(), getAnalysis());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EditingDomainActionBarContributor getActionBarContributor() {
		return (EditingDomainActionBarContributor) getEditorSite().getActionBarContributor();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IActionBars getActionBars() {
		return getActionBarContributor().getActionBars();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AdapterFactory getAdapterFactory() {
		return adapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void dispose() {
		updateProblemIndication = false;

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);

		getSite().getPage().removePartListener(partListener);

		adapterFactory.dispose();

		if (getActionBarContributor().getActiveEditor() == this) {
			getActionBarContributor().setActiveEditor(null);
		}

		for (PropertySheetPage propertySheetPage : propertySheetPages) {
			propertySheetPage.dispose();
		}

		if (contentOutlinePage != null) {
			contentOutlinePage.dispose();
		}

		super.dispose();
	}

	/**
	 * Returns whether the outline view should be presented to the user.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected boolean showOutlineView() {
		return false;
	}

	@SuppressWarnings("unused")
	private void breakpoint() {
		// In some lambdas one cannot set a breakpoint so call this method and set the breakpoint here.
	}

	public static <K, V> Set<V> getSet(Map<K, Set<V>> map, K key) {
		Set<V> set = map.get(key);
		if (set == null) {
			set = new LinkedHashSet<>();
			map.put(key, set);
		}

		return set;
	}

	public static <K, V> boolean add(Map<K, Set<V>> map, K key, V value) {
		Set<V> set = getSet(map, key);
		return set.add(value);
	}

	static void setExpandedSelection(TreeViewer treeViewer, IStructuredSelection selection) {
		Tree tree = treeViewer.getTree();
		tree.setRedraw(false);
		Object[] elements = selection.toArray();
		Object[] expandedElements = treeViewer.getExpandedElements();
		List<Object> allExpandedElements = new ArrayList<>();
		allExpandedElements.addAll(Arrays.asList(elements));
		allExpandedElements.addAll(Arrays.asList(expandedElements));
		treeViewer.setExpandedElements(allExpandedElements.toArray());
		ITreeContentProvider provider = (ITreeContentProvider) treeViewer.getContentProvider();
		for (Object element : elements) {
			Object[] children = provider.getChildren(element);
			if (children.length != 0) {
				treeViewer.setSelection(new StructuredSelection(children[children.length - 1]), true);
			}
		}
		treeViewer.setSelection(selection, true);
		tree.setRedraw(true);
	}

	static String getToolTip(Object object) {
		if (object instanceof ContributionAnalysis) {
			EList<Project> projects = ((ContributionAnalysis) object).getProjects();
			if (!projects.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				for (Project project : projects) {
					new Object() {
						void visit(String indent, Project project) {
							builder.append(indent);
							builder.append(project.getName());
							URI version = project.getVersion();
							if (version != null) {
								String lastSegment = version.lastSegment();
								if (lastSegment != null) {
									builder.append(" - ").append(lastSegment);
								}
							}
							Date releaseDate = project.getReleaseDate();
							if (releaseDate != null) {
								builder.append(" ~ ").append(AnalyzerUtil.format(releaseDate));
							}
							builder.append('\n');

							for (GitRepository gitRepository : project.getRepositories()) {
								builder.append(indent).append("  ").append(gitRepository.getURI());
								Float ageInDays = AnalyzerUtil.getAgeInDays(gitRepository);
								if (ageInDays != null) {
									builder.append(" - " + (int) (float) ageInDays);
								}
								builder.append('\n');
							}

							for (Project subproject : project.getSubprojects()) {
								visit(indent + "  ", subproject);
							}
						}
					}.visit("", project);
				}
				return builder.toString();
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private class MyDialog extends Dialog {
		private TreeViewer viewer;

		protected MyDialog(Shell parentShell) {
			super(parentShell);
			setBlockOnOpen(false);
			setShellStyle(SWT.SHELL_TRIM | SWT.MODELESS);
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("Strict Requirements");
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
		}

		protected void setInput(InstallableUnitAnalysis element) {
			viewer.setInput(new ItemProvider(
					Collections.singleton(AnalyzeHandler.Analyzer.computeStrictlyRequiredBy(element))));
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);

			Tree tree = new Tree(composite, SWT.MULTI);
			viewer = new TreeViewer(tree);

			viewer.setAutoExpandLevel(2);
			viewer.setUseHashlookup(true);
			viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
			viewer.setLabelProvider(new AdapterFactoryLabelProvider.ColorProvider(adapterFactory, viewer));

			composite.setLayout(new FillLayout());

			return composite;
		}
	}

	protected abstract class ShowInAction extends Action {
		private final StructuredViewer viewer;
		private Object target;

		public ShowInAction(String text, IStructuredSelection selection, StructuredViewer viewer) {
			super(text);
			this.viewer = viewer;
			handleSelection(selection);
		}

		@Override
		public boolean isEnabled() {
			return target != null;
		}

		@Override
		public void run() {
			analyzeIfNeeded();
			IContentProvider contentProvider = viewer.getContentProvider();
			if (contentProvider instanceof IStructuredContentProvider) {
				IStructuredContentProvider structuredContentProvider = (IStructuredContentProvider) contentProvider;
				Object input = viewer.getInput();
				List<TreePath> paths = new ArrayList<>();
				for (Object object : structuredContentProvider.getElements(input)) {
					TreePath path = new TreePath(new Object[] { object });
					if (matches(object)) {
						setSelection(new StructuredSelection(object));
						return;
					}
					paths.add(path);
				}

				if (contentProvider instanceof ITreeContentProvider) {
					ITreeContentProvider treeContentProvider = (ITreeContentProvider) contentProvider;
					find(treeContentProvider, paths);
				}
			}
		}

		protected boolean find(ITreeContentProvider treeContentProvider, List<TreePath> paths) {
			List<TreePath> nestedPaths = new ArrayList<>();
			for (TreePath treePath : paths) {
				Object[] children = treeContentProvider.getChildren(treePath.getLastSegment());
				for (Object child : children) {
					TreePath childPath = treePath.createChildPath(child);
					if (matches(child)) {
						setSelection(new TreeSelection(childPath));
						return true;
					}
					nestedPaths.add(childPath);
				}
			}

			if (!nestedPaths.isEmpty()) {
				return find(treeContentProvider, nestedPaths);
			}

			return false;
		}

		protected void setSelection(IStructuredSelection selection) {
			if (viewer instanceof TreeViewer) {
				setExpandedSelection((TreeViewer) viewer, selection);
			} else {
				viewer.setSelection(selection);
			}

			setActivePage(viewer);
		}

		protected boolean matches(Object object) {
			if (object instanceof TreeNode) {
				return matches(((TreeNode) object).getData());
			} else if (object instanceof IWrapperItemProvider) {
				return matches(((IWrapperItemProvider) object).getValue());
			} else if (object instanceof ContributionAnalysis) {
				if (target instanceof ContributionAnalysis) {
					return Objects.equals(((ContributionAnalysis) object).getLabel(),
							((ContributionAnalysis) target).getLabel());
				}
			} else if (object instanceof ProvidedCapabilityWrapper) {
				return ((ProvidedCapabilityWrapper) object).getGenuine() == target;
			} else if (object instanceof RequirementWrapper) {
				return ((RequirementWrapper) object).getGenuine() == target;
			} else if (object instanceof IUPresentation) {
				return Objects.equals(((IUPresentation) object).getInstallableUnit(), target);
			} else if (object instanceof InstallableUnitAnalysis) {
				IInstallableUnit installableUnit = ((InstallableUnitAnalysis) object).getInstallableUnit();
				if (target instanceof IInstallableUnit) {
					return installableUnit == target;
				} else if (target instanceof InstallableUnitAnalysis) {
					return installableUnit == ((InstallableUnitAnalysis) target).getInstallableUnit();
				} else if (target instanceof IUPresentation) {
					return ((IUPresentation) target).getInstallableUnit() == installableUnit;
				}
			} else if (object instanceof RequirementAnalysis) {
				if (target instanceof IRequirement) {
					return ((RequirementAnalysis) object).getRequirement() == target;
				} else if (target instanceof RequirementAnalysis) {
					return ((RequirementAnalysis) object).getRequirement() == ((RequirementAnalysis) target)
							.getRequirement();
				}
			} else if (object instanceof CapabilityAnalysis) {
				if (target instanceof IProvidedCapability) {
					return ((CapabilityAnalysis) object).getCapability() == target;
				} else if (target instanceof CapabilityAnalysis) {
					return ((CapabilityAnalysis) object).getCapability() == ((CapabilityAnalysis) target)
							.getCapability();
				}
			} else if (object instanceof CapabilityResolution) {
				if (target instanceof CapabilityResolution) {
					return ((CapabilityResolution) object).getRequirement()
							.getRequirement() == ((CapabilityResolution) target).getRequirement().getRequirement();
				}
			}

			return Objects.equals(target, object);
		}

		protected void setTarget(Object target) {
			this.target = target;
		}

		protected void handleSelection(IStructuredSelection selection) {
			if (selection.size() == 1) {
				Object element = selection.getFirstElement();
				if (element instanceof TreeNode) {
					handleSelection(((TreeNode) element).getData());
				} else if (element instanceof IWrapperItemProvider) {
					handleSelection(((IWrapperItemProvider) element).getValue());
				} else {
					handleSelection(element);
				}
			}
		}

		abstract void handleSelection(Object element);

		public StructuredViewer getViewer() {
			return viewer;
		}
	}

	private static void openInBrowser(String url) {
		try {
			PlatformUI.getWorkbench().getBrowserSupport()
					.createBrowser(IWorkbenchBrowserSupport.AS_EXTERNAL, null, "External", "").openURL(new URL(url));
		} catch (PartInitException | MalformedURLException e) {
			AggregationAnalyzerEditorPlugin.INSTANCE.log(e);
		}
	}

	@SuppressWarnings("restriction")
	private static boolean isGMFAvailable() {
		try {
			org.eclipse.gmf.runtime.draw2d.ui.render.awt.internal.svg.export.GraphicsSVG.class.toString();
			return true;
		} catch (Throwable throwable) {
			return false;
		}
	}

	private static void export(Graph graph, Analysis analysis) {
		try {
			Path createTempFile = Files.createTempFile("Dependencies", ".svg");
			Files.writeString(createTempFile, convert(graph, analysis));
			java.net.URI uri = createTempFile.toUri();
			System.out.println("Created: " + uri);
			openInBrowser(uri.toString());
		} catch (IOException e) {
			AggregationAnalyzerEditorPlugin.INSTANCE.log(e);
		}
	}

	@SuppressWarnings("restriction")
	private static String convert(Graph graph, Analysis analysis) {
		ScalableFigure root = graph.getRootLayer();
		var normalizer = new Object() {
			private final double scale = root.getScale();
			private final int layoutWidth = analysis.getLayoutWidth();
			private final int layoutHeight = analysis.getLayoutHeight();

			public void normalize() {
				root.setScale(1.0);
				if (layoutWidth > 0 && layoutHeight > 0) {
					graph.setPreferredSize(layoutWidth, layoutHeight);
					root.setBounds(getBounds());
				}

				graph.applyLayoutNow();

				// Give the UI some time to actually show the layout changes.
				Display display = graph.getDisplay();
				display.syncExec(() -> {
					long timeMillis = System.currentTimeMillis();
					while (display.readAndDispatch()) {
						display.sleep();
						if (System.currentTimeMillis() - timeMillis > 1000) {
							break;
						}
					}
				});
			}

			public void reset() {
				root.setScale(scale);
				if (layoutWidth > 0 && layoutHeight > 0) {
					graph.setPreferredSize(-1, -1);
				}
			}

			public org.eclipse.draw2d.geometry.Rectangle getBounds() {
				return layoutWidth > 0 && layoutHeight > 0
						? new org.eclipse.draw2d.geometry.Rectangle(0, 0, layoutWidth, layoutHeight)
						: root.getBounds();
			}
		};

		normalizer.normalize();

		org.eclipse.gmf.runtime.draw2d.ui.render.awt.internal.svg.export.GraphicsSVG graphics = org.eclipse.gmf.runtime.draw2d.ui.render.awt.internal.svg.export.GraphicsSVG
				.getInstance(normalizer.getBounds());
		SVGGraphics2D svgGraphics2D = graphics.getSVGGraphics2D();
		svgGraphics2D.setExtensionHandler(new DefaultExtensionHandler());
		root.paint(graphics);

		normalizer.reset();

		try (StringWriter stringWriter = new StringWriter()) {
			svgGraphics2D.stream(stringWriter);
			stringWriter.flush();
			return stringWriter.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class AdapterFactoryContentProviderWithoutSearchProperties extends AdapterFactoryContentProvider {
		private long lastCheck;
		private boolean noProperties;

		public AdapterFactoryContentProviderWithoutSearchProperties(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		@Override
		public IPropertySource getPropertySource(Object object) {
			long check = System.currentTimeMillis();
			if (check - lastCheck > 1000) {
				lastCheck = check;
				noProperties = false;
				StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
				for (StackTraceElement element : stackTrace) {
					if (element.getClassName().contains("FindAndReplaceTarget")) {
						noProperties = true;
						return null;
					}
				}
			} else if (noProperties) {
				lastCheck = check;
				return null;
			}
			return super.getPropertySource(object);
		}
	}
}