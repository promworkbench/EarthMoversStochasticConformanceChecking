package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

/**
 * 
 * @author sander
 *
 * @param <A>
 *            denotes whether the traces are partially or totally ordered
 */
public interface StochasticLanguage<A> {
	public int size();

	/**
	 * Get a trace.
	 * 
	 * @param traceIndex
	 * @return
	 */
	public int[] getTrace(int traceIndex);

	/**
	 * Get a string representation of a trace.
	 * 
	 * @param traceIndex
	 * @return
	 */
	public String getTraceString(int traceIndex);

	/**
	 * The iterator must be stable. Do not use hashmap iterators.
	 */
	public StochasticTraceIterator<A> iterator();

	/**
	 * Return the key that can be used to get activity labels.
	 * 
	 * @return
	 */
	public Activity2IndexKey getActivityKey();
}