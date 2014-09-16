/* this class holds the results of the tandem queue simulation */

class TandemQueueResult
{
  public final double[] arrivals;
  public final double[][] departures;
  public final double[] waiting;
  public final double[] blocked;

  public TandemQueueResult(double[] A,
                           double[] W,
                           double[] B,
                           double[][] D)
  {
    arrivals = A;
    departures = D;
    waiting = W;
    blocked = B;
  }

  public void printResults()
  {
    // prints the times to the console
    
    // arrivals
    System.out.println("ARRIVALS");
    for(int i = 0; i < arrivals.length; i++)
    {
      if((i % 20) == 0)
      {
        System.out.println();
      }
      System.out.print(arrivals[i] + " ");
    }

    // departures
    System.out.println("DEPARTURES");
//     for(int i = 0; i < departures[0].length; i++)
//     {
//       
//     }

  }
}