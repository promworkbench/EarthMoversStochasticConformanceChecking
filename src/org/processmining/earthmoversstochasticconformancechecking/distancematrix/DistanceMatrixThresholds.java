package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import java.math.BigInteger;

public class DistanceMatrixThresholds {

	public DistanceMatrixThresholds(double defaultValue) {
		this.defaultValueOnFailure = defaultValue;
	}

	/**
	 * In the best case, we want a lower bound on distance. If we cannot compute
	 * the distance, take a higher value that is still a lower bound.
	 */
	public static final DistanceMatrixThresholds best = new DistanceMatrixThresholds(1);
	public static final DistanceMatrixThresholds worst = new DistanceMatrixThresholds(0);

	//init vectors
	private static int initListSize = 4096;

	//heuristic limits
	private static BigInteger maxTotalOrdersToPreferExhaustiveOverAStar = BigInteger.valueOf(1000); //if the partial order has more than this total orders, go for A*
	private static int maxNumberOfCountTotalOrdersBaseCase = 5; //if there are more than this events in a partial order, do not attempt to count the number of total orders
	private static int minTotalOrderLengthToPreferExhaustiveOverAStar = 20; //if the total order is shorter than this, go for A*

	//hard limits
	private final double defaultValueOnFailure;
	private static final long failAfterNumberOfAStarSteps = 1000000;
	private static final BigInteger failOnMoreExhaustiveStepsThen = BigInteger.valueOf(100000);

	public BigInteger getFailOnMoreExhaustiveStepsThen() {
		return failOnMoreExhaustiveStepsThen;
	}

	public long getFailAfterNumberOfAStarSteps() {
		return failAfterNumberOfAStarSteps;
	}

	public double getDefaultValueOnFailure() {
		return defaultValueOnFailure;
	}

	public int getMaxNumberOfCountTotalOrdersBaseCase() {
		return maxNumberOfCountTotalOrdersBaseCase;
	}

	public BigInteger getMaxTotalOrdersToPreferExhaustiveOverAStar() {
		return maxTotalOrdersToPreferExhaustiveOverAStar;
	}

	public static int getMinTotalOrderLengthToPreferExhaustiveOverAStar() {
		return minTotalOrderLengthToPreferExhaustiveOverAStar;
	}

	public static int getInitListSize() {
		return initListSize;
	}
}