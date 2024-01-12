package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrixPartialUncertainVsPartialCertainLevenshteinBest;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrixPartialUncertainVsPartialCertainLevenshteinWorst;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderCertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderUncertain;

public class EMSCParametersLogPartialUncertainModelPartialCertainDefault
		extends EMSCParametersLogModelPartialAbstract<PartialOrderUncertain, PartialOrderCertain> {

	public static final DistanceMatrix<PartialOrderUncertain, PartialOrderCertain> defaultDistanceMatrixBest = new DistanceMatrixPartialUncertainVsPartialCertainLevenshteinBest();
	public static final DistanceMatrix<PartialOrderUncertain, PartialOrderCertain> defaultDistanceMatrixWorst = new DistanceMatrixPartialUncertainVsPartialCertainLevenshteinWorst();

	public EMSCParametersLogPartialUncertainModelPartialCertainDefault() {
		super(defaultDistanceMatrixWorst, defaultDistanceMatrixBest, EMSCParametersDefault.defaultClassifier,
				EMSCParametersLogTotalModelPartialCertainDefault.defaultGenerationStrategy.clone(),
				EMSCParametersDefault.defaultDebug);
	}

	public PartialOrderUncertain getOrderA() {
		return PartialOrderUncertain.instance();
	}

	public PartialOrderCertain getOrderB() {
		return PartialOrderCertain.instance();
	}

}
