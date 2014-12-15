package concad.metrics.calculate;

import concad.core.constant.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class LocalityOfAttributesAccesses implements IAttribute{
	
	@Override
	public void calculate(ClassMetrics node) {
	}

	@Override
	public String getName() {
		return MetricNames.LAA;
	}

	@Override
	public void calculate(MethodMetrics node) {
		float laa = 1;
		if(node.getMetric(MetricNames.ATLD)!=null && node.getMetric(MetricNames.ATFD)!=null){
			if(node.getMetric(MetricNames.ATLD)+node.getMetric(MetricNames.ATFD)!=0){
				laa = node.getMetric(MetricNames.ATLD)/(node.getMetric(MetricNames.ATLD)+node.getMetric(MetricNames.ATFD));
			}
		}
		node.setMetric(getName(),laa);
	}
	
}
