package concad.priotization.rankings;

import java.util.Collections;

import concad.core.smells.interfaces.CodeSmell;
import concad.priotization.criteria.HistoryBetaCriteria;
import concad.priotization.criteria.SmellRelevanceCriteria;

public class RankingBasedOnSmellRelevanceAndHistory extends RankingCalculator{
	protected SmellRelevanceCriteria aaa;
	public RankingBasedOnSmellRelevanceAndHistory(SmellRelevanceCriteria smellRelevanceCriteria, HistoryBetaCriteria historyBetaCriteria) {
		super(RankingType.SMELL_RELEVANCE_AND_HISTORY);
		criteria.add(smellRelevanceCriteria);
		criteria.add(historyBetaCriteria);
	}

	@Override
	protected void sortRanking() {
		 Collections.sort(ranking, new RelevanceOrder());
	}

	@Override
	public Double calculateRankingValue(CodeSmell codeSmell) {
		SmellRelevanceCriteria criterion1=(SmellRelevanceCriteria) criteria.firstElement();
		HistoryBetaCriteria criterion2=(HistoryBetaCriteria) criteria.lastElement();
		Double rankValue1=criterion1.getRelevance(codeSmell.getKindOfSmellName())*criterion2.getBetaValue(codeSmell.getMainClassName());
		return rankValue1;
	}
	
}
