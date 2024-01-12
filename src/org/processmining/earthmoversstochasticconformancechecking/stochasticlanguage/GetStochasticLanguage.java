package org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersLogModelPartial;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersModelModelPartial;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.StochasticPetrinet2StochasticLanguagePartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.XLog2StochasticLanguagePartialOrderEqualTimestamp;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

/**
 * Yes, this is the ugliest code you've ever seen. I want generics and type
 * safety to ensure the right distance matrix is always applied. To keep things
 * general in the plug-ins, this ugly type casting is necessary.
 * 
 * @author sander
 *
 */
public class GetStochasticLanguage {
	@SuppressWarnings("unchecked")
	public static <A extends Order> StochasticLanguage<A> getA(XLog log, EMSCParametersLogModelPartial<A, ?> parameters,
			Activity2IndexKey activityKey, ProMCanceller canceller) {
		if (parameters.getOrderA() instanceof TotalOrder) {
			StochasticLanguage<TotalOrder> languageLog = XLog2StochasticLanguage.convert(log,
					parameters.getClassifierA(), activityKey, canceller);
			return (StochasticLanguage<A>) languageLog;
		} else if (parameters.getOrderA() instanceof PartialOrderUncertain) {
			StochasticLanguage<PartialOrderUncertain> languageLog = XLog2StochasticLanguagePartialOrderEqualTimestamp
					.convert(log, parameters.getClassifierA(), activityKey, canceller);
			return (StochasticLanguage<A>) languageLog;
		}
		assert false;
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <A extends PartialOrder> StochasticPathLanguage<A> getA(StochasticLabelledPetriNet net,
			Activity2IndexKey activityKey, EMSCParametersModelModelPartial<A, ?> parameters, ProMCanceller canceller) {
		return (StochasticPathLanguage<A>) StochasticPetrinet2StochasticLanguagePartialOrder.convert(net, activityKey,
				parameters.getGenerationStrategyA(), canceller);

	}

	@SuppressWarnings("unchecked")
	public static <B extends PartialOrder> StochasticPathLanguage<B> getB(StochasticLabelledPetriNet net,
			Activity2IndexKey activityKey, EMSCParametersLogModelPartial<?, B> parameters, ProMCanceller canceller) {
		return (StochasticPathLanguage<B>) StochasticPetrinet2StochasticLanguagePartialOrder.convert(net, activityKey,
				parameters.getGenerationStrategyB(), canceller);
	}

	@SuppressWarnings("unchecked")
	public static <B extends PartialOrder> StochasticPathLanguage<B> getB(StochasticLabelledPetriNet net,
			Activity2IndexKey activityKey, EMSCParametersModelModelPartial<?, B> parameters, ProMCanceller canceller) {
		return (StochasticPathLanguage<B>) StochasticPetrinet2StochasticLanguagePartialOrder.convert(net, activityKey,
				parameters.getGenerationStrategyB(), canceller);
	}
}
