package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersAbstract;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrder;

public abstract class EMSCParametersLogModelPartialAbstract<A extends Order, B extends PartialOrder>
		extends EMSCParametersAbstract<A, B> implements EMSCParametersLogModelPartial<A, B> {

	private XEventClassifier eventClassifier;
	private DistanceMatrix<A, B> distanceMatrixBest;
	private LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategy;

	public EMSCParametersLogModelPartialAbstract(DistanceMatrix<A, B> distanceMatrixWorst,
			DistanceMatrix<A, B> distanceMatrixBest, XEventClassifier eventClassifier,
			LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategy, boolean debug) {
		super(debug, false);
		this.eventClassifier = eventClassifier;
		this.setDebug(debug);
		this.generationStrategy = generationStrategy;
		this.distanceMatrix = distanceMatrixWorst;
		this.distanceMatrixBest = distanceMatrixBest;
	}

	@Override
	public XEventClassifier getClassifierA() {
		return eventClassifier;
	}

	public void setLogClassifier(XEventClassifier eventClassifier) {
		this.eventClassifier = eventClassifier;
	}

	@Override
	public DistanceMatrix<A, B> getDistanceMatrixBest() {
		return distanceMatrixBest;
	}

	public void setDistanceMatrixHigher(DistanceMatrix<A, B> distanceMatrixBest) {
		this.distanceMatrixBest = distanceMatrixBest;
	}

	@Override
	public LanguageGenerationStrategyFromModelPartialOrderImpl getGenerationStrategyB() {
		return generationStrategy;
	}

	public void setGenerationStrategy(LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategy) {
		this.generationStrategy = generationStrategy;
	}

}