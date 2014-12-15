package concad.priotization.criteria;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.widgets.Shell;

import concad.core.constant.SmellNames;
import concad.ui.views.criteriaConfiguration.CriterionConfigurationDialog;
import concad.ui.views.criteriaConfiguration.SmellRelevance;
import concad.ui.views.criteriaConfiguration.SmellRelevanceConfiguration;

public class SmellRelevanceCriteria extends Criterion{
	private Long id;
	private List<SmellRelevance> relevances;
	public SmellRelevanceCriteria() {
		super("",null);
	}
	public SmellRelevanceCriteria(String project) {
		super("Relevance of the kind of code anomaly",project);
		relevances=new Vector<SmellRelevance>();
		addRelevance(SmellNames.BRAIN_CLASS, 1);
		addRelevance(SmellNames.DATA_CLASS, 1);
		addRelevance(SmellNames.FEATURE_ENVY, 1);
		addRelevance(SmellNames.INTENSIVE_COUPLING, 1);
		addRelevance(SmellNames.SHOTGUN_SURGERY, 1);
		addRelevance(SmellNames.BRAIN_METHOD, 1);
		addRelevance(SmellNames.DISPERSED_COUPLING, 1);
		addRelevance(SmellNames.GOD_CLASS, 1);
		addRelevance(SmellNames.REFUSED_BEQUEST, 1);
		addRelevance(SmellNames.TRADITION_BREAKER, 1);
	
		
		
	}

	@Override
	public CriterionConfigurationDialog getCriterionConfigurationDialog(Shell parentShell) {
		return new SmellRelevanceConfiguration(parentShell,this,getIProject());
	}
	public void addRelevance(String codeSmellName, Integer relevance){
		SmellRelevance smellRelevance=getRelevanceFoSmell(codeSmellName);
		smellRelevance.setRelevance(relevance);
	}
	private SmellRelevance getRelevanceFoSmell(String codeSmellName) {
		for (Iterator<SmellRelevance> iterator = relevances.iterator(); iterator.hasNext();) {
			SmellRelevance type = (SmellRelevance) iterator.next();
			if(type.getSmellName().equals(codeSmellName)){
				return type;
			}
		}
		SmellRelevance newRet=new SmellRelevance(codeSmellName, 1);
		relevances.add(newRet);
		return newRet;
	}
	public Integer getRelevance(String kindOfCodeSmell){
		for (Iterator<SmellRelevance> iterator = relevances.iterator(); iterator.hasNext();) {
			SmellRelevance type = (SmellRelevance) iterator.next();
			if(type.getSmellName().equals(kindOfCodeSmell))
				return type.getRelevance();
		}
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public List<SmellRelevance> getRelevances() {
		return relevances;
	}
	public void setRelevances(List<SmellRelevance> relevances) {
		this.relevances = relevances;
	}
}
