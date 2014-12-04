package concad.metrics.calculate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodInvocation;

import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class CouplingIntensity implements IAttribute{
	
	@Override
	public void calculate(ClassMetrics node) {}

	@Override
	public String getName() {
		return MetricNames.CINT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void calculate(MethodMetrics node) {
		List<MethodInvocation> ListOfMethodInvoked = (List<MethodInvocation>) node.getAttribute(MetricNames.ListOfMethodInvoked);
		List<String> distinctMethods = new ArrayList<String>();
		for(MethodInvocation method:ListOfMethodInvoked){
			if(!method.resolveMethodBinding().isConstructor() && !definedInSuperClass(node, method)
					&& !distinctMethods.contains(method.resolveMethodBinding().getKey())){
				distinctMethods.add(method.resolveMethodBinding().getKey());
			}
		}
		node.setMetric(getName(), distinctMethods.size());
	}

	@SuppressWarnings("unchecked")
	private boolean definedInSuperClass(MethodMetrics node, MethodInvocation method) {
		List<String> superClasses = (List<String>) node.getClassMetrics().getAttribute(MetricNames.SC);
		if(method.resolveMethodBinding().getDeclaringClass()!=null && 
				superClasses.contains(method.resolveMethodBinding().getDeclaringClass().getBinaryName())){
			return true;
		}
		return false;
	}

}
