package concad.core.smells.detectors;

import java.util.List;

import concad.core.smells.RefusedParentBequest;
import concad.core.smells.detectors.configuration.RefusedParentBequestDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.NodeMetrics;

public class RefusedParentBequestDetector extends CodeSmellDetector{
	
	private RefusedParentBequestDetectionConfiguration metricConfiguration;
	public RefusedParentBequestDetector(RefusedParentBequestDetectionConfiguration metricConfiguration) {
		this.metricConfiguration=metricConfiguration;
	}
	
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		if(isChild(classMetrics) && 
				childClassIgnoresBequest((ClassMetrics) classMetrics) &&
				childClassIsNotSmallAndSimple((ClassMetrics) classMetrics)){		   
			return true;
		}
		return false;	
	}

	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new RefusedParentBequest(((ClassMetrics) classMetrics));
	}
	

	
	public boolean childClassIgnoresBequest(ClassMetrics classMetrics){
		if(((classMetrics.getMetric(MetricNames.NProtM)!=null && classMetrics.getMetric(MetricNames.NProtM)>metricConfiguration.getNProtM_Greater_Few() &&
				classMetrics.getMetric(MetricNames.BUR)!=null && classMetrics.getMetric(MetricNames.BUR)<metricConfiguration.getBUR_Less_OneThird()))||
				(classMetrics.getMetric(MetricNames.BOvR)!=null && classMetrics.getMetric(MetricNames.BOvR)<metricConfiguration.getBOvR_Less_OneThird())){
			return true;
		}
		return false;
	}
	
	public boolean childClassIsNotSmallAndSimple(ClassMetrics classMetrics){
		if(((classMetrics.getMetric(MetricNames.AMW)!=null && classMetrics.getMetric(MetricNames.AMW)>metricConfiguration.getAMW_Greater_AMWAvg() ||
				classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC)>metricConfiguration.getWMC_Greater_WMCAvg()))&&
				(classMetrics.getMetric(MetricNames.NOM)!=null && classMetrics.getMetric(MetricNames.NOM)>metricConfiguration.getNOM_Greater_NOMAvg())){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private boolean isChild(NodeMetrics classMetrics){
		List<String> nameOfClasses = (List<String>) classMetrics.getAttribute(MetricNames.nameOfClasses);
		if(((ClassMetrics) classMetrics).getDeclaration().resolveBinding().getSuperclass()!=null && 
				nameOfClasses.contains(((ClassMetrics) classMetrics).getDeclaration().resolveBinding().getSuperclass().getBinaryName()) ){
			return true;
		}
		return false;
	}

}
