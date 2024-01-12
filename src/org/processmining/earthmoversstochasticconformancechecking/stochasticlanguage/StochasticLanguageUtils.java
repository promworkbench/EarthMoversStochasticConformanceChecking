package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

import java.util.Arrays;

import org.processmining.framework.plugin.ProMCanceller;

public class StochasticLanguageUtils {
	public static double getSumProbability(StochasticLanguage<?> language) {
		double sum = 0;

		for (StochasticTraceIterator<?> it = language.iterator(); it.hasNext();) {
			it.next();
			sum += it.getProbability();
		}

		return sum;
	}

	/**
	 * Linear operation.
	 * 
	 * @param language
	 * @param trace
	 * @param canceller
	 * @return
	 */
	public static double getProbability(StochasticLanguage<TotalOrder> language, int[] trace, ProMCanceller canceller) {

		StochasticTraceIterator<TotalOrder> it = language.iterator();
		while (it.hasNext()) {
			if (Arrays.equals(trace, it.next())) {
				return it.getProbability();
			}

			if (canceller.isCancelled()) {
				return Double.NaN;
			}
		}
		return 0;
	}

}