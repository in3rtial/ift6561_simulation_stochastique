package homework_3;

import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.*;


public class Exercise1 {
	
	
	/**
	 * Clever estimator for X (doesn't sample useless regions)
	 * @param a
	 * @param b
	 * @param K
	 * @param n number of samples to take
	 * @param prng pseudo random number generator
	 * @param tallyName name of the statistical collector
	 * @return tally of the observations
	 */
	public static Tally simulateIS(double a, double b, double K,
									   double n,
									   RandomStream prng,
									   String tallyName)
	{

		Distribution stdNorm = new NormalDist(0, 1);
		Tally stats = new Tally(tallyName);
		stats.setConfidenceIntervalNormal();
		
		for(int i = 0; i < n; i++)
		{
			double V1 = prng.nextDouble();
			double U1 = stdNorm.cdf(a - 1) * V1;
			double Y1 = 1 + stdNorm.inverseF(U1);
			
			double V2 = prng.nextDouble();
			double U2 = stdNorm.cdf(b - 1 - Y1) + ((1 - stdNorm.cdf(b - 1 - Y1)) * V2);
			double Y2 = 1 + stdNorm.inverseF(U2);
			
			double X = Y1 + Y2 - K;
			double multiplier = stdNorm.cdf(a - 1) * (1 - stdNorm.cdf((b - 1 - Y1)));
			double Xis = X * multiplier;
			
			stats.add(Xis);
		}
		return stats;
	}
	
	/**
	 * Crude Monte Carlo Estimator (will sample in useless places)
	 * @param a
	 * @param b
	 * @param K
	 * @param n number of samples
	 * @param dist1 first distribution
	 * @param dist2 second distribution
	 * @param prng pseudo random number generator
	 * @param tallyName name of the statistical collector
	 * @return tally of the observations
	 */
	public static Tally simulateDumb(double a, double b, double K,
						      double n,
							  RandomStream prng,
							  String tallyName)
	{
		Distribution dist1 = new NormalDist(1.0, 1.0);
		Distribution dist2 = new NormalDist(1.0, 1.0);
		Tally stats = new Tally(tallyName);
		stats.setConfidenceIntervalNormal();
		for(int i = 0 ; i < n; i++)
		{
			double Y1 = dist1.inverseF(prng.nextDouble());
			double Y2 = dist2.inverseF(prng.nextDouble());
			if( Y1 <= a && Y1 + Y2 >= b)
			{
				stats.add(Y1 + Y2 - K);
			}
			else
			{
				stats.add(0);
			}
		}
		
		return stats;
	}
	
	public static void runExperiment(int n, double a, double b, double K)
	{
		MRG32k3a prng = new MRG32k3a();
		System.out.println(simulateDumb(a, b, K, n, prng, "CRUDE {" + a + ", "+ b + ", " + K+"}").report());
		System.out.println(simulateIS(a, b, K, n, prng, "IS {" + a + ", "+ b + ", " + K+"}").report());
	}
	
	public static void main(String[] args)
	{
		//
		// but with K = 1 and a = 0, and with b = 2, 3, and 4. Discuss your results.
		
		//testExample(100000);
		
		// setup the distributions


		int n = 100000;
		// K = 1, a = 0, b = 2
		runExperiment(n, 0, 2, 1);
		
		// K = 1, a = 0, b = 3
		runExperiment(n, 0, 3, 1);
		
		// K = 1, a = 0, b = 4
		runExperiment(n, 0, 4, 1);
	}
}
