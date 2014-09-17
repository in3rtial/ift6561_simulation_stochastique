import java.util.ArrayList;
import umontreal.iro.lecuyer.rng.RandomStreamBase;
import java.text.DecimalFormat;

/**
 * Implementation of the Tandem Queue used in exercise 1.4
 * 
 * @author gabriel c-parent
 *
 */
class TandemQueue {

	private final int m; // number of servers in the system
	private final double[] serviceRate; // average processing time at node i
	private final int[] capacities; // maximum queue size at
	private final double rate; // arrival rate of the clients

	/**
	 * Tandem Queue constructor
	 * 
	 * @param numberOfServers
	 *            number of servers
	 * @param arrivalRate
	 *            arrival rate of clients
	 * @param queueCapacities
	 *            capacity of each queues
	 * @param serviceTimes
	 *            service time for each server
	 */
	public TandemQueue(int numberOfServers, double arrivalRate,
			int[] queueCapacities, double[] serviceTimes) {
		// assert (numberOfServers+1) == queueCapacities.length
		// assert (numberOfServers+1) == serviceTimes.length
		m = numberOfServers + 1;
		serviceRate = serviceTimes;
		capacities = queueCapacities;
		rate = arrivalRate;
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
			double interval = (double) (-rate * (Math.log(gen1.nextDouble())));
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
	public TandemQueueResult simulateFixedTime(RandomStreamBase gen1, // simulates
																		// A[i],
																		// arrivals
			RandomStreamBase gen2, // simulates S[i][j], service
			double timeCutoff) // total time cutoff
	{
		assert timeCutoff >= 0;
		ArrayList<Double> arrivalTimes = new ArrayList<Double>();
		double totalTime = 0;

		while (true) {
			double newArrival = (double) (-rate * (Math
					.log(gen1.nextDouble())));
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

	private TandemQueueResult simulate(RandomStreamBase gen2, // simulates
																// service times
			double[] arrivals) // arrivals of the clients in the system
	{
		// this is the function called by both simulateFixedNumber and
		// simulateFixedTime
		// who pass the generator for the service times and the precalculated
		// arrival times
		double[][] D = new double[m + 1][arrivals.length + 1];
		double[] W = new double[arrivals.length + 1];
		double[] B = new double[arrivals.length + 1];

		// everything is already zero-initialized in Java
		for (int i = 1; i <= arrivals.length; i++) {
			D[0][i] = arrivals[i - 1];
			for (int j = 1; j <= m; j++) {
				/* calculate the departure of i from station j */
				double serviceTime = -1 * (serviceRate[j]) * Math.log(gen2.nextDouble());

				double d1 = D[j - 1][i] + serviceTime; // no waiting, no
														// blocking
				double d2 = D[j][i - 1] + serviceTime; // waiting, no blocking
				double d3 = 0;
				if ((i - capacities[j + 1]) >= 0 && (j + 1) <= m) {
					d3 = D[j + 1][i - capacities[j + 1]]; // blocking
				}
				double departure = Math.max(Math.max(d1, d2), d3);
				D[j][i] = departure;

				/* calculate the waiting time from the departure */
				double waitingTime = Math.max(0, (D[j][i - 1] - D[j - 1][i]));
				W[i] += waitingTime;

				/* calculate the time spent blocked */
				// D[j][i] − D[j−1][i] − W[j][i] − S[j][i]
				double blockedTime = (((D[j][i] - D[j - 1][i]) - waitingTime) - serviceTime);

				B[i] += blockedTime;

				DecimalFormat df = new DecimalFormat("#.00");
				System.out.println("(" + i + "," + j + ")" + " "
						+ df.format(D[j - 1][i]) + "\tw "
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
		return new TandemQueueResult(arrivals, W, B, D);
	}

}
