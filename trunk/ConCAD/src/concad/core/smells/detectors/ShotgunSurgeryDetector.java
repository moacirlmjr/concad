package concad.core.smells.detectors;

import java.util.List;

import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

import concad.core.constant.MetricNames;
import concad.core.smells.ShotgunSurgery;
import concad.core.smells.detectors.configuration.ShotgunSurgeryDetectionConfiguration;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.metrics.storage.MethodMetrics;
import concad.metrics.storage.NodeMetrics;

public class ShotgunSurgeryDetector extends CodeSmellDetector {
	private ShotgunSurgeryDetectionConfiguration metricConfiguration;
	public ShotgunSurgeryDetector(ShotgunSurgeryDetectionConfiguration metricConfiguration) {
		this.metricConfiguration=metricConfiguration;
	}
	@Override
	public boolean codeSmellVerify(NodeMetrics methodMetric) {
		if(isNonConstructorAndNonStatic(((MethodMetrics) methodMetric).getDeclaration()) &&
				methodMetric.getMetric(MetricNames.CC)!=null && methodMetric.getMetric(MetricNames.CC) >=metricConfiguration.getCC_GreaterEqual_MANY()  &&
		   methodMetric.getMetric(MetricNames.CM)!=null && methodMetric.getMetric(MetricNames.CM) > metricConfiguration.getCM_Greater_SMemCap()
		   ){
			return true;
		}
		return false;
	}

	@Override
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric) {
		return new ShotgunSurgery(((MethodMetrics) methodMetric));
	}
	
	private boolean isNonConstructorAndNonStatic(MethodDeclaration method) {
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = method.modifiers();
		boolean isStatic = false;
		for(IExtendedModifier modifier:modifiers){
			if(modifier.isModifier()){
				if(((Modifier)modifier).isStatic()){
						isStatic=true;
				}
			}
		}
		if(!method.isConstructor()&&!isStatic){
			return true;
		}
		return false;
	}

}
