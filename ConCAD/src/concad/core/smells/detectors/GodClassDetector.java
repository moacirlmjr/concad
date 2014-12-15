package concad.core.smells.detectors;

import concad.core.constant.MetricNames;
import concad.core.smells.GodClass;
import concad.core.smells.detectors.configuration.GodClassDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.NodeMetrics;

public class GodClassDetector extends CodeSmellDetector{
	private GodClassDetectionConfiguration metricConfiguration;
	public GodClassDetector(GodClassDetectionConfiguration metricConfiguration) {
		this.metricConfiguration=metricConfiguration;
	}
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.ATFD)!=null && classMetrics.getMetric(MetricNames.ATFD) > metricConfiguration.getATFD_Greater_FEW()  &&
		   classMetrics.getMetric(MetricNames.TCC)!=null && classMetrics.getMetric(MetricNames.TCC) < metricConfiguration.getTCC_Less_OneThird() &&
		   classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC) >= metricConfiguration.getWMC_GreaterEqual_VeryHigh()){
		   
			classMetrics.setAttribute(MetricNames.GC, Boolean.valueOf(true));
			return true;
		}
		return false;	
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new GodClass(((ClassMetrics) classMetrics));
	}

}
