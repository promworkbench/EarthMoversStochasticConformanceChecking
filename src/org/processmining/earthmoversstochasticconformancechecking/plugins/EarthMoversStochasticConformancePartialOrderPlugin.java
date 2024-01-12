package org.processmining.earthmoversstochasticconformancechecking.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.TransposedDistanceMatrixWrapper;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.StochasticNet2StochasticLabelledPetriNet;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParameters;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersBounds;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersLogModelPartial;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersLogModelPartialAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersModelModelPartial;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersModelModelPartialAbstract;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa.ComputeReallocationMatrix2;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.GetStochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Order;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguageUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

public class EarthMoversStochasticConformancePartialOrderPlugin {
	@Plugin(name = "Compute Earth-movers' stochastic conformance on partial orders (log-model-ARS)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = { EMSCPartialOrderResult.class }, parameterLabels = {
					"Event Log",
					"Stochastic Petri net" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for a log and a stochastic Petri net.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public EMSCPartialOrderResult convert(final UIPluginContext context, XLog log, StochasticNet sNet)
			throws InterruptedException {
		Marking initialMarking = EarthMoversStochasticConformancePlugin.getInitialMarking(sNet);
		StochasticLabelledPetriNet net = StochasticNet2StochasticLabelledPetriNet.convert(sNet, initialMarking);
		return convert(context, log, net);
	}

	@Plugin(name = "Compute Earth-movers' stochastic conformance on partial orders (log-model)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = { EMSCPartialOrderResult.class }, parameterLabels = {
					"Event Log",
					"Stochastic Petri net" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for a log and a stochastic Petri net.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public EMSCPartialOrderResult convert(final UIPluginContext context, XLog log, StochasticLabelledPetriNet net)
			throws InterruptedException {
		EMSCDialogLogModelPartialOrder dialog = new EMSCDialogLogModelPartialOrder(log);
		InteractionResult result = context.showWizard("Earth-movers' stochastic conformance", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		EMSCParametersLogModelPartialAbstract<?, ?> parameters = dialog.getParameters();

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		Pair<Double, Double> r = measureLogModel(log, net, parameters, canceller);

		if (r == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		EMSCPartialOrderResult result2 = new EMSCPartialOrderResult(r.getA(), r.getB());
		return result2;
	}

	@Plugin(name = "Compute Earth-movers' stochastic conformance on partial orders (model-ARS-model-ARS)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = { EMSCPartialOrderResult.class }, parameterLabels = {
					"Stochastic Petri net A",
					"Stochastic Petri net B" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for two stochastic Petri nets.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public EMSCPartialOrderResult convert(final UIPluginContext context, StochasticNet sNetA, StochasticNet sNetB)
			throws InterruptedException {
		Marking initialMarkingA = EarthMoversStochasticConformancePlugin.getInitialMarking(sNetA);
		StochasticLabelledPetriNet netA = StochasticNet2StochasticLabelledPetriNet.convert(sNetA, initialMarkingA);
		Marking initialMarkingB = EarthMoversStochasticConformancePlugin.getInitialMarking(sNetB);
		StochasticLabelledPetriNet netB = StochasticNet2StochasticLabelledPetriNet.convert(sNetB, initialMarkingB);
		return convert(context, netA, netB);
	}

	@Plugin(name = "Compute Earth-movers' stochastic conformance on partial orders (model-model)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = { EMSCPartialOrderResult.class }, parameterLabels = {
					"Stochastic labelled Petri net A",
					"Stochastic labelled Petri net B" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for two stochastic labelled Petri nets.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public EMSCPartialOrderResult convert(final UIPluginContext context, StochasticLabelledPetriNet netA,
			StochasticLabelledPetriNet netB) throws InterruptedException {
		EMSCDialogModelModelPartialOrder dialog = new EMSCDialogModelModelPartialOrder();
		InteractionResult result = context.showWizard("Earth-movers' stochastic conformance", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		EMSCParametersModelModelPartialAbstract<?, ?> parameters = dialog.getParameters();

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		Pair<Double, Double> r = measureModelModel(netA, netB, parameters, canceller);

		if (r == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		EMSCPartialOrderResult result2 = new EMSCPartialOrderResult(r.getA(), r.getB());
		return result2;
	}

	public static <A extends Order, B extends PartialOrder> Pair<Double, Double> measureLogModel(XLog log,
			StochasticLabelledPetriNet net, EMSCParametersLogModelPartial<A, B> parameters, ProMCanceller canceller)
			throws InterruptedException {

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(log, parameters.getClassifierA());
		activityKey.feed(net);

		debug(parameters, "Create stochastic language A..");

		StochasticLanguage<A> languageLog = GetStochasticLanguage.getA(log, parameters, activityKey, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, " size: " + languageLog.size());
		debug(parameters, " probability mass: " + StochasticLanguageUtils.getSumProbability(languageLog));

		debug(parameters, "Create stochastic language B..");

		StochasticPathLanguage<B> languageModel = GetStochasticLanguage.getB(net, activityKey, parameters, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, " size: " + languageModel.size());
		debug(parameters, " probability mass: " + StochasticLanguageUtils.getSumProbability(languageModel));

		Pair<Double, Double> r = compute(parameters, languageLog, languageModel, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, "EMSC done..");

		return Pair.of(r.getA(), r.getB());
	}

	public static <A extends PartialOrder, B extends PartialOrder> Pair<Double, Double> measureModelModel(
			StochasticLabelledPetriNet netA, StochasticLabelledPetriNet netB,
			EMSCParametersModelModelPartial<A, B> parameters, ProMCanceller canceller) throws InterruptedException {

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(netA);
		activityKey.feed(netB);

		debug(parameters, "Create stochastic language A..");

		StochasticPathLanguage<A> languageA = GetStochasticLanguage.getA(netA, activityKey, parameters, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, " size: " + languageA.size());
		debug(parameters, " probability mass: " + StochasticLanguageUtils.getSumProbability(languageA));

		debug(parameters, "Create stochastic language B..");

		StochasticPathLanguage<B> languageB = GetStochasticLanguage.getB(netB, activityKey, parameters, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, " size: " + languageB.size());
		debug(parameters, " probability mass: " + StochasticLanguageUtils.getSumProbability(languageB));

		Pair<Double, Double> r = compute(parameters, languageA, languageB, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, "EMSC done..");

		return Pair.of(r.getA(), r.getB());
	}

	public static <A extends Order, B extends PartialOrder> Pair<Double, Double> compute(
			EMSCParametersBounds<A, B> parameters, StochasticLanguage<A> languageA, StochasticLanguage<B> languageB,
			ProMCanceller canceller) throws InterruptedException {
		if (languageA.size() > 0 && languageB.size() > 0) {

			double truncatedMass = 1 - StochasticLanguageUtils.getSumProbability(languageB);

			if (parameters.getDistanceMatrix() == null) {
				//this is a one-matrix problem.
				double higher = compute(parameters, languageA, languageB, parameters.getDistanceMatrixBest(),
						canceller);

				if (canceller.isCancelled() || Double.isNaN(higher)) {
					return null;
				}

				double lower = higher - truncatedMass;

				return Pair.of(lower, higher);
			} else {
				//this is a two-matrix problem.
				double higher = compute(parameters, languageA, languageB, parameters.getDistanceMatrixBest(),
						canceller);

				if (canceller.isCancelled() || Double.isNaN(higher)) {
					return null;
				}

				double lower = compute(parameters, languageA, languageB, parameters.getDistanceMatrix(), canceller)
						- truncatedMass;

				if (canceller.isCancelled() || Double.isNaN(higher)) {
					return null;
				}

				return Pair.of(lower, higher);
			}
		} else {
			if (languageA.size() == 0 && languageB.size() == 0) {
				return Pair.of(null, 1.0);
			} else {
				return Pair.of(null, 0.0);
			}
		}
	}

	public static <B extends PartialOrder, A extends Order> double compute(EMSCParametersBounds<A, B> parameters,
			StochasticLanguage<A> languageA, StochasticLanguage<B> languageB, DistanceMatrix<A, B> distanceMatrix,
			ProMCanceller canceller) throws InterruptedException {

		debug(parameters, " compute distance matrix of size " + languageA.size() + " x " + languageB.size());

		/*
		 * The way our solver works, if we want to reallocate only the minimum
		 * probability mass of both languages, we have to ensure that the
		 * smallest language comes first.
		 */
		if (StochasticLanguageUtils.getSumProbability(languageA) < StochasticLanguageUtils
				.getSumProbability(languageB)) {

			distanceMatrix = distanceMatrix.clone();
			distanceMatrix.init(languageA, languageB, canceller);

			if (canceller.isCancelled()) {
				return Double.NaN;
			}

			Pair<ReallocationMatrix, Double> p = ComputeReallocationMatrix2
					.computeWithDistanceMatrixInitialised(languageA, languageB, distanceMatrix, parameters, canceller);

			if (canceller.isCancelled() || p == null) {
				return Double.NaN;
			}

			return p.getB();
		} else {
			distanceMatrix = distanceMatrix.clone();
			DistanceMatrix<B, A> transposedDistanceMatrix = new TransposedDistanceMatrixWrapper<>(distanceMatrix);
			transposedDistanceMatrix.init(languageB, languageA, canceller);

			if (canceller.isCancelled()) {
				return Double.NaN;
			}

			Pair<ReallocationMatrix, Double> p = ComputeReallocationMatrix2.computeWithDistanceMatrixInitialised(
					languageB, languageA, transposedDistanceMatrix, parameters, canceller);

			if (canceller.isCancelled() || p == null) {
				return Double.NaN;
			}

			return p.getB();
		}
	}

	public static void debug(EMSCParameters<?, ?> parameters, Object object) {
		if (parameters.isDebug()) {
			System.out.println(object.toString());
		}
	}
}
