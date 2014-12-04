package concad.core.smells;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.core.smells.constants.SmellNames;
import concad.core.smells.interfaces.CodeSmell;
import concad.metrics.constants.MetricNames;
import concad.metrics.storage.MethodMetrics;

public class FeatureEnvy extends CodeSmell{
	public FeatureEnvy(MethodMetrics node) {
		super(SmellNames.FEATURE_ENVY);
		this.element=node.getDeclaration();
		this.node=node;
	}
	@Override
	public TypeDeclaration getMainClass() {
		MethodDeclaration element = (MethodDeclaration) this.getElement();  
		if(element.getParent() instanceof TypeDeclaration){
		    	return ((TypeDeclaration)(element.getParent()));
		}else{
			return ((TypeDeclaration)(element.getParent().getParent()));
		}
	}
	
	/**All the classes invoked the main method of the smell
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getAffectedClasses() {
		return new TreeSet<String>((List<String>)(node.getAttribute(MetricNames.ListOfClassInvoked)));
	}
}
