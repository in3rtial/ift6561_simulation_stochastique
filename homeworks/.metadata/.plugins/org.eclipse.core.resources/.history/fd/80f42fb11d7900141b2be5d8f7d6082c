// A subclass of CallCenter where the busyness B has a discrete
// distribution over small number of values.

import umontreal.iro.lecuyer.simevents.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.stat.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.LinkedList;

// We must recopy practically all the CallCenter class because we want
// to change the internal class Call.

public class CallCenterDisc2 extends CallCenterDisc {

   boolean allServices;
   boolean allPatience;
   Event nextArrival = new Arrival();           // The next Arrival event.

   public CallCenterDisc2 (String fileName, double[] b, double[] q) throws IOException {
      super (fileName, b, q);
   }
   
   // A phone call.
   // Redefined here to use the variables allServices and allPatience.
   class Call { 

      double arrivalTime, serviceTime, patienceTime;

      public Call() {
         if (allServices) serviceTime = genServ.nextDouble(); 
         if (allPatience) patienceTime = generPatience();
         if (nBusy < nAgents) {           // Start service immediately.
            nBusy++;
            nGoodService++;
            statWaitsDay.add (0.0);
            if (!allServices) serviceTime = genServ.nextDouble(); 
            new CallCompletion().schedule (serviceTime);
         } else {                         // Join the queue.
            if (!allPatience) patienceTime = generPatience();
            arrivalTime = Sim.time();
            waitList.addLast (this);
         }
      }
      public void endWait() {
         double wait = Sim.time() - arrivalTime;
         if (patienceTime < wait) { // Caller has abandoned.
            nAbandon++;
            wait = patienceTime;    // Effective waiting time.
         }
         else {
            nBusy++;
            if (!allServices) serviceTime = genServ.nextDouble(); 
            new CallCompletion().schedule (serviceTime);
         }
         if (wait < s) nGoodService++;
         statWaitsDay.add (wait);
      }
   } 

   // Event: A call arrives.
   // Must be redefined here so it uses the methods that are redefined here.
   class Arrival extends Event {
      public void actions() {
         nextArrival.schedule 
            (ExponentialDist.inverseF (arrRate, streamArr.nextDouble()));
         nArrivals++;
         Call2 call = new Call2();               // Call just arrived.
      }
   }

   // Event: A new period begins.
   // Must be redefined here so it uses the methods that are redefined here.
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

   // Event: A call is completed.
   // Must be redefined here because it is a class.
   class CallCompletion extends Event {
      public void actions() { nBusy--;   checkQueue(); }
   }

   // Start answering new calls if agents are free and queue not empty.
   // Must be redefined here because it uses class Call2.
   public void checkQueue() {
      while ((waitList.size() > 0) && (nBusy < nAgents))
         ((Call2)waitList.removeFirst()).endWait();
   }

   // Must be redefined here to use the correct NextPeriod class.
   public void simulateOneDay (double busyness) { 
      Sim.init();     statWaitsDay.init();
      nArrivals = 0;  nAbandon = 0;  nGoodService = 0;  nBusy = 0;
      this.busyness = busyness;

      new NextPeriod(0).schedule (openingTime * HOUR);
      Sim.start();
      // Here the simulation is running...

      statArrivals.add ((double)nArrivals);
      statAbandon.add ((double)nAbandon);
      statGoodService.add ((double)nGoodService);
      statWaits.add (statWaitsDay.sum() / nCallsExpected);
   }

   public void simulateOneDay (double busyness, 
               boolean allServices, boolean allPatience) {
      this.allServices = allServices;
      this.allPatience = allPatience;
      simulateOneDay (busyness);
   }

   public void simulateOneDay (boolean allServices, boolean allPatience) {
      this.allServices = allServices;
      this.allPatience = allPatience;
      super.simulateOneDay ();
   }
}
