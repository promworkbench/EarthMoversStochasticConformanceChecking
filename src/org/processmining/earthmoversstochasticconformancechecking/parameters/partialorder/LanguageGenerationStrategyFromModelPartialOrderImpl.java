package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

public class LanguageGenerationStrategyFromModelPartialOrderImpl
		implements LanguageGenerationStrategyFromModelPartialOrder, Cloneable {

	private int numberOfTracesWithHighestProbability = 1000;
	private int numberOfTracesRandomWalk = 1000;
	private long seed = System.currentTimeMillis();

	public int getNumberOfTracesWithHighestProbability() {
		return numberOfTracesWithHighestProbability;
	}

	public void setNumberOfTracesWithHighestProbability(int numberOfTracesWithHighestProbability) {
		this.numberOfTracesWithHighestProbability = numberOfTracesWithHighestProbability;
	}

	public int getNumberOfTracesRandomWalk() {
		return numberOfTracesRandomWalk;
	}

	public void setNumberOfTracesRandomWalk(int numberOfTracesRandomWalk) {
		this.numberOfTracesRandomWalk = numberOfTracesRandomWalk;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	@Override
	public LanguageGenerationStrategyFromModelPartialOrderImpl clone() {
		LanguageGenerationStrategyFromModelPartialOrderImpl result = null;
		try {
			result = (LanguageGenerationStrategyFromModelPartialOrderImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		result.numberOfTracesRandomWalk = numberOfTracesRandomWalk;
		result.numberOfTracesWithHighestProbability = numberOfTracesWithHighestProbability;
		result.seed = seed;
		return result;
	}

}