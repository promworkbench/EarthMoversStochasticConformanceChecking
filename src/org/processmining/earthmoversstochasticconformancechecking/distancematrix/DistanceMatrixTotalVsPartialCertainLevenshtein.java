package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import java.util.BitSet;
import java.util.Map;
import java.util.PriorityQueue;

import org.processmining.earthmoversstochasticconformancechecking.datastructures.HashBackedPriorityQueue;
import org.processmining.earthmoversstochasticconformancechecking.datastructures.HashMapLinearProbing;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.Levenshtein;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.SymbolicNumber;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrderCertain;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.AStarSetupTotalOrdervsPartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrder2TotalOrders;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderCountTotalOrders;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.TotalOrderPartialOrderState;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.TotalOrderUtils;
import org.processmining.framework.plugin.ProMCanceller;

public class DistanceMatrixTotalVsPartialCertainLevenshtein
		extends DistanceMatrixAbstract<TotalOrder, PartialOrderCertain> {

	@Override
	protected double getDistance(int[] totalOrderA, int[] partialOrderB, ProMCanceller canceller) {
		return getDistance(totalOrderA, partialOrderB, DistanceMatrixThresholds.best, canceller);
	}

	protected double getDistance(int[] totalOrderA, int[] partialOrderB, DistanceMatrixThresholds thresholds,
			ProMCanceller canceller) {
		//choose a strategy
		if (PartialOrderUtils.isTotalOrder(partialOrderB)) {
			return getDistanceTotalOrder(totalOrderA, partialOrderB, canceller);
		} else {
			SymbolicNumber sizeB = PartialOrderCountTotalOrders.count(partialOrderB,
					thresholds.getMaxNumberOfCountTotalOrdersBaseCase(), canceller);

			if (sizeB.isNumber()
					&& sizeB.bigIntegerValue().compareTo(thresholds.getMaxTotalOrdersToPreferExhaustiveOverAStar()) <= 0
					&& TotalOrderUtils.getNumberOfEvents(totalOrderA) >= thresholds
							.getMinTotalOrderLengthToPreferExhaustiveOverAStar()) {
				//B is small enough to exhaustively tackle
				return getDistanceExhaustive(totalOrderA, partialOrderB, canceller);
			} else {
				//no more strategies apply; A* it is
				return getDistanceAStar(totalOrderA, partialOrderB, thresholds, canceller);
			}
		}
	}

	protected double getDistanceTotalOrder(int[] totalOrderA, int[] partialOrderB, ProMCanceller canceller) {
		int[] totalOrderB = PartialOrderUtils.getATotalOrder(partialOrderB);
		return Levenshtein.getNormalisedDistance(totalOrderA, totalOrderB);
	}

	protected double getDistanceExhaustive(int[] totalOrderA, int[] partialOrderB, ProMCanceller canceller) {
		double min = Double.MAX_VALUE;
		for (int[] totalOrderB : PartialOrder2TotalOrders.getTotalOrders(partialOrderB, canceller)) {
			double d = Levenshtein.getNormalisedDistance(totalOrderA, totalOrderB);

			if (canceller.isCancelled()) {
				return Double.NaN;
			}

			min = Math.min(min, d);
		}
		return min;
	}

	protected double getDistanceAStar(int[] totalOrderA, int[] partialOrderB, DistanceMatrixThresholds thresholds,
			ProMCanceller canceller) {

		AStarSetupTotalOrdervsPartialOrder setup = new AStarSetupTotalOrdervsPartialOrder();
		setup.totalOrderA = totalOrderA;
		setup.partialOrderB = partialOrderB;

		//gather number of activities
		setup.numberOfActivities = 0;
		{
			BitSet activities = new BitSet();
			for (int eventIndexA = 0; eventIndexA < TotalOrderUtils.getNumberOfEvents(totalOrderA); eventIndexA++) {
				activities.set(TotalOrderUtils.getActivity(totalOrderA, eventIndexA));
			}
			for (int eventIndexB = 0; eventIndexB < PartialOrderUtils.getNumberOfEvents(partialOrderB); eventIndexB++) {
				activities.set(PartialOrderUtils.getActivity(partialOrderB, eventIndexB));
			}
			setup.numberOfActivities = activities.length();
		}

		TotalOrderPartialOrderState startState = new TotalOrderPartialOrderState();
		startState.f = 0;
		startState.g = 0;
		startState.depth = 0;
		startState.stateA = 0;
		startState.stateB = new BitSet(PartialOrderUtils.getNumberOfEvents(partialOrderB));
		PartialOrderUtils.reset(startState.stateB);

		// Run A*
		TotalOrderPartialOrderState end = aStar(setup, startState, thresholds);

		if (end == null) {
			//			System.out.println("  == too much states in A*, return " + thresholds.getDefaultValueOnFailure());
			return thresholds.getDefaultValueOnFailure();
		}

		double distance = end.f / (double) Math.max(TotalOrderUtils.getNumberOfEvents(totalOrderA),
				PartialOrderUtils.getNumberOfEvents(partialOrderB));

		return distance;
	}

	public TotalOrderPartialOrderState aStar(AStarSetupTotalOrdervsPartialOrder setup,
			TotalOrderPartialOrderState start, DistanceMatrixThresholds thresholds) {
		Map<TotalOrderPartialOrderState, TotalOrderPartialOrderState> closedStates = new HashMapLinearProbing<>(
				thresholds.getInitListSize());
		HashBackedPriorityQueue<TotalOrderPartialOrderState> openStates = new HashBackedPriorityQueue<>(
				thresholds.getInitListSize(), TotalOrderPartialOrderState.comparator);

		int[] totalOrderA = setup.totalOrderA;
		int[] partialOrderB = setup.partialOrderB;

		start.f = start.g + setup.computeHeuristic(start, setup.numberOfActivities);
		openStates.add(start);

		long stepsTaken = 0;

		while (!openStates.isEmpty() && stepsTaken < thresholds.getFailAfterNumberOfAStarSteps()) {
			// Note that this assumes that state is not reachable from itself
			// Otherwise the processing of the step does not work as expected
			TotalOrderPartialOrderState state = openStates.poll();
			assert state != null;
			int stateA = state.stateA;
			BitSet stateB = state.stateB;

			stepsTaken++;

			if (setup.isFinal(state)) {
				return state;
			}

			//			assert TotalOrderUtils.eventsLeft(totalOrderA, stateA) > 0
			//					|| PartialOrderUtils.eventsLeft(partialOrderB, stateB) > 0;

			if (TotalOrderUtils.eventsLeft(totalOrderA, stateA) > 0) {
				//total order can do a step
				//log move
				{
					TotalOrderPartialOrderState target = state.copy();
					target.depth += 1;
					target.stateA = TotalOrderUtils.takeStep(totalOrderA, stateA);
					processStep(setup, closedStates, openStates, state, target, 1);
				}

				//sync/substitution moves
				if (PartialOrderUtils.eventsLeft(partialOrderB, stateB) > 0) {
					//partial order can do a step

					boolean found = false;
					for (int eventIndexB = 0; eventIndexB < PartialOrderUtils
							.getNumberOfEvents(partialOrderB); eventIndexB++) {
						//add sync or substitution moves
						if (PartialOrderUtils.isEnabled(partialOrderB, eventIndexB, state.stateB)) {
							//this step has not been taken yet

							TotalOrderPartialOrderState target = state.copy();
							target.depth += 1;
							target.stateA = TotalOrderUtils.takeStep(totalOrderA, stateA);
							target.stateB = PartialOrderUtils.takeStep(partialOrderB, eventIndexB, target.stateB);

							processStep(setup, closedStates, openStates, state, target,
									PartialOrderUtils.getActivity(partialOrderB, eventIndexB) == TotalOrderUtils
											.getNext(totalOrderA, stateA) ? 0 : 1);

							found = true;
						}
					}
					assert found;
				}
			}

			//move on partial order trace
			if (PartialOrderUtils.eventsLeft(partialOrderB, stateB) > 0) {
				//partial order can do a step

				boolean found = false;
				for (int eventIndexB = 0; eventIndexB < PartialOrderUtils
						.getNumberOfEvents(partialOrderB); eventIndexB++) {
					//add sync or substitution moves

					if (PartialOrderUtils.isEnabled(partialOrderB, eventIndexB, state.stateB)) {
						//this step has not been taken yet
						TotalOrderPartialOrderState target = state.copy();
						target.depth += 1;
						target.stateB = PartialOrderUtils.takeStep(partialOrderB, eventIndexB, target.stateB);

						processStep(setup, closedStates, openStates, state, target, 1);

						found = true;
					}
				}

				assert found;
			}
			closedStates.put(state, state);
		}

		return null;
	}

	public void processStep(AStarSetupTotalOrdervsPartialOrder setup,
			Map<TotalOrderPartialOrderState, TotalOrderPartialOrderState> closedStates,
			HashBackedPriorityQueue<TotalOrderPartialOrderState> openStates, TotalOrderPartialOrderState source,
			TotalOrderPartialOrderState target, int edgeWeight) {
		int totalWeight = source.g + edgeWeight;
		// Note that target Hash is assumed to be independent from .g and .f and .parent
		boolean alreadyInOpen = openStates.contains(target);
		boolean alreadyInClosed = closedStates.containsKey(target);
		boolean flag;

		target.parent = source;
		target.g = totalWeight;
		target.f = target.g + setup.computeHeuristic(target, setup.numberOfActivities);

		//Test Code
		//		if(alreadyInOpen) {
		//			TotalOrderPartialOrderState open = openStates.get(target);
		//			if(target.stateA != open.stateA || !target.stateB.equals(open.stateB)) {
		//				System.out.println("Hash Problem Open");
		//			}
		//		}
		//		if(alreadyInClosed) {
		//			TotalOrderPartialOrderState closed = closedStates.get(target);
		//			if(target.stateA != closed.stateA || !target.stateB.equals(closed.stateB)) {
		//				System.out.println("Hash Problem Closed:");
		//				System.out.println(closed);
		//				System.out.println(target);
		//				System.out.println(closed.stateB.hashCode());
		//				System.out.println(target.stateB.hashCode());
		//			}
		//		}

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
			TotalOrderPartialOrderState closedState = closedStates.get(target);
			if (target.g < closedState.g) {
				flag = openStates.add(target);
				assert flag;
				closedStates.remove(target);
				//				cReOpened++;
			}
		}
	}

	/**
	 * Helper method that creates a GraphViz .dot string for visualizing the
	 * state space
	 * 
	 * @param openList
	 *            A* open list
	 * @param closedList
	 *            A* closed list
	 * @return GraphViz .dot string
	 */
	public String stateSpace2GraphViz(PriorityQueue<TotalOrderPartialOrderState> openList,
			PriorityQueue<TotalOrderPartialOrderState> closedList) {
		StringBuilder builder = new StringBuilder("digraph G {\n");
		for (TotalOrderPartialOrderState s : openList) {
			builder.append(s.hashCode() + " [label=\"f=");
			builder.append(s.f);
			builder.append(" , g=");
			builder.append(s.g);
			builder.append(" , A=");
			builder.append(s.stateA);
			builder.append(" , B=");
			builder.append(s.stateB);
			builder.append(")");
			builder.append("\"];\n");
		}
		for (TotalOrderPartialOrderState s : closedList) {
			builder.append(s.hashCode() + " [label=\"f=");
			builder.append(s.f);
			builder.append(" , g=");
			builder.append(s.g);
			builder.append(" , A=");
			builder.append(s.stateA);
			builder.append(" , B=");
			builder.append(s.stateB);
			builder.append(")");
			builder.append("\", color=red];\n");
		}
		for (TotalOrderPartialOrderState s : openList) {
			if (s.parent != null) {
				builder.append(s.parent.hashCode() + " -> " + s.hashCode() + ";\n");
			}
		}
		for (TotalOrderPartialOrderState s : closedList) {
			if (s.parent != null) {
				builder.append(s.parent.hashCode() + " -> " + s.hashCode() + ";\n");
			}
		}
		builder.append("}");
		return builder.toString();
	}
}