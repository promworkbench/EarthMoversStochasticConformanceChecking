package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.framework.plugin.ProMCanceller;

public abstract class DistanceMatrixAbstract<A extends Order, B extends Order> implements DistanceMatrix<A, B> {

	private double[] distances;

	private int languageBsize;

	public void init(StochasticLanguage<A> languageA, final StochasticLanguage<B> languageB,
			final ProMCanceller canceller) throws InterruptedException {
		languageBsize = languageB.size();
		distances = new double[languageA.size() * languageB.size() + 1];

		final AtomicInteger al = new AtomicInteger(0);
		final StochasticTraceIterator<A> itA = languageA.iterator();

		Thread[] threads = new Thread[Math.max(1, Runtime.getRuntime().availableProcessors() - 1)];
		for (int thread = 0; thread < threads.length; thread++) {
			Runnable runnable = new Runnable() {
				public void run() {
					thread(itA, languageB, al, canceller);
				}
			};
			threads[thread] = new Thread(runnable, "distances thread " + thread);
			threads[thread].start();
		}

		for (int thread = 0; thread < threads.length; thread++) {
			threads[thread].join();
			threads[thread] = null;
		}
	}

	public void thread(StochasticTraceIterator<A> itA, StochasticLanguage<B> languageB, AtomicInteger al,
			ProMCanceller canceller) {
		while (true) {

			//get a trace from the log
			int[] traceA;
			int l;
			synchronized (itA) {
				l = al.getAndIncrement();
				if (!itA.hasNext()) {
					return;
				}
				traceA = itA.next();
			}

			//			if (l % 1 == 0) {
			//				System.out.println("  compute distances for trace " + l + " in " + Thread.currentThread().getName());
			//			}

			if (canceller.isCancelled()) {
				return;
			}

			StochasticTraceIterator<B> itB = languageB.iterator();
			for (int m = 0; m < languageB.size(); m++) {
				int[] traceB = itB.next();

				//				if (m % 10 == 0) {
				//					System.out.println(
				//							"  compute distance for trace " + l + " " + m + " in " + Thread.currentThread().getName());
				//				}

				double distance = getDistance(traceA, traceB, canceller);
				set(l, m, distance);

				if (canceller.isCancelled()) {
					return;
				}
			}
		}
	}

	/**
	 * A method that computes the actual distance. Should be thread-safe.
	 * 
	 * @param traceA
	 * @param traceB
	 * @return
	 */
	protected abstract double getDistance(int[] traceA, int[] traceB, ProMCanceller canceller);

	public double[] getDistances() {
		return distances;
	}

	public double getDistance(int l, int m) {
		return distances[l * languageBsize + m + 1];
	}

	private void set(int l, int m, double distance) {
		distances[l * languageBsize + m + 1] = distance;
	}

	@SuppressWarnings("unchecked")
	public DistanceMatrixAbstract<A, B> clone() {
		DistanceMatrixAbstract<A, B> result;
		try {
			result = (DistanceMatrixAbstract<A, B>) super.clone();
			result.distances = ArrayUtils.clone(distances);
			result.languageBsize = languageBsize;
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}