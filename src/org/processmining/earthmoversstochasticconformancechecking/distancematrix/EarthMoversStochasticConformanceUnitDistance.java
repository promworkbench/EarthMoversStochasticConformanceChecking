package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.UnboundedModelException;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.TotalOrderUtils;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.StochasticNetSemantics;
import org.processmining.models.semantics.petrinet.impl.StochasticNetSemanticsImpl;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.set.hash.THashSet;

public class EarthMoversStochasticConformanceUnitDistance {

	public static double getEMSC(StochasticLanguage<TotalOrder> L, StochasticLanguage<TotalOrder> M) {
		double sum = 0;
		double sumLog = 0;
		for (StochasticTraceIterator<?> it = L.iterator(); it.hasNext();) {
			it.next();
			String[] traceL = TotalOrderUtils.getStringTrace(L, it.getTraceIndex());
			double probability = it.getProbability();
			sumLog += probability;

			double modelProbability = 0;
			for (StochasticTraceIterator<?> itM = M.iterator(); itM.hasNext();) {
				itM.next();
				String[] traceM = TotalOrderUtils.getStringTrace(M, itM.getTraceIndex());
				if (Arrays.deepEquals(traceL, traceM)) {
					modelProbability = itM.getProbability();
				}
			}

			//System.out.println(" p_log " + probability + " p_model" + modelProbability + Arrays.toString(trace));
			sum += Math.max(probability - modelProbability, 0);
		}
//		System.out.println("sum log " + sumLog);
//		System.out.println("sum " + sum);
		return sumLog - sum;
	}

	/**
	 * Make sure that L is normalised and finite.
	 * 
	 * @param L
	 * @param M
	 * @return
	 * @throws IllegalTransitionException
	 * @throws UnboundedModelException
	 */
	public static double getEMSC(StochasticLanguage<TotalOrder> L, StochasticNet M, Marking initialMarking)
			throws IllegalTransitionException, UnboundedModelException {

		StochasticNetSemantics semantics = new StochasticNetSemanticsImpl();
		semantics.initialize(M.getTransitions(), initialMarking);

		double sum = 0;
		double sumLog = 0;
		for (StochasticTraceIterator<?> it = L.iterator(); it.hasNext();) {
			it.next();
			String[] trace = TotalOrderUtils.getStringTrace(L, it.getTraceIndex());
			double probability = it.getProbability();
			sumLog += probability;

			double modelProbability = getProbability(trace, semantics, initialMarking);
			//System.out.println(" p_log " + probability + " p_model" + modelProbability + Arrays.toString(trace));
			sum += Math.max(probability - modelProbability, 0);
		}
		//		System.out.println("sum log " + sumLog);
		//		System.out.println("sum " + sum);
		return sumLog - sum;
	}

