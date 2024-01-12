package org.processmining.earthmoversstochasticconformancechecking.tracealignments;

import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
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
public class StochasticTraceAlignmentsLogModel extends StochasticTraceAlignmentsAbstract<String, StochasticTransition> {

	private final StochasticLabelledPetriNet net;
	private final StochasticPathLanguage<TotalOrder> languageB;

	public StochasticTraceAlignmentsLogModel(ReallocationMatrix relocationMatrix, double similarity,
			StochasticLabelledPetriNet net, StochasticLanguage<TotalOrder> languageA,
			StochasticPathLanguage<TotalOrder> languageB) {
		super(relocationMatrix, similarity, languageA, languageB);
		this.net = net;
		this.languageB = languageB;
	}

	public StochasticLabelledPetriNet getStochasticNet() {
		return net;
	}

	@Override
	public EditIteratorLogModel iterator() {
		return new EditIteratorLogModel();
	}

	public class EditIteratorLogModel extends EditIterator {
		public StochasticPath getStochasticPathB() {
			int[] path = languageB.getPath(subIt.getTraceBIndex());
			return languageB.getTransitionKey().getStochasticPath(path);
		}
	}

}