package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;

public interface EMSCParameters<A extends Order, B extends Order> {
	public DistanceMatrix<A, B> getDistanceMatrix();

	public boolean isComputeStochasticTraceAlignments();

	public boolean isDebug();

	public A getOrderA();

	public B getOrderB();
}