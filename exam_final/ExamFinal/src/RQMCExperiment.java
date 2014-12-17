import umontreal.iro.lecuyer.hups.PointSet;
import umontreal.iro.lecuyer.hups.PointSetIterator;
import umontreal.iro.lecuyer.hups.PointSetRandomization;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.util.Chrono;

/**
 * Provides generic tools to perform RQMC experiments
 * with a simulation model that implements the MonteCarloModel interface.
 */

/**
 * @author Pierre L'Ecuyer
 * 
 */
public class RQMCExperiment extends MonteCarloExperiment {

	// Makes m independent randomizations of the RQMC point set p using
	// the randomization r and stream noise.
	// For each of them, performs one simulation run for each point
	// of p, and adds the average over these points to the collector statQMC.
	public static void simulateRQMC(MonteCarloModel model, int m, PointSet p,
			PointSetRandomization rand, RandomStream noise, Tally statRQMC) {
		Tally statValue = new Tally("stat on value");
		statRQMC.init();
		PointSetIterator stream = p.iterator();
		for (int j = 0; j < m; j++) {
			rand.randomize(p);
			stream.resetStartStream();
			simulateRuns(model, p.getNumPoints(), stream, statValue);
			statRQMC.add(statValue.average());
		}
	}

	public static void simulateRQMCDefaultReport(MonteCarloModel model, int m,
			PointSet p, PointSetRandomization rand, RandomStream noise,
			Tally statRQMC) {
		Chrono timer = new Chrono();
		simulateRQMC(model, m, p, rand, noise, statRQMC);
		statRQMC.setConfidenceIntervalStudent();
		System.out.println(model.toString());
		System.out.println(p.toString());
		System.out.println(rand.toString());
		System.out.println(statRQMC.report(0.95, 4));
		System.out.println("Total CPU time:      " + timer.format() + "\n");
	}

	public static void simulateRQMCDefaultReportCompare(MonteCarloModel model,
			int m, PointSet p, PointSetRandomization rand, RandomStream noise,
			Tally statRQMC, double varianceMC, double secondsMC) {
		Chrono timer = new Chrono();
		simulateRQMC(model, m, p, rand, noise, statRQMC);
		double secondsRQMC = timer.getSeconds() / (m * p.getNumPoints());
		double varianceRQMC = p.getNumPoints() * statRQMC.variance();
		statRQMC.setConfidenceIntervalStudent();
		System.out.println(model.toString());
		System.out.println(p.toString());
		System.out.println(rand.toString());
		System.out.println(statRQMC.report(0.95, 4));
		System.out.println("Total CPU time:      " + timer.format() + "\n");
		System.out.printf("Variance per run: %9.4g%n", varianceRQMC);
		System.out.printf("Variance ratio:   %9.4g%n", varianceMC
				/ varianceRQMC);
		System.out.printf("Efficiency ratio: %9.4g%n", (varianceMC * secondsMC)
				/ (varianceRQMC * secondsRQMC));
		System.out.println("-------------------------------------------\n");
	}
}
