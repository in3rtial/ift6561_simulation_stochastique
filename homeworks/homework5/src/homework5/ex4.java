package homework5;

import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.probdist.*;

public class ex4 {


	public static void simulate(double barrier, int n, RandomStream prng) {
		int s = 10;
		// choose the observation times
		double[] observationTimes = new double[s + 1];
		observationTimes[0] = 0.0;
		for (int j = 1; j <= s; j++)
			observationTimes[j] = (double) j / (double) s;
		AsianDownAndOut process = new AsianDownAndOut(0.08, 0.2, 100.0, 100.0, s,
				observationTimes, barrier);
		Tally statValue = new Tally("Asian down-and-out call option with barrier "+barrier);

		process.simulateRuns(n, prng, statValue);
		statValue.setConfidenceIntervalStudent();
		System.out.println(statValue.report(0.95, 3));
	}
	
	
	
	
	public static void main(String[] args) {
		// STATEMENT
		/*
		 * You are asked to try the control variate suggested in Example 6.18.
		 * Estimate the variance reduction factor compared with naive Monte
		 * Carlo by performing n = 10000 simulation runs with the following
		 * parameters: σ = 0.2, r = 0.08, S(0) = 100, T = 1, K = 100, d = 10,
		 * and t j = j/10 for j = 1, . . . , d. For the barrier , try = 80, 90,
		 * and 95. Discuss your results. Can you suggest other ways of reducing
		 * the variance for this example?
		 */

		// down-and-out call option with barrier l
		// X = exp(-rT)max(0, S(T)
		MRG32k3a prng = new MRG32k3a();
		System.out.println("Original settings\n");
		simulate(80, 10000, prng);
		prng.resetStartSubstream();
		simulate(90, 10000, prng);
		prng.resetStartSubstream();
		simulate(95, 10000, prng);
		prng.resetStartSubstream();
		
		System.out.println("With CV");
	}

}