package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa;

import java.util.List;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.framework.plugin.ProMCanceller;

/**
 * A class which performs the AKP algorithm on an array representation of the
 * tree. Internally the tree (basic solution, not necessary feasible) is
 * represented using several arrays. In each step a complete comparison between
 * surplus sources and deficit targets is made.
 * <p>
 * Papers:
 * <p>
 * <ul>
 * <li>Charalampos Papamanthou, Konstantinos Paparrizos, and Nikolaos Samaras.
 * 2004. Computational experience with exterior point algorithms for the
 * transportation problem. Appl. Math. Comput. 158, 2 (November 2004), 459-475.
 * (AKP algorithm)
 * <li>M.S. Bazaraa, J.J. Jarvis, and H.D. Sherali. Linear Programming and
 * Network Flows. Wiley, 2010, 482-488. (Tree)
 * </ul>
 * 
 * @author brockhoff
 */
public class AKPSolverArraySimple {

	/**
	 * A handle to the internal tree representing the current basic solution (in
	 * general neither primal nor dual feasible)
	 */
	private Tree tree;

	/**
	 * Count sources
	 */
	public int cSrc;
	/**
	 * Count targets;
	 */
	public int cTar;

	/**
	 * Iterator to iterate over subtrees for a given root
	 */
	private TreeIterator itTree;

	/**
	 * Iterator for the targets in the deficit trees
	 */
	private DeficitTargetIterator defTarIt;

	/**
	 * List of deficit trees
	 */
	private List<Integer> defTrees;

	/**
	 * List of surplus trees
	 */
	private List<Integer> surTrees;

	/**
	 * Reducing costs in the current step
	 */
	double minRedC;

	/**
	 * If EMD has been calculated
	 */
	boolean isExact;

	/**
	 * Cost matrix (source,target)
	 */
	DistanceMatrix costs;

	public AKPSolverArraySimple(StructInitEPSA inst) {
		super();
		this.tree = inst.tree;
		this.cSrc = inst.cSrc;
		this.cTar = inst.cTar;
		this.defTrees = inst.defTrees;
		this.surTrees = inst.surTrees;

		// Initialize iterators
		itTree = tree.getFreshTreeIterator();
		defTarIt = new DeficitTargetIterator(tree.getFreshTreeIterator(), defTrees, this.cTar, this.cSrc + 1);

		//iteration = 1;

		isExact = false;

		this.costs = inst.costs;
	}

	/**
	 * Runs the simplex type algorithm.
	 * 
	 * @param canceller
	 */
	public double solve(ProMCanceller canceller) {
		//int iterations = 1;
		//While further iterations are necessary
		while (nextIteration()) {

			if (canceller.isCancelled()) {
				return -Double.MAX_VALUE;
			}

			//iterations++;
		}

		return tree.getCurDual();
	}

	/**
	 * Gets the next entering arc.
	 *
	 * @return
	 */
	public int[] getNextEntering() {

		int src, tar;
		int from = -1;
		int to = -1;
		minRedC = Double.POSITIVE_INFINITY;
		double redC;

		//Compare reducing costs between surplus sources and deficit targets
		//All sources in a surplus tree
		for (Integer surRoot : surTrees) {
			itTree.setRoot(surRoot);
			while (itTree.hasNext()) {
				src = itTree.next();

				//Skip targets
				if (src > cSrc) {
					continue;
				}

				//Compare with possible targets
				while (defTarIt.hasNext()) {
					tar = defTarIt.next();

					redC = tree.redCost(src, tar);
					//Save minimum cost edge
					if (redC < minRedC) {
						minRedC = redC;
						from = src;
						to = tar;
					}
				}
				//Reset targets to first target
				defTarIt.resetToStart();
			}
		}

		//If no edge is found
		//-> No further iteration are mandatory
		if (from == -1 && to == -1)
			return null;
		else {
			int[] res = { from, to };
			return res;
		}
	}

	public boolean nextIteration() {
		int[] entering;
		entering = getNextEntering();
		//There is not edge between a surplus and a deficit tree -> Optimal solution found
		if (entering == null) {
			isExact = true;
			return false;
		} else {
			//Calculate new costs (adpat dual values)
			//Update tree structure
			int[] leaving = tree.enterEdgeNUpdate(entering, minRedC);

			//A (direct) child of the artificial root node is cut off
			//-> A surplus resp. deficit tree is cut off
			if (leaving[0] == 0) {
				if (tree.branchOfLeavingEdge)
					surTrees.remove(Integer.valueOf(leaving[1]));
				else
					defTrees.remove(Integer.valueOf(leaving[1]));
			}
			//Reset for next iteration
			defTarIt.nextIt();

			//Helping
			//Testing

			//curStart = endTime;
			//iteration++;
			return true;
		}

	}

	public boolean isExact() {
		return isExact;
	}

	public void printFinalFlow() {
		// Each tree edge corresponds to a certain flow (possibly 0) but some flow > 0 implies
		// there will be an edge in the tree
		// We cover all edges by investigating each node and its parent
		for (int n = 1; n < cSrc + cTar + 1; n++) {
			int parent = tree.getParent(n);
			// Ignore the artificial node (index 0) 
			if (parent > 0) {
				double flow = tree.getFlow(n);
				System.out.println(String.format("Edge between %d and %d with flow %f", n - 1, parent - 1, flow));
			}
		}
	}

	public void fillMatrix(ReallocationMatrix relocationMatrix) {
		// Each tree edge corresponds to a certain flow (possibly 0) but some flow > 0 implies
		// there will be an edge in the tree
		// We cover all edges by investigating each node and its parent
		int fTar = cSrc + 1; // First target node index (after all source nodes and artificial node)
		for (int n = 1; n < cSrc + cTar + 1; n++) {
			int parent = tree.getParent(n);
			// Ignore the artificial node (index 0) 
			if (parent > 0) {
				double flow = tree.getFlow(n);
				int indexA, indexB;
				
				if(n < fTar) {
					indexA = n - 1;
					indexB = parent - fTar;
				}
				else {
					indexA = parent - 1;
					indexB = n - fTar;
				}
				relocationMatrix.set(indexA, indexB, flow);
			}
		}
	}
}
