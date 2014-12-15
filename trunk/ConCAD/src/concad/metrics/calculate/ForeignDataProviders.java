package concad.metrics.calculate;

import java.util.ArrayList;
import java.util.List;

import concad.core.constant.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class ForeignDataProviders implements IAttribute{
	
	@Override
	public void calculate(ClassMetrics node) {
		float sumOfATFD = 0;
		for(MethodMetrics methodMetrics:node.getMethodsMetrics()){
			sumOfATFD = sumOfATFD + methodMetrics.getMetric(getName());
		}
		node.setMetric(getName(), sumOfATFD);
	}

	@Override
	public String getName() {
		return MetricNames.FDP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void calculate(MethodMetrics node) {
		List<String> listOfATFD = (List<String>)node.getAttribute(MetricNames.ListOfATFD);
		List<String> listFDP = new ArrayList<String>();
		for(String atfd:listOfATFD){
			if(!listFDP.contains(atfd)){
				listFDP.add(atfd);
			}
		}		
		node.setMetric(getName(),listFDP.size());
	}
	
}
