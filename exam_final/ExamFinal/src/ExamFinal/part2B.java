package ExamFinal;

import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.stat.Tally;

public class part2B {

	public part2B() { }
	
	// STATEMENT
	/*
	 * (b) Supposons maintenant que d = 1, de sorte que S= S(t_1) = S(1).
	 * Supposons aussi que K >> 100, de sorte que le “payoff” de l’option sera
	 * rarement positif. On voudra alors utiliser l’importance sampling (IS). Si
	 * on simule le processus par DGBS, il semble raisonnable d’augmenter la
	 * moyenne du processus G + et de diminuer celle du processus G − , de
	 * maniere a augmenter la valeur de G + (1) − G − (1).
	 * 
	 * Pour cela, on peut utiliser la strategie heuristique suivante. On
	 * applique une torsion expo- nentielle (“exponential twisting”) a la
	 * densite de chacune des deux v.a. gamma: on multiplie la densite g + (x)
	 * de G + (1) par e θx et la densite g − (y) de G − (1) par e −θy , pour une
	 * constante θ ≥ 0 qui reste a choisir, puis on normalise les densites. Pour
	 * θ ≥ 0 donne, quelles seront les nouvelles densites? Comment peut-on
	 * generer des variables aleatoires selon ces nouvelles densites? Et quel
	 * sera le rapport de vraisemblance associe?
	 */
	
	public static void main(String[] args)
	{	
		testGamma();
	}/*
		double r = 0.1; // short rate of 10%
		double theta = -0.1436; // drift BM
		double sigma = 0.12136; // volatility of BM
		double nu = 0.3; // variance rate of gamma time change
		double K = 140;  // K
		double s0 = 100; // s0
		int T = 1;
		MRG32k3a prng = new MRG32k3a();

		// E[X(t)] = theta*t
		int s = 1;
		double[] zeta = new double[s + 1];
		zeta[0] = 0.0;
		for (int j = 1; j <= s; j++)
			zeta[j] = ((double) j / (double) s) * (double) T;


		AsianVG process = new AsianVG(r, sigma, theta, nu, K, s0, s, zeta);
		
		Tally stats = new Tally();
		process.DGBS(10000, prng, stats, true);
		System.out.println(stats.report());
	}
	*/
	public static void testGamma()
	{
		MRG32k3a prng = new MRG32k3a();
		GammaDist g1 = new GammaDist(0.5, 1);
		GammaDist g2 = new GammaDist(0.5, 0.5);
		for(int i = 0; i < 100; i++)
		{
			double u = prng.nextDouble();
			System.out.println(g2.inverseF(u) == g1.inverseF(u)*2);
		}
	
		
		
	}

}
