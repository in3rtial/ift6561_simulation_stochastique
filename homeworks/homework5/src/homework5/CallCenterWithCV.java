import umontreal.iro.lecuyer.simevents.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.stat.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.LinkedList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

public class CallCenterWithCV {

   static final double HOUR = 3600.0;  // Time is in seconds. 

   // Data
   // Arrival rates are per hour, service and patience times are in seconds.
   double openingTime;    // Opening time of the center (in hours).
   int numPeriods;        // Number of working periods (hours) in the day.
   int[] numAgents;       // Number of agents for each period.   
   double[] lambda;       // Base arrival rate lambda_j for each j.
   double alpha0;         // Parameter of gamma distribution for B.
   double p;              // Probability that patience time is 0.
   double nu;             // Parameter of exponential for patience time.
   double alpha, beta;    // Parameters of gamma service time distribution. 
   double s;              // Want stats on waiting times smaller than s.

   // Variables
   double busyness;       // Current value of B.
   double arrRate = 0.0;  // Current arrival rate.
   int nAgents;           // Number of agents in current period.
   int nBusy;             // Number of agents occupied;
   int nArrivals;         // Number of arrivals today;
   int nAbandon;          // Number of abandonments during the day.
   int nGoodQoS;          // Number of waiting times less than s today.
   double nCallsExpected; // Expected number of calls per day.
   double sumServiceTimes;

   Event nextArrival = new Arrival();           // The next Arrival event.
   LinkedList<Call> waitList = new LinkedList<Call>();

   RandomStream streamB        = new MRG32k3a(); // For B.
   RandomStream streamArr      = new MRG32k3a(); // For arrivals.
   RandomStream streamPatience = new MRG32k3a(); // For patience times.
   GammaGen genServ;      // For service times; created in readData().

   TallyStore[] allTal = new TallyStore [4];
   TallyStore statArrivals = allTal[0] = new TallyStore ("Number of arrivals per day");
   TallyStore statWaits = allTal[1] = new TallyStore ("Average waiting time per customer");
   TallyStore statGoodQoS = allTal[2] = new TallyStore ("Proportion of waiting times < s");
   TallyStore statServiceTimes = new TallyStore ("Service times");
   TallyStore statAbandon = allTal[3] = new TallyStore ("Proportion of calls lost");
   TallyStore statWaitsDay = new TallyStore ("Waiting times within a day");

   public CallCenterWithCV (String fileName) throws IOException {
      readData (fileName);
      // genServ can be created only after its parameters are read.
      // The acceptance/rejection method is much faster than inversion.
      genServ = new GammaAcceptanceRejectionGen (new MRG32k3a(),
                    new GammaDist (alpha, beta));
   }

   // Reads data and construct arrays.
   public void readData (String fileName) throws IOException {
      BufferedReader input = new BufferedReader (new FileReader (fileName));
      StringTokenizer line = new StringTokenizer (input.readLine());
      openingTime = Double.parseDouble (line.nextToken());
      line = new StringTokenizer (input.readLine());
      numPeriods  = Integer.parseInt (line.nextToken());

      numAgents = new int[numPeriods];
      lambda = new double[numPeriods];
      nCallsExpected = 0.0;
      for (int j=0; j < numPeriods; j++) {
         line = new StringTokenizer (input.readLine());
         numAgents[j] = Integer.parseInt (line.nextToken());
         lambda[j]    = Double.parseDouble (line.nextToken());
         nCallsExpected += lambda[j];
      }
      line = new StringTokenizer (input.readLine());
      alpha0 = Double.parseDouble (line.nextToken());
      line = new StringTokenizer (input.readLine());
      p = Double.parseDouble (line.nextToken());
      line = new StringTokenizer (input.readLine());
      nu = Double.parseDouble (line.nextToken());
      line = new StringTokenizer (input.readLine());
      alpha = Double.parseDouble (line.nextToken());
      line = new StringTokenizer (input.readLine());
      beta = Double.parseDouble (line.nextToken());
      line = new StringTokenizer (input.readLine());
      s = Double.parseDouble (line.nextToken());
      input.close();
   }

   // A phone call.
   class Call { 

