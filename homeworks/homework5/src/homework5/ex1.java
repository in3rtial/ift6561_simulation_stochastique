package homework5;

import java.io.IOException;

import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.stat.TallyStore;
import umontreal.iro.lecuyer.rng.*;

public class ex1 {
	// exercise 6.1
	
	// example section 6.2.2
	// compare
	
	// 
	// 2- CRN without synchronization
	
	// try with 3 values of delta
	
	public static void test(int num) throws IOException
	{
		
	}
	
	
	
	// we'll try with the same 3 values of delta
	public static void part1(int num) throws IOException
	{
		//CRNs with the (a + c) synchronization
		double[] deltas = new double[]{10., 1., 0.1};
		TallyStore[] results = new TallyStore[3];
		TallyStore result_10 = results[0] = CallCenterDisc.diff(deltas[0], num);
		TallyStore result_1 = results[1] =  CallCenterDisc.diff(deltas[1], num);
		TallyStore result_01 = results[2] = CallCenterDisc.diff(deltas[2], num);
		
		for(int i = 0; i < results.length; i++)
			System.out.println("AVG = "+results[i].average()+ "; VAR = "+results[i].variance());
		
	}

	
	public static void part3(int num) throws IOException {
	}

	public static void main(String[] args) throws IOException {
		
		System.out.println("CRNs with the (a + c) synchronization");
		part1(10000);
		
		System.out.println("");
		System.out.println("CRNs without synchronization");
		
		
		System.out.println("");
		System.out.println("IRNs");
		
		/*
		MRG32k3a a = new MRG32k3a();
		//a.setSeed(new long[]{12345, 12345, 12345, 12345, 12345, 12345});
		MRG32k3a b = a.clone();
		//b.setSeed(new long[]{12345, 12345, 12345, 12345, 12345, 12345});
		System.out.println(a.getState());
		System.out.println(a.nextDouble());
		System.out.println(b.getState());
		System.out.println(b.nextDouble());
	*/
	}
}
