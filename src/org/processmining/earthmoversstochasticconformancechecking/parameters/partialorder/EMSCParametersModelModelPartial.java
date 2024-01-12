package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersBounds;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrder;

public interface EMSCParametersModelModelPartial<A extends PartialOrder, B extends PartialOrder>
		extends EMSCParametersBounds<A, B> {
	public LanguageGenerationStrategyFromModelPartialOrder getGenerationStrategyA();

	public LanguageGenerationStrategyFromModelPartialOrder getGenerationStrategyB();

}