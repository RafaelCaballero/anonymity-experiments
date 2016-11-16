package view;

import java.io.IOException;

import javax.swing.JFrame;

import conf.AppConf;
import control.Controller;
import model.connection.ConnectionData;

/**
 * This interface serves as a bridge between the view and the controller
 * 
 * @author rafa
 *
 */
public interface ViewInterface {

	static public final String CONNECT = "Connect";
	static public final String DISCONNECT = "Disconnect";
	static public final String ABOUT = "About";
	static public final String RESET = "Reset";
	static public final String HELP = "Help";
	static public final String LOGINEXIT = "LoginExit";
	static public final String LOGINLOGIN = "LoginLogin";

	/**
	 * @param control
	 *            : the controller
	 */
	void setController(Controller control);

	// show the window
	void start();

	// shows the contextual menu
	void showMenu();

	/**
	 * Asks for the connection data and returns the data.
	 * 
	 * @return A Properties object with the connection data or null if the user
	 *         canceled the operation.
	 */
	ConnectionData getConectionData();

	/**
	 * Opens the login form. Employed to connect to the database
	 */
	void openLoginForm();

	/**
	 * Closes the login form
	 */
	void closeLoginForm();

	/******************** errors *************************/
	void displayError(String msg, Exception e);

	/**
	 * Gets the login information
	 * 
	 * @return The application configuration modified after the login process
	 */
	AppConf getLoginConf();

	/**
	 * true if the login information must be saved
	 * 
	 * @return true if the configuration has been successfully saved, false
	 *         otherwise
	 * @throws IOException
	 *             An IO error occurred writing the configuration file
	 */
	boolean saveLogin(AppConf conf) throws IOException;

	/**
	 * Displays help
	 */
	void help();

	/**
	 * Display about dialog
	 */
	void about();

	/**
	 * @return The main application frame
	 */
	JFrame getFrame();
}
