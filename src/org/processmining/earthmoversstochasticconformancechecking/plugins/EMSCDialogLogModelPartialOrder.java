package org.processmining.earthmoversstochasticconformancechecking.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersLogModelPartialAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersLogPartialUncertainModelPartialCertainDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersLogTotalModelPartialCertainDefault;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class EMSCDialogLogModelPartialOrder extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 400;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	public static final String massText = " of the model's stochastic behaviour";
	public static final String timeText = " minutes";

	private EMSCParametersLogModelPartialAbstract<?, ?> parameters = new EMSCParametersLogTotalModelPartialCertainDefault();

	private final SpringLayout layout;

	public static final String doi = "http://leemans.ch";

	public EMSCDialogLogModelPartialOrder(XLog xLog) {
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

		//log uncertain
		final JCheckBox logUncertain;
		{
			JLabel logUncertainLabel = factory.createLabel("Log events with equivalent timestamps are concurrent");
			add(logUncertainLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, logUncertainLabel, rowHeight,
					SpringLayout.VERTICAL_CENTER, firstClassifiers);
			layout.putConstraint(SpringLayout.EAST, logUncertainLabel, leftColumnWidth, SpringLayout.WEST, this);

			logUncertain = factory.createCheckBox("", false);
			add(logUncertain);
			layout.putConstraint(SpringLayout.WEST, logUncertain, columnMargin, SpringLayout.EAST, logUncertainLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, logUncertain, 0, SpringLayout.VERTICAL_CENTER,
					logUncertainLabel);
		}

		//NB
		final JLabel NBLabel;
		final JLabel NBValue;
		{
			NBLabel = factory.createLabel("Nota Bene");
			add(NBLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, NBLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					logUncertain);
			layout.putConstraint(SpringLayout.EAST, NBLabel, leftColumnWidth, SpringLayout.WEST, this);

			NBValue = factory.createLabel("<html>The model must be stochastically sound, safe<br>and confusion-free.</html>");
			add(NBValue);
			layout.putConstraint(SpringLayout.WEST, NBValue, columnMargin, SpringLayout.EAST, NBLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, NBValue, 0, SpringLayout.VERTICAL_CENTER, NBLabel);
		}

		//doi
		final JLabel doiLabel;
		final JLabel doiValue;
		{
			doiLabel = factory.createLabel("More information");
			add(doiLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, doiLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					NBValue);
			layout.putConstraint(SpringLayout.EAST, doiLabel, leftColumnWidth, SpringLayout.WEST, this);

			doiValue = factory.createLabel(doi);
			add(doiValue);
			layout.putConstraint(SpringLayout.WEST, doiValue, columnMargin, SpringLayout.EAST, doiLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, doiValue, 0, SpringLayout.VERTICAL_CENTER, doiLabel);
		}

		//set up the controller
		firstClassifiers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parameters.setLogClassifier(firstClassifiers.getSelectedClassifier());
			}
		});
		parameters.setLogClassifier(firstClassifiers.getSelectedClassifier());

		logUncertain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (logUncertain.isSelected()) {
					//log uncertain
					parameters = new EMSCParametersLogPartialUncertainModelPartialCertainDefault();
				} else {
					//log total order
					parameters = new EMSCParametersLogTotalModelPartialCertainDefault();
				}
			}
		});

		doiValue.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				IMMiningDialog.openWebPage(doi);
			}
		});

	}

	public EMSCParametersLogModelPartialAbstract<?, ?> getParameters() {
		return parameters;
	}
}