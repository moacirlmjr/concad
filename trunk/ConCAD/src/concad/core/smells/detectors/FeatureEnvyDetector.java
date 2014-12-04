package concad.core.smells.detectors;

import concad.core.smells.FeatureEnvy;
import concad.core.smells.detectors.configuration.FeatureEnvyDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.constants.MetricNames;
import concad.metrics.storage.MethodMetrics;
import concad.metrics.storage.NodeMetrics;

public class FeatureEnvyDetector extends CodeSmellDetector{
	private FeatureEnvyDetectionConfiguration metricConfiguration;
	public FeatureEnvyDetector(FeatureEnvyDetectionConfiguration metricConfiguration) {
		this.metricConfiguration=metricConfiguration;
	}
	
	public boolean codeSmellVerify(NodeMetrics methodMetric){
		if(methodMetric.getMetric(MetricNames.ATFD)!=null && methodMetric.getMetric(MetricNames.ATFD) > metricConfiguration.getATFD_Greater_Few() &&
		   methodMetric.getMetric(MetricNames.LAA)!=null && methodMetric.getMetric(MetricNames.LAA) < metricConfiguration.getLAA_Less_OneThird() &&
		   methodMetric.getMetric(MetricNames.FDP)!=null && methodMetric.getMetric(MetricNames.FDP) <= metricConfiguration.getFDP_LessEqual_FEW()){
		   
			return true;
		}
		return false;	
	}
		
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric){
		return new FeatureEnvy(((MethodMetrics) methodMetric));
	}
	
}
