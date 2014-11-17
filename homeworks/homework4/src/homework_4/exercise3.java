package homework_4;

import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.*;
import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.charts.*;

import java.util.Arrays;

public class exercise3 {


	/**
	 * sample n elements from the list of observations
	 * @param list
	 * @param prng
	 * @return
	 */
	public static double[] sampleWithReplacement(double[] list, RandomStream prng)
	{
		double[] sampled = new double[list.length];
		int listSize = list.length - 1;
		for(int i = 0; i < list.length; i++)
		{
			sampled[i] = list[prng.nextInt(0, listSize)];
		}
		return sampled;
	}
	
	
	public static double getMean(double[] data) {
		double sum = 0.0;
		for (double a : data)
			sum += a;
		return sum / data.length;
	}
	
	
	public static double getVariance(double[] data) {
		double mean = getMean(data);
		double temp = 0;
		for (double a : data)
			temp += (mean - a) * (mean - a);
		return temp / (data.length-1);
	}
	
	
	/**
	 * helper method to print array of doubles
	 * @param arr
	 */
	public static void printArray(double[] arr)
	{
		System.out.print("[");
		for(int i = 0; i < arr.length-1; i++)
		{
			System.out.print(arr[i] + ", ");
		}
		System.out.print(arr[arr.length-1]);
		System.out.println("]");
	}
	
	
	/**
	 * helper method to print matrix of doubles
	 * @param matrix
	 */
	public static void printMatrix(double[][] matrix)
	{
		for(int i = 0; i < matrix.length; i++)
		{
			System.out.print("[");
			for(int j = 0; j < matrix[0].length-1; j++)
			{
				System.out.print(matrix[i][j] +", ");
			}
			System.out.print(matrix[i][matrix[i].length-1]);
			System.out.println("]");
		}
	}
	
	
	/**
	 * generate Chi2 confidence intervals on generate samples ~ Exponential(1)
	 * @param experiments number of independent experiments to generate
	 * @param n size of each experiment
	 * @return Chi2 confidence intervals (low, high)
	 */
	public static double[][] generateConfidenceIntervals(int experiments, int n)
	{
		RandomStream prng = new MRG32k3a();
		Distribution dist = new ExponentialDist(1);
		double[][] intervals = new double[experiments][2];
		
		for(int experiment = 0; experiment < experiments; experiment++)
		{
			Tally stats = new Tally();
			for(int i = 0; i < n ; i++)
			{
				stats.add(dist.inverseF(prng.nextDouble()));
			}
			// left = (n-1)s^2 / (chi2 alpha/2) 
			// right = (n-1)s^2 / (chi2 1 - alpha/2)
			stats.confidenceIntervalVarianceChi2(0.95, intervals[experiment]);
		}
		return intervals;
	}
	
	
	public static double[] getStats(double[][] arr)
	{
		Tally width = new Tally("width of the intervals");
		Tally covers1 = new Tally("bernoulli variable,interval covers true value of variance");
		for(int i = 0; i < arr.length; i++)
		{
			width.add(arr[i][1] - arr[i][0]);
			covers1.add((1.0 <= arr[i][1] && 1.0 >= arr[i][0]) ? 1.0 : 0.0);
		}
		// E[Exponential(1)] = 1, Var[Exponential(1)] = 1
		// center and radius of the student confidence interval
		double[] centerAndRadiusW = new double[2];
		width.confidenceIntervalStudent(0.95, centerAndRadiusW);
		double[] centerAndRadiusP = new double[2];
		covers1.confidenceIntervalStudent(0.95, centerAndRadiusP);
		// E[W], CI 95% chi2 E[W], p, CI 95% chi2 p]
		return new double[] {centerAndRadiusW[0], centerAndRadiusW[1],
							 centerAndRadiusP[0], centerAndRadiusP[1]};
		
	}
	
	
	
	public static double[] getVarianceBootstrap(double[] data, RandomStream prng)
	{
		double[] result = new double[2];
		double[] variances = new double[1000];
		for(int i = 0; i < 1000; i++)
		{
			double[] newArr = sampleWithReplacement(data, prng);
			variances[i] = getVariance(newArr);
		}
		Arrays.sort(variances);
		// left value = sorted variances[25]
		System.out.println("mv " + getMean(variances));
		result[0] = variances[25];
		result[1] = variances[975];
		return result;
	}
	
	
	public static double[][] generateCIBootstrap()
	{
		RandomStream prng = new MRG32k3a();
		Distribution dist = new ExponentialDist(1);
		double[][] intervals = new double[1000][2];
		
		// step 1 1000 independent experiments
		for(int i = 0; i < 1000; i++)
		{
			// step 2 generate random variables and calculate their confidence interval
			double[] sample = new double[100];
			for(int j = 0; j < 100 ; j++)
			{
				sample[j] = dist.inverseF(prng.nextDouble());
			}
			
			intervals[i] = getVarianceBootstrap(sample, prng);
		}
		return intervals;
	}






	/**
	 * test the sampling with replacement
	 * @param n number of resampling to try
	 */
	public static void testSampling(int n)
	{
		RandomStream prng = new MRG32k3a();
		double[] arr = new double[10];
		for(int i = 0; i < 10; i++)
		{
			arr[i] = i;
		}
		printArray(arr);
		for(int i = 0; i < n; i++)
		{
			printArray(sampleWithReplacement(arr, prng));
			
		}
	}


	
	/**
	 * 5.6 part 1
	 * @return
	 */
	public static double[] run(int n)
	{
		// a 1000 times, generate n = 100 variables~Exponential(1)
		assert n>0;
		double[][] confidenceIntervalsChi2 = generateConfidenceIntervals(1000, n);
		return getStats(confidenceIntervalsChi2);
	}


	public static void seeDiff(int n, int m)
	{
		Distribution chi2 = new ChiSquareDist(n);
		MRG32k3a prng = new MRG32k3a();
		double[] data =new double[m];
		for(int i = 0; i < m ; i++)
		{
			data[i] = chi2.inverseF(prng.nextDouble());
		}
		HistogramChart c = new HistogramChart(null, null, null,data);
		c.view(800, 500);
	}
	public static void main(String[] args)
	{
		double[][] data1 = generateConfidenceIntervals(1000, 100);
		double[][] data2 = generateConfidenceIntervals(1000, 1000);
		double[][] data3 = generateCIBootstrap();

		double[] left1 = new double[data1.length];
		double[] right1 = new double[data1.length];

		double[] left2 = new double[data2.length];
		double[] right2 = new double[data1.length];
		
		double[] left3 = new double[data1.length];
		double[] right3 = new double[data1.length];
		
		
		for(int i = 0; i < data1.length; i++)
		{
			left1[i] = data1[i][0];
			right1[i] = data1[i][1];
			
			left2[i] = data2[i][0];
			right2[i] = data2[i][1];
			
			left3[i] = data3[i][0];
			right3[i] = data3[i][1];
		}
		
		HistogramChart chart1 = new HistogramChart("A", "value", "number of samples", left1, right1);
		HistogramChart chart2 = new HistogramChart("B", "value", "number of samples", left2, right2);
		HistogramChart chart3 = new HistogramChart("C", "value", "number of samples", left3, right3);
		chart1.view(800, 500);
		chart2.view(800, 500);
		chart3.view(800, 500);
		printArray(getStats(data1));
		printArray(getStats(data2));
		printArray(getStats(data3));
	}
}
