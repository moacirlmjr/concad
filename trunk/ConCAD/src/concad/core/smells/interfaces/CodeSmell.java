package concad.core.smells.interfaces;

import java.util.Calendar;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.core.design.DesignFlaw;
import concad.core.log.PluginLogger;
import concad.metrics.storage.NodeMetrics;

public abstract class CodeSmell extends DesignFlaw {
	private String kindOfSmellName;
	protected ASTNode element;
	protected NodeMetrics node;	
	private Calendar detectedAt;
	
	
	public CodeSmell(String kindOfSmellName) {
		PluginLogger.logInfo("CodeSmell / constructor( " + kindOfSmellName.toString() + " )");

		this.kindOfSmellName = kindOfSmellName;
		this.setDetectedAt(Calendar.getInstance());
	}
	
	public Calendar getDetectedAt() {
		return detectedAt;
	}
	
	public void setDetectedAt(Calendar detectedAt) {
		this.detectedAt = detectedAt;
	}	

	public ASTNode getElement() {
		return element;
	}

	public String getKindOfSmellName() {
		return kindOfSmellName;
	}

	public String getMainClassName() {
		// System.out.println(getMainClass().resolveBinding().getQualifiedName());
		return getMainClass().resolveBinding().getQualifiedName();
	}

	public abstract TypeDeclaration getMainClass();

	public abstract Set<String> getAffectedClasses();

	public String getElementName() {
		if (element instanceof TypeDeclaration) {
			return ((TypeDeclaration) (element)).getName().toString();
		} else if (element instanceof MethodDeclaration) {
			return ((TypeDeclaration) ((MethodDeclaration) (element))
					.getParent()).getName()
					+ "."
					+ ((MethodDeclaration) (element)).getName().toString();
		}
		return element.toString();
		
		
		
	}

	public int getLine() {
		IJavaElement javaElement = this.getMainClass().resolveBinding().getJavaElement();
		ICompilationUnit compUnit = (ICompilationUnit) javaElement.getAncestor(IJavaElement.COMPILATION_UNIT);
		return (this.getLineNumFromOffset(compUnit, element.getStartPosition()));// this.getiMethod().getSourceRange().getOffset()));
	}

	private int getLineNumFromOffset(ICompilationUnit cUnit, int offSet) {
		try {
			String source = cUnit.getSource();
			IType type = cUnit.findPrimaryType();
			if (type != null) {
				String sourcetodeclaration = source.substring(0, offSet);
				int lines = 0;
				char[] chars = new char[sourcetodeclaration.length()];
				sourcetodeclaration.getChars(0, sourcetodeclaration.length(),
						chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == '\n') {
						lines++;
					}
				}
				return lines + 1;
			}
		} catch (JavaModelException jme) {
		}
		return 0;
	}

	
	
}
