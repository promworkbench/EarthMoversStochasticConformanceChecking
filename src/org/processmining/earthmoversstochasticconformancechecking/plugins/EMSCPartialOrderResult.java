package org.processmining.earthmoversstochasticconformancechecking.plugins;

import org.processmining.framework.util.HTMLToString;

public class EMSCPartialOrderResult implements HTMLToString {

	private double lowerBound;
	private double upperBound;

	public EMSCPartialOrderResult(double lowerBound, double upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public String toHTMLString(boolean includeHTMLTags) {
		return "The EMSC value lies between " + lowerBound + " and " + upperBound + ".";
	}
}
