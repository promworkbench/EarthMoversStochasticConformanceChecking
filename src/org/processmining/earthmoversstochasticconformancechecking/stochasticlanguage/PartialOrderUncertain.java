package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

public interface PartialOrderUncertain extends PartialOrder {
	static PartialOrderUncertain instance = new PartialOrderUncertain() {

	};

	static PartialOrderUncertain instance() {
		return instance;
	}
}
