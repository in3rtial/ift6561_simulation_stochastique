package ExamFinal;

import umontreal.iro.lecuyer.rng.MRG32k3a;

public class MRG32k3a_C extends MRG32k3a {

	int count;
	public MRG32k3a_C() {
		// TODO Auto-generated constructor stub
		count = 0;
	}

	public MRG32k3a_C(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		count=0;
	}
	
	public double nextDouble()
	{
		count += 1;
		return super.nextDouble();
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public void resetCount()
	{
		this.count = 0;
	}

}
