package concad.ui.views.criteriaConfiguration;

import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import concad.core.smells.constants.SmellNames;
import concad.db.DataBaseManager;
import concad.priotization.criteria.SmellRelevanceCriteria;

public class SmellRelevanceConfiguration extends CriterionConfigurationDialog {
	private Vector<Integer> relevanceValues;
	private ComboViewer comboViewerBrainClass;
	private ComboViewer comboViewerDataClass;
	private ComboViewer comboViewerFeatureEnvy;
	private ComboViewer comboViewerIntensiveCoupling;
	private ComboViewer comboViewerShotgunSurgery;
	private ComboViewer comboViewerBrainMethod;
	private ComboViewer comboViewerDispersedCoupling;
	private ComboViewer comboViewerGodClass;
	private ComboViewer comboViewerRefusedParentBequest;
	private ComboViewer comboViewerTraditionBreaker;
	private SmellRelevanceCriteria selectedCriterion;
	
	public SmellRelevanceConfiguration(Shell parentShell, SmellRelevanceCriteria selectedCriterion, IProject currentProject) {
		super(parentShell);
		relevanceValues=new Vector<>();
		relevanceValues.add(1);
		relevanceValues.add(2);
		relevanceValues.add(3);
		relevanceValues.add(4);
		relevanceValues.add(5);
		this.selectedCriterion=selectedCriterion;
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		
		comboViewerBrainClass=createComboViewer(10,30, SmellNames.BRAIN_CLASS,container);
		comboViewerDataClass=createComboViewer(10,80, SmellNames.DATA_CLASS,container);
		comboViewerFeatureEnvy=createComboViewer(10,120,SmellNames.FEATURE_ENVY,container);
		comboViewerIntensiveCoupling=createComboViewer(10,170,SmellNames.INTENSIVE_COUPLING,container);
		comboViewerShotgunSurgery=createComboViewer(10,220,SmellNames.SHOTGUN_SURGERY,container);		
		comboViewerBrainMethod=createComboViewer(170,30,SmellNames.BRAIN_METHOD,container);
		comboViewerDispersedCoupling=createComboViewer(170,80,SmellNames.DISPERSED_COUPLING,container);
		comboViewerGodClass=createComboViewer(170,120,SmellNames.GOD_CLASS,container);
		comboViewerRefusedParentBequest=createComboViewer(170,170,SmellNames.REFUSED_BEQUEST,container);
		comboViewerTraditionBreaker=createComboViewer(170,220,SmellNames.TRADITION_BREAKER,container);
		
		return container;
	}
	
	private ComboViewer createComboViewer(int x,int y, String name, Composite container){
		Label lbl = new Label(container, SWT.NONE);
		lbl.setBounds(x, y-20, 140, 16);
		lbl.setText(name);
		ComboViewer comboViewer = new ComboViewer(container, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setBounds(x, y, 59, 22);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LabelProvider() {
		  @Override
		  public String getText(Object element) {
		    if (element instanceof Integer) {
		    	Integer relevance = (Integer) element;
		      return relevance.toString();
		    }
		    return super.getText(element);
		  }
		});
		comboViewer.setInput(relevanceValues); 
		combo.select(selectedCriterion.getRelevance(name)-1);
		return comboViewer;
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
		selectedCriterion.addRelevance(SmellNames.BRAIN_CLASS, getComboSelection(comboViewerBrainClass));
		selectedCriterion.addRelevance(SmellNames.DATA_CLASS, getComboSelection(comboViewerDataClass));
		selectedCriterion.addRelevance(SmellNames.FEATURE_ENVY, getComboSelection(comboViewerFeatureEnvy));
		selectedCriterion.addRelevance(SmellNames.INTENSIVE_COUPLING, getComboSelection(comboViewerIntensiveCoupling));
		selectedCriterion.addRelevance(SmellNames.SHOTGUN_SURGERY, getComboSelection(comboViewerShotgunSurgery));
		selectedCriterion.addRelevance(SmellNames.BRAIN_METHOD, getComboSelection(comboViewerBrainMethod));
		selectedCriterion.addRelevance(SmellNames.DISPERSED_COUPLING, getComboSelection(comboViewerDispersedCoupling));
		selectedCriterion.addRelevance(SmellNames.GOD_CLASS, getComboSelection(comboViewerGodClass));
		selectedCriterion.addRelevance(SmellNames.REFUSED_BEQUEST, getComboSelection(comboViewerRefusedParentBequest));
		selectedCriterion.addRelevance(SmellNames.TRADITION_BREAKER, getComboSelection(comboViewerTraditionBreaker));
		DataBaseManager.getInstance().saveOrUpdateEntity(selectedCriterion);
	}
	private Integer getComboSelection(ComboViewer comboViewer){
		if(((IStructuredSelection)(comboViewer.getSelection())).size()>0){
			return (Integer) ((IStructuredSelection)(comboViewer.getSelection())).getFirstElement();
		}
		return 1;
	}
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(383, 335);
	}
}
