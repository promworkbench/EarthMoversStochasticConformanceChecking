package org.processmining.earthmoversstochasticconformancechecking.tracealignments;

import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix.ReallocationMatrixIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public abstract class StochasticTraceAlignmentsAbstract<L, M> implements StochasticTraceAlignments<L, M> {
	protected ReallocationMatrix reallocationMatrix;
	protected StochasticTraceAlignment<L, M>[] alignments;
	protected double similarity;
	protected StochasticLanguage<TotalOrder> languageA;
	protected StochasticLanguage<TotalOrder> languageB;

	@SuppressWarnings("unchecked")
	public StochasticTraceAlignmentsAbstract(ReallocationMatrix reallocationMatrix, double similarity,
			StochasticLanguage<TotalOrder> languageA, StochasticLanguage<TotalOrder> languageB) {
		this.reallocationMatrix = reallocationMatrix;
		this.similarity = similarity;
		this.languageA = languageA;
		this.languageB = languageB;

		if (reallocationMatrix != null) {
			alignments = new StochasticTraceAlignment[reallocationMatrix.size()];
		} else {
			alignments = new StochasticTraceAlignment[0];
		}
	}

	public EditIterator iterator() {
		return new EditIterator();
	}

	public class EditIterator implements StochasticTraceAlignmentIterator<L, M> {

		protected final ReallocationMatrixIterator subIt = reallocationMatrix.iterator();
		protected int now = -1;

		public boolean hasNext() {
			return subIt.hasNext();
		}

		public StochasticTraceAlignment<L, M> next() {
			now++;
			subIt.next();
			return alignments[now];
		}

		public void set(StochasticTraceAlignment<L, M> alignment) {
			alignments[now] = alignment;
		}

		public double getProbability() {
			return subIt.getProbability();
		}

		public int getTraceAIndex() {
			return subIt.getTraceAIndex();
		}

		public int getTraceBIndex() {
			return subIt.getTraceBIndex();
		}
	}

	public double getSimilarity() {
		return similarity;
	}

	public StochasticLanguage<TotalOrder> getLanguageA() {
		return languageA;
	}

	public StochasticLanguage<TotalOrder> getLanguageB() {
		return languageB;
	}
}