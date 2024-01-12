package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public class EMSCParametersModelModelAbstract extends EMSCParametersAbstract<TotalOrder, TotalOrder>
		implements EMSCParametersModelModel {
	private LanguageGenerationStrategyFromModelAbstract strategyA;
	private LanguageGenerationStrategyFromModelAbstract strategyB;
	private int numberOfThreads;

	public EMSCParametersModelModelAbstract(DistanceMatrix<TotalOrder, TotalOrder> distanceMatrix,
			LanguageGenerationStrategyFromModelAbstract strategyA,
			LanguageGenerationStrategyFromModelAbstract strategyB, boolean debug,
			boolean computeStochasticTraceAlignments, int numberOfThreads) {
		super(debug, computeStochasticTraceAlignments);
		this.strategyA = strategyA;
		this.strategyB = strategyB;
		this.numberOfThreads = numberOfThreads;
		setDistanceMatrix(distanceMatrix);
	}

	public LanguageGenerationStrategyFromModelAbstract getTerminationStrategyA() {
		return strategyA;
	}

	public void setTerminationStrategyA(LanguageGenerationStrategyFromModelAbstract terminationStrategyA) {
		this.strategyA = terminationStrategyA;
	}

	public LanguageGenerationStrategyFromModelAbstract getTerminationStrategyB() {
		return strategyB;
	}

	public void setTerminationStrategyB(LanguageGenerationStrategyFromModelAbstract terminationStrategyB) {
		this.strategyB = terminationStrategyB;
	}

	public TotalOrder getOrderA() {
		return TotalOrder.instance();
	}

	public TotalOrder getOrderB() {
		return TotalOrder.instance();
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

}