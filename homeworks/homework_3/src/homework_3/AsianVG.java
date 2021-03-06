package homework_3;

import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.stochprocess.BrownianMotion;
import umontreal.iro.lecuyer.stochprocess.GammaProcess;
import umontreal.iro.lecuyer.stochprocess.GammaProcessBridge;
import umontreal.iro.lecuyer.stochprocess.VarianceGammaProcess;
import umontreal.iro.lecuyer.stochprocess.BrownianMotionBridge;
import umontreal.iro.lecuyer.stochprocess.VarianceGammaProcessDiff;

public class AsianVG {
   final double K;    // Strike price.
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
		   		   double K,  // strike price
		   		   double s0, // initial price
		   		   int s, // number of observations
		   		   double[] zeta // observation times
		   		   ) {
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
			double t = zeta[i];
			double St = path[i];
			average += St;
		}

		average /= s;
		System.out.println(average);
		if (average > this.K)
			return this.discount * (average - this.K);
		else
			return 0.0;
	}

	
   public void BGSS(int n,Tally stats)
   {
	   // generate sequentially (BGSS)
	   MRG32k3a gen = new MRG32k3a();
	   VarianceGammaProcess process = new VarianceGammaProcess(s0, drift, volatility, nu, gen);
	   process.setObservationTimes(this.zeta, this.s);

	   for(int i = 0; i < n; i++)
	   {
		   
		   process.resetStartProcess();
		   process.generatePath();
		   for(int j = 0; j < process.getPath().length; j++)
			   System.out.println(process.getPath()[i]);
			   
		   stats.add(getPayoff(process.getPath()));

	   }
   }

   public void BGBS(int n, Tally stats)
   {
	   // bridge sampling (BGBS)
	   MRG32k3a gen = new MRG32k3a();
	   GammaProcessBridge gb = new GammaProcessBridge(this.s0, this.mu, this.nu, gen);
	   BrownianMotionBridge bmb = new BrownianMotionBridge(0.0, this.drift, this.volatility, gen);
	   VarianceGammaProcess process = new VarianceGammaProcess(this.s0, bmb, gb);
	   process.setObservationTimes(this.zeta, this.s);
	   for(int i = 0; i < n; i++)
	   {
		   //process.resetStartProcess();
		   process.generatePath();
		   for(int j = 0; j < process.getPath().length; j++)
			   System.out.println(process.getPath()[i]);
		   stats.add(getPayoff(process.getPath()));
		   gen.resetNextSubstream();
	   }
   }

   public void BGDS(int n, Tally stats)
   {
	   MRG32k3a gen = new MRG32k3a();
	   VarianceGammaProcessDiff process = new VarianceGammaProcessDiff(s0, drift, volatility, nu, gen);
	   process.setObservationTimes(zeta, s);
	   
	   for(int i = 0; i < n; i++)
	   {
		   process.resetStartProcess();
		   process.generatePath();
		   stats.add(getPayoff(process.getPath()));
		   gen.resetNextSubstream();
	   }
   }


   public static void main (String[] args) {

	   // initialize the observation times
	   int s = 16;
      double[] zeta = new double[s+1];   zeta[0] = 0.0;
      for (int j=1; j<=s; j++)
         zeta[j] = (double)j / (double)s;

      // initialize the process
      AsianVG process = new AsianVG(0.1, // short rate of 10%
	    		  					-0.1436, // drift of BM (mu)
	    		  					0.12136, // volatility of BM (sigma)
	    		  					0.3, // variance of the VG
	    		  					101, // K
	    		  					100, // s0
	    		  					s, // s
	    		  					zeta);

      // initialize the tallies
      Tally BGSS = new Tally("BGSS");
      BGSS.setConfidenceIntervalStudent();
      Tally BGBS = new Tally("BGBS");
      BGBS.setConfidenceIntervalStudent();
      Tally BGBS2 = new Tally("BGBS");
      BGBS2.setConfidenceIntervalStudent();
      Tally BGDS = new Tally("BGDS");
      BGDS.setConfidenceIntervalStudent();
      
      int n = 100000;
      
      process.BGSS(1, BGSS);
      process.BGBS(1, BGBS);
      process.BGDS(n, BGDS);

      System.out.println(BGSS.report());
      System.out.println(BGBS.report());
      System.out.println(BGDS.report());
      
      /*
      VarianceGammaProcess G2 = new VarianceGammaProcess(100, -0.1436, // drift of BM (mu)
	    		  					0.12136, // volatility of BM (sigma)
	    		  					0.3, // variance of the VG
	    		  					new MRG32k3a());
      G2.setObservationTimes(zeta, s);
      for(int i = 0; i < 100; i++)
      {
    	  G2.generatePath();
    	  System.out.println(process.getPayoff(G2.getPath()));
      }
      */

   }
}