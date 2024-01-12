package org.processmining.earthmoversstochasticconformancechecking.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.StochasticNet2StochasticLabelledPetriNet;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParameters;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModel;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersModelModel;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa.ComputeReallocationMatrix2;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguageUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPetriNet2StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.ComputeStochasticTraceAlignments;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogLog;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsModelModel;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

public class EarthMoversStochasticConformancePlugin {
	@Plugin(name = "Compute Earth-movers' stochastic conformance (log-log)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = {
					StochasticTraceAlignmentsLogLog.class }, parameterLabels = { "Event Log",
							"Event log" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for two logs.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public StochasticTraceAlignmentsLogLog convert(final UIPluginContext context, XLog logA, XLog logB)
			throws InterruptedException {
		EMSCDialogLogLog dialog = new EMSCDialogLogLog(logA, logB);
		InteractionResult result = context.showWizard("Earth-movers' stochastic conformance", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		EMSCParametersLogLog parameters = dialog.getParameters();

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		//		long start = System.currentTimeMillis();
		StochasticTraceAlignmentsLogLog result2 = measureLogLog(logA, logB, parameters, canceller);
		//		long duration = System.currentTimeMillis() - start;
		//		System.out.println(duration);
		//		System.out.println(result2.getSimilarity());

		if (result2 == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		} else {
			return result2;
		}
	}

	@Plugin(name = "Compute Earth-movers' stochastic conformance (log-model-ARS)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = {
					StochasticTraceAlignmentsLogModel.class }, parameterLabels = { "Event Log",
							"Stochastic Petri net" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for a log and a stochastic Petri net.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public StochasticTraceAlignmentsLogModel convert(final UIPluginContext context, XLog log, StochasticNet sNet)
			throws InterruptedException {
		Marking initialMarking = getInitialMarking(sNet);
		StochasticLabelledPetriNet net = StochasticNet2StochasticLabelledPetriNet.convert(sNet, initialMarking);
		return convert(context, log, net);
	}

	@Plugin(name = "Compute Earth-movers' stochastic conformance (log-model)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = {
					StochasticTraceAlignmentsLogModel.class }, parameterLabels = { "Event Log",
							"Stochastic labelled Petri net" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for a log and a stochastic labelled Petri net.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public StochasticTraceAlignmentsLogModel convert(final UIPluginContext context, XLog log,
			StochasticLabelledPetriNet net) throws InterruptedException {
		EMSCDialogLogModel dialog = new EMSCDialogLogModel(log);
		InteractionResult result = context.showWizard("Earth-movers' stochastic conformance", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		EMSCParametersLogModel parameters = dialog.getParameters();

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		StochasticTraceAlignmentsLogModel result2 = measureLogModel(log, net, parameters, canceller);

		if (result2 == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		} else {
			return result2;
		}
	}

	@Plugin(name = "Compute Earth-movers' stochastic conformance (model-ARS-model-ARS)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = {
					StochasticTraceAlignmentsLogModel.class }, parameterLabels = { "Stochastic Petri net",
							"Stochastic Petri net" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for two stochastic Petri nets.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public StochasticTraceAlignmentsModelModel convert(final UIPluginContext context, StochasticNet sNetA,
			StochasticNet sNetB) throws InterruptedException {
		Marking initialMarkingA = getInitialMarking(sNetA);
		Marking initialMarkingB = getInitialMarking(sNetB);
		StochasticLabelledPetriNet netA = StochasticNet2StochasticLabelledPetriNet.convert(sNetA, initialMarkingA);
		StochasticLabelledPetriNet netB = StochasticNet2StochasticLabelledPetriNet.convert(sNetB, initialMarkingB);
		return convert(context, netA, netB);
	}

	@Plugin(name = "Compute Earth-movers' stochastic conformance (model-model)", level = PluginLevel.Regular, returnLabels = {
			"Stochastic trace alignments" }, returnTypes = {
					StochasticTraceAlignmentsLogModel.class }, parameterLabels = { "Stochastic labelled Petri net",
							"Stochastic labelled Petri net" }, userAccessible = true, help = "Compute the Earth-movers' stochastic conformance checking for two stochastic labelled Petri nets.<br>This will compare them on (1) which activities were executed in which order, and (2) how often a particular order of activities was executed (EMSC/tEMSC).")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public StochasticTraceAlignmentsModelModel convert(final UIPluginContext context, StochasticLabelledPetriNet netA,
			StochasticLabelledPetriNet netB) throws InterruptedException {
		EMSCDialogModelModel dialog = new EMSCDialogModelModel();
		InteractionResult result = context.showWizard("Earth-movers' stochastic conformance", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		EMSCParametersModelModel parameters = dialog.getParameters();

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		StochasticTraceAlignmentsModelModel result2 = measureModelModel(netA, netB, parameters, canceller);

		if (result2 == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		} else {
			return result2;
		}
	}

	public static StochasticTraceAlignmentsLogLog measureLogLog(XLog logA, XLog logB, EMSCParametersLogLog parameters,
			ProMCanceller canceller) throws InterruptedException {

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(logA, parameters.getClassifierA());
		activityKey.feed(logB, parameters.getClassifierB());

		debug(parameters, "Create stochastic language A..");

		StochasticLanguage<TotalOrder> languageLogA = XLog2StochasticLanguage.convert(logA, parameters.getClassifierA(),
				activityKey, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, " size: " + languageLogA.size());

		debug(parameters, "Create stochastic language B..");

		StochasticLanguage<TotalOrder> languageLogB = XLog2StochasticLanguage.convert(logB, parameters.getClassifierA(),
				activityKey, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, " size: " + languageLogB.size());

		debug(parameters, "Compute reallocation matrix..");

		Triple<ReallocationMatrix, Double, DistanceMatrix<TotalOrder, TotalOrder>> r = compute(parameters, languageLogA,
				languageLogB, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		ReallocationMatrix reallocationMatrix = r.getA();
		double similarity = r.getB();

		StochasticTraceAlignmentsLogLog traceAlignments;
		if (parameters.isComputeStochasticTraceAlignments() & reallocationMatrix != null) {
			debug(parameters, "Create stochastic trace alignments..");
			traceAlignments = ComputeStochasticTraceAlignments.computeLogLog(reallocationMatrix, similarity,
					languageLogA, languageLogB);
		} else {
			debug(parameters, "Stochastic trace alignments disabled..");
			traceAlignments = ComputeStochasticTraceAlignments.emptyLogLog(reallocationMatrix, similarity, languageLogA,
					languageLogB);
		}

		debug(parameters, "EMSC done..");

		return traceAlignments;
	}

	public static StochasticTraceAlignmentsLogModel measureLogModel(XLog log, StochasticNet net, Marking initialMarking,
			EMSCParametersLogModel parameters, ProMCanceller canceller) throws InterruptedException {
		StochasticLabelledPetriNet sNet = StochasticNet2StochasticLabelledPetriNet.convert(net, initialMarking);
		return measureLogModel(log, sNet, parameters, canceller);
	}

	public static StochasticTraceAlignmentsLogModel measureLogModel(XLog log, StochasticLabelledPetriNet net,
			EMSCParametersLogModel parameters, ProMCanceller canceller) throws InterruptedException {

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(log, parameters.getClassifierA());

		debug(parameters, "Create stochastic language A..");

		StochasticLanguage<TotalOrder> languageLog = XLog2StochasticLanguage.convert(log, parameters.getClassifierA(),
				activityKey, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, " size: " + languageLog.size());

		debug(parameters, "Create stochastic language B..");

		StochasticPathLanguage<TotalOrder> languageModel = StochasticPetriNet2StochasticPathLanguage.convert(net,
				parameters.getTerminationStrategyB(), activityKey, parameters.getNumberOfThreads(), canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, " size: " + languageModel.size());

		debug(parameters, "Compute reallocation matrix..");

		Triple<ReallocationMatrix, Double, DistanceMatrix<TotalOrder, TotalOrder>> r = compute(parameters, languageLog,
				languageModel, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		ReallocationMatrix reallocationMatrix = r.getA();
		double similarity = r.getB();

		StochasticTraceAlignmentsLogModel traceAlignments;
		if (parameters.isComputeStochasticTraceAlignments() && reallocationMatrix != null) {
			debug(parameters, "Create stochastic trace alignments..");
			traceAlignments = ComputeStochasticTraceAlignments.computeLogModel(reallocationMatrix, similarity,
					languageLog, languageModel, net);
		} else {
			debug(parameters, "Stochastic trace alignments disabled..");
			traceAlignments = ComputeStochasticTraceAlignments.emptyLogModel(reallocationMatrix, similarity,
					languageLog, languageModel, net);
		}

		debug(parameters, "EMSC done..");

		return traceAlignments;
	}

	public static StochasticTraceAlignmentsModelModel measureModelModel(StochasticNet netA, Marking initialMarkingA,
			StochasticNet netB, Marking initialMarkingB, EMSCParametersModelModel parameters, ProMCanceller canceller)
			throws InterruptedException {

		StochasticLabelledPetriNet sNetA = StochasticNet2StochasticLabelledPetriNet.convert(netA, initialMarkingA);
		StochasticLabelledPetriNet sNetB = StochasticNet2StochasticLabelledPetriNet.convert(netB, initialMarkingB);

		return measureModelModel(sNetA, sNetB, parameters, canceller);
	}

	public static StochasticTraceAlignmentsModelModel measureModelModel(StochasticLabelledPetriNet netA,
			StochasticLabelledPetriNet netB, EMSCParametersModelModel parameters, ProMCanceller canceller)
			throws InterruptedException {

		Activity2IndexKey activityKey = new Activity2IndexKey();

		debug(parameters, "Create stochastic language A..");

		StochasticPathLanguage<TotalOrder> languageA = StochasticPetriNet2StochasticPathLanguage.convert(netA,
				parameters.getTerminationStrategyA(), activityKey, parameters.getNumberOfThreads(), canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, "Create stochastic language B..");

		StochasticPathLanguage<TotalOrder> languageB = StochasticPetriNet2StochasticPathLanguage.convert(netB,
				parameters.getTerminationStrategyB(), activityKey, parameters.getNumberOfThreads(), canceller);

		//update 06-06-2021: ensure the largest language comes first
		{
			if (StochasticLanguageUtils.getSumProbability(languageA) < StochasticLanguageUtils
					.getSumProbability(languageB)) {
				StochasticPathLanguage<TotalOrder> languageC = languageA;
				languageA = languageB;
				languageB = languageC;
				StochasticLabelledPetriNet netC = netA;
				netA = netB;
				netB = netC;
			}
		}

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, "Compute reallocation matrix..");

		Triple<ReallocationMatrix, Double, DistanceMatrix<TotalOrder, TotalOrder>> q = compute(parameters, languageA,
				languageB, canceller);

		//update 06-06-2021: adjust value to 1
		{
			double minDist = getMinimumDistance(languageA, languageB, q.getC());
			q = Triple.of(q.getA(), q.getB() + (1 - StochasticLanguageUtils.getSumProbability(languageA)) * minDist,
					q.getC());
		}

		if (canceller.isCancelled()) {
			return null;
		}

		ReallocationMatrix reallocationMatrix = q.getA();
		double similarity = q.getB();

		StochasticTraceAlignmentsModelModel traceAlignments;
		if (parameters.isComputeStochasticTraceAlignments() && reallocationMatrix != null) {
			debug(parameters, "Create stochastic trace alignments..");
			traceAlignments = ComputeStochasticTraceAlignments.computeModelModel(reallocationMatrix, similarity,
					languageA, languageB, netA, netB);
		} else {
			debug(parameters, "Stochastic trace alignments disabled..");
			traceAlignments = ComputeStochasticTraceAlignments.emptyModelModel(reallocationMatrix, similarity,
					languageA, languageB, netA, netB);
		}

		debug(parameters, "EMSC done..");

		return traceAlignments;
	}

	private static double getMinimumDistance(StochasticPathLanguage<TotalOrder> languageA,
			StochasticPathLanguage<TotalOrder> languageB, DistanceMatrix<TotalOrder, TotalOrder> d) {
		double minDist = Double.MAX_VALUE;
		for (int l = 0; l < languageA.size(); l++) {
			for (int m = 0; m < languageB.size(); m++) {
				minDist = Math.min(minDist, d.getDistance(l, m));
			}
		}
		return minDist;
	}

	public static Triple<ReallocationMatrix, Double, DistanceMatrix<TotalOrder, TotalOrder>> compute(
			EMSCParameters<TotalOrder, TotalOrder> parameters, StochasticLanguage<TotalOrder> languageA,
			StochasticLanguage<TotalOrder> languageB, ProMCanceller canceller) throws InterruptedException {
		if (languageA.size() > 0 && languageB.size() > 0) {
			return ComputeReallocationMatrix2.compute(languageA, languageB, parameters.getDistanceMatrix(), parameters,
					canceller);
		} else {
			if (languageA.size() == 0 && languageB.size() == 0) {
				return Triple.of(null, 1.0, null);
			} else {
				return Triple.of(null, 0.0, null);
			}
		}
	}

	public static Marking getInitialMarking(StochasticNet net) {
		//search for an initial marking
		Marking initialMarking = new Marking();
		for (Place place : net.getPlaces()) {
			if (net.getInEdges(place).isEmpty()) {
				initialMarking.add(place);
			}
		}
		return initialMarking;
	}

	public static void debug(EMSCParameters<?, ?> parameters, Object object) {
		if (parameters.isDebug()) {
			System.out.println(object.toString());
		}
	}
}