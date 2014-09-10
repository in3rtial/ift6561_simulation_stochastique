
import java.math.BigInteger;
// import java.math.;
// import java.math.PI;
// import java.math.sqrt;

// import 


public class homework_1 {
/* 
  Gabriel C-Parent
  IFT-6561 UdeM
  Homework 1
*/


public static BigInteger factorial_exact(BigInteger n)
{
  // iteratively calculate the factorial of n
  assert n.compareTo(BigInteger.ZERO) > 0;
  BigInteger ret = new BigInteger("1");

  while(!n.equals(BigInteger.ZERO))
  {
    ret = ret.multiply(n);
    n = n.subtract(BigInteger.ONE);
  }
  return ret;
}

/*
public static BigInteger factorial_stirling(BigInteger n)
{
  // calculate factorial using Stirling's approximation
  BigInteger ret = sqrt
  
  

}*/




public static void main(String[] args)
{
  System.out.println(factorial_exact(new BigInteger("4")).toString());


}










}