/*
  Gabriel C-Parent
  IFT-6561 UdeM
  Homework 1
*/


public class homework_1
{

  public class exercise2
  {
    /* (a) Implement the SWB generator of Example 1.16, whose parameters
are (b, r, k) = (2 31 , 8, 48), and real-valued output defined by u n = x 2n /2 62 +
x 2n+1 /2 31 , and use it to generate three-dimensional points in [0, 1) 3 , defined by
u i = (u 25i , u 25i+20 , u 25i+24 ) for i = 0, . . . , m − 1, for m = 10 4 . 
Partition the unit
cube into k = 10 6 subcubes by partitioning each axis into 100 equal intervals.
Number these subcubes from 0 to k − 1 (in any way), find the number of the
subcube in which each point u i has fallen, and count the number C of collisions
as in Example 1.6. Repeat this 10 times, to obtain 10 “independent” realizations
of C, and compare their distribution with the Poisson approximation given in
Example 1.6. You can do the latter comparison informally; there is no need to
perform a formal statistical test.
(b) Redo the same experiment, but this time using a better generator, such
as MRG32k3a in SSJ, for example. Discuss your results. */
      
   /* public int poissonProbability(int )
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
    
  }




  public static void main(String[] args)
  {
    MathematicaSWB gen = new MathematicaSWB();

    Hypercube hypercube = new Hypercube();
    for(int i = 0; i < 100000; i++)
    {
        double a = gen.nextDouble();
        double b = gen.nextDouble();
        double c = gen.nextDouble();
        hypercube.addObservation(a, b, c);
    }
    System.out.println(hypercube.getPointDistribution());

  }
}