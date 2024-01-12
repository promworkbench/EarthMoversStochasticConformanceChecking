package org.processmining.earthmoversstochasticconformancechecking.visualisation;

import java.awt.Color;

import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsModelProjection;
import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMapFixed;
import org.processmining.plugins.graphviz.colourMaps.ColourMapOpacity;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class StochasticTraceAlignmentsModelVisualisation {

	private static final ColourMap colourMap = new ColourMapOpacity(new ColourMapFixed(Color.red));

	public static Dot visualise(StochasticLabelledPetriNet net, StochasticTraceAlignmentsModelProjection projection) {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		dot.setOption("forcelabels", "true");

		TIntObjectMap<DotNode> place2node = new TIntObjectHashMap<>();

		for (int place = 0; place < net.getNumberOfPlaces(); place++) {
			DotNode dotNode = dot.addNode("");
			dotNode.setOption("shape", "circle");
			place2node.put(place, dotNode);
		}

		for (int transition = 0; transition < net.getNumberOfTransitions(); transition++) {
			DotNode dotNode = dot.addNode("");
			dotNode.setOption("style", "filled");
			if (net.isTransitionSilent(transition)) {
				dotNode.setOption("fillcolor", "#EEEEEE");
				dotNode.setOption("width", "0.15");
			} else {
				double syncProbability = projection.getTransitionSyncLikelihood(transition);
				String syncLabel;
				if (syncProbability < 1) {
					syncLabel = StochasticTraceAlignmentsLogVisualisationTraceComponent.formatNumber(syncProbability)
							+ " sync move";
				} else {
					syncLabel = "1 sync move";
				}

				//dotNode.setOption("xlabel", syncLabel);
				dotNode.setLabel(net.getTransitionLabel(transition) + "\n" + syncLabel);
				dotNode.setOption("fillcolor", colourMap.toHexAlphaString(colourMap.colour(1 - syncProbability)));
			}
			dotNode.setOption("shape", "box");
			place2node.put(transition, dotNode);

			//edges
			for (int targetPlace : net.getOutputPlaces(transition)) {
				dot.addEdge(dotNode, place2node.get(targetPlace));
			}
			for (int sourcePlace : net.getInputPlaces(transition)) {
				dot.addEdge(place2node.get(sourcePlace), dotNode);
			}

		}

		return dot;
	}
}
