package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Idea: store the constraints of a partial order in an int array.
 * 
 * [0] number of events (e)
 * 
 * [1..e] activity of event
 * 
 * [e+1..e+e] start index of list of incoming edges
 * 
 * [e+e+1..] incoming edge list: events
 * 
 * @author sander
 *
 */
public class PartialOrderUtils {

	public static int[] emptyPartialOrder() {
		return new int[] { 0 };
	}

	/**
	 * 
	 * @param prefix
	 * @param transitionOrActivity
	 * @return a copy of the partial order with one event added with index
	 *         length; concurrent with all other events.
	 */
	public static int[] addEvent(int[] partialOrder, int transitionOrActivity) {
		int[] result = new int[partialOrder.length + 2];

		int numberOfEventsOld = getNumberOfEvents(partialOrder);
		int numberOfEventsNew = numberOfEventsOld + 1;

		//number of events
		result[0] = numberOfEventsNew;

		//activities of events
		System.arraycopy(partialOrder, 1, result, 1, numberOfEventsOld);
		result[1 + numberOfEventsNew - 1] = transitionOrActivity;

		//start index of list of incoming edges
		for (int oldI = 1 + numberOfEventsOld; oldI < 1 + numberOfEventsOld + numberOfEventsOld; oldI++) {
			int newI = oldI + 1;
			result[newI] = partialOrder[oldI] + 2;
		}
		result[1 + numberOfEventsNew + numberOfEventsNew - 1] = partialOrder.length + 2;

		//edges
		System.arraycopy(partialOrder, 1 + numberOfEventsOld + numberOfEventsOld, result,
				1 + numberOfEventsNew + numberOfEventsNew,
				partialOrder.length - (1 + numberOfEventsOld + numberOfEventsOld));

		return result;
	}

	/**
	 * Add an edge to the edge with the highest index.
	 * 
	 * @param prefix
	 * @param eventIndexP
	 * @param eventIndexT
	 * @return
	 */
	public static int[] addEdgeToLastAddedEvent(int[] partialOrder, int eventIndexFrom) {
		assert eventIndexFrom < getNumberOfEvents(partialOrder) - 1;
		int[] result = new int[partialOrder.length + 1];
		System.arraycopy(partialOrder, 0, result, 0, partialOrder.length);
		result[result.length - 1] = eventIndexFrom;
		return result;
	}

