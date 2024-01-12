package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;

public interface StochasticPathIterator<O> extends StochasticTraceIterator<O> {
	public int[] getPath();

	public int[] nextPath();
}