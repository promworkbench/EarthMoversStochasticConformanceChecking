package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import java.util.BitSet;
import java.util.Map;

import org.processmining.earthmoversstochasticconformancechecking.datastructures.HashBackedPriorityQueue;
import org.processmining.earthmoversstochasticconformancechecking.datastructures.HashMapLinearProbing;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.Levenshtein;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.SymbolicNumber;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderCertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.AStarSetupPartialOrdervsPartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrder2TotalOrders;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderCountTotalOrders;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderPartialOrderState;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderUtils;
import org.processmining.framework.plugin.ProMCanceller;

public class DistanceMatrixPartialCertainVsPartialCertainLevenshtein
		extends DistanceMatrixAbstract<PartialOrderCertain, PartialOrderCertain> {

	private DistanceMatrixTotalVsPartialCertainLevenshtein shadowTotalCertain = new DistanceMatrixTotalVsPartialCertainLevenshtein();

	protected double getDistance(int[] partialOrderA, int[] partialOrderB, ProMCanceller canceller) {
		return getDistance(partialOrderA, partialOrderB, DistanceMatrixThresholds.best, canceller);
	}

	protected double getDistance(int[] partialOrderA, int[] partialOrderB, DistanceMatrixThresholds thresholds,
			ProMCanceller canceller) {
		boolean tA = PartialOrderUtils.isTotalOrder(partialOrderA);
		boolean tB = PartialOrderUtils.isTotalOrder(partialOrderB);

		if (tA && tB) {
			//both total orders
			int[] totalOrderA = PartialOrderUtils.getATotalOrder(partialOrderA);
			int[] totalOrderB = PartialOrderUtils.getATotalOrder(partialOrderB);
			return Levenshtein.getNormalisedDistance(totalOrderA, totalOrderB);
		} else if (tA) {
			//A total order
			int[] totalOrderA = PartialOrderUtils.getATotalOrder(partialOrderA);
			return shadowTotalCertain.getDistanceAStar(totalOrderA, partialOrderB, thresholds, canceller);
		} else if (tB) {
			//B total order
			int[] totalOrderB = PartialOrderUtils.getATotalOrder(partialOrderB);
			return shadowTotalCertain.getDistanceAStar(totalOrderB, partialOrderA, thresholds, canceller);
		} else {
			SymbolicNumber sizeA = PartialOrderCountTotalOrders.count(partialOrderA, 5, canceller);
			SymbolicNumber sizeB = PartialOrderCountTotalOrders.count(partialOrderB, 5, canceller);

			if (sizeA.isNumber() && sizeB.isNumber() && sizeA.bigIntegerValue().compareTo(sizeB.bigIntegerValue()) <= 0
					&& sizeA.bigIntegerValue()
							.compareTo(thresholds.getMaxTotalOrdersToPreferExhaustiveOverAStar()) <= 0) {
				//A < B, and small enough to exhaustively tackle
				return getDistanceExhaustiveFirst(partialOrderA, partialOrderB, thresholds, canceller);
			} else if (sizeA.isNumber() && sizeB.isNumber()
					&& sizeB.bigIntegerValue().compareTo(sizeA.bigIntegerValue()) <= 0 && sizeB.bigIntegerValue()
							.compareTo(thresholds.getMaxTotalOrdersToPreferExhaustiveOverAStar()) <= 0) {
				//B < A, and small enough to exhaustively tackle
				return getDistanceExhaustiveFirst(partialOrderB, partialOrderA, thresholds, canceller);
			} else {
				//no more strategies apply; A* it is
				return getDistanceAStar(partialOrderA, partialOrderB, thresholds, canceller);
			}
		}
	}

	/**
	 * Compute the distance by enumerating all total orders of the first partial
	 * order exhaustively, and taking the minimum.
	 * 
	 * @param partialOrderA
	 * @param partialOrderB
	 * @param canceller
	 * @return
	 */
	private double getDistanceExhaustiveFirst(int[] partialOrderA, int[] partialOrderB,
			DistanceMatrixThresholds thresholds, ProMCanceller canceller) {
		double min = Double.MAX_VALUE;
		for (int[] totalOrderA : PartialOrder2TotalOrders.getTotalOrders(partialOrderA, canceller)) {
			min = Math.min(min, shadowTotalCertain.getDistanceAStar(totalOrderA, partialOrderB, thresholds, canceller));
		}
		return min;
	}

	protected double getDistanceAStar(int[] partialOrderA, int[] partialOrderB, DistanceMatrixThresholds thresholds,
			ProMCanceller canceller) {

		AStarSetupPartialOrdervsPartialOrder setup = new AStarSetupPartialOrdervsPartialOrder();
		setup.partialOrderA = partialOrderA;
		setup.partialOrderB = partialOrderB;

		//gather number of activities
		setup.numberOfActivities = 0;
		{
			BitSet activities = new BitSet();
			for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrderA); eventIndex++) {
				activities.set(PartialOrderUtils.getActivity(partialOrderA, eventIndex));
			}
			for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrderB); eventIndex++) {
				activities.set(PartialOrderUtils.getActivity(partialOrderB, eventIndex));
			}
			setup.numberOfActivities = activities.length();
		}

		PartialOrderPartialOrderState startState = new PartialOrderPartialOrderState();
		startState.f = 0;
		startState.g = 0;
		startState.stateA = new BitSet(PartialOrderUtils.getNumberOfEvents(partialOrderA));
		startState.stateB = new BitSet(PartialOrderUtils.getNumberOfEvents(partialOrderB));
		PartialOrderUtils.reset(startState.stateB);

		PartialOrderPartialOrderState end = aStar(setup, startState, thresholds);

		if (end == null) {
//			System.out.println("  == too much states in A*, return " + thresholds.getDefaultValueOnFailure());
			return thresholds.getDefaultValueOnFailure();
		}

		double distance = end.f / (double) Math.max(PartialOrderUtils.getNumberOfEvents(partialOrderA),
				PartialOrderUtils.getNumberOfEvents(partialOrderB));

		return distance;
	}

	public PartialOrderPartialOrderState aStar(AStarSetupPartialOrdervsPartialOrder setup,
			PartialOrderPartialOrderState start, DistanceMatrixThresholds thresholds) {
		Map<PartialOrderPartialOrderState, PartialOrderPartialOrderState> closedStates = new HashMapLinearProbing<>(
				thresholds.getInitListSize());
		HashBackedPriorityQueue<PartialOrderPartialOrderState> openStates = new HashBackedPriorityQueue<>(
				thresholds.getInitListSize(), PartialOrderPartialOrderState.comparator);

		int[] partialOrderA = setup.partialOrderA;
		int[] partialOrderB = setup.partialOrderB;

		start.f = start.g + setup.computeHeuristic(start, setup.numberOfActivities);
		openStates.add(start);

		long stepsTaken = 0;

		while (!openStates.isEmpty() && stepsTaken < thresholds.getFailAfterNumberOfAStarSteps()) {
			// Note that this assumes that state is not reachable from itself
			// Otherwise the processing of the step does not work as expected
			PartialOrderPartialOrderState state = openStates.poll();
			BitSet stateA = state.stateA;
			BitSet stateB = state.stateB;

			stepsTaken++;

			if (setup.isFinal(state)) {
				return state;
			}

			//move on both
			if (PartialOrderUtils.eventsLeft(partialOrderA, stateA) > 0
					&& PartialOrderUtils.eventsLeft(partialOrderB, stateB) > 0) {

				//both can do a step
				for (int eventIndexA = 0; eventIndexA < PartialOrderUtils
						.getNumberOfEvents(partialOrderA); eventIndexA++) {
					if (!stateA.get(eventIndexA)) {

						for (int eventIndexB = 0; eventIndexB < PartialOrderUtils
								.getNumberOfEvents(partialOrderB); eventIndexB++) {
							if (!stateB.get(eventIndexB)) {

								PartialOrderPartialOrderState target = state.clone();
								target.depth += 1;
								PartialOrderUtils.takeStep(partialOrderA, eventIndexA, target.stateA);
								PartialOrderUtils.takeStep(partialOrderB, eventIndexB, target.stateB);

								processStep(setup, closedStates, openStates, state, target,
										PartialOrderUtils.getActivity(partialOrderB, eventIndexB) == PartialOrderUtils
												.getActivity(partialOrderA, eventIndexA) ? 0 : 1);
							}
						}
					}
				}
			}

			//move on partial order trace A only 
			if (PartialOrderUtils.eventsLeft(partialOrderA, stateA) != 0) {
				//partial order A can do a step

				for (int eventIndex = 0; eventIndex < PartialOrderUtils
						.getNumberOfEvents(partialOrderA); eventIndex++) {
					//add sync or substitution moves

					if (!stateA.get(eventIndex)
							&& PartialOrderUtils.isEnabled(partialOrderA, eventIndex, state.stateA)) {
						//this step has not been taken yet
						PartialOrderPartialOrderState target = state.clone();
						target.depth += 1;
						PartialOrderUtils.takeStep(partialOrderA, eventIndex, target.stateA);

						processStep(setup, closedStates, openStates, state, target, 1);
					}
				}
			}

			//move on partial order trace B only 
			if (PartialOrderUtils.eventsLeft(partialOrderB, stateB) != 0) {
				//partial order B can do a step

				for (int eventIndex = 0; eventIndex < PartialOrderUtils
						.getNumberOfEvents(partialOrderB); eventIndex++) {
					//add sync or substitution moves

					if (!stateB.get(eventIndex)
							&& PartialOrderUtils.isEnabled(partialOrderB, eventIndex, state.stateB)) {
						//this step has not been taken yet
						PartialOrderPartialOrderState target = state.clone();
						target.depth += 1;
						PartialOrderUtils.takeStep(partialOrderB, eventIndex, target.stateB);

						processStep(setup, closedStates, openStates, state, target, 1);
					}
				}
			}
			closedStates.put(state, state);
		}

		return null;
	}

	public void processStep(AStarSetupPartialOrdervsPartialOrder setup,
			Map<PartialOrderPartialOrderState, PartialOrderPartialOrderState> closedStates,
			HashBackedPriorityQueue<PartialOrderPartialOrderState> openStates, PartialOrderPartialOrderState source,
			PartialOrderPartialOrderState target, int edgeWeight) {
		int totalWeight = source.g + edgeWeight;
		// Note that target Hash is assumed to be independent from .g and .f and .parent
		boolean alreadyInOpen = openStates.contains(target);
		boolean alreadyInClosed = closedStates.containsKey(target);
		boolean flag;

		target.parent = source;
		target.g = totalWeight;
		target.f = target.g + setup.computeHeuristic(target, setup.numberOfActivities);

		if (!alreadyInOpen && !alreadyInClosed) {
			flag = openStates.add(target);
			//			cNewStates++; 
			assert flag;
		} else if (alreadyInOpen) {
			//			cReEncounteredStatesOpen++;
			flag = openStates.addOrUpdateIfBetter(target);
			//			if (flag) {
			//				cReEncounteredBetterStatesOpen++;
			//			}
		} else if (alreadyInClosed) {
			//			cReEncounteredStatesClosed++;
			PartialOrderPartialOrderState closedState = closedStates.get(target);
			if (target.g < closedState.g) {
				flag = openStates.add(target);
				assert flag;
				closedStates.remove(target);
				//				cReOpened++;
			}
		}
	}

}