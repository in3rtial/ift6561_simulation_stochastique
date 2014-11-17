package homework_4;


import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.*;
import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.charts.*;


// do only (i) and (ii)
public class exercise4 {

	// portfolio of 5 assets whose values are assumed to follow
	// independent geometric brownian motion with parameters 
	// sigma = 0.3
	// r = 0.05
	// S(0) = 50
	// estimate the value-at-risk (VaR) for time T = 0.1 with p=0.01
	// loss case is L = sum {j=1} {5} (50 - Sj(T))
	
	
	public static void main(String[] args)
	{
		final int size = 1000000;
		final int df = 20001000;
		for(int i = 100; i < 100000; i+=1000)
		{
			Distribution dist = new StudentDist(i);
			System.out.println("df " + i + " low " + dist.inverseF(0.025) + " high " + dist.inverseF(0.975));
		}
		Distribution dist = new StudentDist(df);
		MRG32k3a prng = new MRG32k3a();
		
		double[] data = new double[size];
		for(int i =0; i < size; i++)
		{
			data[i] = dist.inverseF(prng.nextDouble());
		}
		
		HistogramChart chart = new HistogramChart(null, null, null, data);
		chart.view(800, 500);
	}
}
