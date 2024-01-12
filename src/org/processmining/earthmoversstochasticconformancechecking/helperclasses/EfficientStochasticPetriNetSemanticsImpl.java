package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import java.util.Arrays;
import java.util.Collection;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * Adapted from EfficientPetrinetSemanticsImpl by Felix Mannhardt and Bas van
 * Zelst.
 * 
 * @author sander
 *
 */
public class EfficientStochasticPetriNetSemanticsImpl implements EfficientStochasticPetriNetSemantics {

	private final TObjectIntMap<Transition> transitionMap;
	private final Transition[] orderedTransitions;
	private final TObjectIntMap<Place> placeMap;
	private final Place[] orderedPlaces;

	private final byte[][] consuming;
	private final byte[][] producing;
	private final byte[][] effect;
	private final double[] weights;
	private final int[] priorities;
	private final boolean[] invisible;
	private final String[] labels;

	private byte[] state;

	/**
	 * Creates the semantics for the supplied {@link StochasticNet} and initial
	 * {@link Marking}. This semantics class treat all {@link StochasticNet}s as
	 * a {@link StochasticNet} (so does not honor the semantics of reset or
	 * inhibitor nets).
	 * 
	 * @param net
	 *            with the graph structure
	 * @param initialMarking
	 */
	public EfficientStochasticPetriNetSemanticsImpl(StochasticNet net, Marking initialMarking) {
		Collection<Place> places = net.getPlaces();
		placeMap = new TObjectIntHashMap<>(places.size(), 0.5f, -1);
		orderedPlaces = new Place[places.size()];
		state = new byte[places.size()];

		int currentPlaceIndex = 0;
		for (Place p : places) {
			placeMap.put(p, currentPlaceIndex);
			orderedPlaces[currentPlaceIndex] = p;
			Integer tokens = initialMarking.occurrences(p);
			state[currentPlaceIndex] = tokens.byteValue();
			currentPlaceIndex++;
		}

		Collection<Transition> transitions = net.getTransitions();
		orderedTransitions = new Transition[transitions.size()];
		transitionMap = new TObjectIntHashMap<>(transitions.size(), 0.5f, -1);
		consuming = new byte[transitions.size()][];
		producing = new byte[transitions.size()][];
		effect = new byte[transitions.size()][];
		weights = new double[transitions.size()];
		priorities = new int[transitions.size()];
		invisible = new boolean[transitions.size()];
		labels = new String[transitions.size()];

		int currentTransitionIndex = 0;
		for (Transition t : transitions) {
			transitionMap.put(t, currentTransitionIndex);
			orderedTransitions[currentTransitionIndex] = t;
			weights[currentTransitionIndex] = ((TimedTransition) t).getWeight();
			priorities[currentTransitionIndex] = ((TimedTransition) t).getPriority();
			invisible[currentTransitionIndex] = t.isInvisible();
			labels[currentTransitionIndex] = t.getLabel();

			effect[currentTransitionIndex] = new byte[places.size()];

			consuming[currentTransitionIndex] = new byte[places.size()];
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdges = net.getInEdges(t);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inEdges) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;

					PetrinetNode sourceNode = edge.getSource();
					Place sourcePlace = (Place) sourceNode;
					int placeIndex = placeMap.get(sourcePlace);
					// consumes n tokens from this place 
					consuming[currentTransitionIndex][placeIndex] = (byte) arc.getWeight();
					effect[currentTransitionIndex][placeIndex] -= (byte) arc.getWeight();
				}
			}

			producing[currentTransitionIndex] = new byte[places.size()];
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdges = net.getOutEdges(t);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outEdges) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;

					PetrinetNode targetNode = edge.getTarget();
					Place targetPlace = (Place) targetNode;
					int placeIndex = placeMap.get(targetPlace);
					// produces n tokens to this place 
					producing[currentTransitionIndex][placeIndex] = (byte) arc.getWeight();
					effect[currentTransitionIndex][placeIndex] += (byte) arc.getWeight();
				}
			}

			currentTransitionIndex++;
		}
	}

	@Override
	public void executeTransition(int transitionIndex) {
		byte[] effectOnTokens = effect[transitionIndex];
		for (int i = 0; i < effectOnTokens.length; i++) {
			state[i] = (byte) (state[i] + effectOnTokens[i]);
		}
	}

	@Override
	public int[] getEnabledTransitions() {
		int[] enabled = new int[priorities.length];
		Arrays.fill(enabled, -1);

		//enabled by tokens and weight
		int countEnabledTokens = 0;
		for (int transitionIndex = 0; transitionIndex < priorities.length; transitionIndex++) {
			if (isEnabledTokens(transitionIndex) && weights[transitionIndex] > 0) {
				enabled[countEnabledTokens] = transitionIndex;
				countEnabledTokens++;
			}
		}

		//find maximum priority
		int maxPriority = Integer.MIN_VALUE;
		for (int transitionIndex : enabled) {
			if (transitionIndex >= 0) {
				maxPriority = Math.max(maxPriority, priorities[transitionIndex]);
			} else {
				break;
			}
		}

		//disable non-highest priorities
		int[] enabled2 = new int[countEnabledTokens];

		int countMax = 0;
		for (int transitionIndex : enabled) {
			if (transitionIndex < 0) {
				break;
			}
			if (maxPriority <= priorities[transitionIndex]) {
				enabled2[countMax] = transitionIndex;
				countMax++;
			}
		}

		int[] enabled3 = new int[countMax];
		System.arraycopy(enabled2, 0, enabled3, 0, countMax);
		return enabled3;
	}

	@Override
	public byte[] getState() {
		return state.clone();
	}

	@Override
	public void setState(byte[] marking) {
		System.arraycopy(marking, 0, this.state, 0, marking.length);
	}

	private boolean isEnabledTokens(final int transitionIndex) {
		byte[] neededTokens = consuming[transitionIndex];
		for (int i = 0; i < neededTokens.length; i++) {
			byte tokens = neededTokens[i];
			if ((tokens > 0) && (state[i] < tokens)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public byte[] convert(Marking marking) {
		byte[] tokens = new byte[orderedPlaces.length];
		for (Place p : marking) { //iterator is multiset aware!
			tokens[placeMap.get(p)]++;
		}
		return tokens;
	}

	@Override
	public Marking convert(byte[] marking) {
		Marking obj = new Marking();
		for (int i = 0; i < marking.length; i++) {
			int tokens = marking[i];
			if (tokens > 0) {
				obj.add(orderedPlaces[i], tokens);
			}
		}
		return obj;
	}

	@Override
	public double getTransitionWeight(int transitionIndex) {
		return weights[transitionIndex];
	}

	@Override
	public boolean isInvisible(int transitionIndex) {
		return invisible[transitionIndex];
	}

	@Override
	public String getLabel(int transitionIndex) {
		return labels[transitionIndex];
	}

	@Override
	public EfficientStochasticPetriNetSemanticsImpl clone() {
		EfficientStochasticPetriNetSemanticsImpl result;
		try {
			result = (EfficientStochasticPetriNetSemanticsImpl) super.clone();

			//only the state requires a deep copy: all the other fields cannot be changed after construction
			result.state = state.clone();

			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TObjectIntMap<Transition> getTransitionMap() {
		return transitionMap;
	}
}
