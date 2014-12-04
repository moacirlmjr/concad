package concad.metrics.calculate;

import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public interface IAttribute {
	
	public void calculate(ClassMetrics node);
	
	public void calculate(MethodMetrics node);
	
	public String getName();

}
