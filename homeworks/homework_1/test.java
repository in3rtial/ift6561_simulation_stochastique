

import java.math.BigInteger;
import java.util.Collections;
import umontreal.iro.lecuyer.rng.MRG32k3a;

public class test{

  

  public static void main(String[] args)
  {


    // tandem queue test
    int m = 5;
    double arrivalRate = 0.01;
    int[] queueCapacities = {1, 1, 1, 1, 1};
    double[] serviceTimes = {5.0, 5.0, 5.0, 5.0, 5.0};
    MRG32k3a gen1 = new MRG32k3a();
    MRG32k3a gen2 = new MRG32k3a();
    TandemQueue t = new TandemQueue(m, arrivalRate, queueCapacities, serviceTimes);
    TandemQueueResult r = t.simulateFixedNumber(gen1, gen2, 3);
//     r.printResults();
    
    
  }
}