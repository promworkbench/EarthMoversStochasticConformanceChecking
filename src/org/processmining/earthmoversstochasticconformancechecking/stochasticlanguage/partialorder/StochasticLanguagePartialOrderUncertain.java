package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderUncertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;

public abstract class StochasticLanguagePartialOrderUncertain implements StochasticLanguage<PartialOrderUncertain> {

	private final Activity2IndexKey activityKey;

	public StochasticLanguagePartialOrderUncertain(Activity2IndexKey activityKey) {
		this.activityKey = activityKey;
	}

	@Override
	public String getTraceString(int traceIndex) {
		return "one of " + PartialOrder2String.toStringActivity(getTrace(traceIndex), activityKey);
	}

	@Override
	public Activity2IndexKey getActivityKey() {
		return activityKey;
	}

}