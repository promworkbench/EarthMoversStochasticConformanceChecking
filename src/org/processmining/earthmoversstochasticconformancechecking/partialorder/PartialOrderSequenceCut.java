package org.processmining.earthmoversstochasticconformancechecking.partialorder;

import java.util.BitSet;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderUtils;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class PartialOrderSequenceCut {
	public static TIntList[] findCut(int[] partialOrder) {
		/*
		 * As there are no connected components, we only have to merge the
		 * pairwise unreachable nodes.
		 */

		int numberOfEvents = PartialOrderUtils.getNumberOfEvents(partialOrder);
		int numberOfGroups = numberOfEvents;

		//initialise groups: every event is a group
		int[] eventIndex2groupNumber = new int[numberOfEvents];
		{
			for (int eventIndex = 0; eventIndex < eventIndex2groupNumber.length; eventIndex++) {
				eventIndex2groupNumber[eventIndex] = eventIndex;
			}
		}

		//compute reachabilities
		BitSet[] reachabilities = new BitSet[numberOfEvents];
		{
			for (int eventIndexB = 0; eventIndexB < numberOfEvents; eventIndexB++) {
				reachabilities[eventIndexB] = new BitSet();
				reachabilities[eventIndexB].set(eventIndexB);
				for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
						eventIndexB); edgeIndex++) {
					int eventIndexA = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder, eventIndexB,
							edgeIndex);
					reachabilities[eventIndexB].or(reachabilities[eventIndexA]);
				}
			}
		}

		//merge groups of which at least 2 events are not pairwise reachable
		for (int eventIndexB = 1; eventIndexB < numberOfEvents; eventIndexB++) {
			for (int eventIndexA = 0; eventIndexA < eventIndexB; eventIndexA++) {
				//see if B is reachable from A
				if (eventIndex2groupNumber[eventIndexA] != eventIndex2groupNumber[eventIndexB]) {
					if (!reachabilities[eventIndexB].get(eventIndexA)) {
						if (mergeGroupsOf(eventIndex2groupNumber, eventIndexA, eventIndexB)) {
							numberOfGroups--;
						}

						if (numberOfGroups < 2) {
							return null;
						}

						if (numberOfGroups < 2) {
							return null;
						}
					}
				}
			}
		}

		if (numberOfGroups < 2) {
			return null;
		}

		//by construction, the groups are already sorted 
		return toPartition(numberOfEvents, numberOfGroups, eventIndex2groupNumber);
	}

	public static TIntList[] toPartition(int numberOfEvents, int numberOfGroups, int[] eventIndex2groupNumber) {
		TIntList[] resultX = new TIntList[numberOfEvents];
		for (int eventIndex = 0; eventIndex < numberOfEvents; eventIndex++) {
			int groupNumber = eventIndex2groupNumber[eventIndex];
			if (resultX[groupNumber] == null) {
				resultX[groupNumber] = new TIntArrayList();
			}
			resultX[groupNumber].add(eventIndex);
		}

		TIntList[] result = new TIntList[numberOfGroups];
		int i = 0;
		for (TIntList group : resultX) {
			if (group != null) {
				result[i] = group;
				i++;
			}
		}
		return result;
	}

	public static boolean mergeGroupsOf(int[] eventIndex2group, int eventIndexA, int eventIndexB) {
		int groupA = eventIndex2group[eventIndexA];
		int groupB = eventIndex2group[eventIndexB];
		if (groupA == groupB) {
			return false;
		}
		for (int eventIndex = 0; eventIndex < eventIndex2group.length; eventIndex++) {
			if (eventIndex2group[eventIndex] == groupB) {
				eventIndex2group[eventIndex] = groupA;
			}
		}
		return true;
	}
}
