package org.processmining.earthmoversstochasticconformancechecking.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrder2TotalOrders.IIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderUtils;

import cern.colt.Arrays;

public class IIteratorOneTrace implements IIterator {

	private boolean done = false;
	private final int[] totalOrder;

	public IIteratorOneTrace(int[] partialOrder) {
		this.totalOrder = PartialOrderUtils.getATotalOrder(partialOrder);
	}

	public int getTraceLength() {
		return totalOrder.length;
	}

	public int[] next() {
		done = true;
		return totalOrder;
	}

	public boolean hasNext() {
		return !done;
	}

	public void reset() {
		done = false;
	}

	public int[] get() {
		return totalOrder;
	}

	public String toString() {
		return Arrays.toString(totalOrder);
	}
}