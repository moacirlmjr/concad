package concad.ui.views;

import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import concad.priotization.RankingManager;
import concad.priotization.criteria.Criterion;
import concad.priotization.rankings.RankingCalculator;

public class RankingConfiguration extends Dialog {
	private ComboViewer comboViewer;
	private ListViewer listViewer;
	
	private ConCADView ConCADView;
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param currentProject 
	 * @param concadview 
	 */
	public RankingConfiguration(Shell parentShell, ConCADView concadview) {
		super(parentShell);
		
		this.ConCADView=concadview;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		
		comboViewer = new ComboViewer(container, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText("Select the way in which the ranking will be calculated");
		combo.setBounds(24, 46, 264, 22);
		
		
		Label lblSelectTheRankin = new Label(container, SWT.NONE);
		lblSelectTheRankin.setBounds(24, 26, 141, 14);
		lblSelectTheRankin.setText("Select ranking calculator");
		
		Label lblCriteria = new Label(container, SWT.NONE);
		lblCriteria.setBounds(323, 29, 59, 14);
		lblCriteria.setText("Criteria");
		
		listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
		List list = listViewer.getList();
		list.setBounds(323, 49, 298, 78);
		listViewer.setContentProvider(ArrayContentProvider.getInstance());
		listViewer.setLabelProvider(new LabelProvider() {
		  @Override
		  public String getText(Object element) {
		    if (element instanceof Criterion) {
		    	Criterion criterion = (Criterion) element;
		    	return criterion.getName();
		    }
		    return super.getText(element);
		  }
		});
		
		Button btnConfigure = new Button(container, SWT.NONE);
		btnConfigure.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				configurationButtonClicked();
			}
		});
		btnConfigure.setBounds(627, 53, 94, 28);
		btnConfigure.setText("Configure");
		
	
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LabelProvider() {
		  @Override
		  public String getText(Object element) {
		    if (element instanceof RankingCalculator) {
		    	RankingCalculator ranking = (RankingCalculator) element;
		      return ranking.getName();
		    }
		    return super.getText(element);
		  }
		});
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			  @Override
			  public void selectionChanged(SelectionChangedEvent event) {
			    IStructuredSelection selection = (IStructuredSelection) event
			      .getSelection();
			    if (selection.size() > 0){
			    	comboViewerChanged((RankingCalculator) selection.getFirstElement());
			    }
			  }
			}); 
		comboViewer.setInput(RankingManager.getInstance().getRankingsCalculators()); 
		
		
		int selectedIndex=RankingManager.getInstance().getPredefinedCalculatorIndex();
		combo.select(selectedIndex);
		listViewer.setInput(RankingManager.getInstance().getRankingsCalculators().get(selectedIndex).getCriteria()); 
		return container;
	}

	private void configurationButtonClicked() {
		
		if(((IStructuredSelection)(listViewer.getSelection())).size()>0){
			Criterion selectedCriterion=(Criterion) ((IStructuredSelection)(listViewer.getSelection())).getFirstElement();
			selectedCriterion.getCriterionConfigurationDialog(listViewer.getControl().getShell()).open();
		}
		
	}

	private void comboViewerChanged(RankingCalculator rankingCalculator) {
		
		listViewer.setInput(rankingCalculator.getCriteria()); 
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				okButtonPressed();
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	private void okButtonPressed() {
		if(((IStructuredSelection)(comboViewer.getSelection())).size()>0){
			RankingCalculator ranking=(RankingCalculator) ((IStructuredSelection)(comboViewer.getSelection())).getFirstElement();
			ranking.recalculateRanking();
			RankingManager.getInstance().setCurrentRanking(ranking);
			ConCADView.updateView();
			Preferences.userNodeForPackage(RankingCalculator.class).put("selectedRanking", ranking.getName());
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(731, 209);
	}
}
