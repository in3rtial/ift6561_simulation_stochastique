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
	
	
	public static double[] getChi2(int df, int numberOfSamples)
	{
		MRG32k3a prng= new MRG32k3a();
		double[] result = new double[numberOfSamples];
		Distribution chi2 = new ChiSquareDist(df);
		for(int i = 0; i < numberOfSamples; i++)
		{
			result[i] = chi2.inverseF(prng.nextDouble());
		}
		return result;
	}
	
	
	
	/**
	 * generate Chi2 confidence intervals on variance of samples ~ Exponential(1)
	 * @param experiments number of independent experiments to generate
	 * @param n size of each experiment
	 * @return Chi2 confidence intervals (low, high)
	 */
	public static double[][] generateCIChi2(int experiments, int n)
	{
		RandomStream prng = new MRG32k3a();
		Distribution dist = new ExponentialDist(1);
		double[][] intervals = new double[experiments][3];
		
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
			intervals[experiment][2] = stats.variance();
		}
		return intervals;
	}
	
	
	public static double[] getStats(double[][] arr)
	{
		Tally width = new Tally("width of the intervals");
		Tally covers1 = new Tally("interval covers true value of variance");
		Tally centralValues = new Tally("central values");
		for(int i = 0; i < arr.length; i++)
		{
			width.add(arr[i][1] - arr[i][0]);
			covers1.add((1.0 <= arr[i][1] && 1.0 >= arr[i][0]) ? 1.0 : 0.0);
			centralValues.add((arr[i][0] + arr[i][1]) / 2.0);
		}
		// E[Exponential(1)] = 1, Var[Exponential(1)] = 1
		// center and radius of the student confidence interval
		double[] centerAndRadiusW = new double[2];
		width.confidenceIntervalStudent(0.95, centerAndRadiusW);
		double[] centerAndRadiusP = new double[2];
		covers1.confidenceIntervalStudent(0.95, centerAndRadiusP);
		// E[W], CI 95% chi2 E[W], p, CI 95% chi2 p, E[centralValues]]
		return new double[] {centerAndRadiusW[0], centerAndRadiusW[1],
							 centerAndRadiusP[0], centerAndRadiusP[1], centralValues.average()};
		
	}
	
	
	
	public static double[] getVarianceBootstrap(double[] data, RandomStream prng, int bootstrapSize)
	{
		double[] result = new double[3];
		double[] variances = new double[bootstrapSize];
		for(int i = 0; i < bootstrapSize; i++)
		{
			double[] newArr = sampleWithReplacement(data, prng);
			variances[i] = getVariance(newArr);
		}
		Arrays.sort(variances);
		int leftIndex = (int)(0.025 * bootstrapSize);
		int rightIndex = (int)(0.975 * bootstrapSize);
		result[0] = variances[leftIndex];
		result[1] = variances[rightIndex];
		result[2] = getMean(variances);
		return result;
	}
	
	
	public static double[][] generateCIBootstrap(int numberOfExperiments, int experimentSize, int bootstrapSize)
	{
		RandomStream prng = new MRG32k3a();
		Distribution exponentialDist = new ExponentialDist(1);
		double[][] intervals = new double[numberOfExperiments][2];
		
		// step 1 1000 independent experiments
		for(int i = 0; i < numberOfExperiments; i++)
		{
			// step 2 generate random variables and calculate their confidence interval
			double[] sample = new double[experimentSize];
			for(int j = 0; j < experimentSize ; j++)
			{
				sample[j] = exponentialDist.inverseF(prng.nextDouble());
			}
			
			intervals[i] = getVarianceBootstrap(sample, prng, bootstrapSize);
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
	
	
	public static void main(String[] args)
	{
		final int size1 = 100;
		final int size2 = 1000;

		double t1a = System.currentTimeMillis();
		double[][] data1 = generateCIChi2(1000, size1);
		double t1b = System.currentTimeMillis();
		double t1 = ((t1b - t1a) / 1000.0);
	
		double t2a = System.currentTimeMillis();
		double[][] data2 = generateCIChi2(1000, size2);
		double t2b = System.currentTimeMillis();
		double t2 = ((t2b - t2a) / 1000.0);
		
		double t3a = System.currentTimeMillis();
		double[][] data3 = generateCIBootstrap(1000, size1, 1000);
		double t3b = System.currentTimeMillis();
		double t3 = ((t3b - t3a) / 1000.0);
		
		double t4a = System.currentTimeMillis();
		double[][] data4 = generateCIBootstrap(1000, size2, 1000);
		double t4b = System.currentTimeMillis();
		double t4 = ((t4b - t4a) / 1000.0);
		
		double[] left1 = new double[data1.length];
		double[] right1 = new double[data1.length];
		double[] center1 = new double[data1.length];

		double[] left2 = new double[data2.length];
		double[] right2 = new double[data2.length];
		double[] center2 = new double[data2.length];
		
		double[] left3 = new double[data3.length];
		double[] right3 = new double[data3.length];
		double[] center3 = new double[data3.length];
		
		double[] left4 = new double[data4.length];
		double[] right4 = new double[data4.length];
		double[] center4 = new double[data4.length];
		
		for(int i = 0; i < data1.length; i++)
		{
			left1[i] = data1[i][0];
			right1[i] = data1[i][1];
			center1[i] = data1[i][2];
			
			left2[i] = data2[i][0];
			right2[i] = data2[i][1];
			center2[i] = data2[i][2];
			
			left3[i] = data3[i][0];
			right3[i] = data3[i][1];
			center3[i] = data3[i][2];
			
			left4[i] = data4[i][0];
			right4[i] = data4[i][1];
			center4[i] = data4[i][2];
		}
		
		HistogramChart chi2Chart99 = new HistogramChart("Chi2 distribution, 99 degrees of freedom", "values", "number of occurrences", getChi2(99, 1000));
		HistogramChart chi2Chart999 = new HistogramChart("Chi2 distribution, 999 degrees of freedom", "values", "number of occurrences", getChi2(999, 1000));
		HistogramChart chart1 = new HistogramChart("Chi2, size of sample = 100, done in " + t1 + "s", "left, right and center values", "number of occurrences", center1,left1, right1);
		HistogramChart chart2 = new HistogramChart("Chi2, size of sample = 1000, done in " + t2 + "s", "left, right and center values", "number of occurrences", center2, left2, right2);
		HistogramChart chart3 = new HistogramChart("Bootstrap, size of sample = 100, done in " + t3 + "s", "left, right and center values", "number of occurrences", center3, left3, right3);
		HistogramChart chart4 = new HistogramChart("Bootstrap, size of sample = 1000, done in " + t4 + "s", "left, right and center values", "number of occurrences", center4, left4, right4);
		
		chart1.view(800, 500);
		chart2.view(800, 500);
		chart3.view(800, 500);
		chart4.view(800, 500);
		chi2Chart99.view(800, 500);
		chi2Chart999.view(800, 500);
		
		printArray(getStats(data1));
		printArray(getStats(data2));
		printArray(getStats(data3));
		printArray(getStats(data4));
	}
}
