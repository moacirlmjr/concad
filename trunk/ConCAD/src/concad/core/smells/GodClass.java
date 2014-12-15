package concad.core.smells;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.core.constant.MetricNames;
import concad.core.constant.SmellNames;
import concad.core.smells.interfaces.CodeSmell;
import concad.metrics.storage.ClassMetrics;

public class GodClass extends CodeSmell{
	public GodClass(ClassMetrics node) {
		super(SmellNames.GOD_CLASS);
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
	/**All the classes invoked by the main class of the smell
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getAffectedClasses() {
		return new TreeSet<String>((List<String>)(node.getAttribute(MetricNames.ListOfClassInvoked)));
	}
}
