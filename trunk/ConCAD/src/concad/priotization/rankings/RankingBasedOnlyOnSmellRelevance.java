package concad.priotization.rankings;

import java.util.Collections;

import concad.core.smells.interfaces.CodeSmell;
import concad.priotization.criteria.SmellRelevanceCriteria;

public class RankingBasedOnlyOnSmellRelevance extends RankingCalculator{
	
	public RankingBasedOnlyOnSmellRelevance(SmellRelevanceCriteria smellRelevanceCriteria) {
		super(RankingType.SMELL_RELEVANCE);
		criteria.add(smellRelevanceCriteria);
	}

	@Override
	protected void sortRanking() {
		 Collections.sort(ranking, new RelevanceOrder());
	}

	@Override
	public Double calculateRankingValue(CodeSmell codeSmell) {
		SmellRelevanceCriteria criterion=(SmellRelevanceCriteria) criteria.firstElement();
		return new Double(criterion.getRelevance(codeSmell.getKindOfSmellName()));
	}
	
}
