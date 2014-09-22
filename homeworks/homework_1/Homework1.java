/**
 * Homework 1
 * IFT-6561 UdeM
 * @author Gabriel C-Parent
 */
public class Homework1 {

	/**
	 * runs the exercises of the homework
	 */
	public static void main(String[] args) {

		//---------------------------------------------------------------------
		Exercise2 e2 = new Exercise2();
		System.out.print("[");
		for(int i = 0; i < e2.collisionsA.length; i++)
		{
			System.out.print(e2.collisionsA[i]);
			if (i != e2.collisionsA.length - 1)
			{
				System.out.print(", ");
			}
		}
		System.out.println("]");
		
		System.out.print("[");
		for(int i = 0; i < e2.collisionsB.length; i++)
		{
			System.out.print(e2.collisionsB[i]);
			if (i != e2.collisionsB.length - 1)
			{
				System.out.print(", ");
			}
		}
		System.out.println("]");

		//---------------------------------------------------------------------
		Exercise4 e4 = new Exercise4(false, 1000);
		
		// display the confidence intervals
		for(int i = 0; i < e4.waitingAverages.length; i++)
		{
			System.out.println(e4.waitingAverages[i].report());
			if(i != e4.waitingAverages.length -1)
			{

				System.out.println(e4.blockedAverages[i].report());
			}
		}
	}
}