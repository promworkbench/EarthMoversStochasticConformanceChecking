package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

import org.processmining.framework.plugin.ProMCanceller;

public class StochasticLanguageNormalise {
	public static <A> StochasticLanguage<A> normalise(final StochasticLanguage<A> language, ProMCanceller canceller) {

		final double sum = StochasticLanguageUtils.getSumProbability(language);

		return new StochasticLanguage<A>() {
			public StochasticTraceIterator<A> iterator() {
				final StochasticTraceIterator<A> it = language.iterator();
				return new StochasticTraceIterator<A>() {

					@Override
					public int[] next() {
						return it.next();
					}

					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public double getProbability() {
						return it.getProbability() / sum;
					}

					@Override
					public int getTraceIndex() {
						return it.getTraceIndex();
					}
				};
			}

			@Override
			public int size() {
				return language.size();
			}

			@Override
			public String getTraceString(int traceIndex) {
				return language.getTraceString(traceIndex);
			}

			@Override
			public int[] getTrace(int traceIndex) {
				return language.getTrace(traceIndex);
			}

			@Override
			public Activity2IndexKey getActivityKey() {
				return language.getActivityKey();
			}

		};
	}
}
