/*
 * Implements variance reduction techniques for the example
 * CallCenterDisc.java.   
 * Written by Mario Pouchet et Pierre L'Ecuyer.
 */
import umontreal.iro.lecuyer.simevents.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.stat.*;
import umontreal.iro.lecuyer.util.PrintfFormat;
import java.io.*;
import java.util.LinkedList;
import cern.colt.list.DoubleArrayList;

public class CallCenterEi extends CallCenterDisc {

   // Constructor.
   public CallCenterEi (String fileName, String fileName2) throws IOException {
      super (fileName, fileName2);
   }
   
   // To simplify the writing in the code.
   private void println (String s) {
      System.out.println (s);
   }
  
   private void println () {
      System.out.println ();
   }
  
   private String f (double x) {
      return PrintfFormat.f (9, 3, x);
   }
   
   // Compute and print confidence interval with control variate.
   private void computeCICV (DoubleArrayList A, DoubleArrayList X, double beta, double varMC) {
      Tally statCV = new Tally ();
      for (int i = 0; i < A.size(); i++) {
         statCV.add (X.get(i) - beta * (A.get(i) - nCallsExpected));
      }
      println (statCV.reportAndCIStudent (0.9));
      println ("meanX = " + f (statCV.average()) +
         ",   Var(X) = " + f(statCV.variance()) +
         ",   ratio = " + f(statCV.variance() / varMC));
      println ();
   }

   // *************************************************************
   // Simulation experiments without stratification.
   // Uses n simulation runs and n0 pilot runs.   Returns varMC.
   private double simul (int n, int n0) {
      println ("**** Simulations without stratification ****");

      for (int t=0; t<k; t++) {
         println (" t = " + t + ",  b[t] = " + f(b[t]) + ",  q[t] = " + f(q[t]));
      }
      double a = nCallsExpected;
      // Variance exacte du nombre d'appels recus par jour.
      // Var[A] = E[Var[A|B]] + Var[E[A|B]] = E[aB] + Var[aB].
      double exactVarA = a * meanB + a * a * varianceB;
      println (" meanB = " + f(meanB) + ",  varianceB = " + 
               PrintfFormat.f(9, 7, varianceB));
      println (" a = " + f(a) + ",  Exact Var[a] = " + f(exactVarA));

      // Pilot runs to estimate the variances and covariances for beta.
      statArrivals.init();     statWaits.init();
      statGoodService.init();  statAbandon.init(); 
      for (int i=0; i < n0; i++)  simulateOneDay();

      double meanAp = statArrivals.average();
      double varAp  = statArrivals.variance();
      double meanXp = statGoodService.average();
      double varXp  = statGoodService.variance();
      double covarAXp = statArrivals.covariance (statGoodService);
      double meanDp = meanAp - meanXp;
      double varDp  = varAp + varXp - 2.0 * covarAXp;
      double covarADp = varAp - covarAXp;
      println ("\n\n**** Results of the pilot runs ****");
      println ("meanAp = " + f(meanAp) + ",  Var(A) = " + f(varAp));
      println ("Covar(A,X) = " + f(covarAXp));
      println ("Covar(A,D) = " + f(covarADp));
      println ();


      // Perform crude simulations to estimate means and variances.
      simulateDays (n);   println ();

      // Empirical means and variances.
      double meanA = statArrivals.average();
      double varA  = statArrivals.variance();
      double meanX = statGoodService.average();
      double varX  = statGoodService.variance();
      double varMC = varX;   // Variance with crude MC.
      double covarAX = statArrivals.covariance (statGoodService);
      double meanD = meanA - meanX;
      double varD  = varA + varX - 2.0 * covarAX;
      // Note: cov(A,A-X) = var(A) - cov(A,X).
      double covarAD = varA - covarAX;
      double beta;           // Coefficient for VC.

      DoubleArrayList A = statArrivals.getArray();
      DoubleArrayList X = statGoodService.getArray();
      TallyStore statBadService = new TallyStore (n);
      for (int i = 0; i < n; i++) {
         statBadService.add (A.get(i) - X.get(i));
      }
      DoubleArrayList D = statBadService.getArray();

      println ("\n**** Crude estimator ****");
      println ("meanA = " + f(meanA) + ",   Var(A) = " + f(varA));
      println ("meanX = " + f(meanX) + ",   Var(X) = " + f(varX));
      println ("meanD = " + f(meanD) + ",   Var(D) = " + f(varD));
      println ("Covar(A,X) = " + f(covarAX));
      println ("Covar(A,D) = " + f(covarAD));
      println ();

      println ("\n**** Indirect estimator ****");
      println ("meanX = " + f(a - meanD) + ",   Var(D) = " + f(varD) +
               ",   ratio = " + f(varD / varMC));
      println ();

      A = statArrivals.getArray();
      X = statGoodService.getArray();
      D = statBadService.getArray();

      println ("\n**** Control Variate A with exact variance of A, no pilot runs ****");
      println ("Exact value of Var(A) = " + f(exactVarA));
      beta = covarAX / exactVarA;
      println ("beta = covar(A,X) / exactVarA = " + f(beta));
      computeCICV (A, X, beta, varMC);

      println ("\n**** Control Variate A with var(A) estimated, no pilot runs ****");
      beta = covarAX / varA;
      println ("beta = covar(A,X) / VarA = " + f(beta));
      computeCICV (A, X, beta, varMC);

      println ("\n**** Indirect + Control variate, exact variance of A, no pilot runs ****");
      beta = covarAD / exactVarA;
      println ("beta = covar(A,D) / exactVarA = " + f(beta));
      computeCICV (A, D, beta, varMC);

      println ("\n**** Indirect + Control variate, estimated var(A), no pilot runs ****");
      beta = covarAD / varA;
      println ("beta = covar(A,D) / VarA = " + f(beta));
      computeCICV (A, X, beta, varMC);

      println ("\n**** Control Variate A with exact variance of A, pilot runs ****");
      beta = covarAXp / exactVarA;
      println ("beta = covar(A,X) / exactVarA = " + f(beta));
      computeCICV (A, X, beta, varMC);

      println ("\n**** Control Variate A with estimated variance of A, pilot runs ****");
      beta = covarAXp / varAp;
      println ("beta = covar(A,X) / VarA = " + f(beta));
      computeCICV (A, X, beta, varMC);

      println ("\n**** Indirect + Control variate, exact variance of A, pilot runs ****");
      beta = covarADp / exactVarA;
      println ("beta = covar(A,D) / exactVarA = " + f(beta) + " from pilot runs");
      computeCICV (A, D, beta, varMC);

      println ("\n**** Indirect + Control variate, estimated var(A), pilot runs ****");
      beta = covarADp / varAp;
      println ("beta = covar(A,D) / varA = " + f(beta) + " from pilot runs");
      computeCICV (A, D, beta, varMC);

      return varMC;
   }




