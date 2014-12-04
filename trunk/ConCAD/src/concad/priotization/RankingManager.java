package concad.priotization;

import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;

import concad.core.smells.interfaces.CodeSmell;
import concad.db.DataBaseManager;
import concad.priotization.criteria.AlphaCriteria;
import concad.priotization.criteria.HistoryBetaCriteria;
import concad.priotization.criteria.ModifiabilityScenariosCriteria;
import concad.priotization.criteria.SmellRelevanceCriteria;
import concad.priotization.rankings.RankingBasedOnRelevanceHistoryAndScenarios;
import concad.priotization.rankings.RankingBasedOnSmellRelevanceAndHistory;
import concad.priotization.rankings.RankingBasedOnlyOnSmellRelevance;
import concad.priotization.rankings.RankingCalculator;

public class RankingManager {
	private static RankingManager instance;
	private Vector<RankingCalculator> rankingsCalculators;
	private RankingCalculator currentRanking;
	private String currentProject;
	
	public static RankingManager getInstance(){
		if(instance==null)
			instance=new RankingManager();
		return instance;
	}
	public RankingManager() {
		rankingsCalculators=new Vector<RankingCalculator>();
		currentProject=null;
	}
	public RankingCalculator getRankingCalculator(){
		return currentRanking;
	}
	public Vector<RankingCalculator> getRankingsCalculators() {
		return rankingsCalculators;
	}
	public void setCurrentRanking(RankingCalculator currentRanking) {
		this.currentRanking = currentRanking;
	}
	
	public void setSmells(Vector<CodeSmell> smells,String project){
		currentProject=project;
		loadRankings(project);
		for (Iterator<RankingCalculator> iterator = rankingsCalculators.iterator(); iterator.hasNext();) {
			RankingCalculator calculator = (RankingCalculator) iterator.next();
			calculator.setCodeSmells(smells);
		}
		
		RankingCalculator calculator=getRankingCalculator(Preferences.userNodeForPackage(RankingCalculator.class).get("selectedRanking", rankingsCalculators.get(0).getName()));
		
		calculator.recalculateRanking();
	
		currentRanking=calculator;
	}
	
	private RankingCalculator getRankingCalculator(String name) {
		for (Iterator<RankingCalculator> iterator = rankingsCalculators.iterator(); iterator.hasNext();) {
			RankingCalculator calculator = (RankingCalculator) iterator.next();
			if(calculator.getName().equals(name))
				return calculator;
		}
		return null;
	}
	private void loadRankings(String project){
		/**for (Iterator<SmellRelevanceCriteria> iterator = DataBaseManager.getInstance().listSmellRelevanceCriteria().iterator(); iterator.hasNext();) {
			SmellRelevanceCriteria type = (SmellRelevanceCriteria) iterator.next();
			DataBaseManager.getInstance().deleteSmellRelevanceCriteria(type);
		}*/
		/**for (Iterator<HistoryBetaCriteria> iterator = DataBaseManager.getInstance().listHistoryBetaCriteria().iterator(); iterator.hasNext();) {
			HistoryBetaCriteria type = (HistoryBetaCriteria) iterator.next();
			DataBaseManager.getInstance().deleteHistoryBetaCriteria(type);
		}*/
		SmellRelevanceCriteria smellRelevanceCriteria=DataBaseManager.getInstance().getSmellRelevanceCriteriaForProject(project);
		HistoryBetaCriteria historyBetaCriteria=DataBaseManager.getInstance().getHistoryBetaCriteriaForProject(project);
		ModifiabilityScenariosCriteria modifiabilityScenariosCriteria=DataBaseManager.getInstance().getModifiabilityScenariosCriteriaForProject(project);
		AlphaCriteria alphaCriteria=DataBaseManager.getInstance().getAlphaCriteriaForProject(project);
		
		rankingsCalculators=new Vector<RankingCalculator>();
		rankingsCalculators.add(new RankingBasedOnlyOnSmellRelevance(smellRelevanceCriteria));
		rankingsCalculators.add(new RankingBasedOnSmellRelevanceAndHistory(smellRelevanceCriteria, historyBetaCriteria));
		rankingsCalculators.add(new RankingBasedOnRelevanceHistoryAndScenarios(smellRelevanceCriteria, historyBetaCriteria, modifiabilityScenariosCriteria, alphaCriteria));
		
		
	}
	public int getPredefinedCalculatorIndex() {
		String name=Preferences.userNodeForPackage(RankingCalculator.class).get("selectedRanking", rankingsCalculators.get(0).getName());
		for (Iterator<RankingCalculator> iterator = rankingsCalculators.iterator(); iterator.hasNext();) {
			RankingCalculator calculator = (RankingCalculator) iterator.next();
			if(calculator.getName().equals(name))
				return rankingsCalculators.indexOf(calculator);
		}
		return 0;
	}
	public String getCurrentProject() {
		return currentProject;
	}
}
