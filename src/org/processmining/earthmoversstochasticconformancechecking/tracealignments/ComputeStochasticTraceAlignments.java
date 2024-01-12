package org.processmining.earthmoversstochasticconformancechecking.tracealignments;

import java.util.Arrays;
import java.util.List;

import org.processmining.earthmoversstochasticconformancechecking.helperclasses.Levenshtein;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPath;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.TotalOrderUtils;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

public class ComputeStochasticTraceAlignments {

	/**
	 * Compute the stochastic alignment. Does not keep track of the projection
	 * to a model.
	 * 
	 * @param relocationMatrix
	 * @param languageA
	 * @param languageB
	 * @param result
	 */
	public static StochasticTraceAlignmentsLogLog computeLogLog(ReallocationMatrix relocationMatrix, double similarity,
			StochasticLanguage<TotalOrder> languageA, StochasticLanguage<TotalOrder> languageB) {
		StochasticTraceAlignmentsLogLog result = new StochasticTraceAlignmentsLogLog(relocationMatrix, similarity,
				languageA, languageB);

		for (StochasticTraceAlignmentsLogLog.EditIterator it = result.iterator(); it.hasNext();) {
			it.next();
			int traceIndexA = it.getTraceAIndex();
			int traceIndexB = it.getTraceBIndex();

			String[] traceA = TotalOrderUtils.getStringTrace(languageA, traceIndexA);
			String[] traceB = TotalOrderUtils.getStringTrace(languageB, traceIndexB);

			StochasticTraceAlignment<String, String> traceAlignment = processTracePair(Arrays.asList(traceA),
					Arrays.asList(traceB));
			it.set(traceAlignment);
		}
		return result;
	}

	public static StochasticTraceAlignmentsLogLog emptyLogLog(ReallocationMatrix relocationMatrix, double similarity,
			StochasticLanguage<TotalOrder> languageA, StochasticLanguage<TotalOrder> languageB) {
		StochasticTraceAlignmentsLogLog result = new StochasticTraceAlignmentsLogLog(relocationMatrix, similarity,
				languageA, languageB);
		return result;
	}

	/**
	 * Compute the stochastic alignment. Keeps track of the projection to a
	 * model.
	 * 
	 * @param reallocationMatrix
	 * @param languageA
	 * @param languageModel
	 * @param result
	 */
	public static StochasticTraceAlignmentsLogModel computeLogModel(ReallocationMatrix reallocationMatrix,
			double similarity, StochasticLanguage<TotalOrder> languageA, StochasticPathLanguage<TotalOrder> languageB,
			StochasticLabelledPetriNet net) {
		StochasticTraceAlignmentsLogModel result = new StochasticTraceAlignmentsLogModel(reallocationMatrix, similarity,
				net, languageA, languageB);

		for (StochasticTraceAlignmentsLogModel.EditIteratorLogModel it = result.iterator(); it.hasNext();) {
			it.next();
			int traceIndexA = it.getTraceAIndex();
			String[] traceA = TotalOrderUtils.getStringTrace(languageA, traceIndexA);
			StochasticPath pathB = it.getStochasticPathB();

			StochasticTraceAlignment<String, StochasticTransition> traceAlignment = processTracePair(
					Arrays.asList(traceA), pathB);
			it.set(traceAlignment);
		}
		return result;
	}

	public static StochasticTraceAlignmentsLogModel emptyLogModel(ReallocationMatrix reallocationMatrix,
			double similarity, StochasticLanguage<TotalOrder> languageA, StochasticPathLanguage<TotalOrder> languageB,
			StochasticLabelledPetriNet net) {
		StochasticTraceAlignmentsLogModel result = new StochasticTraceAlignmentsLogModel(reallocationMatrix, similarity,
				net, languageA, languageB);
		return result;
	}

