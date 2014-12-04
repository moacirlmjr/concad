package concad.core.design;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import concad.core.log.PluginLogger;
import concad.core.smells.detectors.BrainClassDetector;
import concad.core.smells.detectors.BrainMethodDetector;
import concad.core.smells.detectors.DataClassDetector;
import concad.core.smells.detectors.DispersedCouplingDetector;
import concad.core.smells.detectors.FeatureEnvyDetector;
import concad.core.smells.detectors.GodClassDetector;
import concad.core.smells.detectors.IntensiveCouplingDetector;
import concad.core.smells.detectors.RefusedParentBequestDetector;
import concad.core.smells.detectors.ShotgunSurgeryDetector;
import concad.core.smells.detectors.TraditionBreakerDetector;
import concad.core.smells.interfaces.CodeSmell;
import concad.core.smells.interfaces.CodeSmellDetector;
import concad.core.util.HelperProjects;
import concad.core.visitor.VisitorCompilationUnit;
import concad.db.DataBaseManager;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

public class DesignFlawManager {
	private static DesignFlawManager instance;
	private Vector<CodeSmell> smells;
	private Vector<CodeSmellDetector> classDetectors;
	private Vector<CodeSmellDetector> methodDetectors;
	VisitorCompilationUnit visitor = new VisitorCompilationUnit();

	public static DesignFlawManager getInstance() {
		PluginLogger.logInfo("DesignFlawManager / getInstance()");
		if (instance == null)
			instance = new DesignFlawManager();
		return instance;
	}

	private DesignFlawManager() {
		initialize();
	}

	public void initialize() {
		PluginLogger.logInfo("DesignFlawManager / initialize()");
		smells = new Vector<CodeSmell>();
		visitor = new VisitorCompilationUnit();
	}

	public Vector<CodeSmell> getSmells() {
		return smells;
	}

	public void countCodeSmellsDebug() {
		PluginLogger.logInfo("DesignFlawManager / countCodeSmellsDebug()");
		HashMap<String, Integer> codeSmellsCount = new HashMap<String, Integer>();
		for (CodeSmell smell : smells) {
			if (codeSmellsCount.get(smell.getKindOfSmellName()) == null) {
				codeSmellsCount.put(smell.getKindOfSmellName(), 1);
			} else {
				codeSmellsCount.put(smell.getKindOfSmellName(), codeSmellsCount.get(smell.getKindOfSmellName()) + 1);
			}
		}
		for (String codeSmell : codeSmellsCount.keySet()) {
			System.out.println(codeSmell + ": " + codeSmellsCount.get(codeSmell));
		}
	}

	public void calculateMetricsCode(IProject project) throws IOException {
		PluginLogger.logInfo("DesignFlawManager / calculateMetricsCode(IProject)");

		try {
			analyseProject(project);
			visitor.executeMetrics();
		} catch (JavaModelException e) {
			e.printStackTrace();
			PluginLogger.logError(e.getMessage(), e);
		}
	}

	public void calculateAditionalMetrics() {
		PluginLogger.logInfo("DesignFlawManager / calculateAditionalMetrics()");
		visitor.calculateAditionalMetrics();
	}

	public void detectCodeSmells(String selectedProject) {
		PluginLogger.logInfo("DesignFlawManager / detectCodeSmells(String)");

		smells = new Vector<CodeSmell>();
		resetDetectors(selectedProject);
		List<ClassMetrics> lclassMetrics = visitor.getLClassesMetrics();
		for (ClassMetrics classMetrics : lclassMetrics) {
			for (MethodMetrics methodMetrics : classMetrics.getMethodsMetrics()) {
				for (CodeSmellDetector methodDetector : methodDetectors) {
					if (methodDetector.codeSmellVerify(methodMetrics)) {
						String classMethod = classMetrics.getDeclaration().getName() + "."
								+ methodMetrics.getDeclaration().getName();
						CodeSmell smell = methodDetector.codeSmellDetected(methodMetrics);
						boolean teste = true;
						int position = 0;
						for (CodeSmell smellAux : smells) {
							if (smellAux.getKindOfSmellName().equalsIgnoreCase(smell.getKindOfSmellName())
									&& classMethod.equalsIgnoreCase(smellAux.getElementName())) {
								teste = false;
								break;
							}
							position++;
						}
						if (teste){
							smells.add(methodDetector.codeSmellDetected(methodMetrics));
						}else{
							CodeSmell ancient = smells.get(position);  
							smells.remove(position);
							CodeSmell newSmell = methodDetector.codeSmellDetected(methodMetrics);
							newSmell.setDetectedAt(ancient.getDetectedAt());
							smells.add(newSmell);
						}
					}
				}
			}
			for (CodeSmellDetector classDetector : classDetectors) {
				if (classDetector.codeSmellVerify(classMetrics)) {
					smells.add(classDetector.codeSmellDetected(classMetrics));
				}
			}
		}
		loadTextMarkers(selectedProject);
	}