      double arrivalTime, serviceTime, patienceTime;

      public Call() {
         serviceTime = genServ.nextDouble(); // Generate service time.
         sumServiceTimes += serviceTime;
         if (nBusy < nAgents) {           // Start service immediately.
            nBusy++;
            nGoodQoS++;
            statWaitsDay.add (0.0);
            new CallCompletion().schedule (serviceTime);
         } else {                         // Join the queue.
            patienceTime = generPatience();
            arrivalTime = Sim.time();
            waitList.addLast (this);
         }
      }
      public void endWait() {
         double wait = Sim.time() - arrivalTime;
         if (patienceTime < wait) { // Caller has abandoned.
            nAbandon++;
            wait = patienceTime;    // Effective waiting time.
         } else {
            nBusy++;
            new CallCompletion().schedule (serviceTime);
         }
         if (wait < s) nGoodQoS++;
         statWaitsDay.add (wait);
      }
   } 

   // Event: A new period begins.
   class NextPeriod extends Event {
      int j;     // Number of the new period.
      public NextPeriod (int period) { j = period; }
      public void actions() {
         if (j < numPeriods) {
            nAgents = numAgents[j];
            arrRate = busyness * lambda[j] / HOUR;
            if (j == 0) 
               nextArrival.schedule 
                  (ExponentialDist.inverseF (arrRate, streamArr.nextDouble()));
            else {
               checkQueue();
               nextArrival.reschedule ((nextArrival.time() - Sim.time()) 
                                       * lambda[j-1] / lambda[j]);
            }
            new NextPeriod(j+1).schedule (1.0 * HOUR);
         }
         else
            nextArrival.cancel();  // End of the day.
      }
   }

   // Event: A call arrives.
   class Arrival extends Event {
      public void actions() {
         nextArrival.schedule 
            (ExponentialDist.inverseF (arrRate, streamArr.nextDouble()));
         nArrivals++;
         new Call();               // Call just arrived.
      }
   }

   // Event: A call is completed.
   class CallCompletion extends Event {
      public void actions() { nBusy--;   checkQueue(); }
   }

   // Start answering new calls if agents are free and queue not empty.
   public void checkQueue() {
      while ((waitList.size() > 0) && (nBusy < nAgents))
         (waitList.removeFirst()).endWait();
   }

   // Generates the patience time for a call.
   public double generPatience() {
      double u = streamPatience.nextDouble();
      if (u <= p) 
         return 0.0;
      else 
         return ExponentialDist.inverseF (nu, (1.0-u) / (1.0-p));
   }

   public void simulateOneDay (double busyness) { 
      Sim.init();        statWaitsDay.init();
      nArrivals = 0;     nAbandon = 0;     
      nGoodQoS = 0;      nBusy = 0;
      sumServiceTimes = 0;
      this.busyness = busyness;

      new NextPeriod(0).schedule (openingTime * HOUR);
      Sim.start();
      // Here the simulation is running...

      statArrivals.add ((double)nArrivals);
      statAbandon.add ((double)nAbandon / nCallsExpected);
      statGoodQoS.add ((double)nGoodQoS / nCallsExpected);
      statWaits.add (statWaitsDay.sum() / nCallsExpected);
      statServiceTimes.add (sumServiceTimes / nArrivals);
   }

   public void simulateOneDay () {
      simulateOneDay (GammaDist.inverseF (alpha0, alpha0, 8, 
                                          streamB.nextDouble()));
   }

   static Algebra alg = new Algebra();

