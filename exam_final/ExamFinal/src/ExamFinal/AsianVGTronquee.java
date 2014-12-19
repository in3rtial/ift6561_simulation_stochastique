package ExamFinal;

import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.probdist.TruncatedDist;
import umontreal.iro.lecuyer.randvar.GammaGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.stat.Tally;

public class AsianVGTronquee {

	final double K;
	final double discount; // discount factor = Math.exp(-r * zeta[s]).
	final double s0;
	final double sigma;
	final double theta;
	final double nu;
	final double mu;
	final double omega;
	final double r;
	final double logs0;
	final double T;

	public AsianVGTronquee(double r, double sigma, double theta, double nu,
			double K, double s0, double T) {
		this.K = K;
		this.s0 = s0;
		this.logs0 = Math.log(s0);
		this.discount = Math.exp(-r * T);
		this.sigma = sigma;
		this.mu = 1;
		this.theta = theta;
		this.nu = nu;
		this.r = r;
		this.T = T;
		this.omega = Math.log(1. - (theta * nu) - ((sigma * sigma * nu) / 2.0))
				/ nu;

	}

	public double getPayoff(double vgPath) {

		double St = Math.exp(logs0 + vgPath + r + omega);

		if (St > this.K)
			return this.discount * (St - this.K);
		else
			return 0.0;
	}

	public void simulate(int n, double theta2, RandomStream prng, Tally stats,
			boolean jump) {
		// transform the parameters of the VG for the gamma processes
		double mu_p = (Math.sqrt(theta * theta + ((2 * sigma * sigma) / nu)) + theta) / 2.;
		double mu_n = (Math.sqrt(theta * theta + ((2 * sigma * sigma) / nu)) - theta) / 2.;
		double nu_p = (mu_p * mu_p * nu);
		double nu_n = (mu_n * mu_n * nu);

		// calculate the \alpha and \lambda parameters of the GammaDists
		double p_alpha = mu_p * mu_p / nu_p;
		double p_lambda = mu_p / nu_p;
		double n_alpha = mu_n * mu_n / nu_n;
		double n_lambda = mu_n / nu_n;

		double p_lc = Math.pow(p_lambda, p_alpha)
				/ Math.pow(p_lambda + theta2, p_alpha);
		double n_lc = Math.pow(n_lambda, n_alpha)
				/ Math.pow(n_lambda + theta2, n_alpha);

		
		double rwlnk = Math.log(K/s0) - r - omega;
		// simulate the system
		for (int i = 0; i < n; i++) {
			// generate G-(1)
			double x_n = GammaGen.nextDouble(prng, n_alpha, n_lambda + theta2);

			// calculate the cut point
			double threshold = x_n + rwlnk;
			GammaDist gp = new GammaDist(p_alpha, p_lambda - theta2);
			double truncation_likelihood = 1. - gp.cdf(threshold);
			
			// we should truncate at the threshold value
			TruncatedDist tgp = new TruncatedDist(gp, threshold, Double.MAX_VALUE);
			
			double x_p = tgp.inverseF(prng.nextDouble());
			
			// adjust the likelihoods
			double likelihood_p = p_lc * Math.exp(-theta * x_p);
			double likelihood_n = n_lc * Math.exp(-theta * x_n);
			double L = likelihood_p * likelihood_n * truncation_likelihood;

	

			double p = x_p - x_n;
			double val = L * getPayoff(p);
			
			if(val > 10000)
			{
				System.out.println("G-: " + x_n + " Crit:" + threshold + " Likel: " + truncation_likelihood);
				System.out.println(x_p);
				i-=1;
				continue;
			}
			
			stats.add(val);
			if (jump == true)
				prng.resetNextSubstream();
		}
	}

	public static void main(String[] args) {

		double r = 0.1; // short rate of 10%
		double theta = -0.1436; // drift BM
		double sigma = 0.12136; // volatility of BM
		double nu = 0.3; // variance rate of gamma time change
		double K = 150; // K
		double s0 = 100; // s0
		double T = 1;
		MRG32k3a prng = new MRG32k3a();
		AsianVGTronquee process = new AsianVGTronquee(r, sigma, theta, nu, K, s0, T);

		int n = 10000;
		Tally stats = new Tally();
		process.simulate(n, 3., prng, stats, true);
		System.out.println(stats.report());

	}
}