   // *************************************************************
   // Simulation experiments WITH stratification.
   // Uses n simulation runs and n0 pilot runs in each stratum.
   private void simulStrat (int n, int n0, double varMC) {
      println ("*********************************************************");
      println ("**** Simulations WITH stratification ****");

      // Pilot runs to estimate the variances and covariances for beta.
      double[] a = new double[k];
      double[] meanAp = new double[k];
      double[] varAp = new double[k];
      double[] meanXp = new double[k];
      double[] varXp = new double[k];
      double[] covarAXp = new double[k];
      double[] meanDp = new double[k];
      double[] varDp = new double[k];
      double[] covarADp = new double[k];
      double[] varCp = new double[k];
      double[] varCIp = new double[k];

      double[] betap = new double[k];   // beta fo direct est.
      double[] betaIp = new double[k];  // beta for indirect est.
      double[] sigma = new double[k];   // sigma for direct est.
      double[] sigmaI = new double[k];  // sigma for indirect.
      double[] sigmaC = new double[k];  // sigma for direct, with CV.
      double[] sigmaCI = new double[k]; // sigma for indirect, with CV.
      double[] sigmaBest = new double[k]; // smallest sigma, for given t.
      double sigmaT = 0.0;              // Weighted sum of sigma[t]'s.
      double sigmaIT = 0.0;
      double sigmaCT = 0.0;
      double sigmaCIT = 0.0;
      double sigmaBestT = 0.0;
      double varProportional = 0.0;      // Strat. with propor. sampling.
      double varProportionalC = 0.0;     // Strat with propor. sampling + CV.

      int[] nOpt = new int[k];           // Optimal number of runs per strata.
      int[] nOptC = new int[k];


      for (int t=0; t<k; t++) {
         // Pilot runs with B = b[t].
         statArrivals.init();     statWaits.init();
         statGoodService.init();  statAbandon.init(); 
         for (int i=0; i < n0; i++)  simulateOneDay (b[t]);  // <-- Pilot runs

         a[t] = nCallsExpected * b[t];   // Num expected calls, given B = b[t].
         meanAp[t] = statArrivals.average();
         varAp[t]  = statArrivals.variance();
         meanXp[t] = statGoodService.average();
         varXp[t]  = statGoodService.variance();
         covarAXp[t] = statArrivals.covariance (statGoodService);
         meanDp[t] = meanAp[t] - meanXp[t];
         varDp[t]  = varAp[t] + varXp[t] - 2.0 * covarAXp[t];
         covarADp[t] = varAp[t] - covarAXp[t];

         // Standard deviations sigma per stratum, needed for optimal n_t's.
         // Direct estimation.
         sigma[t] = Math.sqrt (varXp[t]);
         sigmaT += sigma[t] * q[t];
         sigmaBest[t] = sigma[t];
         varProportional += varXp[t] * q[t];

         // Indirect estimation.
         sigmaI[t] = Math.sqrt (varDp[t]);
         sigmaIT += sigmaI[t] * q[t];
         if (sigmaI[t] < sigmaBest[t]) sigmaBest[t] = sigmaI[t];

         // Direct estimation with control variate.
         betap[t] = covarAXp[t] / a[t];
         varCp[t] = varXp[t] + betap[t] * 
                    (betap[t] * varAp[t] - 2.0 * covarAXp[t]);
         sigmaC[t] = Math.sqrt (varCp[t]);
         sigmaCT += sigmaC[t] * q[t];
         if (sigmaC[t] < sigmaBest[t]) sigmaBest[t] = sigmaC[t];
         varProportionalC += varCp[t] * q[t];

         // Indirect estimation with control variate.
         betaIp[t] = -covarADp[t] / a[t];
         varCIp[t] = varDp[t] + betaIp[t] * 
                     (betaIp[t] * varAp[t] - 2.0 * covarADp[t]);
         sigmaCI[t] = Math.sqrt (varCIp[t]);
         sigmaCIT += sigmaCI[t] * q[t];
         if (sigmaCI[t] < sigmaBest[t]) sigmaBest[t] = sigmaCI[t];

         sigmaBestT += sigmaBest[t];

         println ("\n\n**  Pilot runs for t = " + t);
         println (" b[t] = E[B|t] = " + f(b[t]) + ",  prob[t] = " + f(q[t]));
         println (" meanAp[t] = " + f(meanAp[t]) + ",  Var(A[t]) = " + f(varAp[t]));
         println (" meanXp[t] = " + f(meanXp[t]) + ",  Var(X[t]) = " + f(varXp[t]));
         println (" meanDp[t] = " + f(meanDp[t]) + ",  Var(D[t]) = " + f(varDp[t]));
         println (" Covar(A[t],X[t]) = " + f(covarAXp[t]));
         println (" Covar(A[t],D[t]) = " + f(covarADp[t]));
         println ("  betap[t] = " + f(betap[t]) + ",   betaIp[t] = " + f(betaIp[t]));
         println ("  varCp[t] = " + f(varCp[t]) + ",   VarCIp[t] = " + f(varCIp[t]));
         println ("  sigma = " + f(sigma[t]) + ",  sigmaI = " + f(sigmaI[t]) +
                  ", sigmaC = " + f(sigmaC[t]) + ",  sigmaCI = " + f(sigmaCI[t]));
         println ("  sigmaBest = " + f(sigmaBest[t]));
         println ();
      }
      double varOptimal  = sigmaT * sigmaT;     // Strat. with optimal alloc.
      double varOptimalC = sigmaCT * sigmaCT;   // Strat. with optimal alloc., + CV.
      println ("\nVariance with propor. allocation        = " + f(varProportional) +
               ",  var. ratio = " + f(varProportional / varMC));
      println ("Variance with propor. allocation + CV   = " + f(varProportionalC) +
               ",  var. ratio = " + f(varProportionalC / varMC));
      println ("Variance with optimal allocation        = " + f(varOptimal) +
               ",  var. ratio = " + f(varOptimal / varMC));
      println ("Variance with optimal allocation + CV   = " + f(varOptimalC) +
               ",  var. ratio = " + f(varOptimalC / varMC) + "\n\n");

      // Estimate proportional and optimal allocation for stratification.
      int[] numProp = new int[k];
      int sump = 0;   int sumn = 0;   int sumnC = 0;
      for (int t=0; t<k-1; t++) {
         sump  += numProp[t] = (int)Math.round (n * q[t]);
         sumn  += nOpt[t]  = (int)Math.round (n * sigma[t] * q[t] / sigmaT);
         sumnC += nOptC[t] = (int)Math.round (n * sigmaC[t] * q[t] / sigmaCT);
      }
      numProp[k-1] = n - sump;   nOpt[k-1] = n - sumn;    nOptC[k-1] = n - sumnC;

      // Perform runs with optimal allocation + CV.
      double meanOptimal = 0.0;    
      double meanOptimalC = 0.0;  
      varOptimal = 0.0;    varOptimalC = 0.0;  
      for (int t=0; t<k; t++) {
         statArrivals.init();     statWaits.init();
         statGoodService.init();  statAbandon.init(); 
         for (int i=0; i < nOpt[t]; i++)  simulateOneDay (b[t]);  // <-- Prod. runs
         // Direct estimation, no CV.
         meanOptimal += statGoodService.average() * q[t];
         varOptimal += statGoodService.standardDeviation() * q[t];

         statArrivals.init();     statWaits.init();
         statGoodService.init();  statAbandon.init(); 
         for (int i=0; i < nOptC[t]; i++)  simulateOneDay (b[t]);

         varAp[t]  = statArrivals.variance();
         varXp[t]  = statGoodService.variance();
         covarAXp[t] = statArrivals.covariance (statGoodService);
         // betap[t] = covarAXp[t] / a[t];
         varCp[t] = varXp[t] + betap[t] * 
            (betap[t] * varAp[t] - 2.0 * covarAXp[t]);
         meanOptimalC += q[t] * (statGoodService.average()
            - betap[t] * (statArrivals.average() - a[t]));
         varOptimalC += Math.sqrt (varCp[t]) * q[t];

         println ("\n\n**   Final runs for t = " + t + 
                  ";  nOpt[t] = " + nOpt[t] + ";  nOptC[t] = " + nOptC[t]);
         println ("  betap[t] = " + f(betap[t]));
         println ("  varCp[t] = " + f(varCp[t]));
         println ();
      }
      varOptimal  *= varOptimal;
      varOptimalC *= varOptimalC;
      println ("\nMean with optimal allocation            = " + f(meanOptimal));
      println ("Variance with optimal allocation        = " + f(varOptimal) +
         ",  var. ratio = " + f(varOptimal / varMC));
      println ("Mean with optimal allocation + CV       = " + f(meanOptimalC));
      println ("Variance with optimal allocation + CV   = " + f(varOptimalC) +
         ",  var. ratio = " + f(varOptimalC / varMC));
 
      // Perform runs with proportional allocation.
      double meanProportional = 0.0;    
      double meanProportionalC = 0.0;  
      varProportional = 0.0;    varProportionalC = 0.0;  
      for (int t=0; t<k; t++) {
         statArrivals.init();     statWaits.init();
         statGoodService.init();  statAbandon.init(); 
         for (int i=0; i < numProp[t]; i++)  simulateOneDay (b[t]); // <-- Prod. runs

         varAp[t]  = statArrivals.variance();
         varXp[t]  = statGoodService.variance();
         covarAXp[t] = statArrivals.covariance (statGoodService);

         // Direct estimation.
         meanProportional += statGoodService.average() * q[t];
         varProportional += varXp[t] * q[t] * q[t] * n / (double)numProp[t];

         // Direct estimation with control variate.
         // betap[t] = covarAXp[t] / a[t];
         varCp[t] = varXp[t] + betap[t] * 
            (betap[t] * varAp[t] - 2.0 * covarAXp[t]);
         meanProportionalC += q[t] * (statGoodService.average() 
            - betap[t] * (statArrivals.average() - b[t] * nCallsExpected));
         varProportionalC += varCp[t] * q[t] * q[t] * n / (double)numProp[t];

         println ("\n\n**   Final runs for t = " + t + ";  n[t] = " + numProp[t]);
         println ("  betap[t] = " + f(betap[t]));
         println ("  varCp[t] = " + f(varCp[t]));
         println ();
      }
      println ("\nMean with propor. allocation            = " + f(meanProportional));
      println ("Variance with propor. allocation        = " + f(varProportional) +
         ",  var. ratio = " + f(varProportional / varMC));
      println ("Mean with propor. allocation + CV       = " + f(meanProportionalC));
      println ("Variance with propor. allocation + CV   = " + f(varProportionalC) +
         ",  var. ratio = " + f(varProportionalC / varMC) + "\n\n");
   }

 
   
   public static void main (String[] args) throws IOException {
      CallCenterEi cc = new CallCenterEi ("CallCenter.dat", "CallCenterDisc.dat");
      cc.println ("\nSimulation where the factor B has a discrete distribution");
      cc.println ("_________________________________________________________\n");
      double varMC = cc.simul (100000, 1000);
      cc.simulStrat (100000, 1000, varMC);
   }
}
