package concad.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import concad.core.design.DesignFlawManager;
import concad.metrics.storage.InvokingCache;
import concad.priotization.RankingManager;
import concad.ui.views.ConCADView;

public class FindDesignFlawAction implements IObjectActionDelegate{
	
	private ISelection selection;
	private IWorkbenchWindow window;	
	
	@Override
	public void run(IAction action) {
		try {
			window.run(false, true, new IRunnableWithProgress() {
		         public void run(IProgressMonitor monitor)
		            throws InvocationTargetException, InterruptedException {
		            monitor.beginTask("Finding code anomaly", 14);
		           
		            IProject selectedProject=(IProject)(((StructuredSelection)selection).getFirstElement());
		            DesignFlawManager dfManager = DesignFlawManager.getInstance();
					dfManager.initialize();
					InvokingCache.getInstance().initialize();
					try {
						long time_start, time_end;
						time_start = System.currentTimeMillis();

						monitor.subTask("Calculating metrics");
						dfManager.calculateMetricsCode(selectedProject);
						monitor.worked(4);
						monitor.subTask("Calculating cross attributes");
						dfManager.calculateAditionalMetrics();
						monitor.worked(4);						
						time_end = System.currentTimeMillis();
						
						System.out.println("the task has taken "+ ( time_end - time_start ) +" milliseconds");

						
						dfManager.detectCodeSmells(selectedProject.getFullPath().toString());
						
						monitor.worked(4);
						
					} catch (Exception e) {
						System.out.println(e);
						/*MessageDialog.openInformation(
								shell,
								"ConcadMenu",
								"Calcular metricas was executed.");*/
					}
					
					monitor.subTask("Calculating ranking");
					
					dfManager.countCodeSmellsDebug();
					
					RankingManager.getInstance().setSmells(dfManager.getSmells(),selectedProject.getFullPath().toString());
					monitor.worked(2);
		            
					
					
		            /**for (int i = 20; i > 0; --i) {
		                monitor.subTask("seconds left = " + i);
		                Thread.sleep(1000);
		                monitor.worked(1);
		             }**/
					ConCADView view;
					try {
						view = (ConCADView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("concad.ui.views.ConCADView");
						view.updateView();
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
		            monitor.done();
		         }
		      });
			
			
		} catch ( InvocationTargetException | InterruptedException e) {}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		window=targetPart.getSite().getWorkbenchWindow();
	}

}