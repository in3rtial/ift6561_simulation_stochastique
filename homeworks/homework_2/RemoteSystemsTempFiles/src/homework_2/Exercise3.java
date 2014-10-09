package homework_2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import umontreal.iro.lecuyer.rng.MRG32k3a;

public class Exercise3 {

	public Exercise3() {
		runExercise3(true);
	}

	public void runExercise3(boolean dumpData) {
		double[] serviceRates = { 0.5, 0.4, 0.4 };
		double arrivalRate = 1.0;
		int numberOfStations = 3;
		int[] numberOfServers = { 3, 3, 3 };
		int[] queueSizes = { Integer.MAX_VALUE, 4, 8 };
		MRG32k3a prng1 = new MRG32k3a();
		MRG32k3a prng2 = new MRG32k3a();

		TandemQueueEv queue = new TandemQueueEv(arrivalRate, numberOfStations,
				numberOfServers, serviceRates, queueSizes, prng1, prng2);

		int numberOfSimulations = 1000;

		// data collectors for E[time] (time statistics at each station)
		ArrayList<double[]> waitTimes = new ArrayList<double[]>();
		ArrayList<double[]> blockTimes = new ArrayList<double[]>();

		// data collector for E[|clients|] (number of clients)
		int[] numberOfClients = new int[numberOfSimulations];

		for (int i = 0; i < numberOfSimulations; i++) {
			queue.simulateOneRun(1000.0);
			// extract the data and summarize it
			waitTimes.add(queue.waitingTimes.clone());
			blockTimes.add(queue.blockedTimes.clone());
			numberOfClients[i] = queue.numberOfClients;
		}

		// dump the data as csv
		if (dumpData == true) {
			try {
				FileWriter FW = new FileWriter("exercise3_wait.csv", false);
				// write the waiting data
				for (int i = 0; i < waitTimes.size(); i++) {
					double[] data = waitTimes.get(i);
					for (int j = 0; j < data.length - 1; j++) {
						FW.write(data[j] + ", ");

					}
					FW.write(data[data.length - 1] + "\n");
				}
				FW.close();
			} catch (IOException e) {
				System.out.println("Well, things don't always work out...");
			}

			// write the blocking data
			try {
				FileWriter FW = new FileWriter("exercise3_blocking.csv", false);
				// write the waiting data
				for (int i = 0; i < blockTimes.size(); i++) {
					double[] data = blockTimes.get(i);
					for (int j = 0; j < data.length - 1; j++) {
						FW.write(data[j] + ", ");
					}
					FW.write(data[data.length - 1] + "\n");
				}
				FW.close();
			} catch (IOException e) {
				System.out.println("Well, things don't always work out...");
			}
			
			// writhe the number of clients data
			try {
				FileWriter FW = new FileWriter("exercise3_numclients.csv",
						false);
				// write the waiting data
				for (int j = 0; j < numberOfClients.length; j++) {
					FW.write(numberOfClients[j] + "\n");
				}
				FW.close();
			} catch (IOException e) {
				System.out.println("Well, things don't always work out...");
			}
		}
	}
	
	public double[] summarize(ArrayList<double[]> list)
	{
		double[] sum = new double[list.get(0).length];
		for(int i = 0; i < list.size(); i++)
		{
			for(int j = 0; j < list.get(0).length; j++)
			{
				sum[j] += list.get(i)[j];
			}
		}
		
		return sum;
	}
	
	public static void main(String[] args)
	{
		new Exercise3();
	}
}