package concad.priotization.criteria;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.swt.widgets.Shell;

import concad.priotization.criteria.util.ModifiabilityScenario;
import concad.ui.views.criteriaConfiguration.CriterionConfigurationDialog;
import concad.ui.views.criteriaConfiguration.ModifiabilityScenariosConfiguration;

public class ModifiabilityScenariosCriteria extends Criterion {
	private List<ModifiabilityScenario> scenarios;
	private Long id;
	private Integer affectedClassesByScenariosCache=null;
	public ModifiabilityScenariosCriteria(String project) {
		super("Related Modifiability Scenarios",project);
		scenarios=new Vector<ModifiabilityScenario>();
	}
	public ModifiabilityScenariosCriteria() {
		super("",null);
	}
	@Override
	public CriterionConfigurationDialog getCriterionConfigurationDialog(
			Shell parentShell) {
		
		return new ModifiabilityScenariosConfiguration(parentShell, this, getIProject());
	}
	
	
	
	public double getMostImportantScenarioValueAffectingAClass(String aClassPath){
		double mostImportantScenario=0.0;
		
		for (Iterator<ModifiabilityScenario> iterator = scenarios.iterator(); iterator.hasNext();) {
			ModifiabilityScenario scenario = (ModifiabilityScenario) iterator.next();
			if(scenario.affectsAClass(aClassPath)){
				if(scenario.getImportance()>mostImportantScenario)
					mostImportantScenario=scenario.getImportance();
			}
		}
		
		return mostImportantScenario;
	}
	public int getNumberOfClassesAffectedByTheScenarios(){
		if(affectedClassesByScenariosCache==null){
			Set<String> ret = new TreeSet<String>();
			for (Iterator<ModifiabilityScenario> iterator = scenarios.iterator(); iterator.hasNext();) {
				ModifiabilityScenario scenario = (ModifiabilityScenario) iterator.next();
				ret.addAll(scenario.getAllAffectedClasses());
			}	
			affectedClassesByScenariosCache=ret.size();
		}
		
		return affectedClassesByScenariosCache;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<ModifiabilityScenario> getScenarios() {
		return scenarios;
	}
	public void setScenarios(List<ModifiabilityScenario> scenarios) {
		this.scenarios = scenarios;
		affectedClassesByScenariosCache=null;
	}
}
