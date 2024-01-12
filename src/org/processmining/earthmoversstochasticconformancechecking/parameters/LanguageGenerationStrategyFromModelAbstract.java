package org.processmining.earthmoversstochasticconformancechecking.parameters;

public class LanguageGenerationStrategyFromModelAbstract implements LanguageGenerationStrategyFromModel {

	private long maxDuration;
	private double maxMassCovered;
	private long maxTraces;
	private double epsilon;

	private long startTime;

	public LanguageGenerationStrategyFromModelAbstract(long maxDuration, double maxMassCovered, long maxTraces,
			double epsilon) {
		this.maxDuration = maxDuration;
		this.maxMassCovered = maxMassCovered;
		this.maxTraces = maxTraces;
		this.epsilon = epsilon;
	}

	public void initialise() {
		startTime = System.currentTimeMillis();
	}

	public boolean isTerminated(double massCovered, long traces) {
		if (massCovered + epsilon >= maxMassCovered) {
			return true;
		}
		if (System.currentTimeMillis() - startTime > maxDuration) {
			return true;
		}
		if (traces >= maxTraces) {
			return true;
		}
		return false;
	}

	public LanguageGenerationStrategyFromModelAbstract clone() {
		LanguageGenerationStrategyFromModelAbstract result;
		try {
			result = (LanguageGenerationStrategyFromModelAbstract) super.clone();
			result.maxDuration = maxDuration;
			result.maxMassCovered = maxMassCovered;
			result.epsilon = epsilon;
			result.maxTraces = maxTraces;
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}

	public double getMaxMassCovered() {
		return maxMassCovered;
	}

	public void setMaxMassCovered(double maxMassCovered) {
		this.maxMassCovered = maxMassCovered;
	}

	public long getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(long maxDuration) {
		this.maxDuration = maxDuration;
	}

	public void setMaxTraces(long maxTraces) {
		this.maxTraces = maxTraces;
	}

	public long getMaxTraces() {
		return maxTraces;
	}
}