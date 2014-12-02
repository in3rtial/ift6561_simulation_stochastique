package homework5;

import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.stat.TallyStore;

public class ex4 {


	private static void simulateCV(int numRuns, int numPilotRuns, double barrier) {
		
		
		// we use without barrier as a CV
		double r = 0.08;     // short rate
		double sigma = 0.2;  // volatility
		double K = 100.;     // strike price
		double s_0 = 100.;   // initial price
		double T = 1;        // total time difference
		int s=10;            // number of evenly spaced observations
		EuropeanCallOption option = new EuropeanCallOption(r, sigma, K, s_0, T, s);
		
		
		long[] seed = new long[]{ 1, 12345, 12345, 12345, 12345, 12345 };
		MRG32k3a prng = new MRG32k3a();
		prng.setSeed(seed);
		
		
		
		// PILOT RUNS ---------------------------------------------------------------
		double betaVC; // Coefficient for VC.
		
		// generate the baseline (without barrier)
		TallyStore pilotStd = new TallyStore();
		pilotStd.init();
		prng.resetStartSubstream();
		
		option.simulateRunsStd(numPilotRuns, prng, pilotStd);
		
		// generate the runs with the barrier
		TallyStore pilotBarrier = new TallyStore();
		pilotBarrier.init();
		prng.resetStartSubstream();
		
		option.simulateRunsDownAndOut(numPilotRuns, barrier, prng, pilotBarrier);

		
		// fit betaVC
		// betaVC = COV[X, Y] / Var[X]
		double varX_pilot = pilotBarrier.variance();
		double covXY_pilot = pilotStd.covariance(pilotBarrier);
		betaVC = covXY_pilot / varX_pilot;

		
		
		// Perform the real runs

		TallyStore realStd = new TallyStore();
		pilotStd.init();
		prng.resetStartSubstream();
		
		option.simulateRunsStd(numRuns, prng, realStd);
		
		// generate the runs with the barrier
		TallyStore realBarrier = new TallyStore();
		pilotBarrier.init();
		//double[] barrierResult = new double[numPilotRuns];
		prng.resetStartSubstream();
		
		option.simulateRunsDownAndOut(numRuns, barrier, prng, realBarrier);
		//arrayCopy(barrierResult, statsBarrier.getArray());
		
		
		// Empirical means and variances.
		double meanY = realStd.average();
		double varY = realStd.variance();
		double meanX = realBarrier.average();
		double varX = realBarrier.variance();
		double varCrude = varX; // Variance with crude MC.
		double covarXY = realStd.covariance(realBarrier);
		double meanDiff = meanY - meanX;
		double varDiff = varY + varX - 2.0 * covarXY;
		// Note: cov(Y,Y-X) = var(Y) - cov(Y,X).
		double covarYD = varY - covarXY;		
		
		
		System.out.println("==== CRUDE ESTIMATOR RESULTS ("+barrier+") ====================");
		System.out.println("beta =  " + betaVC);
		System.out.println("meanY = " + meanY + ",   Var(Y) = " + varY);
		System.out.println("meanX = " + meanX + ",   Var(X) = " + varX);
		System.out.println("meanD = " + meanDiff + ",   Var(D) = " + varDiff);
		System.out.println("Covar(Y,X) = " + covarXY);
		System.out.println("Covar(Y,D) = " + covarYD);
		

		System.out.println("\n==== CV RESULTS ("+barrier+") ====================");
		System.out.println("meanX = " + (meanX - betaVC*(meanX - option.getExpectedValue())));
		System.out.println("Var(D) = " + varDiff + "\nratio = " + (varDiff / varCrude));
		System.out.println("\n\n");		
		}

	
	public static void main(String[] args) {
		/*
		 * STATEMENT
		 * You are asked to try the control variate suggested in Example 6.18.
		 * Estimate the variance reduction factor compared with naive Monte
		 * Carlo by performing n = 10000 simulation runs with the following
		 * parameters: σ = 0.2, r = 0.08, S(0) = 100, T = 1, K = 100, d = 10,
		 * and t j = j/10 for j = 1, . . . , d. For the barrier , try = 80, 90,
		 * and 95. Discuss your results. Can you suggest other ways of reducing
		 * the variance for this example?
		 */

		int numRuns = 10000;
		int numPilotRuns = 1000;
		double[] barriers = new double[]{0, 75, 80, 90, 95};
		for(int i = 0; i < barriers.length; i++)
			simulateCV(numRuns, numPilotRuns, barriers[i]);
		
		
	}

	
}
