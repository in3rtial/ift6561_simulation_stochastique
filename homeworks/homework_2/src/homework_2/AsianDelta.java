package homework_2;

import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.rng.RandomStream;

public class AsianDelta {

	// f'(x) = f(x + d) - f(x) / d as d -> 0
	Asian process1; // f(x)
	Asian process2; // f(x + d)
	double delta;

	/**
	 * Computes the forward finite difference of the delta parameter (greeks) of
	 * the Asian option.
	 * 
	 * @param shortRate
	 *            compound rate of bank account
	 * @param volatility
	 *            of the Standard Brownian Motion
	 * @param strikePrice
	 *            K, price agreed upon to buy the asset at time T
	 * @param initialPrice
	 *            initial price of the asset
	 * @param T
	 *            number of observation times
	 * @param observationTimes
	 *            vector of observation times
	 * @param delta
	 *            delta on the initial price
	 */
	public AsianDelta(double shortRate, double volatility, double strikePrice,
			double initialPrice, int T, double[] observationTimes, double delta) {
		assert delta >= 0;
		this.delta = delta;

		this.process1 = new Asian(shortRate, volatility, strikePrice,
				initialPrice, T, observationTimes);
		this.process2 = new Asian(shortRate, volatility, strikePrice,
				initialPrice + delta, T, observationTimes);

	}

	/**
	 * Simulates the system(s0) and the system(s0 + delta) to get difference
	 * f(x+d) - f(x), using common random numbers
	 * 
	 * @param n
	 *            number of runs
	 * @param collector
	 *            statistical collector
	 * @param prng
	 *            pseudo-random number generator
	 */
	public void simulateDiffCRN(int n, Tally collector, RandomStream prng) {
		collector.init();
		prng.resetNextSubstream();

		double observations1[] = this.process1.simulateRuns(n, prng);
		double observations2[] = this.process2.simulateRuns(n, prng);
		for (int i = 0; i < n; i++) {
			collector.add(((observations2[i] - observations1[i])/ this.delta));
		}
	}

	/**
	 * Simulates the system(s0) and the system(s0 + delta) to get difference
	 * f(x+d) - f(x), not using common random numbers
	 * 
	 * @param n
	 *            number of runs
	 * @param collector
	 *            statistical collector
	 * @param prng
	 *            pseudo-random number generator
	 */
	public void simulateDiffIRN(int n, Tally collector, RandomStream prng) {
		collector.init();
		prng.resetNextSubstream();
		
		for(int i = 0; i < n; i++)
		{
			collector.add(((this.process2.simulateOneRun(prng) - this.process1.simulateOneRun(prng))/this.delta));
		}
	}
	
}
