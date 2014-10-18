import java.util.TreeMap;

/**
 * Implements a 3D cube with 10^6 subcubes for testing RNG collisions.
 * @author gabriel.c-parent
 *
 */
public class UnitCube {

	private int[] array;
	private final int arraySize;

	public UnitCube() {
		arraySize = 1000000;
		array = new int[arraySize];
	}


	/**
	 * Adds an observation (3D point)
	 * @param x first coordinate
	 * @param y second coordinate
	 * @param z third coordinate
	 * @throws RuntimeException if the subcube's integer overflows
	 */
	public void addObservation(double x, double y, double z)
			throws RuntimeException {
		int a = (int) (x / 0.01);
		int b = (int) (y / 0.01);
		int c = (int) (z / 0.01);
		int arrayPosition = c + (100 * b) + (100 * 100 * a);

		if (array[arrayPosition] == Integer.MAX_VALUE) {
			throw new RuntimeException("Overflow in subcube counter");
		} else {
			array[arrayPosition] += 1;
		}
	}

	/**
	 * Check all the subcubes and returns the distribution of collisions.
	 * @return mapping {number of collisions -> number of occurrences}.
	 */
	public TreeMap<Integer, Integer> getPointDistribution() {
		TreeMap<Integer, Integer> collisions = new TreeMap<Integer, Integer>();
		for (int i = 0; i < arraySize; i++) {
			int value = array[i];
			if (!collisions.containsKey(value)) {
				collisions.put(value, 1);
			} else {
				collisions.put(value, collisions.get(value) + 1);
			}
		}
		return collisions;
	}

	/**
	 * Returns the number of collisions that have occured, without their distribution.
	 * @return number of collisions
	 */
	public int getNumCollisions() {
		// returns the number of empty subcubes
		int collisions = 0;
		for (int i = 0; i < arraySize; i++) {
			if (array[i] > 1) {
				collisions += (array[i] - 1);
			}
		}
		return collisions;
	}
}