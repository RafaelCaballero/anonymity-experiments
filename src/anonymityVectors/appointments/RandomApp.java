package anonymityVectors.appointments;

import java.util.Arrays;
import java.util.Random;

import anonymityVectors.tools.Tools;

public class RandomApp extends Appointment {

	@Override
	public int[] generate(int n, int Q, int[] f, int R, int[] c) {
		int[] v = new int[n];

		// copy everything that can be modified
		int[] fc = Arrays.copyOfRange(f, 0, f.length);
		int[] cc = Arrays.copyOfRange(c, 0, c.length);
		int nc = n;

		// appointment
		int[][] a = new int[Q][R];

		Random randomGenerator = new Random();

		while (nc > 0) {
			int randomQ = randomGenerator.nextInt(Q);
			int randomR = randomGenerator.nextInt(R);

			while (fc[randomQ] == 0)
				randomQ = (randomQ + 1) % Q;
			while (cc[randomR] == 0)
				randomR = (randomR + 1) % R;

			a[randomQ][randomR]++;
			fc[randomQ]--;
			cc[randomR]--;
			nc--;
		}

		v = Tools.getV(a, n, Q, R);
		return v;
	}
}
