package concad.metrics.calculate;

import java.util.List;

import concad.core.constant.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class CouplingDispersion implements IAttribute{
	
	@Override
	public void calculate(ClassMetrics node) {}

	@Override
	public String getName() {
		return MetricNames.CDISP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void calculate(MethodMetrics node) {
		List<String> ListOfClassInvoked = (List<String>) node.getAttribute(MetricNames.ListOfClassInvoked);
		List<String> superClasses = (List<String>) node.getClassMetrics().getAttribute(MetricNames.SC);
		int numberOfclasses = 0;
		for(String clazz:ListOfClassInvoked){
			if(!superClasses.contains(clazz)){
				numberOfclasses++;
			}
		}
		if(node.getMetric(MetricNames.CINT)!=0){
			float cdisp = (float)numberOfclasses/(float)node.getMetric(MetricNames.CINT);
			node.setMetric(getName(), cdisp);
		}else{
			node.setMetric(getName(), 1);
		}
	}

}
