package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.processmining.earthmoversstochasticconformancechecking.helperclasses.MaximalIndependentSets;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.StochasticPetriNetUtils;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.StochasticPetriNetUtils.PetriNetCache;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.LanguageGenerationStrategyFromModelPartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPetriNet2StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition2IndexKey;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;

import cern.colt.Arrays;

/**
 * Assumption: the net is stochastically sound and safe.
 * 
 * @author sander
 *
 */
public class StochasticPetrinet2StochasticLanguagePartialOrder {

	public static StochasticPathLanguage<PartialOrder> convert(StochasticLabelledPetriNet net,
			Activity2IndexKey activityKey, LanguageGenerationStrategyFromModelPartialOrder generationStrategy,
			ProMCanceller canceller) {
		StochasticLabelledPetriNetSemantics semantics = net.getDefaultSemantics();
		StochasticTransition2IndexKey transitionKey = new StochasticTransition2IndexKey(semantics, activityKey);
		StochasticPathLanguagePartialOrderImpl language = new StochasticPathLanguagePartialOrderImpl(transitionKey,
				activityKey);

		//initialise queue
		PriorityQueue<Item> queue = new PriorityQueue<>();
		{
			Item item = new Item();
			item.marking = semantics.getState();
			item.prefix = PartialOrderUtils.emptyPartialOrder();
			item.probability = 1;
			queue.add(item);
		}

		PetriNetCache neighbourCache = new PetriNetCache();

		//stage 1: add the most likely paths
		mostLikelyPaths(net, generationStrategy.getNumberOfTracesWithHighestProbability(), semantics, language, queue,
				neighbourCache);

		//stage 2: random walk
		randomWalk(queue, generationStrategy.getNumberOfTracesRandomWalk(), generationStrategy.getSeed(), semantics,
				net, language, neighbourCache);

		return language;
	}

	private static class Item implements Comparable<Item> {
		byte[] marking;
		int[] prefix;
		double probability;

		public int compareTo(Item other) {
			return Double.compare(other.probability, probability); //priority queues favour low numbers
		}

		public String toString() {
			return probability + ", " + Arrays.toString(prefix);
		}
	}

	private static void mostLikelyPaths(StochasticLabelledPetriNet net, int numberOfTraces,
			StochasticLabelledPetriNetSemantics semantics, StochasticPathLanguagePartialOrderImpl language,
			PriorityQueue<Item> queue, PetriNetCache neighbourCache) {
		while (!queue.isEmpty() && numberOfTraces > 0) {
			Item item = queue.poll();

			semantics.setState(item.marking);
			BitSet enabledTransitions = semantics.getEnabledTransitions();
			double totalTransitionsMass = semantics.getTotalWeightOfEnabledTransitions();

			if (semantics.isFinalState()) {
				//We are in a deadlock state. By definition, that's a final state.
				//Accept the current trace and add it to the language
				language.add(prefix2path(semantics, item.prefix), item.probability);
				numberOfTraces--;
			} else {
				//we can still take steps; add independent transitions to the prefix and continue
				Collection<int[]> As = MaximalIndependentSets.BronKerbosch2(net, enabledTransitions, neighbourCache);
				for (int[] A : As) {
					//create a new priority queue item to recurse on
					Item newItem = createChildItem(net, semantics, item, totalTransitionsMass, A, enabledTransitions,
							neighbourCache);

					queue.add(newItem);
				}

			}
		}
	}

	private static Item createChildItem(StochasticLabelledPetriNet net, StochasticLabelledPetriNetSemantics semantics,
			Item item, double totalTransitionsMass, int[] A, BitSet enabledTransitions, PetriNetCache cache) {
		Item newItem = new Item();

		//marking
		{
			semantics.setState(item.marking);
			for (int transition : A) {
				semantics.executeTransition(transition);
			}
			newItem.marking = semantics.getState();
		}

		//probability
		{
			newItem.probability = item.probability * getProbability(A, enabledTransitions, net, semantics, cache);
		}

		//prefix
		{
			//add a connection for each dependent predecessor of each transition
			newItem.prefix = item.prefix;
			for (int transition : A) {
				//add event
				int eventIndex = PartialOrderUtils.getNumberOfEvents(newItem.prefix);
				newItem.prefix = PartialOrderUtils.addEvent(newItem.prefix, transition);

				for (int eventIndexP = 0; eventIndexP < eventIndex; eventIndexP++) {
					int transitionP = PartialOrderUtils.getActivity(newItem.prefix, eventIndexP);
					if (StochasticPetriNetUtils.isDependent(net, transitionP, transition, cache)) {
						newItem.prefix = PartialOrderUtils.addEdgeToLastAddedEvent(newItem.prefix, eventIndexP);
					}
				}
			}
		}
		return newItem;
	}

