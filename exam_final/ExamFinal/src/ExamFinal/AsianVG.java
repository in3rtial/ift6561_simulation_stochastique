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
   final double drift;
   final double volatility;
   final double nu;
   final double mu;
   final double w;
   final double r;

   // Array zeta[0..s] must contain zeta[0]=0.0, plus the s observation times.
   public AsianVG (double r, // short rate
		   		   double drift, // drift of the BM (theta or mu)
		   		   double volatility, // volatility of BM (sigma)
		   		   double nu, // variance of the VG (nu)
		   		   double K,  // K price
		   		   double s0, // initial price
		   		   int s, // number of observations
		   		   double[] zeta // observation times
		   		   )
   {
      this.K = K;
      this.s0 = s0;
      this.s = s;
      this.zeta = zeta;
      this.discount = Math.exp (-r * zeta[s]);
      this.volatility = volatility;
      this.mu = 1;
      this.drift = drift;
      this.nu = nu;
      this.r = r;
      this.w = Math.log(1 - 
    		   (drift*nu) -
    		   (volatility*volatility*nu/2.0))/nu;
      System.out.println("w ="+this.w);
   }

  	/**
	 * Computes and returns the discounted option payoff.
	 * 
	 * @return payoff of the option
	 */
	public double getPayoff(double[] path) {
		double average = 0.0; // Average of the process
		for(int i = 1; i <= this.s; i++)
		{
			double St = path[i];
			average += Math.exp(St);
		}

		average /= s;
		//System.out.println(average);
		if (average > this.K)
			return this.discount * (average - this.K);
		else
			return 0.0;
	}


	
   public void BGSS(int n, MRG32k3a prng, Tally stats)
 {
		// generate sequentially (BGSS)

		VarianceGammaProcess process = new VarianceGammaProcess(s0, drift,
				volatility, nu, prng);
		process.setObservationTimes(this.zeta, this.s);

		for (int i = 0; i < n; i++) {

			process.resetStartProcess();
			process.generatePath();
			for (int j = 0; j < process.getPath().length; j++)
				// System.out.println(process.getPath()[i]);

				stats.add(getPayoff(process.getPath()));

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

		double r = 0.1; // short rate of 10%
		double theta = 0; // drift of BM (mu)
		double sigma = 0.12136; // volatility of BM (sigma)
		double nu = 0.5; // variance of the VG
		double mu = 1.;
		double K = 101; // K
		double s0 = 100; // s0
		int T = 1000;
		MRG32k3a prng = new MRG32k3a();

		// E[X(t)] = theta*t
		int s = 16;
		double[] zeta = new double[s + 1];
		zeta[0] = 0.0;
		for (int j = 1; j <= s; j++)
			zeta[j] = ((double) j / (double) s) * (double) T;

		for(double z: zeta)
			System.out.println(z);
		
		GammaProcess gamma = new GammaProcess(0., mu, nu, prng);
		gamma.setObservationTimes(zeta, zeta.length-1);
		
		for(int i=0; i<100; i++)
			prng.resetNextSubstream();
			
		for(int i=0; i<10; i++)
		{
			double[] a = gamma.generatePath();
			printA(a);
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

	}
	
	public static void printA(double[] arr)
	{
		System.out.print("[");
		for(int i = 0; i < arr.length-1; i++)
			System.out.print(arr[i]+", ");
		System.out.print(arr[arr.length-1]+"]\n");
	}


	public static void testGamma()
	{
		// imitate gamma process
		
		double delta = 1./16.;
		double s_0 = 0.;
		double nu=0.3;
		double mu=1.;
		double first = (delta * mu * mu )/ nu;
		GammaDist a = new GammaDist(first, mu/nu);
		MRG32k3a prng = new MRG32k3a();
		
		for(int i = 0; i < 100; i++)
		{
			System.out.println(a.inverseF(prng.nextDouble()));
		}
		
	}



	public static void main(String[] args) {
		testGamma();
		//testVGProcess();
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