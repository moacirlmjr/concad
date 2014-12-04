package concad.core.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import concad.core.graph.CallGraph;
import concad.metrics.calculate.AccessToData;
import concad.metrics.calculate.AverageMethodWeight;
import concad.metrics.calculate.BaseClassUsageRatio;
import concad.metrics.calculate.ChangingClasses;
import concad.metrics.calculate.ChangingMethods;
import concad.metrics.calculate.CouplingDispersion;
import concad.metrics.calculate.CouplingIntensity;
import concad.metrics.calculate.ForeignDataProviders;
import concad.metrics.calculate.IAttribute;
import concad.metrics.calculate.LocalityOfAttributesAccesses;
import concad.metrics.calculate.MaximumNestingLevel;
import concad.metrics.calculate.McCabe;
import concad.metrics.calculate.NameOfFields;
import concad.metrics.calculate.NamesOfInvokedClasses;
import concad.metrics.calculate.NamesOfInvokingClasses;
import concad.metrics.calculate.NumOverrideMethods;
import concad.metrics.calculate.NumProtMembersInParent;
import concad.metrics.calculate.NumberOfAccessorMethods;
import concad.metrics.calculate.NumberOfAddedServices;
import concad.metrics.calculate.NumberOfFields;
import concad.metrics.calculate.NumberOfLinesOfCode;
import concad.metrics.calculate.NumberOfMethods;
import concad.metrics.calculate.NumberOfPublicFields;
import concad.metrics.calculate.NumberOfPublicMethods;
import concad.metrics.calculate.PercentageOfAddedServices;
import concad.metrics.calculate.SuperClassHierarchy;
import concad.metrics.calculate.TightClassCohesion;
import concad.metrics.calculate.WeightOfClass;
import concad.metrics.constants.MetricNames;
import concad.metrics.storage.ClassMetrics;
import concad.metrics.storage.MethodMetrics;

/**
 * It adds the interactions between methods, field and etc in the current file
 * to the callGraph object.
 * 
 * @author Luciano Sampaio
 * @Date: 2014-05-07
 * @Version: 02
 */
public class VisitorCompilationUnit extends ASTVisitor {

	HashMap<TypeDeclaration, ClassMetrics> classesMetrics = new HashMap<TypeDeclaration, ClassMetrics>();
	List<IAttribute> metricForClasses = new ArrayList<IAttribute>();
	List<IAttribute> metricForMethods = new ArrayList<IAttribute>();
	List<IAttribute> couplingMetrics = new ArrayList<IAttribute>();

	List<IAttribute> changingMetrics = new ArrayList<IAttribute>();

	List<IAttribute> invokingAttributes = new ArrayList<IAttribute>();

	List<String> nameOfClasses = new ArrayList<String>();

	public VisitorCompilationUnit(IResource resource, CallGraph callGraph) {
		this();
	}
	
	public VisitorCompilationUnit(){
		super();
		System.out.println("Visitor2");
		this.metricForClasses.add(new NameOfFields());
		this.metricForClasses.add(new McCabe());
		this.metricForClasses.add(new NumberOfMethods());
		this.metricForClasses.add(new NumberOfFields());
		this.metricForClasses.add(new AccessToData());
		this.metricForClasses.add(new TightClassCohesion());
		this.metricForClasses.add(new NumberOfLinesOfCode());
		this.metricForClasses.add(new NumberOfPublicFields());
		this.metricForClasses.add(new NumberOfPublicMethods());
		this.metricForClasses.add(new WeightOfClass());
		this.metricForClasses.add(new NumberOfAccessorMethods());
		this.metricForClasses.add(new AverageMethodWeight());
		this.metricForClasses.add(new NumProtMembersInParent(nameOfClasses));
		this.metricForClasses.add(new NumOverrideMethods(nameOfClasses));
		this.metricForClasses.add(new BaseClassUsageRatio());
		this.metricForClasses.add(new NumberOfAddedServices());
		this.metricForClasses.add(new PercentageOfAddedServices());

		this.metricForMethods.add(new NameOfFields());
		this.metricForMethods.add(new McCabe());
		this.metricForMethods.add(new AccessToData());
		this.metricForMethods.add(new NumberOfFields());
		this.metricForMethods.add(new TightClassCohesion());
		this.metricForMethods.add(new NumberOfLinesOfCode());
		this.metricForMethods.add(new MaximumNestingLevel());
		this.metricForMethods.add(new LocalityOfAttributesAccesses());
		this.metricForMethods.add(new ForeignDataProviders());

		this.couplingMetrics.add(new CouplingIntensity());
		this.couplingMetrics.add(new CouplingDispersion());

		this.invokingAttributes.add(new NamesOfInvokedClasses(nameOfClasses));
		this.invokingAttributes.add(new NamesOfInvokingClasses());

		this.changingMetrics.add(new ChangingClasses());
		this.changingMetrics.add(new ChangingMethods());
	}

