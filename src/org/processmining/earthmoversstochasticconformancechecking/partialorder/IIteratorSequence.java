package org.processmining.earthmoversstochasticconformancechecking.partialorder;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrder2TotalOrders.IIterator;

public class IIteratorSequence implements IIterator {

	private boolean initialised = false;
	private List<IIterator> subIterators;

	public IIteratorSequence(List<IIterator> subIterators) {
		this.subIterators = subIterators;
	}

	public int getTraceLength() {
		int sum = 0;
		for (IIterator subIterator : subIterators) {
			sum += subIterator.getTraceLength();
		}
		return sum;
	}

	public int[] get() {
		int[] result = subIterators.get(0).get();
		for (int i = 1; i < subIterators.size(); i++) {
			result = ArrayUtils.addAll(result, subIterators.get(i).get());
		}
		return result;
	}

	public int[] next() {
		if (!initialised) {
			//progress all sub-iterators
			for (IIterator subIterator : subIterators) {
				subIterator.next();
			}
			initialised = true;
			return get();
		} else {
			for (IIterator subIterator : subIterators) {
				if (subIterator.hasNext()) {
					subIterator.next();
					return get();
				} else {
					subIterator.reset();
					subIterator.next();
				}
			}
			assert false; //this should have been catched by hasNext()
			return null;
		}
	}

	public boolean hasNext() {
		for (Iterator<int[]> it : subIterators) {
			if (it.hasNext()) {
				return true;
			}
		}
		return false;
	}

	public void reset() {
		for (IIterator subIterator : subIterators) {
			subIterator.reset();
		}
		initialised = false;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("seq(");
		for (Iterator<IIterator> it = subIterators.iterator(); it.hasNext();) {
			result.append(it.next().toString());
			if (it.hasNext()) {
				result.append(", ");
			}
		}
		result.append(")");
		return result.toString();
	}
}