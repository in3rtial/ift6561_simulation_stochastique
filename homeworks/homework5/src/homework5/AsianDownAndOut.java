package homework5;

import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.stat.Tally;

public class AsianDownAndOut extends Asian {
	double barrier;
	
	public AsianDownAndOut(double shortRate, double volatility,
			double strikePrice, double initialPrice, int T,
			double[] observationTimes, double barrier) {
		super(shortRate, volatility, strikePrice, initialPrice, T,
				observationTimes);
		this.barrier = barrier;
	}
	
	/**
	 * Computes and returns the discounted option payoff.
	 * WITH THE DOWN AND OUT BARRIER
	 * 
	 * @return payoff of the option
	 */
	public double getPayoffDownAndOut() {
		double value = 0; // to get the pesky warning off
		for (int j = 1; j <= T; j++)
		{
			value = Math.exp(logS[j]);
			if(value < barrier)
			{
				return 0.0;
			}
		}
		if (value > strikePrice)
			return discount * (value - strikePrice);
		else
			return 0.0;
	}
	
	public double getPayOffDownAndOutCV()
	{
		
	}

	public void simulateRuns(int n, RandomStream stream, Tally stat) {
		stat.init();
		for (int i = 0; i < n; i++) {
			generatePath(stream);
			stat.add(getPayoffDownAndOut());
		}
	}
	
	
	
	public static void main(String[] args)
	{
		int s = 12;
		double barrier = 150;
		// choose the observation times
		double[] observationTimes = new double[s + 1];
		observationTimes[0] = 0.0;
		for (int j = 1; j <= s; j++)
			observationTimes[j] = (double) j / (double) s;
		AsianDownAndOut process = new AsianDownAndOut(0.08, 0.2, 100.0, 100.0, s, observationTimes, barrier);
		Tally statValue = new Tally("Stats on value of Asian option");

		int n = 20;
		process.simulateRuns(n, new MRG32k3a(), statValue);
		statValue.setConfidenceIntervalStudent();
		System.out.println(statValue.report(0.95, 3));
	}
}