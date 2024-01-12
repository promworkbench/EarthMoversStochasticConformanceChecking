package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

public interface TotalOrder extends Order {

	static TotalOrder instance = new TotalOrder() {

	};

	static TotalOrder instance() {
		return instance;
	}

}
