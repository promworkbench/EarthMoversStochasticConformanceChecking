package org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.TotalOrderUtils;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignment;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignments;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignments.StochasticTraceAlignmentIterator;

public class StochasticTraceAlignmentsLogProjectionImpl implements StochasticTraceAlignmentsLogProjection {

	private StochasticLanguage<TotalOrder> language;
	private double[] traceProbabilities;
	private double[][] eventSyncLikelihoods;

	public static enum Which {
		A, B;
	}

	public StochasticTraceAlignmentsLogProjectionImpl(StochasticTraceAlignments<?, ?> alignments, Which which) {
		if (which == Which.A) {
			language = alignments.getLanguageA();
		} else {
			language = alignments.getLanguageB();
		}

		//initialise
		double[][] sumSyncProbability = new double[language.size()][];
		{
			int traceIndex = 0;
			for (StochasticTraceIterator<?> it = language.iterator(); it.hasNext();) {
				it.next();
				sumSyncProbability[traceIndex] = new double[language.getTrace(it.getTraceIndex()).length];
				traceIndex++;
			}

			traceProbabilities = new double[language.size()];
		}

		//gather
		for (StochasticTraceAlignmentIterator<?, ?> it = alignments.iterator(); it.hasNext();) {
			StochasticTraceAlignment<?, ?> alignment = it.next();
			double probability = it.getProbability();

			int traceIndex;
			if (which == Which.A) {
				traceIndex = it.getTraceAIndex();
			} else {
				traceIndex = it.getTraceBIndex();
			}

			traceProbabilities[traceIndex] += probability;

			processAlignment(traceIndex, which, sumSyncProbability, alignment, probability);
		}

		//wrap up
		{
			eventSyncLikelihoods = new double[language.size()][];

			int traceIndex = 0;
			for (StochasticTraceIterator<?> it = language.iterator(); it.hasNext();) {
				it.next();
				int length = language.getTrace(it.getTraceIndex()).length;
				eventSyncLikelihoods[traceIndex] = new double[length];

				//divide the sums of synchronous probability by the total sum for this trace
				for (int logMove = 0; logMove < length; logMove++) {
					eventSyncLikelihoods[traceIndex][logMove] = sumSyncProbability[traceIndex][logMove]
							/ traceProbabilities[traceIndex];
				}

				traceIndex++;
			}
		}
	}

	private static void processAlignment(int traceIndex, Which which, double[][] sumSyncProbability,
			StochasticTraceAlignment<?, ?> alignment, double probability) {

		//walk through the trace and administer the log moves
		int traceMove = 0;
		for (int move = 0; move < alignment.getNumberOfMoves(); move++) {
			if ((which == Which.A && alignment.getMoveA(move) != null)
					|| (which == Which.B && alignment.getMoveB(move) != null)) {
				//this is a log or synchronous move

				if ((which == Which.A && alignment.getMoveB(move) != null)
						|| (which == Which.B && alignment.getMoveA(move) != null)) {
					//this is a synchronous move, count the probability with the sum for this log step
					sumSyncProbability[traceIndex][traceMove] += probability;
				} else {
					//this is a log move, and thus not counted
				}

				traceMove++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.earthmoversstochasticconformancechecking.
	 * stochastictracealignment.StochasticTraceAlignmentsLogProjectionI#
	 * getNumberOfLogtraces()
	 */
	@Override
	public int getNumberOfLogtraces() {
		return language.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.earthmoversstochasticconformancechecking.
	 * stochastictracealignment.StochasticTraceAlignmentsLogProjectionI#
	 * getTraceProbability(int)
	 */
	@Override
	public double getTraceProbability(int logTrace) {
		return traceProbabilities[logTrace];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.earthmoversstochasticconformancechecking.
	 * stochastictracealignment.StochasticTraceAlignmentsLogProjectionI#getTrace
	 * (int)
	 */
	@Override
	public String[] getTrace(int traceIndex) {
		return TotalOrderUtils.getStringTrace(language, traceIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.earthmoversstochasticconformancechecking.
	 * stochastictracealignment.StochasticTraceAlignmentsLogProjectionI#
	 * getEventSyncLikelihoods(int)
	 */
	@Override
	public double[] getEventSyncLikelihoods(int logTrace) {
		return eventSyncLikelihoods[logTrace];
	}

}