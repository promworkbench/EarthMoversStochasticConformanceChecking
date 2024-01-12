package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import java.util.ArrayList;
import java.util.List;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition2IndexKey;

import cern.colt.Arrays;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class StochasticPathLanguagePartialOrderImpl implements StochasticPathLanguage<PartialOrder> {

	protected List<int[]> paths = new ArrayList<>();
	protected TDoubleList probabilities = new TDoubleArrayList();
	private Activity2IndexKey activityKey;
	private StochasticTransition2IndexKey transitionKey;

	public StochasticPathLanguagePartialOrderImpl(StochasticTransition2IndexKey transitionKey,
			Activity2IndexKey activityKey) {
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

	public StochasticPathIterator<PartialOrder> iterator() {
		return new StochasticPathIterator<PartialOrder>() {
			int pathIndex = -1;

			@Override
			public int[] next() {
				int[] path = nextPath();
				int[] trace = PartialOrderPath2Trace.convert(getTransitionKey(), path);
				return trace;
			}

			@Override
			public boolean hasNext() {
				return pathIndex < paths.size() - 1;
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
