package concad.core.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import concad.core.graph.CallGraph;
import concad.core.util.PluginLogger;
import concad.core.visitor.VisitorCallGraph;

public class BuilderJob extends Job {

	private static Map<IProject, CallGraph> mapCallGraphs;
	private IProject project;
	private IResourceDelta delta;

	static {
		mapCallGraphs = new HashMap<IProject, CallGraph>();
	}

	private BuilderJob(String name) {
		super(name);
		System.out.println("BuilderJob");
		PluginLogger.logInfo("BuilderJob / constructor(String)");
	}

	public BuilderJob(String name, IProject project) {
		this(name);
		System.out.println("BuilderJob");
		PluginLogger.logInfo("BuilderJob / constructor(String, IProject)");
		setProject(project);
		addProjectToList(project);
	}

	public BuilderJob(String name, IResourceDelta delta) {
		this(name);
		System.out.println("BuilderJob");
		PluginLogger
				.logInfo("BuilderJob / constructor(String, IResourceDelta)");
		setDelta(delta);
	}

	private static Map<IProject, CallGraph> getMapCallGraphs() {
		PluginLogger.logInfo("BuilderJob / getMapCallGraphs()");
		return mapCallGraphs;
	}

	private IProject getProject() {
		return project;
	}

	private void setProject(IProject project) {
		this.project = project;
	}

	private IResourceDelta getDelta() {
		return delta;
	}

	private void setDelta(IResourceDelta delta) {
		this.delta = delta;
	}

	private void addProjectToList(IProject project) {
		PluginLogger.logInfo("BuilderJob / addProjectToList(IProject)");
		if (!getMapCallGraphs().containsKey(project)) {
			getMapCallGraphs().put(project, new CallGraph());
		}
	}

	private CallGraph getCallGraph() {
		PluginLogger.logInfo("BuilderJob / getCallGraph()");
		if (null != getDelta()) {
			return getMapCallGraphs()
					.get(getDelta().getResource().getProject());
		} else {
			return getMapCallGraphs().get(getProject());
		}
	}

	private boolean userCanceledProcess(IProgressMonitor monitor) {
		return ((null != monitor) && (monitor.isCanceled()));
	}

	public void run() {
		// setPriority(Job.INTERACTIVE); // The highest priority.
		schedule();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		PluginLogger.logInfo("BuilderJob / run()");
		System.out.println("BuilderJobRun");
		try {
			// if (manager.shouldPerformVerifications()) {
		

			// 02 - Get the CallGraph instance for this project.
			CallGraph callGraph = getCallGraph();
			if (null != callGraph) {
				monitor.beginTask("Task", IProgressMonitor.UNKNOWN);

				@SuppressWarnings("unused")
				List<IResource> resourcesUpdated = new ArrayList<IResource>();
				if (!userCanceledProcess(monitor)) {
					// 03 - Use the VISITOR pattern to create/populate the
					// call graph.
					resourcesUpdated = createCallGraph(callGraph, monitor);
				} else {
					return Status.CANCEL_STATUS;
				}

			} else {
				String projectName = (null != getProject()) ? getProject()
						.getName() : "";
				PluginLogger.logError(String.format("Call graph does not contain the project",
						projectName), null);
			}
		
			// }
		} catch (CoreException e) {
			PluginLogger.logError(e);
			return e.getStatus();
		} catch (Exception e) {
			PluginLogger.logError(e);
			return Status.CANCEL_STATUS;
		} finally {
			if (null != monitor) {
				monitor.done();
			}
		}

		return Status.OK_STATUS;
	}

	private List<IResource> createCallGraph(CallGraph callGraph,
			IProgressMonitor monitor) throws CoreException {
		List<IResource> resourcesUpdated = new ArrayList<IResource>();
		PluginLogger.logInfo("BuilderJob / createCallGraph(CallGraph, IProgressMonitor)");

		System.out.println("BuilderJobCallGraph");
		VisitorCallGraph visitorCallGraph = new VisitorCallGraph(callGraph,
				monitor);

		if (null != getDelta()) {

			// 03 - Use the VISITOR pattern to create/populate the call graph.
			resourcesUpdated = visitorCallGraph.run(getDelta());

		} else if (null != getProject()) {

			// 03 - Use the VISITOR pattern to create/populate the call graph.
			resourcesUpdated = visitorCallGraph.run(getProject());

		}

		return resourcesUpdated;
	}

}
