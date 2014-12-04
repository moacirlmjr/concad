package concad.core.smells.interfaces;

import concad.metrics.storage.NodeMetrics;

public abstract class CodeSmellDetector {
	
	public abstract boolean codeSmellVerify(NodeMetrics nodeMetrics);
	
	public abstract CodeSmell codeSmellDetected(NodeMetrics nodeMetrics);
	
}
