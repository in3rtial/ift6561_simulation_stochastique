package homework_2;

import org.junit.*;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.util.Num;
import java.lang.Math;

/**
 * unit test for the gumbel distribution
 * @author gab
 *
 */
public class GumbelTest {

	@Test
	public void testDistribution() {

		// initialize
		final int sampleSize = 100000;
		final double mu = 42;
		final double B = 42;

		double[] sample = new double[sampleSize];
		MRG32k3a prng = new MRG32k3a();
		Gumbel distribution = new Gumbel(mu, B);

		// test variables
		double sampleMean = 0;
		double epsilon = 0.01; // accepted precision

		// run
		for (int i = 0; i < sampleSize; i++) {
			sample[i] = distribution.getGumbel(prng);
			sampleMean += sample[i] / sampleSize;
		}

		// test results
		double sampleVariance = 0;
		for (int i = 0; i < sampleSize; i++) {
			sampleVariance += (sample[i] - sampleMean)
					* (sample[i] - sampleMean);
		}
		sampleVariance = sampleVariance / (sampleSize - 1);

		System.out.println("Variance = " + sampleVariance);
		System.out.println("Expected = " + ((Math.PI * Math.PI) / 6) * (B * B));

		System.out.println("Mean     = " + sampleMean);
		System.out.println("Expected = " + (mu + B * Num.EULER));

		// check that the average is in the expected range
		Assert.assertTrue(sampleMean - (epsilon * sampleVariance) <= (mu + B * Num.EULER)
				&& (sampleMean + (epsilon * sampleVariance) >= (mu + B * Num.EULER)));

		// check that the variance is in the expected range
		Assert.assertTrue(sampleVariance - (epsilon * sampleVariance) <= ((Math.PI * Math.PI) / 6) * (B * B)
				&& (sampleVariance + (epsilon * sampleVariance) >= ((Math.PI * Math.PI) / 6) * (B * B)));
	}
}