	@Override
	public boolean visit(TypeDeclaration clazz) {
		System.out.println("VisitorCU visit");
		if (!nameOfClasses.contains(clazz.resolveBinding().getBinaryName())) {
			nameOfClasses.add(clazz.resolveBinding().getBinaryName());
		}
		boolean teste = true;
		TypeDeclaration tdAux = null;
		for (TypeDeclaration t : classesMetrics.keySet()) {
			if (t.resolveBinding().getBinaryName().equals(clazz.resolveBinding().getBinaryName())) {
				teste = false;
				tdAux = t;
				break;
			}
		}

		ClassMetrics classMetrics = new ClassMetrics(clazz);
		if (teste) {
			classesMetrics.put(clazz, classMetrics);
		} else {
			classesMetrics.remove(tdAux);
			classesMetrics.put(clazz, classMetrics);
		}

		return super.visit(clazz);
	}

	public void executeMetrics() {
		for (TypeDeclaration clazz : classesMetrics.keySet()) {
			ClassMetrics classMetrics = classesMetrics.get(clazz);
			classMetrics.setAttribute(MetricNames.nameOfClasses, nameOfClasses);
			IAttribute superClasses = new SuperClassHierarchy(nameOfClasses);
			superClasses.calculate(classMetrics);

			MethodDeclaration[] methods = clazz.getMethods();
			for (MethodDeclaration method : methods) {
				MethodMetrics methodMetrics = new MethodMetrics(method, classMetrics);
				for (IAttribute metric : metricForMethods) {
					metric.calculate(methodMetrics);
				}
				classMetrics.getMethodsMetrics().add(methodMetrics);
			}

			for (IAttribute metric : metricForClasses) {
				metric.calculate(classMetrics);
			}
		}
	}

	public List<ClassMetrics> getLClassesMetrics() {
		List<ClassMetrics> lclassMetrics = new ArrayList<ClassMetrics>();
		for (TypeDeclaration clazz : classesMetrics.keySet()) {
			lclassMetrics.add(classesMetrics.get(clazz));
		}
		return lclassMetrics;
	}

	private void executeInvokingAttributes() {
		for (IAttribute invokingAttribute : invokingAttributes) {
			for (ClassMetrics classMetrics : getLClassesMetrics()) {
				for (MethodMetrics methodMetric : classMetrics.getMethodsMetrics()) {
					invokingAttribute.calculate(methodMetric);
				}
				invokingAttribute.calculate(classMetrics);
			}
		}
	}

	public void calculateAditionalMetrics() {

		for (IAttribute couplingMetric : couplingMetrics) {
			for (ClassMetrics classMetric : getLClassesMetrics()) {
				for (MethodMetrics methodMetric : classMetric.getMethodsMetrics()) {
					couplingMetric.calculate(methodMetric);
				}
				couplingMetric.calculate(classMetric);
			}
		}

		executeInvokingAttributes();

		for (IAttribute changingMetric : changingMetrics) {
			for (ClassMetrics classMetric : getLClassesMetrics()) {
				for (MethodMetrics methodMetric : classMetric.getMethodsMetrics()) {
					changingMetric.calculate(methodMetric);
				}
				changingMetric.calculate(classMetric);
			}
		}
	}

}
