package concad.ui.views.ranking;

import concad.priotization.rankings.RankingType;

public class RankingViewerFactory {
	 public static RankingViewer getRankingViewer(RankingType rankingType) {
		 	RankingViewer viewer = null;
	        switch (rankingType) {
	        case SMELL_RELEVANCE:
	        	viewer = new RankingCalculatorViewer();
	            break;
	 
	        case SMELL_RELEVANCE_AND_HISTORY:
	        	viewer = new RankingCalculatorViewer();
	            break;
	 
	        case RELEVANCE_HISTORY_AND_SCENARIOS:
	        	viewer = new RankingHistoryAndScenariosViewer();
	            break;
	 
	        default:
	            break;
	        }
	        return viewer;
	    }
	 
	 public static RankingViewerComparator getRankingComparatorViewer(RankingType rankingType) {
		 	RankingViewerComparator comparator = null;
	        switch (rankingType) {
	        case SMELL_RELEVANCE:
	        	comparator = new RankingViewerComparator();
	            break;
	 
	        case SMELL_RELEVANCE_AND_HISTORY:
	        	comparator = new RankingViewerComparator();
	            break;
	 
	        case RELEVANCE_HISTORY_AND_SCENARIOS:
	        	comparator = new RankingHistoryAndScenariosComparator();
	            break;
	 
	        default:
	            break;
	        }
	        return comparator;
	    }
}
