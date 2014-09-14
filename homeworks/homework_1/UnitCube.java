/* unit cube class used in exercise 1.2 */
import java.util.TreeMap;

public class UnitCube
{
  /* a 0,1 3d cube with 10^6 subcubes */
  private int[] array;
  private int arraySize;

  public UnitCube()
  {
    arraySize = 1000000;
    array = new int[arraySize];
  }

  public void addObservation(double a, double b, double c) throws RuntimeException
  {
    // add 1 to the subcube (and check for overflow)
    int x = (int)(a / 0.01);
    int y = (int)(b / 0.01);
    int z = (int)(c / 0.01);
    int arrayPosition = z + (100*y) + (100*100*x);

    if(array[arrayPosition] == Integer.MAX_VALUE)
    {
      throw new RuntimeException("Overflow in subcube counter");
    }
    else
    {
    array[arrayPosition] += 1;
    }
  }

  public TreeMap<Integer, Integer> getPointDistribution()
  {
    // returns the number of collisions in the subcubes
    TreeMap<Integer, Integer> collisions = new TreeMap<Integer, Integer>();
    for(int i= 0; i < arraySize; i++)
    {
      int value = array[i];
      if(!collisions.containsKey(value))
      {
        collisions.put(value, 1);
      }
      else
      {
        collisions.put(value, collisions.get(value) + 1);
      }
    }
    return collisions;
  }

  public int getNumCollisions()
  {
    // returns the number of empty subcubes
    int collisions = 0;
    for(int i = 0; i < arraySize; i++)
    {
      if(array[i] > 1)
      {
        collisions += (array[i] - 1);
      }
    }
    return collisions;
  }
}