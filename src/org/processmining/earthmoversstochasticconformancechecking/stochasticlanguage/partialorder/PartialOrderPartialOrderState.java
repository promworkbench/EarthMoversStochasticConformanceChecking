package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder;

import java.util.BitSet;
import java.util.Comparator;

public class PartialOrderPartialOrderState implements Cloneable {

	public static final Comparator<PartialOrderPartialOrderState> comparator = new Comparator<PartialOrderPartialOrderState>() {
		public int compare(PartialOrderPartialOrderState o1, PartialOrderPartialOrderState o2) {

			// States are ordered in ascending heuristic's value order
			// Smaller -> Better
			int res = Integer.compare(o1.f, o2.f);
			if (res == 0) {
				// If heuristics value is equal, prefer the one that already did edit operations
				// and not the one which is simply expecting some
				// Larger -> Better
				res = -1 * Integer.compare(o1.g, o2.g);
				if (res == 0) {
					// If heuristic and current edit value are the same prefer deeper states
					// Larger -> Better
					res = -1 * Integer.compare(o1.depth, o2.depth);
				}
			}
			return res;
		}
	};

	public BitSet stateA; //events executed
	public BitSet stateB; //events executed

	public int f;
	public int g;

	public PartialOrderPartialOrderState parent;

	/**
	 * Depth of the state in the A* search tree
	 */
	public int depth;

	@Override
	public PartialOrderPartialOrderState clone() {
		PartialOrderPartialOrderState result;
		try {
			result = (PartialOrderPartialOrderState) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		result.stateA = (BitSet) stateA.clone();
		result.stateB = (BitSet) stateB.clone();
		result.depth = depth;
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stateA == null) ? 0 : stateA.hashCode());
		result = prime * result + ((stateB == null) ? 0 : stateB.hashCode());
		result = prime * result + ((stateA == null) ? 0 : stateA.hashCode());
		result = prime * result + ((stateB == null) ? 0 : stateB.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// IMPORTANT: Equality only tests on state equality. 
		// It does not consider depth even though compareTo does!
		// For A*, the identification of already explored states should only depend on the state of the replay. 
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PartialOrderPartialOrderState other = (PartialOrderPartialOrderState) obj;
		if (stateA == null) {
			if (other.stateA != null) {
				return false;
			}
		} else if (!stateA.equals(other.stateA)) {
			return false;
		}
		if (stateB == null) {
			if (other.stateB != null) {
				return false;
			}
		} else if (!stateB.equals(other.stateB)) {
			return false;
		}
		return true;
	}
}