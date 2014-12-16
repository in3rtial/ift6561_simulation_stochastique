/**
 * 
 */
package ExamFinal;

import umontreal.iro.lecuyer.hups.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.*;

/**
 * @author gab
 *
 */
public class AsianVG_RQMC extends AsianVG {

	/**
	 * AsianVG wih Random Quasi Monte Carlo input for path generation
	 * @param r
	 * @param sigma
	 * @param theta
	 * @param nu
	 * @param K
	 * @param s0
	 * @param s
	 * @param zeta
	 */
	public AsianVG_RQMC(double r, double sigma, double theta, double nu,
			double K, double s0, int s, double[] zeta) {
		super(r, sigma, theta, nu, K, s0, s, zeta);
	}
	
	
	/**
	 * Brownian Gamma Sequential Sampling with RQMC
	 * @param m number of experiments
	 * @param pointSet already processed point set (no rng needed)
	 * @param stats statistics collector
	 */
	public void BGSS_RQMC(int m, PointSet pointSet, Tally stats)
	{
		PointSetIterator stream = pointSet.iterator();
		Tally tmp = new Tally();
		for(int j = 0; j < m; j++)
		{
			stream.resetStartStream();
			BGSS(pointSet.getNumPoints(), stream, tmp, true);
			stats.add(tmp.average());
		}
	}
	
	
	/**
	 * 
	 * @param m
	 * @param pointSet
	 * @param stats
	 */
	public void BGBS_RQMC(int m, PointSet pointSet, Tally stats)
	{
		PointSetIterator stream = pointSet.iterator();
		Tally tmp = new Tally();
		for(int j = 0; j < m; j++)
		{
			stream.resetStartStream();
			DGBS(pointSet.getNumPoints(), stream, tmp, true);
			stats.add(tmp.average());
		}
	}
	
	
	public void DGBS_RQMC(int m, PointSet pointSet, Tally stats)
	{
		PointSetIterator stream = pointSet.iterator();
		Tally tmp = new Tally();
		for(int j = 0; j < m; j++)
		{
			stream.resetStartStream();
			DGBS(pointSet.getNumPoints(), stream, tmp, true);
			stats.add(tmp.average());
		}
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
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
		
		AsianVG_RQMC process = new AsianVG_RQMC(r, sigma, theta, nu, K, s0, s, zeta);

		
		// experimental settings
		int m = 10;
		int numExperiments = 8;
		Tally[] allTallies = new Tally[numExperiments];
		
		// INITIALIZE THE POINT SETS
		// strategy 1
		// Sobol’ nets with a random digital shift only (Sob-S)
		SobolSequence PS1 = new SobolSequence(8, 31, s*2);
		PS1.addRandomShift(prng);
		
		// strategy 2
		// Sobol’ nets with a left matrix scramble followed by a digital shift (Sob-LMS-S)
		SobolSequence PS2 = new SobolSequence(8, 31, s*2);
		PS2.leftMatrixScramble(prng);
		PS2.addRandomShift(prng);
		
		// strategy 3
		// Korobov lattice rules with a random shift modulo 1 (Kor-S)
		KorobovLattice PS3 = new KorobovLattice (65521, 944, s); // approx. 2^{16} points.
	    
		// strategy 4
		// Korobov lattice rules with a random shift modulo 1 followed by a baker transformation (Kor-S-B)
	    BakerTransformedPointSet PS4 = new BakerTransformedPointSet (PS3);
	    PS4.addRandomShift(prng);
	    PS3.addRandomShift(prng);
		
	    // get the stats for the different point sets
	    PointSet[] PS = new PointSet[]{PS1, PS2, PS3, PS4};
	    Tally[][] Tallies = new Tally[4][4];
	    
	    for(int i = 0; i < 4; i++)
	    {
	    	for(int j = 0; j < 4; j++)
	    	{
	    		Tallies[i][j] = new Tally();
	    	}
	    }
	    
	    for(int i=0; i<4; i++)
	    {
	    	if(i==0)
	    	{
	    		
	    	}
	    	else if(i == 1)
	    	{
	    		
	    	}
	    	else if(i == 2)
	    	{
	    		
	    	}
	    	else
	    	{
	    		
	    	}
	    }
		
		
		
		process.BGSS_RQMC(m, sob, stats1);
		process.BGBS_RQMC(m, sob, prng, stats2);
		process.DGBS_RQMC(m, sob, prng, stats3);
		System.out.println(stats1.report() + stats1.average() + " " + stats1.variance());
		System.out.println(stats2.report() + stats2.average() + " " + stats2.variance());
		System.out.println(stats3.report() + stats3.average() + " " + stats3.variance());
	}

}
