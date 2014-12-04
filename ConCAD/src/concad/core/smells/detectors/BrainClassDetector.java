package concad.core.smells.detectors;

import concad.core.smells.BrainClass;
import concad.core.smells.detectors.configuration.BrainClassDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;
import concad.metrics.storage.NodeMetrics;

public class BrainClassDetector extends CodeSmellDetector{
	
	private BrainClassDetectionConfiguration metricsConfiguration;
	
	public BrainClassDetector(BrainClassDetectionConfiguration metricsConfiguration) {
		this.metricsConfiguration=metricsConfiguration;
	}
	
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		boolean isGodclass = classMetrics.getAttribute(MetricNames.BM)!=null?(Boolean)(classMetrics.getAttribute(MetricNames.BM)):false;
		if(!isGodclass && ((isVeryLarge(classMetrics)||isExtremelyLarge(classMetrics))&&isVeryComplexAndNonCohesive(classMetrics))){		   
			return true;
		}
		return false;	
	}

	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new BrainClass(((ClassMetrics) classMetrics));
	}
	
	public int countBrainMethods(NodeMetrics classMetrics){
		int countBrainMethods=0;
		for(MethodMetrics methodMetrics:((ClassMetrics)classMetrics).getMethodsMetrics()){
			if(methodMetrics.getAttribute(MetricNames.BM)!=null && (Boolean)(methodMetrics.getAttribute(MetricNames.BM))){
				countBrainMethods++;
			}
		}
		return countBrainMethods;
	}
	
	private boolean isVeryLarge(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.LOC)!=null && classMetrics.getMetric(MetricNames.LOC)>=metricsConfiguration.getLOC_GreaterEqual_VeryHigh()){
			if(countBrainMethods(classMetrics)>1){
				return true;
			}
		}
		return false;
	}
	
	private boolean isExtremelyLarge(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.LOC)!=null && classMetrics.getMetric(MetricNames.LOC)>=metricsConfiguration.getLOC_GreaterEqual_2xVeryHigh()
				&& classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC)>=metricsConfiguration.getWMC_GreaterEqual_2xVeryHigh()){
			if(countBrainMethods(classMetrics)==1){
				return true;
			}
		}
		return false;
	}
	
	
	private boolean isVeryComplexAndNonCohesive(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.TCC)!=null && classMetrics.getMetric(MetricNames.TCC)<metricsConfiguration.getTCC_Less_Half()
				&& classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC)>=metricsConfiguration.getWMC_GreaterEqual_VeryHigh()){
			return true;
		}
		return false;
	}

}
