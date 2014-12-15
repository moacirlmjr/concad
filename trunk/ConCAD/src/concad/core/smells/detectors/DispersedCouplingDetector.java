package concad.core.smells.detectors;

import concad.core.constant.MetricNames;
import concad.core.smells.DispersedCoupling;
import concad.core.smells.detectors.configuration.DispersedCouplingDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.storage.MethodMetrics;
import concad.metrics.storage.NodeMetrics;

public class DispersedCouplingDetector extends CodeSmellDetector{
	private DispersedCouplingDetectionConfiguration metricConfiguration;
	public DispersedCouplingDetector(DispersedCouplingDetectionConfiguration metricConfiguration) {
		this.metricConfiguration=metricConfiguration;
	}
	public boolean codeSmellVerify(NodeMetrics methodMetric){
		if(methodMetric.getMetric(MetricNames.MNL)!=null && methodMetric.getMetric(MetricNames.MNL) > metricConfiguration.getMAXNESTING_Greater_Shallow()  &&
		   methodMetric.getMetric(MetricNames.CDISP)!=null && methodMetric.getMetric(MetricNames.CDISP) >= metricConfiguration.getCDISP_GreaterEqual_Half() &&
		   methodMetric.getMetric(MetricNames.CINT)!=null && methodMetric.getMetric(MetricNames.CINT) > metricConfiguration.getCINT_Greater_SMemCap()
		   ){

			return true;
		}
		return false;
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric){
		return new DispersedCoupling(((MethodMetrics) methodMetric));
	}

}
