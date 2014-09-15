/* implementation of the tandem queue for exercise 4 */

import umontreal.iro.lecuyer.stat.Tally;
import java.util.ArrayList;

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
  private int m; // the number of nodes in the system
  private double[] u; // average processing time at node i
  private int[] c; // maximum queue size at
  private double lambda; // arrival rate of the clients

  public TandemQueue(int numberOfNodes,
                     double arrivalRate,  // arrival rate, follows exp law
                     int[] queueCapacities,  // size of the queues (for blocking)
                     double[] serviceTimes)  // service time of the servers
  {
      m = numberOfNodes;
      u = serviceTimes;
      c = queueCapacities;
      lambda = arrivalRate;
  }


  public void simulateFixedNumber(RandomStream gen1, // simulates A[i], arrivals
                                  RandomStream gen2, // simulates S[i][j], service
                                  int max_clients)   // number of clients cutoff
  {
    // in this case, supposing that the number of arrivals and the number of
    // queues are not enormous, we can preallocate the random numbers in a 
    assert number >= 0;
    double[] arrivalTimes = double[max_clients];
    for(int i=0; i < max_clients; i++)
    {
      arrivalTimes[i] = (double)((Math.log(gen1.nextDouble())) / lambda);
    }

    // pass the arrival time array with the generator to the simulate method
    simulate(gen2, arrivals);
  }


  public void simulateFixedTime((RandomStream gen1, // simulates A[i], arrivals
                                 RandomStream gen2, // simulates S[i][j], service
                                 double timeCutoff)       // total time cutoff
  {
    assert timeCutoff >= 0;
    ArrayList<double> arrivalTimes = ArrayList<double>();
    double totalTime = 0;

    while(true)
    {
      double newArrival = (double)((Math.log(gen1.nextDouble())) / lambda);
      if((newArrival + totalTime) > timeCutoff)
      {
        break;
      }
      else
      {
        arrivalTimes.add(newArrival);
        totalTime += newArrival;
      }
    }

    // convert the arraylist into an array of double
    double[] arrivals = arrivalTimes.toArray();

    // pass the arrival time array with the generator to the simulate method
    simulate(gen2, arrivals);
  }

  private void simulate(RandomStream gen2, // simulates service times
                        double[] arrivals) // arrivals of the clients in the system
  {
      // this is the function called by both simulateFixedNumber and simulateFixedTime
      // who pass the generator for the service times and the precalculated arrival times
      
      
  }

}
