package homework_3;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.randvarmulti.MultinormalCholeskyGen;
import umontreal.iro.lecuyer.randvar.NormalGen;
import umontreal.iro.lecuyer.rng.*;

public class Exercise6 {
	
	/**
	 * Used for NORTA
	 * @param p
	 * @param n
	 * @return
	 */
	public static double[][] generateMultiNormal(double p, int n)
	{
		MRG32k3a prng = new MRG32k3a();
		NormalGen ng = new NormalGen(prng);
		MultinormalCholeskyGen dist = new MultinormalCholeskyGen(ng, new double[] {0, 0}, new double[][] { {1, p},{p, 1}});
		
		double [][] result = new double[n][2];
		for(int i = 0; i < n; i++)
		{
			dist.nextPoint(result[i]);
		}
		return result;
	}
	
	/**
	 * Used for NORTA
	 * @param samples
	 * @return
	 */
	public static double[][] invertMultiNormal(double[][] samples)
	{
		double[][] result = new double[samples.length][2];
		for(int i = 0; i < samples.length; i++)
		{
			result[i][0] = samples[i][0];
			result[i][1] = samples[i][1];
		}
		for(int i = 0; i < samples.length; i++)
		{
			result[i][0] = NormalDist.cdf01(samples[i][0]);
			result[i][1] = NormalDist.cdf01(samples[i][1]);
		}
		return result;
	}
	
	/**
	 * Get an array of correlated tuples (X, Y) uniform(0, 1), correlated by p
	 * @param n number of tuples
	 * @param p correlation between the tuples
	 * @return [(X1, Y1), ..., (Xn, Yn)] correlated by p
	 */
	public static double[][] NORTA(int n, double p)
	{
		double[][] vars = generateMultiNormal(p, n);
		return invertMultiNormal(vars);
	}
	
	

	public static double[][] FRECHET(int n, double p)
	{
		p = (p + 1.0) / 2.0;
		MRG32k3a prng = new MRG32k3a();
		
		double[][] result = new double[n][2];
		
		for(int i = 0; i < n; i++)
		{
			double U1 = prng.nextDouble();
			double U2;
			if(prng.nextDouble() <= p)
			{
				U2 = U1;
			}
			else
			{
				U2 = 1 - U1;
			}
			result[i][0] = U1;
			result[i][1] = U2;
			
		}
		return result;
	}
	
	public static void test1(int n, double p)
	{
		double[][] a = generateMultiNormal(p, n);
		for(int i = 0; i < n; i++)
		{
			System.out.println("[" + a[i][0] + ", "+a[i][1]+"]");
		}
	}

	public static void test2(int n, double p)
	{
		double[][] vars = generateMultiNormal(p, n);
		double[][] inverted = invertMultiNormal(vars);
		for(int i = 0; i < n; i++)
		{
			System.out.println("[" + inverted[i][0] + ", "+ inverted[i][1]+"]");
			System.out.println("[" + vars[i][0] + ", "+vars[i][1]+"]");
		}
	}
	
	public static void test3(int n, double p)
	{
		double[][] vars = FRECHET(n, p);
		for(int i = 0; i < n; i++)
		{
			System.out.println("[" + vars[i][0] + ", "+vars[i][1]+"]");
		}
	}
	
	/**
	 * 
	 * @param n number of variables to generate
	 * @param p correlation between the two uniforms
	 * @param threshold threshold of the 
	 */
	public static void runExerciseB(int n, double p, double threshold)
	{
		double[][] norta = NORTA(n, p);
		double[][] frech = FRECHET(n, p);
		int sumNorta = 0;
		int sumFrech = 0;
		for(int i = 0; i < n; i++)
		{
			if ((norta[i][0] + norta[i][1]) < threshold)
			{
				sumNorta += 1;
			}
			if ((frech[i][0] + frech[i][1]) < threshold)
			{
				sumFrech += 1;
			}
		}
		double frechProb = (double)sumFrech / (double)n;
		double nortaProb = (double)sumNorta / (double)n;
		
		System.out.println("NORTA = " + nortaProb + " FRECHET = " + frechProb);
		
	}
	
	
	
	public static void runExerciseC(int n, double p, double threshold)
	{
		double[][] norta = NORTA(n, p);
		double[][] frech = FRECHET(n, p);
		int sumNorta = 0;
		int sumFrech = 0;
		for(int i = 0; i < n; i++)
		{
			if ((norta[i][0] - norta[i][1]) < threshold)
			{
				sumNorta += 1;
			}
			if ((frech[i][0] - frech[i][1]) < threshold)
			{
				sumFrech += 1;
			}
		}
		double frechProb = (double)sumFrech / (double)n;
		double nortaProb = (double)sumNorta / (double)n;
		
		System.out.println("NORTA = " + nortaProb + " FRECHET = " + frechProb);
		
	}
	public static void main(String args[])
	{
		//test1(100, 0.99);
		//test2(100, -1);
		//test3(100, 1);
		System.out.println("Exercise B");
		runExerciseB(1000000, 0.9, 0.1);
		System.out.println("Exercise C");
		runExerciseC(1000000, 0.8, 0.05);
	}
}
