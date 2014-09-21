import java.util.ArrayList;
import umontreal.iro.lecuyer.rng.RandomStreamBase;
import java.text.DecimalFormat;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.stat.Tally;

/**
 * Implementation of the Tandem Queue used in exercise 1.4
 * 
 * @author gabriel c-parent
 *
 */
class TandemQueue {

	private final int numberOfServers;
	private final double[] serviceRates;
	private final int[] queueCapacities;
	private final double arrivalRate;

	/**
	 * Tandem Queue constructor
	 * 
	 * @param arrivalRate
	 *            arrival rate of clients from the origin. exponential
	 *            distribution with mean 1/lambda.
	 * @param queueCapacities
	 *            capacity of each queues excluding the origin (infinite)
	 * @param serviceTimes
	 *            service time for each server excluding the origin. exponential
	 *            distribution with mean 1 / serviceTimes[i].
	 */
	public TandemQueue(int m, double arrivalRate, int[] queueCapacities,
			double[] serviceRates) {
		assert (queueCapacities.length == serviceRates.length);
		assert (m == queueCapacities.length);
		assert queueCapacities[0] > 100000; // could check for infinity if
											// double...

		this.arrivalRate = arrivalRate;
		this.numberOfServers = m;
		this.queueCapacities = queueCapacities;
		this.serviceRates = serviceRates;

	}

	/**
	 * get the number of servers in the queueing system
	 * 
	 * @return number of servers
	 */
	public int getNumberOfServers() {
		return this.numberOfServers;
	}

	/**
	 * Simulates a tandem queue with fixed number of clients in the system.
	 * 
	 * The first generator is used to generate the arrival times of the clients
	 * in the system and then the simulateSystem method is called.
	 * 
	 * @param gen1
	 *            first generator, instance of a RandomStreamBase
	 * @param gen2
	 *            second generator, instance of a RandomStreamBase
	 * @param numberOfClients
	 *            number of clients in the system
	 * @return TandemQueueResult object, which can be probed for later analysis
	 */
	public void simulateFixedNumber(RandomStreamBase gen1,
				                    RandomStreamBase gen2,
				                    int numberOfClients,
				                    Tally[] waitingTally,
				                    Tally[] blockingTally) {
		assert numberOfClients >= 0 : "number of clients must be superior to zero";
		assert (waitingTally.length == numberOfServers) : "incorrect number of waiting time collectors";
		assert (blockingTally.length + 1 == numberOfServers) : "incorrect number of blocked time collectors";

		double[] arrivals = new double[numberOfClients];
		double totalTime = 0;
		for (int i = 0; i < numberOfClients; i++) {
			double interval = ExponentialDist.inverseF(arrivalRate,
					gen1.nextDouble());
			totalTime += interval;
			arrivals[i] = totalTime;
			
		}

		// pass the arrival time array with the generator to the simulate method
		simulate(gen2, arrivals, waitingTally, blockingTally);

	}

	/**
	 * Simulates a tandem queue with fixed number of clients in the system,
	 * which we infer the number by precalculating the arrivals.
	 * 
	 * The first generator is used to generate the arrival times of the clients
	 * in the system and then the simulateSystem method is called.
	 * 
	 * @param gen1
	 *            first generator, instance of a RandomStreamBase
	 * @param gen2
	 *            second generator, instance of a RandomStreamBase
	 * @param numberOfClients
	 *            number of clients in the system
	 * @return TandemQueueResult object, which can be probed for later analysis
	 */
	public void simulateFixedTime(RandomStreamBase gen1, RandomStreamBase gen2,
			double timeCutoff, Tally[] waitingTimes, Tally[] blockedTimes) {
		assert timeCutoff >= 0 : "time limit cannot be inferior to zero";
		assert (waitingTimes.length == numberOfServers) : "incorrect number of waiting time collectors";
		assert (blockedTimes.length + 1 == numberOfServers) : "incorrect number of blocked time collectors";

		ArrayList<Double> arrivalTimes = new ArrayList<Double>();
		double totalTime = 0;

		while (true) {
			double newArrival = ExponentialDist.inverseF(arrivalRate,
					gen1.nextDouble());
			if ((newArrival + totalTime) > timeCutoff) {
				break;
			} else {
				totalTime += newArrival;
				arrivalTimes.add(totalTime);
			}
		}

		// convert the arraylist into an array of double
		double[] arrivals = new double[arrivalTimes.size()];
		for (int i = 0; i < arrivalTimes.size(); i++) {
			arrivals[i] = (double) arrivalTimes.get(i);
		}

		// pass the arrival time array with the generator to the simulate method
		simulate(gen2, arrivals, waitingTimes, blockedTimes);
	}

	/**
	 * simulate method is called by other two (fixed number and fixed time) and
	 * does the heavy lifting
	 * 
	 * @param gen2
	 *            the generator that calculates the service times
	 * @param arrivals
	 *            time of arrival of the clients at the first server
	 * @return structure that keeps statistics on the simulation
	 */
	private void simulate(RandomStreamBase gen2,
			              double[] arrivals,
			              Tally[] waitingTallies,
			              Tally[] blockingTallies) {
		double[][] departures = new double[numberOfServers + 1][arrivals.length + 1];
		double[] totalWaiting = new double[numberOfServers + 1];
		double[] totalBlocking = new double[numberOfServers + 1];

		for (int i = 1; i <= arrivals.length; i++) {
			departures[0][i] = arrivals[i - 1];
			for (int j = 1; j < numberOfServers + 1; j++) {
				/* calculate the departure of i from station j */
				double serviceTime = ExponentialDist.inverseF(serviceRates[j - 1],
						gen2.nextDouble());

				// APPLY THE RECURRENCE
				// no waiting, no blocking
				double d1 = departures[j - 1][i] + serviceTime;
				// waiting, no blocking
				double d2 = 0;
				if(i - 1 >= 0)
				{
					d2 = departures[j][i - 1] + serviceTime;
				}
				// blocking
				double d3 = 0;
				if ((j + 1 < numberOfServers)
						&& (i - queueCapacities[j + 1]) >= 0) {
					d3 = departures[j + 1][i - queueCapacities[j + 1]];
				}
				double departure = Math.max(Math.max(d1, d2), d3);
				departures[j][i] = departure;

				/* calculate the waiting time from the departure */
				double waitingTime = Math.max(0,
						(departures[j][i - 1] - departures[j - 1][i]));

				/* calculate the time spent blocked */
				// D[j][i] − D[j−1][i] − W[j][i] − S[j][i]
				double blockedTime = (((departures[j][i] - departures[j - 1][i]) - waitingTime) - serviceTime);


				totalWaiting[j] += waitingTime;
				totalBlocking[j] += blockedTime;

//				DecimalFormat df = new DecimalFormat("#.00");
//				System.out.println("(" + i + "," + j + ")" + " "
//						+ df.format(departures[j - 1][i]) + "\tw "
//						+ df.format(waitingTime) + "\ts "
//						+ df.format(serviceTime) + "\tb "
//						+ df.format(blockedTime) + "\td "
//						+ df.format(departure));

			}

		}

		// add the average waiting times to the statistics collectors
		for (int j = 0; j < numberOfServers; j++) {
			waitingTallies[j].add(totalWaiting[j]);
			if (j != numberOfServers - 1) {
				blockingTallies[j].add(totalBlocking[j]);
			}
		}

		System.out.println("simulation finished");
	}

}
