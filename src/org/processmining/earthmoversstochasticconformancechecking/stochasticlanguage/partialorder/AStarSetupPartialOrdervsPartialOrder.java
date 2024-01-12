package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

public class AStarSetupPartialOrdervsPartialOrder {

	public int[] partialOrderA;
	public int[] partialOrderB;
	public int numberOfActivities;

	public boolean isFinal(PartialOrderPartialOrderState state) {
		return PartialOrderUtils.eventsLeft(partialOrderA, state.stateA) == 0 && //
				PartialOrderUtils.eventsLeft(partialOrderB, state.stateB) == 0;
	}

	public int computeHeuristic(PartialOrderPartialOrderState state, int numberOfActivities) {
		int[] activitiesLeft = new int[numberOfActivities];
		{
			for (int eventIndex = 0; eventIndex < PartialOrderUtils.getNumberOfEvents(partialOrderA); eventIndex++) {
				if (!PartialOrderUtils.hasBeenExecuted(partialOrderA, state.stateA, eventIndex)) {
					int activity = PartialOrderUtils.getActivity(partialOrderA, eventIndex);
					activitiesLeft[activity]++;
				}
			}
		}

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