package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import org.processmining.earthmoversstochasticconformancechecking.partialorder.IIteratorConcurrent;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.IIteratorOneTrace;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.IIteratorPermutations;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.IIteratorSequence;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.PartialOrderSequenceCut;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.PartialOrderSplit;
import org.processmining.earthmoversstochasticconformancechecking.partialorder.PartialOrderXorCut;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;

import gnu.trove.list.TIntList;

public class PartialOrder2TotalOrders {

	public static interface IIterable extends Iterable<int[]> {
		@Override
		public IIterator iterator();
	}

	public static interface IIterator extends Iterator<int[]> {
		public int[] get();

		public void reset();

		/**
		 * The same for all traces.
		 * 
		 * @return
		 */
		public int getTraceLength();
	}

	public static IIterable getTotalOrders(int[] partialOrder, ProMCanceller canceller) {
		if (canceller.isCancelled()) {
			return null;
		}

		if (PartialOrderUtils.getNumberOfEvents(partialOrder) <= 1) {
			return getTotalOrdersLengthOne(partialOrder, canceller);
		}

		if (PartialOrderUtils.isFullyConcurrent(partialOrder)) {
			return getTotalOrdersFullyConcurrent(partialOrder);
		}

		if (PartialOrderUtils.isTotalOrder(partialOrder)) {
			return getTotalOrdersLengthOne(partialOrder, canceller);
		}

		//check if there's a sequence cut
		{
			TIntList[] partition = PartialOrderSequenceCut.findCut(partialOrder);
			if (partition != null) {
				//we have a sequence cut; split the partial order accordingly
				int[][] subPartialOrders = PartialOrderSplit.split(partialOrder, partition);

				//recurse
				IIterable[] subIterables = new IIterable[subPartialOrders.length];
				for (int i = 0; i < subPartialOrders.length; i++) {
					subIterables[i] = getTotalOrders(subPartialOrders[i], canceller);
				}
				return sequenceIterator(subIterables);
			}
		}

		//check if there's a xor cut
		{
			TIntList[] partition = PartialOrderXorCut.findCut(partialOrder);
			if (partition != null) {
				//we have an xor cut; split the partial order accordingly
				int[][] subPartialOrders = PartialOrderSplit.split(partialOrder, partition);

				//recurse
				IIterable[] subIterables = new IIterable[subPartialOrders.length];
				for (int i = 0; i < subPartialOrders.length; i++) {
					subIterables[i] = getTotalOrders(subPartialOrders[i], canceller);
				}
				IIterable result = concurrentIterator(subIterables);

				return result;
			}
		}

		if (canceller.isCancelled()) {
			return null;
		}

		return getTotalOrdersBaseCase(partialOrder, canceller);
	}

	private static IIterable getTotalOrdersFullyConcurrent(int[] partialOrder) {
		int[] totalOrder = PartialOrderUtils.getATotalOrder(partialOrder);
		return new IIterable() {
			public IIterator iterator() {
				return new IIteratorPermutations(totalOrder);
			}
		};
	}

	/**
	 * Combine the iterators sequentially.
	 * 
	 * @param subIterables
	 * @return
	 */
	protected static IIterable sequenceIterator(final IIterable[] subIterables) {
		return new IIterable() {
			public IIterator iterator() {
				//initialise sub-iterators
				List<IIterator> subIterators = new ArrayList<>();
				for (IIterable subIterable : subIterables) {
					IIterator subIt = subIterable.iterator();
					subIterators.add(subIt);
				}

				return new IIteratorSequence(subIterators);
			}
		};
	}

	protected static IIterable concurrentIterator(final IIterable[] subIterables) {
		return new IIterable() {
			public IIterator iterator() {
				//initialise sub-iterators
				List<IIterator> subIterators = new ArrayList<>();
				for (IIterable subIterable : subIterables) {
					IIterator subIt = subIterable.iterator();
					subIterators.add(subIt);
				}

				return new IIteratorConcurrent(subIterators);
			}
		};
	}

	public static IIterable getTotalOrdersLengthOne(int[] partialOrder, ProMCanceller canceller) {
		return new IIterable() {
			public IIterator iterator() {
				return new IIteratorOneTrace(partialOrder);
			}
		};
	}

	public static IIterable getTotalOrdersBaseCase(int[] partialOrder, ProMCanceller canceller) {
		ArrayDeque<Pair<int[], BitSet>> queue = new ArrayDeque<>();
		queue.add(Pair.of(TotalOrderUtils.emptyTotalOrder(), PartialOrderUtils.getNewState(partialOrder)));

		return new IIterable() {
			public IIterator iterator() {
				return new IIterator() {
					int[] last = null;

					public int getTraceLength() {
						return PartialOrderUtils.getNumberOfEvents(partialOrder);
					}

					public int[] get() {
						return last;
					}

					public void reset() {
						queue.clear();
						queue.add(Pair.of(TotalOrderUtils.emptyTotalOrder(),
								PartialOrderUtils.getNewState(partialOrder)));
					}

					public int[] next() {
						while (!queue.isEmpty()) {
							Pair<int[], BitSet> item = queue.poll();
							int[] prefix = item.getA();
							BitSet state = item.getB();

							boolean somethingEnabled = false;
							for (int eventIndex = 0; eventIndex < PartialOrderUtils
									.getNumberOfEvents(partialOrder); eventIndex++) {

								if (canceller.isCancelled()) {
									return null;
								}

								if (PartialOrderUtils.isEnabled(partialOrder, eventIndex, state)) {
									int activityIndex = PartialOrderUtils.getActivity(partialOrder, eventIndex);
									int[] newPrefix = TotalOrderUtils.addEvent(prefix, activityIndex);
									BitSet newState = PartialOrderUtils.takeStep(partialOrder, eventIndex,
											(BitSet) state.clone());

									queue.addFirst(Pair.of(newPrefix, newState));

									somethingEnabled = true;
								}
							}

							if (!somethingEnabled) {
								last = prefix;
								return prefix;
							}
						}
						assert false;
						return null;
					}

					public boolean hasNext() {
						return !queue.isEmpty() && !canceller.isCancelled();
					}
				};
			}
		};
	}
}
