package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderUncertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguageNormalise;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.custom_hash.TObjectLongCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

/**
 * Create partial orders from a log. Two events are concurrent if they have the
 * same timestamp.
 * 
 * @author sander
 *
 */
public class XLog2StochasticLanguagePartialOrderEqualTimestamp {

	public static StochasticLanguage<PartialOrderUncertain> convert(XLog log, XEventClassifier classifier,
			final Activity2IndexKey activityKey, ProMCanceller canceller) {

		activityKey.feed(log, classifier);

		/**
		 * Optimise a little by comparing "equivalent" partial orders. By
		 * keeping the buckets in a trace sorted, we have equivalence if and
		 * only if the int[] representation is equivalent. which saves us
		 * quadratically in the number of O(n!) A* distance measures.
		 */
		final TObjectLongMap<int[]> setLog = new TObjectLongCustomHashMap<>(new HashingStrategy<int[]>() {
			private static final long serialVersionUID = 1L;

			public int computeHashCode(int[] object) {
				return Arrays.hashCode(object);
			}

			public boolean equals(int[] o1, int[] o2) {
				return Arrays.equals(o1, o2);
			}
		}, 10, 0.5f, 0);

		for (XTrace trace : log) {
			int[] partialOrder = convert(trace, classifier, activityKey);
			setLog.adjustOrPutValue(partialOrder, 1, 1);

			if (canceller.isCancelled()) {
				return null;
			}
		}

		int[][] keys2 = new int[setLog.size()][];
		keys2 = setLog.keys(keys2);
		final int[][] keys3 = keys2;
		StochasticLanguagePartialOrderUncertain language = new StochasticLanguagePartialOrderUncertain(activityKey) {

			private int[][] keys = keys3;
			private long[] values = setLog.values();

			public int size() {
				return setLog.size();
			}

			public StochasticTraceIterator<PartialOrderUncertain> iterator() {
				return new StochasticTraceIterator<PartialOrderUncertain>() {
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

			public int[] getTrace(int traceIndex) {
				return keys[traceIndex];
			}
		};

		return StochasticLanguageNormalise.normalise(language, canceller);
	}

	private static int[] convert(XTrace trace, XEventClassifier classifier, Activity2IndexKey activityKey) {
		/*
		 * Observation: the partial order has a chain-of-buckets structure.
		 * First, gather the buckets
		 */
		List<TIntList> buckets = createBuckets(trace, classifier, activityKey);

		/**
		 * Second, sort the buckets for big gains later
		 */
		for (TIntList bucket : buckets) {
			bucket.sort();
		}

		/**
		 * Third, convert to a partial order
		 */
		return createPartialOrder(trace, buckets);
	}

	public static int[] createPartialOrder(XTrace trace, List<TIntList> buckets) {

		//count number of edges
		int edgeCount = 0;
		{
			for (int i = 1; i < buckets.size(); i++) {
				edgeCount += buckets.get(i).size() * buckets.get(i - 1).size();
			}
		}

		int[] result = new int[1 + trace.size() + trace.size() + edgeCount];

		//		 * [0] number of events (e)
		result[0] = trace.size();

		//		 * [1..e] activity of event
		{
			int eventIndex = 1;
			for (TIntList bucket : buckets) {
				for (TIntIterator bi = bucket.iterator(); bi.hasNext();) {
					result[eventIndex] = bi.next();
					eventIndex++;
				}
			}
		}

		//intermediate step: create an eventIndex map
		int[][] bucket2eventBucket2eventIndex = new int[buckets.size()][];
		{
			int eventIndex = 0;
			for (int bucketIndex = 0; bucketIndex < buckets.size(); bucketIndex++) {
				bucket2eventBucket2eventIndex[bucketIndex] = new int[buckets.get(bucketIndex).size()];
				for (int eventBucketIndex = 0; eventBucketIndex < buckets.get(bucketIndex).size(); eventBucketIndex++) {
					bucket2eventBucket2eventIndex[bucketIndex][eventBucketIndex] = eventIndex;
					eventIndex++;
				}
			}
		}

		//		 * [e+1..e+e] start index of list of incoming edges
		//		 * [e+e+1..] incoming edge list: events
		{
			int edgePointerIndex = trace.size() + 1;
			int edgeListIndex = 1 + trace.size() + trace.size();

			//the events in the first bucket have no incoming edges
			for (int eventBucketIndex = 0; eventBucketIndex < buckets.get(0).size(); eventBucketIndex++) {
				result[edgePointerIndex] = edgeListIndex;

				edgePointerIndex += 0; //there are no edges, so the pointer stays the same
				edgePointerIndex++;
			}

			//the events in the subsequent buckets have incoming edges
			for (int bucketIndex = 1; bucketIndex < buckets.size(); bucketIndex++) {
				int numberOfIncomingEdges = buckets.get(bucketIndex - 1).size();

				for (int eventBucketIndex = 0; eventBucketIndex < buckets.get(bucketIndex).size(); eventBucketIndex++) {
					//set the pointer to the list of incoming edges
					result[edgePointerIndex] = edgeListIndex;

					//set the edges from the previous bucket
					for (int edgeIndex = 0; edgeIndex < numberOfIncomingEdges; edgeIndex++) {
						result[edgeListIndex + edgeIndex] = bucket2eventBucket2eventIndex[bucketIndex - 1][edgeIndex];
					}

					//each event in the bucket has an incoming edge from all events in the previous bucket
					edgeListIndex += numberOfIncomingEdges;
					edgePointerIndex++;
				}
			}
		}

		return result;
	}

	public static List<TIntList> createBuckets(XTrace trace, XEventClassifier classifier,
			Activity2IndexKey activityKey) {
		List<TIntList> buckets = new ArrayList<>();
		TIntList currentBucket = null;
		long currentBucketTime = Long.MIN_VALUE;
		for (XEvent event : trace) {

			//see whether this event has the same timestamp as the current bucket
			boolean newBucket = true;
			{
				long time = Long.MIN_VALUE;
				if (event.hasAttributes()) {
					XAttribute att = event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
					if (att != null) {
						time = AttributeUtils.parseTimeFast(att);
						if (time != Long.MIN_VALUE && time == currentBucketTime) {
							newBucket = false;
						}
						currentBucketTime = time;
					}
				}
			}

			//create a new bucket if necessary
			if (newBucket) {
				currentBucket = new TIntArrayList();
				buckets.add(currentBucket);
			}

			//add the event to the current bucket
			{
				String activity = classifier.getClassIdentity(event);
				int activityIndex = activityKey.toIndex(activity);
				currentBucket.add(activityIndex);
			}
		}
		return buckets;
	}
}
