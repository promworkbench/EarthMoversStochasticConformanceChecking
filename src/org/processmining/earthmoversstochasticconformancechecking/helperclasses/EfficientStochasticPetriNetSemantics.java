package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import org.processmining.models.semantics.petrinet.Marking;

public interface EfficientStochasticPetriNetSemantics extends Cloneable {

	/**
	 * Executes (fires) a transition. For performance reasons, this method does
	 * not check whether the transition is actually enabled and does not return
	 * information on what changed.
	 * 
	 * @param transitionIndex
	 */
	public void executeTransition(int transitionIndex);

	/**
	 * 
	 * @param transitionIndex
	 * @return
	 */
	public double getTransitionWeight(int transitionIndex);

	/**
	 * 
	 * @return The transitions that have a probability > 0 to fire.
	 */
	public int[] getEnabledTransitions();

	/**
	 * @return a copy of the underlying array of tokens in the current state.
	 *         (marking).
	 */
	public byte[] getState();

	/**
	 * Sets the state to the supplied state (marking). The state array is copied
	 * into the internal data structure.
	 * 
	 * @param state
	 */
	public void setState(byte[] state);

	/**
	 * allows to convert a given marking to an equivalent int array based on the
	 * internal index representation of the petri net's places.
	 * 
	 * @param marking
	 *            of the petri net used wihtin semantics
	 * @return corresponding int array
	 */
	public byte[] convert(Marking marking);

	/**
	 * convert a primative int array to a marking object based on the internal
	 * index representation of the petri net's places.
	 * 
	 * @param marking
	 *            to transform (int arr)
	 * @return fresh marking object
	 */
	public Marking convert(byte[] marking);

	/**
	 * 
	 * @param transitionIndex
	 * @return
	 */
	public boolean isInvisible(int transitionIndex);

	/**
	 * 
	 * @param transitionIndex
	 * @return
	 */
	public String getLabel(int transitionIndex);

	/**
	 * May create a shallow copy, except the state, which must be deep copied.
	 * 
	 * @return
	 */
	EfficientStochasticPetriNetSemantics clone();

}
