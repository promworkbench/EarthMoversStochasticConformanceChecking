package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public interface EMSCParametersLogLog extends EMSCParameters<TotalOrder, TotalOrder> {
	public XEventClassifier getClassifierA();

	public XEventClassifier getClassifierB();

}
