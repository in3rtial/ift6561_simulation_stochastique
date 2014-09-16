/* implementation of the tandem queue for exercise 4 */

// import umontreal.iro.lecuyer.stats.Tally;
import java.util.ArrayList;


import umontreal.iro.lecuyer.rng.RandomStreamBase;


/*
  NOTATION

  Ti = arrival time of customer i at first queue, for i ≥ 1, and T 0 = 0;
  Ai = T i − T i−1 = time between arrivals i − 1 and i;
  Wj,i = waiting time in queue j for customer i;
  Sj,i = service time at station j for customer i;
  Bj,i = blocked time at station j for customer i;
  Dj,i = departure time from station j for customer i
*/


/*
  as far as I can understand, the departure of the next individual
  is dependent only on the previous, and so on.

  Therefore, as in dynamic programming, we don't actually need the matrix, because
  we only need the departure times of the previous individual in the system (and
  obviously the information about the system e.g. the size of the queues).

*/

class TandemQueue
{
  /* */
  private final int m; // the number of nodes in the system
  private final double u; // average processing time at node i
  private final int c; // maximum queue size at
  private final double lambda; // arrival rate of the clients

  public TandemQueue(int numberOfNodes,
                     double arrivalRate,  // arrival rate, follows exp law
                     int queueCapacities,  // size of the queues (for blocking)
                     double serviceTimes)  // service time of the servers
  {
    m = numberOfNodes;
    u = serviceTimes;
    c = queueCapacities;
    lambda = arrivalRate;
  }


  public TandemQueueResult simulateFixedNumber(RandomStreamBase gen1, // simulates A[i], arrivals
                                  RandomStreamBase gen2, // simulates S[i][j], service
                                  int max_clients)   // number of clients cutoff
  {
    // in this case, supposing that the number of arrivals and the number of
    // queues are not enormous, we can preallocate the random numbers in a 
    assert max_clients >= 0;

    double[] arrivals = new double[max_clients];
    double totalTime = 0;
    for(int i=0; i < max_clients; i++)
    {
      double interval = (double)(-lambda * (Math.log(gen1.nextDouble())));
      totalTime += interval;
      arrivals[i] = totalTime;
    }

    // pass the arrival time array with the generator to the simulate method
    return simulate(gen2, arrivals);
  }


  public TandemQueueResult simulateFixedTime(RandomStreamBase gen1, // simulates A[i], arrivals
                                             RandomStreamBase gen2, // simulates S[i][j], service
                                             double timeCutoff) // total time cutoff
  {
    assert timeCutoff >= 0;
    ArrayList<Double> arrivalTimes = new ArrayList<Double>();
    double totalTime = 0;

    while(true)
    {
      double newArrival = (double)(-lambda * (Math.log(gen1.nextDouble())));
      if((newArrival + totalTime) > timeCutoff)
      {
        break;
      }
      else
      {
        totalTime += newArrival;
        arrivalTimes.add(totalTime);
        totalTime += newArrival;
      }
    }

    // convert the arraylist into an array of double
    double[] arrivals = new double[arrivalTimes.size()];
    for(int i=0; i < arrivalTimes.size(); i++)
    {
      arrivals[i] = (double)arrivalTimes.get(i);
    }


    // pass the arrival time array with the generator to the simulate method
    return simulate(gen2, arrivals);
  }

  private TandemQueueResult simulate(RandomStreamBase gen2, // simulates service times
                                     double[] arrivals) // arrivals of the clients in the system
  {
    // this is the function called by both simulateFixedNumber and simulateFixedTime
    // who pass the generator for the service times and the precalculated arrival times
    double[][] D = new double[m+1][arrivals.length+1];
    double[] W = new double[arrivals.length+1];
    double[] B = new double[arrivals.length+1];

    // everything is already zero-initialized in Java
    for(int i = 1; i <= arrivals.length; i++)
    {
      System.out.println("i = " + i);
      D[0][i] = arrivals[i-1];
      for(int j = 1; j < m; j++)
      {
        System.out.println("j = " + j);

        /* calculate the departure of i from station j */
        double serviceTime = -(u) * Math.log(gen2.nextDouble());
        double departure = Math.max((D[j-1][i] + serviceTime), (D[j][i-1] + serviceTime));
        if (i-c >0)
        {
          departure = Math.max(departure, D[j+1][i-c]);
        }
        D[j][i] = departure;

        /* calculate the waiting time from the departure*/
        double waitingTime = Math.max(0, (D[j][i-1] - D[j-1][i]));
        if(i-c > 0)
        {
          waitingTime = Math.max(waitingTime, D[j+1][i-c]);
        }
        W[i] += waitingTime;

        /* calculate the time spent blocked */
        double blockedTime = D[j][i] - D[j-1][i] - serviceTime;
        B[i] += blockedTime;
      }
    }
    return new TandemQueueResult(arrivals, W, B, D);
  }


//   public static void main(String[] args)
//   {
//     
//   }
}




