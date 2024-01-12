package org.processmining.earthmoversstochasticconformancechecking.plugins;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogLog;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjection;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjectionImpl;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjectionImpl.Which;
import org.processmining.earthmoversstochasticconformancechecking.visualisation.StochasticTraceAlignmentsLogDfgVisualisation;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

public class EMSCLogDfgVisualisationPlugin {
	public static final int maxNodeSize = 50;

	@Plugin(name = "Log A - directly follows (log-log)", returnLabels = {
			"directly follows visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy1(PluginContext context, StochasticTraceAlignmentsLogLog alignments) {

		//project alignments on log
		StochasticTraceAlignmentsLogProjection projection = new StochasticTraceAlignmentsLogProjectionImpl(alignments,
				Which.A);

		Dot dot = StochasticTraceAlignmentsLogDfgVisualisation.convert(projection);

		if (dot.getNodes().size() > maxNodeSize) {
			dot = new Dot();
			dot.addNode("Graphs of more than " + maxNodeSize + " nodes are not visualised to prevent hanging.");
		}

		return new DotPanel(dot);
	}

	@Plugin(name = "Log B - directly follows (log-log)", returnLabels = {
			"directly follows visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy2(PluginContext context, StochasticTraceAlignmentsLogLog alignments) {

		//project alignments on log
		StochasticTraceAlignmentsLogProjection projection = new StochasticTraceAlignmentsLogProjectionImpl(alignments,
				Which.B);

		Dot dot = StochasticTraceAlignmentsLogDfgVisualisation.convert(projection);

		if (dot.getNodes().size() > maxNodeSize) {
			dot = new Dot();
			dot.addNode("Graphs of more than " + maxNodeSize + " nodes are not visualised to prevent hanging.");
		}

		return new DotPanel(dot);
	}

	@Plugin(name = "Log A - directly follows (log-model)", returnLabels = {
			"directly follows visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy2(PluginContext context, StochasticTraceAlignmentsLogModel alignments) {

		//project alignments on log
		StochasticTraceAlignmentsLogProjection projection = new StochasticTraceAlignmentsLogProjectionImpl(alignments,
				Which.A);

		Dot dot = StochasticTraceAlignmentsLogDfgVisualisation.convert(projection);

		if (dot.getNodes().size() > maxNodeSize) {
			dot = new Dot();
			dot.addNode("Graphs of more than " + maxNodeSize + " nodes are not visualised to prevent hanging.");
		}

		return new DotPanel(dot);
	}
}
