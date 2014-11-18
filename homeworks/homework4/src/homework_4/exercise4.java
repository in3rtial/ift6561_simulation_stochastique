package homework_4;


import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.probdist.*;
import homework_4.helpers;
import umontreal.iro.lecuyer.charts.*;
import java.util.Arrays;


// do only (i) and (ii)
public class exercise4 {
	
	public static double sum(double[] arr)
	{
		double total = 0;
		for(double x: arr)
			total += x;
		return total;
	}
	
	
	// portfolio of 5 assets whose values are assumed to follow
	// independent geometric brownian motion with parameters 
	// sigma = 0.3
	// r = 0.05
	// S(0) = 50
	// estimate the value-at-risk (VaR) for time T = 0.1 with p=0.01
	// loss case is L = sum {j=1} {5} (50 - Sj(T))

	
	public static double trajectory(double sigma, double mu, double t, double S_0, RandomStream prng)
	{
		double left = (mu - (sigma * sigma)/2)*t;
		double right = sigma * NormalDist.inverseF(0, t, prng.nextDouble());
		return S_0 * Math.exp(left + right);
	}
	
	
	public static double generateLoss(int numberOfActions, double T, double sigma, double mu, double S_0)
	{
		MRG32k3a prng = new MRG32k3a();
		double[] results = new double[numberOfActions];
		for(int i = 0; i < numberOfActions; i++)
		{
			results[i] = trajectory(sigma, mu, T, S_0, prng);
		}
		
		return numberOfActions*S_0 - sum(results);
		
	}
	
	
	public static double[] generate(int sampleSize, int numberOfActions, double T, double sigma, double mu, double S_0)
	{
		double[] result = new double[sampleSize];
		for(int i = 0; i < sampleSize; i++)
			result[i] = (generateLoss(numberOfActions, T, sigma, mu, S_0));
		return result;
	}
	
	/**
	 * return the boundary of the value at risk on the left tail
	 * @param observations
	 * @param confidenceLevel
	 * @return
	 */
	public static double getVaR(double[] observations, double confidenceLevel)
	{
		// 0.05 will be index 
		if(! helpers.isSorted(observations))
		{
			Arrays.sort(observations);
		}
		
		int index = (int)(confidenceLevel * observations.length);
		
		return observations[index];
	}
	

	
	
	public static double[] getCI95Binomial(double[] losses, double confidenceLevel)
	{
		if(! helpers.isSorted(losses))
		{
			Arrays.sort(losses);
		}
		
		// confidence interval on quantiles (see p.351)
		// using Agresti-Coull's calculation, the one in the notes is vague...
		// and the factorials are damn expensive
		// as implemented in the binom R package (and in the wikipedia reference)...
		int X = (int) (confidenceLevel * losses.length);
		double z = NormalDist.inverseF01(0.975);
		double n_tilde = losses.length + z*z;
		double p_tilde = (X + 0.5*z*z)/n_tilde;

		// build the confidence interval
		double interval = (z * Math.sqrt((p_tilde * (1.0 - p_tilde))/n_tilde));
		double left = p_tilde - interval;
		double right = p_tilde + interval;

		int j = (int) Math.round(left * losses.length);
		int k = (int) Math.round(right* losses.length);
		// binomial random variable with parameters(n, confidenceLvel)
		// select the values j and k s.t. probability is close
		return new double[] {losses[j], losses[k], (double)j, (double)k};
	}
	
	
	public static double[] getCI95Normal(double[] losses, double confidenceLevel)
	{
		if(! helpers.isSorted(losses))
		{
			Arrays.sort(losses);
		}
		
		int n = losses.length;
		double q = confidenceLevel;
		double z = NormalDist.inverseF01(0.975);
		double delta = Math.sqrt(n*q*(1.0 - q)) * z;
		int j = (int) Math.floor(n * q + 1 - delta);
		int k = (int) Math.floor(n * q + 1 + delta);

		
		return new double[] {losses[j], losses[k], (double) j, (double) k};
		
	}
	
	
	public static void main(String[] args)
	{
		// a)
		int size = 1000;
		
		double[] data = generate(size, 5, 0.1, 0.3, 0.05, 50);
		
		System.out.println("a)");
		
		// print VaR at p = 0.01
		System.out.println("Value at Risk, p = 0.01");
		System.out.println(getVaR(data, 0.01));
		HistogramChart chart = new HistogramChart("Empirical distribution of L, 1000 observations", "L", "Number of observations", data);
		chart.view(800, 500);
		System.out.println("Normal 95% confidence interval");
		helpers.printArray(getCI95Normal(data, 0.01));
		System.out.println("Binomial 95% confidence interval");
		helpers.printArray(getCI95Binomial(data, 0.01));
		
		
		System.out.println("\nb)");
		size = 10000;
		double[] data2 = generate(size, 5, 0.1, 0.3, 0.05, 50);
		System.out.println("Value at Risk, p = 0.01");
		System.out.println(getVaR(data2, 0.01));
		HistogramChart chart2 = new HistogramChart("Empirical distribution of L, 10000 observations", "L", "Number of observations", data2);
		chart2.view(800, 500);
		System.out.println("Normal 95% confidence interval");
		helpers.printArray(getCI95Normal(data2, 0.01));
		System.out.println("Binomial 95% confidence interval");
		helpers.printArray(getCI95Binomial(data2, 0.01));
		
		System.out.println("\nc)");
		System.out.println("Value at Risk, p = 0.001");
		System.out.println(getVaR(data, 0.001));
		System.out.println("Normal 95% confidence interval");
		helpers.printArray(getCI95Normal(data, 0.001));
		System.out.println("Binomial 95% confidence interval");
		helpers.printArray(getCI95Binomial(data, 0.001));
		
		System.out.println("");
		System.out.println("Value at Risk, p = 0.001");
		System.out.println(getVaR(data2, 0.001));
		System.out.println("Normal 95% confidence interval");
		helpers.printArray(getCI95Normal(data2, 0.001));
		System.out.println("Binomial 95% confidence interval");
		helpers.printArray(getCI95Binomial(data2, 0.001));
	}
}
