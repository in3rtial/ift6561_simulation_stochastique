package homework_3;

import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.randvar.UniformGen;
import umontreal.iro.lecuyer.stat.*;


//Perform a simulation experiment similar to that reported in Example 1.42,
//but with K = 1 and a = 0, and with b = 2, 3, and 4. Discuss your results.


// example 1.42
//Let Y 1 and Y 2 be two independent random variables with
//densities π 1 and π 2 , and cdfs F 1 and F 2 , respectively, over R. Suppose that we
//receive a payoff
//X = Y 1 + Y 2 − K if Y 1 ≤ a and Y 1 + Y 2 ≥ b
//	  0 otherwise 
//where K > 0, and a and b are fixed constants.
//We want to estimate μ = E[X].
//This is a simplified version of a kind of model that occurs in the pricing of barrier
//options in finance (Glasserman 2004, Hull 2000). The standard MC method
//generates Y 1 and Y 2 from their original normal distributions and computes X.



public class Exercise1 {
	
	

	
	public void ex1(double a, double b)
	{
		//
		assert a > 0;
		assert b > 0;
		
		NormalDist dist1 = new NormalDist(1, 1);
		NormalDist dist2 = new NormalDist(1, 1);
		
		double F1a = dist1.cdf((a - 1.0));
		
	}
	
	public void simulateClever(double a, double b, double n, RandomStream prng)
	{
		// figure out the sampling limit
		double F1  = NormalDist.cdf(1.0, 1.0, a - 1.0);
		
		// generate from this interval
		UniformGen U1 = new UniformGen(prng ,0.0, F1);
		
		//
		
		Tally tally = new Tally();
		//
		for(int i = 0; i < n; i++)
		{
			double Y1 = 1 + NormalDist.inverseF(1,  1, U1.nextDouble());
			UniformGen U2 = new UniformGen(prng, NormalDist.cdf(1, 1, b - 1.0 - Y1), 1);
			double Y2 = 1 + NormalDist.inverseF(1,  1,  U2.nextDouble());
			
		}
	}
	
	public static Tally simulateDumb(double a, double b, double K,
						      double n,
							  Distribution dist1, Distribution dist2,
							  RandomStream prng)
	{
		Tally stats = new Tally();
		for(int i =0 ; i < n; i++)
		{
			// if Y1 ≤ a and Y 1 + Y 2 ≥ b
			// {
			//  	X = Y1 + Y2 − K 
			// }
			// else
			// {
			//  	X = 0
			// }
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
	
	public static void testExample(int n)
	{
		Distribution dist1 = new NormalDist(1.0, 1.0);
		Distribution dist2 = new NormalDist(1.0, 1.0);
		MRG32k3a prng = new MRG32k3a();
		System.out.println(simulateDumb(0.5, 2, 1, n, dist1, dist2, prng).report());
	}
	
	
	public static void main(String[] args)
	{
		//
		testExample(10000);
		
	}
}
