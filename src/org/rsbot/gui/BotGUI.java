package org.rsbot.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.rsbot.accessors.Client;
import org.rsbot.bot.Bot;
import org.rsbot.bot.input.CanvasWrapper;
import org.rsbot.bot.input.Listener;
import org.rsbot.event.EventMulticaster;
import org.rsbot.event.impl.CharacterMovedLogger;
import org.rsbot.event.impl.DrawBoundaries;
import org.rsbot.event.impl.DrawInventory;
import org.rsbot.event.impl.DrawItems;
import org.rsbot.event.impl.DrawModel;
import org.rsbot.event.impl.DrawMouse;
import org.rsbot.event.impl.DrawNPCs;
import org.rsbot.event.impl.DrawObjects;
import org.rsbot.event.impl.DrawPlayers;
import org.rsbot.event.impl.DrawSettings;
import org.rsbot.event.impl.ServerMessageLogger;
import org.rsbot.event.impl.TActualMousePosition;
import org.rsbot.event.impl.TAnimation;
import org.rsbot.event.impl.TCamera;
import org.rsbot.event.impl.TFPS;
import org.rsbot.event.impl.TFloorHeight;
import org.rsbot.event.impl.TLoginIndex;
import org.rsbot.event.impl.TMenuActions;
import org.rsbot.event.impl.TMousePosition;
import org.rsbot.event.impl.TPlayerPosition;
import org.rsbot.event.impl.TTab;
import org.rsbot.event.impl.TUserInputAllowed;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptHandler;
import org.rsbot.script.ScriptManifest;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.ScreenshotUtil;
import org.rsbot.util.UpdateUtil;
import org.rsbot.util.GlobalConfiguration.OperatingSystem;
import org.rsbot.util.logging.TextAreaLogHandler;

