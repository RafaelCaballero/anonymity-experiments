package model;

import java.util.List;
import java.util.stream.IntStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;

import anonymityVectors.appointments.Appointment;
import anonymityVectors.appointments.Greedy;
import anonymityVectors.size.RecursivePQ;
import anonymityVectors.tools.Population;
import anonymityVectors.tools.Tools;
import logback.AreaAppender;
import model.connection.ConnectionData;
import model.connection.SQLConnector;
import model.queries.Query;

public class Model {

	private static final Logger logger = AreaAppender.getLogger(Model.class);

	/**
	 * Database connection
	 */
	SQLConnector connector = null;

	/**
	 * Starts a database connection
	 * 
	 * @param cData
	 * 
	 * @return true if the connection was possible, false otherwise
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public boolean connect(ConnectionData cData) throws ClassNotFoundException, SQLException {
		boolean result = false;

		try {
			connector = new SQLConnector();
			connector.connect(cData);
			start();
			result = true;
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Error during connection!" + e.getStackTrace());
			// e.printStackTrace();
		}
		return result;
	}

	/**
	 * Ends a database connection
	 * 
	 * @return true if the disconnection was possible, false otherwise
	 */
	public boolean disconnect() {
		boolean result;
		if (connector != null) {
			connector.disconnect();
			connector = null;
			result = true;
		} else
			result = true;

		return result;
	}

	/**
	 * Reset all the data
	 */
	public void reset() {

	}

	/**
	 * Help
	 */
	public void Help() {

	}

	public boolean isConnected() {
		return connector != null;
	}

	public void start() {
		// logger.info("Entrando en Start");
		// first retrieve the number of regions
		Connection con = connector.getConnection();
		List<Integer> regs = Query.getRegions(con);
		logger.info("{} regions detected", regs.size());
		for (Integer reg : regs) {
			logger.info("**** Region {} ***** ", reg);
			List<Integer> labs = Query.getLabs(con, reg);
			logger.info("     Num.Labs {}", labs.size());
			List<String> dates = Query.getScreeningDates(con, reg);
			logger.info("     Num.Dates {}", dates.size());
			Query.getImprovement(con, reg, dates, labs);

		}

	}

}
