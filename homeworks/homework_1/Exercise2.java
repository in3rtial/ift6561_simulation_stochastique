import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStreamBase;

/**
 * Idea is to generate random numbers and plot them into 3D cube and observe the
 * number of collisions. Ideally, the number of collisions should be equal to
 * the Poisson approximation: ((numberOfPoints^2) / (2 * numberOfCases)).
 * 
 * @author gabriel c-parent
 */
public class Exercise2 {

	int[] collisionsA; // collisions using SWB
	int[] collisionsB; // collisions using MRG32k3A
	/**
	 * Launches the exercise 2.
	 * 
	 */
	public Exercise2() {
		this.collisionsA = exerciseA();
		this.collisionsB = exerciseB();
	}

	/**
	 * Generates 3 number from the generator, u[i], u[i+20], u[i+24] where i is
	 * a multiple of 25 and i is the state of the RNG.
	 * 
	 * @param generator
	 * @return u[i], u[i+20], u[i+24] where i is a multiple of 25
	 */
	private double[] getNextPoint(RandomStreamBase generator) {
		double[] generated = new double[25];
		double[] ret = new double[3];

		for (int i = 0; i < 25; i++) {
			generated[i] = generator.nextDouble();
		}
		ret[0] = generated[0]; // u[25i]
		ret[1] = generated[20]; // u[25i + 20]
		ret[2] = generated[24]; // u[25i + 24]
		return ret;
	}

	/**
	 * Exercise 2 a) Idea is to generate random numbers and plot them into 3D
	 * cube and observe the number of collisions. Ideally, the number of
	 * collisions should be equal to the Poisson approximation:
	 * ((numberOfPoints^2) / (2 * numberOfCases)).
	 * 
	 * Generate the numbers 10 times with MathematicaSWB and observe
	 * 
	 * @return number of collisions for each run
	 */
	public int[] exerciseA() {
		MathematicaSWB generator = new MathematicaSWB();
		int[] collisions = new int[10];

		for (int iteration = 0; iteration < 10; iteration++) {
			UnitCube cube = new UnitCube();
			for (int i = 0; i < 10000; i++) {
				double[] point = getNextPoint(generator);
				cube.addObservation(point[0], point[1], point[2]);
			}
			collisions[iteration] = cube.getNumCollisions();
		}
		return collisions;
	}

	/**
	 * Exercise 2 b) Generate the numbers 10 times with MRG32k3a and observe
	 * 
	 * @return number of collisions for each run
	 */
	public int[] exerciseB() {
		MRG32k3a generator = new MRG32k3a();
		int[] collisions = new int[10];

		for (int iteration = 0; iteration < 10; iteration++) {
			UnitCube cube = new UnitCube();
			for (int i = 0; i < 10000; i++) // add 10^4 random points to the
											// cube
			{
				double[] point = getNextPoint(generator);
				cube.addObservation(point[0], point[1], point[2]);
			}
			collisions[iteration] = cube.getNumCollisions();
		}
		return collisions;
	}

}
