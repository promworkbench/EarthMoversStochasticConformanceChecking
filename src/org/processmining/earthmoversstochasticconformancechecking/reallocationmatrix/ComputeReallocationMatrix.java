package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParameters;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;

import lpsolve.LpSolve;

public class ComputeReallocationMatrix {

	public static Pair<ReallocationMatrix, Double> compute(StochasticLanguage languageA, StochasticLanguage languageB,
			DistanceMatrix distanceMatrix, EMSCParameters parameters, ProMCanceller canceller)
			throws InterruptedException {
		distanceMatrix = distanceMatrix.clone();
		distanceMatrix.init(languageA, languageB, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		LpSolve solver = null;
		try {
			solver = CreateLPsolveProblem.makeProblem(languageA, languageB, distanceMatrix);

			if (parameters.isDebug()) {
				System.out.println("start solving");
			}

			// solve the problem
			//solver.printLp();
			solver.solve();
			//solver.printSolution(languageModel.getNumberOfTraces());

			ReallocationMatrix relocationMatrix = new ReallocationMatrix(languageA.size(), languageB.size());

			if (parameters.isDebug()) {
				System.out.println(String.format("LipSolve EMD: %f", solver.getObjective()));
			}

			// store solution in matrix
			//System.out.println("Value of objective function: " + solver.getObjective());
			double similarity = 1 - solver.getObjective();

			double[] var = solver.getPtrVariables();
			int logTrace = 0;
			int modelTrace = 0;
			for (int i = 0; i < var.length; i++) {
				//System.out.println("Value of var[" + i + "] = " + var[i]);
				relocationMatrix.set(logTrace, modelTrace, var[i]);

				modelTrace++;
				if (modelTrace == languageB.size()) {
					modelTrace = 0;
					logTrace++;
				}
			}

			return Pair.of(relocationMatrix, similarity);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (solver != null) {
				solver.deleteAndRemoveLp();
			}
		}
	}
}
