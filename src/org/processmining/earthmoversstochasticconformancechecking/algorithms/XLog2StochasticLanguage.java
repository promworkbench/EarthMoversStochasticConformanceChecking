package org.processmining.earthmoversstochasticconformancechecking.algorithms;

import java.util.Arrays;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguageNormalise;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.framework.plugin.ProMCanceller;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.custom_hash.TObjectLongCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

public class XLog2StochasticLanguage {

	public static StochasticLanguage<TotalOrder> convert(XLog log, XEventClassifier classifier,
			final Activity2IndexKey activityKey, ProMCanceller canceller) {

		activityKey.feed(log, classifier);

		final TObjectLongMap<int[]> sLog = new TObjectLongCustomHashMap<>(new HashingStrategy<int[]>() {
			private static final long serialVersionUID = 1L;

			public int computeHashCode(int[] object) {
				return Arrays.hashCode(object);
			}

			public boolean equals(int[] o1, int[] o2) {
				return Arrays.equals(o1, o2);
			}
		}, 10, 0.5f, 0);

		for (XTrace trace : log) {
			int[] sTrace = new int[trace.size()];
			{
				int i = 0;
				for (XEvent event : trace) {
					String activity = classifier.getClassIdentity(event);
					int activityIndex = activityKey.toIndex(activity);
					sTrace[i] = activityIndex;
					i++;
				}
			}

			if (canceller.isCancelled()) {
				return null;
			}

			sLog.adjustOrPutValue(sTrace, 1, 1);
		}

		int[][] keys2 = new int[sLog.size()][];
		keys2 = sLog.keys(keys2);
		final int[][] keys3 = keys2;
		StochasticLanguage<TotalOrder> language = new StochasticLanguage<TotalOrder>() {

			private int[][] keys = keys3;
			private long[] values = sLog.values();

			@Override
			public StochasticTraceIterator<TotalOrder> iterator() {
				return new StochasticTraceIterator<TotalOrder>() {
					int now = -1;

					public int[] next() {
						now++;
						return keys[now];
					}

					public boolean hasNext() {
						return now < keys.length - 1;
					}

					public double getProbability() {
						return values[now];
					}

					public int getTraceIndex() {
						return now;
					}
				};
			}

			@Override
			public int size() {
				return keys.length;
			}

			@Override
			public int[] getTrace(int traceIndex) {
				return keys[traceIndex];
			}

			@Override
			public Activity2IndexKey getActivityKey() {
				return activityKey;
			}

			public String getTraceString(int traceIndex) {
				return null;
			}

		};

		return StochasticLanguageNormalise.normalise(language, canceller);
	}

}