	private static class StateSpace {
		ArrayList<Item> roots;
		BigDecimal sumWeightLeft;
	}

	/**
	 * Idea: keep a frontier of Items. For each step in the random walk, remove
	 * a node from the frontier and add all of its children to the frontier.
	 * Select a weighted random child and repeat.
	 * 
	 * @param queue
	 * @param numberOfTraces
	 * @param seed
	 * @param semantics
	 * @param net
	 * @param transitionMap
	 * @param language
	 * @param neighbourCache
	 */
	private static void randomWalk(Collection<Item> queue, int numberOfTraces, long seed,
			StochasticLabelledPetriNetSemantics semantics, StochasticLabelledPetriNet net,
			StochasticPathLanguagePartialOrderImpl language, PetriNetCache neighbourCache) {
		//initialise state space
		StateSpace stateSpace = new StateSpace();
		{
			stateSpace.roots = new ArrayList<>(queue);
			stateSpace.sumWeightLeft = new BigDecimal(weightNodes(stateSpace.roots));
		}

		Random random = new Random(seed);

		for (int i = 0; i < numberOfTraces; i++) {

			//			if (i % 100 == 0) {
			//				System.out.println("  random trace " + i);
			//			}

			//initialise trace
			List<Item> trace = new ArrayList<>();

			//pick the trace
			{
				//select a root node
				Item node = pickNode(stateSpace.roots, stateSpace.sumWeightLeft.doubleValue(), random);

				if (node == null) {
					return;
				}

				while (node != null) {
					trace.add(node);

					//expand children
					semantics.setState(node.marking);
					BitSet enabledTransitions = semantics.getEnabledTransitions();
					double totalTransitionsMass = semantics.getTotalWeightOfEnabledTransitions();

					if (semantics.isFinalState()) {
						//We are in a deadlock state. By definition, that's a final state.
						//Accept the current trace and add it to the language
						language.add(prefix2path(semantics, node.prefix), node.probability);

						//update the total weight of the state space 
						stateSpace.sumWeightLeft = stateSpace.sumWeightLeft
								.subtract(BigDecimal.valueOf(node.probability));

						node = null;
					} else {
						//we can still take steps; add independent transitions to the prefix and continue
						Collection<int[]> As = MaximalIndependentSets.BronKerbosch2(net, enabledTransitions,
								neighbourCache);
						ArrayList<Item> children = new ArrayList<Item>();
						for (int[] A : As) {
							//create a new priority queue item to recurse on
							Item childNode = createChildItem(net, semantics, node, totalTransitionsMass, A,
									enabledTransitions, neighbourCache);

							children.add(childNode);
						}

						node = pickNode(children, node.probability, random);
						stateSpace.roots.addAll(children);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param nodes
	 * @param random
	 * @return a randomly selected node, or null if there was no node to choose
	 *         from. The node is removed from the state space.
	 */
	private static Item pickNode(Iterable<Item> nodes, double sumWeight, Random random) {
		if (sumWeight == 0) {
			return null;
		}
		double pick = random.nextDouble() * sumWeight;
		Iterator<Item> it = nodes.iterator();
		while (it.hasNext()) {
			Item node = it.next();
			pick -= node.probability;
			if (pick <= 0 || !it.hasNext()) {
				it.remove();
				return node;
			}
		}
		assert false;
		return null;
	}

	private static double weightNodes(Iterable<Item> nodes) {
		double sum = 0;
		for (Item node : nodes) {
			sum += node.probability;
		}
		return sum;
	}

	private static double getProbability(int[] selectedTransitions, BitSet enabledTransitions,
			StochasticLabelledPetriNet net, StochasticLabelledPetriNetSemantics semantics, PetriNetCache cache) {
		double result = 1;
		for (int transitionIndex : selectedTransitions) {

			double weightTransition = semantics.getTransitionWeight(transitionIndex);

			BitSet neighbours = StochasticPetriNetUtils.getNeighbours(net, transitionIndex, enabledTransitions, cache);
			neighbours.set(transitionIndex); //neighbours doesn't include the transition itself, so add it
			double weightNeighbours = StochasticPetriNet2StochasticPathLanguage.getTotalMass(semantics, neighbours);

			result *= weightTransition / weightNeighbours;
		}
		return result;
	}

	private static int[] prefix2path(StochasticLabelledPetriNetSemantics semantics, int[] prefix) {
		int[] result = new int[prefix.length];
		System.arraycopy(prefix, 0, result, 0, prefix.length);
		return result;
	}
}