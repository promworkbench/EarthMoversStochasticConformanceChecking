package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemanticsSimpleWeightsImpl;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeights;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class StochasticNet2StochasticLabelledPetriNet {
	public static StochasticLabelledPetriNet convert(StochasticNet net, Marking initialMarking) {

		TObjectIntMap<Transition> transition2index = new TObjectIntHashMap<>();
		Transition[] index2transition = new Transition[net.getTransitions().size()];
		{
			int i = 0;
			for (Transition transition : net.getTransitions()) {
				index2transition[i] = transition;
				transition2index.put(transition, i);
				i++;
			}
		}

		TObjectIntMap<Place> place2index = new TObjectIntHashMap<>();
		Place[] index2place = new Place[net.getPlaces().size()];
		{
			int i = 0;
			for (Place place : net.getPlaces()) {
				index2place[i] = place;
				place2index.put(place, i);
				i++;
			}
		}

		final AtomicReference<StochasticLabelledPetriNetSemantics> semantics = new AtomicReference<>();
		StochasticLabelledPetriNetSimpleWeights result = null;
		result = new StochasticLabelledPetriNetSimpleWeights() {

			public boolean isTransitionSilent(int transition) {
				return index2transition[transition].isInvisible();
			}

			public int isInInitialMarking(int place) {
				return initialMarking.occurrences(index2place[place]);
			}

			public String getTransitionLabel(int transition) {
				return index2transition[transition].getLabel();
			}

			public int[] getOutputTransitions(int place) {
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net
						.getOutEdges(index2place[place]);

				int[] result = new int[edges.size()];
				int i = 0;
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
					Transition target = (Transition) edge.getTarget();
					result[i] = transition2index.get(target);
					i++;
				}
				return result;
			}

			public int[] getOutputPlaces(int transition) {
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net
						.getOutEdges(index2transition[transition]);

				int[] result = new int[edges.size()];
				int i = 0;
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
					Place target = (Place) edge.getTarget();
					result[i] = place2index.get(target);
					i++;
				}
				return result;
			}

			public int getNumberOfTransitions() {
				return index2transition.length;
			}

			public int getNumberOfPlaces() {
				return index2place.length;
			}

			public int[] getInputTransitions(int place) {
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net
						.getInEdges(index2place[place]);

				int[] result = new int[edges.size()];
				int i = 0;
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
					Transition source = (Transition) edge.getSource();
					result[i] = transition2index.get(source);
					i++;
				}
				return result;
			}

			public int[] getInputPlaces(int transition) {
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net
						.getInEdges(index2transition[transition]);

				int[] result = new int[edges.size()];
				int i = 0;
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
					Place source = (Place) edge.getSource();
					result[i] = place2index.get(source);
					i++;
				}
				return result;
			}

			public double getTransitionWeight(int transition) {
				return ((TimedTransition) index2transition[transition]).getWeight();
			}

			public StochasticLabelledPetriNetSemantics getDefaultSemantics() {
				return semantics.get();
			}
		};

		semantics.set(new StochasticLabelledPetriNetSemanticsSimpleWeightsImpl(result));
		return result;
	}
}