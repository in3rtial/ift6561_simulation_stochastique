package homework5;

// A subclass of CallCenter where the busyness B has a discrete
// distribution over small number of values.

//They made me write this against my will.

import umontreal.iro.lecuyer.randvar.GammaAcceptanceRejectionGen;
import umontreal.iro.lecuyer.rng.*;

import java.io.*;

public class CallCenterDisc extends CallCenterMod {

	double[] b, q, Q; // Values of B, their probab., and cdf.
	double meanB; // Mean of B.
	double varianceB; // Variance of B.
	MRG32k3a streamServ; // the generator for genServ

	public CallCenterDisc(String fileName, double[] b, double[] q, double gammaGenBeta)
			throws IOException {
		super(fileName);
		
		this.gammaGenBeta = gammaGenBeta; // modify the beta, whatever it was
		this.b = b;
		this.q = q;
		this.Q = new double[b.length];
		double sum = 0.0;
		double mean2 = 0.0;
		this.meanB = 0.0;
		for (int j = 0; j < b.length; j++) {
			Q[j] = sum += q[j];
			meanB += b[j] * q[j];
			mean2 += b[j] * b[j] * q[j];
		}
		this.varianceB = mean2 - meanB * meanB;
		
		this.streamServ = new MRG32k3a();

		this.genServ = new GammaAcceptanceRejectionGen(streamServ, alpha, gammaGenBeta);
	}
	
	public MRG32k3a getRNG1()
	{
		return this.streamB;
	}
	public MRG32k3a getRNG2()
	{
		return this.streamArr;
	}
	
	public MRG32k3a getRNG3()
	{
		return this.streamPatience;
	}
	
	public MRG32k3a getRNG4()
	{
		return this.streamServ;
	}
	
	

	public double meanOfB() {
		return meanB;
	}

	public double varianceOfB() {
		return varianceB;
	}

	// Here, B is generated from cdf Q.
	public void simulateOneDay(boolean allServices, boolean allPatience) {
		int t;
		double u = streamB.nextDouble();
		// find the index by inversion (sum through the cdf)
		for (t = 0; (t < Q.length) && (Q[t] < u); t++) {
		}
		simulateOneDay(b[t], allServices, allPatience);
	}
	
	public void jumpRNG(int n)
	{
		for(int _ =0; _<n; _++)
		{
	        this.streamB.resetNextSubstream();
	        this.streamArr.resetNextSubstream();
	        this.streamPatience.resetNextSubstream();
	        this.streamServ.resetNextSubstream();
		}
	}
	
	public double[] outputRNG()
	{
		return new double[]{this.streamB.nextDouble(), this.streamArr.nextDouble(),
							this.streamPatience.nextDouble(), this.streamServ.nextDouble()};
	}

	public static boolean testRNGs() throws IOException
	{
		boolean result=true;
		double[] b = new double[] { 0.8, 1.0, 1.2, 1.4 };
		double[] q = new double[] { 0.25, 0.55, 0.15, 0.05 };
		CallCenterDisc cc1 = new CallCenterDisc("CallCenter.dat", b, q, (1.0/100.0));
		CallCenterDisc cc2 = new CallCenterDisc("CallCenter.dat", b, q, (1.0/100.0));
		
		boolean synchronize = true;
		long[] seedArr = new long[]{ 1, 12345, 12345, 12345, 12345, 12345 };
		long[] seedB = new long[]{ 2, 12345, 12345, 12345, 12345, 12345 };
		long[] seedPatience=  new long[]{ 3, 12345, 12345, 12345, 12345, 12345 };
		long[] seedServ = new long[]{ 4, 12345, 12345, 12345, 12345, 12345 };
		// fix the generators
		if (synchronize == true) {
			cc1.getRNG1().setSeed(seedB);
			cc2.getRNG1().setSeed(seedB);
			
			cc1.getRNG2().setSeed(seedArr);
			cc2.getRNG2().setSeed(seedArr);
			
			cc1.getRNG3().setSeed(seedServ);
			cc2.getRNG3().setSeed(seedServ);
			
			cc1.getRNG4().setSeed(seedPatience);
			cc2.getRNG4().setSeed(seedPatience);
		}
		
		double b1, b2, a1, a2, p1, p2, s1,s2;
		for(int i = 0 ; i < 100; i++)
		{
			/*
			a1=cc1.streamArr.nextDouble();
			a2=cc2.streamArr.nextDouble();
			
			b1=cc1.streamB.nextDouble();
			b2=cc2.streamB.nextDouble();
			
			p1=cc1.streamPatience.nextDouble();
			p2=cc2.streamPatience.nextDouble();
			
			s1=cc1.genServ.nextDouble();
			s2=cc2.genServ.nextDouble();
			
			// break immediately in case of problem
			if(s1!=s2 || a1 != a2 || p1!=p2 || b1!=b2)
			{
				System.out.println("problem with prng synchronization");
				return false;
			}
			//System.out.println(a1+" " + a2 +"; "+b1+" "+b2+"; "+p1+" "+p2+"; "+s1+" "+ s2);
			*/
			double[] c1 = cc1.outputRNG();
			double[] c2 = cc2.outputRNG();
			
			for(int j = 0; j < c1.length; j++)
			{
				if(c1[j] != c2[j])
					return false;
			}
			cc1.simulateOneDay(true, true);
			cc2.simulateOneDay(true, true);
			
			cc1.jumpRNG(1);
			cc2.jumpRNG(1);
		}
		
		System.out.println("prng are well synchronized");
		return result;
	}
	
	
	public static void main(String[] args) throws IOException {
		//testRNGs();
		
		double[] b = new double[] { 0.8, 1.0, 1.2, 1.4 };
		double[] q = new double[] { 0.25, 0.55, 0.15, 0.05 };
		double gammaGenBeta = 1.0/(100.0);
		CallCenterMod cc = new CallCenterDisc("CallCenter.dat", b, q, gammaGenBeta);
		for(int i =0 ; i< 100; i++)
			cc.simulateOneDay(true,true);
		

		System.out.println(cc.statArrivals.report());
		System.out.println(cc.statGoodQoS.report());
		
	}
}
