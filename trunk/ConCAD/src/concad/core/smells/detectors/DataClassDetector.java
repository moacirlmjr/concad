package concad.core.smells.detectors;

import concad.core.constant.MetricNames;
import concad.core.smells.DataClass;
import concad.core.smells.detectors.configuration.DataClassDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.NodeMetrics;

public class DataClassDetector extends CodeSmellDetector{
	private DataClassDetectionConfiguration metricConfiguration;
	public DataClassDetector(DataClassDetectionConfiguration metricConfiguration) {
		this.metricConfiguration=metricConfiguration;
	}
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.NOAM)!=null && classMetrics.getMetric(MetricNames.NOPA)!=null){
			int sum = classMetrics.getMetric(MetricNames.NOAM).intValue() + (int)classMetrics.getMetric(MetricNames.NOPA).intValue(); 
			if(  classMetrics.getMetric(MetricNames.WOC)!=null && classMetrics.getMetric(MetricNames.WOC) < metricConfiguration.getWOC_Less_OneThird() &&
			   ((sum > metricConfiguration.getNOAP_SOAP_Greater_Few() &&
			   classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC) >= metricConfiguration.getWMC_Less_High())||
			   (sum > metricConfiguration.getNOAP_SOAP_Greater_Many() &&
					   classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC) >= metricConfiguration.getWMC_Less_VeryHigh()))){		   
			return true;
			}
		}
		return false;	
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new DataClass(((ClassMetrics) classMetrics));
	}

}
