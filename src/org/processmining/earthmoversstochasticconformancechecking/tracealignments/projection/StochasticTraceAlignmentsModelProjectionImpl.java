package org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection;

import java.util.Arrays;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignment;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel.EditIteratorLogModel;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;

public class StochasticTraceAlignmentsModelProjectionImpl implements StochasticTraceAlignmentsModelProjection {

	private final TIntDoubleMap transition2sync = new TIntDoubleHashMap(10, 0.5f, -1, -1);

	public StochasticTraceAlignmentsModelProjectionImpl(StochasticTraceAlignmentsLogModel alignments) {

		StochasticLabelledPetriNet model = alignments.getStochasticNet();

		int[] movesOnTransition = new int[model.getNumberOfTransitions()];
		int[] syncMovesOnTransition = new int[model.getNumberOfTransitions()];

		double[] sumMassSynchronous = new double[model.getNumberOfTransitions()];
		double[] sumMassPotential = new double[model.getNumberOfTransitions()];

		for (EditIteratorLogModel it = alignments.iterator(); it.hasNext();) {
			StochasticTraceAlignment<String, StochasticTransition> alignment = it.next();

			double traceAlignmentProbability = it.getProbability();

			//count the number of moves on each transition
			{
				Arrays.fill(movesOnTransition, 0);
				Arrays.fill(syncMovesOnTransition, 0);
				for (int move = 0; move < alignment.getNumberOfMoves(); move++) {
					StochasticTransition modelMove = alignment.getMoveB(move);
					String logMove = alignment.getMoveA(move);
					if (modelMove != null) {
						//this move has a model component
						movesOnTransition[modelMove.getIndex()]++;

						if (logMove != null || modelMove.isInvisible()) {
							//this is a synchronous move
							syncMovesOnTransition[modelMove.getIndex()]++;
						}
					}
				}
			}

			//update the global counters
			for (int transition = 0; transition < model.getNumberOfTransitions(); transition++) {
				if (movesOnTransition[transition] > 0) {
					//there were moves on this transition in this trace alignment, so count it
					sumMassSynchronous[transition] += traceAlignmentProbability
							* (syncMovesOnTransition[transition] / (movesOnTransition[transition] * 1.0));

					sumMassPotential[transition] += traceAlignmentProbability;
				}
			}
		}

		//wrap up the computation for each retrieval
		for (int transition = 0; transition < model.getNumberOfTransitions(); transition++) {
			double syncProbability;
			if (sumMassPotential[transition] > 0) {
				syncProbability = sumMassSynchronous[transition] / sumMassPotential[transition];
			} else {
				syncProbability = 1;
			}

			transition2sync.put(transition, syncProbability);
		}
	}

	public double getTransitionSyncLikelihood(int transition) {
		return transition2sync.get(transition);
	}

}
