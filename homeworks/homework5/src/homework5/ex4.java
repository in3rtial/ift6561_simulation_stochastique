package homework5;

import cern.colt.list.DoubleArrayList;
import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.stat.TallyStore;
import umontreal.iro.lecuyer.util.PrintfFormat;
import umontreal.iro.lecuyer.probdist.*;

public class ex4 {




	

	
/*
	private double simulateCV(int numRuns, int numPilotRuns) {
		
		
		
		
		// Exact variance on the number of expected calls
		// Var[A] = E[Var[A|B]] + Var[E[A|B]] = E[aB] + Var[aB].
		double exactVarA = a * meanB + a * a * varianceB;
		
		println(" meanB = " + f(meanB) + ",  varianceB = "
				+ PrintfFormat.f(9, 7, varianceB));
		println(" a = " + f(a) + ",  Exact Var[a] = " + f(exactVarA));

		// Initialize the internal tallies
		this.statArrivals.init();
		this.statWaits.init();
		this.statGoodQoS.init();
		this.statAbandon.init();
		
		// Pilot runs to estimate the variances and covariances for beta.
		for (int i = 0; i < numPilotRuns; i++)
			simulateOneDay(true, true);

		// calculate the pilot stats
		double meanApilot = statArrivals.average();
		double varApilot = statArrivals.variance();
		double meanXpilot = statGoodQoS.average();
		double varXpilot = statGoodQoS.variance();
		double covarAXpilot = statArrivals.covariance(statGoodQoS);
		double meanDpilot = meanApilot - meanXpilot;
		double varDpilot = varApilot + varXpilot - 2.0 * covarAXpilot;
		double covarADpilot = varApilot - covarAXpilot;
		

		println("\n\n==== PILOT RUN RESULTS ====");
		println("Mean(Arrivals) = " + f(meanApilot) + ",  Var(Arrivals) = " + f(varApilot));
		println("Covar(A,X) = " + f(covarAXpilot));
		println("Covar(A,D) = " + f(covarADpilot));
		println();


		// Perform crude simulations to estimate means and variances.
		this.statArrivals.init();
		this.statWaits.init();
		this.statGoodQoS.init();
		this.statAbandon.init();
		
		// Crude runs
		for (int i = 0; i < numRuns; i++)
			simulateOneDay(true, true);

		// Empirical means and variances.
		double meanA = statArrivals.average();
		double varA = statArrivals.variance();
		double meanX = statGoodQoS.average();
		double varX = statGoodQoS.variance();
		double varMC = varX; // Variance with crude MC.
		double covarAX = statArrivals.covariance(statGoodQoS);
		double meanD = meanA - meanX;
		double varD = varA + varX - 2.0 * covarAX;
		// Note: cov(A,A-X) = var(A) - cov(A,X).
		double covarAD = varA - covarAX;
		double betaVC; // Coefficient for VC.

		DoubleArrayList A = statArrivals.getDoubleArrayList();
		DoubleArrayList X = statGoodQoS.getDoubleArrayList();
		
		// get the indirect estimator
		TallyStore statBadService = new TallyStore(numRuns);
		for (int i = 0; i < numRuns; i++) {
			statBadService.add(A.get(i) - X.get(i));
		}
		DoubleArrayList D = statBadService.getDoubleArrayList();

		println("\n==== CRUDE ESTIMATOR RESULTS ====");
		println("meanA = " + f(meanA) + ",   Var(A) = " + f(varA));
		println("meanX = " + f(meanX) + ",   Var(X) = " + f(varX));
		println("meanD = " + f(meanD) + ",   Var(D) = " + f(varD));
		println("Covar(A,X) = " + f(covarAX));
		println("Covar(A,D) = " + f(covarAD));
		println();

		println("\n==== INDIRECT ESTIMATOR RESULTS ====");
		println("meanX = " + f(a - meanD) + ",   Var(D) = " + f(varD)
				+ ",   ratio = " + f(varD / varMC));
		println();
		/*
		A = statArrivals.getDoubleArrayList();
		X = statGoodQoS.getDoubleArrayList();
		D = statBadService.getDoubleArrayList();
		
		println("\n**** Control Variate A with exact variance of A, no pilot runs ****");
		println("Exact value of Var(A) = " + f(exactVarA));
		betaVC = covarAX / exactVarA;
		println("beta = covar(A,X) / exactVarA = " + f(betaVC));
		computeCICV(A, X, betaVC, varMC);

		println("\n**** Control Variate A with var(A) estimated, no pilot runs ****");
		betaVC = covarAX / varA;
		println("beta = covar(A,X) / VarA = " + f(betaVC));
		computeCICV(A, X, betaVC, varMC);

		println("\n**** Indirect + Control variate, exact variance of A, no pilot runs ****");
		betaVC = covarAD / exactVarA;
		println("beta = covar(A,D) / exactVarA = " + f(betaVC));
		computeCICV(A, D, betaVC, varMC);

		println("\n**** Indirect + Control variate, estimated var(A), no pilot runs ****");
		betaVC = covarAD / varA;
		println("beta = covar(A,D) / VarA = " + f(betaVC));
		computeCICV(A, X, betaVC, varMC);

		println("\n**** Control Variate A with exact variance of A, pilot runs ****");
		betaVC = covarAXpilot / exactVarA;
		println("beta = covar(A,X) / exactVarA = " + f(betaVC));
		computeCICV(A, X, betaVC, varMC);

		println("\n**** Control Variate A with estimated variance of A, pilot runs ****");
		betaVC = covarAXpilot / varApilot;
		println("beta = covar(A,X) / VarA = " + f(betaVC));
		computeCICV(A, X, betaVC, varMC);

		println("\n**** Indirect + Control variate, exact variance of A, pilot runs ****");
		betaVC = covarADpilot / exactVarA;
		println("beta = covar(A,D) / exactVarA = " + f(betaVC)
				+ " from pilot runs");
		computeCICV(A, D, betaVC, varMC);

		println("\n**** Indirect + Control variate, estimated var(A), pilot runs ****");
		betaVC = covarADpilot / varApilot;
		println("beta = covar(A,D) / varA = " + f(betaVC) + " from pilot runs");
		computeCICV(A, D, betaVC, varMC);

		
		return varMC;
		
	}
	*/
	
	
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

		/* the control variate suggested in example 6.18 is:
		 * to use the standard European option (i.e. with barrier = -Inf)
		 */
		
	
		double sigma = 0.2;
		double r = 0.08;
		double s_0 = 100.;
		double  T = 1;
		double K = 100.;
		int s=10;
		
		double[] barriers = new double[]{80, 90, 95};
		
		
		
		MRG32k3a prng = new MRG32k3a();
		
		System.out.println("Original settings\n");
		//simulate(0, 10000, prng, r, sigma, K, s_0, T);
		prng.resetStartSubstream();
		
		/*
		simulate(Double.NEGATIVE_INFINITY, 10000, prng, r, sigma, K, s_0, T);
		prng.resetStartSubstream();
		
		/*
		simulate(90, 10000, prng);
		prng.resetStartSubstream();
		simulate(95, 10000, prng);
		prng.resetStartSubstream();
		
		System.out.println("With CV");
		*/
	}

}
