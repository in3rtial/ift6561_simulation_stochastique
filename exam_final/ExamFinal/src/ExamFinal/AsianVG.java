package ExamFinal;

import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.*;
import umontreal.iro.lecuyer.stochprocess.*;

public class AsianVG {
   final double K;
   final int s;
   final double discount;  // discount factor = Math.exp(-r * zeta[s]).
   final double[] zeta;
   final double s0;
   final double sigma;
   final double theta;
   final double nu;
   final double mu;
   final double omega;
   final double r;
   final double logs0;

   /**
    * 
    * @param r short rate
    * @param sigma volatility of BM
    * @param theta drift of BM
    * @param nu variance of Gamma
    * @param K strike price
    * @param s0 initial price
    * @param s number of observations
    * @param zeta observation times
    */
   public AsianVG (double r,
		   		   double sigma,
		   		   double theta,
		   		   double nu,
		   		   double K,
		   		   double s0,
		   		   int s,
		   		   double[] zeta)
   {
      this.K = K;
      this.s0 = s0;
      this.logs0 = Math.log(s0);
      this.s = s;
      this.zeta = zeta;
      assert(zeta[0] == 0.0);
      assert(zeta.length == s+1);
      this.discount = Math.exp (-r * zeta[s]);
      this.sigma = sigma;
      this.mu = 1;
      this.theta = theta;
      this.nu = nu;
      this.r = r;
      this.omega = Math.log(1. - (theta*nu) - ((sigma*sigma*nu)/2.0))/nu;
   }


   /**
    * 
    */
	public double getPayoff(double[] vgPath) {
		double average = 0.0; // Average of the process
		
		for(int i = 1; i <= this.s; i++)
		{
			double t = zeta[i];
			double St = Math.exp(logs0 + vgPath[i] + r*t + omega*t);
			average += St;
		}
		average /= s;
		//System.out.println(average);
		if (average > this.K)
			return this.discount * (average - this.K);
		else
			return 0.0;
	}


	/**
	 * Generate paths using Sequential Sampling
	 * @param n number of samples to generate
	 * @param prng pseudo-random number generator (MRG32k3a ideally)
	 * @param stats tally to keep the results
	 */
	public void BGSS(int n, RandomStream prng, Tally stats, boolean jump) {
		
		// initialize the subprocesses (Gamma, BM)
		GammaProcess gamma = new GammaProcess(0.0, 1.0, nu, prng);
		BrownianMotion bm = new BrownianMotion(0.0, theta, sigma, prng);
		// initialize the main process
		VarianceGammaProcess process = new VarianceGammaProcess(0., bm, gamma);
		
		// set the parameters
		process.setObservationTimes(this.zeta, this.s);
		
		// generate the paths and add the costs to the stats collector
		for (int i = 0; i < n; i++) {
			double[] vgPath = process.generatePath();
			stats.add(getPayoff(vgPath));
			if(jump==true)
				prng.resetNextSubstream();
		}
	}


	/**
	 * Generate paths using Bridge Sampling
	 * @param n
	 * @param prng1
	 * @param prng2
	 * @param stats
	 * @param jump
	 */
	public void BGBS(int n, RandomStream prng, Tally stats, boolean jump) {
		
		// initialize the subprocesses
		GammaProcessBridge gamma = new GammaProcessSymmetricalBridge(0.0, 1.0, nu, prng);
		BrownianMotionBridge bm = new BrownianMotionBridge(0.0, theta, sigma, prng);
		// initialize the main process
		VarianceGammaProcess process = new VarianceGammaProcess(0., bm, gamma);
		
		// set the parameters
		process.setObservationTimes(this.zeta, this.s);

		// generate the paths and add the costs to the stats collector
		for (int i = 0; i < n; i++) {
			double[] vgPath = process.generatePath();
			stats.add(getPayoff(vgPath));
			if(jump==true)
				prng.resetNextSubstream();	
		}
	}

	
	/**
	 * difference of gammas bridge sampling (DGBS)
	 */
	public void DGBS(int n, RandomStream prng, Tally stats, boolean jump) {
		double mu_p = (Math.sqrt(theta*theta + ((2*sigma*sigma)/nu)) + theta)/2.;
		double mu_n = (Math.sqrt(theta*theta + ((2*sigma*sigma)/nu)) - theta)/2.;
		double nu_p = (mu_p * mu_p * nu);
		double nu_n = (mu_n * mu_n * nu);
		
		GammaProcessBridge gammaP = new GammaProcessSymmetricalBridge(0.0, mu_p, nu_p, prng);
		GammaProcessBridge gammaN = new GammaProcessSymmetricalBridge(0.0, mu_n, nu_n, prng);
		VarianceGammaProcessDiff process = new VarianceGammaProcessDiff(0.,
				theta, sigma, nu, gammaP, gammaN);

		process.setObservationTimes(this.zeta, this.s);
		
		for (int i = 0; i < n; i++) {
			double[] vgPath = process.generatePath();
			stats.add(getPayoff(vgPath));
			if(jump==true)
				prng.resetNextSubstream();
		}
	}


	
	public static void testGenerators() {

		// initializtestBGSS
		Tally stats1 = new TallyStore("stat");
		Tally stats2 = new TallyStore("stat");
		Tally stats3 = new TallyStore("stat");
		stats1.setConfidenceIntervalStudent();
		stats2.setConfidenceIntervalStudent();
		stats3.setConfidenceIntervalStudent();

		
		double r = 0.1; // short rate of 10%
		double theta = -0.1436; // drift BM
		double sigma = 0.12136; // volatility of BM
		double nu = 0.3; // variance rate of gamma time change
		double K = 150; // K
		double s0 = 100; // s0
		int T = 1;
		MRG32k3a prng = new MRG32k3a();

		// E[X(t)] = theta*t
		int s = 1;
		double[] zeta = new double[s + 1];
		zeta[0] = 0.0;
		for (int j = 1; j <= s; j++)
			zeta[j] = ((double) j / (double) s) * (double) T;
		System.out.println((Math.sqrt(theta*theta + ((2*sigma*sigma)/nu)) + theta)/2.);
		AsianVG process = new AsianVG(r, sigma, theta, nu, K, s0, s, zeta);
		
		/*
		process.BGSS(10000, prng, stats1, true);
		System.out.println(stats1.report());
		
		process.BGBS(10000, prng, stats2, true);
		System.out.println(stats2.report());
		*/
		process.DGBS(100000, prng, stats3, true);
		System.out.println(stats3.report());
	}

	
	public static void printA(double[] arr)
	{
		System.out.print("[");
		for(int i = 0; i < arr.length-1; i++)
			System.out.print(arr[i]+", ");
		System.out.print(arr[arr.length-1]+"]\n");
	}


	public static void main(String[] args) {
		testGenerators();
		
		

	}
}