   /**
    * Applies a vector of control variables to an estimator.
    * The tally x contains the observations used to estimate the output
    * average while the array of tallies c contains the
    * observations of the control variates.
    * The third array, ec, contains the known expectations
    * of the control variates.
    * The length c and ec should be equal while x, and c should
    * contain the same number of observations.
    *
    * This method estimates the optimal vector of constants for
    * the control variates, and prints information on the vector,
    * and the variance with CV.     
    * @param x the tally containing the observations for the output value.
    * @param c the tally containing the observations of the control variates.
    * @param ec the vector of expectations.
    */
   public static void applyCV (TallyStore x, TallyStore[] c, double[] ec) {
      // Construct and fill the matrix Cov[C]
      DoubleMatrix2D matC = new DenseDoubleMatrix2D (c.length, c.length);
      for (int i = 0; i < c.length; i++)
         matC.setQuick (i, i, c[i].variance ());
      for (int i = 0; i < c.length - 1; i++)
         for (int j = i + 1; j < c.length; j++) {
            double cov = c[i].covariance (c[j]);
            matC.setQuick (i, j, cov);
            matC.setQuick (j, i, cov);
         }
      // Construct and fill the vector Cov[C, X]
      DoubleMatrix2D matCX = new DenseDoubleMatrix2D (c.length, 1);
      for (int i = 0; i < c.length; i++)
         matCX.setQuick (i, 0, x.covariance (c[i]));

      System.out.print (x.getName ());
      System.out.println (" with CV:");
      for (TallyStore tally : c) {
         System.out.print ("   ");
         System.out.println (tally.getName ());
      }
      DoubleMatrix2D mbeta;
      try {
         // Find Beta vector solving Cov[C, X] = Cov[C]*Beta
         mbeta = alg.solve (matC, matCX);
      }
      catch (IllegalArgumentException iae) {
         // This can happen, e.g., if the variance of a CV is (incorrectly) 0.
         System.out.println ("Cannot apply CV");
         System.out.println ();
         return;
      }
      
      // Compute average Xc = X - Beta^t*(C - E[C])
      double avgWithCV = x.average ();
      for (int i = 0; i < c.length; i++)
         avgWithCV -= mbeta.getQuick (i, 0) * (c[i].average () - ec[i]);
      // Compute variance Var[Xc] = Var[X] + Beta^t*Var[C]*Beta - 2Beta*Cov[C, X]
      double varWithCV = x.variance ();
      // viewDice transposes the matrix mbeta (which contains a single column),
      // and zMult performs the matrix multiplication.
      // The null second argument instructs Colt to create a new matrix for the
      // result.
      // The result of the operation is a 1x1 matrix from which we extract the single element;
      // this is the second term of the controlled variance.
      varWithCV += mbeta.viewDice ().zMult (matC, null).zMult (mbeta, null).getQuick (0, 0);
      // A similar technique is used to compute the third term of the controlled variance.
      varWithCV -= 2*mbeta.viewDice ().zMult (matCX, null).getQuick (0, 0);

      // Print some information
      System.out.print ("Beta vector with CV                        : (");
      for (int i = 0; i < c.length; i++)
         System.out.printf ("%s%.3g", i > 0 ? ", " : "", mbeta.getQuick (i, 0));
      System.out.println (")");
      System.out.printf ("Average without CV                         : %8.5g%n", x.average ());
      System.out.printf ("Average with CV                            : %8.5g%n", avgWithCV);
      System.out.printf ("Variance without CV                        : %8.5g%n", x.variance ());
      System.out.printf ("Variance with CV                           : %8.5g%n", varWithCV);
      System.out.printf ("Variance reduction factor                  : %8.5g%n", x.variance () / varWithCV);
      System.out.println();
   }

   static public void main (String[] args) throws IOException { 
      java.util.Locale.setDefault (java.util.Locale.ROOT);  // If you computer is configurated in French.
      CallCenterWithCV cc = new CallCenterWithCV ("CallCenter.dat"); 
      for (int i = 0; i < 1000; i++)  cc.simulateOneDay();
      System.out.println ("\nNum. calls expected = " + cc.nCallsExpected +"\n");
      for (Tally tally : cc.allTal) {
         tally.setConfidenceIntervalStudent();
         tally.setConfidenceLevel (0.90);
      }
      System.out.println (Tally.report ("CallCenter:", cc.allTal));

      applyCV (cc.statGoodQoS, new TallyStore[] { cc.statArrivals, cc.statServiceTimes },
               new double[] { cc.nCallsExpected, cc.genServ.getDistribution().getMean() });
      // Code for a single CV
      //applyCV (cc.statGoodQoS, new TallyStore[] { cc.statArrivals }, new double[] { cc.nCallsExpected });
   }
}
