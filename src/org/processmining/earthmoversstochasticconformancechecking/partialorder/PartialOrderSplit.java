package org.processmining.earthmoversstochasticconformancechecking.partialorder;

import java.util.Arrays;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderUtils;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class PartialOrderSplit {
	public static int[][] split(int[] partialOrder, TIntList[] partition) {
		int[][] result = new int[partition.length][];

		for (int p = 0; p < partition.length; p++) {
			int[] part = partition[p].toArray();
			Arrays.sort(part);
			TIntSet partS = new TIntHashSet(partition[p]);

			int e = part.length;

			TIntList partResult = new TIntArrayList();

			//[0] number of events (e)
			partResult.add(e);

			//[1..e] activity of event
			int[] eventIndex2eventIndexPart = new int[PartialOrderUtils.getNumberOfEvents(partialOrder)];
			{
				int eventIndexPart = 0;
				for (int eventIndex : part) {
					eventIndex2eventIndexPart[eventIndex] = eventIndexPart;
					int activity = PartialOrderUtils.getActivity(partialOrder, eventIndex);
					partResult.add(activity);

					eventIndexPart++;
				}
			}

			//[e+1..e+e] start index of list of incoming edges
			//[e+e+1..] incoming edge list: events
			{
				int startIndexOfEdges = e + e + 1;
				for (int i = 0; i < e; i++) {
					partResult.add(-1); //start index, but unknown which one yet
				}

				for (int eventIndex : part) {
					int eventIndexPart = eventIndex2eventIndexPart[eventIndex];

					partResult.set(e + 1 + eventIndexPart, startIndexOfEdges);

					//walk through edges
					for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
							eventIndex); edgeIndex++) {
						int edgeSourceEventIndex = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder,
								eventIndex, edgeIndex);
						if (partS.contains(edgeSourceEventIndex)) {
							//this is an internal edge; add it to the result
							int edgeSourceEventIndexPart = eventIndex2eventIndexPart[edgeSourceEventIndex];
							partResult.add(edgeSourceEventIndexPart);
							startIndexOfEdges++;
						}
					}
				}
			}

			result[p] = partResult.toArray();
		}

		return result;
	}
}