	public void detectCodeSmellsVisitor(IResource resource) {
		PluginLogger.logInfo("DesignFlawManager / detectCodeSmellsVisitor(IResource)");
		resetDetectors(resource.getProject().getName());
		List<ClassMetrics> lclassMetrics = visitor.getLClassesMetrics();
		ClassMetrics classMetrics = null;
		for (ClassMetrics classMetricsAux : lclassMetrics) {
			if ((classMetricsAux.getDeclaration().resolveBinding().getBinaryName() + ".java").equals(resource.getProject().getName()+"."+resource.getName())) {
				classMetrics = classMetricsAux;
			}
		}

		if (classMetrics != null) {
			for (MethodMetrics methodMetrics : classMetrics.getMethodsMetrics()) {
				String classMethod = classMetrics.getDeclaration().getName() + "."
						+ methodMetrics.getDeclaration().getName();
				int countMetricsMethodPass = 0;
				for (CodeSmellDetector methodDetector : methodDetectors) {
					if (methodDetector.codeSmellVerify(methodMetrics)) {
						CodeSmell smell = methodDetector.codeSmellDetected(methodMetrics);
						boolean teste = true;
						int position = 0;
						for (CodeSmell smellAux : smells) {
							if (smellAux.getKindOfSmellName().equalsIgnoreCase(smell.getKindOfSmellName())
									&& classMethod.equalsIgnoreCase(smellAux.getElementName())) {
								teste = false;
								break;
							}
							position++;
						}
						if (teste){
							smells.add(methodDetector.codeSmellDetected(methodMetrics));
						}else{
							CodeSmell ancient = smells.get(position);  
							smells.remove(position);
							CodeSmell newSmell = methodDetector.codeSmellDetected(methodMetrics);
							newSmell.setDetectedAt(ancient.getDetectedAt());
							smells.add(newSmell);
						}
					} else {
						countMetricsMethodPass++;
					}
				}
				
				if (countMetricsMethodPass == 5) {
					List<CodeSmell> smellsAux = new ArrayList<>(smells);
					int i = 0;
					for (CodeSmell smell : smellsAux) {
						if (smell.getElementName().equals(classMethod)) {
							smells.remove(i);
							break;
						}
						i++;
					}
				}
			}

			for (CodeSmellDetector classDetector : classDetectors) {
				if (classDetector.codeSmellVerify(classMetrics)) {
					smells.add(classDetector.codeSmellDetected(classMetrics));
				}
			}
		}

		loadTextMarkers(resource.getProject().getName());
	}

	private void resetDetectors(String projectName) {
		PluginLogger.logInfo("DesignFlawManager / resetDetectors(String)");

		classDetectors = new Vector<CodeSmellDetector>();
		classDetectors.add(new GodClassDetector(DataBaseManager.getInstance().getGodClassDetectionConfiguration(projectName)));
		classDetectors.add(new BrainClassDetector(DataBaseManager.getInstance().getBrainClassDetectionConfiguration(projectName)));
		classDetectors.add(new DataClassDetector(DataBaseManager.getInstance().getDataClassDetectionConfiguration(projectName)));
		classDetectors.add(new RefusedParentBequestDetector(DataBaseManager.getInstance().getRefusedParentBequestDetectionConfiguration(projectName)));
		classDetectors.add(new TraditionBreakerDetector(DataBaseManager.getInstance().getTraditionBreakerDetectionConfiguration(projectName)));
		
		methodDetectors = new Vector<CodeSmellDetector>();
		methodDetectors.add(new BrainMethodDetector(DataBaseManager.getInstance().getBrainMethodDetectionConfiguration(projectName)));
		methodDetectors.add(new FeatureEnvyDetector(DataBaseManager.getInstance().getFeatureEnvyDetectionConfiguration(projectName)));
		methodDetectors.add(new DispersedCouplingDetector(DataBaseManager.getInstance().getDispersedCouplingDetectionConfiguration(projectName)));
		methodDetectors.add(new IntensiveCouplingDetector(DataBaseManager.getInstance().getIntensiveCouplingDetectionConfiguration(projectName)));
		methodDetectors.add(new ShotgunSurgeryDetector(DataBaseManager.getInstance().getShotgunSurgeryDetectionConfiguration(projectName)));
	}

