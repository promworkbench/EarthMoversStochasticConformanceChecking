package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;

public class StochasticPath2String {
	public static String convert(int[] path, StochasticTransition2IndexKey transitionKey,
			Activity2IndexKey activityKey) {
		StringBuilder result = new StringBuilder();
		convert(path, transitionKey, activityKey, result);
		return result.toString();
	}

	public static void convert(int[] path, StochasticTransition2IndexKey transitionKey, Activity2IndexKey activityKey,
			StringBuilder result) {
		result.append("[");
		for (int pathStep = 0; pathStep < path.length; pathStep++) {
			int transitionIndex = path[pathStep];
			int activityIndex = transitionKey.transition2activityIndex(transitionIndex);

			if (transitionIndex < 0) {
				result.append("tau");
			} else {
				result.append(activityKey.toString(activityIndex));
			}
			if (pathStep < path.length - 1) {
				result.append(", ");
			}
		}
		result.append("]");
	}
}
