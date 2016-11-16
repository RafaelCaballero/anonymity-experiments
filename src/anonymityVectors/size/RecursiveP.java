package anonymityVectors.size;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Recursive implementation of function p(N), which calculates the number of
 * partitions/anonymity vectors for a population of size N It uses a hashmap in
 * order to avoid repetitive method calls (tabling)
 * 
 * @author rafael caballero
 *
 */
public class RecursiveP implements Size<BigInteger> {
	/**
	 * Already computed p(a,b) calls. Since the method has two arguments they
	 * are combined into a single value using method key.
	 */
	private Map<Long, BigInteger> map;
	private long N;
	/**
	 * Just for statistical purpose
	 */
	private long totalCalls;
	private boolean trace = true;

	public RecursiveP() {
		map = new HashMap<Long, BigInteger>();
		totalCalls = 0L;

	}

	public RecursiveP(int N) {
		map = new HashMap<Long, BigInteger>();
		this.N = N;
		totalCalls = 0L;

	}

	@Override
	public BigInteger p(int N) {
		this.N = N;
		map = new HashMap<Long, BigInteger>();
		totalCalls = 0L;
		BigInteger r = p(N, N);
		if (trace) {
			System.out.println("totalCalls: " + totalCalls + " r:" + r);
		}
		return r;

	}

	/**
	 * Returns the key for the hashmap
	 * 
	 * @param a
	 *            an integer <=N
	 * @param b
	 *            an integer <=N
	 * @return the key for the map "map"
	 */
	private long key(int a, int b) {
		return (a + 1L) * N + (b + 0L);
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
		if (n < 0 || m == 0)
			return new BigInteger("0");
		else if (n == 0 || m == 1)
			return new BigInteger("1");
		else {
			totalCalls++;
			long k = key(n, m);
			BigInteger v = map.get(k);
			if (v == null) {
				BigInteger r = p(n - m, m).add(p(n, m - 1));
				map.put(k, r);
				return r;
			} else {
				return v;
			}
		}
	}

}
