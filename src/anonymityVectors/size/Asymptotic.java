package anonymityVectors.size;

public class Asymptotic implements Size<Double> {

	@Override
	public Double p(int N) {
		double f1 = 1.0 / (4 * N * Math.sqrt(3));
		double f2 = Math.exp(Math.PI * Math.sqrt((2 * N) / 3));
		return f1 * f2;
	}

}
