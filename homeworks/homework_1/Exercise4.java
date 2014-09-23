import umontreal.iro.lecuyer.stat.TallyStore;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import java.io.FileWriter;
import java.io.IOException;


/**
 * exercise 4 
 * Tandem Queue
 * chose to dump the tally stored data into csv file (exercise2.csv)
 * and plot it using python's graphic libs (easier and prettier).
 */

class Exercise4 {
	public final TallyStore[] waitingAverages;
	public final TallyStore[] blockedAverages;

	/**
	 * launches the exercise 4
	 * @param dumpData true if data must be dumped in exercise.csv file
	 * @param numberOfSimulations number of runs of the simulation
	 */
	public Exercise4(boolean dumpData, int numberOfSimulations) {
		// use Integer.MAX_VALUE to symbolize positive infinity
		TandemQueue queue = new TandemQueue(3, 1, new int[] {
				Integer.MAX_VALUE, 4, 8 }, new double[] { 1.5, 1.2, 1.2 });

		// initialize the TallyStore objects
		TallyStore w1 = new TallyStore("Average waiting times for station 1");
		TallyStore w2 = new TallyStore("Average waiting times for station 2");
		TallyStore w3 = new TallyStore("Average waiting times for station 3");
		TallyStore b1 = new TallyStore("Average blocking times for station 1");
		TallyStore b2 = new TallyStore("Average blocking times for station 2");

		this.waitingAverages = new TallyStore[] { w1, w2, w3 };
		this.blockedAverages = new TallyStore[] { b1, b2 };

		// prngs
		MRG32k3a gen1 = new MRG32k3a();
		MRG32k3a gen2 = new MRG32k3a();

		// run the simulations
		for (int i = 0; i < numberOfSimulations; i++) {
			queue.simulateFixedTime(gen1, gen2, 1000, waitingAverages,
					blockedAverages);
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
				double[] data = blockedAverages[i].getArray();
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
		
	}
}
