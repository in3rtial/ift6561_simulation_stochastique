package homework_4;

import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.*;
import umontreal.iro.lecuyer.probdist.*;

public class exercise3 {

	public static double[] sampleWithReplacement(double[] observations, RandomStream prng)
	{
		double[] newObservations = new double[observations.length];
		int s = observations.length - 1;
		for(int i = 0; i < observations.length; i++)
		{
			newObservations[i] = observations[prng.nextInt(0, s)];
		}
		return newObservations;
	}
	
	
	
	public static double[][] runAB(int experiments, int n)
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
			
			// compute the confidence interval 1 - \alpha = 0.95
			stats.confidenceIntervalVarianceChi2(0.95, intervals[experiment]);
		}
		
		return intervals;
	}

	public static double getStats(double[][] arr)
	{
		Tally width = new Tally("E[W]");
		Tally covers1 = new Tally("Confidence interval covers true value");
		for(int i = 0; i < arr.length; i++)
		{
			width.add(arr[i][1] - arr[i][0]);
			covers1.add((1.0 <= arr[i][1] && 1.0 >= arr[i][0]) ? 1.0 : 0.0);
		}
		
		// average of E[W]
		double Ew = width.average();
		// center and radius of the student confidence interval
		double[] EwCI = new double[2];
		width.confidenceIntervalStudent(0.95, EwCI);
		
		
		
		
	}
	
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
//		testSampling(10);
		
		double[][] a = runAB(1000, 1000);
		printMatrix(a);
	}
}
