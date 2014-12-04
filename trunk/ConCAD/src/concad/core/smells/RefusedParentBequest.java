package concad.core.smells;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.core.smells.constants.SmellNames;
import concad.core.smells.interfaces.CodeSmell;
import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;

public class RefusedParentBequest extends CodeSmell {
	public RefusedParentBequest(ClassMetrics node) {
		super(SmellNames.REFUSED_BEQUEST);
		this.element = node.getDeclaration();
		this.node = node;
	}

	@Override
	public TypeDeclaration getMainClass() {
		if (this.getElement() instanceof TypeDeclaration) {
			return ((TypeDeclaration) (this.getElement()));
		}
		return null;
	}

	/**
	 * All the super classes of the main class of the smell
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getAffectedClasses() {
		return new TreeSet<String>(
				(List<String>) (node.getAttribute(MetricNames.SC)));
	}

}
