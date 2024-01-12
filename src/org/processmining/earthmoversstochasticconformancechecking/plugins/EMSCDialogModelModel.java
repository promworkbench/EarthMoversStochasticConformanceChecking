package org.processmining.earthmoversstochasticconformancechecking.plugins;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersModelModel;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersModelModelAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersModelModelDefault;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class EMSCDialogModelModel extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	public static final String massTextA = " of model A's stochastic behaviour";
	public static final String massTextB = " of model B's stochastic behaviour";
	public static final String timeText = " minutes";

	private EMSCParametersModelModelAbstract parameters = new EMSCParametersModelModelDefault();

	private final SpringLayout layout;

	public EMSCDialogModelModel() {
		SlickerFactory factory = SlickerFactory.instance();

		layout = new SpringLayout();
		setLayout(layout);

		//first group
		final JLabel terminationConditionA;
		{
			JLabel terminationCondition = factory.createLabel("Gather at least");
			add(terminationCondition);
			layout.putConstraint(SpringLayout.NORTH, terminationCondition, 5, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, terminationCondition, leftColumnWidth, SpringLayout.WEST, this);

			final JSlider sliderMass = factory.createSlider(SwingConstants.HORIZONTAL);
			sliderMass.setMinimum(0);
			sliderMass.setMaximum(1000);
			sliderMass.setValue((int) (parameters.getTerminationStrategyA().getMaxMassCovered() * 1000));
			add(sliderMass);
			layout.putConstraint(SpringLayout.WEST, sliderMass, columnMargin, SpringLayout.EAST, terminationCondition);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, sliderMass, 0, SpringLayout.VERTICAL_CENTER,
					terminationCondition);

			final JLabel firstNoiseValue = factory
					.createLabel(String.format("%.2f", sliderMass.getValue() / 1000.0) + massTextA);
			add(firstNoiseValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, firstNoiseValue, 0, SpringLayout.VERTICAL_CENTER,
					terminationCondition);
			layout.putConstraint(SpringLayout.WEST, firstNoiseValue, 5, SpringLayout.EAST, sliderMass);

			sliderMass.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					parameters.getTerminationStrategyA().setMaxMassCovered(sliderMass.getValue() / 1000.0);
					firstNoiseValue.setText(String.format("%.2f", sliderMass.getValue() / 1000.0) + massTextA);
				}
			});

			terminationConditionA = factory.createLabel("however, do not take longer than");
			add(terminationConditionA);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, terminationConditionA, rowHeight,
					SpringLayout.VERTICAL_CENTER, terminationCondition);
			layout.putConstraint(SpringLayout.EAST, terminationConditionA, leftColumnWidth, SpringLayout.WEST, this);

			final JSlider sliderTime = factory.createSlider(SwingConstants.HORIZONTAL);
			sliderTime.setMinimum(0);
			sliderTime.setMaximum(1000);
			sliderTime.setValue((int) (parameters.getTerminationStrategyA().getMaxDuration() / (1000 * 60)));
			add(sliderTime);
			layout.putConstraint(SpringLayout.WEST, sliderTime, columnMargin, SpringLayout.EAST, terminationConditionA);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, sliderTime, 0, SpringLayout.VERTICAL_CENTER,
					terminationConditionA);

			final JLabel timeValue = factory.createLabel(sliderTime.getValue() + timeText);
			add(timeValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, timeValue, 0, SpringLayout.VERTICAL_CENTER,
					terminationConditionA);
			layout.putConstraint(SpringLayout.WEST, timeValue, 5, SpringLayout.EAST, sliderMass);

			sliderTime.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					parameters.getTerminationStrategyA().setMaxDuration(sliderTime.getValue() * 1000 * 60);
					timeValue.setText(sliderTime.getValue() + timeText);
				}
			});
		}

		//second group
		{
			JLabel terminationCondition = factory.createLabel("Gather at least");
			add(terminationCondition);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, terminationCondition, rowHeight * 2,
					SpringLayout.VERTICAL_CENTER, terminationConditionA);
			layout.putConstraint(SpringLayout.EAST, terminationCondition, leftColumnWidth, SpringLayout.WEST, this);

			final JSlider sliderMass = factory.createSlider(SwingConstants.HORIZONTAL);
			sliderMass.setMinimum(0);
			sliderMass.setMaximum(1000);
			sliderMass.setValue((int) (parameters.getTerminationStrategyB().getMaxMassCovered() * 1000));
			add(sliderMass);
			layout.putConstraint(SpringLayout.WEST, sliderMass, columnMargin, SpringLayout.EAST, terminationCondition);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, sliderMass, 0, SpringLayout.VERTICAL_CENTER,
					terminationCondition);

			final JLabel firstNoiseValue = factory
					.createLabel(String.format("%.2f", sliderMass.getValue() / 1000.0) + massTextB);
			add(firstNoiseValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, firstNoiseValue, 0, SpringLayout.VERTICAL_CENTER,
					terminationCondition);
			layout.putConstraint(SpringLayout.WEST, firstNoiseValue, 5, SpringLayout.EAST, sliderMass);

			sliderMass.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					parameters.getTerminationStrategyB().setMaxMassCovered(sliderMass.getValue() / 1000.0);
					firstNoiseValue.setText(String.format("%.2f", sliderMass.getValue() / 1000.0) + massTextB);
				}
			});

			JLabel terminationCondition2 = factory.createLabel("however, do not take longer than");
			add(terminationCondition2);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, terminationCondition2, rowHeight,
					SpringLayout.VERTICAL_CENTER, terminationCondition);
			layout.putConstraint(SpringLayout.EAST, terminationCondition2, leftColumnWidth, SpringLayout.WEST, this);

			final JSlider sliderTime = factory.createSlider(SwingConstants.HORIZONTAL);
			sliderTime.setMinimum(0);
			sliderTime.setMaximum(1000);
			sliderTime.setValue((int) (parameters.getTerminationStrategyB().getMaxDuration() / (1000 * 60)));
			add(sliderTime);
			layout.putConstraint(SpringLayout.WEST, sliderTime, columnMargin, SpringLayout.EAST, terminationCondition2);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, sliderTime, 0, SpringLayout.VERTICAL_CENTER,
					terminationCondition2);

			final JLabel timeValue = factory.createLabel(sliderTime.getValue() + timeText);
			add(timeValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, timeValue, 0, SpringLayout.VERTICAL_CENTER,
					terminationCondition2);
			layout.putConstraint(SpringLayout.WEST, timeValue, 5, SpringLayout.EAST, sliderMass);

			sliderTime.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					parameters.getTerminationStrategyB().setMaxDuration(sliderTime.getValue() * 1000 * 60);
					timeValue.setText(sliderTime.getValue() + timeText);
				}
			});
		}
	}

	public EMSCParametersModelModel getParameters() {
		return parameters;
	}
}