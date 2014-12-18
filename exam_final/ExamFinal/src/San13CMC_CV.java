import java.io.IOException;

import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.rng.LFSR113;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.stat.TallyStore;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

public class San13CMC_CV extends San13CMC {

	TallyStore[] Ys;  // tallies to save control variates
	int[] CVs;  // indices of the control variates
	
	public San13CMC_CV(double x, String fileName) throws IOException {
		super(x, fileName);
		this.Ys = new TallyStore[8];
		this.CVs = new int[] { 0, 1, 2, 3, 7, 10, 11, 12 };
		for(int i = 0; i < Ys.length; i++)
		{
			this.Ys[i] = new TallyStore("V"+this.CVs[i]);
		}
	}
	
	
	// add the tally system
	public void simulate(RandomStream stream)
	{
		for (int k = 0; k < 13; k++)
		{
			V[k] = dist[k].inverseF(stream.nextDouble());
			if (V[k] < 0.0)
			{
				V[k] = 0.0;
			}
		}
		
		// add the control variates to the tally
		for(int i = 0; i < this.CVs.length; i++)
		{
			this.Ys[i].add(V[CVs[i]]);
		}
			
		
		double prod = 1.0;

		// Incomplete path lengths
		paths[0] = V[1] + V[10];
		paths[1] = V[0] + V[2] + V[11];
		if (paths[0] > paths[1])
			paths[1] = paths[0];
		
		prod *= dist[5].cdf(x - paths[1]);
		paths[2] = V[0] + V[10];
		
		prod *= dist[4].cdf(x - paths[2]);
		paths[3] = V[0] + V[3] + V[7] + V[10];
		
		prod *= dist[9].cdf(x - paths[3]);
		paths[4] = V[0] + V[3] + V[7] + V[12];
		
		prod *= dist[8].cdf(x - paths[4]);
		paths[5] = V[0] + V[3] + V[11] + V[12];
		
		prod *= dist[6].cdf(x - paths[5]);
		estimate = 1.0 - prod;
	}
	
	public static double studentConfInterval95(double var, int num) {
		double level = 0.95;
		double z = NormalDist.inverseF01(0.5D * (level + 1.0D));
		return (z * Math.sqrt(var / num));
	}
	
	static Algebra alg = new Algebra();

	/**
	 * Applies a vector of control variables to an estimator. The tally x
	 * contains the observations used to estimate the output average while the
	 * array of tallies c contains the observations of the control variates. The
	 * third array, ec, contains the known expectations of the control variates.
	 * The length c and ec should be equal while x, and c should contain the
	 * same number of observations.
	 *
	 * This method estimates the optimal vector of constants for the control
	 * variates, and prints information on the vector, and the variance with CV.
	 * 
	 * @param x
	 *            the tally containing the observations for the output value.
	 * @param c
	 *            the tally containing the observations of the control variates.
	 * @param ec
	 *            the vector of expectations.
	 */
	public static void applyCV(TallyStore x, TallyStore[] c, double[] ec) {
		// Construct and fill the matrix Cov[C]
		DoubleMatrix2D matC = new DenseDoubleMatrix2D(c.length, c.length);
		for (int i = 0; i < c.length; i++)
			matC.setQuick(i, i, c[i].variance());
		for (int i = 0; i < c.length - 1; i++)
			for (int j = i + 1; j < c.length; j++) {
				double cov = c[i].covariance(c[j]);
				matC.setQuick(i, j, cov);
				matC.setQuick(j, i, cov);
			}
		// Construct and fill the vector Cov[C, X]
		DoubleMatrix2D matCX = new DenseDoubleMatrix2D(c.length, 1);
		for (int i = 0; i < c.length; i++)
			matCX.setQuick(i, 0, x.covariance(c[i]));

		System.out.print(x.getName());
		System.out.println(" with CV:");
		for (TallyStore tally : c) {
			System.out.print("   ");
			System.out.println(tally.getName());
		}
		DoubleMatrix2D mbeta;
		try {
			// Find Beta vector solving Cov[C, X] = Cov[C]*Beta
			mbeta = alg.solve(matC, matCX);
		} catch (IllegalArgumentException iae) {
			// This can happen, e.g., if the variance of a CV is (incorrectly)
			// 0.
			System.out.println("Cannot apply CV");
			System.out.println();
			return;
		}

		// Compute average Xc = X - Beta^t*(C - E[C])
		double avgWithCV = x.average();
		for (int i = 0; i < c.length; i++)
			avgWithCV -= mbeta.getQuick(i, 0) * (c[i].average() - ec[i]);
		// Compute variance Var[Xc] = Var[X] + Beta^t*Var[C]*Beta - 2Beta*Cov[C,
		// X]
		double varWithCV = x.variance();
		// viewDice transposes the matrix mbeta (which contains a single
		// column),
		// and zMult performs the matrix multiplication.
		// The null second argument instructs Colt to create a new matrix for
		// the
		// result.
		// The result of the operation is a 1x1 matrix from which we extract the
		// single element;
		// this is the second term of the controlled variance.
		varWithCV += mbeta.viewDice().zMult(matC, null).zMult(mbeta, null)
				.getQuick(0, 0);
		// A similar technique is used to compute the third term of the
		// controlled variance.
		varWithCV -= 2 * mbeta.viewDice().zMult(matCX, null).getQuick(0, 0);

		// Print some information
		System.out.print("Beta vector with CV                        : (");
		for (int i = 0; i < c.length; i++)
			System.out
					.printf("%s%.3g", i > 0 ? ", " : "", mbeta.getQuick(i, 0));
		System.out.println(")");
		System.out.printf(
				"Average without CV                         : %8.5g%n",
				x.average());
		System.out.printf(
				"Average with CV                            : %8.5g%n",
				avgWithCV);
		System.out.printf(
				"Variance without CV                        : %8.5g%n",
				x.variance());
		System.out.printf(
				"Variance with CV                           : %8.5g%n",
				varWithCV);
		System.out.printf(
				"Variance reduction factor                  : %8.5g%n",
				x.variance() / varWithCV);
	      
	      // compute the confidence intervals with and without CV
	      double c_n = x.average();
	      double i_n = studentConfInterval95(x.variance(), x.numberObs());
	      double i_cv = studentConfInterval95(varWithCV, x.numberObs());
	      System.out.printf("Confidence interval student 95 without CV  : %8.5g \u00B1 %8.5g\n",c_n, i_n );
	      System.out.printf("Confidence interval student 95 with CV     : %8.5g \u00B1 %8.5g\n",avgWithCV , i_cv);
	      System.out.println();
	}

	public static void main(String[] args) throws IOException {
		// CV was used as such
		//applyCV (cc.statGoodQoS, new TallyStore[] { cc.statArrivals, cc.statServiceTimes },
	    //           new double[] { cc.nCallsExpected, cc.genServ.getDistribution().getMean() });
		int n = 10000;
		San13CMC_CV san = new San13CMC_CV(90.0, "san13a.dat");
		MRG32k3a prng = new MRG32k3a();
		
		// first part, the main results
		TallyStore Observations = new TallyStore("P[T > x]");
		MonteCarloExperiment.simulateRunsDefaultReport(san, n, prng, Observations);
	
		// second part, the control variates
		TallyStore[] ControlVariates = san.Ys;
		
		// third part, the expected values of the distributions
		double[] Expectations = new double[san.CVs.length];
		for(int i = 0 ; i < Expectations.length; i++)
		{
			Expectations[i] = san.dist[san.CVs[i]].getMean();
		}
		applyCV(Observations, ControlVariates, Expectations);
		
		
	}
}
