package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

public interface PartialOrderCertain extends PartialOrder {
	static PartialOrderCertain instance = new PartialOrderCertain() {

	};

	static PartialOrderCertain instance() {
		return instance;
	}
}