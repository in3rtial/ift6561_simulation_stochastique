package homework_4;
import java.util.Arrays;

public class PiecewiseConstantDist {
	
	double[] cdf;
	double[] X;

	/**
	 * Maps the U(0, 1) to the piecewise constant between 0,1
	 * @param X x coordinates
	 * @param Y y coordinates
	 * @param U Uniformly distributed numbers (0,1)
	 * @return
	 */
	public PiecewiseConstantDist(double[] X, double[] Y)
	{
		// X in the interval (0, 1)
		// Y is bounded >= 0
		// e.g.
		// x = {0.0, 0.281023, 0.89538, 1}
		// y = {0.68136, 16.0/9.0, 1.00649}
		
		//step 1: normalize the densities to a sum of 1
		double totalArea = 0.0;
		for(int i = 0; i < Y.length; i++)
		{
			totalArea += (X[i+1] - X[i]) * Y[i];
		}
		
		double[] normalizedAreas = new double[Y.length];
		for(int i = 0; i < Y.length; i++)
		{
			normalizedAreas[i] = (X[i+1] - X[i]) * Y[i] / totalArea;
		}
		
		//step 2: calculate the cumulative densities
		double[] cumulDensities = new double[Y.length];
		for(int i = 0; i < Y.length; i++)
		{
			if(i == 0) {
				cumulDensities[0] = normalizedAreas[0];
			}
			else
			{
				cumulDensities[i] = cumulDensities[i-1] + normalizedAreas[i];
			}
		}
		
		// assign to the variables
		
		this.cdf = cumulDensities;
		this.X = Arrays.copyOfRange(X, 1, X.length);
	}
	
	public double inverseF01(double U)
	{
		// first sequential search to find which 'bin' U is in
		int index = 0;
		while(index < (this.cdf.length-1) && this.cdf[index] < U)
		{
			index += 1;
		}
		//System.out.println("index " + index);
		// return linear interpolation
		double y0 = index == 0 ? 0.0 : this.X[index-1];
		double y1 = this.X[index];
		double x0 = index == 0 ? 0.0 : this.cdf[index -1];
		double x1 = this.cdf[index];
		
		//System.out.println("y0 " + y0 + " y1 " + y1 + " x0 " + x0 + " x1 " + x1);
		return( y0 + (y1 - y0)*((U - x0)/(x1 - x0)));
	}
	
	public boolean test()
	{
		for(int i = 0; i < this.X.length; i++)
			System.out.println(this.X[i]);
		
		for(int i = 0; i < this.X.length; i++)
			System.out.println(this.cdf[i]);
		
		boolean success = true;
		for(int i=0; i < this.cdf.length; i++)
		{
			if(this.inverseF01(this.cdf[i]) != this.X[i])
			{
				System.out.println("cdf " + this.cdf[i] + " expected " + this.X[i] + " got " + this.inverseF01(this.cdf[i]));
				success=false;
			}
		}
		return success;
	}
	
	
	public static void main(String[] args)
	{
		PiecewiseConstantDist dist1 = new PiecewiseConstantDist(new double[] {0.0, 0.25, 0.5, 0.75, 1}, new double[] {35, 25, 10, 30});
		System.out.println(dist1.test());
		System.out.println(dist1.inverseF01(0.5));
	}

}
