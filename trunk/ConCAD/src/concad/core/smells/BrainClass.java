package concad.core.smells;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.core.constant.MetricNames;
import concad.core.constant.SmellNames;
import concad.core.smells.interfaces.CodeSmell;
import concad.metrics.storage.ClassMetrics;

public class BrainClass extends CodeSmell{
	public BrainClass(ClassMetrics node) {
		super(SmellNames.BRAIN_CLASS);
		this.element=node.getDeclaration();
		this.node=node;
	}
	@Override
	public TypeDeclaration getMainClass() {
		if(this.getElement() instanceof TypeDeclaration){
	    	return ((TypeDeclaration)(this.getElement()));
	    }
		return null;
	}
	
	/**All the classes invoked and invoking the main class of the smell
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getAffectedClasses() {
		List<String> list = (List<String>)(node.getAttribute(MetricNames.ListOfClassInvoked));
		for(String name:(List<String>)(node.getAttribute(MetricNames.ListOfClassInvoking))){
			if(!list.contains(name)){
				list.add(name);
			}
		}
		return new TreeSet<String>(list);
	}
}
