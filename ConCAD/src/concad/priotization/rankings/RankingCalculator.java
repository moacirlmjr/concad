package concad.priotization.rankings;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import concad.core.design.DesignFlaw;
import concad.core.smells.interfaces.CodeSmell;
import concad.priotization.criteria.Criterion;

public abstract class RankingCalculator{
	protected Vector<Criterion> criteria;
	protected Vector<CodeSmell> ranking;
	private Hashtable<CodeSmell,Double> rankingsValueCache;
	protected RankingType rankingType;
	
	
	public RankingCalculator(RankingType rankingType) {
		this.rankingType = rankingType;
		criteria=new Vector<Criterion>();
		rankingsValueCache=new Hashtable<CodeSmell,Double>();
	}
	
	//los criteria values los deberia pasar por parametro segun el subtipo
	public int getRanking(DesignFlaw flaw){
		return ranking.indexOf(flaw)+1;
	}
	
	public String getName() {
		return rankingType.getName();
	}
	
	public RankingType getRankingType() {
		return rankingType;
	}

	public Vector<Criterion> getCriteria() {
		return criteria;
	}
	public void setCodeSmells(Vector<CodeSmell> smells){
		ranking=smells;
		//recalculateRanking();
	}
	
	public void recalculateRanking(){
		rankingsValueCache=new Hashtable<CodeSmell,Double>();
		sortRanking();
	}
	protected abstract void sortRanking();
	
	public Double getRankingValue(CodeSmell codeSmell){
		Double rankingValue=rankingsValueCache.get(codeSmell);
		if(rankingValue==null){
			rankingValue=calculateRankingValue(codeSmell);
			rankingsValueCache.put(codeSmell, rankingValue);
		}
		return rankingValue;
	}
	protected abstract Double calculateRankingValue(CodeSmell codeSmell);
	protected class RelevanceOrder implements Comparator<DesignFlaw>{

		@Override
		public int compare(DesignFlaw o1, DesignFlaw o2) {
			
			return (getRankingValue((CodeSmell) o1)).compareTo(getRankingValue((CodeSmell) o2))*(-1);//Multiplico por -1 para que ordene de mayor a menor
		}
		
	}

}
