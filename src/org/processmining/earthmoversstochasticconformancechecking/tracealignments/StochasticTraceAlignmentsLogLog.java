package org.processmining.earthmoversstochasticconformancechecking.tracealignments;

import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

/**
 * Wrapper class as ProM does not support generics.
 * 
 * @author sander
 *
 */
public class StochasticTraceAlignmentsLogLog extends StochasticTraceAlignmentsAbstract<String, String> {

	public StochasticTraceAlignmentsLogLog(ReallocationMatrix relocationMatrix, double similarity,
			StochasticLanguage<TotalOrder> languageA, StochasticLanguage<TotalOrder> languageB) {
		super(relocationMatrix, similarity, languageA, languageB);
	}

}