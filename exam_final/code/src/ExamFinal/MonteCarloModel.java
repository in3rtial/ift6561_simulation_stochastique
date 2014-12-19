package ExamFinal;
import umontreal.iro.lecuyer.rng.*;

/**
 * An interface for a simulation model for which Monte Carlo (MC) and RQMC
 * experiments are to be performed. This interface is used by the classes
 * MonteCarloExperiment and RQMCExperiment, among others, to run the model.
 */

public interface MonteCarloModel {

	// Optional
	// public void simulate ();

	// Simulates the model for one run
	public void simulate(RandomStream stream);

	// Recovers the realization of the estimator.
	public double getValue();

	// Returns a short description of the model and its parameters.
	public String toString();

}
