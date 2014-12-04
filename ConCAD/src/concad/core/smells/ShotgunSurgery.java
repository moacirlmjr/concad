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

public class ShotgunSurgery extends CodeSmell{
	public ShotgunSurgery(MethodMetrics node) {
		super(SmellNames.SHOTGUN_SURGERY);
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
	
	/**All the classes invoking the main method of the smell
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getAffectedClasses() {
		return new TreeSet<String>((List<String>)(node.getAttribute(MetricNames.ListOfClassInvoking)));
	}
}
