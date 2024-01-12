package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersAbstract;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrder;

public abstract class EMSCParametersModelModelPartialAbstract<A extends PartialOrder, B extends PartialOrder>
		extends EMSCParametersAbstract<A, B> implements EMSCParametersModelModelPartial<A, B> {

	private DistanceMatrix<A, B> distanceMatrixHigher;
	private LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategyA;
	private LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategyB;

	public EMSCParametersModelModelPartialAbstract(DistanceMatrix<A, B> distanceMatrixLower,
			DistanceMatrix<A, B> distanceMatrixHigher,
			LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategyA,
			LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategyB, boolean debug,
			boolean computeStochasticTraceAlignments) {
		super(debug, computeStochasticTraceAlignments);
		this.setDebug(debug);
		this.generationStrategyA = generationStrategyA;
		this.generationStrategyB = generationStrategyB;
		this.distanceMatrix = distanceMatrixLower;
		this.distanceMatrixHigher = distanceMatrixHigher;
	}

	@Override
	public DistanceMatrix<A, B> getDistanceMatrixBest() {
		return distanceMatrixHigher;
	}

	public void setDistanceMatrixHigher(DistanceMatrix<A, B> distanceMatrixHigher) {
		this.distanceMatrixHigher = distanceMatrixHigher;
	}

	@Override
	public LanguageGenerationStrategyFromModelPartialOrderImpl getGenerationStrategyA() {
		return generationStrategyA;
	}

	public void setGenerationStrategyA(LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategyA) {
		this.generationStrategyA = generationStrategyA;
	}

	@Override
	public LanguageGenerationStrategyFromModelPartialOrderImpl getGenerationStrategyB() {
		return generationStrategyB;
	}

	public void setGenerationStrategyB(LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategyB) {
		this.generationStrategyB = generationStrategyB;
	}

}