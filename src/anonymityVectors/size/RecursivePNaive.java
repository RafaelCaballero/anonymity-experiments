package anonymityVectors.size;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Recursive implementation of function p(N), which calculates the number of
 * partitions/anonymity vectors for a population of size N.
 * 
 * @author rafael caballero
 *
 */
public class RecursivePNaive implements Size<BigInteger> {
	private long totalCalls;
	private boolean trace = true;

	@Override
	public BigInteger p(int N) {
		totalCalls = 0L;
		BigInteger r = p(N, N);
		if (trace) {
			System.out.println("totalCalls: " + totalCalls + " r:" + r);
		}
		return r;

	}

	/**
	 * Number of partitions/anonymity vectors of size n, where each summand is
	 * less than or equal to m
	 * 
	 * @param n:
	 *            integer to partition/population size
	 * @param m:
	 *            upper bound for summands
	 * @return number of partitions(anonymity vectors
	 */
	public BigInteger p(int n, int m) {

		BigInteger r;
		if (n < 0)
			r = new BigInteger("0");
		else if (n == 0 || m == 1)
			r = new BigInteger("1");
		else {
			totalCalls++;
			r = p(n - m, m).add(p(n, m - 1));
		}
		return r;
	}

}
