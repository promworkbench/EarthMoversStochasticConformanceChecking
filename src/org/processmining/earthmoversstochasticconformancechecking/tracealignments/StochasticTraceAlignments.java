package org.processmining.earthmoversstochasticconformancechecking.tracealignments;

import java.util.Iterator;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public interface StochasticTraceAlignments<L, M> extends Iterable<StochasticTraceAlignment<L, M>> {

	/**
	 * Get the EMSC value (similarity). Compute 1 - similarity = distance.
	 * 
	 * @return
	 */
	public double getSimilarity();

	public interface StochasticTraceAlignmentIterator<L, M> extends Iterator<StochasticTraceAlignment<L, M>> {
		public double getProbability();

		public int getTraceAIndex();

		public int getTraceBIndex();
	}

	@Override
	public StochasticTraceAlignmentIterator<L, M> iterator();

	/**
	 * The first language.
	 * 
	 * @return
	 */
	public StochasticLanguage<TotalOrder> getLanguageA();

	/**
	 * The second language.
	 * 
	 * @return
	 */
	public StochasticLanguage<TotalOrder> getLanguageB();

}