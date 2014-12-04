package concad.core.nature;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import concad.core.constant.Constant;
import concad.core.log.PluginLogger;


public class NatureHandler {

	public void add(Collection<IProject> projects) throws CoreException {
		PluginLogger.logInfo("NatureHandler / add(Collection<IProject>)");
		for (IProject project : projects) {
			add(project);
		}
	}

	public void add(IProject project) throws CoreException {
		PluginLogger.logInfo("NatureHandler / add(IProject)");

		if ((project.isAccessible()) && (project.isOpen())) {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (Constant.NATURE_ID.equals(natures[i])) {
					// If the project already has the nature, there is nothing else to do.
					return;
				}
			}

			// Add the nature.
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = Constant.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		}
	}

	
	public void remove(Collection<IProject> projects) throws CoreException {
		PluginLogger.logInfo("NatureHandler / remove(Collection<IProject>)");
		for (IProject project : projects) {
			remove(project);
		}
	}

	public void remove(IProject project) throws CoreException {
		PluginLogger.logInfo("NatureHandler / remove(IProject)");
		if ((project.isAccessible()) && (project.isOpen())) {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (Constant.NATURE_ID.equals(natures[i])) {
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);
					return;
				}
			}
		}
	}

}
