package concad.metrics.calculate;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.core.constant.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class NumberOfPublicMethods implements IAttribute{

	@Override
	public void calculate(ClassMetrics node) {
		TypeDeclaration declaration = (TypeDeclaration) node.getDeclaration();
		
		MethodDeclaration[] methods = declaration.getMethods();
		int publicMethods = 0;
		if(methods!=null){
			for(MethodDeclaration method:methods){
				if(!method.isConstructor() && Modifier.isPublic(method.getModifiers())){
					publicMethods++;
				}
			}
		}		
		node.setMetric(getName(), publicMethods);
	}

	@Override
	public String getName() {
		return MetricNames.NOPM;
	}

	@Override
	public void calculate(MethodMetrics node) {

	}
	


}
