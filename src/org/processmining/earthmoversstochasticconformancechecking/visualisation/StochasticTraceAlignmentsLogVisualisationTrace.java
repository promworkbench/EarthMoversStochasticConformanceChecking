package org.processmining.earthmoversstochasticconformancechecking.visualisation;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjection;

/**
 * This class renders an aligned trace for a JList or similar.
 * 
 * @author sander
 *
 */
public class StochasticTraceAlignmentsLogVisualisationTrace extends StochasticTraceAlignmentsLogVisualisationTraceComponent
		implements ListCellRenderer<Object> {

	private static final long serialVersionUID = 6472163016425890008L;

	public StochasticTraceAlignmentsLogVisualisationTrace(StochasticTraceAlignmentsLogProjection projection) {
		super(projection);
	}

	public Component getListCellRendererComponent(JList<? extends Object> list, Object notNeeded, int traceIndex,
			boolean isSelected, boolean cellHasFocus) {
		set(traceIndex);
		setTraceSelected(isSelected);
		return this;
	}
}