	/**
	 * Compute the stochastic alignment. Keeps track of the projection to a
	 * model.
	 * 
	 * @param relocationMatrix
	 * @param languageLog
	 * @param languageModel
	 * @param result
	 */
	public static StochasticTraceAlignmentsModelModel computeModelModel(ReallocationMatrix relocationMatrix,
			double similarity, StochasticPathLanguage<TotalOrder> languageA,
			StochasticPathLanguage<TotalOrder> languageB, StochasticLabelledPetriNet netA,
			StochasticLabelledPetriNet netB) {
		StochasticTraceAlignmentsModelModel result = new StochasticTraceAlignmentsModelModel(relocationMatrix,
				similarity, netA, netB, languageA, languageB);

		for (StochasticTraceAlignmentsModelModel.EditIteratorModelModel it = result.iterator(); it.hasNext();) {
			it.next();
			StochasticPath pathA = it.getStochasticPathA();
			StochasticPath pathB = it.getStochasticPathB();

			StochasticTraceAlignment<StochasticTransition, StochasticTransition> traceAlignment = processTracePair(
					pathA, pathB);
			it.set(traceAlignment);
		}
		return result;
	}

	public static StochasticTraceAlignmentsModelModel emptyModelModel(ReallocationMatrix relocationMatrix,
			double similarity, StochasticPathLanguage<TotalOrder> languageA,
			StochasticPathLanguage<TotalOrder> languageB, StochasticLabelledPetriNet netA,
			StochasticLabelledPetriNet netB) {
		StochasticTraceAlignmentsModelModel result = new StochasticTraceAlignmentsModelModel(relocationMatrix,
				similarity, netA, netB, languageA, languageB);
		return result;
	}

	public static <L, M> StochasticTraceAlignment<L, M> processTracePair(List<L> logTrace, List<M> modelPath) {
		int[][] levenshtein = Levenshtein.getMatrix(logTrace, modelPath);

		StochasticTraceAlignmentImpl<L, M> result = new StochasticTraceAlignmentImpl<L, M>();

		/*
		 * Notice that the Levenshtein matrix has a dummy first column and first
		 * row, thus every reported index is subtracted by one.
		 */

		int l = levenshtein.length - 1;
		int m = levenshtein[0].length - 1;

		while ((l > 0 || m > 0)) {
			//			if the value in the diagonal cell (going up+left) is smaller or equal to the
			//		      values found in the other two cells
			//		   AND 
			//		      if this is same or 1 minus the value of the current cell 

			if (l > 0 && m > 0 && //
					levenshtein[l - 1][m - 1] <= levenshtein[l - 1][m] && //
					levenshtein[l - 1][m - 1] <= levenshtein[l][m - 1] && //
					levenshtein[l - 1][m - 1] == levenshtein[l][m]) {
				//only take a substitution step if it's free
				//		   then  "take the diagonal cell"

				//		         if the value of the diagonal cell is one less than the current cell:
				//		            Add a SUBSTITUTION operation (from the letters corresponding to
				//		            the _current_ cell)
				//if (levenshtein[l - 1][m - 1] != levenshtein[l][m]) {
				//report synchronous move
				if (l > 0 && m > 0) {
					reportSynchronousMove(result, logTrace.get(l - 1), modelPath.get(m - 1));
				}
				//} else {
				//		         otherwise: do not add an operation this was a no-operation.
				//}
				l = l - 1;
				m = m - 1;
			} else //
					//		   elseif the value in the cell to the left is smaller of equal to the value of
					//		       the of the cell above current cell
					//		   AND 
					//		       if this value is same or 1 minus the value of the current cell
			if (l == 0 || (m > 0 && levenshtein[l][m - 1] <= levenshtein[l - 1][m] && //
					levenshtein[l][m - 1] <= levenshtein[l][m])) {
				//				   then "take the cell to left", and
				//		        add an INSERTION of the letter corresponding to the cell

				if (m > 0) {
					reportModelMove(result, modelPath.get(m - 1));
				}

				m = m - 1;
			} else {
				//				   else
				//		       take the cell above, add
				//		       Add a DELETION operation of the letter in 's string'

				//report log move
				if (l > 0) {
					reportLogMove(result, logTrace.get(l - 1));
				}

				assert (l > 0);
				l = l - 1;
			}
		}

		//the thing is in reverse order, so reverse the stochastic trace alignment
		result.reverse();

		return result;
	}

	private static <L, M> void reportLogMove(StochasticTraceAlignmentImpl<L, M> result, L event) {
		result.add(event, null);
	}

	private static <L, M> void reportModelMove(StochasticTraceAlignmentImpl<L, M> result, M transition) {
		result.add(null, transition);
	}

	private static <L, M> void reportSynchronousMove(StochasticTraceAlignmentImpl<L, M> result, L event, M transition) {
		result.add(event, transition);
	}
}