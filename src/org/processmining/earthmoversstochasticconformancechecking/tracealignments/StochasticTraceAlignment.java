package org.processmining.earthmoversstochasticconformancechecking.tracealignments;

/**
 * 
 * @author sander
 *
 * @param <L>
 *            transition/move of log (language 1)
 * @param <M>
 *            transition/move of model (language 2)
 */
public interface StochasticTraceAlignment<L, M> {

	public int getNumberOfMoves();

	/**
	 * The log move, or null if this move does not contain a log move part.
	 * 
	 * @param indexOfMove
	 * @return
	 */
	public L getMoveA(int indexOfMove);

	/**
	 * The model move, or null if this move does not contain a model move part.
	 * 
	 * @param indexOfMove
	 * @return
	 */
	public M getMoveB(int indexOfMove);
}
