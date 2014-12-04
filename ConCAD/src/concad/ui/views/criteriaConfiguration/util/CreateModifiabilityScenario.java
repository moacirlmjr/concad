package concad.ui.views.criteriaConfiguration.util;

import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
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
import org.eclipse.swt.widgets.Text;

import concad.priotization.criteria.util.ModifiabilityScenario;
import concad.ui.views.criteriaConfiguration.ModifiabilityScenariosConfiguration;

public class CreateModifiabilityScenario extends Dialog {
	private ModifiabilityScenariosConfiguration modifiabilityScenariosConfiguration;
	private Text text;
	private Text text_1;
	private IProject currentProject;
	private Vector<String> packages;
	private Vector<String> selectedPackages;
	private Vector<String> classes;
	private Vector<String> selectedClasses;
	private ListViewer allPackagesViewer;
	private ListViewer allClassesViewer;
	private ListViewer selectedPackagesViewer;
	private ListViewer selectedClassesViewer;
	private ComboViewer comboViewer;
	private Vector<Double> importanceValues;
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param modifiabilityScenariosConfiguration 
	 * @param currentProject 
	 */
	public CreateModifiabilityScenario(Shell parentShell, ModifiabilityScenariosConfiguration modifiabilityScenariosConfiguration, IProject currentProject) {
		super(parentShell);
		this.modifiabilityScenariosConfiguration=modifiabilityScenariosConfiguration;
		this.currentProject=currentProject;
		importanceValues=new Vector<Double>();
		importanceValues.add(0.0);
		importanceValues.add(0.1);
		importanceValues.add(0.2);
		importanceValues.add(0.3);
		importanceValues.add(0.4);
		importanceValues.add(0.5);
		importanceValues.add(0.6);
		importanceValues.add(0.7);
		importanceValues.add(0.8);
		importanceValues.add(0.9);
		importanceValues.add(1.0);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		loadClassesAndPackages();
		Label lblName = new Label(container, SWT.NONE);
		lblName.setBounds(10, 13, 59, 14);
		lblName.setText("Name");
		
		text = new Text(container, SWT.BORDER);
		text.setBounds(100, 10, 413, 19);
		
		Label lblDescription = new Label(container, SWT.NONE);
		lblDescription.setBounds(10, 42, 94, 14);
		lblDescription.setText("Description");
		
		text_1 = new Text(container, SWT.BORDER);
		text_1.setBounds(131, 37, 382, 45);
		
		Label lblPackages = new Label(container, SWT.NONE);
		lblPackages.setBounds(10, 103, 59, 14);
		lblPackages.setText("Packages");
		
		allPackagesViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		List list = allPackagesViewer.getList();
		list.setBounds(10, 123, 223, 157);
		allPackagesViewer.setContentProvider(ArrayContentProvider.getInstance());
		allPackagesViewer.setInput(packages);
		
		Label lblClasses = new Label(container, SWT.NONE);
		lblClasses.setText("Classes");
		lblClasses.setBounds(10, 293, 59, 14);
		
		allClassesViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		List list_1 = allClassesViewer.getList();
		list_1.setBounds(10, 313, 223, 157);
		allClassesViewer.setContentProvider(ArrayContentProvider.getInstance());
		allClassesViewer.setInput(classes);
		
		Button button = new Button(container, SWT.NONE);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				selectPackageClicked();
			}
		});
		button.setBounds(237, 166, 59, 28);
		button.setText(">>");
		
		Button button_1 = new Button(container, SWT.NONE);
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				selectClassClicked();
			}
		});
		button_1.setText(">>");
		button_1.setBounds(237, 362, 59, 28);
		
		Button button_2 = new Button(container, SWT.NONE);
		button_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				unselectedPackageCliked();
			}
		});
		
		button_2.setText("<<");
		button_2.setBounds(237, 199, 59, 28);
		
		Button button_3 = new Button(container, SWT.NONE);
		button_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				unselectedClassCliked();
			}
		});
		button_3.setText("<<");
		button_3.setBounds(237, 392, 59, 28);
		
		selectedPackagesViewer = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		List list_2 = selectedPackagesViewer.getList();
		list_2.setBounds(302, 123, 223, 157);
		selectedPackagesViewer.setContentProvider(ArrayContentProvider.getInstance());
		selectedPackagesViewer.setInput(selectedPackages);
		
		selectedClassesViewer = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		List list_3 = selectedClassesViewer.getList();
		list_3.setBounds(302, 313, 223, 157);
		
		Label lblImportance = new Label(container, SWT.NONE);
		lblImportance.setBounds(10, 505, 94, 14);
		lblImportance.setText("Importance");
		selectedClassesViewer.setContentProvider(ArrayContentProvider.getInstance());
		selectedClassesViewer.setInput(selectedClasses);

		comboViewer = new ComboViewer(container, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setBounds(100, 501, 85, 22);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LabelProvider() {
		  @Override
		  public String getText(Object element) {
		    if (element instanceof Double) {
		    	Double relevance = (Double) element;
		      return relevance.toString();
		    }
		    return super.getText(element);
		  }
		});
		comboViewer.setInput(importanceValues); 
		return container;
	}

	private void unselectedClassCliked() {
		IStructuredSelection selection = (IStructuredSelection) selectedClassesViewer
			      .getSelection();
		if (selection.size() > 0){
			int index=selectedClasses.indexOf(selection.getFirstElement());
			if(index>=0){
				selectedClasses.removeElementAt(index);
				classes.add((String) selection.getFirstElement());
				allClassesViewer.setInput(classes);
				selectedClassesViewer.setInput(selectedClasses);
			}
		}
	}

	private void unselectedPackageCliked() {
		IStructuredSelection selection = (IStructuredSelection) selectedPackagesViewer
			      .getSelection();
		if (selection.size() > 0){
			int index=selectedPackages.indexOf(selection.getFirstElement());
			if(index>=0){
				selectedPackages.removeElementAt(index);
				packages.add((String) selection.getFirstElement());
				allPackagesViewer.setInput(packages);
				selectedPackagesViewer.setInput(selectedPackages);
			}
		}
	}

	private void selectClassClicked() {
		IStructuredSelection selection = (IStructuredSelection) allClassesViewer
			      .getSelection();
		if (selection.size() > 0){
			int index=classes.indexOf(selection.getFirstElement());
			if(index>=0){
				classes.removeElementAt(index);
				selectedClasses.add((String) selection.getFirstElement());
				allClassesViewer.setInput(classes);
				selectedClassesViewer.setInput(selectedClasses);
			}
		}
	}

	private void selectPackageClicked() {
		IStructuredSelection selection = (IStructuredSelection) allPackagesViewer
			      .getSelection();
		if (selection.size() > 0){
			int index=packages.indexOf(selection.getFirstElement());
			if(index>=0){
				packages.removeElementAt(index);
				selectedPackages.add((String) selection.getFirstElement());
				allPackagesViewer.setInput(packages);
				selectedPackagesViewer.setInput(selectedPackages);
			}
		}
		
	}

	private void loadClassesAndPackages() {
		packages=new Vector<String>();
		classes=new Vector<String>();
		selectedPackages=new Vector<String>();
		selectedClasses=new Vector<String>();
		IJavaProject javaProject = JavaCore.create(currentProject);
		IPackageFragment[] packages2;
		try {
			packages2 = javaProject.getPackageFragments();
			 for (IPackageFragment myPackage : packages2) {
				 if (myPackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					 	if(myPackage.getElementName().length()>0)
					 		packages.add(myPackage.getElementName());
					 	for (ICompilationUnit unit : myPackage.getCompilationUnits()) {
					 		// System.out.println("Source file " + unit.getElementName());
					 		
					 		for(IType type:unit.getAllTypes()){
					 			classes.add(type.getFullyQualifiedName().toString().replace("$", "."));
					 		}
					 			
					 		
					 	 }

				      }
					  
				 // packages.add("");
			   
			  }
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				okButtonClicked();
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	private void okButtonClicked() {
		modifiabilityScenariosConfiguration.addScenario(new ModifiabilityScenario(text.getText(), text_1.getText(), selectedClasses, selectedPackages, getComboSelection(comboViewer), currentProject.getFullPath().toString() ));
	}
	private Double getComboSelection(ComboViewer comboViewer){
		if(((IStructuredSelection)(comboViewer.getSelection())).size()>0){
			return (Double) ((IStructuredSelection)(comboViewer.getSelection())).getFirstElement();
		}
		return 0.0;
	}
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(557, 637);
	}
}
