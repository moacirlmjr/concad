package concad.metrics.calculate;

import concad.core.constant.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class NumberOfAddedServices implements IAttribute{

	@Override
	public void calculate(ClassMetrics node) {
		int numAddServices = 0;
		if(node.getMetric(MetricNames.NPOvMAns)!=null && node.getMetric(MetricNames.NOPM)!=null){
			numAddServices = (int) (node.getMetric(MetricNames.NOPM) - node.getMetric(MetricNames.NPOvMAns)); 
		}
		node.setMetric(getName(), numAddServices);
	}

	@Override
	public String getName() {
		return MetricNames.NAS;
	}

	@Override
	public void calculate(MethodMetrics node) {
		// TODO Auto-generated method stub
		
	}

}
