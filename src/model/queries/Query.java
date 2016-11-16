package model.queries;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.slf4j.Logger;

import anonymityVectors.appointments.Appointment;
import anonymityVectors.appointments.Greedy;
import anonymityVectors.size.RecursivePQ;
import anonymityVectors.tools.Population;
import anonymityVectors.tools.Tools;
import logback.AreaAppender;
import model.connection.ConnectionData;
import model.connection.SQLConnector;

/**
 * General queries to the database. Obtaining information about user tables and
 * views. It is assumed that the connection has been established in advance.
 * 
 * @author rafa
 *
 */
public class Query {

	private static final Logger logger = AreaAppender.getLogger(Query.class);

	// system schemata are excluded
	public static List<String> sExcluded = new ArrayList<String>(Arrays.asList("pg_catalog", "information_schema"));

	public static String GETRELATIONS = "select table_name, table_schema from INFORMATION_SCHEMA.tables "; // WHERE
																											//
	public static String GETVIEWCODE = "select definition from pg_views where schemaname=? and viewname = ?";

	public static String GETSEARCHPATH = "SHOW search_path";

	public static String GETREGIONS = "select distinct reg from cr_data order by reg";
	public static String GETLABS = "select distinct lab_nr from cr_data where reg=? order by lab_nr";
	public static String GETDATES = "select distinct diagnosisdate from cr_data where reg=?  order by diagnosisdate";
	// Q, using year as quasi-id
	public static String GETQ = "select count(*) from cr_data where reg=? and diagnosisdate=? group by year order by year";
	// QR, using year and lab_nr as quasi_id
	public static String GETQR = "select count(*) from cr_data where reg=? and diagnosisdate=? group by year,lab_nr order by year,lab_nr";
	// max capacity of a laboratoty
	public static String GETCAPACITY = "select count(*) from cr_data where reg=? and diagnosisdate=? and lab_nr=?";

	/**
	 * Obtains a list of user defined views
	 * 
	 * @param conn
	 *            The current connection
	 * @return List of Strings with the name of the views.
	 */
	public static List<String> getUserRelationNames(Connection conn) {
		ArrayList<String> la = null;
		if (conn != null) {
			la = new ArrayList<String>();
			// make sure autocommit is off
			try {
				conn.setAutoCommit(false);
				Statement st;

				st = conn.createStatement();
				// Turn use of the cursor on.
				st.setFetchSize(50);
				ResultSet rs = st.executeQuery(Query.GETRELATIONS);
				while (rs.next()) {
					String tname = rs.getString(1);
					String tschema = rs.getString(2);
					if (!sExcluded.contains(tschema))
						la.add(tschema + "." + tname);
				}
				rs.close();
				// Turn the cursor off.
				st.setFetchSize(0);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return la;

	}

	/**
	 * Obtains a list of user defined views
	 * 
	 * @param conn
	 *            The current connection
	 * @return List of Strings with each schema name in the search path
	 */
	public static List<String> getSearchPath(Connection conn) {
		List<String> la = null;
		if (conn != null) {
			// make sure autocommit is off
			try {
				conn.setAutoCommit(false);
				Statement st;

				st = conn.createStatement();
				// Turn use of the cursor on.
				st.setFetchSize(50);
				ResultSet rs = st.executeQuery(Query.GETSEARCHPATH);
				if (rs.next()) {
					String tname = rs.getString(1);
					logger.info("Search path: {} ", tname);

					la = Arrays.asList(tname.split("\\s*,\\s*"));
				}
				rs.close();
				// Turn the cursor off.
				st.setFetchSize(0);

			} catch (SQLException e) {
				logger.error("Error fetching search path");
				e.printStackTrace();
			}

		}
		return la;

	}

	public static List<Integer> getRegions(Connection conn) {
		List<Integer> la = null;
		if (conn != null) {
			la = new ArrayList<Integer>();
			// make sure autocommit is off
			try {
				conn.setAutoCommit(false);
				Statement st;

				st = conn.createStatement();
				// Turn use of the cursor on.
				st.setFetchSize(50);
				ResultSet rs = st.executeQuery(Query.GETREGIONS);
				while (rs.next()) {
					int reg = rs.getInt(1);
					la.add(reg);
				}
				rs.close();
				// Turn the cursor off.
				st.setFetchSize(0);

			} catch (SQLException e) {
				logger.error("Error getting regions {}. {}", Query.GETREGIONS, e.getMessage());
				e.printStackTrace();
			}

		}
		return la;

	}

	public static List<Integer> getLabs(Connection conn, Integer reg) {
		List<Integer> la = null;
		if (conn != null) {
			la = new ArrayList<Integer>();
			// make sure autocommit is off
			try {
				conn.setAutoCommit(false);
				PreparedStatement st;

				st = conn.prepareStatement(GETLABS);
				// put the parameter
				st.setInt(1, reg);

				// Turn use of the cursor on.
				st.setFetchSize(50);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					int lab = rs.getInt(1);
					la.add(lab);
				}
				rs.close();
				// Turn the cursor off.
				st.setFetchSize(0);

			} catch (SQLException e) {
				logger.error("Error getting labs {}. {}", Query.GETLABS, e.getMessage());
				e.printStackTrace();
			}

		}
		return la;

	}

	public static List<String> getDiagnosisDates(Connection conn, Integer reg) {
		List<String> la = null;
		if (conn != null) {
			la = new ArrayList<String>();
			// make sure autocommit is off
			try {
				conn.setAutoCommit(false);
				PreparedStatement st;

				st = conn.prepareStatement(GETDATES);
				// put the parameter
				st.setInt(1, reg);

				// Turn use of the cursor on.
				st.setFetchSize(50);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					String date = rs.getString(1);
					la.add(date);
				}
				rs.close();
				// Turn the cursor off.
				st.setFetchSize(0);

			} catch (SQLException e) {
				logger.error("Error getting diagnosis dates {}. {}", Query.GETDATES, e.getMessage());
				e.printStackTrace();
			}

		}
		return la;

	}

