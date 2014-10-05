package homework_2;


import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.util.*;


public class AsianDelta {
   double strikePrice;
   int T;            // Number of observation times.
   double discount;  // Discount factor exp(-r * zeta[t]).
   double[] muDelta; // Differences * (r - sigma^2/2).
   double[] sigmaSqrtDelta; // Square roots of differences * sigma.
   double[] logS;    // Log of the GBM process: logS[t] = log (S[t]).

   // Array zeta[0..s] must contain zeta[0]=0.0, plus the s observation times.

   /**
    * constructor for AsianDelta option
    * @param shortRate compound rate of bank account
    * @param volatility of the Standard Brownian Motion
    * @param strikePrice K, price agreed upon to buy the asset at time T
    * @param initialPrice initial price of the asset
    * @param T number of observation times
    * @param observationTimes
    */
   public AsianDelta (double shortRate,
                 double volatility,
                 double strikePrice,
                 double initialPrice,
                 int T,
                 double[] observationTimes)
   {
      this.strikePrice = strikePrice;
      this.T = T;
      discount = Math.exp (-shortRate * observationTimes[T]);
      double mu = shortRate - 0.5 * volatility * volatility;
      muDelta = new double[T];
      sigmaSqrtDelta = new double[T];
      logS = new double[T+1];
      double delta;

      for (int j = 0; j < T; j++) {
         delta = observationTimes[j+1] - observationTimes[j];
         muDelta[j] = mu * delta;
         sigmaSqrtDelta[j] = volatility * Math.sqrt (delta);
      }

      logS[0] = Math.log (initialPrice);
   }

   // Generates the process S.
   public void generatePath (RandomStream stream) {
       for (int j = 0; j < T; j++)
          logS[j+1] = logS[j] + muDelta[j] + sigmaSqrtDelta[j]
                   * NormalDist.inverseF01 (stream.nextDouble());
   }

   // Computes and returns the discounted option payoff.
   public double getPayoff () {
       double average = 0.0;  // Average of the GBM process.
       for (int j = 1; j <= T; j++) average += Math.exp (logS[j]);
       average /= T;
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
      
      // choose the observation times
      double[] observationTimes = new double[s+1];
      observationTimes[0] = 0.0;
      for (int j=1; j<=s; j++)
         observationTimes[j] = (double)j / (double)s;
      AsianDelta process = new AsianDelta (0.05, 0.5, 100.0, 100.0, s, observationTimes);
      Tally statValue = new Tally ("Stats on value of Asian option");

      int n = 100000;
      process.simulateRuns (n, new MRG32k3a(), statValue);
      statValue.setConfidenceIntervalStudent();
      System.out.println (statValue.report (0.95, 3));
   }
}

