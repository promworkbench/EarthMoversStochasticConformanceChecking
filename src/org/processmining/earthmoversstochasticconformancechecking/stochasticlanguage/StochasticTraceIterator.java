package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

import java.util.Iterator;

/**
 * 
 * @author sander
 *
 * @param <O>
 *            denotes whether traces are totally or partially ordered
 */
public interface StochasticTraceIterator<O> extends Iterator<int[]> {

	/**
	 * 
	 * @return The index of the last trace returned by next()
	 */
	public int getTraceIndex();

	/**
	 * 
	 * @return The probability of the last trace returned by next()
	 */
	public double getProbability();

}