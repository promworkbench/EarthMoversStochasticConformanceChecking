package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrixTotalVsPartialCertainLevenshtein;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderCertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public class EMSCParametersLogTotalModelPartialCertainDefault
		extends EMSCParametersLogModelPartialAbstract<TotalOrder, PartialOrderCertain> {

	public static final DistanceMatrix<TotalOrder, PartialOrderCertain> defaultDistanceMatrixLower = null;
	public static final DistanceMatrix<TotalOrder, PartialOrderCertain> defaultDistanceMatrixHigher = new DistanceMatrixTotalVsPartialCertainLevenshtein();

	public static final LanguageGenerationStrategyFromModelPartialOrderImpl defaultGenerationStrategy = new LanguageGenerationStrategyFromModelPartialOrderImpl();

	public EMSCParametersLogTotalModelPartialCertainDefault() {
		super(defaultDistanceMatrixLower, defaultDistanceMatrixHigher, EMSCParametersDefault.defaultClassifier,
				defaultGenerationStrategy.clone(), EMSCParametersDefault.defaultDebug);
	}

	public TotalOrder getOrderA() {
		return TotalOrder.instance();
	}

	public PartialOrderCertain getOrderB() {
		return PartialOrderCertain.instance();
	}

}
