package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.BitSet;

import org.processmining.earthmoversstochasticconformancechecking.helperclasses.SymbolicNumber;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.PartialOrderSequenceCut;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.PartialOrderSplit;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.PartialOrderXorCut;
import org.processmining.framework.plugin.ProMCanceller;

import gnu.trove.list.TIntList;

public class PartialOrderCountTotalOrders {

	/**
	 * 
	 * @param partialOrder
	 * @param maxBaseCase
	 *            the maximum number of events for which brute force counting
	 *            will be applied after reduction. Instead, result will be
	 *            "limit exceeded".
	 * @param canceller
	 * @return
	 */
	public static SymbolicNumber count(int[] partialOrder, int maxBaseCase, ProMCanceller canceller) {
		if (PartialOrderUtils.getNumberOfEvents(partialOrder) < 2) {
			return new SymbolicNumber(BigInteger.ONE);
		}

		if (PartialOrderUtils.isTotalOrder(partialOrder)) {
			//			System.out.println("   total order with " + PartialOrderUtils.getNumberOfEvents(partialOrder) + " events");
			return new SymbolicNumber(BigInteger.ONE);
		}

		if (canceller.isCancelled()) {
			return null;
		}

		{
			TIntList[] xorPartition = PartialOrderXorCut.findCut(partialOrder);
			if (xorPartition != null) {
				//				System.out.println("  xor cut " + Arrays.deepToString(xorPartition));
				return countXor(partialOrder, xorPartition, maxBaseCase, canceller);
			}
		}

		if (canceller.isCancelled()) {
			return null;
		}

		{
			TIntList[] sequencePartition = PartialOrderSequenceCut.findCut(partialOrder);
			if (sequencePartition != null) {
				//				System.out.println("  sequence cut " + Arrays.deepToString(sequencePartition));
				return countSequence(partialOrder, sequencePartition, maxBaseCase, canceller);
			}
		}

		if (canceller.isCancelled()) {
			return null;
		}

		//		System.out.println(PartialOrder2String.toString(partialOrder));
		//		System.out.println("   base case with " + PartialOrderUtils.getNumberOfEvents(partialOrder) + " events");

		return countBaseCase(partialOrder, maxBaseCase, canceller);
	}

	public static SymbolicNumber countXor(int[] partialOrder, TIntList[] partition, int maxBaseCase,
			ProMCanceller canceller) {
		//https://math.stackexchange.com/questions/987514/counting-permutations-that-respect-a-partial-order
		SymbolicNumber sum = new SymbolicNumber(BigInteger.ZERO);
		SymbolicNumber product = new SymbolicNumber(BigInteger.ONE);

		int[][] subPartialOrders = PartialOrderSplit.split(partialOrder, partition);

		for (int[] subPartialOrder : subPartialOrders) {
			SymbolicNumber sr = count(subPartialOrder, maxBaseCase, canceller);

			if (canceller.isCancelled()) {
				return null;
			}

			int numberOfEvents = PartialOrderUtils.getNumberOfEvents(subPartialOrder);
			SymbolicNumber subResult = sr.multiply(new SymbolicNumber(BigInteger.valueOf(numberOfEvents)));
			sum = sum.add(subResult);
			product = product.multiply(subResult.factorial());
		}

		SymbolicNumber result = sum.factorial().divide(product);
		return result;
	}

	public static SymbolicNumber countSequence(int[] partialOrder, TIntList[] partition, int maxBaseCase,
			ProMCanceller canceller) {
		SymbolicNumber result = new SymbolicNumber(BigInteger.ONE);

		int[][] subPartialOrders = PartialOrderSplit.split(partialOrder, partition);

		for (int[] subPartialOrder : subPartialOrders) {
			SymbolicNumber subResult = count(subPartialOrder, maxBaseCase, canceller);

			if (canceller.isCancelled()) {
				return null;
			}

			result = result.multiply(subResult);
		}
		return result;
	}

	public static SymbolicNumber countBaseCase(int[] partialOrder, int maxBaseCase, ProMCanceller canceller) {

		int numberOfEvents = PartialOrderUtils.getNumberOfEvents(partialOrder);

		if (numberOfEvents > maxBaseCase) {
			return SymbolicNumber.NaN("limit exceeded");
		}

		BigInteger result = BigInteger.ZERO;

		ArrayDeque<BitSet> queue = new ArrayDeque<>();
		queue.add(new BitSet(numberOfEvents));

		while (!queue.isEmpty()) {

			if (canceller.isCancelled()) {
				return null;
			}

			BitSet state = queue.poll();

			boolean somethingEnabled = false;
			for (int eventIndex = numberOfEvents; (eventIndex = state.previousClearBit(eventIndex - 1)) >= 0;) {
				boolean allIncomingEdgesExecuted = true;
				for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
						eventIndex); edgeIndex++) {
					int sourceEventIndex = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder, eventIndex,
							edgeIndex);

					if (!state.get(sourceEventIndex)) {
						allIncomingEdgesExecuted = false;
						break;
					}
				}

				if (allIncomingEdgesExecuted) {
					BitSet newState = (BitSet) state.clone();
					newState.set(eventIndex);
					queue.addFirst(newState);

					somethingEnabled = true;
				}
			}

			if (!somethingEnabled) {
				result = result.add(BigInteger.ONE);
			}
		}

		return new SymbolicNumber(result);
	}
}