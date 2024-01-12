package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrixPartialCertainVsPartialCertainLevenshtein;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderCertain;

public class EMSCParametersModelPartialCertainModelPartialCertainDefault
		extends EMSCParametersModelModelPartialAbstract<PartialOrderCertain, PartialOrderCertain> {

	public static final DistanceMatrix<PartialOrderCertain, PartialOrderCertain> defaultDistanceMatrixLower = new DistanceMatrixPartialCertainVsPartialCertainLevenshtein();
	public static final DistanceMatrix<PartialOrderCertain, PartialOrderCertain> defaultDistanceMatrixHigher = new DistanceMatrixPartialCertainVsPartialCertainLevenshtein();

	public static final LanguageGenerationStrategyFromModelPartialOrderImpl defaultGenerationStrategy = new LanguageGenerationStrategyFromModelPartialOrderImpl();

	public EMSCParametersModelPartialCertainModelPartialCertainDefault() {
		super(defaultDistanceMatrixLower, defaultDistanceMatrixHigher, defaultGenerationStrategy.clone(),
				defaultGenerationStrategy.clone(), EMSCParametersDefault.defaultDebug,
				EMSCParametersDefault.defaultComputeStochasticTraceAlignments);
	}

	public PartialOrderCertain getOrderA() {
		return PartialOrderCertain.instance();
	}

	public PartialOrderCertain getOrderB() {
		return PartialOrderCertain.instance();
	}

}
