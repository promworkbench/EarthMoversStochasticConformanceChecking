package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model;

public interface StochasticTransition {

	/**
	 * 
	 * @return the label of this transition, or null if the transition is
	 *         unlabelled
	 */
	public String getLabel();

	public boolean isInvisible();

	/**
	 * @return a number that is unique for this transition. This number must be
	 *         between 0 (inclusive) and the number of transitions in
	 *         consideration (exclusive).
	 */
	public int getIndex();

}
