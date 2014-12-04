package concad.ui.views;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import org.eclipse.jface.action.Action;

public class ConCADViewPopupMenuActionManager extends Observable{
	private static ConCADViewPopupMenuActionManager instance;
	private List<Action> actions;
	public static ConCADViewPopupMenuActionManager getInstance(){
		if(instance==null)
			instance=new ConCADViewPopupMenuActionManager();
		return instance;
	}
	public ConCADViewPopupMenuActionManager() {
		actions=new Vector<Action>();
	}
	public List<Action> getActions() {
		return actions;
	}
	public void addAction(Action anAction){
		//Antes de agregar chequeo que no se haya agregado antes
		for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();) {
			Action action = (Action) iterator.next();
			if(action.getText().equals(anAction.getText()))
				return; 
		}
		actions.add(anAction);
	}
}
