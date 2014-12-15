package concad.core.smells.detectors;
import concad.core.constant.MetricNames;
import concad.core.smells.BrainMethod;
import concad.core.smells.detectors.configuration.BrainMethodDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.storage.MethodMetrics;
import concad.metrics.storage.NodeMetrics;

public class BrainMethodDetector extends CodeSmellDetector{
	private BrainMethodDetectionConfiguration metricsConfiguration;
	public BrainMethodDetector(BrainMethodDetectionConfiguration metricsConfiguration) {
		this.metricsConfiguration=metricsConfiguration;
	}
	public boolean codeSmellVerify(NodeMetrics methodMetric){
		if(methodMetric.getMetric(MetricNames.LOC)!=null && methodMetric.getMetric(MetricNames.LOC) > metricsConfiguration.getLOC_Greater_VeryHigh() &&
				   methodMetric.getMetric(MetricNames.MNL)!=null && methodMetric.getMetric(MetricNames.MNL) >= metricsConfiguration.getMAXNESTING_GreaterEqual_DEEP() &&
				   //methodMetric.getMetric(MetricNames.WMC)!=null && methodMetric.getMetric(MetricNames.WMC)/2 >= MetricThresholds.DEEP &&
				   methodMetric.getMetric(MetricNames.WMC)!=null && methodMetric.getMetric(MetricNames.WMC) >= metricsConfiguration.getWMC_GreaterEqual_Many() &&
				   methodMetric.getMetric(MetricNames.NOF)!=null && methodMetric.getMetric(MetricNames.NOF) >= metricsConfiguration.getNOF_GreaterEqual_SMemCap()
				   ){
				   
			methodMetric.setAttribute(MetricNames.BM, Boolean.valueOf(true));
			return true;
		}
		return false;
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric){
		return new BrainMethod(((MethodMetrics) methodMetric));
	}

}
