package org.processmining.earthmoversstochasticconformancechecking.partialorder;

import java.util.Arrays;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrder2TotalOrders.IIterator;

// based on
// https://stackoverflow.com/questions/49478238/permutation-iterator-in-java
public class IIteratorPermutations implements IIterator {
	private int[] arr = null;
	private int[] arr1 = null;
	private int[] arr2 = null;
	private int size;
	private int[] stack = null;

	private int index = 0;

	public IIteratorPermutations(int[] arr) {
		this.arr = arr;
		reset();
	}

	@Override
	public boolean hasNext() {
		return (null != arr1 && arr1.length > 0);
	}

	@Override
	public int[] next() {
		// start computing.
		// We will return original array as value of last permutation.
		// This is to make "hasNext() " implementation easy.
		updateValue();
		return arr2;
	}

	@Override
	public int[] get() {
		return arr2;
	}

	@Override
	public void reset() {
		if (arr.length > 0) {
			arr1 = arr;

			size = arr1.length;
			arr2 = Arrays.copyOf(arr1, size);

			stack = new int[size];
			Arrays.fill(stack, 0);
		}
		index = 0;
	}

	protected void updateValue() {

		boolean bret = false;

		for (; index < size;) {

			if (stack[index] < index) {

				if (index % 2 == 0) {
					swap(0, index);
				} else {
					swap(stack[index], index);
				}

				stack[index]++;
				index = 0;
				bret = true;
				break;
			} else {
				stack[index] = 0;
				index++;
			}
		}

		if (!bret) {
			// No more permutation available. 
			// Set the original array as return value.
			// Also set arr1 = null , so that hasNext() will return false for next test
			arr2 = arr1;
			arr1 = null;
		}
	}

	private void swap(final int i, final int j) {
		int temp = arr2[i];
		arr2[i] = arr2[j];
		arr2[j] = temp;
	}

	@Override
	public int getTraceLength() {
		return arr.length;
	}

	public String toString() {
		return "perm" + Arrays.toString(arr);
	}
}
