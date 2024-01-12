package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

public interface LanguageGenerationStrategyFromModelPartialOrder {
	public int getNumberOfTracesWithHighestProbability();

	public int getNumberOfTracesRandomWalk();

	public long getSeed();
}
