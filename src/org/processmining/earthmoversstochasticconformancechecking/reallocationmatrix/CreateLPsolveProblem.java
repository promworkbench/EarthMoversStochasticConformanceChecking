package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix;

import java.util.Arrays;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class CreateLPsolveProblem {

	static {
		System.loadLibrary("lpsolve55");
		System.loadLibrary("lpsolve55j");
	}

	public static LpSolve makeProblem(StochasticLanguage languageA, StochasticLanguage languageB,
			DistanceMatrix distanceMatrix) throws LpSolveException {
		LpSolve solver = LpSolve.makeLp(0, languageA.size() * languageB.size());

		solver.setDebug(false);
		solver.setVerbose(0);

		solver.setAddRowmode(true);

		//		System.out.println("start computing Levenshtein distance");

		//set the objective function
		double[] objectiveFunction = distanceMatrix.getDistances();
		solver.setObjFn(objectiveFunction);

		//System.out.println(DistanceMatrix2String.toLatex(L, languageModel, objectiveFunction));

		//		System.out.println("start building LP problem");

		//table should be 1
		{
			double[] constraint = constraint(languageA, languageB);
			Arrays.fill(constraint, 1);

			solver.addConstraint(constraint, LpSolve.EQ, 1);
		}

		//each row should sum to L's value
		StochasticTraceIterator itA = languageA.iterator();
		{
			TIntList indices = new TIntArrayList();
			TDoubleList values = new TDoubleArrayList();
			for (int l = 0; l < languageA.size(); l++) {
				indices.clear();
				values.clear();
				for (int m = 0; m < languageB.size(); m++) {
					set(indices, values, languageB, l, m, 1);
				}
				itA.next();
				//System.out.println(Arrays.toString(next));

				solver.addConstraintex(indices.size(), values.toArray(), indices.toArray(), LpSolve.EQ,
						itA.getProbability());
			}
		}

		//each column should sum to M's value
		{
			StochasticTraceIterator itB = languageB.iterator();
			TIntList indices = new TIntArrayList();
			TDoubleList values = new TDoubleArrayList();
			for (int m = 0; m < languageB.size(); m++) {
				//double[] constraint = constraint(L, M);
				indices.clear();
				values.clear();
				for (int l = 0; l < languageA.size(); l++) {
					//set(constraint, M, l, m, 1);
					set(indices, values, languageB, l, m, 1);
				}
				itB.next();

				solver.addConstraintex(indices.size(), values.toArray(), indices.toArray(), LpSolve.GE,
						itB.getProbability());
			}
		}

		solver.setAddRowmode(false);

		return solver;
	}

	public static double[] constraint(StochasticLanguage languageA, StochasticLanguage languageB) {
		return new double[languageA.size() * languageB.size() + 1];
	}

	public static void set(double[] constraint, StochasticLanguage languageB, int l, int m, double value) {
		constraint[l * languageB.size() + m + 1] = value;
	}

	private static void set(TIntList indices, TDoubleList values, StochasticLanguage languageB, int l, int m,
			double value) {
		indices.add(l * languageB.size() + m + 1);
		values.add(value);
	}
}
