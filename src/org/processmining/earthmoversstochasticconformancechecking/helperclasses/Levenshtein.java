package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import java.util.Arrays;
import java.util.List;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPath;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * *
 * <p>
 * This code has been adapted from Apache Commons Lang 3.3.
 * </p>
 * 
 * @author sander
 *
 */
public class Levenshtein {

	public static double getNormalisedDistance(int[] left, int[] right) {
		return getDistance(left, right) / (double) Math.max(left.length, right.length);
	}

	public static double getNormalisedDistance(String[] left, String[] right) {
		return getDistance(left, right) / (double) Math.max(left.length, right.length);
	}

	public static int getDistance(String[] left, String[] right) {
		TObjectIntMap<String> map = new TObjectIntHashMap<>(10, 0.5f, -1);
		int lastIndex = -1;

		int[] leftL = new int[left.length];
		for (int i = 0; i < left.length; i++) {
			leftL[i] = map.adjustOrPutValue(left[i], 0, lastIndex + 1);
			if (leftL[i] == lastIndex + 1) {
				lastIndex++;
			}
		}

		int[] rightL = new int[right.length];
		for (int i = 0; i < right.length; i++) {
			rightL[i] = map.adjustOrPutValue(right[i], 0, lastIndex + 1);
			if (rightL[i] == lastIndex + 1) {
				lastIndex++;
			}
		}

		return getDistance(leftL, rightL);
	}

	public static int getDistance(int[] left, int[] right) {
		if (left == null || right == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		/*
		 * This implementation use two variable to record the previous cost
		 * counts, So this implementation use less memory than the previous
		 * impl.
		 */

		int n = left.length; // length of left
		int m = right.length; // length of right

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			final int[] tmp = left;
			left = right;
			right = tmp;
			n = m;
			m = right.length;
		}

		int[] p = new int[n + 1];

		// indexes into strings left and right
		int i; // iterates through left
		int j; // iterates through right
		int upper_left;
		int upper;

		long rightJ; // jth character of right
		int cost; // cost

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			upper_left = p[0];
			rightJ = right[j - 1];
			p[0] = j;

			for (i = 1; i <= n; i++) {
				upper = p[i];
				cost = left[i - 1] == rightJ ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upper_left + cost);
				upper_left = upper;
			}
		}

		return p[n];
	}

	public static <L, M> int[][] getMatrix(List<L> s, List<M> t) {
		int m = s.size();
		int n = t.size();
		// for all i and j, d[i,j] will hold the Levenshtein distance between
		// the first i characters of s and the first j characters of t
		// note that d has (m+1)*(n+1) values
		int[][] d = new int[m + 1][n + 1];

		//set each element in d to zero
		for (int x = 0; x < d.length; x++) {
			Arrays.fill(d[x], 0);
		}

		// source prefixes can be transformed into empty string by
		// dropping all characters
		for (int i = 1; i <= m; i++) {
			d[i][0] = i;
		}

		// target prefixes can be reached from empty source prefix
		// by inserting every character
		for (int j = 1; j <= n; j++) {
			d[0][j] = j;
		}

		//for j from 1 to n:
		for (int j = 1; j <= n; j++) {
			//for i from 1 to m:
			for (int i = 1; i <= m; i++) {
				L sL = s.get(i - 1);
				M tM = t.get(j - 1);

				int substitutionCost = getSubstitutionCost(sL, tM);
				int insertionCost = getInsertionCost(tM);
				int deletionCost = getInsertionCost(sL);
				d[i][j] = Math.min(Math.min(d[i - 1][j] + deletionCost, // deletion
						d[i][j - 1] + insertionCost), // insertion
						d[i - 1][j - 1] + substitutionCost); // substitution
			}
		}
		return d;
	}

	public static int[][] getMatrix(String[] s, StochasticPath t) {
		int m = s.length;
		int n = t.size();
		// for all i and j, d[i,j] will hold the Levenshtein distance between
		// the first i characters of s and the first j characters of t
		// note that d has (m+1)*(n+1) values
		int[][] d = new int[m + 1][n + 1];

		//set each element in d to zero
		for (int x = 0; x < d.length; x++) {
			Arrays.fill(d[x], 0);
		}

		// source prefixes can be transformed into empty string by
		// dropping all characters
		for (int i = 1; i <= m; i++) {
			d[i][0] = i;
		}

		// target prefixes can be reached from empty source prefix
		// by inserting every character
		for (int j = 1; j <= n; j++) {
			d[0][j] = j;
		}

		//for j from 1 to n:
		for (int j = 1; j <= n; j++) {
			//for i from 1 to m:
			for (int i = 1; i <= m; i++) {
				int insertionCost = getInsertionCost(s[i - 1], t.get(j - 1));
				int substitutionCost = getSubstitutionCost(s[i - 1], t.get(j - 1));

				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, // deletion
						d[i][j - 1] + insertionCost), // insertion
						d[i - 1][j - 1] + substitutionCost); // substitution
			}
		}
		return d;
	}

	private static int getInsertionCost(String string, StochasticTransition stochasticTransition) {
		if (stochasticTransition.isInvisible()) {
			return 0;
		} else {
			return 1;
		}
	}

	private static <M> int getInsertionCost(M tM) {
		if (tM instanceof StochasticTransition && ((StochasticTransition) tM).isInvisible()) {
			return 0;
		} else {
			return 1;
		}
	}

	public static int getSubstitutionCost(String s, StochasticTransition transition) {
		if (!transition.isInvisible() && s.equals(transition.getLabel())) {
			return 0;
		} else if (transition.isInvisible()) {
			return 1;
		} else {
			return 2;
		}
	}

	private static <L, M> int getSubstitutionCost(L sL, M tM) {
		String labelL;
		if (sL instanceof StochasticTransition) {
			if (((StochasticTransition) sL).isInvisible()) {
				labelL = null;
			} else {
				labelL = ((StochasticTransition) sL).getLabel();
			}
		} else {
			labelL = sL.toString();
		}

		String labelM;
		if (tM instanceof StochasticTransition) {
			if (((StochasticTransition) tM).isInvisible()) {
				labelM = null;
			} else {
				labelM = ((StochasticTransition) tM).getLabel();
			}
		} else {
			labelM = tM.toString();
		}

		if (labelL == null) {
			if (labelM == null) {
				//both invisible
				return 0;
			} else {
				return 1;
			}
		} else {
			if (labelM == null) {
				return 1;
			} else {
				if (labelL.equals(labelM)) {
					return 0;
				} else {
					return 2;
				}
			}
		}
	}
}
