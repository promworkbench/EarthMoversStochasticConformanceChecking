package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;

public interface EMSCParametersBounds<A extends Order, B extends Order> extends EMSCParameters<A, B> {
	public DistanceMatrix<A, B> getDistanceMatrixBest();
}
