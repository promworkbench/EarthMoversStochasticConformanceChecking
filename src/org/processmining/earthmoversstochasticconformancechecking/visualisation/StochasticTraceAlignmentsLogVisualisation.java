package org.processmining.earthmoversstochasticconformancechecking.visualisation;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjection;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjectionSorted;

public class StochasticTraceAlignmentsLogVisualisation extends JList<Object> {

	private static final long serialVersionUID = 2932404485893881410L;
	private StochasticTraceAlignmentsLogVisualisationTrace traceRenderer;
	private StochasticTraceAlignmentsLogProjection projection;

	public StochasticTraceAlignmentsLogVisualisation(StochasticTraceAlignmentsLogProjection projection) {
		super();

		projection = new StochasticTraceAlignmentsLogProjectionSorted(projection);
		this.projection = projection;
		setModel(new AbstractListModel<Object>() {
			private static final long serialVersionUID = 4092905639524616603L;

			public Object getElementAt(int index) {
				return null;
			}

			public int getSize() {
				return StochasticTraceAlignmentsLogVisualisation.this.projection.getNumberOfLogtraces();
			}
		});

		traceRenderer = new StochasticTraceAlignmentsLogVisualisationTrace(projection);
		setCellRenderer(traceRenderer);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(-1);
	}

	public void setProjection(StochasticTraceAlignmentsLogProjection projection) {
		traceRenderer.setProjection(new StochasticTraceAlignmentsLogProjectionSorted(projection));
	}
}
