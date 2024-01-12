package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix;

import java.text.DecimalFormat;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix.ReallocationMatrixIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;

public class ReallocationMatrix2String {
	public static String toString(StochasticLanguage<?> languageA, StochasticLanguage<?> languageB,
			ReallocationMatrix matrix) {
		StringBuilder result = new StringBuilder();

		for (ReallocationMatrixIterator it = matrix.iterator(); it.hasNext();) {
			double probability = it.next();
			int traceAIndex = it.getTraceAIndex();
			int traceBIndex = it.getTraceBIndex();

			result.append(probability);
			result.append(languageA.getTraceString(traceAIndex));
			result.append(" to ");
			result.append(languageB.getTraceString(traceBIndex));

			if (it.hasNext()) {
				result.append(", ");
			}
		}

		return result.toString();
	}

	public static String toString(double value) {
		if (value == 0) {
			return "0";
		}
		return new DecimalFormat("0.0000").format(value);
	}

	public static String toString(String[] trace) {
		String r = ArrayUtils.toString(trace);
		r = r.substring(1, r.length() - 1);
		return "$\\langle " + r + "\\rangle$";
	}
}
