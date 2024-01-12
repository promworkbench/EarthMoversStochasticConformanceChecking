package org.processmining.earthmoversstochasticconformancechecking.parameters;

public class LanguageGenerationStrategyFromModelDefault extends LanguageGenerationStrategyFromModelAbstract {

	public final static long defaultMaxDuration = 1000 * 60 * 20;
	public final static double defaultMaxMassCovered = 0.95;
	public final static long defaultMaxTraces = Long.MAX_VALUE;
	public final static double defaultEpsilon = 0.0000000001;

	public LanguageGenerationStrategyFromModelDefault() {
		super(defaultMaxDuration, defaultMaxMassCovered, defaultMaxTraces, defaultEpsilon);
	}

	public LanguageGenerationStrategyFromModelDefault clone() {
		return (LanguageGenerationStrategyFromModelDefault) super.clone();
	}
}