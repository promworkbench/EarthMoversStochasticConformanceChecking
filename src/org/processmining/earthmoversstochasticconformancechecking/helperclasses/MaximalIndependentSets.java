package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import java.util.ArrayList;
import java.util.BitSet;

import org.processmining.earthmoversstochasticconformancechecking.helperclasses.StochasticPetriNetUtils.PetriNetCache;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

public class MaximalIndependentSets {

	public static ArrayList<int[]> BronKerbosch2(StochasticLabelledPetriNet net, BitSet enabledTransitions,
			PetriNetCache neighbourCache) {
		ArrayList<int[]> result = new ArrayList<>();
		BitSet P = new BitSet();
		for (int transition = enabledTransitions.nextSetBit(0); transition >= 0; transition = enabledTransitions
				.nextSetBit(transition + 1)) {
			P.set(transition);
		}
		BronKerbosch2(net, enabledTransitions, new BitSet(), P, new BitSet(), result, neighbourCache);
		return result;
	}

	public static void BronKerbosch2(StochasticLabelledPetriNet net, BitSet enabledTransitions, BitSet R, BitSet P,
			BitSet X, ArrayList<int[]> result, PetriNetCache neighbourCache) {
		if (P.isEmpty() && X.isEmpty()) {
			//if P and X are both empty then
			//report R as a maximal clique
			int[] set = new int[R.cardinality()];
			int i = 0;
			for (int v = R.nextSetBit(0); v >= 0; v = R.nextSetBit(v + 1)) {
				set[i] = v;
				i++;
			}
			result.add(set);
		} else {
			//choose a pivot vertex u in P ⋃ X
			int u = Math.max(P.nextSetBit(0), X.nextSetBit(0));

			//for each vertex v in P \ N(u) do
			for (int v = P.nextSetBit(0); v >= 0; v = P.nextSetBit(v + 1)) {

				if (u == v || StochasticPetriNetUtils.isNeighbour(net, v, u, neighbourCache)) {

					BitSet Nv = StochasticPetriNetUtils.getNonNeighbours(net, v, enabledTransitions, neighbourCache);

					//clone and recurse
					//BronKerbosch2(R ⋃ {v}, P ⋂ N(v), X ⋂ N(v))
					BitSet Rn = (BitSet) R.clone();
					Rn.set(v);
					BitSet Pn = (BitSet) P.clone();
					Pn.and(Nv);
					BitSet Xn = (BitSet) X.clone();
					Xn.and(Nv);

					BronKerbosch2(net, enabledTransitions, Rn, Pn, Xn, result, neighbourCache);

					//P := P \ {v}
					P.clear(v);

					//X := X ⋃ {v}
					X.set(v);
				}

				if (v == Integer.MAX_VALUE) {
					break;
				}
			}
		}
	}

}