import umontreal.iro.lecuyer.stat.TallyStore;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.util.io.TextDataWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



/**
 * exercise 4 
 * Tandem Queue
 */

class Exercise4 {
	private final TallyStore[] waitingAverages;
	private final TallyStore[] blockedTimes;
	private final double[] waitingConfidenceIntervals95;
	private final double[] blockedConfidenceIntervals95;

	public Exercise4(boolean dumpData, int numberOfSimulations) {
		// use Integer.MAX_VALUE to symbolize positive infinity
		TandemQueue queue = new TandemQueue(3, 1, new int[] {
				Integer.MAX_VALUE, 4, 8 }, new double[] { 1.5, 1.2, 1.2 });

		// initialize the TallyStore objects
		TallyStore w1 = new TallyStore("Waiting time station 1");
		TallyStore w2 = new TallyStore("Waiting time station 2");
		TallyStore w3 = new TallyStore("Waiting time station 3");
		TallyStore b1 = new TallyStore("Blocked time station 1");
		TallyStore b2 = new TallyStore("Blocked time station 2");

		this.waitingAverages = new TallyStore[] { w1, w2, w3 };
		this.blockedTimes = new TallyStore[] { b1, b2 };

		// prngs
		MRG32k3a gen1 = new MRG32k3a();
		MRG32k3a gen2 = new MRG32k3a();

		// run the simulations
		for (int i = 0; i < numberOfSimulations; i++) {
			queue.simulateFixedTime(gen1, gen2, 1000, waitingAverages,
					blockedTimes);
		}


		// dump the data as csv
		if(dumpData == true)
		{
			try{
			FileWriter FW = new FileWriter("exercise2.csv", false);
			// write the waiting data
			for(int i = 0; i < queue.getNumberOfServers(); i++)
			{
				double[] data = waitingAverages[i].getArray();
				FW.write("W,"+i);
				for(int j = 0; j < data.length; j++)
				{
					FW.write(","+data[j]);
				}
				FW.write("\n");
			}
			// write the blocking data
			for(int i = 0; i < queue.getNumberOfServers() - 1; i++)
			{
				double[] data = blockedTimes[i].getArray();
				FW.write("B,"+i);
				for(int j = 0; j < data.length; j++)
				{
					FW.write(","+data[j]);
				}
				FW.write("\n");
			}
			
			FW.close();}
			catch (IOException e) {
				System.out.println("Well, things don't always work out...");
			}
		}
		
		// assign the confidence intervals
		waitingConfidenceIntervals95 = new double[queue.getNumberOfServers()];
		blockedConfidenceIntervals95 = new double[queue.getNumberOfServers() - 1];
		for(int i = 0; i < queue.getNumberOfServers(); i++)
		{
			waitingConfidenceIntervals95[i] = waitingAverages[i].getConfidenceLevel();
			
			if(i != queue.getNumberOfServers() - 1)
			{
				blockedConfidenceIntervals95[i] = blockedTimes[i].getConfidenceLevel();
			}
		}
		
	}
}
