import java.util.ArrayList;
import umontreal.iro.lecuyer.rng.RandomStreamBase;
import java.text.DecimalFormat;
import umontreal.iro.lecuyer.probdist.ExponentialDist;

/**
 * Implementation of the Tandem Queue used in exercise 1.4
 * 
 * @author gabriel c-parent
 *
 */
class TandemQueue {

	private final int numberOfservers;
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
	public TandemQueue(double arrivalRate, int[] queueCapacities,
			double[] serviceRates) {
		assert (queueCapacities.length == serviceRates.length);

		this.numberOfservers = queueCapacities.length;
		this.serviceRates = serviceRates;
		this.queueCapacities = queueCapacities;
		this.arrivalRate = arrivalRate;
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
	public TandemQueueResult simulateFixedNumber(RandomStreamBase gen1,
			RandomStreamBase gen2, int numberOfClients) {
		assert numberOfClients >= 0;

		double[] arrivals = new double[numberOfClients];
		double totalTime = 0;
		for (int i = 0; i < numberOfClients; i++) {
			double interval = ExponentialDist.inverseF(arrivalRate,
					gen1.nextDouble());
			totalTime += interval;
			arrivals[i] = totalTime;
		}

		// pass the arrival time array with the generator to the simulate method
		return simulate(gen2, arrivals);
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
	public TandemQueueResult simulateFixedTime(RandomStreamBase gen1,
			RandomStreamBase gen2, double timeCutoff) {
		assert timeCutoff >= 0;
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
				totalTime += newArrival;
			}
		}

		// convert the arraylist into an array of double
		double[] arrivals = new double[arrivalTimes.size()];
		for (int i = 0; i < arrivalTimes.size(); i++) {
			arrivals[i] = (double) arrivalTimes.get(i);
		}

		// pass the arrival time array with the generator to the simulate method
		return simulate(gen2, arrivals);
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
	private TandemQueueResult simulate(RandomStreamBase gen2, double[] arrivals) {
		double[][] departures = new double[numberOfservers + 1][arrivals.length + 1];
		double[] waitings = new double[arrivals.length + 1];
		double[] blockings = new double[arrivals.length + 1];

		// everything is already zero-initialized in Java
		for (int i = 1; i <= arrivals.length; i++) {
			departures[0][i] = arrivals[i - 1];
			for (int j = 1; j < numberOfservers; j++) {
				/* calculate the departure of i from station j */
				double serviceTime = ExponentialDist.inverseF(serviceRates[j],
						gen2.nextDouble());

				double d1 = departures[j - 1][i] + serviceTime; // no waiting,
																// no
				// blocking
				double d2 = departures[j][i - 1] + serviceTime; // waiting, no
																// blocking
				double d3 = 0;
				if (((j + 1) < numberOfservers) && (i - queueCapacities[j + 1]) >= 0) {
					d3 = departures[j + 1][i - queueCapacities[j + 1]]; // blocking
				}
				double departure = Math.max(Math.max(d1, d2), d3);
				departures[j][i] = departure;

				/* calculate the waiting time from the departure */
				double waitingTime = Math.max(0,
						(departures[j][i - 1] - departures[j - 1][i]));
				waitings[i] += waitingTime;

				/* calculate the time spent blocked */
				// D[j][i] − D[j−1][i] − W[j][i] − S[j][i]
				double blockedTime = (((departures[j][i] - departures[j - 1][i]) - waitingTime) - serviceTime);

				blockings[i] += blockedTime;

				DecimalFormat df = new DecimalFormat("#.00");
				System.out.println("(" + i + "," + j + ")" + " "
						+ df.format(departures[j - 1][i]) + "\tw "
						+ df.format(waitingTime) + "\ts "
						+ df.format(serviceTime) + "\tb "
						+ df.format(blockedTime) + "\td "
						+ df.format(departure));
				if (blockedTime < -1.1) {
					System.exit(1);
				}
			}

		}
		System.out.println("simulation finished");
		return new TandemQueueResult(arrivals, waitings, blockings, departures);
	}

}
