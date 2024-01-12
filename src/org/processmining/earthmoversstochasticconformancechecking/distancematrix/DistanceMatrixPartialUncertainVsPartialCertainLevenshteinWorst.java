package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import org.processmining.earthmoversstochasticconformancechecking.helperclasses.SymbolicNumber;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderCertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderUncertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrder2TotalOrders;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderCountTotalOrders;
import org.processmining.framework.plugin.ProMCanceller;

public class DistanceMatrixPartialUncertainVsPartialCertainLevenshteinWorst
		extends DistanceMatrixAbstract<PartialOrderUncertain, PartialOrderCertain> {

	private DistanceMatrixTotalVsPartialCertainLevenshtein totalVsPartial = new DistanceMatrixTotalVsPartialCertainLevenshtein();

	/**
	 * A is uncertain, B is certain
	 */
	protected double getDistance(int[] partialOrderA, int[] partialOrderB, ProMCanceller canceller) {
		return getDistance(partialOrderA, partialOrderB, DistanceMatrixThresholds.worst, canceller);
	}

	protected double getDistance(int[] partialOrderA, int[] partialOrderB, DistanceMatrixThresholds thresholds,
			ProMCanceller canceller) {

		SymbolicNumber sizeA = PartialOrderCountTotalOrders.count(partialOrderA,
				thresholds.getMaxNumberOfCountTotalOrdersBaseCase(), canceller);
		SymbolicNumber sizeB = PartialOrderCountTotalOrders.count(partialOrderB,
				thresholds.getMaxNumberOfCountTotalOrdersBaseCase(), canceller);

		/*
		 * We cannot swap A and B -- that would compute something different --
		 * so if it's too large just return 1. It's the worst case after all.
		 */
		if (sizeA.isNumber() && sizeA.bigIntegerValue().compareTo(thresholds.getFailOnMoreExhaustiveStepsThen()) >= 0) {
			System.out.println("   == give up on " + sizeA + " x " + sizeB + " traces");
			return thresholds.getDefaultValueOnFailure();
		}

		//System.out.println("   consider " + sizeA + " x " + sizeB + " traces in " + Thread.currentThread().getName());

		double max = -1;
		Iterable<int[]> totalOrdersA = PartialOrder2TotalOrders.getTotalOrders(partialOrderA, canceller);
		for (int[] totalOrderA : totalOrdersA) {
			double min = totalVsPartial.getDistance(totalOrderA, partialOrderB, canceller);

			if (canceller.isCancelled()) {
				return Double.NaN;
			}

			max = Math.max(max, min);
		}

		return max;
	}

}