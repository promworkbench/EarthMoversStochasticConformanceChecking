package org.processmining.earthmoversstochasticconformancechecking.parameters;

public class EMSCParametersLogModelDefault extends EMSCParametersLogModelAbstract {

	public EMSCParametersLogModelDefault() {
		super(EMSCParametersDefault.defaultDistanceMatrix, EMSCParametersDefault.defaultClassifier,
				EMSCParametersDefault.defaultTerminationStrategy, EMSCParametersDefault.defaultDebug,
				EMSCParametersDefault.defaultComputeStochasticTraceAlignments,
				EMSCParametersDefault.defaultNumberOfThreads);
	}

}
