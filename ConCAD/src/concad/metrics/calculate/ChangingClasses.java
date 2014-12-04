package concad.metrics.calculate;

import java.util.List;

import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class ChangingClasses implements IAttribute{
	
	@Override
	public void calculate(ClassMetrics node) {}

	@Override
	public String getName() {
		return MetricNames.CC;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void calculate(MethodMetrics node) {
		if(node.getAttribute(MetricNames.ListOfClassInvoking)!=null){
			List<String> listOfClassInvoking = (List<String>) node.getAttribute(MetricNames.ListOfClassInvoking);
			node.setMetric(getName(), listOfClassInvoking.size());
		}
	}

}
