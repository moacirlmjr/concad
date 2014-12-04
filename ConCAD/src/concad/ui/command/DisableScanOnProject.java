package concad.ui.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import concad.core.design.DesignFlawManager;
import concad.core.smells.interfaces.CodeSmell;
import concad.ui.views.ConCADView;

/**
 * If the current selected project is being monitored, remove it from the list.
 * 
 * @author Luciano Sampaio
 */
public class DisableScanOnProject extends AbstracCommand {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the list(unique elements) of selected projects by the developer.
		// Even if he/she selected a file.
		System.out.println("Disable");
		List<IProject> selectedProjects = getSelectedProjects(event);
		DesignFlawManager dfManager = DesignFlawManager.getInstance();
		for (IProject project : selectedProjects) {
			dfManager.deleteSmellMarker(project);
			try {
				List<CodeSmell> listSmells = new ArrayList<>(dfManager.getSmells());
				int i = 0;
				for(CodeSmell s:listSmells){
					if(s.getMainClassName().contains(project.getName()+".")){
						dfManager.getSmells().remove(i);
						i-=1;
					}
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ConCADView view;
			try {
				view = (ConCADView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView(ConCADView.ID);
				view.updateView();
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		// If the collection is empty there is nothing to do.
		if (!selectedProjects.isEmpty()) {
			// Save the list back to the preference store.
			removeProjectsFromListOfMonitoredProjects(selectedProjects);
		}

		return null;
	}

}