	private static double getProbability(String[] trace, StochasticNetSemantics semantics, Marking initialMarking)
			throws IllegalTransitionException, UnboundedModelException {

		ArrayDeque<Marking> queueMarking = new ArrayDeque<>();
		TDoubleArrayList queuePrefixProbabilities = new TDoubleArrayList();
		ArrayDeque<String[]> queuePrefix = new ArrayDeque<>();
		ArrayDeque<Set<Marking>> queueSilentMarkingsVisited = new ArrayDeque<>(); //contains all markings reached by following silent transitions. The sets are shared by all searches through silent transitions.

		queueMarking.add(initialMarking);
		queuePrefixProbabilities.add(1);
		queuePrefix.add(new String[0]);
		queueSilentMarkingsVisited.add(new THashSet<Marking>());

		double sumProbability = 0;

		while (!queueMarking.isEmpty()) {
			Marking currentMarking = queueMarking.remove();
			double prefixProbability = queuePrefixProbabilities.removeAt(0);
			String[] prefix = queuePrefix.poll();
			Set<Marking> silentMarkingsVisited = queueSilentMarkingsVisited.poll();

			semantics.setCurrentState(currentMarking);
			Collection<Transition> enabledTransitions = semantics.getExecutableTransitions();

			if (trace.length == prefix.length) {
				if (enabledTransitions.isEmpty()) {
					//deadlock found when we were looking for one.
					sumProbability += prefixProbability;
				} else {
					//deadlock found when we were looking for an activity.
					//still follow silent transitions
					for (Transition transition : enabledTransitions) {
						if (transition.isInvisible()) {
							//follow this silent transition as it might lead to something

							semantics.setCurrentState(currentMarking);
							semantics.executeExecutableTransition(transition);
							Marking newMarking = semantics.getCurrentState();

							if (!silentMarkingsVisited.contains(newMarking)) {
								double totalTransitionsMass = getTotalMass(enabledTransitions);
								//compute the new probability
								double newProbability;
								if (totalTransitionsMass > 0) {
									newProbability = prefixProbability * ((TimedTransition) transition).getWeight()
											/ totalTransitionsMass;
								} else {
									newProbability = prefixProbability;
								}

								checkBoundnedNess(silentMarkingsVisited, newMarking);
								silentMarkingsVisited.add(newMarking);
								addToQueues(queuePrefix, queuePrefixProbabilities, queueMarking,
										queueSilentMarkingsVisited, newMarking, prefix, newProbability,
										silentMarkingsVisited);
							} else {
								//marking encountered twice
								//throw new ModelHasTauLoopException();
							}
						}
					}
				}
			} else {
				if (enabledTransitions.isEmpty()) {
					//deadlock found when we were looking for an activity
					sumProbability += 0;
				} else {
					for (Transition transition : enabledTransitions) {
						String activity = trace[prefix.length];
						if (transition.isInvisible()) {
							//follow this silent transition as it might lead to something

							semantics.setCurrentState(currentMarking);
							semantics.executeExecutableTransition(transition);
							Marking newMarking = semantics.getCurrentState();

							if (!silentMarkingsVisited.contains(newMarking)) {
								double totalTransitionsMass = getTotalMass(enabledTransitions);
								//compute the new probability
								double newProbability;
								if (totalTransitionsMass > 0) {
									newProbability = prefixProbability * ((TimedTransition) transition).getWeight()
											/ totalTransitionsMass;
								} else {
									newProbability = prefixProbability;
								}
								checkBoundnedNess(silentMarkingsVisited, newMarking);
								silentMarkingsVisited.add(newMarking);
								addToQueues(queuePrefix, queuePrefixProbabilities, queueMarking,
										queueSilentMarkingsVisited, newMarking, prefix, newProbability,
										silentMarkingsVisited);
							}
						} else {
							if (activity.equals(transition.getLabel())) {
								double totalTransitionsMass = getTotalMass(enabledTransitions);

								semantics.setCurrentState(currentMarking);
								semantics.executeExecutableTransition(transition);
								Marking newMarking = semantics.getCurrentState();

								String[] newPrefix = ArrayUtils.add(prefix, transition.getLabel());

								//compute the new probability
								double newProbability;
								if (totalTransitionsMass > 0) {
									newProbability = prefixProbability * ((TimedTransition) transition).getWeight()
											/ totalTransitionsMass;
								} else {
									newProbability = prefixProbability;
								}

								addToQueues(queuePrefix, queuePrefixProbabilities, queueMarking,
										queueSilentMarkingsVisited, newMarking, newPrefix, newProbability,
										new THashSet<Marking>());

							} else {
								//we found a different activity than we are looking for.
								sumProbability += 0;
							}
						}
					}
				}
			}
		}

		return sumProbability;
	}

	private static void addToQueues(ArrayDeque<String[]> prefixQueue, TDoubleArrayList prefixProbabilityQueue,
			ArrayDeque<Marking> markingQueue, ArrayDeque<Set<Marking>> queueSilentMarkingsVisited, Marking newMarking,
			String[] newPrefix, double newProbability, Set<Marking> silentMarkingsVisited) {
		prefixQueue.add(newPrefix);
		prefixProbabilityQueue.add(newProbability);
		markingQueue.add(newMarking);
		queueSilentMarkingsVisited.add(silentMarkingsVisited);
	}

	private static void checkBoundnedNess(Iterable<Marking> markings, Marking add) throws UnboundedModelException {
		for (Marking a : markings) {
			boolean bounded = false;
			for (Place p : add) {
				if (add.occurrences(p) < a.occurrences(p)) {
					bounded = true;
					continue;
				}
			}
			if (bounded) {
				return;
			}
			for (Place p : a) {
				if (add.occurrences(p) < a.occurrences(p)) {
					bounded = true;
					continue;
				}
			}
			if (bounded) {
				return;
			}
			throw new UnboundedModelException();
		}

	}

	public static double getTotalMass(Iterable<Transition> enabledTransitions) {
		double sum = 0;
		for (Transition t : enabledTransitions) {
			sum += ((TimedTransition) t).getWeight();
		}
		return sum;
	}

}
