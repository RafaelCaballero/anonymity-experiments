package anonymityVectors.appointments;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.search.strategy.ISF;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class Choco extends Appointment {

	@Override
	public int[] generate(int n, int Q, int[] f, int R, int[] c) {
		int max1 = 0;
		for (int i = 0; i < Q; i++)
			if (f[i] > max1)
				max1 = f[i];// frec maxima

		int max2 = 0;
		for (int i = 0; i < R; i++)
			if (c[i] > max2)
				max2 = c[i];// recuso maximo

		int max = max2;
		if (max1 < max2)
			max = max1;

		int p = 0;
		for (int i = 0; i < Q; i++)
			p = p + f[i]; // poblacion total

		int[] v = new int[p];// vector de anonimicidad TAMAÑO MIN DE LOS MAX.
								// Ric;hard

		Solver solver = new Solver("anom");

		IntVar[] a = new IntVar[Q * R];// matriz plana
		int minLocal;
		for (int i = 0; i < Q; i++) {
			for (int j = 0; j < R; j++) {

				if (c[j] > f[i])
					minLocal = f[i];
				else
					minLocal = c[j];
				a[i * R + j] = VariableFactory.enumerated("a" + i + "_" + j, 0, minLocal, solver);
			}
		}

		IntVar[] fila = null;
		IntVar[] columna = null;
		// C1
		for (int i = 0; i < Q; i++) {
			fila = new IntVar[R];
			for (int j = 0; j < R; j++) {
				fila[j] = a[i * R + j];
			}

			// IntVar sum=VariableFactory.enumerated(qf[i+1]+"", qf[i+1],
			// qf[i+1], solver);//TRUCO?
			IntVar sum = VariableFactory.fixed(f[i], solver);
			solver.post(IntConstraintFactory.sum(fila, sum));
		}
		// C2
		for (int i = 0; i < R; i++) {
			columna = new IntVar[Q];
			for (int j = 0; j < Q; j++) {

				columna[j] = a[i + j * R];
			}
			// IntVar sum1=VariableFactory.enumerated(rc[i+1]+"", rc[i+1],
			// rc[i+1], solver);
			IntVar sum1 = VariableFactory.fixed(c[i], solver);
			solver.post(IntConstraintFactory.sum(columna, "<=", sum1));
		}

		int[] weight = new int[p];
		IntVar[] vchoco = new IntVar[p];
		IntVar[] weightByValue = new IntVar[p];
		for (int i = 0; i < p; i++) {
			vchoco[i] = VariableFactory.enumerated("v" + (i + 1), 0, p / (i + 1), solver);
			weightByValue[i] = VariableFactory.enumerated("wbyvalue" + (i + 1), 0, p, solver);
		}
		for (int i = 0; i < p; i++)
			weight[i] = i + 1;
		for (int i = 0; i < p; i++)
			solver.post(IntConstraintFactory.times(vchoco[i], i + 1, weightByValue[i]));

		// C3
		solver.post(IntConstraintFactory.global_cardinality(a, weight, vchoco, false));

		// C4: vchoco es un vector de anonimato válido
		IntVar ps = VariableFactory.fixed(p, solver);
		solver.post(IntConstraintFactory.sum(weightByValue, ps));

		// System.out.println("Al find solution!");
		int l = vchoco.length + a.length;
		IntVar[] vl = new IntVar[l];
		for (int i = 0; i < vchoco.length; i++)
			vl[i] = vchoco[i];
		for (int i = vchoco.length; i < l; i++)
			vl[i] = a[i - vchoco.length];

		solver.set(ISF.custom(ISF.lexico_var_selector(), ISF.min_value_selector(), vl));
		if (solver.findSolution()) {
			for (int i = 0; i < Q; i++) {
				for (int j = 0; j < R; j++) {
					// System.out.print(a[i * R + j].getValue() + " ");
				}
				// System.out.println("");
			}

			for (int i = 0; i < p; i++)
				v[i] = vchoco[i].getValue();
		} else
			System.out.println("No solution ");

		return v;

	}

}
