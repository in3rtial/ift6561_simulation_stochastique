import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.util.Chrono;

/**
 * Provides generic tools to perform Monte Carlo experiments
 * with a simulation model that implements the MonteCarloModel interface.
 */

/**
 * @author Pierre L'Ecuyer
 * 
 */
public class MonteCarloExperiment {

	// Performs n indep. runs using stream and collects statistics in statValue.
	public static void simulateRuns(MonteCarloModel model, int n,
			RandomStream stream, Tally statValue) {
		statValue.init();
		for (int i = 0; i < n; i++) {
			model.simulate(stream);
			statValue.add(model.getValue());
			stream.resetNextSubstream();
		}
	}

	// Performs n indep. runs using stream, collects statistics in statValue,
	// and prints a report with a confidence interval.
	public static void simulateRunsDefaultReport(MonteCarloModel model, int n,
			RandomStream stream, Tally statValue) {
		Chrono timer = new Chrono();
		simulateRuns(model, n, stream, statValue);
		statValue.setConfidenceIntervalStudent();
		System.out.println(model.toString());
		System.out.println(statValue.report(0.95, 4));
		System.out
				.printf("Variance per run: %9.5g%n", statValue.variance() * n);
		System.out.println("Total CPU time:      " + timer.format() + "\n");
	}

	public static void simulateRunsDefaultReport(MonteCarloModel model, int n,
			RandomStream stream, Tally statValue, Chrono timer) {
		timer.init();
		simulateRuns(model, n, stream, statValue);
		statValue.setConfidenceIntervalStudent();
		System.out.println(model.toString());
		System.out.println(statValue.report(0.95, 4));
		System.out
				.printf("Variance per run: %9.5g%n", statValue.variance() * n);
		System.out.println("Total CPU time:      " + timer.format() + "\n");
	}
}
