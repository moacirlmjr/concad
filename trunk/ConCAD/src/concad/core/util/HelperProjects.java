package concad.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import concad.Activator;
import concad.core.builder.IncrementalBuilder;
import concad.core.constant.Constant;
import concad.core.log.PluginLogger;


public abstract class HelperProjects {

	
	public static List<IProject> getProjectsInWorkspace() {
		// Returns the collection of projects which exist under this root. The projects can be open or closed.
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		// This collection only has Java projects which are also accessible and opened.
		List<IProject> javaProjects = new ArrayList<>(allProjects.length);

		for (IProject project : allProjects) {
			try {
				if ((project.isAccessible()) && (project.isOpen()) && (project.isNatureEnabled(Constant.JDT_NATURE))) {
					javaProjects.add(project);
				}
			} catch (CoreException e) {
				PluginLogger.logError(e);
			}
		}

		// Return the list of all projects in the current workspace.
		return javaProjects;
	}

	
	public static List<IProject> getMonitoredProjects() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		// Get the list of monitored projects split by the SEPARATOR constant.
		String storedMonitoredProjects = store.getString(Constant.PrefPageSecurityVulnerability.FIELD_MONITORED_PROJECTS);

		// Extract a collection (unique elements) from the string.
		List<String> projectsNames = Convert.fromStringToList(storedMonitoredProjects, Constant.SEPARATOR_RESOURCES_TYPE);

		// The list with the projects that are being monitored by our plug-in.
		List<IProject> listMonitoredProjects = new ArrayList<IProject>();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (String projectName : projectsNames) {
			listMonitoredProjects.add(root.getProject(projectName));
		}

		return listMonitoredProjects;
	}

	/**
	 * @param projects
	 */
	public static void setProjectsToListOfMonitoredProjects(List<IProject> projects) {
		// The collection of projects that are being monitored by our plug-in.
		List<IProject> monitoredProjects = getMonitoredProjects();

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		StringBuilder projectsToSave = new StringBuilder();
		for (IProject project : projects) {
			projectsToSave.append(project.getName()).append(Constant.SEPARATOR_RESOURCES_TYPE);
		}

		store.putValue(Constant.PrefPageSecurityVulnerability.FIELD_MONITORED_PROJECTS, projectsToSave.toString());

		// Add or remove the nature to the each project.
		//updateNatureOnProjects(monitoredProjects, projects);

		resetPluginState();
	}

	public static void resetPluginState() {
		// Reset the state of the plug-in to its initial state.
		IncrementalBuilder.reset();
	}

	/*private static void updateNatureOnProjects(List<IProject> oldProjects, List<IProject> newProjects) {
		System.out.println("Natureeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		// Create a difference from the old and the new list.
		List<IProject> projectsToAdd = new ArrayList<IProject>();
		projectsToAdd.addAll(newProjects);
		projectsToAdd.removeAll(oldProjects);

		List<IProject> projectsToRemove = new ArrayList<IProject>();
		projectsToRemove.addAll(oldProjects);
		projectsToRemove.removeAll(newProjects);

		try {
			NatureHandler2 handler = new NatureHandler2();
			handler.add(projectsToAdd);
			handler.remove(projectsToRemove);
		} catch (CoreException e) {
			PluginLogger.logError(e);
		}
	}*/

	
	public static void addProjectsToListOfMonitoredProjects(List<IProject> projects) {
		// If the collection is empty there is nothing to do.
		if ((null != projects) && (!projects.isEmpty())) {
			// The collection of projects that are being monitored by our plug-in.
			List<IProject> monitoredProjects = getMonitoredProjects();

			// Adds the selected projects to the list of monitored projects.
			// It should not allow repeated elements.
			for (IProject project : projects) {
				if (!monitoredProjects.contains(project)) {
					monitoredProjects.add(project);
				}
			}

			// Save the list back to the preference store.
			setProjectsToListOfMonitoredProjects(monitoredProjects);
		}
	}

	public static void removeProjectsFromListOfMonitoredProjects(List<IProject> projects) {
		// If the collection is empty there is nothing to do.
		if ((null != projects) && (!projects.isEmpty())) {
			// The collection of projects that are being monitored by our plug-in.
			List<IProject> monitoredProjects = getMonitoredProjects();

			// Removes the selected projects from the list of monitored projects.
			for (IProject project : projects) {
				monitoredProjects.remove(project);
			}

			// Save the list back to the preference store.
			setProjectsToListOfMonitoredProjects(monitoredProjects);
		}
	}

	
	public static List<IProject> getSelectedProjects(ExecutionEvent event) {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ISelection selection = window.getActivePage().getSelection();

		return getSelectedProjects(selection);
	}

	
	public static List<IProject> getSelectedProjects(ISelection selection) {
		List<IProject> projects = new ArrayList<IProject>();

		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection) selection).iterator(); iter.hasNext();) {
				// Translate the selected object into a project.
				IProject project = Convert.fromResourceToProject(iter.next());

				if ((null != project) && (!projects.contains(project))) {
					projects.add(project);
				}
			}
		}

		return projects;
	}

	

}
