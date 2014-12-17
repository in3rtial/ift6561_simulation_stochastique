package ExamFinal;

import umontreal.iro.lecuyer.hups.BakerTransformedPointSet;
import umontreal.iro.lecuyer.hups.KorobovLattice;
import umontreal.iro.lecuyer.hups.PointSet;
import umontreal.iro.lecuyer.hups.SobolSequence;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.stat.Tally;

public class part2A {

	public part2A() { }
	
	
	// STATEMENT
	/*
	 * (a) Ecrivez un programme permettant de reproduire a peu pres les
	 * resultats du tableau 6.12 des notes et refaites les experiences pour la
	 * colonne n = 2^14 , avec m = 32 repetitions, pour les quatre methodes RQMC
	 * indiques dans le tableau. Bien sur, vous n’obtiendrez pas exactement
	 * les memes facteurs, car ces valeurs sont des estimations bruitees.
	 */
	public static void main(String[] args)
	{
		double r = 0.1; // short rate of 10%
		double theta = -0.1436; // drift BM
		double sigma = 0.12136; // volatility of BM
		double nu = 0.3; // variance rate of gamma time change
		double K = 101; // K
		double s0 = 100; // s0
		int T = 1;
		MRG32k3a prng = new MRG32k3a();

		// E[X(t)] = theta*t
		int s = 16;
		double[] zeta = new double[s + 1];
		zeta[0] = 0.0;
		for (int j = 1; j <= s; j++)
			zeta[j] = ((double) j / (double) s) * (double) T;

		AsianVG_RQMC process = new AsianVG_RQMC(r, sigma, theta, nu, K, s0, s,
				zeta);

		// experimental settings
		double varMC = 28.89;
		int m = 32; // number of randomizations

		// INITIALIZE THE POINT SETS
		// strategy 1
		// Sobol’ nets with a random digital shift only (Sob-S)
		SobolSequence PS1 = new SobolSequence(14, 31, s * 2);
		PS1.addRandomShift(prng);

		// strategy 2
		// Sobol’ nets with a left matrix scramble followed by a digital shift
		// (Sob-LMS-S)
		SobolSequence PS2 = new SobolSequence(14, 31, s * 2);
		PS2.leftMatrixScramble(prng);
		PS2.addRandomShift(prng);

		// strategy 3
		// Korobov lattice rules with a random shift modulo 1 (Kor-S)
		KorobovLattice PS3 = new KorobovLattice(16381, 5693, s * 2);

		// strategy 4
		// Korobov lattice rules with a random shift modulo 1 followed by a
		// baker transformation (Kor-S-B)
		BakerTransformedPointSet PS4 = new BakerTransformedPointSet(PS3);
		PS4.addRandomShift(prng);
		PS3.addRandomShift(prng);

		// get the stats for the different point sets
		PointSet[] PS = new PointSet[] { PS1, PS2, PS3, PS4 };
		Tally[][] Tallies = new Tally[4][4];

		// initialize the tallies and perform the experiments
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (j == 0) {
					// BGSS
					Tallies[i][j] = new Tally("PS" + (i + 1) + " BGSS");
					process.BGSS_RQMC(m, PS[i], prng, Tallies[i][j]);
					System.out.println(Tallies[i][j].report());
					double varQMC = PS[i].getNumPoints()
							* Tallies[i][j].variance();
					System.out.println("Variance ratio = " + varMC / varQMC
							+ "\n\n#############################\n");
				} else if (j == 1) {
					// BGBS
					Tallies[i][j] = new Tally("PS" + (i + 1) + " BGBS");
					process.BGBS_RQMC(m, PS[i], prng, Tallies[i][j]);
					System.out.println(Tallies[i][j].report());
					double varQMC = PS[i].getNumPoints()
							* Tallies[i][j].variance();
					System.out.println("Variance ratio = " + varMC / varQMC
							+ "\n\n#############################\n");
				} else {
					// DGBS
					Tallies[i][j] = new Tally("PS" + (i + 1) + " DGBS");
					process.DGBS_RQMC(m, PS[i], prng, Tallies[i][j]);
					System.out.println(Tallies[i][j].report());
					double varQMC = PS[i].getNumPoints()
							* Tallies[i][j].variance();
					System.out.println("Variance ratio = " + varMC / varQMC
							+ "\n\n#############################\n");
				}
			}
		}
	}
}
