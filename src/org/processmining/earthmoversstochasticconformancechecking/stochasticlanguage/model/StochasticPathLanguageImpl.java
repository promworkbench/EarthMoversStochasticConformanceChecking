package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * A stochastic path is a list of transitions. (int[]). A
 * StochasticTransition2IndexKey transforms this into a trace or an
 * integerTrace.
 * 
 * @author sander
 *
 */
public class StochasticPathLanguageImpl<A> implements StochasticPathLanguage<A> {

	protected List<int[]> paths = new ArrayList<>();
	protected TDoubleList probabilities = new TDoubleArrayList();
	private Activity2IndexKey activityKey;
	private StochasticTransition2IndexKey transitionKey;

	public StochasticPathLanguageImpl(StochasticTransition2IndexKey transitionKey, Activity2IndexKey activityKey) {
		this.activityKey = activityKey;
		this.transitionKey = transitionKey;
	}

	/**
	 * Unfortunately, adding to a list is not thread safe.
	 * 
	 * @param path
	 * @param probability
	 */
	public synchronized void add(int[] path, double probability) {
		paths.add(path);
		probabilities.add(probability);
	}

	public StochasticPathIterator<A> iterator() {
		return new StochasticPathIterator<A>() {
			int pathIndex = -1;

			@Override
			public int[] next() {
				int[] path = nextPath();
				int[] trace = getTransitionKey().path2trace(path);
				return trace;
			}

			@Override
			public boolean hasNext() {
				return pathIndex < paths.size() - 2;
			}

			@Override
			public int getTraceIndex() {
				return pathIndex;
			}

			@Override
			public double getProbability() {
				return probabilities.get(pathIndex);
			}

			@Override
			public int[] nextPath() {
				pathIndex++;
				return getPath();
			}

			@Override
			public int[] getPath() {
				return paths.get(pathIndex);
			}
		};
	}

	@Override
	public int size() {
		return paths.size();
	}

	@Override
	public int[] getPath(int pathIndex) {
		return paths.get(pathIndex);
	}

	@Override
	public String getTraceString(int traceIndex) {
		return Arrays.toString(getActivityKey().toTraceString(getTrace(traceIndex)));
	}

	@Override
	public int[] getTrace(int traceIndex) {
		return getTransitionKey().path2trace(getPath(traceIndex));
	}

	public Activity2IndexKey getActivityKey() {
		return activityKey;
	}

	public void setActivityKey(Activity2IndexKey activityKey) {
		this.activityKey = activityKey;
	}

	public StochasticTransition2IndexKey getTransitionKey() {
		return transitionKey;
	}

	public void setTransitionKey(StochasticTransition2IndexKey transitionKey) {
		this.transitionKey = transitionKey;
	}
}