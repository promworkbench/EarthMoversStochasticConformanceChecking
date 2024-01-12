package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition2IndexKey;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotNode;

import gnu.trove.map.hash.TIntObjectHashMap;

public class PartialOrder2String {
	public static String toStringActivity(int[] partialOrder, Activity2IndexKey activityKey) {
		StringBuilder result = new StringBuilder();

		for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrder); eventIndex++) {
			result.append("#" + eventIndex + ": "
					+ activityKey.toString(PartialOrderUtils.getActivity(partialOrder, eventIndex)));
			result.append(" needs");
			for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
					eventIndex); edgeIndex++) {
				int sourceEventIndex = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder, eventIndex,
						edgeIndex);
				result.append(" #" + sourceEventIndex);
			}
			if (eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrder) - 1) {
				result.append("\n");
			}
		}

		return result.toString();
	}

	public static String toStringTransition(int[] partialOrder, StochasticTransition2IndexKey transitionKey) {
		StringBuilder result = new StringBuilder();

		for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrder); eventIndex++) {
			int transitionIndex = PartialOrderUtils.getTransition(partialOrder, eventIndex);
			String label = transitionKey.getSemantics().getTransitionLabel(transitionIndex);
			result.append("#" + eventIndex + ": " + label);

			result.append(" needs");
			for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
					eventIndex); edgeIndex++) {
				int sourceEventIndex = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder, eventIndex,
						edgeIndex);
				result.append(" #" + sourceEventIndex);
			}
			if (eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrder) - 1) {
				result.append("\n");
			}
		}

		return result.toString();
	}

	public static Dot toDotTransition(int[] partialOrder, StochasticTransition2IndexKey transitionKey) {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);

		TIntObjectHashMap<DotNode> event2node = new TIntObjectHashMap<>();

		for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrder); eventIndex++) {
			int transitionIndex = PartialOrderUtils.getTransition(partialOrder, eventIndex);
			String label = transitionKey.getSemantics().getTransitionLabel(transitionIndex);

			DotNode node = dot.addNode(label);
			event2node.put(eventIndex, node);

			for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
					eventIndex); edgeIndex++) {
				int sourceEventIndex = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder, eventIndex,
						edgeIndex);
				dot.addEdge(event2node.get(sourceEventIndex), node);
			}

		}

		return dot;
	}

	public static String toString(int[] partialOrder) {
		StringBuilder result = new StringBuilder();

		for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrder); eventIndex++) {
			result.append("#" + eventIndex + ": " + PartialOrderUtils.getActivity(partialOrder, eventIndex));
			result.append(" needs");
			for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
					eventIndex); edgeIndex++) {
				int sourceEventIndex = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder, eventIndex,
						edgeIndex);
				result.append(" #" + sourceEventIndex);
			}
			if (eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrder) - 1) {
				result.append("\n");
			}
		}

		return result.toString();
	}
}