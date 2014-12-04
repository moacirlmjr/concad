package concad.ui.views.ranking;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

import concad.core.smells.interfaces.CodeSmell;
import concad.priotization.RankingManager;
import concad.priotization.rankings.RankingBasedOnRelevanceHistoryAndScenarios;

public class RankingHistoryAndScenariosViewer extends RankingCalculatorViewer {
	
	public void createTableViewer(RankingViewerComparator comparator, TableViewer viewer){
		super.createTableViewer(comparator, viewer);
		TableViewerColumn colRanking = createTableViewerColumn(comparator, viewer, "History Value", 100, 4);
		colRanking.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		   
		    return (getHistoryValue(p)).toString();
		  }
		});
		TableViewerColumn colRankingValue = createTableViewerColumn(comparator, viewer, "Scenarios Value", 100, 5);
		colRankingValue.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		   
		    return String.valueOf(RankingManager.getInstance().getRankingCalculator().getRankingValue(p)-getHistoryValue(p));
		  }
		});
	}
	
	private Double getHistoryValue(CodeSmell p){
		return ((RankingBasedOnRelevanceHistoryAndScenarios)RankingManager.getInstance().getRankingCalculator()).getHistoryValue(p);
	}

}
