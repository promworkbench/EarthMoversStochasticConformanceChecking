package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public interface EMSCParametersLogModel extends EMSCParameters<TotalOrder, TotalOrder> {
	public XEventClassifier getClassifierA();

	public LanguageGenerationStrategyFromModel getTerminationStrategyB();

	public int getNumberOfThreads();

}