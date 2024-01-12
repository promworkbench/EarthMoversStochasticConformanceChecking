package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;

public abstract class EMSCParametersAbstract<A extends Order, B extends Order> implements EMSCParameters<A, B> {

	protected DistanceMatrix<A, B> distanceMatrix;
	private boolean debug;
	private boolean computeStochasticTraceAlignments;

	public EMSCParametersAbstract(boolean debug, boolean computeStochasticTraceAlignments) {
		this.debug = debug;
		this.computeStochasticTraceAlignments = computeStochasticTraceAlignments;
	}

	public DistanceMatrix<A, B> getDistanceMatrix() {
		return distanceMatrix;
	}

	public void setDistanceMatrix(DistanceMatrix<A, B> distanceMatrix) {
		this.distanceMatrix = distanceMatrix;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isComputeStochasticTraceAlignments() {
		return computeStochasticTraceAlignments;
	}

	/**
	 * If the stochastic trace alignments are not necessary (i.e. only the
	 * numeric result is required), set this to false to save a bit of time.
	 * 
	 * @param computeStochasticTraceAlignments
	 */
	public void setComputeStochasticTraceAlignments(boolean computeStochasticTraceAlignments) {
		this.computeStochasticTraceAlignments = computeStochasticTraceAlignments;
	}

}