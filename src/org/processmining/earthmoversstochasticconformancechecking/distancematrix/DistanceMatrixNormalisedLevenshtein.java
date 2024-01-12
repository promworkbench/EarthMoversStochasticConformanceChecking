package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import org.processmining.earthmoversstochasticconformancechecking.helperclasses.Levenshtein;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.framework.plugin.ProMCanceller;

public class DistanceMatrixNormalisedLevenshtein extends DistanceMatrixAbstract<TotalOrder, TotalOrder> {

	protected double getDistance(int[] traceB, int[] traceL, ProMCanceller canceller) {
		return Levenshtein.getNormalisedDistance(traceB, traceL);
	}

}
