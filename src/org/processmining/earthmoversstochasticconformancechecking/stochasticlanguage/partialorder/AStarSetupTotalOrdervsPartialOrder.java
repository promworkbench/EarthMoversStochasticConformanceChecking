package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

public class AStarSetupTotalOrdervsPartialOrder {

	public int[] totalOrderA;
	public int[] partialOrderB;
	public int numberOfActivities;

	public boolean isFinal(TotalOrderPartialOrderState state) {
		return TotalOrderUtils.eventsLeft(totalOrderA, state.stateA) == 0 && //
				PartialOrderUtils.eventsLeft(partialOrderB, state.stateB) == 0;
	}

	public int computeHeuristic(TotalOrderPartialOrderState state, int numberOfActivities) {
		// Which heuristic (analysis code)
		return computeHeuristic2(state, numberOfActivities);
	}

	public int computeHeuristic1(TotalOrderPartialOrderState state, int numberOfActivities) {
		int[] activitiesLeft = new int[numberOfActivities];
		{
			for (int eventIndex = 0; eventIndex < TotalOrderUtils.getNumberOfEvents(totalOrderA); eventIndex++) {
				if (!TotalOrderUtils.hasBeenExecuted(totalOrderA, state.stateA, eventIndex)) {
					int activity = TotalOrderUtils.getActivity(totalOrderA, eventIndex);
					activitiesLeft[activity]++;
				}
			}
		}

		for (int eventIndex = state.stateB.nextClearBit(0); eventIndex < PartialOrderUtils
				.getNumberOfEvents(partialOrderB); eventIndex = state.stateB.nextClearBit(eventIndex + 1)) {
			int activity = PartialOrderUtils.getActivity(partialOrderB, eventIndex);
			activitiesLeft[activity]--;
		}

		int sum = 0;
		for (int activity = 0; activity < activitiesLeft.length; activity++) {
			sum += Math.abs(activitiesLeft[activity]);
		}

		return (int) Math.ceil(sum / 2.0);
	}

	/**
	 * Heuristic for best-case future LVS given the state. Idea: - All events
	 * with equal label can be matched - We will obtain Max(remaining events in
	 * total order, remaining events in partial order) edit operations of cost 1
	 * (Rename as long as there are unmatchable activities in the shorter on,
	 * and delete/insert the rest)
	 * 
	 * @param state
	 *            Current A* state
	 * @param numberOfActivities
	 *            Total number of activities
	 * @return
	 */
	public int computeHeuristic2(TotalOrderPartialOrderState state, int numberOfActivities) {
		//TODO Both for loops are exactly the same as in heuristic1 (refractor in case we will keep both)
		// Count un-matchable activities
		// Count open activities (labeled events) in the total order (log)
		int[] activitiesLeft = new int[numberOfActivities];
		{
			for (int eventIndex = 0; eventIndex < TotalOrderUtils.getNumberOfEvents(totalOrderA); eventIndex++) {
				if (!TotalOrderUtils.hasBeenExecuted(totalOrderA, state.stateA, eventIndex)) {
					int activity = TotalOrderUtils.getActivity(totalOrderA, eventIndex);
					activitiesLeft[activity]++;
				}
			}
		}

		// Count open activities (labeled events) in the partial order (model)
		// Subtraction already accounts for potential matches (equal label)
		for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrderB); eventIndex++) {
			if (!PartialOrderUtils.hasBeenExecuted(partialOrderB, state.stateB, eventIndex)) {
				int activity = PartialOrderUtils.getActivity(partialOrderB, eventIndex);
				activitiesLeft[activity]--;
			}
		}

		// Count open / additional activities in log and model
		int addLog = 0;
		int addModel = 0;
		int nActLeft;
		for (int activity = 0; activity < activitiesLeft.length; activity++) {
			nActLeft = activitiesLeft[activity];
			if (nActLeft > 0) {
				addLog += nActLeft;
			} else {
				addModel -= nActLeft;
			}
		}

		return Math.max(addLog, addModel);
	}

}