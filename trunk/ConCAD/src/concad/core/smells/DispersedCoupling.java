package concad.core.smells;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.core.constant.MetricNames;
import concad.core.constant.SmellNames;
import concad.core.smells.interfaces.CodeSmell;
import concad.metrics.storage.MethodMetrics;

public class DispersedCoupling extends CodeSmell{
	public DispersedCoupling(MethodMetrics node) {
		super(SmellNames.DISPERSED_COUPLING);
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
	
	/**All the classes invoked and invoking the main method of the smell
	 * 
	 */
	@Override
	public Set<String> getAffectedClasses() {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>)(node.getAttribute(MetricNames.ListOfClassInvoked));
		@SuppressWarnings("unchecked")
		List<String> listInvoking = (List<String>)(node.getAttribute(MetricNames.ListOfClassInvoking));
		if(listInvoking!=null){
			for(String name:listInvoking){
				if(!list.contains(name)){
					list.add(name);
				}
			}
		}
		return new TreeSet<String>();//(list);
	}
}
