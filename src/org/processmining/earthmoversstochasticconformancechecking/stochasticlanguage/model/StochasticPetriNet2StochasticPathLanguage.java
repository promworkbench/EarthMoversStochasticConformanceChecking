package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model;

import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.PrefixProbabilityMarking;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.QueueCombination;
import org.processmining.earthmoversstochasticconformancechecking.parameters.LanguageGenerationStrategyFromModel;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * Use multithreading and byte storage
 * 
 * @author sander
 *
 */
public class StochasticPetriNet2StochasticPathLanguage {

	/**
	 * Assumption: the deadlock markings are equal to the final markings.
	 * 
	 * @param net
	 * @param initialMarking
	 * @param terminationStrategy
	 * @param canceller
	 * @return
	 * @throws IllegalTransitionException
	 * @throws InterruptedException
	 */
	public static StochasticPathLanguage<TotalOrder> convert(StochasticLabelledPetriNet net,
			LanguageGenerationStrategyFromModel terminationStrategy, Activity2IndexKey activityKey, int numberOfThreads,
			ProMCanceller canceller) throws InterruptedException {

		//initialise
		StochasticLabelledPetriNetSemantics semantics = net.getDefaultSemantics();
		StochasticTransition2IndexKey transitionKey = new StochasticTransition2IndexKey(semantics, activityKey);
		StochasticPathLanguageImpl<TotalOrder> language = new StochasticPathLanguageImpl<>(transitionKey, activityKey);

		//set to work
		walk(language, semantics, terminationStrategy, numberOfThreads, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		return language;
	}

	public static double walk(final StochasticPathLanguageImpl<TotalOrder> language,
			final StochasticLabelledPetriNetSemantics semantics,
			LanguageGenerationStrategyFromModel terminationStrategy, int numberOfThreads, final ProMCanceller canceller)
			throws InterruptedException {
		//initialise queues
		final ConcurrentLinkedQueue<byte[]> queue = new ConcurrentLinkedQueue<>();
		final AtomicInteger queueSize = new AtomicInteger(1);

		//add the first step to the queues
		semantics.setInitialState();
		queue.add(PrefixProbabilityMarking.pack(new int[0], 1.0, semantics.getState()));

		final AtomicDouble massCovered = new AtomicDouble(0);
		final LanguageGenerationStrategyFromModel terminationStrategy2 = terminationStrategy.clone();
		terminationStrategy2.initialise();

		Thread[] threads = new Thread[Math.max(1, numberOfThreads)];
		for (int thread = 0; thread < threads.length; thread++) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						thread(semantics.clone(), queue, queueSize, massCovered, terminationStrategy2, language,
								canceller);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			threads[thread] = new Thread(runnable, "stochastic language thread " + thread);
			threads[thread].start();
		}

		for (int thread = 0; thread < threads.length; thread++) {
			threads[thread].join();
			threads[thread] = null;
		}

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		return massCovered.get();
	}

	public static double getTotalMass(StochasticLabelledPetriNetSemantics semantics, BitSet enabledTransitions) {
		double sum = 0;
		for (int transition = enabledTransitions.nextSetBit(0); transition >= 0; transition = enabledTransitions
				.nextSetBit(transition + 1)) {
			sum += semantics.getTransitionWeight(transition);
			if (transition == Integer.MAX_VALUE) {
				break;
			}
		}
		return sum;
	}

	public static void thread(StochasticLabelledPetriNetSemantics semantics, ConcurrentLinkedQueue<byte[]> globalQueue,
			AtomicInteger globalQueueSize, AtomicDouble massCovered,
			LanguageGenerationStrategyFromModel terminationStrategy, StochasticPathLanguageImpl<TotalOrder> language,
			ProMCanceller canceller) throws InterruptedException {
		final int markingLength = semantics.getState().length;

		final QueueCombination queue = new QueueCombination(globalQueue, globalQueueSize);
		//ConcurrentLinkedQueue<byte[]> queue = globalQueue;

		byte[] prefixProbabilityMarking;
		while (true) {

			if (canceller.isCancelled()) {
				return;
			}

			prefixProbabilityMarking = queue.poll();
			if (prefixProbabilityMarking == null) {
				if (terminationStrategy.isTerminated(massCovered.get(), language.size())) {
					return;
				} else {
					Thread.sleep(10);
					continue;
				}
			}

			int[] prefix = PrefixProbabilityMarking.getPrefix(prefixProbabilityMarking, markingLength);
			double prefixProbability = PrefixProbabilityMarking.getProbability(prefixProbabilityMarking);
			byte[] marking = PrefixProbabilityMarking.getMarking(prefixProbabilityMarking, markingLength);

			semantics.setState(marking);
			double totalTransitionsMass = semantics.getTotalWeightOfEnabledTransitions();

			if (semantics.isFinalState()) {
				//we are in a deadlock state. by assumption, that's a final state.

				//accept the current trace and add it to the log
				synchronized (language) {
					if (terminationStrategy.isTerminated(massCovered.get(), language.size())) {
						return;
					}
					language.add(prefix2path(semantics, prefix), prefixProbability);
					if (terminationStrategy.isTerminated(massCovered.addAndGet(prefixProbability), language.size())) {
						return;
					}
				}

				//System.out.println(prefix + " accepted, total " + massCovered);

				//				System.out.println(prefixProbability + " " + Arrays.toString(prefix) + " total " + massCovered);

			} else {
				//we are not in a deadlock state; continue
				BitSet enabledTransitions = (BitSet) semantics.getEnabledTransitions().clone();
				for (int transition = enabledTransitions.nextSetBit(0); transition >= 0; transition = enabledTransitions
						.nextSetBit(transition + 1)) {

					semantics.setState(marking);
					semantics.executeTransition(transition);
					byte[] newMarking = semantics.getState();

					int[] newPrefix = ArrayUtils.add(prefix, transition);

					//compute the new probability
					double newProbability = prefixProbability * semantics.getTransitionWeight(transition)
							/ totalTransitionsMass;

					queue.add(PrefixProbabilityMarking.pack(newPrefix, newProbability, newMarking));
				}
			}
		}
	}

	private static int[] prefix2path(StochasticLabelledPetriNetSemantics semantics, int[] prefix) {
		int[] result = new int[prefix.length];
		System.arraycopy(prefix, 0, result, 0, prefix.length);
		return result;
	}
}
