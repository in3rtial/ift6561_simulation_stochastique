package homework_4;

import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.charts.*;

public class exercise2 {
	
	
	public static double beta32(double x) {
		assert(x <= 1);
		assert(0 <= x);
		
		
		return 12.0 * x * x * ( 1.0 - x);
		
	}
	
	/**
	 * Generate points from the Beta(3,2) density with 16/9 U(0,1) as hat function
	 * As in Figure 4.1
	 * @param n number of samples to generate (effective samples)
	 * @return
	 */
	public static double[] naive(int n)
	{
		// X is U(0, 1)
		// Y is 16/9 * X
		// 
		assert(n > 0);
		
		RandomStream prng = new MRG32k3a();
		double[] result = new double[n];
		int successes = 0;
		while(successes < n)
		{
			double x = prng.nextDouble();
			double y = prng.nextDouble() * (16.0 / 9.0);
			// check if the point belongs to the surface of f
			if(y <= beta32(x))
			{
				result[successes] = x;
				successes += 1;
			}
		}
		return result;
		
	}
	
	
	private static double getH(double[] xs, double[] hs, double x)
	{
		double h = -1;
		for(int i = 0; i < xs.length; i++)
		{
			if(xs[i] > x)
			{
				h = hs[i];
				break;
			}
		}
		return h;
	}
	
	
	/**
	 * Generate points from the Beta(3,2) density with 3 breakpoints as hat function
	 * As in figure 4.2
	 * @param n
	 * @return
	 */
	public static double[] threePieces(int n)
	{
		// the optimized breakpoints
		double x1 = 0.281023;
		double y1 = beta32(x1);
		double x2 = 0.89538;
		double y2 = beta32(x2);
		
		double[] xs = {x1, x2, 1.0};
		double[] hs = {y1, 16.0 / 9.0, y2};
		
		PiecewiseConstantDist dist = new PiecewiseConstantDist(new double[] {0.0, 0.281023, 0.89538, 1.0}, new double[] {0.681365, 16.0/9.0, 1.0064929});
		
		
		RandomStream prng = new MRG32k3a();
		double[] result = new double[n];
		int successes = 0;
		while(successes < n)
		{
			double x = dist.inverseF01(prng.nextDouble());
			double y = prng.nextDouble() * getH(xs, hs, x);
			// check if the point belongs to the surface of f
			if(y <= beta32(x))
			{
				result[successes] = x;
				successes += 1;
			}
		}
		return result;	
		
	}
	
	
	/**
	 * Generate points from the Beta(3,2) density with 3 breakpoints as hat function
	 * As in figure 4.2
	 * @param n
	 * @return
	 */
	public static double[] fivePieces(int n)
	{
		// the optimized breakpoints
		double x1 = 0.202604;
		double x2 = 0.381073;
		double x3 = 0.847968;
		double x4 = 0.932157;
		double y1 = beta32(x1);
		double y2 = beta32(x2);
		double y3 = beta32(x3);
		double y4 = beta32(x4);
		
		double[] xs = {x1, x2, x3, x4, 1.0};
		double[] hs = {y1, y2, 16.0 / 9.0, y3, y4};
		
		PiecewiseConstantDist dist = new PiecewiseConstantDist(new double[] {0.0, 0.281023, 0.89538, 1.0}, new double[] {0.681365, 16.0/9.0, 1.0064929});
		
		
		RandomStream prng = new MRG32k3a();
		double[] result = new double[n];
		int successes = 0;
		while(successes < n)
		{
			double x = dist.inverseF01(prng.nextDouble());
			double y = prng.nextDouble() * getH(xs, hs, x);
			// check if the point belongs to the surface of f
			if(y <= beta32(x))
			{
				result[successes] = x;
				successes += 1;
			}
		}
		return result;	
		
	}
	
	
	
	/**
	 * Return the time (ms) of execution for the naive sampling algorithm
	 * @param n
	 * @return
	 */
	public static double time_naive(int n)
	{
		double startTime = System.currentTimeMillis();
		double[] generated_numbers = naive(n);
		long stopTime = System.currentTimeMillis();
		double elapsedTime = (stopTime - startTime) / 1000.0;
		return elapsedTime;
	}
	
	
	/**
	 * Return the time (ms) of execution for the 3 breakpoints sampling algorithm
	 * @param n
	 * @return
	 */
	public static double time3Pieces(int n)
	{
		double startTime = System.currentTimeMillis();
		double[] generated_numbers = threePieces(n);
		double endTime = System.currentTimeMillis();
		return ((endTime - startTime)/1000.);
	}
	
	
	/**
	 * Return the time (ms) of execution for the 5 breakpoints sampling algorithm
	 * @param n
	 * @return
	 */
	public static double time5Pieces(int n)
	{
		double startTime = System.currentTimeMillis();
		double[] generated_numbers = fivePieces(n);
		double endTime = System.currentTimeMillis();
		return ((endTime - startTime)/1000.);
	}
	/**
	 * generate charts to make sure that the accept-reject is working
	 */
	public static void test() {
		double[] a = naive(100000);
		HistogramChart chart = new HistogramChart("naive method", null, null, a);
		chart.view(800, 500);
		double[] b = threePieces(100000);
		HistogramChart chart2 = new HistogramChart("3 pieces method", null, null, b);
		chart2.view(800, 500);
		
		double[] c = fivePieces(100000);
		HistogramChart chart3 = new HistogramChart("5 pieces method", null, null, b);
		chart3.view(800, 500);
	}
	
	/**
	 * calculate the running times of the different algorithms
	 * @param n
	 */
	public static void time(int[] n)
	{
		double[][] fivePieces = new double[2][n.length];
		double[][] threePieces = new double[2][n.length];
		double[][] naive = new double[2][n.length];
		for(int i = 0; i < n.length; i++)
		{
			threePieces[0][i] = n[i];
			threePieces[1][i] = time3Pieces(n[i]);
			fivePieces[0][i] = n[i];
			fivePieces[1][i] = time5Pieces(n[i]);
			naive[0][i] = n[i];
			naive[1][i] = time_naive(n[i]);
		}
		

		XYChart naiveTimes = new XYLineChart("naive algorithm running time","sample size", "running time (seconds)", naive);
		naiveTimes.setAutoRange00(true, true); // Axes pass through (0,0)
		naiveTimes.view(800,500);
		
		XYChart threePiecesChart = new XYLineChart("3 pieces algorithm running time", "sample size", "running time (seconds)", threePieces);
		threePiecesChart.setAutoRange00(true, true); // Axes pass through (0,0)
		threePiecesChart.view(800,500);
		
		XYChart fivePiecesChart = new XYLineChart("5 pieces algorithm running time", "sample size", "running time (seconds)", fivePieces);
		fivePiecesChart.setAutoRange00(true, true); // Axes pass through (0,0)
		fivePiecesChart.view(800,500);
		
	}
	
	public static void main(String[] args)
	{
		// verify that the distributions are good
		test();
		
		// check the runtimes
		int[] samples = new int[27];
		for(int i = 0; i < samples.length; i++)
		{
			samples[i] = 1<<i;
		}
		
		time(samples);
		
	}
}
