package org.processmining.earthmoversstochasticconformancechecking.parameters;

public class EMSCParametersModelModelDefault extends EMSCParametersModelModelAbstract {

	public EMSCParametersModelModelDefault() {
		super(EMSCParametersDefault.defaultDistanceMatrix, EMSCParametersDefault.defaultTerminationStrategy,
				EMSCParametersDefault.defaultTerminationStrategy, EMSCParametersDefault.defaultDebug,
				EMSCParametersDefault.defaultComputeStochasticTraceAlignments,
				EMSCParametersDefault.defaultNumberOfThreads);
	}

}
