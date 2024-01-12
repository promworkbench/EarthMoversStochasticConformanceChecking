package org.processmining.earthmoversstochasticconformancechecking.parameters;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public  class EMSCParametersLogLogAbstract extends EMSCParametersAbstract<TotalOrder, TotalOrder>
		implements EMSCParametersLogLog {
	private XEventClassifier eventClassifierA;
	private XEventClassifier eventClassifierB;

	public EMSCParametersLogLogAbstract(DistanceMatrix<TotalOrder, TotalOrder> distanceMatrix,
			XEventClassifier eventClassifierA, XEventClassifier eventClassifierB, boolean debug,
			boolean computeStochasticTraceAlignments) {
		super(debug, computeStochasticTraceAlignments);
		this.eventClassifierA = eventClassifierA;
		this.eventClassifierB = eventClassifierB;
		setDistanceMatrix(distanceMatrix);
	}

	public XEventClassifier getClassifierA() {
		return eventClassifierA;
	}

	public void setClassifierA(XEventClassifier eventClassifierA) {
		this.eventClassifierA = eventClassifierA;
	}

	public XEventClassifier getClassifierB() {
		return eventClassifierB;
	}

	public void setClassifierB(XEventClassifier eventClassifierB) {
		this.eventClassifierB = eventClassifierB;
	}

	public TotalOrder getOrderA() {
		return TotalOrder.instance();
	}

	public TotalOrder getOrderB() {
		// TODO Auto-generated method stub
		return null;
	}

}