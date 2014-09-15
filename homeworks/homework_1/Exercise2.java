
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStreamBase;

public class Exercise2
{
  /* (a) Implement the SWB generator of Example 1.16, whose parameters
  are (b, r, k) = (2^31, 8, 48), and real-valued output defined by u n = x2n /2^62 +
  x2n+1 /2^31 , and use it to generate three-dimensional points in [0, 1) 3 , defined by
  ui = (u[25i], u[25i+20], u[25i+24]) for i = 0, ..., m − 1, for m = 10^4.

  Partition the unit cube into k = 10^6 subcubes by partitioning each axis into 100 equal intervals.
  Number these subcubes from 0 to k − 1 (in any way), find the number of the
  subcube in which each point ui has fallen, and count the number C of collisions
  as in Example 1.6. Repeat this 10 times, to obtain 10 “independent” realizations
  of C, and compare their distribution with the Poisson approximation given in
  Example 1.6. You can do the latter comparison informally; there is no need to
  perform a formal statistical test. */

  public Exercise2()
  {
    System.out.println("\n===========================================");
    System.out.println("Exercise 2 a)");
    a();
    System.out.println("===========================================");

    System.out.println("Exercise 2 b)");
    b();
    System.out.println("===========================================\n");
  }

  public static double expectedCollisions(int m, int k)
  {
    // expected number of collisions given a Poisson distribution of
    // average m elements to place into k addresses
    return (double) m*m/(2*k);
  }


  public double[] getNextPoint(RandomStreamBase generator)
  {
    double[] generated = new double[25];
    double[] ret = new double[3];

    for(int i = 0; i < 25; i++)
    {
      generated[i] = generator.nextDouble();
    }
    ret[0] = generated[0]; // u[25i]
    ret[1] = generated[20]; // u[25i + 20]
    ret[2] = generated[24]; // u[25i + 24]
    return ret;
  }

  public int[] a(int number_of_experiments)
  {
    assert number_of_experiments > 0;
    MathematicaSWB generator = new MathematicaSWB();
    int[] collisions = new int[10];

    for(int iteration=0; iteration < 10; iteration++)
    {
      UnitCube cube = new UnitCube();
      for(int i=0; i < 10000; i++)  // add 10^4 random points to the cube
      {
        double[] point = getNextPoint(generator);
        cube.addObservation(point[0], point[1], point[2]);
      }
      collisions[iteration] = cube.getNumCollisions();
      System.out.println(cube.getPointDistribution());
    }

    // print the collisions
    for(int i = 0; i < 10; i++)
    {
    System.out.println(collisions[i]);
    }
    return collisions;
  }

  /* (b) Redo the same experiment, but this time using a better generator, such
  as MRG32k3a in SSJ, for example. Discuss your results. */
  public int[] b()
  {
    MRG32k3a generator = new MRG32k3a();
    int[] collisions = new int[10];

    for(int iteration=0; iteration < 10; iteration++)
    {
      UnitCube cube = new UnitCube();
      for(int i=0; i < 10000; i++)  // add 10^4 random points to the cube
      {
        double[] point = getNextPoint(generator);
        cube.addObservation(point[0], point[1], point[2]);
      }
      collisions[iteration] = cube.getNumCollisions();
      System.out.println(cube.getPointDistribution());
    }

    for(int i = 0; i < 10; i++)
    {
    System.out.println(collisions[i]);
    }
    return collisions;
  }


}