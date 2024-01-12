package org.processmining.earthmoversstochasticconformancechecking.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModel;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelDefault;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class EMSCDialogLogModel extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	public static final String massText = " of the model's stochastic behaviour";
	public static final String timeText = " minutes";

	private EMSCParametersLogModelAbstract parameters = new EMSCParametersLogModelDefault();

	private final SpringLayout layout;

	public EMSCDialogLogModel(XLog xLog) {
		SlickerFactory factory = SlickerFactory.instance();

		layout = new SpringLayout();
		setLayout(layout);

		//first group
		final ClassifierChooser firstClassifiers;
		{
			JLabel classifierLabel = factory.createLabel("Log event classifier");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.NORTH, classifierLabel, 5, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			firstClassifiers = new ClassifierChooser(xLog);
			add(firstClassifiers);
			layout.putConstraint(SpringLayout.WEST, firstClassifiers, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, firstClassifiers, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		//second group
		{
			JLabel terminationCondition = factory.createLabel("Gather at least");
			add(terminationCondition);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, terminationCondition, rowHeight * 2,
					SpringLayout.VERTICAL_CENTER, firstClassifiers);
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
					.createLabel(String.format("%.2f", sliderMass.getValue() / 1000.0) + massText);
			add(firstNoiseValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, firstNoiseValue, 0, SpringLayout.VERTICAL_CENTER,
					terminationCondition);
			layout.putConstraint(SpringLayout.WEST, firstNoiseValue, 5, SpringLayout.EAST, sliderMass);

			sliderMass.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					parameters.getTerminationStrategyB().setMaxMassCovered(sliderMass.getValue() / 1000.0);
					firstNoiseValue.setText(String.format("%.2f", sliderMass.getValue() / 1000.0) + massText);
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

		//set up the controller
		firstClassifiers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parameters.setLogClassifier(firstClassifiers.getSelectedClassifier());
			}
		});
		parameters.setLogClassifier(firstClassifiers.getSelectedClassifier());

	}

	public EMSCParametersLogModel getParameters() {
		return parameters;
	}
}