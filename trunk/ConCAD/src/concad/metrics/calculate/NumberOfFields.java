package concad.metrics.calculate;

import java.util.List;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class NumberOfFields implements IAttribute{

	@Override
	public void calculate(ClassMetrics node) {
		TypeDeclaration declaration = (TypeDeclaration) node.getDeclaration();
		node.setMetric(getName(), declaration.getFields().length);
	}

	@Override
	public String getName() {
		return MetricNames.NOF;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void calculate(MethodMetrics node) {
		List fields = (List) node.getAttribute(MetricNames.NOAV);
		if(fields!=null){
			node.setMetric(getName(), fields.size());
		}
	}

}
