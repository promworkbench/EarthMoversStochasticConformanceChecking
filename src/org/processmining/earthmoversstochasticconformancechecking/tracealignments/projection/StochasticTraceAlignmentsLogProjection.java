package org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection;

public interface StochasticTraceAlignmentsLogProjection {

	public int getNumberOfLogtraces();

	public double getTraceProbability(int logTrace);

	public String[] getTrace(int logTrace);

	public double[] getEventSyncLikelihoods(int logTrace);

}