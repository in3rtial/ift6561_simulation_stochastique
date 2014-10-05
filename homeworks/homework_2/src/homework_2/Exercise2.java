package homework_2;

import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.stat.Tally;

public class Exercise2 {

	/**
	 * calculates
	 */
	public static void runExercise2c() {

		int s = 12;

		// choose the observation times
		double[] observationTimes = new double[s + 1];
		observationTimes[0] = 0.0;
		for (int j = 1; j <= s; j++)
			observationTimes[j] = (double) j / (double) s;

		int n = 10000;
		int deltaLength = 15;

		double[] deltas = new double[deltaLength];
		deltas[0] = Math.pow(10, -15);

		for (int i = 1; i < deltaLength; i++) {
			deltas[i] = deltas[i - 1] * 10;
		}

		Tally[] collectorsIRN = new Tally[deltaLength];
		Tally[] collectorsCRN = new Tally[deltaLength];

		MRG32k3a generator = new MRG32k3a();

		for (int i = 0; i < deltaLength; i++) {
			//
			collectorsIRN[i] = new Tally("Delta = " + deltas[i] + " IRN");
			collectorsCRN[i] = new Tally("Delta = " + deltas[i] + " CRN");
			AsianDelta process = new AsianDelta(0.05, 0.5, 100.0, 100.0, s,
					observationTimes, deltas[i]);

			process.simulateDiffIRN(n, collectorsIRN[i], generator);
			process.simulateDiffCRN(n, collectorsCRN[i], generator);

			collectorsIRN[i].setConfidenceIntervalStudent();
			collectorsCRN[i].setConfidenceIntervalStudent();

		}

		// print the reports of the estimation of derivatives under common
		// random numbers
		for (int i = 0; i < deltaLength; i++) {
			System.out.println(collectorsCRN[i].report());
		}
		// print the reports of the estimation of derivatives under independent
		// random numbers
		for (int i = 0; i < deltaLength; i++) {
			System.out.println(collectorsIRN[i].report());
		}

	}
	
	
	public static void runExercise2e()
	{
		// calculate as in b), but using stochastic gradient
		
		// Asian process parameters
		int s = 12;

		double[] observationTimes = new double[s + 1];
		observationTimes[0] = 0.0;
		for (int j = 1; j <= s; j++)
			observationTimes[j] = (double) j / (double) s;

				
		// need the fixed stream for the GBM path
		double[] fixedStream= new double[s];
		
		MRG32k3a generator = new MRG32k3a();
		for(int i =0 ; i< s; i++)
		{
			fixedStream[i] = generator.nextDouble();
		}
		
				
		Asian process = new Asian(0.05, 0.5, 100.0, 100.0, s, observationTimes);
		
		// f(x)
		double referenceObservation = process.simulateOneRunFixed(fixedStream);
		
		
		// array of f(x + d)
		int deltaLength = 15; // we'll work with 15 different observations
		double[] observations = new double[deltaLength];
		double[] deltas = new double[deltaLength];
		deltas[0] = Math.pow(10, -15);

		for (int i = 1; i < deltaLength; i++) {
			deltas[i] = deltas[i - 1] * 10;
		}

		for(int i = 0; i < deltaLength; i++)
		{
			process.changeInitialPrice(100.0 + deltas[i]);
			observations[i] = process.simulateOneRunFixed(fixedStream);
		}
		
		for(int i = 0; i < deltaLength; i++)
		{
			System.out.println(observations[i]);
		}
		
	}
	

	public static void main(String[] args) {
//		runExercise2c();
		runExercise2e();
	}

}
