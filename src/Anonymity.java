import java.awt.EventQueue;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.stream.IntStream;

import anonymityVectors.appointments.Appointment;
import anonymityVectors.appointments.Choco;
import anonymityVectors.appointments.Greedy;
import anonymityVectors.appointments.RandomApp;
import anonymityVectors.size.Asymptotic;
import anonymityVectors.size.RecursiveP;
import anonymityVectors.size.RecursivePNaive;
import anonymityVectors.size.RecursivePQ;
import anonymityVectors.size.Size;
import anonymityVectors.tools.Population;
import anonymityVectors.tools.Tools;
import control.Controller;
import model.Model;
import view.MainFrame;

/**
 * Main class for checking the anonymity experiments
 * 
 * @author rafael
 *
 */
public class Anonymity {

	private static Model mod;
	private static MainFrame view;
	private static Controller control;

	/**
	 * Checking function p
	 */
	public static void checkSize() {
		final int N = 10000;
		// Size<BigInteger> s1 = new RecursiveP();
		Size<BigInteger> s2 = new RecursivePQ();
		// Size<BigInteger> s2 = new RecursivePNaive();
		Size<Double> s3 = new Asymptotic();

		System.out.println("N=" + N);
		// System.out.println("Recursive p(" + N + "): " + s1.p(N));
		// System.out.println("Asymptotic p(" + N + "): " + s3.p(N));
		System.out.println("RecursiveQ p(" + N + "): " + s2.p(N));
		// double r = Math.abs(s1.p(N).doubleValue() - s3.p(N)) /
		// s1.p(N).doubleValue();
		// System.out.println("Error asymptotic: " + r);
	}

	public static Population checkPopulation(int n) {

		final int Q = n / 100 + 5;
		int qmin = n / Q - 5 < 0 ? 1 : n / Q - 5;
		if (qmin <= 0)
			qmin = 1;
		final int qmax = n / Q + 10;
		final int R = n / 10;
		final int cmin = n / R - 3 < 0 ? 1 : n / R - 5;
		final int cmax = n / R + 4;

		Population pop = new Population(n, Q, qmin, qmax, R, cmin, cmax);
		// System.out.println(pop);
		return pop;

	}

	public static void checkGreedy(int n, int m) {
		Appointment c = new Greedy();
		checkMethod(n, c, m);

	}

	public static void checkChoco(int n, int m) {
		Appointment c = new Choco();
		checkMethod(n, c, m);

	}

	public static void checkRandom(int n, int m) {

		Appointment c = new RandomApp();
		checkMethod(n, c, m);
	}

	public static void checkThree(Population p, int n, int m) {

		Appointment rapp = new RandomApp();
		Appointment choco = new Choco();
		Appointment greedy = new Greedy();
		BigInteger impGreedyRand = new BigInteger("0");
		BigInteger impChocoGreedy = new BigInteger("0");

		for (int i = 0; i < m; i++) {
			// if (p==null)
			p = checkPopulation(n);
			// System.out.println(p);
			int[] base = Tools.base(p.getN(), p.getF());
			RecursivePQ pq = new RecursivePQ(n);
			BigInteger ibase = pq.index(base);

			// System.out.println(p);
			int vr[] = rapp.generate(p);
			int nr = Tools.getN(vr);
			BigInteger ir = pq.index(vr);
			// Tools.printV(vr); System.out.println("random N: "+nr+"\n ====");
			int vchoco[] = choco.generate(p);
			int nc = Tools.getN(vchoco);
			BigInteger ic = pq.index(vchoco);
			// Tools.printV(vchoco); System.out.println("choco N: "+nc+"\n
			// ====");
			int vgreedy[] = greedy.generate(p);
			BigInteger ig = pq.index(vgreedy);
			int ng = Tools.getN(vgreedy);
			// Tools.printV(vgreedy); System.out.println("greedy N: "+ng+"\n
			// ====");
			if (nr != n)
				System.out.println(" nr: " + nr);
			if (nc != n)
				System.out.println(" nc: " + nr);
			if (ng != n)
				System.out.println(" ng: " + nr);
			BigInteger impgr = Tools.improvement(ig, ir, ibase, m);
			impGreedyRand = impGreedyRand.add(impgr);
			BigInteger impcg = Tools.improvement(ic, ig, ibase, m);
			impChocoGreedy = impChocoGreedy.add(impcg);
		}

		System.out.println(impGreedyRand.divide(new BigInteger(m + "")).floatValue() / 10.0);
		System.out.println(impChocoGreedy.divide(new BigInteger(m + "")).floatValue() / 10.0);
	}

