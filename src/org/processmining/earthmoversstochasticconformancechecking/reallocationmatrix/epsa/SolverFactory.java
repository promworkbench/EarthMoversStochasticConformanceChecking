package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.framework.plugin.ProMCanceller;

/**
 * An abstract class that contains basic functionality every class that sets up
 * a solver needs.
 * <p>
 * Building a factory of this type also means to set up a context in which the
 * following solver are build in e.g. a common ground distance and if needed a
 * tolerance for being optimal. Furthermore, common data as a value for
 * additional information to the solver building process is abstracted.
 * 
 * @author brockhoff
 *
 * @param <T>
 *            The type of solver that is constructed.
 */
public abstract class SolverFactory<T> {

	/**
	 * Some additional data for the initialization
	 */
	protected int additionalInfo;
	/**
	 * Ground distance that will be used to compute costs
	 */
	protected DistanceMatrix<?, ?> groundDist;

	/**
	 * Tolerance for the solver to be optimal if not exact anyway
	 */
	private double tol;

	/**
	 * Constructor in case that the solver does not
	 * 
	 * @param groundDist
	 */
	public SolverFactory(DistanceMatrix<?, ?> groundDist) {
		super();
		this.groundDist = groundDist;
		this.tol = 0;
	}

	/**
	 * @param groundDist
	 * @param tol
	 */
	public SolverFactory(DistanceMatrix<?, ?> groundDist, double tol) {
		super();
		this.groundDist = groundDist;
		this.tol = tol;
	}

	/**
	 * 
	 * @param groundDist
	 * @param additionalInfo
	 */
	public SolverFactory(DistanceMatrix<?, ?> groundDist, int additionalInfo) {
		super();
		this.additionalInfo = additionalInfo;
		this.groundDist = groundDist;
		this.tol = 0;
	}

	/**
	 * This constructor is used by IPM.
	 * 
	 * @param additionalInfo
	 * @param groundDist
	 * @param tol
	 * 
	 */
	public SolverFactory(DistanceMatrix<?, ?> groundDist, double tol, int additionalInfo) {
		super();
		this.additionalInfo = additionalInfo;
		this.groundDist = groundDist;
		this.tol = tol;
	}

	/**
	 * Sets up a new solver of the given type.
	 * 
	 * @param s1
	 *            StochasticLanguage "from"
	 * @param s2
	 *            StochasticLanguage "to"
	 * @param costs
	 *            Cost matrix
	 * @param deltaBound
	 *            "DeltaBound" needed for IM initialization
	 */
	public abstract void setupNewSolver(StochasticLanguage<?> s1, StochasticLanguage<?> s2, ProMCanceller canceller);

	/**
	 * 
	 * @return Current solver instance
	 */
	public abstract T getSolver();

	/**
	 * @return the groundDist
	 */
	public DistanceMatrix<?, ?> getGroundDist() {
		return groundDist;
	}

	/**
	 * @return the tol
	 */
	public double getTol() {
		return tol;
	}

	/**
	 * 
	 * @return The additional information stored
	 */
	public int getAdditionalInfo() {
		return additionalInfo;
	}

	/**
	 * Set the additional information.
	 * 
	 * @param additionalInfo
	 *            The additional information value to store
	 */
	public void setAdditionalInfo(int additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

}
