package org.processmining.earthmoversstochasticconformancechecking.distancematrix;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.framework.plugin.ProMCanceller;

public class TransposedDistanceMatrixWrapper<A extends Order, B extends Order> implements DistanceMatrix<A, B> {

	private DistanceMatrix<B, A> distMatrix;

	public TransposedDistanceMatrixWrapper(DistanceMatrix<B, A> distMatrix) {
		this.distMatrix = distMatrix;
	}

	@SuppressWarnings("unchecked")
	public DistanceMatrix<A, B> clone() {
		return (DistanceMatrix<A, B>) distMatrix.clone();
	}

	public void init(StochasticLanguage<A> languageA, StochasticLanguage<B> languageB, ProMCanceller canceller)
			throws InterruptedException {
		distMatrix.init(languageB, languageA, canceller);
	}

	public double[] getDistances() {
		return distMatrix.getDistances();
	}

	public double getDistance(int l, int m) {
		return distMatrix.getDistance(m, l);
	}

}
