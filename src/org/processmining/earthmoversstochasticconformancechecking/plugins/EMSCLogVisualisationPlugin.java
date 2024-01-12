package org.processmining.earthmoversstochasticconformancechecking.plugins;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogLog;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjection;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjectionImpl;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjectionImpl.Which;
import org.processmining.earthmoversstochasticconformancechecking.visualisation.StochasticTraceAlignmentsLogVisualisation;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class EMSCLogVisualisationPlugin {
	@Plugin(name = "Log A - log projection (log-log)", returnLabels = {
			"stochastic trace alignments visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author
			+ ", Artem Polyvyanyy, Wil van der Aalst", email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy1(PluginContext context, StochasticTraceAlignmentsLogLog alignments) {

		//project alignments on log
		StochasticTraceAlignmentsLogProjection projection = new StochasticTraceAlignmentsLogProjectionImpl(alignments,
				Which.A);

		StochasticTraceAlignmentsLogVisualisation list = new StochasticTraceAlignmentsLogVisualisation(projection);
		JScrollPane listScroller = new JScrollPane(list);
		return listScroller;
	}

	@Plugin(name = "Log B - log projection (log-log)", returnLabels = {
			"stochastic trace alignments visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author
			+ ", Artem Polyvyanyy, Wil van der Aalst", email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy2(PluginContext context, StochasticTraceAlignmentsLogLog alignments) {

		//project alignments on log
		StochasticTraceAlignmentsLogProjection projection = new StochasticTraceAlignmentsLogProjectionImpl(alignments,
				Which.B);

		StochasticTraceAlignmentsLogVisualisation list = new StochasticTraceAlignmentsLogVisualisation(projection);
		JScrollPane listScroller = new JScrollPane(list);
		return listScroller;
	}

	@Plugin(name = "Log A - log projection (log-model)", returnLabels = {
			"stochastic trace alignments visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author
			+ ", Artem Polyvyanyy, Wil van der Aalst", email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy2(PluginContext context, StochasticTraceAlignmentsLogModel alignments) {

		//project alignments on log
		StochasticTraceAlignmentsLogProjection projection = new StochasticTraceAlignmentsLogProjectionImpl(alignments,
				Which.A);

		StochasticTraceAlignmentsLogVisualisation list = new StochasticTraceAlignmentsLogVisualisation(projection);
		JScrollPane listScroller = new JScrollPane(list);
		return listScroller;
	}
}
