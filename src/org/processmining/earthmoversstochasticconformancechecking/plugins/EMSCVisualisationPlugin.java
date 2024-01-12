package org.processmining.earthmoversstochasticconformancechecking.plugins;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.util.HtmlPanel;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignment;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentImpl;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignments;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignments.StochasticTraceAlignmentIterator;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogLog;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsModelModel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class EMSCVisualisationPlugin {
	@Plugin(name = "Earth Movers' Stochastic Conformance (log-log)", returnLabels = {
			"stochastic trace alignments visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author
			+ ", Artem Polyvyanyy, Wil van der Aalst", email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy(PluginContext context, StochasticTraceAlignmentsLogLog alignments) {
		return fancy(alignments);
	}

	@Plugin(name = "Earth Movers' Stochastic Conformance (log-model)", returnLabels = {
			"stochastic trace alignments visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author
			+ ", Artem Polyvyanyy, Wil van der Aalst", email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy(PluginContext context, StochasticTraceAlignmentsLogModel alignments) {
		return fancy(alignments);
	}

	@Plugin(name = "Earth Movers' Stochastic Conformance (model-model)", returnLabels = {
			"stochastic trace alignments visualisation" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author
			+ ", Artem Polyvyanyy, Wil van der Aalst", email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic trace alignments", requiredParameterLabels = { 0 })
	public JComponent fancy(PluginContext context, StochasticTraceAlignmentsModelModel alignments) {
		return fancy(alignments);
	}

	public static <L, M> JComponent fancy(StochasticTraceAlignments<L, M> alignments) {
		StringBuilder result = new StringBuilder();

		result.append("Similarity: " + alignments.getSimilarity());

		result.append(
				"<br><br>Where 1 is stochastical-language equivalent, and 0 is having not a single event in common.<br><br>");

		//addTable(alignments, result);

		return new HtmlPanel(result.toString());
	}

	public static <M, L> void addTable(StochasticTraceAlignments<L, M> alignments, StringBuilder result) {
		result.append("<table>");

		for (StochasticTraceAlignmentIterator<L, M> it = alignments.iterator(); it.hasNext();) {

			StochasticTraceAlignment<L, M> alignment = it.next();

			result.append("<tr><td>");
			result.append(it.getProbability());
			result.append("</td>");
			if (alignment instanceof StochasticTraceAlignmentImpl) {
				result.append(((StochasticTraceAlignmentImpl<L, M>) alignment).toHTMLString());
			} else {
				result.append(alignment.toString());
			}
			result.append("</tr>");
		}

		result.append("</table>");
	}
}