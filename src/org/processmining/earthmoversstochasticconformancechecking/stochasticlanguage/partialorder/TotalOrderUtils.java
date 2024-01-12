package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

/**
 * Idea: state denotes the next activity to be executed.
 * 
 * @author sander
 *
 */
public class TotalOrderUtils {

	public static int[] emptyTotalOrder() {
		return new int[] {};
	}

	/**
	 * 
	 * @param prefix
	 * @param transitionOrActivity
	 * @return a copy of the partial order with one event added with index
	 *         length; concurrent with all other events.
	 */
	public static int[] addEvent(int[] partialOrder, int transitionOrActivity) {
		int[] result = new int[partialOrder.length + 1];
		System.arraycopy(partialOrder, 0, result, 0, partialOrder.length);
		result[result.length - 1] = transitionOrActivity;
		return result;
	}

	public static int getNumberOfEvents(int[] totalOrder) {
		return totalOrder.length;
	}

	/**
	 * Marks the given event as executed (in place)
	 * 
	 * @param totalOrder
	 * @param eventIndex
	 */
	public static int takeStep(int[] totalOrder, int state) {
		return state + 1;
	}

	public static int reset(int state) {
		return 0;
	}

	public static int eventsLeft(int[] totalOrder, int state) {
		return totalOrder.length - state;
	}

	public static int getNext(int[] totalOrder, int state) {
		return totalOrder[state];
	}

	public static int getActivity(int[] totalOrder, int eventIndex) {
		return totalOrder[eventIndex];
	}

	public static boolean hasBeenExecuted(int[] totalOrder, int state, int eventIndex) {
		return eventIndex < state;
	}

	/**
	 * 
	 * @param traceIndex
	 * @return a string array of the activities in the trace
	 */
	public static String[] getStringTrace(StochasticLanguage<TotalOrder> language, int traceIndex) {
		return language.getActivityKey().toTraceString(language.getTrace(traceIndex));
	}

	/**
	 * Helper method that creates a GraphViz .dot string for the visualization of the total order
	 * @param totalOrder Total order to be visualized
	*/
	public static String toGraphVizString(int[] totalOrder) {
		StringBuilder buildGViz = new StringBuilder("digraph G {\n");
		for(int eventIndex = 0; eventIndex < getNumberOfEvents(totalOrder); eventIndex++) {
			if(eventIndex == 0) {
				buildGViz.append(eventIndex);
			}
			else {
				buildGViz.append(" -> " + eventIndex);
			}
		}
		buildGViz.append(";\n");
		for(int eventIndex = 0; eventIndex < getNumberOfEvents(totalOrder); eventIndex++) {
			buildGViz.append(eventIndex + " [label=\"" + getActivity(totalOrder, eventIndex) + "\"];\n" );

		}
		buildGViz.append("}");
		return buildGViz.toString();
				
	}
}
