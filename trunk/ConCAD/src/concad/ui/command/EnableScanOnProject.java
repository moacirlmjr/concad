package concad.ui.command;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import concad.core.design.DesignFlawManager;
import concad.metrics.storage.InvokingCache;
import concad.priotization.RankingManager;
import concad.ui.views.ConCADView;

/**
 * If the current selected project is not being monitored, add it to the list.
 * 
 * @author Luciano Sampaio
 */
public class EnableScanOnProject extends AbstracCommand {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the list(unique elements) of selected projects by the developer.
		// Even if he/she selected a file.
		System.out.println("Enable");
		List<IProject> selectedProjects = getSelectedProjects(event);
		DesignFlawManager dfManager = DesignFlawManager.getInstance();
		InvokingCache.getInstance().initialize();
		for (IProject selectedProject : selectedProjects) {
			try {
				dfManager.calculateMetricsCode(selectedProject);
				dfManager.calculateAditionalMetrics();

				dfManager.detectCodeSmells(selectedProject.getFullPath().toString());

			} catch (Exception e) {
				System.out.println(e);
				/*
				 * MessageDialog.openInformation( shell, "ConcadMenu",
				 * "Calcular metricas was executed.");
				 */
			}

			dfManager.countCodeSmellsDebug();

			RankingManager.getInstance().setSmells(dfManager.getSmells(), selectedProject.getFullPath().toString());
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
			addProjectsToListOfMonitoredProjects(selectedProjects);
		}

		return null;
	}

}
