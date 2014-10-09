package homework_2;

import java.lang.Math;
import umontreal.iro.lecuyer.rng.RandomStreamBase;

public class Gumbel {
	double u;
	double B;

	/**
	 * constructor for gumbel distribution
	 * 
	 * @param u
	 *            location
	 * @param B
	 *            scale
	 */
	public Gumbel(double u, double B) {
		this.u = u;
		this.B = B;
	}

	/**
	 * return a gumbel distributed variable from a uniformly distributed random
	 * variable
	 * 
	 * @param prng
	 *            pseudorandom generator
	 * @return gumbel distributed random variable
	 */
	public double getGumbel(RandomStreamBase prng) {
		double uniform = prng.nextDouble();
		return (this.u - this.B * Math.log(-Math.log(uniform)));
	}

}
