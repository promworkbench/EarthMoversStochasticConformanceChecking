package org.processmining.earthmoversstochasticconformancechecking.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderUtils;

import gnu.trove.list.TIntList;

/**
 * It's a xor cut in Inductive Miner, and for a partial order it's a concurrent
 * cut.
 * 
 * @author sander
 *
 */
public class PartialOrderXorCut {
	public static TIntList[] findCut(int[] partialOrder) {
		int numberOfEvents = PartialOrderUtils.getNumberOfEvents(partialOrder);
		int numberOfGroups = numberOfEvents;

		//initialise groups: every event is a group
		int[] eventIndex2groupNumber = new int[numberOfEvents];
		{
			for (int eventIndex = 0; eventIndex < eventIndex2groupNumber.length; eventIndex++) {
				eventIndex2groupNumber[eventIndex] = eventIndex;
			}
		}

		for (int eventIndexB = 0; eventIndexB < numberOfEvents; eventIndexB++) {
			for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
					eventIndexB); edgeIndex++) {
				int eventIndexA = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder, eventIndexB,
						edgeIndex);
				if (PartialOrderSequenceCut.mergeGroupsOf(eventIndex2groupNumber, eventIndexA, eventIndexB)) {
					numberOfGroups--;
				}

				if (numberOfGroups < 2) {
					return null;
				}
			}
		}

		if (numberOfGroups < 2) {
			return null;
		}

		return PartialOrderSequenceCut.toPartition(numberOfEvents, numberOfGroups, eventIndex2groupNumber);
	}
}
