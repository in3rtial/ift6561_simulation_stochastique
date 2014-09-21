

import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.stat.Tally;


public class test{

  

  public static void main(String[] args)
  {



    int m = 5;
    double arrivalRate = 10;
    int[] queueCapacities = {Integer.MAX_VALUE, 1, 1};
    double[] serviceRates = {5.0, 5.0, 5.0, 5.0};
    MRG32k3a gen1 = new MRG32k3a();
    MRG32k3a gen2 = new MRG32k3a();
    TandemQueue queue = new TandemQueue(3, arrivalRate, queueCapacities, serviceRates);
    Tally[] waitingTimes = {new Tally(), new Tally(), new Tally()};
    Tally[] blockingTimes = {new Tally(), new Tally()};
    
    queue.simulateFixedNumber(gen1, gen2, 3, waitingTimes, blockingTimes);

    
    
    new Exercise4();
  }
}