	public static void checkTwo(Population p, int n, int m) {

		Appointment rapp = new RandomApp();
		Appointment greedy = new Greedy();
		BigInteger impGreedyRand = new BigInteger("0");

		for (int i = 0; i < m; i++) {
			// if (p==null)
			// System.out.println("Generating population....");

			p = checkPopulation(n);
			// System.out.println("Generated");
			// System.out.println(p);
			int[] base = Tools.base(p.getN(), p.getF());
			RecursivePQ pq = new RecursivePQ(n);
			BigInteger ibase = pq.index(base);

			// System.out.println(p);
			// System.out.println("Start rand");
			long startTimeRand = System.nanoTime();

			int vr[] = rapp.generate(p);
			long endTimeRand = System.nanoTime();
			// System.out.println(endTimeRand - startTimeRand);

			int nr = Tools.getN(vr);
			BigInteger ir = pq.index(vr);
			// Tools.printV(vr); System.out.println("random N: "+nr+"\n ====");
			// System.out.println("Start heuristic");
			long startTimeHeur = System.nanoTime();
			int vgreedy[] = greedy.generate(p);
			long endTimeHeur = System.nanoTime();
			// System.out.println(endTimeHeur - startTimeHeur);

			BigInteger ig = pq.index(vgreedy);
			int ng = Tools.getN(vgreedy);
			// Tools.printV(vgreedy); System.out.println("greedy N: "+ng+"\n
			// ====");
			if (nr != n)
				System.out.println(" nr: " + nr);
			if (ng != n)
				System.out.println(" ng: " + nr);
			BigInteger impgr = Tools.improvement(ig, ir, ibase, m);
			impGreedyRand = impGreedyRand.add(impgr);
		}

		System.out.println(impGreedyRand.divide(new BigInteger(m + "")).floatValue() / 10.0);
	}

	public static void checkTwoK(Population p, int n, int m) {

		double krand = 0.0;
		double kgreedy = 0.0;
		double kbase = 0.0;

		Appointment rapp = new RandomApp();
		Appointment greedy = new Greedy();

		for (int i = 0; i < m; i++) {
			// if (p==null)
			System.out.println("Generating population....");

			p = checkPopulation(n);
			System.out.println("Generated");
			// System.out.println(p);
			int[] base = Tools.base(p.getN(), p.getF());
			kbase += Tools.getK(base);

			// System.out.println(p);
			System.out.println("Start rand");
			long startTimeRand = System.nanoTime();

			int vr[] = rapp.generate(p);
			long endTimeRand = System.nanoTime();
			System.out.println(endTimeRand - startTimeRand);
			int nr = Tools.getN(vr);

			krand += Tools.getK(vr);

			// Tools.printV(vr); System.out.println("random N: "+nr+"\n ====");
			System.out.println("Start heuristic");
			long startTimeHeur = System.nanoTime();
			int vgreedy[] = greedy.generate(p);
			long endTimeHeur = System.nanoTime();
			System.out.println(endTimeHeur - startTimeHeur);

			kgreedy += Tools.getK(vgreedy);
			int ng = Tools.getN(vgreedy);

			// Tools.printV(vgreedy); System.out.println("greedy N: "+ng+"\n
			// ====");
			if (nr != n)
				System.out.println(" nr: " + nr);
			if (ng != n)
				System.out.println(" ng: " + nr);

		}

		System.out.println("k base" + (kbase / (m * 1.0)));
		System.out.println("k rand" + (krand / (m * 1.0)));
		System.out.println("k greedy" + (kgreedy / (m * 1.0)));
	}

