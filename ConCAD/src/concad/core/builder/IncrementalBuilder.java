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

import concad.core.log.PluginLogger;
import concad.ui.l10n.Message;

/**
 * This class is invoked when Eclipse is going to compile(build) the files in a
 * project that is being edited by the developer.
 * 
 * @Author: Luciano Sampaio
 * @Date: 2014-05-07
 * @Version: 01
 */
public class IncrementalBuilder extends IncrementalProjectBuilder {

	/**
	 * It will guarantee that only one job will be running at any given time.
	 */
	private static MutexRule rule = new MutexRule();

	/**
	 * A list with all the projects that were full built. This is important
	 * because sometimes Eclipse might call IncrementalBuild on a project that
	 * was not first full built. We need this information for the CallGraph.
	 */
	private static List<IProject> fullBuiltProjects;
	private BuilderJob jobProject;
	private BuilderJob jobDelta;

	static {
		reset();
	}

	/**
	 * In case the user change some settings, we have to reset this list and
	 * start over.
	 */
	public static void reset() {
		System.out.println("IncrementalBuilder Reset");
		PluginLogger.logInfo("IncrementalBuilder / reset()");
		fullBuiltProjects = new ArrayList<IProject>();
	}

	/**
	 * Add the project to the list of full built projects.
	 * 
	 * @param project
	 *            The project that will be added to the list.
	 */
	private void addProjectToFullBuiltList(IProject project) {
		if (!wasProjectFullBuilt(project)) {
			fullBuiltProjects.add(project);
		}
	}

	/**
	 * Checks if the project was already full built.
	 * 
	 * @param project
	 *            The project that will be checked.
	 * @return True if the project was already full built, otherwise false.
	 */
	private boolean wasProjectFullBuilt(IProject project) {
		return fullBuiltProjects.contains(project);
	}

	/**
	 * Cancel the job if the state is different from RUNNING, which means the
	 * job is sleeping or waiting.
	 * 
	 * @param job
	 *            The job that will be canceled if it is not running.
	 */
	private void cancelIfNotRunning(BuilderJob job) {
		if ((null != job) && (job.getState() != Job.RUNNING)) {
			job.cancel();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		try {
			PluginLogger.logInfo("IncrementalBuilder / build()");

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
		PluginLogger.logInfo("IncrementalBuilder / fullBuild()");
		addProjectToFullBuiltList(getProject());
		System.out.println("IncrementalBuilderFullBuild");
		cancelIfNotRunning(jobProject);
		jobProject = new BuilderJob(Message.Plugin.JOB, getProject());
		jobProject.setRule(rule);
		jobProject.run();
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		if (wasProjectFullBuilt(getProject())) {
			cancelIfNotRunning(jobDelta);
			PluginLogger.logInfo("IncrementalBuilder / incrementalBuild()");
			System.out.println("IncrementalBuilderIncrementalBuild");
			jobDelta = new BuilderJob(Message.Plugin.JOB, delta);
			jobDelta.setRule(rule);
			jobDelta.run();
		} else {
			// Sometimes Eclipse invokes the incremental build without ever
			// invoked the full build,
			// but we need at least one full build to create the full CallGraph.
			// After this first time we're OK.
			fullBuild(monitor);
		}
	}
}