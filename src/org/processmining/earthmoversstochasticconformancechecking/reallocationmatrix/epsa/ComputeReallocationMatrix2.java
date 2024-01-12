package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParameters;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Triple;

public class ComputeReallocationMatrix2 {
	/**
	 * 
	 * @param <X>
	 * @param <Y>
	 * @param languageA
	 * @param languageB
	 * @param distanceMatrix
	 * @param parameters
	 * @param canceller
	 * @return reallocation matrix, distance, and the clone of the distance
	 *         matrix
	 * @throws InterruptedException
	 */
	public static <X extends Order, Y extends Order> Triple<ReallocationMatrix, Double, DistanceMatrix<X, Y>> compute(
			StochasticLanguage<X> languageA, StochasticLanguage<Y> languageB, DistanceMatrix<X, Y> distanceMatrix,
			EMSCParameters<X, Y> parameters, ProMCanceller canceller) throws InterruptedException {

		EarthMoversStochasticConformancePlugin.debug(parameters, "Create distance matrix..");

		distanceMatrix = distanceMatrix.clone();
		distanceMatrix.init(languageA, languageB, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		Pair<ReallocationMatrix, Double> p = computeWithDistanceMatrixInitialised(languageA, languageB, distanceMatrix,
				parameters, canceller);

		if (p == null || canceller.isCancelled()) {
			return null;
		}

		return Triple.of(p.getA(), p.getB(), distanceMatrix);
	}

	public static <X extends Order, Y extends Order> Pair<ReallocationMatrix, Double> computeWithDistanceMatrixInitialised(
			StochasticLanguage<X> languageA, StochasticLanguage<Y> languageB, DistanceMatrix<X, Y> distanceMatrix,
			EMSCParameters<?, ?> parameters, ProMCanceller canceller) {

		EarthMoversStochasticConformancePlugin.debug(parameters, "Create solver..");

		AKPFactoryArraySimple fac = new AKPFactoryArraySimple(distanceMatrix);
		fac.setupNewSolver(languageA, languageB, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		EarthMoversStochasticConformancePlugin.debug(parameters, "Solve..");

		AKPSolverArraySimpleImprov solver = fac.getSolver();

		double similarity = 1 - solver.solve(canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		EarthMoversStochasticConformancePlugin.debug(parameters, "Create reallocation matrix..");

		ReallocationMatrix relocationMatrix = new ReallocationMatrix(languageA.size(), languageB.size());
		solver.fillMatrix(relocationMatrix);

		//solver.printFinalFlow();

		return Pair.of(relocationMatrix, similarity);
	}
}