	public static void checkMethod(int n, Appointment c, int m) {
		final int iterations = m;

		RecursivePQ pq = new RecursivePQ(n);
		BigInteger total = new BigInteger("0");

		for (int i = 0; i < iterations; i++) {
			Population p = checkPopulation(n);
			int v[] = c.generate(p);

			System.out.println(p);
			System.out.println("N (from v): " + Tools.getN(v));
			for (int pi = 0; pi < v.length; pi++)
				System.out.print(v[pi] + " ");
			System.out.println("");
			BigInteger index = pq.index(v);
			System.out.println("\nIndex: " + index);
			total = total.add(index);
		}
		BigInteger divisor = new BigInteger(iterations + "");
		BigInteger result = total.divide(divisor);
		System.out.println(result);

	}

	public static void checkQ() {

		int v[] = { 1, 0, 2 };
		int n = Tools.getN(v);
		RecursivePQ q = new RecursivePQ(n);
		System.out.println("index({1,0,2}): " + q.index(v));

		n = 5000;
		int first[] = Tools.first(n);
		int last[] = Tools.last(n);
		q = new RecursivePQ(n);
		System.out.println("RecursiveQ p(" + n + "): " + q.p(n));
		System.out.println("Index first: " + q.index(first));
		System.out.println("Index last: " + q.index(last));

	}

	public static void checkcr(int f[], int fr[], int nc) {

		int n = IntStream.of(f).sum();
		System.out.println(n);
		int[] base = Tools.base(n, f);
		Tools.printV(base);
		int[] vr = Tools.base(n, fr);
		Tools.printV(vr);
		int nr = IntStream.of(fr).sum();
		if (n != nr)
			System.out.println("Error in N");
		int c[] = new int[nc];
		for (int i = 0; i < c.length; i++)
			c[i] = n / nc + 1;
		Population p = new Population(f, c);
		Appointment greedy = new Greedy();
		int vgreedy[] = greedy.generate(p);
		Tools.printV(vgreedy);
		int ng = Tools.getN(vgreedy);
		if (n != ng)
			System.out.println("Error in N heuristic");

		// check improvement
		RecursivePQ pq = new RecursivePQ(n);
		BigInteger ibase = pq.index(base);

		BigInteger ir = pq.index(vr);
		BigInteger ig = pq.index(vgreedy);
		BigInteger impgr = Tools.improvement(ig, ir, ibase, 1);
		System.out.println("improvement: " + impgr.floatValue() / 10.0);

		/*
		 * double krand=0.0; double kgreedy=0.0; double kbase=0.0;
		 * 
		 * Appointment rapp = new RandomApp(); Appointment greedy = new
		 * Greedy();
		 * 
		 * for (int i=0; i<m; i++) { //if (p==null) System.out.println(
		 * "Generating population....");
		 * 
		 * p = checkPopulation(n); System.out.println("Generated");
		 * //System.out.println(p); int [] base = Tools.base(p.getN(),
		 * p.getF()); kbase += Tools.getK(base);
		 * 
		 * //System.out.println(p); System.out.println("Start rand"); long
		 * startTimeRand = System.nanoTime();
		 * 
		 * int vr[] = rapp.generate(p); long endTimeRand = System.nanoTime();
		 * System.out.println(endTimeRand - startTimeRand); int nr =
		 * Tools.getN(vr);
		 * 
		 * krand += Tools.getK(vr);
		 * 
		 * //Tools.printV(vr); System.out.println("random N: "+nr+"\n ====");
		 * System.out.println("Start heuristic"); long startTimeHeur =
		 * System.nanoTime(); int vgreedy[] = greedy.generate(p); long
		 * endTimeHeur = System.nanoTime(); System.out.println(endTimeHeur -
		 * startTimeHeur);
		 * 
		 * kgreedy += Tools.getK(vgreedy); int ng = Tools.getN(vgreedy);
		 * 
		 * 
		 * //Tools.printV(vgreedy); System.out.println("greedy N: "+ng+"\n ===="
		 * ); if (nr != n) System.out.println(" nr: "+nr); if (ng != n)
		 * System.out.println(" ng: "+nr);
		 * 
		 * }
		 * 
		 * System.out.println("k base"+ (kbase/(m*1.0))); System.out.println(
		 * "k rand"+ (krand/(m*1.0))); System.out.println("k greedy"+
		 * (kgreedy/(m*1.0)));
		 */
	}

