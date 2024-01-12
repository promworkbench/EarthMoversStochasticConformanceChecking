package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.deckfour.xes.classification.XEventClassifier;

public class uEMSCParametersAbstract implements uEMSCParameters {

	private XEventClassifier classifierA;
	private boolean debug;

	public uEMSCParametersAbstract(XEventClassifier classifierA, boolean debug) {
		this.classifierA = classifierA;
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public XEventClassifier getClassifierA() {
		return classifierA;
	}

	public void setClassifierA(XEventClassifier classifierA) {
		this.classifierA = classifierA;
	}

}
