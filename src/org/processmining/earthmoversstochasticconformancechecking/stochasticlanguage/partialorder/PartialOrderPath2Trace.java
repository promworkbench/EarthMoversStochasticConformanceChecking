package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import java.util.BitSet;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition2IndexKey;

/**
 * Project a partial order on its activities (thus also removing silent
 * transitions)
 * 
 * @author sander
 *
 */
public class PartialOrderPath2Trace {
	public static int[] convert(StochasticTransition2IndexKey transitionKey, int[] partialOrderPath) {
		if (PartialOrderUtils.isTotalOrder(partialOrderPath)) {
			return convertTotalOrder(transitionKey, partialOrderPath);
		} else {
			return convertPartialOrder(transitionKey, partialOrderPath);
		}
	}

	public static int[] convertTotalOrder(StochasticTransition2IndexKey transitionKey, int[] partialOrderPath) {
		int[] totalOrderPath = PartialOrderUtils.getATotalOrder(partialOrderPath);

		//count non-silent transitions
		int[] totalOrderTrace;
		{
			int count = 0;
			for (int transitionIndex : totalOrderPath) {
				//get activity
				int activityIndex = transitionKey.transition2activityIndex(transitionIndex);
				if (activityIndex >= 0) {
					count++;
				}
			}
			totalOrderTrace = new int[count];
		}

		//transform to total order trace
		{
			int i = 0;
			for (int transitionIndex : totalOrderPath) {
				//get activity
				int activityIndex = transitionKey.transition2activityIndex(transitionIndex);
				if (activityIndex >= 0) {
					totalOrderTrace[i] = activityIndex;
					i++;
				}
			}
		}

		//transform to partial order trace
		return PartialOrderUtils.totalOrder2partialOrder(totalOrderTrace);
	}

	public static int[] convertPartialOrder(StochasticTransition2IndexKey transitionKey, int[] partialOrderPath) {

		assert PartialOrderUtils.isValid(partialOrderPath);

		int[] partialOrderTrace = PartialOrderUtils.emptyPartialOrder();

		int[] eventIndexPath2eventIndexTrace = new int[PartialOrderUtils.getNumberOfEvents(partialOrderPath)];

		for (int eventIndexPath = 0; eventIndexPath < PartialOrderUtils
				.getNumberOfEvents(partialOrderPath); eventIndexPath++) {
			int transitionIndex = PartialOrderUtils.getTransition(partialOrderPath, eventIndexPath);

			//get activity
			int activityIndex = transitionKey.transition2activityIndex(transitionIndex);

			//if the transition is not invisible
			if (activityIndex != -1) {
				//this is a non-silent transition

				//add to the trace
				int eventIndexTrace = PartialOrderUtils.getNumberOfEvents(partialOrderTrace);
				partialOrderTrace = PartialOrderUtils.addEvent(partialOrderTrace, activityIndex);

				//keep track of mapping path -> trace
				eventIndexPath2eventIndexTrace[eventIndexPath] = eventIndexTrace;

				//transform edges
				{
					//initialise queue
					BitSet queue = new BitSet();
					for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrderPath,
							eventIndexPath); edgeIndex++) {
						int eventIndexPathSource = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrderPath,
								eventIndexPath, edgeIndex);
						queue.set(eventIndexPathSource);
					}

					//walk backwards over the edges (which can only point backward)
					if (!queue.isEmpty()) {
						for (int eventIndexPathT = queue
								.length(); (eventIndexPathT = queue.previousSetBit(eventIndexPathT - 1)) >= 0;) {

							int transition = PartialOrderUtils.getTransition(partialOrderPath, eventIndexPathT);
							if (transitionKey.transition2activityIndex(transition) == -1) {
								//pretend that this transition is not there and keep moving backward
								for (int edgeIndex = 0; edgeIndex < PartialOrderUtils
										.getNumberOfIncomingEdges(partialOrderPath, eventIndexPathT); edgeIndex++) {
									queue.set(PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrderPath,
											eventIndexPathT, edgeIndex));
								}
							} else {
								//this is a transition that must be fired before this event can fire
								partialOrderTrace = PartialOrderUtils.addEdgeToLastAddedEvent(partialOrderTrace,
										eventIndexPath2eventIndexTrace[eventIndexPathT]);
							}
						}
					}
				}
			}
		}

		assert PartialOrderUtils.isValid(partialOrderTrace);

		return partialOrderTrace;
	}
}
