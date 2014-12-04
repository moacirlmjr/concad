package concad.core.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

public class ReferenceManager {

	private final List<VariableReferenceElement>	references;

	public ReferenceManager() {
		references = new ArrayList<VariableReferenceElement>();
	}

	public void add(ASTNode reference, int contextId) {
		references.add(new VariableReferenceElement(reference, contextId));
	}

	public boolean hasReference(ASTNode reference, int contextId) {
		for (VariableReferenceElement current : references) {
			if ((current.getReference().equals(reference)) && (current.getContextId() == contextId)) {
				return true;
			}
		}

		return false;
	}

}
