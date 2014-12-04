package concad.core.smells.detectors;

import concad.core.smells.IntensiveCoupling;
import concad.core.smells.detectors.configuration.IntensiveCouplingDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.constants.MetricNames;
import concad.metrics.storage.MethodMetrics;
import concad.metrics.storage.NodeMetrics;

public class IntensiveCouplingDetector extends CodeSmellDetector{
	private IntensiveCouplingDetectionConfiguration metricConfiguration;
	
	public IntensiveCouplingDetector(IntensiveCouplingDetectionConfiguration metricConfiguration) {
		this.metricConfiguration=metricConfiguration;
	}
	
	public boolean codeSmellVerify(NodeMetrics methodMetric){
		if(methodMetric.getMetric(MetricNames.MNL)!=null && methodMetric.getMetric(MetricNames.MNL) > metricConfiguration.getMAXNESTING_Greater_SHALLOW() &&
				
		    ((methodMetric.getMetric(MetricNames.CDISP)!=null && methodMetric.getMetric(MetricNames.CDISP) < metricConfiguration.getCDISP_Less_OneQuarter() &&
		    methodMetric.getMetric(MetricNames.CINT)!=null && methodMetric.getMetric(MetricNames.CINT) > metricConfiguration.getCINT_Greater_Few())||(
			methodMetric.getMetric(MetricNames.CDISP)!=null && methodMetric.getMetric(MetricNames.CDISP) < metricConfiguration.getCDISP_Less_Half() &&
			methodMetric.getMetric(MetricNames.CINT)!=null && methodMetric.getMetric(MetricNames.CINT) > metricConfiguration.getCINT_Greater_SMemCap()))	   
		   ){

			return true;
		}
		return false;
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric){
		return new IntensiveCoupling(((MethodMetrics) methodMetric));
	}

}
