package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

public class StochasticLanguage2String {
	public static String toString(StochasticLanguage<?> language, boolean useHTML) {
		StringBuilder result = new StringBuilder();

		for (StochasticTraceIterator<?> it = language.iterator(); it.hasNext();) {
			it.next();
			result.append(it.getProbability());
			result.append(" ");
			result.append(language.getTraceString(it.getTraceIndex()));
			if (useHTML) {
				result.append("<br>");
			} else {
				result.append("\n");
			}
		}
		return result.toString();
	}

}
