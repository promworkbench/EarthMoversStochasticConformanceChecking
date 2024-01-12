package org.processmining.earthmoversstochasticconformancechecking.plugins;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsModelProjection;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsModelProjectionImpl;
import org.processmining.earthmoversstochasticconformancechecking.visualisation.StochasticTraceAlignmentsModelVisualisation;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

public class EMSCModelVisualisationPlugin {
	@Plugin(name = "Model B - stochastic alignments (log-model)", returnLabels = {
			"stochastic alignment visualisation (model)" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Stochastic trace alignments" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author
			+ ", Artem Polyvyanyy", email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise stochastic alignment", requiredParameterLabels = { 0 })
	public JComponent fancy(PluginContext context, StochasticTraceAlignmentsLogModel alignments)
			throws UnknownTreeNodeException {
		StochasticTraceAlignmentsModelProjection projection = new StochasticTraceAlignmentsModelProjectionImpl(
				alignments);
		Dot dot = StochasticTraceAlignmentsModelVisualisation.visualise(alignments.getStochasticNet(), projection);

		return new DotPanel(dot);
	}
}
