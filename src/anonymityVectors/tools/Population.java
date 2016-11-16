package anonymityVectors.tools;

import java.util.Random;

/**
 * Generates random population
 * 
 * @author rafael
 *
 */
public class Population {

	/**
	 * Population
	 */
	int n;
	/**
	 * Number of quasi-id different values
	 */
	int Q;
	/**
	 * Frequency of each quasi-id value
	 */
	int[] f;
	int R;
	int[] c;

	/**
	 * @param f
	 *            frequency of each quasi. Array of Q elements
	 * @param c
	 *            capacity of each resource
	 */
	public Population(int[] f, int[] c) {
		this.Q = f.length;
		this.R = c.length;
		n = 0;
		for (int i = 0; i < Q; i++)
			n += f[i]; // poblacion total
		this.f = new int[f.length];
		this.c = new int[c.length];
		System.arraycopy(f, 0, this.f, 0, f.length);
		System.arraycopy(c, 0, this.c, 0, c.length);
	}

	/**
	 * Generates: - resources capacity given the parameters and using random
	 * values (attr. c) - Vector of quasiids (f) Precond: cmax*R<n Postconds: -
	 * sum(c)>=n. c.length==R - sum(f)=n. f.length==Q
	 * 
	 * @param n:
	 *            total population
	 * @param Q:
	 *            Number of quasi-id values
	 * @param qmin:
	 *            Minimum frequency for any quasiid
	 * @param qmax:
	 *            Max. frequency for any quasi
	 * @param R:
	 *            Number of resources
	 * @param cmin:
	 *            Minimum capacity
	 * @param cmax:
	 *            Max. capacity.
	 */
	public Population(int n, int Q, int qmin, int qmax, int R, int cmin, int cmax) {
		this.n = n;
		this.Q = Q;
		this.R = R;
		// create the vectors
		f = generateRandom(Q, n, qmin, qmax, true);
		c = generateRandom(R, n, cmin, cmax, false);
		if (f == null || c == null)
			System.out.println("Erroneous appointment parameters n:" + n + " Q:" + Q + " qmin: " + qmin + " qmax: "
					+ qmax + " R: " + R + " cmin: " + cmin + " cmax: " + cmax);

	}

	/**
	 * Generates a new vector v of size l such that: - either sum(v)==n (if
	 * equal=true) or sum(v)>=n (if equal=false) - min<=v[i]<=max
	 * 
	 * @param l
	 *            size of the vector
	 * @param n
	 *            total population
	 * @param min
	 *            min value for each vector component
	 * @param max
	 *            max. value for each vector component
	 * @param equal:
	 *            true if the sum of the produced vector must be exactly n.
	 *            false indicates that it can be also >= n
	 * @return the new vector, or null if l*max < n
	 */
	private int[] generateRandom(int l, int n, int min, int max, boolean equal) {
		int[] vector = new int[l];
		if (l * max < n)
			vector = null;
		else {
			Random rnd = new Random();
			// first attempt
			for (int i = 0; i < l; i++)
				vector[i] = (int) (rnd.nextDouble() * (max - min + 1) + min);
			// get the sum and compare with n
			int sum = 0;
			for (int i = 0; i < l; i++)
				sum += vector[i];
			// System.out.println("sum: "+sum);
			// if (sum<n)
			// System.out.println("too few");
			// if too few; pick up elements with value < max and increment
			while (sum < n) {
				boolean found = false;
				for (int i = 0; i < l && !found; i++)
					if (vector[i] < max) {
						found = true;
						sum++;
						vector[i]++;
					}
			}
			// too many
			// if (sum > n && equal)
			// System.out.println("too many");
			while (sum > n && equal) {
				boolean found = false;
				for (int i = 0; i < l && !found; i++)
					if (vector[i] > min) {
						found = true;
						sum--;
						vector[i]--;
					}
			}
		}
		return vector;

	}

	@Override
	public String toString() {
		String r = "N: " + n + "\nQ:" + Q + "\nf={";
		for (int i = 0; i < Q; i++)
			r += f[i] + (i == Q - 1 ? "}\n" : ",");
		r += "R: " + R + "\nc={";
		for (int i = 0; i < R; i++)
			r += c[i] + (i == R - 1 ? "}\n" : ",");

		return r;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getQ() {
		return Q;
	}

	public void setQ(int q) {
		Q = q;
	}

	public int[] getF() {
		return f;
	}

	public void setF(int[] f) {
		this.f = f;
	}

	public int getR() {
		return R;
	}

	public void setR(int r) {
		R = r;
	}

	public int[] getC() {
		return c;
	}

	public void setC(int[] c) {
		this.c = c;
	}

}
