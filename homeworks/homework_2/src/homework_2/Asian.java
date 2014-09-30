package homework_2;

import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.util.*;


public class Asian {
   double strikePrice;    // Strike price, at which the buyer will by the stocks in T time
   int s;            // Number of observation times.
   double discount;  // Discount factor exp(-r * zeta[t]).
   double[] muDelta; // Differences * (r - sigma^2/2).
   double[] sigmaSqrtDelta; // Square roots of differences * sigma.
   double[] logS;    // Log of the GBM process: logS[t] = log (S[t]).

   // Array zeta[0..s] must contain zeta[0]=0.0, plus the s observation times.

   /**
    * constructor for Asian option
    * @param shortRate compound rate for bank account
    * @param volatility of the standard Brownian motion
    * @param strikePrice
    * @param s0
    * @param s
    * @param zeta
    */
   public Asian (double shortRate,
                 double volatility,
                 double strikePrice,
                 double s0,
                 int s,
                 double[] zeta)
   {
      this.strikePrice = strikePrice;
      this.s = s;
      discount = Math.exp (-shortRate * zeta[s]);
      double mu = shortRate - 0.5 * volatility * volatility;
      muDelta = new double[s];
      sigmaSqrtDelta = new double[s];
      logS = new double[s+1];
      double delta;

      for (int j = 0; j < s; j++) {
         delta = zeta[j+1] - zeta[j];
         muDelta[j] = mu * delta;
         sigmaSqrtDelta[j] = volatility * Math.sqrt (delta);
      }

      logS[0] = Math.log (s0);
   }

   // Generates the process S.
   public void generatePath (RandomStream stream) {
       for (int j = 0; j < s; j++)
          logS[j+1] = logS[j] + muDelta[j] + sigmaSqrtDelta[j]
                   * NormalDist.inverseF01 (stream.nextDouble());
   }

   // Computes and returns the discounted option payoff.
   public double getPayoff () {
       double average = 0.0;  // Average of the GBM process.
       for (int j = 1; j <= s; j++) average += Math.exp (logS[j]);
       average /= s;
       if (average > strikePrice) return discount * (average - strikePrice);
       else return 0.0;
   }

   // Performs n indep. runs using stream and collects statistics in statValue.
   public void simulateRuns (int n, RandomStream stream, Tally statValue) {
      statValue.init();
      for (int i=0; i<n; i++) {
         generatePath (stream);
         statValue.add (getPayoff ());
         stream.resetNextSubstream();
      }
   }

   public static void main (String[] args) {
      int s = 12;
      double[] zeta = new double[s+1];   zeta[0] = 0.0;
      for (int j=1; j<=s; j++)
         zeta[j] = (double)j / (double)s;
      Asian process = new Asian (0.05, 0.5, 100.0, 100.0, s, zeta);
      Tally statValue = new Tally ("Stats on value of Asian option");

      Chrono timer = new Chrono();
      int n = 100000;
      process.simulateRuns (n, new MRG32k3a(), statValue);
      statValue.setConfidenceIntervalStudent();
      System.out.println (statValue.report (0.95, 3));
      System.out.println ("Total CPU time:      " + timer.format() + "\n");
   }
}
