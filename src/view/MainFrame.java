package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;

import model.connection.ConnectionData;
import conf.AppConf;
import control.Controller;
import logback.AreaAppender;
import logback.TextFactory;

/**
 * Main application GUI
 * 
 * @author rafa
 *
 */
public class MainFrame extends JFrame implements ViewInterface {

	private static final Logger logger = AreaAppender.getLogger(MainFrame.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Color SIDECOLOR = new Color(230, 230, 255);;
	private static String appname = "Anonimity";
	private static String appdesc = "Anonimity Experiments";
	private static String title = "Anonimity";
	// tabbed pane
	JTabbedPane east;
	AppConf conf = new AppConf();
	// login form
	private LoginForm login = new LoginForm(null, true, conf);
	// about
	AboutDialog aboutDialog;

	/**
	 * Constructs the main window
	 * 
	 * @throws HeadlessException
	 */
	public MainFrame() throws HeadlessException {
		super();
		init();
	}

	private void init() {
		setTitle(title);

		initComponents();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {

			e.printStackTrace();
		}

		/*
		 * setLocationRelativeTo(null); setSize(640, 480);
		 * setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); pack();
		 * setVisible(true);
		 */

	}

	/**
	 * Prepare the main window
	 */
	private void initComponents() {
		// borderLayout as outer level
		JPanel panel = new JPanel(new BorderLayout());
		// panel.setBorder((Border) new EmptyBorder(new Insets(0, 5, 5, 5)));

		// menu
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");

		menubar.add(file);

		JPanel top = topPanel();
		panel.add(top, BorderLayout.NORTH);
		JTabbedPane pane = mainPanel();
		panel.add(pane, BorderLayout.CENTER);

		add(panel);

	}

	/**
	 * Tabbed panel at the east
	 * 
	 * @return
	 */
	private JTabbedPane mainPanel() {

		east = new JTabbedPane();
		// ImageIcon logicon = createImageIcon("/resources/loginicon.png");
		ImageIcon logicon = null;
		JTextPane text = new JTextPane();
		JScrollPane stext = new JScrollPane(text);

		east.addTab("Anonimity - Log", logicon, stext, "Logging information");
		TextFactory.setText(text);

		return east;

	}

	private JPanel topPanel() {
		JPanel top = new JPanel(new BorderLayout());
		top.setBorder((Border) new EmptyBorder(new Insets(5, 5, 5, 5)));

		JPanel topLeft = new JPanel(new BorderLayout());
		topLeft.setBorder((Border) new EmptyBorder(new Insets(3, 3, 3, 3)));

		JPanel topLeftCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		Color cTop = Color.WHITE; /* new Color(30, 50, 100); */
		top.setBackground(cTop);
		topLeft.setBackground(cTop);
		topLeftCenter.setBackground(cTop);
		topRight.setBackground(cTop);
		JLabel labelName = new JLabel(appname);
		JLabel labelDesc = new JLabel(" -  " + appdesc);

		labelName.setBackground(new Color(30, 50, 100));
		labelName.setForeground(new Color(00, 250, 100));
		labelDesc.setBackground(new Color(30, 50, 100));
		labelDesc.setForeground(new Color(255, 255, 255));

		Font fontappname = new Font("SansSerif", Font.ITALIC + Font.BOLD, 20);
		Font fontappdesc = new Font("Serif", Font.ITALIC + Font.BOLD, 16);
		labelName.setFont(fontappname);
		labelDesc.setFont(fontappdesc);
		topLeftCenter.add(labelName);
		topLeftCenter.add(labelDesc);

		// ImageHopla image = new ImageHopla();
		// topLeft.add(image,BorderLayout.WEST);
		// topLeft.add(topLeftCenter, BorderLayout.CENTER);
		// topRight.add(imageMenu);

		top.add(topLeft, BorderLayout.WEST);
		top.add(topRight, BorderLayout.EAST);

		return top;
	}

	@Override
	public void setController(Controller control) {
		// display the menu associated to the icon
		// imageMenu.addMouseListener(control);
		// menu.setActionListener(control);
		login.setActionListener(control);

	}

	@Override
	public void start() {
		setLocationRelativeTo(null);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 480);
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		setVisible(true);
		conf = new AppConf();
		login.setVisible(true);

	}

	@Override
	public void showMenu() {
		// menu associated to the icon
		// imageMenu.showMenu();

	}

	@Override
	public ConnectionData getConectionData() {
		Properties props = new Properties();
		props.setProperty("user", login.getUser());
		props.setProperty("password", login.getPass());
		props.setProperty("ssl", login.getSsl());
		String url = login.getUrl();
		String database = login.getDatabase();
		ConnectionData cd = new ConnectionData(props, url, database);
		return cd;
	}

	@Override
	public void closeLoginForm() {
		login.setVisible(false);

	}

	@Override
	public void openLoginForm() {
		login.setVisible(true);

	}

	@Override
	public void displayError(String msg, Exception e) {
		JOptionPane.showMessageDialog(null, "Error: " + msg + (e == null ? "" : e.getMessage()), "Error",
				JOptionPane.ERROR_MESSAGE);

	}

	@Override
	public AppConf getLoginConf() {
		AppConf result = login.getConfig();

		return result;

	}

	@Override
	public boolean saveLogin(AppConf conf) throws IOException {
		conf.store();
		return true;

	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = MainFrame.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/*
	 * private void initHelp() { if (helpDialog == null) { HelpDetailedPanel hd
	 * = new HelpDetailedPanel(new HelpFile().toString()); //hd.setSize(640,
	 * 400); JScrollPane scrollPane = new JScrollPane(hd); helpDialog = new
	 * JDialog(this, "SBuggy - Help", false); helpDialog.setResizable(true);
	 * helpDialog.getContentPane().add(scrollPane); helpDialog.setSize(640,
	 * 400); helpDialog.pack(); Dimension Size =
	 * Toolkit.getDefaultToolkit().getScreenSize(); helpDialog.setLocation(new
	 * Double((Size.getWidth() / 2) - (helpDialog.getWidth() / 2)).intValue(),
	 * new Double((Size.getHeight() / 2) - (helpDialog.getHeight() /
	 * 2)).intValue()); }
	 * 
	 * }
	 */
	@Override
	public void help() {
		// initHelp();
		// helpDialog.setVisible(true);
		// new HelpFile();
		String url = conf.getProperty("helpURL");
		try {
			openWebpage(new URL(url).toURI());
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void about() {

		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(this);
		}
		aboutDialog.setVisible(true);
	}

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public JFrame getFrame() {

		return this;
	}

}
