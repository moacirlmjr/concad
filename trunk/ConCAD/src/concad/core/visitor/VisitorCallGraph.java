package concad.core.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import concad.core.design.DesignFlawManager;
import concad.core.graph.CallGraph;
import concad.metrics.storage.InvokingCache;
import concad.priotization.RankingManager;
import concad.ui.views.ConCADView;

public class VisitorCallGraph implements IResourceVisitor, IResourceDeltaVisitor {

	private final List<IResource> resourcesUpdated;
	private final CallGraph callGraph;
	private IProgressMonitor progressMonitor;

	public VisitorCallGraph(CallGraph callGraph, IProgressMonitor monitor) {
		System.out.println("Visitor");
		resourcesUpdated = new ArrayList<IResource>();
		this.callGraph = callGraph;
		setProgressMonitor(monitor);
	}

	@SuppressWarnings("unused")
	private CallGraph getCallGraph() {
		return callGraph;
	}

	private final void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	private IProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	private boolean userCanceledProcess(IProgressMonitor monitor) {
		return ((null != monitor) && (monitor.isCanceled()));
	}

	@SuppressWarnings("unused")
	private void setSubTask(String taskName) {
		if (null != getProgressMonitor()) {
			getProgressMonitor().subTask(taskName);
		}
	}

	@SuppressWarnings("unused")
	private CompilationUnit parse(ICompilationUnit unit) {
		System.out.println("VisitorParse");
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // Parse.
	}

	public List<IResource> run(IProject project) throws CoreException {
		project.accept(this);

		return resourcesUpdated;
	}

	public List<IResource> run(IResourceDelta delta) throws CoreException {
		delta.accept(this);
		System.out.println("VisitorRun");
		return resourcesUpdated;
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();

		System.out.println("VisitorVisit");
		switch (delta.getKind()) {
		case IResourceDelta.REMOVED:
			// Delete old files, markers and etc related to this project.
			break;
		case IResourceDelta.ADDED:
		case IResourceDelta.CHANGED:
			if (resource.getFullPath().toString().contains(".java")) {
				return visit(resource);
			}
		}
		// Return true to continue visiting children.
		return true;
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		System.out.println("VisitorVisit2 " + resource.getName());
		if (!userCanceledProcess(getProgressMonitor())) {
			if (resource.getFullPath().toString().contains(".java")) {
				DesignFlawManager dfManager = DesignFlawManager.getInstance();
				InvokingCache.getInstance().initialize();
				try {
					dfManager.calculateMetricsCode(resource.getProject());
					dfManager.calculateAditionalMetrics();

					dfManager.detectCodeSmellsVisitor(resource);

					dfManager.countCodeSmellsDebug();

					RankingManager.getInstance().setSmells(dfManager.getSmells(), resource.getProject().toString());
					Display display = Display.getCurrent();
					if (display == null) {
						display = Display.getDefault();
					}
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							IWorkbench wb = PlatformUI.getWorkbench();
							ConCADView view;
							try {
								view = (ConCADView) wb.getActiveWorkbenchWindow().getActivePage()
										.showView(ConCADView.ID);
								view.updateView();
							} catch (PartInitException e) {
								e.printStackTrace();
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		} else {
			// The user has canceled the process.
			// Returns false to stop visiting the files.
			return false;
		}
	}
}
