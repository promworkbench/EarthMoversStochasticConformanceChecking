package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;

public class StochasticTransition2IndexKey {

	private final StochasticLabelledPetriNetSemantics semantics;
	private final int[] transition2activity;
	private final Activity2IndexKey activityKey;

	/**
	 * 
	 * @param semantics
	 * @param activityKey
	 *            May be reused between runs and logs/models.
	 */
	public StochasticTransition2IndexKey(StochasticLabelledPetriNetSemantics semantics, Activity2IndexKey activityKey) {
		this.activityKey = activityKey;
		this.semantics = semantics;
		transition2activity = new int[semantics.getNumberOfTransitions()];

		for (int transitionIndex = 0; transitionIndex < semantics.getNumberOfTransitions(); transitionIndex++) {
			if (semantics.isTransitionSilent(transitionIndex)) {
				//invisible transition
				transition2activity[transitionIndex] = -1;
			} else {
				//visible transition
				int activityIndex = activityKey.feed(semantics.getTransitionLabel(transitionIndex));
				transition2activity[transitionIndex] = activityIndex;
			}
		}
		activityKey.reIndex();
	}

	/**
	 * Transform a path into a trace by removing all silent transitions and
	 * replacing visible transitions with their activities.
	 * 
	 * @param path
	 * @return
	 */
	public int[] path2trace(int[] path) {
		int[] result = new int[path.length];

		int nextFreeIndex = 0;

		for (int transitionIndex : path) {
			if (transition2activity[transitionIndex] != -1) {
				result[nextFreeIndex] = transition2activity[transitionIndex];
				nextFreeIndex++;
			}
		}

		//copy to tight array
		int[] result2 = new int[nextFreeIndex];
		System.arraycopy(result, 0, result2, 0, nextFreeIndex);
		return result2;
	}

	public int transition2activityIndex(int transitionIndex) {
		return transition2activity[transitionIndex];
	}

	public StochasticPath getStochasticPath(int[] path) {
		StochasticPathImpl result = new StochasticPathImpl();
		for (int transitionIndex : path) {
			final int transitionIndex2 = transitionIndex;
			final int activityIndex = transition2activityIndex(transitionIndex);
			if (activityIndex < 0) {
				result.add(new StochasticTransition() {
					public boolean isInvisible() {
						return true;
					}

					public String getLabel() {
						return "tau";
					}

					public int getIndex() {
						return transitionIndex2;
					}
				});
			} else {
				result.add(new StochasticTransition() {

					public boolean isInvisible() {
						return false;
					}

					public String getLabel() {
						return activityKey.toString(activityIndex);
					}

					public int getIndex() {
						return transitionIndex2;
					}

				});
			}
		}
		return result;
	}

	public StochasticLabelledPetriNetSemantics getSemantics() {
		return semantics;
	}
}