package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import java.text.DecimalFormat;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;

public class DistanceMatrix2String {
	public static <A, B> String toLatex(StochasticLanguage<A> languageA, StochasticLanguage<B> languageB,
			double[] distances) {
		StringBuilder result = new StringBuilder();

		//header
		{
			result.append("&");
			StochasticTraceIterator<B> itB = languageB.iterator();
			for (int m = 0; m < languageB.size(); m++) {
				String traceM = languageB.getTraceString(itB.getTraceIndex());

				result.append("\\rotatebox{90}{$\\langle " + traceM + "\\rangle$}\n");

				if (itB.hasNext()) {
					result.append("&");
				}
			}
		}

		result.append("\\\\\n");

		StochasticTraceIterator<A> itA = languageA.iterator();
		for (int l = 0; l < languageA.size(); l++) {
			String traceA = languageA.getTraceString(itA.getTraceIndex());

			result.append(toString(traceA));
			result.append("&");

			for (int m = 0; m < languageB.size(); m++) {
				double distance = distances[l * languageB.size() + m + 1];
				result.append(toString(distance));
				if (m < languageB.size() - 1) {
					result.append("&");
				}
			}

			result.append("\\\\\n");
		}

		return result.toString();
	}

	public static String toString(double value) {
		return new DecimalFormat("0.00").format(value);
	}

	public static String toString(String trace) {
		String r = ArrayUtils.toString(trace);
		r = r.substring(1, r.length() - 1);
		return "$\\langle " + r + "\\rangle$";
	}
}
