package org.processmining.earthmoversstochasticconformancechecking.visualisation;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjection;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMapFixed;
import org.processmining.plugins.graphviz.colourMaps.ColourMapOpacity;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.set.hash.THashSet;

public class StochasticTraceAlignmentsLogDfgVisualisation {
	public static Dot convert(StochasticTraceAlignmentsLogProjection projection) {
		final Dot dot = new Dot();

		//dot.setOption("newrank", "true"); //avoid a bug in Dot

		//start and end
		final DotNode start = dot.addNode("");
		start.setOption("width", "0.2");
		start.setOption("shape", "circle");
		start.setOption("style", "filled");
		start.setOption("fillcolor", "#80ff00");

		final DotNode end = dot.addNode("");
		end.setOption("width", "0.2");
		end.setOption("shape", "circle");
		end.setOption("style", "filled");
		end.setOption("fillcolor", "#E40000");

		//gather activities
		Set<String> activities = new THashSet<>();
		final Map<String, DotNode> activity2InNode = new THashMap<>();
		final Map<String, DotNode> activity2OutNode = new THashMap<>();
		TObjectDoubleHashMap<String> startActivities = new TObjectDoubleHashMap<>(10, 0.5f, 0);
		TObjectDoubleHashMap<String> endActivities = new TObjectDoubleHashMap<>(10, 0.5f, 0);
		TObjectDoubleHashMap<Pair<String, String>> edges = new TObjectDoubleHashMap<>(10, 0.5f, 0);
		TObjectDoubleHashMap<String> activity2sync = new TObjectDoubleHashMap<>(10, 0.5f, 0);
		TObjectDoubleHashMap<String> activity2count = new TObjectDoubleHashMap<>(10, 0.5f, 0);
		int nrOfTraces = projection.getNumberOfLogtraces();
		for (int traceIndex = 0; traceIndex < nrOfTraces; traceIndex++) {
			String previousActivity = null;
			double[] syncLikelihoods = projection.getEventSyncLikelihoods(traceIndex);
			double traceProbability = projection.getTraceProbability(traceIndex);
			int eventIndex = 0;
			for (String activity : projection.getTrace(traceIndex)) {
				if (!activity2InNode.containsKey(activity)) {
					activities.add(activity);
				}

				//keep trace of synchronousness
				activity2sync.adjustOrPutValue(activity, syncLikelihoods[eventIndex] * traceProbability,
						syncLikelihoods[eventIndex] * traceProbability);
				activity2count.adjustOrPutValue(activity, traceProbability, traceProbability);

				//start activity
				if (previousActivity == null) {
					startActivities.adjustOrPutValue(activity, projection.getTraceProbability(traceIndex),
							projection.getTraceProbability(traceIndex));
				} else {
					//df edge
					edges.adjustOrPutValue(Pair.of(previousActivity, activity),
							projection.getTraceProbability(traceIndex), projection.getTraceProbability(traceIndex));
				}

				previousActivity = activity;
				eventIndex++;
			}

			//to end
			if (previousActivity != null) {
				endActivities.adjustOrPutValue(previousActivity, projection.getTraceProbability(traceIndex),
						projection.getTraceProbability(traceIndex));
			}
		}

		final DecimalFormat numberFormat = new DecimalFormat("0.####");
		ColourMap colourMap = new ColourMapOpacity(new ColourMapFixed(Color.red));

		for (String activity : activities) {
			//DotCluster cluster = dot.addCluster();
			//cluster.setOption("style", "invis");
			Dot cluster = dot;

			//activity
			double sync = activity2sync.get(activity) / activity2count.get(activity);
			DotNode node = cluster.addNode(activity + "\\nsync: " + numberFormat.format(sync));
			//dot.addEdge(inNode, node);
			node.setOption("shape", "box");
			node.setOption("style", "rounded,filled");
			node.setOption("fillcolor", colourMap.toHexAlphaString(colourMap.colour(1 - sync)));
			node.setOption("fontsize", "12");

			//in node
			//			DotNode inNode = cluster.addNode("");
			//			inNode.setOption("width", "0.05");
			//			inNode.setOption("shape", "circle");
			activity2InNode.put(activity, node);

			//out node
			//			DotNode outNode = cluster.addNode("");
			//			outNode.setOption("width", "0.05");
			//			outNode.setOption("shape", "circle");
			activity2OutNode.put(activity, node);
			//			dot.addEdge(node, outNode);
		}

		edges.forEachEntry(new TObjectDoubleProcedure<Pair<String, String>>() {
			public boolean execute(Pair<String, String> a, double b) {
				dot.addEdge(activity2OutNode.get(a.getA()), activity2InNode.get(a.getB()), numberFormat.format(b));
				return true;
			}
		});

		startActivities.forEachEntry(new TObjectDoubleProcedure<String>() {
			public boolean execute(String a, double b) {
				dot.addEdge(start, activity2InNode.get(a), numberFormat.format(b));
				return true;
			}
		});

		endActivities.forEachEntry(new TObjectDoubleProcedure<String>() {
			public boolean execute(String a, double b) {
				dot.addEdge(activity2OutNode.get(a), end, numberFormat.format(b));
				return true;
			}
		});

		return dot;
	}
}
