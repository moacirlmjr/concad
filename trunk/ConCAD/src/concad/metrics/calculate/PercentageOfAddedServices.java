package concad.metrics.calculate;

import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class PercentageOfAddedServices implements IAttribute{
	@Override
	public void calculate(ClassMetrics node) {
		float numAddServices = 0;
		if(node.getMetric(MetricNames.NAS)!=null && node.getMetric(MetricNames.NOPM)!=null && node.getMetric(MetricNames.NOPM)>0){
			numAddServices = (float)node.getMetric(MetricNames.NAS) / (float)node.getMetric(MetricNames.NOPM); 
		}
		node.setMetric(getName(), numAddServices);
	}

	@Override
	public String getName() {
		return MetricNames.PNAS;
	}

	@Override
	public void calculate(MethodMetrics node) {
		// TODO Auto-generated method stub
		
	}

}
