package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix;

import java.util.Iterator;

public class ReallocationMatrix {

	private int size;
	private double[] probabilities;
	private int[] traceAIndex;
	private int[] traceBIndex;

	public class ReallocationMatrixIterator implements Iterator<Double> {
		int now = -1;

		public Double next() {
			now++;
			return getProbability();
		}

		public double getProbability() {
			return probabilities[now];
		}

		public int getTraceAIndex() {
			return traceAIndex[now];
		}

		public int getTraceBIndex() {
			return traceBIndex[now];
		}

		public boolean hasNext() {
			return now < size - 1;
		}
	}

	public ReallocationMatrixIterator iterator() {
		return new ReallocationMatrixIterator();
	}

	public ReallocationMatrix(int logTraces, int modelTraces) {
		int totalSize = logTraces + modelTraces - 1;
		probabilities = new double[totalSize];
		traceAIndex = new int[totalSize];
		traceBIndex = new int[totalSize];
		size = 0;
	}

	public void set(int traceAIndex, int traceBIndex, double value) {
		if (value > 0) {
			probabilities[size] = value;
			this.traceAIndex[size] = traceAIndex;
			this.traceBIndex[size] = traceBIndex;
			size++;
			return;
		}
	}

	public int size() {
		return size;
	}

}