package anonymityVectors.size;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import anonymityVectors.tools.Tools;

/**
 * Recursive implementation of function p(N), which calculates the number of
 * partitions/anonymity vectors for a population of size N It uses a hashmap in
 * order to avoid repetitive method calls (tabling)
 * 
 * @author rafael caballero
 *
 */
public class RecursivePQ implements Size<BigInteger> {
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

	public RecursivePQ() {
		map = new HashMap<Long, BigInteger>();
		totalCalls = 0L;

	}

	public RecursivePQ(int N) {
		map = new HashMap<Long, BigInteger>();
		this.N = N;
		totalCalls = 0L;

	}

	@Override
	public BigInteger p(int N) {
		this.N = N;
		map = new HashMap<Long, BigInteger>();
		totalCalls = 0L;
		BigInteger r = q(N, 1);
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
	 * Number of anonymity vectors of size n, starting with weight m
	 * 
	 * @param n:
	 *            integer to partition/population size
	 * @param m:
	 *            first weight
	 * @return number of partitions(anonymity vectors
	 */
	public BigInteger q(int n, int m) {
		if (n == 0 || m == n)
			return new BigInteger("1");
		else if (/* n < 0 || */ m > n)
			return new BigInteger("0");
		else {
			totalCalls++;
			long k = key(n, m);
			BigInteger v = map.get(k);
			if (v == null) {
				v = new BigInteger("0");
				int t = n / m;
				for (int i = 0; i <= t; i++) {
					BigInteger sumand = q(n - i * m, m + 1);
					v = v.add(sumand);
				}

				map.put(k, v);
				return v;
			} else {
				return v;
			}
		}
	}

	/**
	 * Ordinal index of an anonymity vector
	 * 
	 * @param v
	 * @return The lexicographic index, starting with 0 for (N)
	 */
	public BigInteger index(int[] v) {
		BigInteger r = new BigInteger("0");
		int l = Tools.getLength(v);
		int n = Tools.getN(v);
		for (int i = 0, k = 1; i < l; i++, k++) {
			// values start with pos i greater than v[i]
			int t = n / (i + 1);
			for (int j = v[i] + 1; j <= t; j++) {
				BigInteger sumand = q(n - (i + 1) * j, i + 2); // i+2 because i
																// starts in 0
				r = r.add(sumand);
			}
			// before proceeding assume that we consider vectors which (i+1)-th
			// position
			// starts in v[i]
			n -= (i + 1) * v[i];

		}

		return r;

	}
}
