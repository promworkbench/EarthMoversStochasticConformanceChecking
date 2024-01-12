package org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection;

import java.util.Arrays;
import java.util.Comparator;

public class StochasticTraceAlignmentsLogProjectionSorted implements StochasticTraceAlignmentsLogProjection {

	private final int[] map;
	private final StochasticTraceAlignmentsLogProjection projection;

	public StochasticTraceAlignmentsLogProjectionSorted(final StochasticTraceAlignmentsLogProjection projection) {
		this.projection = projection;

		Integer[] mapx = new Integer[projection.getNumberOfLogtraces()];
		for (int i = 0; i < mapx.length; i++) {
			mapx[i] = i;
		}
		Arrays.sort(mapx, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return Double.compare(projection.getTraceProbability(o2), projection.getTraceProbability(o1));
			}
		});

		this.map = new int[projection.getNumberOfLogtraces()];
		for (int i = 0; i < mapx.length; i++) {
			map[i] = mapx[i];
		}
	}

	public int getNumberOfLogtraces() {
		return projection.getNumberOfLogtraces();
	}

	public double getTraceProbability(int logTrace) {
		return projection.getTraceProbability(map[logTrace]);
	}

	public String[] getTrace(int logTrace) {
		return projection.getTrace(map[logTrace]);
	}

	public double[] getEventSyncLikelihoods(int logTrace) {
		return projection.getEventSyncLikelihoods(map[logTrace]);
	}

}