	public static boolean isValid(int[] partialOrder) {
		for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrder); eventIndex++) {
			for (int edgeIndex = 0; edgeIndex < getNumberOfIncomingEdges(partialOrder, eventIndex); edgeIndex++) {
				int source = getIncomingEdgeSourceEventIndex(partialOrder, eventIndex, edgeIndex);
				if (source >= eventIndex) {
					System.out.println("invalid partial order " + Arrays.toString(partialOrder));
					return false;
				}
			}
		}
		return true;
	}

	public static int getNumberOfEvents(int[] partialOrder) {
		return partialOrder[0];
	}

	public static int getActivity(int[] partialOrder, int eventIndex) {
		return partialOrder[eventIndex + 1];
	}

	public static int getTransition(int[] partialOrder, int eventIndex) {
		return getActivity(partialOrder, eventIndex);
	}

	/**
	 * 
	 * @param partialOrder
	 * @param eventIndex
	 * @return the index in the array at which the list of incoming edges starts
	 *         for this event
	 */
	protected static int getIncomingEdgesStartAt(int[] partialOrder, int eventIndex) {
		assert eventIndex < getNumberOfEvents(partialOrder);
		return partialOrder[1 + getNumberOfEvents(partialOrder) + eventIndex];
	}

	public static int getNumberOfIncomingEdges(int[] partialOrder, int eventIndex) {
		assert eventIndex < getNumberOfEvents(partialOrder);
		if (eventIndex == getNumberOfEvents(partialOrder) - 1) {
			//event last
			return partialOrder.length - getIncomingEdgesStartAt(partialOrder, eventIndex);
		} else {
			//event not last
			return getIncomingEdgesStartAt(partialOrder, eventIndex + 1)
					- getIncomingEdgesStartAt(partialOrder, eventIndex);
		}
	}

	protected static int getIncomingEdgesNextStartAt(int[] partialOrder, int eventIndex) {
		if (eventIndex == getNumberOfEvents(partialOrder) - 1) {
			//event last
			return partialOrder.length;
		} else {
			//event not last
			return getIncomingEdgesStartAt(partialOrder, eventIndex + 1);
		}
	}

	public static int getIncomingEdgeSourceEventIndex(int[] partialOrder, int eventIndex, int edgeIndex) {
		return partialOrder[getIncomingEdgesStartAt(partialOrder, eventIndex) + edgeIndex];
	}

	public static BitSet getNewState(int[] partialOrder) {
		return new BitSet(getNumberOfEvents(partialOrder));
	}

	/**
	 * Marks the given event as executed (in place)
	 * 
	 * @param partialOrder
	 * @param eventIndex
	 */
	public static BitSet takeStep(int[] partialOrder, int eventIndex, BitSet state) {
		state.set(eventIndex);
		return state;
	}

	public static void reset(BitSet state) {
		state.clear();
	}

	public static boolean isEnabled(int[] partialOrder, int eventIndex, BitSet state) {
		if (hasBeenExecuted(partialOrder, state, eventIndex)) {
			return false;
		}

		int i = getIncomingEdgesStartAt(partialOrder, eventIndex);
		int endI = getIncomingEdgesNextStartAt(partialOrder, eventIndex);
		while (i < endI) {
			int eventI = partialOrder[i];
			if (!state.get(eventI)) {
				//				System.out.println("  not enabled event " + eventIndex + " because of event " + eventI + " state "
				//						+ state + " po " + Arrays.toString(partialOrder));
				return false;
			}

			i++;
		}
		return true;
	}

	public static int eventsLeft(int[] partialOrder, BitSet state) {
		return getNumberOfEvents(partialOrder) - state.cardinality();
	}

	public static boolean hasBeenExecuted(int[] partialOrder, BitSet state, int eventIndex) {
		return state.get(eventIndex);
	}

	/**
	 * Helper method that creates a GraphViz .dot string for the visualization
	 * of the partial order
	 * 
	 * @param partialOrder
	 *            Partial order to be visualized
	 * @return
	 */
	public static String toGraphVizString(int[] partialOrder) {
		StringBuilder buildGViz = new StringBuilder("digraph G {\n");
		for (int eventIndex = 0; eventIndex < getNumberOfEvents(partialOrder); eventIndex++) {
			int i = getIncomingEdgesStartAt(partialOrder, eventIndex);
			int endI = getIncomingEdgesNextStartAt(partialOrder, eventIndex);

			for (int j = i; j < endI; j++) {
				buildGViz.append(partialOrder[j] + " -> " + eventIndex + ";\n");
			}
		}
		for (int eventIndex = 0; eventIndex < getNumberOfEvents(partialOrder); eventIndex++) {
			buildGViz.append(eventIndex + " [label=\"" + getActivity(partialOrder, eventIndex) + "\"];\n");

		}
		buildGViz.append("}");
		return buildGViz.toString();
	}

	/**
	 * 
	 * @param partialOrder
	 * @return whether the partial order is totally ordered.
	 */
	public static boolean isTotalOrder(int[] partialOrder) {
		for (int eventIndex = 1; eventIndex < getNumberOfEvents(partialOrder); eventIndex++) {
			//check whether this eventIndex is dependent on the previous event
			boolean found = false;
			for (int edgeIndex = 0; edgeIndex < getNumberOfIncomingEdges(partialOrder, eventIndex); edgeIndex++) {
				if (getIncomingEdgeSourceEventIndex(partialOrder, eventIndex, edgeIndex) == eventIndex - 1) {
					found = true;
					break;
				}
			}

			if (!found) {
				return false;
			}
		}
		return true;
	}

	public static boolean isFullyConcurrent(int[] partialOrder) {
		if (getNumberOfEvents(partialOrder) < 2) {
			return true;
		}
		return getIncomingEdgesStartAt(partialOrder, 0) == partialOrder.length;
	}

	/**
	 * 
	 * @param partialOrder
	 * @return an arbitrary total order that fits the partial order
	 */
	public static int[] getATotalOrder(int[] partialOrder) {
		int[] result = new int[getNumberOfEvents(partialOrder)];
		System.arraycopy(partialOrder, 1, result, 0, result.length);
		return result;
	}

	/**
	 * 
	 * @param totalOrder
	 * @return a partial order representation of this total order
	 */
	public static int[] totalOrder2partialOrder(int[] totalOrder) {
		int e = totalOrder.length;

		int[] partialOrder = new int[Math.max(1, 1 + e + e + (e - 1))];

		//[0] number of events (e)
		partialOrder[0] = totalOrder.length;

		if (e > 0) {

			//[1..e] activity of event
			System.arraycopy(totalOrder, 0, partialOrder, 1, e);

			//[e+1..e+e] start index of list of incoming edges
			{
				int startIndex = e + e + 1;
				partialOrder[e + 1] = startIndex;
				for (int i = e + 2; i <= e + e; i++) {
					partialOrder[i] = startIndex;
					startIndex++;
				}
			}

			//[e+e+1..] incoming edge list: events
			{
				int sourceIndex = 0;
				for (int i = e + e + 1; i < partialOrder.length; i++) {
					partialOrder[i] = sourceIndex;
					sourceIndex++;
				}
			}
		}

		return partialOrder;
	}

}