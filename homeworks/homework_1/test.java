

import java.math.BigInteger;
import java.util.Collections;
public class test{
  
  private int classify(double coord)
  {
    return (int)Math.round(coord / 0.01);
  }
  
  public static void main(String[] args)
  {
    int[] result = new int[10000];
    result[2] += 1;
    BigInteger n = new BigInteger("1");
    System.out.println(result[2]);
    System.out.println(Integer.MAX_VALUE);
    System.out.println(Collections.max([2,3,4]));
  }
}