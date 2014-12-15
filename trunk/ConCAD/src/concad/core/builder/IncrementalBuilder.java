package concad.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import concad.core.util.PluginLogger;


public class IncrementalBuilder extends IncrementalProjectBuilder {

	private static List<IProject> fullBuiltProjects;
	private BuilderJob jobProject;
	private BuilderJob jobDelta;

	static {
		reset();
	}

	public static void reset() {
		System.out.println("IncrementalBuilder Reset");
		PluginLogger.logInfo("IncrementalBuilder / reset()");
		fullBuiltProjects = new ArrayList<IProject>();
	}

	
	private void addProjectToFullBuiltList(IProject project) {
		if (!wasProjectFullBuilt(project)) {
			fullBuiltProjects.add(project);
		}
	}

	
	private boolean wasProjectFullBuilt(IProject project) {
		return fullBuiltProjects.contains(project);
	}

	private void cancelIfNotRunning(BuilderJob job) {
		if ((null != job) && (job.getState() != Job.RUNNING)) {
			job.cancel();
		}
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		try {
			PluginLogger.logInfo("IncrementalBuilder / build(int, Map, IProgressMonitor)");

			if (kind == FULL_BUILD) {
				fullBuild(monitor);
			} else if (kind == CLEAN_BUILD) {
				clean(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (null == delta) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
				}
			}
		} catch (CoreException e) {
			PluginLogger.logError(e);
		}

		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor) {
		PluginLogger.logInfo("IncrementalBuilder / fullBuild(IProgressMonitor)");
		addProjectToFullBuiltList(getProject());
		System.out.println("IncrementalBuilderFullBuild");
		cancelIfNotRunning(jobProject);
		jobProject = new BuilderJob("Job", getProject());		
		jobProject.run();
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		if (wasProjectFullBuilt(getProject())) {
			cancelIfNotRunning(jobDelta);
			PluginLogger.logInfo("IncrementalBuilder / incrementalBuild(IResourceDelta, IProgressMonitor)");
			System.out.println("IncrementalBuilderIncrementalBuild");
			jobDelta = new BuilderJob("Job", delta);
			jobDelta.run();
		} else {			
			fullBuild(monitor);
		}
	}
}