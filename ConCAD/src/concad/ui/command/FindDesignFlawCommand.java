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

public class FindDesignFlawCommand extends AbstracCommand {

	@Override
	public final Object execute(final ExecutionEvent event) throws ExecutionException {

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

		return null;
	}

}