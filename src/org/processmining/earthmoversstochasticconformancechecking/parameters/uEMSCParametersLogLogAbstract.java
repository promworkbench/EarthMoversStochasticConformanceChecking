package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.deckfour.xes.classification.XEventClassifier;

public class uEMSCParametersLogLogAbstract extends uEMSCParametersAbstract implements uEMSCParametersLogLog {

	private XEventClassifier classifierB;

	public uEMSCParametersLogLogAbstract(XEventClassifier classifierA, XEventClassifier classifierB, boolean debug) {
		super(classifierA, debug);
		this.classifierB = classifierB;
	}

	public XEventClassifier getClassifierB() {
		return classifierB;
	}

	public void setClassifierB(XEventClassifier classifierB) {
		this.classifierB = classifierB;
	}

}
