package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;

import conf.AppConf;
import logback.AreaAppender;
import model.Model;
import model.connection.ConnectionData;
import view.ViewInterface;

/**
 * The controller in the model–view–controller design pattern. Detects the user
 * actions, informs the model and refreshes the view.
 * 
 * @author rafa
 *
 */
public class Controller implements ActionListener, MouseListener {
	private ViewInterface view;
	private Model mod;
	private boolean debugging;

	private static final Logger logger = AreaAppender.getLogger(Controller.class);

	public Controller(ViewInterface view, Model mod) {
		this.view = view;
		this.mod = mod;
		this.debugging = false;
		logger.info("Anonymity Application started. V2.0");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();

		switch (s) {
		case ViewInterface.CONNECT:
			// connect to a database with a username and passwd.
			view.openLoginForm();
			debugging = true;
			ConnectionData cd = view.getConectionData();
			// mod.start(cd);
			break;
		case ViewInterface.DISCONNECT:
			debugging = false;
			boolean isDisconnected = mod.disconnect();
			if (isDisconnected) {
				mod.disconnect();
				view.openLoginForm();
				debugging = true;

			}
			break;
		case ViewInterface.RESET:
			loginControl(true);
			break;
		case ViewInterface.HELP:
			view.help();
			break;
		case ViewInterface.ABOUT:
			view.about();
			break;
		case ViewInterface.LOGINEXIT:
			view.closeLoginForm();
			break;

		case ViewInterface.LOGINLOGIN:
			loginControl(true);
			break;
		default:
			logger.error("Unexpected menu Error! (getActionCommand: {})", s);
		}
	}

	private void loginControl(boolean save) {
		// close the login form
		view.closeLoginForm();
		if (save) {
			// save the login data to the configuration
			AppConf conf = view.getLoginConf();
			// save the configuration
			try {
				view.saveLogin(conf);
				logger.info("Saved configuration in file {} ", conf.getPath());
			} catch (IOException e1) {
				logger.error("Error saving configuration " + e1.getMessage());
				e1.printStackTrace();
			}
		}
		// ask for the connection data
		ConnectionData cData = view.getConectionData();
		if (cData != null) {
			try {
				boolean success = mod.connect(cData);
				if (!success) {
					view.displayError("Connection error: database empty or not found! ", null);
					logger.error("Database empty or not found!");

				}
			} catch (ClassNotFoundException e) {
				view.displayError("Driver not found ", e);
				logger.error("Driver not found!");
			} catch (SQLException ex) {
				view.displayError("Connection failed ", ex);
				logger.error("Connection failed. \n {} ", cData);
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// show contextual menu
		view.showMenu();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
