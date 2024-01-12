package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class Activity2IndexKey {
	private String[] index2activity;
	private TObjectIntMap<String> activity2index;

	/**
	 * Creates an activity key. Do not forget to feed the logs/models to the key
	 * using feed().
	 */
	public Activity2IndexKey() {
		index2activity = new String[0];
		activity2index = new TObjectIntHashMap<String>(10, 0.5f, -1);
	}

	public void feed(XLog log, XEventClassifier classifier) {
		int max = index2activity.length - 1;
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				String activity = classifier.getClassIdentity(event);
				if (activity2index.putIfAbsent(activity, max + 1) == activity2index.getNoEntryValue()) {
					max++;
				}
			}
		}

		reIndex();
	}

	public void feed(StochasticLabelledPetriNet net) {
		int max = index2activity.length - 1;
		for (int transition = 0; transition < net.getNumberOfTransitions(); transition++) {
			if (!net.isTransitionSilent(transition)) {
				String activity = net.getTransitionLabel(transition);
				if (activity2index.putIfAbsent(activity, max + 1) == activity2index.getNoEntryValue()) {
					max++;
				}
			}
		}

		reIndex();
	}

	/**
	 * After calling a bunch of these, call reindex().
	 * 
	 * @param transition
	 * @return
	 */
	public int feed(Transition transition) {
		String activity = transition.getLabel();
		return feed(activity);
	}

	public int feed(String activity) {
		int max = index2activity.length - 1;
		int previous = activity2index.putIfAbsent(activity, max + 1);
		if (previous == activity2index.getNoEntryValue()) {
			max++;
			return max - 1;
		} else {
			return previous;
		}
	}

	public void reIndex() {
		index2activity = new String[activity2index.size()];
		for (TObjectIntIterator<String> it = activity2index.iterator(); it.hasNext();) {
			it.advance();
			index2activity[it.value()] = it.key();
		}
	}

	public int toIndex(String activity) {
		return activity2index.get(activity);
	}

	public String[] toTraceString(int[] indexTrace) {
		String[] result = new String[indexTrace.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = index2activity[indexTrace[i]];
		}
		return result;
	}

	public String toString(int activityIndex) {
		return index2activity[activityIndex];
	}
}