public class BotGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4411033752001988794L;
	private static final Logger log = Logger.getLogger(BotGUI.class.getName());

	private static boolean loggedIn = false;
	public static String cookies;

	static {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	}

	private Bot bot;

	protected Map<String, JCheckBoxMenuItem> commandCheckMap = new HashMap<String, JCheckBoxMenuItem>();
	protected Map<String, JMenuItem> commandMenuItem = new HashMap<String, JMenuItem>();

	private final EventMulticaster eventMulticaster = new EventMulticaster();
	protected Map<String, EventListener> listeners = new TreeMap<String, EventListener>();
	private File menuSetting = null;
	private JScrollPane textScroll;


	private JToolBar toolBar;
	private JButton userInputButton;
	private JButton userPauseButton;

	private final JMenuItem pauseResumeScript;
	private final Dimension minsize;

	public BotGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		initializeGUI();
		pauseResumeScript = commandMenuItem.get("Pause Script");
		pauseResumeScript.setEnabled(false);
		commandMenuItem.get("Login").setVisible(false);

		setTitle();
		setLocationRelativeTo(getOwner());

		try {
			setIconImage(Toolkit.getDefaultToolkit().getImage(GlobalConfiguration.RUNNING_FROM_JAR ? getClass().getResource(GlobalConfiguration.Paths.Resources.ICON) : new File(GlobalConfiguration.Paths.ICON).toURI().toURL()));
		} catch (final Exception e) {
			e.printStackTrace();
		}

		minsize = getSize();
		setMinimumSize(minsize);
		setResizable(true);
		setVisible(true);
		BotGUI.log.info("Welcome to " + GlobalConfiguration.NAME + "!");
		bot.startClient();
		
		if (GlobalConfiguration.RUNNING_FROM_JAR) {
			final BotGUI parent = this;
			new Thread(new Runnable() {
				public void run() {
					final UpdateUtil updater = new UpdateUtil(parent);
					updater.checkUpdate(false);
				}
			}).start();
		}
	}

	public void actionPerformed(final ActionEvent e) {
		final String action = e.getActionCommand();
		final int z = action.indexOf('.');
		final String[] command = new String[2];
		if (z == -1) {
			command[0] = action;
			command[1] = "";
		} else {
			command[0] = action.substring(0, z);
			command[1] = action.substring(z + 1);
		}
		if (command[0].equals("File")) {
			final ScriptHandler sh = Bot.getScriptHandler();
			if ("Run Script".equals(command[1])) {
				showRunScriptSelector();
				final Map<Integer, Script> running = sh.getRunningScripts();
				if (running.size() > 0) {
					pauseResumeScript.setText("Pause Script");
					pauseResumeScript.setEnabled(true);
					updatePauseButton("Pause Script", GlobalConfiguration.Paths.Resources.ICON_PAUSE, GlobalConfiguration.Paths.ICON_PAUSE);
				} else {
					pauseResumeScript.setEnabled(false);
					updatePauseButton("Run Script", GlobalConfiguration.Paths.Resources.ICON_PLAY, GlobalConfiguration.Paths.ICON_PLAY);
				}
			} else if ("Stop Script".equals(command[1])) {
				showStopScriptSelector();
			} else if ("Pause Script".equals(command[1]) || "Resume Script".equals(command[1])) {
				final Map<Integer, Script> running = sh.getRunningScripts();
				if (running.size() > 0) {
					final int id = running.keySet().iterator().next();
					final Script s = running.get(id);
					final ScriptManifest prop = s.getClass().getAnnotation(ScriptManifest.class);
					final String name = prop.name();
					if (running.get(id).isPaused) {
						sh.pauseScript(id);
						BotGUI.log.info(name + " has resumed!");
					} else {
						sh.pauseScript(id);
						BotGUI.log.info(name + " has been paused!");
					}
					if (running.get(id).isPaused) {
						pauseResumeScript.setText("Resume Script");
						updatePauseButton("Resume Script", GlobalConfiguration.Paths.Resources.ICON_PLAY, GlobalConfiguration.Paths.ICON_PLAY);
					} else {
						pauseResumeScript.setText("Pause Script");
						updatePauseButton("Pause Script", GlobalConfiguration.Paths.Resources.ICON_PAUSE, GlobalConfiguration.Paths.ICON_PAUSE);
					}
				}
			} else if ("Save Screenshot".equals(command[1])) {
				ScreenshotUtil.takeScreenshot(new Methods().isLoggedIn());
			} else if ("Save Screenshot [No censor]".equals(command[1])) {
				ScreenshotUtil.takeScreenshot(false);
			} else if ("Login".equals(command[1])) {
				if (BotGUI.loggedIn) {
					BotGUI.cookies = "";
					BotGUI.loggedIn = false;
					commandMenuItem.get(command[1]).setText("Login");
					BotGUI.log.info("Logged out.");
				} else {
					promptAndLogin();
				}
			} else if ("Exit".equals(command[1])) {
				System.exit(0);
			}
		} else if (command[0].equals("Edit")) {
			if ("Accounts".equals(command[1])) {
				AccountManager.getInstance().showGUI();
			} else if ("Block User Input".equals(command[1])) {
				Listener.blocked = !Listener.blocked;
				try {
					userInputButton.setIcon(new ImageIcon(Listener.blocked ? (GlobalConfiguration.RUNNING_FROM_JAR ? getClass().getResource(GlobalConfiguration.Paths.Resources.ICON_DELETE) : new File(GlobalConfiguration.Paths.ICON_DELETE).toURI().toURL()) : GlobalConfiguration.RUNNING_FROM_JAR ? getClass().getResource(GlobalConfiguration.Paths.Resources.ICON_TICK) : new File(GlobalConfiguration.Paths.ICON_TICK).toURI().toURL()));
				} catch (final MalformedURLException e1) {
					e1.printStackTrace();
				}
			} else if ("Use Less CPU".equals(command[1])) {
				CanvasWrapper.slowGraphics = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			} else if ("Disable Randoms".equals(command[1])) {
				Bot.disableRandoms = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			} else if ("Disable Auto Login".equals(command[1])) {
				Bot.disableAutoLogin = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			} else if ("Disable Break Handler".equals(command[1])) {
				Bot.disableBreakHandler = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			}
		} else if (command[0].equals("View")) {
			final boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			if ("All Debugging".equals(command[1])) {
				for (final String key : listeners.keySet()) {
					final EventListener el = listeners.get(key);
					final boolean wasSelected = commandCheckMap.get(key).isSelected();
					commandCheckMap.get(key).setSelected(selected);
					if (selected) {
						if (!wasSelected) {
							eventMulticaster.addListener(el);
						}
					} else {
						if (wasSelected) {
							eventMulticaster.removeListener(el);
						}
					}
				}
				commandCheckMap.get("All Text Debugging").setSelected(selected);
				commandCheckMap.get("All Paint Debugging").setSelected(selected);
			} else if ("Hide Toolbar".equals(command[1])) {
				toggleViewState(toolBar, selected);
			} else if ("Hide Log Window".equals(command[1])) {
				toggleViewState(textScroll, selected);
			} else if ("All Text Debugging".equals(command[1])) {
				if (!selected) {
					commandCheckMap.get("All Debugging").setSelected(false);
				}
				for (final String key : listeners.keySet()) {
					final EventListener el = listeners.get(key);
					if (el instanceof TextPaintListener) {
						final boolean wasSelected = commandCheckMap.get(key).isSelected();
						commandCheckMap.get(key).setSelected(selected);
						if (selected) {
							if (!wasSelected) {
								eventMulticaster.addListener(el);
							}
						} else {
							if (wasSelected) {
								eventMulticaster.removeListener(el);
							}
						}
					}
				}
			} else if ("All Paint Debugging".equals(command[1])) {
				if (!selected) {
					commandCheckMap.get("All Debugging").setSelected(false);
				}
				for (final String key : listeners.keySet()) {
					final EventListener el = listeners.get(key);
					if (el instanceof PaintListener) {
						final boolean wasSelected = commandCheckMap.get(key).isSelected();
						commandCheckMap.get(key).setSelected(selected);
						if (selected) {
							if (!wasSelected) {
								eventMulticaster.addListener(el);
							}
						} else {
							if (wasSelected) {
								eventMulticaster.removeListener(el);
							}
						}
					}
				}
			} else {
				final EventListener el = listeners.get(command[1]);
				commandCheckMap.get(command[1]).setSelected(selected);
				if (selected) {
					eventMulticaster.addListener(el);
				} else {
					commandCheckMap.get("All Text Debugging").setSelected(false);
					commandCheckMap.get("All Paint Debugging").setSelected(false);
					commandCheckMap.get("All Debugging").setSelected(false);
					eventMulticaster.removeListener(el);
				}
			}
		} else if (command[0].equals("Help")) {
			if ("Site".equals(command[1])) {
				openURL(GlobalConfiguration.Paths.URLs.SITE);
			} else if("Project".equals(command[1])) {
				openURL(GlobalConfiguration.Paths.URLs.PROJECT);
			} else if ("About".equals(command[1])) {
				JOptionPane.showMessageDialog(this, new String[] { "An open source bot.", "Visit " + GlobalConfiguration.Paths.URLs.SITE + "/ for more information." }, "About", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private JMenuBar constructMenu() {
		final List<String> debugItems = new ArrayList<String>();
		debugItems.add("All Debugging");
		debugItems.add("Hide Toolbar");
		debugItems.add("Hide Log Window");
		debugItems.add("-");
		debugItems.add("All Paint Debugging");
		for (final String key : listeners.keySet()) {
			final EventListener el = listeners.get(key);
			if (el instanceof PaintListener) {
				debugItems.add(key);
			}
		}
		debugItems.add("-");
		debugItems.add("All Text Debugging");
		for (final String key : listeners.keySet()) {
			final EventListener el = listeners.get(key);
			if (el instanceof TextPaintListener) {
				debugItems.add(key);
			}
		}
		debugItems.add("-");
		for (final String key : listeners.keySet()) {
			final EventListener el = listeners.get(key);
			if (!(el instanceof TextPaintListener) && !(el instanceof PaintListener)) {
				debugItems.add(key);
			}
		}
		for (final ListIterator<String> it = debugItems.listIterator(); it.hasNext();) {
			final String s = it.next();
			if (s.equals("-")) {
				continue;
			}
			it.set("ToggleF " + s);
		}

		final String[] titles = new String[] { "File", "Edit", "View", "Help" };
		final String[][] elements = new String[][] { { "Run Script", "Stop Script", "Pause Script", "-", "Save Screenshot", "Save Screenshot [No censor]", "Login", "-", "Exit" }, { "Accounts", "-", "ToggleF Block User Input", "ToggleF Use Less CPU", "-", "ToggleF Disable Randoms", "ToggleF Disable Auto Login", "ToggleF Disable Break Handler" }, debugItems.toArray(new String[debugItems.size()]), { "Site", "Project", "About" } };
		final JMenuBar bar = new JMenuBar();
		for (int i = 0; i < titles.length; i++) {
			final String title = titles[i];
			final JMenu menu = new JMenu(title);
			final String[] elems = elements[i];
			for (String e : elems) {
				if (e.equals("-")) {
					menu.add(new JSeparator());
				} else {
					JMenuItem jmi;
					if (e.startsWith("Toggle")) {
						e = e.substring("Toggle".length());
						final char state = e.charAt(0);
						e = e.substring(2);
						jmi = new JCheckBoxMenuItem(e);
						if ((state == 't') || (state == 'T')) {
							jmi.setSelected(true);
						}
						commandCheckMap.put(e, (JCheckBoxMenuItem) jmi);
					} else {
						jmi = new JMenuItem(e);
						commandMenuItem.put(e, jmi);
					}
					jmi.addActionListener(this);
					jmi.setActionCommand(title + "." + e);
					menu.add(jmi);
				}
			}
			bar.add(menu);
		}
		return bar;
	}

	private void initializeGUI() {
		bot = new Bot();

		Bot.getEventManager().getMulticaster().addListener(eventMulticaster);
		Listener.blocked = false;
		registerListeners();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				if (safeClose()) {
					dispose();
					shutdown();
				}
			}
		});

		final JMenuBar bar = constructMenu();
		setJMenuBar(bar);

		try {
			userInputButton = new JButton("User Input", new ImageIcon(GlobalConfiguration.RUNNING_FROM_JAR ? getClass().getResource(GlobalConfiguration.Paths.Resources.ICON_TICK) : new File(GlobalConfiguration.Paths.ICON_TICK).toURI().toURL()));
		} catch (final MalformedURLException e1) {
			e1.printStackTrace();
		}
		userInputButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				commandCheckMap.get("Block User Input").doClick();
			}
		});
		userInputButton.setFocusable(false);

		try {
			userPauseButton = new JButton("Run Script", new ImageIcon(GlobalConfiguration.RUNNING_FROM_JAR ? getClass().getResource(GlobalConfiguration.Paths.Resources.ICON_PLAY) : new File(GlobalConfiguration.Paths.ICON_PLAY).toURI().toURL()));
		} catch (final MalformedURLException e1) {
			e1.printStackTrace();
		}
		userPauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				final ScriptHandler sh = Bot.getScriptHandler();
				final Map<Integer, Script> running = sh.getRunningScripts();
				if (running.size() >= 1) {
					pauseResumeScript.doClick();
				}
				if (running.size() == 0) {
					commandMenuItem.get("Run Script").doClick();
				}

			}
		});
		userPauseButton.setFocusable(false);

		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(userPauseButton);
		toolBar.add(userInputButton);

		// applet
		final Dimension dim = new Dimension(765, 503);
		bot.getLoader().setPreferredSize(dim);

		// log
		textScroll = new JScrollPane(TextAreaLogHandler.textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textScroll.setBorder(null);
		textScroll.setPreferredSize(new Dimension(dim.width, 120));
		textScroll.setVisible(true);

		add(toolBar, BorderLayout.NORTH);
		add(bot.getLoader(), BorderLayout.CENTER);
		add(textScroll, BorderLayout.SOUTH);

		pack();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				initListeners();
			}
		});
	}

	protected void initListeners() {
		if (menuSetting == null) {
			menuSetting = new File(GlobalConfiguration.Paths.getMenuCache());
		}
		if (!menuSetting.exists()) {
			try {
				if (menuSetting.createNewFile()) {
					BotGUI.log.warning("Failed to create settings file.");
				}
			} catch (final IOException e) {
				BotGUI.log.warning("Failed to create settings file.");
			}
		} else {
			try {
				final BufferedReader br = new BufferedReader(new FileReader(menuSetting));
				String s;
				while ((s = br.readLine()) != null) {
					final JCheckBoxMenuItem item = commandCheckMap.get(s);
					if (item != null) {
						item.doClick();
					}
				}
			} catch (final IOException e) {
				BotGUI.log.warning("Unable to read settings.");
			}
		}

	}

	private boolean isLoggedIn() {
		final Client client = Bot.getClient();
		final int index = client == null ? -1 : client.getLoginIndex();
		return (index == 30) || (index == 25);
	}

	/**
	 * Logs into the Forums using the given username and password
	 * 
	 * @param username
	 *            Name of forum account
	 * @param password
	 *            Password of forum account
	 * @return True if logged in, False otherwise
	 */
	private boolean login(final String username, final String password) {
		final String url = /*GlobalConfiguration.Paths.URLs.FORUMS +*/ "login.php";
		try {
			final URL login = new URL(url);
			final HttpURLConnection connect = (HttpURLConnection) login.openConnection();
			connect.setRequestMethod("POST");
			connect.setDoOutput(true);
			connect.setDoInput(true);
			connect.setUseCaches(false);
			connect.setAllowUserInteraction(false);
			final String write = "do=login&vb_login_username=" + username + "&vb_login_password=" + password + "&cookieuser=1";
			final Writer writer = new OutputStreamWriter(connect.getOutputStream(), "UTF-8");
			writer.write(write);
			writer.flush();
			writer.close();
			String headerName;
			for (int i = 1; (headerName = connect.getHeaderFieldKey(i)) != null; i++) {
				if (headerName.equals("Set-Cookie")) {
					String cookie = connect.getHeaderField(i);
					cookie = cookie.substring(0, cookie.indexOf(";"));
					BotGUI.cookies += cookie + "; ";
				}
			}
			BotGUI.cookies = BotGUI.cookies.substring(0, BotGUI.cookies.length() - 3);
			final BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			String temp;
			while ((temp = in.readLine()) != null) {
				if (temp.toLowerCase().contains(username.toLowerCase())) {
					connect.disconnect();
					in.close();
					return true;
				}
				if (temp.toLowerCase().contains("register")) {
					connect.disconnect();
					in.close();
					return false;
				}
			}
			in.close();
			connect.disconnect();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void openURL(final String url) {
		final OperatingSystem os = GlobalConfiguration.getCurrentOperatingSystem();
		try {
			if (os == OperatingSystem.MAC) {
				final Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				final Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
				openURL.invoke(null, url);
			} else if (os == OperatingSystem.WINDOWS) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else { // assume Unix or Linux
				final String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; (count < browsers.length) && (browser == null); count++) {
					if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null)
					throw new Exception("Could not find web browser");
				else {
					Runtime.getRuntime().exec(new String[] { browser, url });
				}
			}
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(this, "Error Opening Browser", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void promptAndLogin() {
		final JDialog frame = new JDialog(this, "Forum Login", true);
		final JPanel pane = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		final JButton login = new JButton("Login");
		final JTextField name = new JTextField(8);
		final JPasswordField pass = new JPasswordField(8);
		login.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				frame.dispose();
				if (login(name.getText(), new String(pass.getPassword()))) {
					BotGUI.loggedIn = true;
					commandMenuItem.get("Login").setText("Logout");
					BotGUI.log.info("Successfully logged in as " + name.getText() + ".");
				} else {
					BotGUI.loggedIn = false;
					BotGUI.log.warning("Could not log in as \"" + name.getText() + "\".");
				}
			}
		});
		pass.setEchoChar('*');
		pane.add(new JLabel("Username: "), c);
		c.gridx = 1;
		pane.add(name, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(new JLabel("Password: "), c);
		c.gridx = 1;
		pane.add(pass, c);
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 2;
		pane.add(login, c);
		frame.add(pane);
		frame.pack();
		frame.setLocationRelativeTo(this);
		frame.setVisible(true);
	}

	/**
	 * Registers a listener.
	 * 
	 * @param name
	 *            Debug Menu Name
	 * @param el
	 *            The Event Listener
	 * */
	protected void registerListener(final String name, final EventListener el) {
		listeners.put(name, el);
	}

	/**
	 * Registers the default listeners so they can be displayed in the debug
	 * menu.
	 * */
	protected void registerListeners() {
		// Text
		registerListener("Login Index", TLoginIndex.inst);
		registerListener("Current Tab", TTab.inst);
		registerListener("Camera", TCamera.inst);
		registerListener("Animation", TAnimation.inst);
		registerListener("Floor Height", TFloorHeight.inst);
		registerListener("Player Position", TPlayerPosition.inst);
		registerListener("Mouse Position", TMousePosition.inst);
		registerListener("Actual Mouse Position", TActualMousePosition.inst);
		registerListener("User Input Allowed", TUserInputAllowed.inst);
		registerListener("Menu Actions", TMenuActions.inst);
		registerListener("FPS", TFPS.inst);

		// Paint
		registerListener("Players", DrawPlayers.inst);
		registerListener("NPCs", DrawNPCs.inst);
		registerListener("Objects", DrawObjects.inst);
		registerListener("Mouse", DrawMouse.inst);
		registerListener("Inventory", DrawInventory.inst);
		// registerListener("Ground", DrawGround.inst);
		registerListener("Items", DrawItems.inst);
		registerListener("Calc Test", DrawBoundaries.inst);
		registerListener("Current Model", DrawModel.inst);
		registerListener("Settings", DrawSettings.inst);

		// Other
		registerListener("Character Moved (LAG)", CharacterMovedLogger.inst);
		registerListener("Server Messages", ServerMessageLogger.inst);
	}

	public void runScript(final String name, final Script script, final Map<String, String> args) {
		Bot.setAccount(name);
		setTitle();
		Bot.getScriptHandler().runScript(script, args);
		if (!Listener.blocked) {
			commandCheckMap.get("Block User Input").doClick();
		}
	}

	private boolean safeClose() {
		boolean pass = true;
		if (isLoggedIn()) {
			final int result = JOptionPane.showConfirmDialog(this, "Are you sure you would like to quit?", "Close", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
			pass = result == JOptionPane.YES_OPTION;
		}
		return pass;
	}

	private void setTitle() {
		final String name = Bot.getAccountName();
		setTitle((name.isEmpty() ? "" : name + " - ") + GlobalConfiguration.NAME + " v" + ((float)GlobalConfiguration.getVersion() / 100));
	}

	private void showRunScriptSelector() {
		if (AccountManager.getAccountNames().length == 0) {
			JOptionPane.showMessageDialog(this, "No accounts found! Please create one before using the bot.");
			AccountManager.getInstance().showGUI();
		} else {
			final ScriptHandler sh = Bot.getScriptHandler();
			final Map<Integer, Script> running = sh.getRunningScripts();
			if (running.size() > 0) {
				JOptionPane.showMessageDialog(this, "A script is already running.", "Script", JOptionPane.ERROR_MESSAGE);
			} else {
				ScriptSelector.getInstance(this).showSelector();
			}
		}
	}

	private void showStopScriptSelector() {
		final ScriptHandler sh = Bot.getScriptHandler();
		final Map<Integer, Script> running = sh.getRunningScripts();
		if (running.size() > 0) {
			final int id = running.keySet().iterator().next();
			final Script s = running.get(id);
			final ScriptManifest prop = s.getClass().getAnnotation(ScriptManifest.class);
			final int result = JOptionPane.showConfirmDialog(this, "Would you like to stop the script " + prop.name() + "?", "Script", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				sh.stopScript(id);
				
				pauseResumeScript.setText("Pause Script");
				pauseResumeScript.setEnabled(false);
				updatePauseButton("Run Script", GlobalConfiguration.Paths.Resources.ICON_PLAY, GlobalConfiguration.Paths.ICON_PLAY);
			}
		}
	}

	public void shutdown() {
		try {
			final BufferedWriter bw = new BufferedWriter(new FileWriter(menuSetting));
			boolean f = true;
			for (final JCheckBoxMenuItem item : commandCheckMap.values()) {
				if (item == null) {
					continue;
				}

				if (item.isSelected() && !item.getText().startsWith("All")) {
					if (!f) {
						bw.newLine();
					}
					f = false;

					bw.write(item.getText());
				}
			}
			bw.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		BotGUI.log.info("Closing");
		System.exit(0);
	}

	private void toggleViewState(final Component component, final boolean visible) {
		final Dimension size = minsize;
		size.height += component.getSize().height * (visible ? -1 : 1);
		component.setVisible(!visible);
		setMinimumSize(size);
		if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
			pack();
		}
	}

	public void updatePauseButton(final String text, final String pathResource, final String pathFile) {
		userPauseButton.setText(text);
		try {
			userPauseButton.setIcon(new ImageIcon(GlobalConfiguration.RUNNING_FROM_JAR ? getClass().getResource(pathResource) : new File(pathFile).toURI().toURL()));
		} catch (final MalformedURLException e1) {
			e1.printStackTrace();
		}
	}

}