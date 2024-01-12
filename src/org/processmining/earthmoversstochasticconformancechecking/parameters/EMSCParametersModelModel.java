package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public interface EMSCParametersModelModel extends EMSCParameters<TotalOrder, TotalOrder> {

	public LanguageGenerationStrategyFromModel getTerminationStrategyA();

	public LanguageGenerationStrategyFromModel getTerminationStrategyB();

	public int getNumberOfThreads();
}
