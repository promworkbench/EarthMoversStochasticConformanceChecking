package org.processmining.earthmoversstochasticconformancechecking.plugins;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersModelModelPartialAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersModelPartialCertainModelPartialCertainDefault;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class EMSCDialogModelModelPartialOrder extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	public static final String massText = " of the model's stochastic behaviour";
	public static final String timeText = " minutes";

	private EMSCParametersModelModelPartialAbstract<?, ?> parameters = new EMSCParametersModelPartialCertainModelPartialCertainDefault();

	private final SpringLayout layout;

	public EMSCDialogModelModelPartialOrder() {
		SlickerFactory factory = SlickerFactory.instance();

		layout = new SpringLayout();
		setLayout(layout);

		//NB
		final JLabel NBLabel;
		final JLabel NBValue;
		{
			NBLabel = factory.createLabel("Nota Bene");
			add(NBLabel);
			layout.putConstraint(SpringLayout.NORTH, NBLabel, 5, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, NBLabel, leftColumnWidth, SpringLayout.WEST, this);

			NBValue = factory
					.createLabel("<html>The models must be stochastically sound, safe<br>and confusion-free.</html>");
			add(NBValue);
			layout.putConstraint(SpringLayout.WEST, NBValue, columnMargin, SpringLayout.EAST, NBLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, NBValue, 0, SpringLayout.VERTICAL_CENTER, NBLabel);
		}

	}

	public EMSCParametersModelModelPartialAbstract<?, ?> getParameters() {
		return parameters;
	}
}