package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model;

public class StochasticPathLanguage2String {
	public static String convert(StochasticPathLanguage<?> language) {
		StringBuilder result = new StringBuilder();

		result.append("{");
		for (StochasticPathIterator<?> it = language.iterator(); it.hasNext();) {
			int[] path = it.nextPath();
			double probability = it.getProbability();

			StochasticPath2String.convert(path, language.getTransitionKey(), language.getActivityKey(), result);
			result.append(": ");
			result.append(probability);

			if (it.hasNext()) {
				result.append(", ");
			}
		}
		result.append("}");

		return result.toString();
	}
}