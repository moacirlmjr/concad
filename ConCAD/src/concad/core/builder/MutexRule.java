package concad.core.builder;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author Luciano Sampaio
 */
public class MutexRule implements ISchedulingRule {
	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		System.out.println("MutexRule1");
		return rule == this;
	}

	@Override
	public boolean contains(ISchedulingRule rule) {
		System.out.println("MutexRule2");
		return rule == this;
	}
}
