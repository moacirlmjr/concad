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
import concad.core.log.PluginLogger;
import concad.core.util.Timer;
import concad.core.visitor.VisitorCallGraph;
import concad.ui.l10n.Message;

/**
 * This class performs its operations in a different(new) thread from the UI
 * Thread. So the user will not be blocked.
 * 
 * @Author: Luciano Sampaio
 * @Date: 2014-05-07
 * @Version: 01
 */
public class BuilderJob extends Job {

	/**
	 * This object contains all the methods, variables and their interactions,
	 * on the project that is being analyzed. At any given time, we should only
	 * have one call graph per project.
	 */
	private static Map<IProject, CallGraph> mapCallGraphs;
	private IProject project;
	private IResourceDelta delta;

	static {
		mapCallGraphs = new HashMap<IProject, CallGraph>();
	}

	private BuilderJob(String name) {
		super(name);
		System.out.println("BuilderJob");
		PluginLogger.logInfo("BuilderJob / constructor()");
	}

	public BuilderJob(String name, IProject project) {
		this(name);
		System.out.println("BuilderJob");
		PluginLogger.logInfo("BuilderJob / constructor(IProject)");
		setProject(project);
		addProjectToList(project);
	}

	public BuilderJob(String name, IResourceDelta delta) {
		this(name);
		System.out.println("BuilderJob");
		PluginLogger.logInfo("BuilderJob / constructor(IResourceDelta)");
		setDelta(delta);
	}

	private static Map<IProject, CallGraph> getMapCallGraphs() {
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

	/**
	 * Add the project to the list of call graphs.
	 * 
	 * @param project
	 *            The project that will be added to the list.
	 */
	private void addProjectToList(IProject project) {
		System.out.println("BuilderJob");
		if (!getMapCallGraphs().containsKey(project)) {
			getMapCallGraphs().put(project, new CallGraph());
		}
	}

	/**
	 * Returns the call graph of the current project.
	 * 
	 * @return The call graph of the current project.
	 */
	private CallGraph getCallGraph() {
		System.out.println("BuilderJob");
		if (null != getDelta()) {
			return getMapCallGraphs().get(getDelta().getResource().getProject());
		} else {
			return getMapCallGraphs().get(getProject());
		}
	}

	/**
	 * Returns whether cancellation of current operation has been requested
	 * 
	 * @param reporter
	 * @return true if cancellation has been requested, and false otherwise.
	 */
	private boolean userCanceledProcess(IProgressMonitor monitor) {
		return ((null != monitor) && (monitor.isCanceled()));
	}

	public void run() {
		// setPriority(Job.INTERACTIVE); // The highest priority.
		schedule();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		System.out.println("BuilderJobRun");
		try {
			// if (manager.shouldPerformVerifications()) {
			Timer timerCP = (new Timer("01 - Complete Process: ")).start();

			// 02 - Get the CallGraph instance for this project.
			CallGraph callGraph = getCallGraph();
			if (null != callGraph) {
				monitor.beginTask(Message.Plugin.TASK, IProgressMonitor.UNKNOWN);

				List<IResource> resourcesUpdated = new ArrayList<IResource>();
				if (!userCanceledProcess(monitor)) {
					// 03 - Use the VISITOR pattern to create/populate the
					// call graph.
					resourcesUpdated = createCallGraph(callGraph, monitor);
				} else {
					return Status.CANCEL_STATUS;
				}

			} else {
				String projectName = (null != getProject()) ? getProject().getName() : "";
				PluginLogger.logError(String.format(Message.Error.CALL_GRAPH_DOES_NOT_CONTAIN_PROJECT, projectName),
						null);
			}
			PluginLogger.logInfo(timerCP.stop().toString());
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

	private List<IResource> createCallGraph(CallGraph callGraph, IProgressMonitor monitor) throws CoreException {
		List<IResource> resourcesUpdated = new ArrayList<IResource>();
		System.out.println("BuilderJobCallGraph");
		VisitorCallGraph visitorCallGraph = new VisitorCallGraph(callGraph, monitor);

		if (null != getDelta()) {
			Timer timer = (new Timer("01.1 - Call Graph Delta: ")).start();
			// 03 - Use the VISITOR pattern to create/populate the call graph.
			resourcesUpdated = visitorCallGraph.run(getDelta());
			PluginLogger.logIfDebugging(timer.stop().toString());
		} else if (null != getProject()) {
			Timer timer = (new Timer("01.1 - Call Graph Project: ")).start();
			// 03 - Use the VISITOR pattern to create/populate the call graph.
			resourcesUpdated = visitorCallGraph.run(getProject());
			PluginLogger.logIfDebugging(timer.stop().toString());
		}

		return resourcesUpdated;
	}

}
