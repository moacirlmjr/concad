package concad.metrics.calculate;

import java.util.List;

import concad.core.constant.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class ChangingMethods implements IAttribute{
	
	@Override
	public void calculate(ClassMetrics node) {}

	@Override
	public String getName() {
		return MetricNames.CM;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void calculate(MethodMetrics node) {
		if(node.getAttribute(MetricNames.ListOfMethodInvoking)!=null){
			List<String> listOfMethodInvoking = (List<String>) node.getAttribute(MetricNames.ListOfMethodInvoking);
			node.setMetric(getName(), listOfMethodInvoking.size());
		}
	}

}