	public static int[] getQ(Connection conn, Integer reg, String date, String query) {
		List<Integer> la = null;
		PreparedStatement st = null;
		if (conn != null) {
			la = new ArrayList<Integer>();
			// make sure autocommit is off
			try {
				conn.setAutoCommit(false);

				st = conn.prepareStatement(query);
				// put the parameter
				st.setInt(1, reg);
				st.setString(2, date);

				// Turn use of the cursor on.
				st.setFetchSize(50);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Integer qi = rs.getInt(1);
					la.add(qi);
				}
				rs.close();
				// Turn the cursor off.
				st.setFetchSize(0);

			} catch (SQLException e) {
				logger.error("Error getting q {}. {}", (st == null ? Query.GETQ : st.toString()), e.getMessage());
				e.printStackTrace();
			}

		}
		return la.stream().mapToInt(Integer::intValue).toArray();

	}

	public static void getImprovement(Connection conn, Integer reg, List<String> dates, List<Integer> labs) {
		List<Integer> la1 = null;
		List<Integer> la2 = null;
		try {
			PreparedStatement st1 = conn.prepareStatement(Query.GETQ);
			PreparedStatement st2 = conn.prepareStatement(Query.GETQR);
			st1.setInt(1, reg);
			st2.setInt(1, reg);
			conn.setAutoCommit(false);
			st1.setFetchSize(50);
			st2.setFetchSize(50);
			double total = 0.0, min = 0.0, max = 0.0;
			int n = 0;
			if (conn != null) {
				for (String date : dates) {
					la1 = new ArrayList<Integer>();
					la2 = new ArrayList<Integer>();
					// make sure autocommit is off

					// put the parameter
					st1.setString(2, date);
					st2.setString(2, date);

					// Turn use of the cursor on.
					ResultSet rs1 = st1.executeQuery();
					ResultSet rs2 = st2.executeQuery();
					while (rs1.next()) {
						Integer qi = rs1.getInt(1);
						la1.add(qi);
					}
					rs1.close();
					while (rs2.next()) {
						Integer qi = rs2.getInt(1);
						la2.add(qi);
					}
					rs2.close();

					//
					int[] f = la1.stream().mapToInt(Integer::intValue).toArray();
					int[] fr = la2.stream().mapToInt(Integer::intValue).toArray();
					
					// get the capacity 
				    int[] capacity = Query.getCapacity(conn, reg, date, labs);
					double r = checkcr(f, fr, capacity);
					total += r;
					n++;
					if (n == 1) {
						min = max = r;
					} else {
						if (r < min)
							min = r;
						if (r > max)
							max = r;
					}
					System.out.println("    (" + n + "). Improvement: " + r);

				}
				logger.info(" *** Average Improvement: {}. Mimimum: {}. Maximum {}", total / n, min, max);
			}
		} catch (SQLException e) {
			logger.error("Error getting q, qr. {}", e.getMessage());
			e.printStackTrace();
		}

		// return la.stream().mapToInt(Integer::intValue).toArray();

	}

	/**
	 * @param f
	 *            frequencies with appointment
	 * @param fr
	 *            frequencies with appointment, random
	 * @param c
	 *            capacity of each resources
	 * @return improvement between 0 and 100%
	 */
	public static double checkcr(int f[], int fr[], int []c) {

		int n = IntStream.of(f).sum();
		// System.out.println(n);
		int[] base = Tools.base(n, f);
		// Tools.printV(base);
		int[] vr = Tools.base(n, fr);
		// Tools.printV(vr);
		int nr = IntStream.of(fr).sum();
		if (n != nr)
			System.out.println("Error in N");
		/*
		int c[] = new int[nc];
		for (int i = 0; i < c.length; i++)
			c[i] = n / nc + 1;
			*/
		Population p = new Population(f, c);
		Appointment greedy = new Greedy();
		int vgreedy[] = greedy.generate(p);
		// Tools.printV(vgreedy);
		int ng = Tools.getN(vgreedy);
		if (n != ng)
			logger.error("Error in N heuristic");

		// check improvement
		RecursivePQ pq = new RecursivePQ(n);
		BigInteger ibase = pq.index(base);

		BigInteger ir = pq.index(vr);
		BigInteger ig = pq.index(vgreedy);
		BigInteger impgr = Tools.improvement(ig, ir, ibase, 1);
		// System.out.println("improvement: " + impgr.floatValue()/10.0);

		return impgr.floatValue() / 10.0;

	}

	public static int[] getCapacity(Connection conn, Integer reg, String date, List<Integer> labs) {
		int[] la = new int[labs.size()];
		PreparedStatement st = null;
		if (conn != null) {

			// make sure autocommit is off
			try {
				conn.setAutoCommit(false);

				st = conn.prepareStatement(Query.GETCAPACITY);
				// put the parameter
				st.setInt(1, reg);
				st.setString(2, date);

				// Turn use of the cursor on.
				st.setFetchSize(50);
				for (int i = 0; i < labs.size(); i++) {
					st.setInt(3, labs.get(i));
					ResultSet rs = st.executeQuery();
					if (rs.next()) {
						la[i] = rs.getInt(1);

					} else
						la[i] = 0;
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Error getting capacity {}. {}", Query.GETCAPACITY, e.getMessage());
				e.printStackTrace();
			}

		}
		return la;

	}

}
