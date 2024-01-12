package org.processmining.earthmoversstochasticconformancechecking.partialorder;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrder2TotalOrders.IIterator;

/**
 * Iterate over all possible concurrent steps of this iterator.
 * 
 * @author sander
 *
 */
public class IIteratorConcurrent implements IIterator {

	private final IIterator subIteratorA;
	private final IIterator subIteratorB;
	private final int traceLength;

	private Iterator<int[]> combinationsIterator;
	private boolean initialised;
	private int[] combination; //list of indices where A has an event
	private int[] lastObtainedResult; //last obtained result

	public IIteratorConcurrent(List<IIterator> subIterators) {
		//we can only handle binary concurrency, so split up the iterators if there are more
		assert subIterators.size() >= 2;
		if (subIterators.size() == 2) {
			subIteratorA = subIterators.get(0);
			subIteratorB = subIterators.get(1);
		} else if (subIterators.size() == 3) {
			subIteratorA = new IIteratorConcurrent(subIterators.subList(0, 2));
			subIteratorB = subIterators.get(2);
		} else {
			int split = subIterators.size() / 2;
			subIteratorA = new IIteratorConcurrent(subIterators.subList(0, split));
			subIteratorB = new IIteratorConcurrent(subIterators.subList(split, subIterators.size()));
		}

		traceLength = subIteratorA.getTraceLength() + subIteratorB.getTraceLength();

		reset();
	}

	public int getTraceLength() {
		return traceLength;
	}

	public boolean hasNext() {
		return combinationsIterator.hasNext() || subIteratorA.hasNext() || subIteratorB.hasNext();
	}

	public int[] next() {
		if (!initialised) {
			combination = combinationsIterator.next();
			subIteratorA.next();
			subIteratorB.next();
			initialised = true;
		} else {
			//progress the iterators one by one in a cascaded clock-like way
			if (combinationsIterator.hasNext()) {
				combination = combinationsIterator.next();
			} else {
				combinationsIterator = CombinatoricsUtils.combinationsIterator(traceLength,
						subIteratorA.getTraceLength());
				combination = combinationsIterator.next();

				//progress A
				if (subIteratorA.hasNext()) {
					subIteratorA.next();
				} else {
					subIteratorA.reset();
					subIteratorA.next();

					//progress B
					subIteratorB.next();
				}
			}
		}

		lastObtainedResult = combine();

		return lastObtainedResult;
	}

	public int[] get() {
		return lastObtainedResult;
	}

	public void reset() {
		//reset all child iterators
		combinationsIterator = CombinatoricsUtils.combinationsIterator(traceLength, subIteratorA.getTraceLength());
		subIteratorA.reset();
		subIteratorB.reset();
		initialised = false;
	}

	public int[] combine() {
		int[] result = new int[traceLength];
		int eventIndexA = 0;
		int eventIndexB = 0;
		int c = 0;

		for (int eventIndex = 0; eventIndex < traceLength; eventIndex++) {
			if (c < combination.length && eventIndex == combination[c]) {
				//progress A
				result[eventIndex] = subIteratorA.get()[eventIndexA];
				eventIndexA++;

				//progress combination
				c++;
			} else {
				//progress b
				result[eventIndex] = subIteratorB.get()[eventIndexB];
				eventIndexB++;
			}
		}

		return result;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("con(");
		result.append(subIteratorA.toString());
		result.append(", ");
		result.append(subIteratorB.toString());
		result.append(")");
		return result.toString();
	}
}