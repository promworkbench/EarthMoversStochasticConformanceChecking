package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public abstract class EMSCParametersLogModelAbstract extends EMSCParametersAbstract<TotalOrder, TotalOrder>
		implements EMSCParametersLogModel {
	private XEventClassifier eventClassifier;
	private LanguageGenerationStrategyFromModelAbstract terminationStrategy;
	private int numberOfThreads;

	public EMSCParametersLogModelAbstract(DistanceMatrix<TotalOrder, TotalOrder> distanceMatrix,
			XEventClassifier eventClassifier, LanguageGenerationStrategyFromModelAbstract terminationStrategy,
			boolean debug, boolean computeStochasticTraceAlignments, int numberOfThreads) {
		super(debug, computeStochasticTraceAlignments);
		this.eventClassifier = eventClassifier;
		this.terminationStrategy = terminationStrategy;
		this.setDebug(debug);
		this.numberOfThreads = numberOfThreads;
		setDistanceMatrix(distanceMatrix);
	}

	public XEventClassifier getClassifierA() {
		return eventClassifier;
	}

	public void setLogClassifier(XEventClassifier eventClassifier) {
		this.eventClassifier = eventClassifier;
	}

	public LanguageGenerationStrategyFromModelAbstract getTerminationStrategyB() {
		return terminationStrategy;
	}

	public void setModelTerminationStrategy(LanguageGenerationStrategyFromModelAbstract terminationStrategy) {
		this.terminationStrategy = terminationStrategy;
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