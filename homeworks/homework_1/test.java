

import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.stat.Tally;


public class test{

  

  public static void main(String[] args)
  {



    int m = 5;
    double arrivalRate = 1;
    int[] queueCapacities = {1, 1, 1};
    double[] serviceTimes = {5.0, 5.0, 5.0};
    MRG32k3a gen1 = new MRG32k3a();
    MRG32k3a gen2 = new MRG32k3a();
    TandemQueue queue = new TandemQueue(3, arrivalRate, queueCapacities, serviceTimes);
    Tally[] waitingTimes = {new Tally(), new Tally(), new Tally()};
    Tally[] blockingTimes = {new Tally(), new Tally()};
    
    queue.simulateFixedNumber(gen1, gen2, 3, waitingTimes, blockingTimes);

    
    
    //new Exercise4();
  }
}