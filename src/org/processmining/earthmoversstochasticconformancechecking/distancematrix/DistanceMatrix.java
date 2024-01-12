package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.framework.plugin.ProMCanceller;

public interface DistanceMatrix<A extends Order, B extends Order> extends Cloneable {

	public void init(StochasticLanguage<A> languageA, final StochasticLanguage<B> languageB, ProMCanceller canceller)
			throws InterruptedException;

	/**
	 * Gives an array with distances, for use in lpsolve. Notice: there are
	 * "empty" values in this array to make it directly suitable for lpsolve.
	 * Should be a fast method (all computations must have been done in the init
	 * function)
	 * 
	 * @return
	 */
	public double[] getDistances();

	/**
	 * Get one particular distance. Should be a fast method (all computations
	 * must have been done in the init function)
	 * 
	 * @param l
	 * @param m
	 * @return
	 */
	public double getDistance(int l, int m);

	public DistanceMatrix<A, B> clone();

}