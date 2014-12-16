package ExamFinal;

import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.*;
import umontreal.iro.lecuyer.stochprocess.BrownianMotion;
import umontreal.iro.lecuyer.stochprocess.GammaProcess;
import umontreal.iro.lecuyer.stochprocess.GammaProcessBridge;
import umontreal.iro.lecuyer.stochprocess.VarianceGammaProcess;
import umontreal.iro.lecuyer.stochprocess.BrownianMotionBridge;
import umontreal.iro.lecuyer.stochprocess.VarianceGammaProcessDiff;

public class AsianVG {
   final double K;    // K price.
   final int s;            // Number of observation times.
   final double discount;  // Discount factor exp(-r * zeta[t]).
   final double[] zeta;
   final double s0;
   final double sigma;
   final double theta;
   final double nu;
   final double mu;
   final double omega;
   final double r;

   
   // Array zeta[0..s] must contain zeta[0]=0.0, plus the s observation times. 
   /**
    * 
    * @param r short rate
    * @param sigma volatility of BM
    * @param theta variance rate of gamma time change
    * @param nu drift of BM
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
      this.s = s;
      this.zeta = zeta;
      assert(zeta[0] == 0.0);
      this.discount = Math.exp (-r * zeta[s]);
      this.sigma = sigma;
      this.mu = 1;
      this.theta = theta;
      this.nu = nu;
      this.r = r;
      this.omega = Math.log(1. - (theta*nu) - ((sigma*sigma*nu)/2.0))/nu;      
      
   }

  	/**
	 * Computes and returns the discounted option payoff.
	 * 
	 * @return payoff of the option
	 */
	public double getPayoff(double[] path) {
		double average = 0.0; // Average of the process
		double logs0 = Math.log(s0);
		for(int i = 1; i <= this.s; i++)
		{
			double St = Math.exp(logs0+path[i]);
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
	 * Generate path and payoff using Brownian Gamma Sequential Sampling
	 * @param n number of samples to generate
	 * @param prng pseudo-random number generator (MRG32k3a ideally)
	 * @param stats tally to keep the results
	 */
	public void BGSS(int n, MRG32k3a prng, TallyStore stats) {
		// Sequential (BGSS), the simplest one
		// uses a variance gamma subordinator to the
		
		//double log_s0 = Math.log(s0);
		VarianceGammaProcess process = new VarianceGammaProcess(0., //s0
																theta, sigma, nu, // gamma process parameters
																prng); // pseudorandom number generator
		process.setObservationTimes(this.zeta, this.s);
		
		for (int i = 0; i < n; i++) {
			// generate the path (s VG and s N(0, 1) generated)
			double[] vg_path = process.generatePath();	
			double[] path = new double[vg_path.length];
			
			for (int j = 0; j < path.length; j++)
			{
				double t = zeta[j];
				path[j] = vg_path[j] + r*t + omega*t;
			}
			stats.add(getPayoff(path));
		}
	}

	/*
	 * public void BGBS(int n, Tally stats) { // bridge sampling (BGBS) MRG32k3a
	 * gen = new MRG32k3a(); GammaProcessBridge gb = new
	 * GammaProcessBridge(this.s0, this.mu, this.nu, gen); BrownianMotionBridge
	 * bmb = new BrownianMotionBridge(0.0, this.drift, this.volatility, gen);
	 * VarianceGammaProcess process = new VarianceGammaProcess(this.s0, bmb,
	 * gb); process.setObservationTimes(this.zeta, this.s); for(int i = 0; i <
	 * n; i++) { //process.resetStartProcess(); process.generatePath(); for(int
	 * j = 0; j < process.getPath().length; j++)
	 * System.out.println(process.getPath()[i]);
	 * stats.add(getPayoff(process.getPath())); gen.resetNextSubstream(); } }
	 * 
	 * public void BGDS(int n, Tally stats) { MRG32k3a gen = new MRG32k3a();
	 * VarianceGammaProcessDiff process = new VarianceGammaProcessDiff(s0,
	 * drift, volatility, nu, gen); process.setObservationTimes(zeta, s);
	 * 
	 * for(int i = 0; i < n; i++) { process.resetStartProcess();
	 * process.generatePath(); stats.add(getPayoff(process.getPath()));
	 * gen.resetNextSubstream(); } }
	 */

	public static void testVGProcess() {

		// initialize the tally
		TallyStore stats = new TallyStore("stat");
		stats.setConfidenceIntervalStudent();

		/*
		 * Variance Gamma parameters
		 * - sigma = volatility BM
		 * - nu = variance rate of gamma time change
		 * - theta = drift BM
		 */
		double r = 0.1; // short rate of 10%
		double theta = -0.1436; // drift BM
		double sigma = 0.12136; // volatility of BM
		double nu = 0.3; // variance rate of gamma time change
		double K = 101; // K
		double s0 = 100; // s0
		int T = 1;
		MRG32k3a prng = new MRG32k3a();

		// E[X(t)] = theta*t
		int s = 16;
		double[] zeta = new double[s + 1];
		zeta[0] = 0.0;
		for (int j = 1; j <= s; j++)
			zeta[j] = ((double) j / (double) s) * (double) T;
		
		printA(zeta);
		
		AsianVG process = new AsianVG(r, sigma, theta, nu, K, s0, s, zeta);
		
		process.BGSS(100000, prng, stats);
		
		System.out.println(stats.report());
		
	}
		/*
		VarianceGammaProcess VGProcess = new VarianceGammaProcess(Math.log(s0), theta,
				sigma, nu, prng);
		VGProcess.setObservationTimes(zeta, zeta.length - 1);
		for (int i = 0; i < 10; i++) {
			double a[] = VGProcess.generatePath();
			double c = a[a.length-1];
			System.out.println("c = "+ c);
			stats.add(c);
			System.out.println(c);
		}
		for(double d: stats.getArray())
			System.out.println(d);
		System.out.println(stats.report());
		*/

	
	public static void printA(double[] arr)
	{
		System.out.print("[");
		for(int i = 0; i < arr.length-1; i++)
			System.out.print(arr[i]+", ");
		System.out.print(arr[arr.length-1]+"]\n");
	}


	public static void main(String[] args) {
		//testGamma();
		testVGProcess();
		/*

		// initialize the observation times
		int s = 16;
		int T=1;
		double[] zeta = new double[s + 1];
		zeta[0] = 0.0;
		for (int j = 1; j <= s; j++)
			zeta[j] = (double) j / (double) T;

		// initialize the Asian VG process
		AsianVG process = new AsianVG(0.1, // short rate of 10%
				-0.1436, // drift of BM (mu)
				0.12136, // volatility of BM (sigma)
				0.3, // variance of the VG
				101, // K
				100, // s0
				s, // s
				zeta);

		// set the experiment parameters
		int n = 100000;


		// System.out.println(BGBS.report());
		// System.out.println(BGDS.report());

		/*
		 * VarianceGammaProcess G2 = new VarianceGammaProcess(100, -0.1436, //
		 * drift of BM (mu) 0.12136, // volatility of BM (sigma) 0.3, //
		 * variance of the VG new MRG32k3a()); G2.setObservationTimes(zeta, s);
		 * for(int i = 0; i < 100; i++) { G2.generatePath();
		 * System.out.println(process.getPayoff(G2.getPath())); }
		 */
		

	}
}