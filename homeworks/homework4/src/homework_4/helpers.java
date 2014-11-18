package homework_4;

public class helpers {
	
	public static boolean isSorted(double[] arr)
	{
		for(int i = 0; i < arr.length - 1; i++)
		{
			if (arr[i+1] < arr[i])
			{
				return false;
			}
		}
		return true;
	}
	
	
	public static void printArray(double[] arr)
	{
		System.out.print("[");
		for(int i = 0; i < arr.length-1; i++)
		{
			System.out.print(arr[i] + ", ");
		}
		System.out.print(arr[arr.length-1]);
		System.out.println("]");
	}

}