	public static void cr_1() {
		// \copy (select count(*), ',' from data where
		// screening_date='15.01.1992' group by year,reg order by year, reg) to
		// '/media/rafael/OS/rafa/investigacion/articulos/ijmi/15.01.1992.txt' ;
		// total regs 4541
		int[] q = { 2, 1, 1, 3, 2, 2, 2, 1, 3, 1, 1, 4, 1, 1, 3, 1, 1, 9, 2, 2, 1, 2, 1, 2, 1, 3, 11, 2, 2, 14, 6, 3, 1,
				6, 4, 3, 1, 18, 4, 4, 2, 8, 2, 3, 4, 1, 9, 2, 3, 3, 11, 2, 4, 2, 2, 14, 1, 8, 2, 14, 8, 1, 3, 2, 11, 3,
				4, 5, 1, 24, 6, 4, 4, 20, 2, 2, 1, 2, 17, 5, 2, 2, 3, 24, 3, 4, 1, 30, 5, 3, 2, 2, 17, 6, 6, 2, 2, 18,
				5, 3, 7, 5, 19, 11, 6, 3, 4, 18, 8, 6, 5, 4, 22, 5, 5, 5, 1, 23, 3, 4, 2, 4, 22, 4, 9, 6, 2, 30, 8, 5,
				2, 4, 33, 4, 6, 7, 5, 43, 13, 7, 6, 5, 23, 7, 12, 7, 2, 44, 9, 9, 11, 3, 56, 14, 6, 4, 4, 69, 16, 12, 7,
				1, 59, 12, 8, 6, 10, 57, 11, 12, 11, 9, 55, 13, 17, 11, 8, 65, 14, 10, 10, 8, 58, 10, 15, 13, 4, 51, 8,
				17, 11, 7, 64, 16, 5, 18, 3, 58, 9, 17, 13, 7, 52, 15, 15, 7, 5, 66, 8, 19, 17, 5, 64, 15, 19, 9, 2, 73,
				12, 16, 7, 2, 70, 16, 12, 6, 9, 69, 17, 17, 12, 7, 67, 14, 19, 9, 4, 64, 9, 13, 11, 4, 89, 24, 18, 12,
				5, 91, 16, 24, 12, 6, 85, 21, 19, 13, 6, 89, 24, 18, 11, 8, 84, 21, 16, 18, 5, 97, 23, 9, 18, 2, 73, 17,
				15, 15, 2, 85, 18, 22, 12, 71, 16, 18, 16, 2, 70, 19, 24, 17, 1, 61, 14, 18, 11, 3, 64, 11, 9, 7, 31,
				11, 9, 6, 1 };
		int[] qr = { 2, 1, 3, 1, 2, 4, 8, 12, 5, 1, 11, 3, 1, 1, 1, 17, 1, 8, 8, 9, 2, 1, 3, 10, 1, 8, 1, 9, 3, 4, 1, 5,
				25, 8, 5, 8, 1, 3, 1, 7, 6, 2, 3, 1, 14, 10, 24, 2, 4, 1, 2, 11, 1, 5, 2, 7, 1, 2, 7, 1, 2, 2, 1, 1, 1,
				7, 6, 1, 5, 2, 15, 4, 5, 6, 5, 8, 5, 3, 4, 8, 3, 3, 2, 4, 1, 6, 1, 2, 4, 5, 1, 4, 1, 3, 1, 11, 7, 1, 4,
				7, 8, 1, 2, 2, 1, 4, 2, 9, 1, 5, 3, 4, 3, 1, 5, 6, 5, 1, 6, 2, 1, 2, 2, 1, 3, 3, 1, 4, 2, 4, 13, 2, 2,
				1, 2, 2, 1, 5, 3, 2, 4, 6, 1, 6, 2, 6, 6, 11, 2, 2, 3, 1, 4, 2, 2, 6, 6, 4, 3, 2, 4, 3, 2, 7, 5, 3, 7,
				1, 2, 6, 3, 1, 2, 2, 1, 1, 2, 9, 9, 1, 2, 2, 2, 7, 3, 6, 1, 5, 2, 5, 1, 3, 5, 2, 2, 6, 2, 1, 1, 4, 1, 4,
				15, 6, 9, 6, 1, 2, 1, 5, 3, 1, 2, 2, 2, 2, 4, 8, 7, 1, 4, 8, 14, 3, 1, 2, 15, 4, 10, 4, 1, 3, 6, 1, 1,
				2, 1, 1, 2, 4, 1, 1, 6, 2, 1, 7, 1, 9, 1, 1, 2, 2, 2, 2, 3, 6, 3, 2, 5, 1, 2, 2, 1, 5, 4, 4, 4, 14, 3,
				1, 4, 8, 1, 5, 7, 8, 6, 2, 7, 2, 1, 9, 21, 2, 2, 1, 6, 3, 2, 8, 2, 4, 19, 3, 2, 5, 1, 6, 4, 1, 6, 1, 3,
				7, 3, 9, 5, 3, 29, 33, 6, 2, 2, 5, 3, 24, 3, 4, 1, 4, 7, 2, 7, 2, 5, 5, 3, 1, 1, 4, 21, 8, 4, 10, 7, 1,
				3, 2, 10, 4, 1, 1, 12, 3, 1, 5, 5, 9, 3, 9, 1, 8, 5, 1, 3, 2, 4, 2, 4, 2, 3, 6, 1, 3, 6, 4, 3, 3, 7, 3,
				1, 4, 1, 10, 3, 4, 1, 2, 3, 1, 7, 5, 2, 1, 7, 1, 7, 6, 8, 1, 5, 1, 2, 9, 2, 1, 10, 6, 2, 8, 2, 5, 5, 2,
				2, 5, 1, 6, 3, 31, 1, 5, 3, 4, 1, 4, 4, 1, 6, 15, 5, 1, 7, 25, 3, 5, 1, 2, 1, 11, 6, 4, 8, 9, 1, 4, 1,
				3, 1, 1, 13, 1, 7, 1, 4, 2, 1, 7, 2, 5, 7, 5, 2, 3, 1, 2, 8, 4, 19, 1, 4, 1, 1, 8, 7, 4, 10, 5, 2, 16,
				6, 21, 7, 2, 3, 2, 3, 1, 4, 20, 4, 5, 2, 9, 5, 3, 2, 6, 4, 1, 2, 11, 8, 13, 1, 1, 2, 3, 1, 3, 2, 1, 1,
				7, 1, 11, 7, 13, 6, 5, 4, 5, 4, 1, 4, 2, 2, 1, 7, 4, 4, 2, 1, 33, 15, 2, 1, 23, 4, 3, 1, 5, 1, 1, 11, 4,
				6, 2, 13, 2, 6, 4, 24, 1, 2, 2, 1, 1, 9, 5, 6, 7, 1, 8, 1, 8, 3, 13, 4, 2, 12, 5, 20, 4, 3, 5, 2, 4, 2,
				8, 13, 4, 8, 1, 2, 7, 2, 1, 5, 4, 8, 1, 1, 2, 6, 3, 4, 2, 14, 1, 1, 4, 3, 9, 1, 1, 1, 29, 2, 2, 2, 5, 1,
				1, 5, 2, 1, 6, 2, 2, 2, 1, 8, 6, 2, 2, 34, 1, 10, 4, 1, 3, 8, 11, 7, 2, 2, 4, 1, 4, 1, 1, 5, 1, 1, 4,
				12, 9, 2, 2, 2, 1, 1, 1, 5, 1, 4, 1, 6, 7, 4, 1, 12, 1, 1, 2, 27, 10, 1, 2, 1, 6, 2, 7, 6, 22, 6, 2, 6,
				2, 2, 13, 3, 2, 4, 3, 10, 1, 6, 3, 10, 3, 4, 3, 10, 2, 11, 2, 2, 7, 3, 1, 2, 1, 7, 11, 1, 1, 1, 1, 4,
				15, 3, 2, 16, 1, 2, 5, 6, 3, 5, 8, 4, 7, 5, 11, 11, 6, 2, 4, 1, 6, 2, 5, 3, 5, 3, 5, 14, 9, 2, 7, 23, 2,
				1, 3, 21, 11, 2, 1, 2, 6, 6, 16, 5, 9, 10, 6, 6, 16, 13, 10, 1, 2, 7, 3, 5, 1, 5, 1, 1, 3, 1, 7, 5, 1,
				2, 6, 3, 2, 1, 28, 1, 7, 2, 5, 2, 15, 1, 4, 2, 1, 4, 3, 7, 2, 1, 3, 2, 5, 1, 1, 2, 4, 1, 10, 14, 2, 1,
				5, 1, 1, 4, 2, 3, 24, 5, 1, 1, 1, 3, 7, 2, 1, 6, 29, 1, 1, 3, 2, 3, 1, 7, 1, 17, 6, 4, 8, 3, 2, 3, 4, 1,
				3, 8, 4, 2, 10, 5, 10, 6, 9, 24, 27, 8, 3, 2, 2, 1, 1, 5, 1, 1, 14, 8, 4, 2, 1, 3, 6, 4, 3, 5, 1, 1, 1,
				4, 2, 3, 1, 1, 1, 7, 2, 1, 1, 1, 2, 2, 9, 3, 8, 4, 10, 1, 6, 1, 11, 4, 3, 2, 9, 4, 2, 3, 1, 7, 2, 38, 3,
				3, 31, 3, 7, 6, 2, 1, 1, 2, 1, 11, 4, 3, 2, 1, 12, 3, 1, 11, 1, 7, 3, 16, 1, 1, 4, 5, 3, 2, 14, 1, 13,
				7, 3, 9, 6, 3, 23, 4, 3, 3, 3, 3, 2, 4, 2, 1, 1, 2 };

		// select count(*) from data where reg=1 and
		// screening_date='15.01.1992' group by year order by year
		// for (int i=0; i<q.length; i++) System.out.print(qr[i]+", ");
		// System.out.println();
		checkcr(q, qr, 21);
	}

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				mvc();

			}
		});
	}

	public static void mvc() {
		// the model
		mod = new Model();
		// view
		view = new MainFrame();
		// controller
		control = new Controller(view, mod);
		// configura la vista
		view.setController(control);
		// y arranca la interfaz (vista):
		view.start();
	}

	public static void test(String[] args) {
		System.out.println("version 2.0"); 
		cr_1();
		/*
		 * checkRandom(20,1); checkGreedy(20,1); checkChoco(20,1);
		 */
		// int []f = {1,1,1,4,13};
		// int []c = {13,7};

		// Population p = new Population(f,c);

		// checkThree(null, 50,1);
		// checkTwo(null,1000,100);

		// checkTwoK(null,100000,1);
	}

}
