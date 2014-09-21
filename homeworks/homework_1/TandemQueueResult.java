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

//  public void testResults()
//  {
//    //
//    for(int client = 0; client < arrivals.length - 1; client++)
//    {
//      //
//      int client2 = client + 1;
//      for(int server = 0; server < m; server++)
//      {
//        continue;
//      }
//    }
//  }

  public void printResults()
  {
    // prints the times to the console
    // arrivals
    System.out.println("ARRIVALS");
    for(int i = 0; i < arrivals.length; i++)
    {
      System.out.println(arrivals[i]);
    }
    System.out.println();

    // departures
    System.out.println("DEPARTURES");
    for(int client = 1; client < departures[0].length; client++)
    {
      System.out.println("client " + client);
      for(int station = 0; station < departures.length; station++)
      {
        System.out.println(departures[station][client]);
      }
    }
    System.out.println();

    // blocking times
    System.out.println("BLOCKING");
    for(int i = 1; i < blocked.length; i++)
    {
      System.out.println(blocked[i]);
    }
    System.out.println();

  }
}