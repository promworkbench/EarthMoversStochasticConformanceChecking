package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;

/**
 * 
 * @author sander
 *
 * @param <A>
 *            denotes whether the traces are partially or totally ordered
 */
public interface StochasticPathLanguage<A> extends StochasticLanguage<A> {
	public StochasticTransition2IndexKey getTransitionKey();

	/**
	 * The iterator must be stable. Do not use hashmap iterators.
	 */
	public StochasticPathIterator<A> iterator();

	public int[] getPath(int pathIndex);

}