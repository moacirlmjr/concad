package concad.ui.views.ranking;

import java.text.SimpleDateFormat;
import java.util.Set;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;
import concad.core.smells.interfaces.CodeSmell;
import concad.priotization.RankingManager;

public class RankingCalculatorViewer implements RankingViewer{
	
	public void createTableViewer(RankingViewerComparator comparator, TableViewer viewer){
		TableViewerColumn colKindOfSmell = createTableViewerColumn(comparator, viewer, "Code Anomaly", 120, 0);
		colKindOfSmell.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		    return p.getKindOfSmellName();
		  }
		});
		
		TableViewerColumn colJavaClassElement = createTableViewerColumn(comparator, viewer, "Class Element", 180, 1);
		colJavaClassElement.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		    return p.getMainClassName();
		  }
		});
		
		TableViewerColumn colJavaElement = createTableViewerColumn(comparator, viewer, "Java Element", 180, 2);
		colJavaElement.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		    return p.getElementName();
		  }
		});
		
		TableViewerColumn lineNumberJavaElement = createTableViewerColumn(comparator, viewer, "Line Number", 100, 3);
		lineNumberJavaElement.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		    return String.valueOf(p.getLine());
		  }
		});

		
		TableViewerColumn colRanking = createTableViewerColumn(comparator, viewer, "#Ranking", 100, 4);
		colRanking.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;		   
		    return (new Integer(RankingManager.getInstance().getRankingCalculator().getRanking(p))).toString();
		  }
		});
		TableViewerColumn colRankingValue = createTableViewerColumn(comparator, viewer, "Ranking Value", 100, 5);
		colRankingValue.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;		   
		    return (RankingManager.getInstance().getRankingCalculator().getRankingValue(p)).toString();
		  }
		});
		
		TableViewerColumn createdOn = createTableViewerColumn(comparator, viewer, "Created at", 100, 6);
		createdOn.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;		    	    
		    SimpleDateFormat formatBra = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");  
		    return formatBra.format(p.getDetectedAt().getTime());
		       
		  }
		});		
		
		TableViewerColumn affectedClassesRankingValue = createTableViewerColumn(comparator, viewer, "Affected Classes", 100, 7);
		affectedClassesRankingValue.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		    String ret = "";
		    Set<String> set = p.getAffectedClasses();
		    for (String g : set){
		    	ret+=g;
		    }
		    
		    return ret;
		  }
		});
	}
	
	protected TableViewerColumn createTableViewerColumn(RankingViewerComparator comparator, TableViewer viewer,String title, int bound, final int colNumber) {
	    final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
	        SWT.NONE);
	    final TableColumn column = viewerColumn.getColumn();
	    column.setText(title);
	    column.setWidth(bound);
	    column.setResizable(true);
	    column.setMoveable(true);
	    column.addSelectionListener(getSelectionAdapter(comparator, viewer,column, colNumber));
	    return viewerColumn;
	 }
	
	protected SelectionAdapter getSelectionAdapter(final RankingViewerComparator comparator, final TableViewer viewer,final TableColumn column,
		      final int index) {
	    SelectionAdapter selectionAdapter = new SelectionAdapter() {
	      @Override
	      public void widgetSelected(SelectionEvent e) {
	        comparator.setColumn(index);
	        int dir = comparator.getDirection();
	        viewer.getTable().setSortDirection(dir);
	        viewer.getTable().setSortColumn(column);
	        viewer.refresh();
	      }
	    };
	    return selectionAdapter;
	}

}
