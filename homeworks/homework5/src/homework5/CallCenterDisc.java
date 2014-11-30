package homework5;

// A subclass of CallCenter where the busyness B has a discrete
// distribution over small number of values.

//They made me write this against my will.

import umontreal.iro.lecuyer.randvar.GammaAcceptanceRejectionGen;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.TallyStore;

import java.io.*;

public class CallCenterDisc extends CallCenterMod {

	double[] b, q, Q; // Values of B, their probab., and cdf.
	double meanB; // Mean of B.
	double varianceB; // Variance of B.

	public CallCenterDisc(String fileName, double[] b, double[] q)
			throws IOException {
		super(fileName);
		this.b = b;
		this.q = Q;
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
	
	public void jumpRNG()
	{
        this.streamB.resetNextSubstream();
        this.streamArr.resetNextSubstream();
        this.streamPatience.resetNextSubstream();
        (this.genServ.getStream()).resetNextSubstream();
	}

	public static TallyStore diff(double delta, int n) throws IOException
	{
		double[] b = new double[] { 0.8, 1.0, 1.2, 1.4 };
		double[] q = new double[] { 0.25, 0.55, 0.15, 0.05 };
		CallCenterDisc cc1 = new CallCenterDisc("CallCenter.dat", b, q);
		CallCenterDisc cc2 = new CallCenterDisc("CallCenter.dat", b, q);
		cc2.beta = (1.0 / (100.0 - delta));
		cc2.genServ = new GammaAcceptanceRejectionGen (cc2.streamB.clone(), cc2.alpha, cc2.beta);
		TallyStore statDiffCRN = new TallyStore();

		double value1, value2;
		
		for (int i = 0; i < n; i++)
		{
			cc1.jumpRNG();
			//System.out.println(cc1.streamArr.nextDouble());
			cc1.simulateOneDay(false, false);
			
			value1 = cc1.nGoodQoS;
			
			cc2.jumpRNG();
			//System.out.println(cc2.streamArr.nextDouble());
			cc2.simulateOneDay(false, false);

			value2 = cc2.nGoodQoS;
			//System.out.println("value 1 = " +value1 + " value 2 = " + value2);
			
			statDiffCRN.add(value2 - value1);
		}
		return statDiffCRN;
	}
	
	
	public static void main(String[] args) throws IOException {
		
		System.out.println(diff(10., 10000).report());
	}
}
