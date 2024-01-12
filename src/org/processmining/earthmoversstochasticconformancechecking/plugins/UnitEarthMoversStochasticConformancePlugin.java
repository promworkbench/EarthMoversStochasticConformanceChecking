package org.processmining.earthmoversstochasticconformancechecking.plugins;

import java.math.BigDecimal;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.parameters.uEMSCParameters;
import org.processmining.earthmoversstochasticconformancechecking.parameters.uEMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.uEMSCParametersLogLog;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguageUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;
import org.processmining.stochasticlabelledpetrinets.plugins.StochasticLabelledPetriNetTraceProbability;

import lpsolve.LpSolveException;

public class UnitEarthMoversStochasticConformancePlugin {

	@Plugin(name = "Compute unit Earth-movers' stochastic conformance (log-model)", level = PluginLevel.Regular, returnLabels = {
			"uEMSC value" }, returnTypes = { HTMLToString.class }, parameterLabels = { "Event Log",
					"Stochastic labelled Petri net" }, userAccessible = true, help = "Compute the unit Earth-movers' stochastic conformance checking for a log and a stochastic labelled Petri net.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (uEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute uEMSC, default", requiredParameterLabels = { 0, 1 })
	public HTMLToString convert(final PluginContext context, XLog log, StochasticLabelledPetriNet net)
			throws LpSolveException {

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		uEMSCParameters parameters = new uEMSCParametersDefault();
		final double result = measureLogModel(log, net.getDefaultSemantics(), parameters, canceller);

		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return "uEMSC: " + result;
			}
		};
	}

	/**
	 * The languages must have been constructed using the same activityKey.
	 * 
	 * @param languageA
	 * @param languageB
	 * @param debug
	 * @param canceller
	 * @return
	 */
	public static double measureLogLog(StochasticLanguage<TotalOrder> languageA,
			StochasticLanguage<TotalOrder> languageB, boolean debug, ProMCanceller canceller) {
		debug(debug, "Computing uEMSC..");

		BigDecimal sum = BigDecimal.ZERO;
		for (StochasticTraceIterator<TotalOrder> it = languageA.iterator(); it.hasNext();) {
			int[] traceA = it.next();

			BigDecimal probabilityA = BigDecimal.valueOf(it.getProbability());
			BigDecimal probabilityB = BigDecimal
					.valueOf(StochasticLanguageUtils.getProbability(languageB, traceA, canceller));

			BigDecimal difference = probabilityA.subtract(probabilityB).max(BigDecimal.ZERO);

			sum = sum.add(difference);

			if (canceller.isCancelled()) {
				return Double.NaN;
			}
		}

		debug(debug, "uEMSC done..");

		return BigDecimal.ONE.subtract(sum).doubleValue();
	}

	public static double measureLogLog(XLog logA, XLog logB, uEMSCParametersLogLog parameters,
			ProMCanceller canceller) {

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(logA, parameters.getClassifierA());
		activityKey.feed(logB, parameters.getClassifierA());

		debug(parameters, "Create stochastic language A..");

		StochasticLanguage<TotalOrder> languageA = XLog2StochasticLanguage.convert(logA, parameters.getClassifierA(),
				activityKey, canceller);

		if (canceller.isCancelled()) {
			return Double.NaN;
		}

		debug(parameters, " size: " + languageA.size());

		debug(parameters, "Create stochastic language B..");

		StochasticLanguage<TotalOrder> languageB = XLog2StochasticLanguage.convert(logB, parameters.getClassifierB(),
				activityKey, canceller);

		if (canceller.isCancelled()) {
			return Double.NaN;
		}

		debug(parameters, " size: " + languageB.size());

		return measureLogLog(languageA, languageB, parameters.isDebug(), canceller);
	}

	public static double measureLogModel(XLog log, StochasticLabelledPetriNetSemantics semanticsB,
			uEMSCParameters parameters, ProMCanceller canceller) throws LpSolveException {

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(log, parameters.getClassifierA());

		debug(parameters, "Create stochastic language A..");

		StochasticLanguage<TotalOrder> languageA = XLog2StochasticLanguage.convert(log, parameters.getClassifierA(),
				activityKey, canceller);

		if (canceller.isCancelled()) {
			return Double.NaN;
		}

		debug(parameters, " size: " + languageA.size());

		return measureLogModel(languageA, semanticsB, parameters.isDebug(), canceller);
	}

	public static double measureLogModel(StochasticLanguage<TotalOrder> languageA,
			StochasticLabelledPetriNetSemantics semanticsB, boolean debug, ProMCanceller canceller)
			throws LpSolveException {

		debug(debug, "Computing uEMSC..");

		BigDecimal sum = BigDecimal.ZERO;
		for (StochasticTraceIterator<TotalOrder> it = languageA.iterator(); it.hasNext();) {
			String[] traceA = languageA.getActivityKey().toTraceString(it.next());

			BigDecimal probabilityA = BigDecimal.valueOf(it.getProbability());
			BigDecimal probabilityB = BigDecimal.valueOf(
					StochasticLabelledPetriNetTraceProbability.getTraceProbability(semanticsB, traceA, canceller));

			BigDecimal difference = probabilityA.subtract(probabilityB).max(BigDecimal.ZERO);

			sum = sum.add(difference);

			if (canceller.isCancelled()) {
				return Double.NaN;
			}
		}

		debug(debug, "uEMSC done..");

		return BigDecimal.ONE.subtract(sum).doubleValue();
	}

	public static void debug(boolean debug, Object object) {
		if (debug) {
			System.out.println(object.toString());
		}
	}

	public static void debug(uEMSCParameters parameters, Object object) {
		if (parameters.isDebug()) {
			System.out.println(object.toString());
		}
	}
}