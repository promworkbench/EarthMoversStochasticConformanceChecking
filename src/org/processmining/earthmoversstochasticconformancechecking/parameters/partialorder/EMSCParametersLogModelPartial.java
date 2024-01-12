package org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersBounds;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;

public interface EMSCParametersLogModelPartial<A extends Order, B extends Order> extends EMSCParametersBounds<A, B> {
	public XEventClassifier getClassifierA();

	public LanguageGenerationStrategyFromModelPartialOrder getGenerationStrategyB();

}