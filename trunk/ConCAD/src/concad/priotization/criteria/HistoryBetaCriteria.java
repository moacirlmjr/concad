package concad.priotization.criteria;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.widgets.Shell;

import concad.priotization.criteria.util.NOMHistoryOfAVersion;
import concad.priotization.criteria.util.NOMOfAClass;
import concad.ui.views.criteriaConfiguration.CriterionConfigurationDialog;
import concad.ui.views.criteriaConfiguration.HistoryBetaConfiguration;

public class HistoryBetaCriteria extends Criterion {
	private List<NOMHistoryOfAVersion> nomHistory;
	private Hashtable<String,Double> betaValues;
	private Vector<Double> systemReturns;
	private Long id;
	public HistoryBetaCriteria(String project) {
		super("Beta Analysis",project);
		nomHistory=new Vector<NOMHistoryOfAVersion>();
		
	}
	public HistoryBetaCriteria() {
		super("",null);
	}
	@Override
	public CriterionConfigurationDialog getCriterionConfigurationDialog(
			Shell parentShell) {
		return new HistoryBetaConfiguration(parentShell, this,getIProject());
	}

	private void calculateBetaValues(){
		betaValues=new Hashtable<String,Double>();
		systemReturns=systemReturns();
		
		NOMHistoryOfAVersion lastVersion=nomHistory.get(nomHistory.size()-1);
		for (Iterator<NOMOfAClass> iterator = lastVersion.getNomValues().iterator(); iterator.hasNext();) {
			NOMOfAClass type = (NOMOfAClass) iterator.next();
			betaValues.put(type.getaClassName(), Math.abs(calculateBetaValuesForAClass(type.getaClassName())));
		}
		normalizeBetaValues();
	}
	
	private void normalizeBetaValues() {
		double saturation=2;
		double maxValue=Double.MIN_VALUE;
		Enumeration<String> classes=betaValues.keys();
		while(classes.hasMoreElements()){
			String aClass = (String) classes.nextElement();
			if(betaValues.get(aClass)>saturation)
				betaValues.put(aClass, saturation);
			if(betaValues.get(aClass)>maxValue)
				maxValue=betaValues.get(aClass);
		}
		
		Enumeration<String> classes2=betaValues.keys();
		while(classes2.hasMoreElements()){
			String aClass = (String) classes2.nextElement();
			if(maxValue==0)
				betaValues.put(aClass, 0.0);
			else
				betaValues.put(aClass, (betaValues.get(aClass))/maxValue);
		}
	}

	private Double calculateBetaValuesForAClass(String aClass) {
		Vector<Double> classReturns =calculateReturnsForAClass(aClass);
		Vector<Double> subArraySystemReturns=new Vector<Double>(systemReturns.subList(systemReturns.size()-classReturns.size(), systemReturns.size()));
		double systemVariance=Statistics.variance(subArraySystemReturns);
		double beta;
		if(systemVariance==0) 
			beta=0;
		else{
			double covariance=Statistics.covariance(classReturns, subArraySystemReturns);
			beta=covariance/systemVariance;
		}
		return beta;
	}
	
	private Vector<Double> systemReturns(){
		Vector<Integer> ret=new Vector<Integer>();
		for (Iterator<NOMHistoryOfAVersion> iterator = nomHistory.iterator(); iterator.hasNext();) {
			NOMHistoryOfAVersion nomForVersion = (NOMHistoryOfAVersion) iterator.next();
			int sum=0;
			for (Iterator<NOMOfAClass> iterator2 = nomForVersion.getNomValues().iterator(); iterator2.hasNext();) {
				NOMOfAClass type = (NOMOfAClass) iterator2.next();
				sum=sum+type.getNomValue();
			}
			ret.add(sum);
		}
		return createReturns(ret);
	}
	
	private Vector<Double> calculateReturnsForAClass(String aClass) {
		Vector<Integer> ret=new Vector<Integer>();
		for (Iterator<NOMHistoryOfAVersion> iterator = nomHistory.iterator(); iterator.hasNext();) {
			NOMHistoryOfAVersion nomForVersion = (NOMHistoryOfAVersion) iterator.next();
			if(nomForVersion.containsClass(aClass))
				ret.add(nomForVersion.getNOMForClass(aClass).getNomValue());
		}
		return createReturns(ret);
	}
	private Vector<Double> createReturns(Vector<Integer> ret) {
		Vector<Double> returns=new Vector<Double>();
		
				
		for (int i = 1; i < ret.size(); i++) {
			if(ret.get(i-1)==0){
				returns.add(0d);
			}else{
				Double returnValue=  (Math.abs(((ret.get(i)*(1.0d))-ret.get(i-1)))*(Math.pow(2, (i+1-ret.size())  )));//El 1.0f es para que me tome como float
				returns.add(returnValue);
			}
		}
		return returns;
	}

	public void loadNOMHistory(List<NOMHistoryOfAVersion> nomHistory){
		this.nomHistory=nomHistory;
		calculateBetaValues();
	}
	/**
	 * 
	 * @param qualifiedNameOfAClass it is the path to the class separated by dots.
	 * @return
	 */
	public Double getBetaValue(String qualifiedNameOfAClass){
		if(betaValues==null) return 0.0;
		return betaValues.get(qualifiedNameOfAClass);
	}
	private static class Statistics{
		private static double variance(Vector<Double> collection){
			if(collection.size()==0) return 0;
			double mean=mean(collection);
			double temp=0;
			for (Iterator<Double> iterator = collection.iterator(); iterator.hasNext();) {
				Double double1 = (Double) iterator.next();
				temp=temp+ ((mean-double1)*(mean-double1));
			}
			return temp/(collection.size());
		}

		private static double mean(Vector<Double> collection) {
			double sum=0;
			for (Iterator<Double> iterator = collection.iterator(); iterator.hasNext();) {
				Double double1 = (Double) iterator.next();
				sum=sum+double1;
			}
			return sum/(collection.size());
		}
		private static double covariance(Vector<Double> collection1,Vector<Double> collection2){
			if(collection1.size()==0) return 0;
			double mean1=mean(collection1);
			double mean2=mean(collection2);
			double result=0;
			int index=(collection2.size())-(collection1.size());
			for (Iterator<Double> iterator = collection1.iterator(); iterator.hasNext();) {
				Double double1 = (Double) iterator.next();
				Double double2= collection2.get(index);
				result=result+((double1-mean1)*(double2-mean2));
				index++;
			}
			return result/(collection1.size());
		}
	}
	public List<NOMHistoryOfAVersion> getNomHistory() {
		return nomHistory;
	}
	public void setNomHistory(List<NOMHistoryOfAVersion> nomHistory) {
		this.nomHistory = nomHistory;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
