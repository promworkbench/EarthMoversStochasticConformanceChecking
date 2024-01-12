package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.framework.plugin.ProMCanceller;

/**
 * A factory for an AKP solver that represents the tree with an array and does a
 * complete comparison between surplus sources and deficit targets in each step.
 * 
 * @author brockhoff
 */
public class AKPFactoryArraySimple extends SolverFactory<AKPSolverArraySimpleImprov> {

	/**
	 * Reference to the solver that will be build
	 */
	private AKPSolverArraySimpleImprov solver;
	
	/**
	 * 
	 * @param groundDist
	 */
	public AKPFactoryArraySimple(DistanceMatrix<?, ?> groundDist) {
		super(groundDist);
	}
	
	// IMPORTANT: weights of holes need to be positive
	@Override
	public void setupNewSolver(StochasticLanguage<?> Ll, StochasticLanguage<?> Lr, ProMCanceller canceller) {
		int cSrc = Ll.size();
		int cTar = Lr.size();

		// Weights sources
		float[] sizeHill = new float[Ll.size()];
		{
			StochasticTraceIterator<?> itL = Ll.iterator();
			for (int i = 0; i < Ll.size(); i++) {
				itL.next();
				sizeHill[i] = (float) itL.getProbability();
			}
		}

		if (canceller.isCancelled()) {
			return;
		}

		// Weights targets
		float[] sizeHole = new float[Lr.size()];
		{
			StochasticTraceIterator<?> itR = Lr.iterator();
			for (int i = 0; i < Lr.size(); i++) {
				itR.next();
				sizeHole[i] = (float) itR.getProbability();
			}
		}

		if (canceller.isCancelled()) {
			return;
		}

		float[] negSizeHole = new float[Lr.size()];

		for (int i = 0; i < Lr.size(); i++) {
			negSizeHole[i] = -1 * sizeHole[i];
		}

		if (canceller.isCancelled()) {
			return;
		}

		StructInitEPSA initSolverStruct;
		InitTreeArrayStruct initTreeStruct;
		//Build the initial tree and calculate the dual values
		initTreeStruct = BuildTree.buildTreeArray(cSrc, cTar, sizeHill, negSizeHole, groundDist);
		initSolverStruct = new StructInitEPSA(initTreeStruct);
		//TestHelper.arrayTreeToLatex(initSolverStruct.tree, 0);

		if (canceller.isCancelled()) {
			return;
		}

		solver = new AKPSolverArraySimpleImprov(initSolverStruct);

	}

	@Override
	public AKPSolverArraySimpleImprov getSolver() {
		return solver;
	}
}