	private void loadTextMarkers(String selectedProject) {
		PluginLogger.logInfo("DesignFlawManager / loadTextMarkers()");

		for (IProject project : HelperProjects.getMonitoredProjects()) {
			if (project.getName().equals(selectedProject.replace("/", ""))) {
				deleteSmellMarker(project);
			}
		}

		for (Iterator<CodeSmell> iterator = smells.iterator(); iterator.hasNext();) {
			CodeSmell type = (CodeSmell) iterator.next();

			IJavaElement javaElement = type.getMainClass().resolveBinding().getJavaElement();
			ICompilationUnit compUnit = (ICompilationUnit) javaElement.getAncestor(IJavaElement.COMPILATION_UNIT);

			IMarker marker;
			try {
				deleteSmellMarker(compUnit.getCorrespondingResource(), type.getKindOfSmellName(), type.getLine());
				marker = compUnit.getCorrespondingResource().createMarker(IMarker.PROBLEM);// "codeSmellMarker");
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				marker.setAttribute(IMarker.MESSAGE, type.getKindOfSmellName());
				marker.setAttribute(IMarker.LINE_NUMBER, type.getLine());// 1);
				marker.setAttribute(IMarker.TRANSIENT, false);
				// marker.setAttribute(IMarker.CHAR_START,type.getElement().getStartPosition());
				// marker.setAttribute(IMarker.CHAR_END,type.getElement().getStartPosition()+type.getElement().getLength());
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

	}

	private void deleteSmellMarker(IResource target, String kind, int line) {
		PluginLogger.logInfo("DesignFlawManager / deleteSmellMarker(IResource, String, int)");
		
		try {
			IMarker[] markers = target.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for (int i = 0; i < markers.length; i++) {
				String markerMessage = (String) markers[i].getAttribute(IMarker.MESSAGE);
				Integer lineNumber = (Integer) markers[i].getAttribute(IMarker.LINE_NUMBER);
				if (markerMessage != null && lineNumber != null) {
					if ((markerMessage.equals(kind)) && (lineNumber == line)) {
						markers[i].delete();
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void deleteSmellMarker(IProject target) {
		PluginLogger.logInfo("DesignFlawManager / deleteSmellMarker(IProject)");

		try {

			IMarker[] markers = target.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for (int i = 0; i < markers.length; i++) {
				String markerMessage = (String) markers[i].getAttribute(IMarker.MESSAGE);
				Integer lineNumber = (Integer) markers[i].getAttribute(IMarker.LINE_NUMBER);
				if (markerMessage != null && lineNumber != null) {
					markers[i].delete();
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	
	public ASTNode getCompilationUnit(ASTNode node) {
		if (node.getNodeType() == ASTNode.COMPILATION_UNIT)
			return node;
		else {
			return getAncestorCU(node);
		}
	}

	
	public ASTNode getAncestorCU(ASTNode element) {
		while (element != null) {
			if (element.getNodeType() == ASTNode.COMPILATION_UNIT)
				return element;
			element = element.getParent();
		}
		return null;
	}

	private void analyseProject(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				createAST(mypackage);
			}
		}
	}

	private void createAST(IPackageFragment mypackage) throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			CompilationUnit parse = parse(unit);
			parse.accept(visitor);
		}
	}


	private static CompilationUnit parse(ICompilationUnit unit) {
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}
}
