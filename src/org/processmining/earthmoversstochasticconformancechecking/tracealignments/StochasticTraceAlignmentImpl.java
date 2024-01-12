package org.processmining.earthmoversstochasticconformancechecking.tracealignments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition;

public class StochasticTraceAlignmentImpl<L, M> implements StochasticTraceAlignment<L, M> {

	List<L> logMoves = new ArrayList<>();
	List<M> modelMoves = new ArrayList<>();

	public int getNumberOfMoves() {
		return logMoves.size();
	}

	public L getMoveA(int indexOfMove) {
		return logMoves.get(indexOfMove);
	}

	public M getMoveB(int indexOfMove) {
		return modelMoves.get(indexOfMove);
	}

	public void add(L logMove, M modelMove) {
		logMoves.add(logMove);
		modelMoves.add(modelMove);
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		for (int move = 0; move < modelMoves.size(); move++) {
			L logMove = logMoves.get(move);
			M modelMove = modelMoves.get(move);

			if (logMove != null && modelMove != null) {
				//sync move
				result.append("s " + logMove);
			} else if (logMove != null) {
				//log move
				result.append("l " + logMove);
			} else {
				//model move
				result.append("m " + modelMove.toString());
			}

			if (move < modelMoves.size() - 1) {
				result.append(", ");
			}
		}

		return result.toString();
	}

	public String toHTMLString() {
		StringBuilder result = new StringBuilder();
		for (int move = 0; move < modelMoves.size(); move++) {
			L logMove = logMoves.get(move);
			M modelMove = modelMoves.get(move);

			if (logMove != null && modelMove != null) {
				//sync move
				result.append("<td></td><td>" + logMove + "</td>");
			} else if (logMove != null) {
				//log move
				if (logMove instanceof StochasticTransition && ((StochasticTransition) logMove).isInvisible()) {
					result.append("<td></td>[tau]</td>");
				} else if (logMove instanceof StochasticTransition) {
					result.append("<td>l</td>" + ((StochasticTransition) logMove).getLabel() + "</td>");
				} else {
					result.append("<td>l</td>" + logMove.toString() + "</td>");
				}
			} else {
				//model move
				if (modelMove instanceof StochasticTransition && ((StochasticTransition) modelMove).isInvisible()) {
					result.append("<td></td>[tau]</td>");
				} else if (modelMove instanceof StochasticTransition) {
					result.append("<td>m</td>" + ((StochasticTransition) modelMove).getLabel() + "</td>");
				} else {
					result.append("<td>m</td>" + modelMove.toString() + "</td>");
				}
			}
		}

		return result.toString();
	}

	public void reverse() {
		Collections.reverse(logMoves);
		Collections.reverse(modelMoves);
	}

}
