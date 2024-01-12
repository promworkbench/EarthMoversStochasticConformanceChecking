package org.processmining.earthmoversstochasticconformancechecking.tracealignments;

import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPath;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

/**
 * Wrapper class as ProM does not support generics.
 * 
 * @author sander
 *
 */
public class StochasticTraceAlignmentsModelModel
		extends StochasticTraceAlignmentsAbstract<StochasticTransition, StochasticTransition> {

	private final StochasticLabelledPetriNet netA;
	private final StochasticLabelledPetriNet netB;

	private final StochasticPathLanguage<TotalOrder> languageA;
	private final StochasticPathLanguage<TotalOrder> languageB;

	public StochasticTraceAlignmentsModelModel(ReallocationMatrix relocationMatrix, double similarity,
			StochasticLabelledPetriNet netA, StochasticLabelledPetriNet netB,
			StochasticPathLanguage<TotalOrder> languageA, StochasticPathLanguage<TotalOrder> languageB) {
		super(relocationMatrix, similarity, languageA, languageB);
		this.languageA = languageA;
		this.languageB = languageB;
		this.netA = netB;
		this.netB = netB;
	}

	@Override
	public EditIteratorModelModel iterator() {
		return new EditIteratorModelModel();
	}

	public class EditIteratorModelModel extends EditIterator {
		public StochasticPath getStochasticPathA() {
			int[] path = languageA.getPath(now);
			return languageA.getTransitionKey().getStochasticPath(path);
		}

		public StochasticPath getStochasticPathB() {
			int[] path = languageB.getPath(now);
			return languageB.getTransitionKey().getStochasticPath(path);
		}
	}

	public StochasticLabelledPetriNet getStochasticNetA() {
		return netA;
	}

	public StochasticLabelledPetriNet getStochasticNetB() {
		return netB;
	}
}