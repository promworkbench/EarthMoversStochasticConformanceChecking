package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrixNormalisedLevenshtein;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

public class EMSCParametersDefault {
	public static final DistanceMatrix<TotalOrder, TotalOrder> defaultDistanceMatrix = new DistanceMatrixNormalisedLevenshtein();
	public static final XEventClassifier defaultClassifier = MiningParameters.getDefaultClassifier();
	public static final LanguageGenerationStrategyFromModelDefault defaultTerminationStrategy = new LanguageGenerationStrategyFromModelDefault();
	public static final boolean defaultDebug = false;
	public static final boolean defaultComputeStochasticTraceAlignments = true;

	public static final int defaultNumberOfThreads = Runtime.getRuntime().availableProcessors() - 1;
}