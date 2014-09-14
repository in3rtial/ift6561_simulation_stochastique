/*
  Gabriel C-Parent
  IFT-6561 UdeM
  Homework 1
*/

import java.math.BigInteger;

public class homework_1
{
  public static BigInteger factorial(BigInteger n)
  {
    // iteratively calculate the factorial of n
    assert n.compareTo(BigInteger.ZERO) >= 0;
    BigInteger ret = new BigInteger("1");

    while(!n.equals(BigInteger.ZERO))
    {
      ret = ret.multiply(n);
      n = n.subtract(BigInteger.ONE);
    }
    return ret;
  }

  public static long factorial(int n)
  {
    assert n >= 0;
    long ret = 1;
    while(!(n == 0))
    {
      ret *= n;
      n -= 1;
    }
    return ret;
  }

  public static double poissonProbability(int lambdaM, int j)
  {
    /*
      k: number of physical memory locations reserved for storage (used)
      M: number of distinct indentifiers
      M has is a random variable having a Poisson distribution with mean lambda(m)
      P[M = j] = e^(-lambda(m)) * (lambda(m)^j) / (factorial(j))

      C: number of collisions
      D: number of distinct address to which identifiers are mapped
      C = M - D
      */

      return (double)(Math.exp(-lambdaM) * (lambdaM^j) / factorial(j));
  }






  public static void main(String[] args)
  {
    MathematicaSWB gen = new MathematicaSWB();

    UnitCube cube = new UnitCube();
    for(int i = 0; i < 100000; i++)
    {
        double a = gen.nextDouble();
        double b = gen.nextDouble();
        double c = gen.nextDouble();
        cube.addObservation(a, b, c);
    }
    System.out.println(cube.getPointDistribution());
    System.out.println(cube.getNumCollisions());
    System.out.println(gen.getState());
    System.out.println(poissonProbability(2, 4));
    new exercise2();
    

  }
}