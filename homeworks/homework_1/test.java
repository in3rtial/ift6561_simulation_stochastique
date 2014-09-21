

import java.math.BigInteger;
import java.util.Collections;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.probdist.ExponentialDist;

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
    TandemQueue t = new TandemQueue(arrivalRate, queueCapacities, serviceTimes);
    TandemQueueResult r = t.simulateFixedNumber(gen1, gen2, 3);
    System.out.println(ExponentialDist.inverseF(1, 0.5));
    
    
    
  }
}