package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderCertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderUncertain;
import org.processmining.framework.plugin.ProMCanceller;

public class DistanceMatrixPartialUncertainVsPartialCertainLevenshteinBest
		extends DistanceMatrixAbstract<PartialOrderUncertain, PartialOrderCertain> {

	DistanceMatrixPartialCertainVsPartialCertainLevenshtein shadow = new DistanceMatrixPartialCertainVsPartialCertainLevenshtein();

	@Override
	protected double getDistance(int[] partialOrderA, int[] partialOrderB, ProMCanceller canceller) {
		return shadow.getDistance(partialOrderA, partialOrderB, DistanceMatrixThresholds.best, canceller);
	}
}