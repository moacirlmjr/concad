package concad.metrics.calculate;

import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class NumberOfLinesOfCode implements IAttribute{

	@Override
	public void calculate(ClassMetrics node) {
		String classCode = node.getDeclaration().toString();;
		node.setMetric(getName(), countLines(classCode));
	}

	@Override
	public String getName() {
		return MetricNames.LOC;
	}

	@Override
	public void calculate(MethodMetrics node) {
		if(node.getDeclaration().getBody()!=null){
			String methodCode = node.getDeclaration().getBody().toString();
			node.setMetric(getName(), countLines(methodCode));
		}else{
			node.setMetric(getName(),0);			
		}	
	}
	
	
	private static int countLines(String str){
	   String[] lines = str.split("\r\n|\r|\n");
	   return  lines.length;
	}

}
