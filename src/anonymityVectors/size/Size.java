package anonymityVectors.size;

import java.math.BigInteger;

/**
 * This interface defines the function p that represents the number of
 * partitions for a positive integer N, or analogously the number of anonymity
 * vectors for a population size of N
 * 
 * @author rafael
 *
 */
public interface Size<T> {
	/**
	 * Number of partitions/anonymity vectors
	 * 
	 * @param N
	 *            the integer to partition/population size
	 * @return Number of partitions/anonymity vectors
	 */
	public T p(int N);

}
