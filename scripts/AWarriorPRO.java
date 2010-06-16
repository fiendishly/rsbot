import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Antic, XScripting Inc" }, category = "Combat", name = "AWarriorPro", version = 1.6, description = "  <html>"
		+ "<body>" + "Click OK to start the GUI." + "</body>" + "</html>"

)
@SuppressWarnings( { "unused", "serial" })
public class AWarriorPRO extends Script implements PaintListener,
		ServerMessageListener {

	xAntiBan antiban;
	Thread t;
	private final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	public int[] lootarray = { 213, 205, 217, 215, 211, 207, 209, 2485, 1440 };
	public int[] prices = new int[lootarray.length];
	public int[] strpot = { 119, 117, 115, 113 };
	public int[] attpot = { 125, 123, 121, 2428 };
	public int[] pots = { 125, 123, 121, 2428, 119, 117, 115, 113 };
	public int looted;
	public int foodid = 0;
	public URLConnection url = null;
	public BufferedReader in = null;
	public BufferedWriter out = null;
	public int arrowid;
	public String arrowname;
	public int ItemPrice;
	public int price;
	public int MoneyGained;
	public long startTime;
	public int currentstrlvl;
	public int currentattlvl;
	public long stopTime;
	public int startexp;
	public int aftermin;
	public int str;
	public int att;
	public int range;
	public int hp;
	public int def;
	String specat;
	String specialprocent = RSInterface.getInterface(884).getChild(8).getText();
	public boolean enablebreak;
	public String XAmount;
	public int RANGEstartexp;
	public int STRENGTHstartexp;
	public int DEFENSEstartexp;
	public int HITPOINTSstartexp;
	int withdraw = 0;
	String[] arraynames = { "Grimy kwuarm", "Grimy harralander",
			"Grimy dwarf weed", "Grimy cadantine", "Grimy avantoe",
			"Grimy ranarr", "Grimy irit", "Grimy lantadyme", "Earth talisman" };
	RSTile togoats = new RSTile(3292, 3170);
	RSTile tobank = new RSTile(3269, 3166);
	RSTile toMen = new RSTile(3104, 3508);
	RSTile toMen2 = new RSTile(3095, 3509);
	RSTile toBank = new RSTile(3097, 3496);
	GoatSlayerGUI gui;
	public boolean guiWait = true, guiExit;
	public boolean antiban2;
	public boolean arrowpickup;
	public boolean special;
	public boolean strpotion;
	public boolean attpotion;
	public boolean bones;
	public boolean bury;
	public boolean logout1;
	public boolean spec1;
	public boolean spec2;
	public boolean logout2;
	public boolean killopt1;
	public boolean nofood;

	public boolean onStart(Map<String, String> args) {

		URLConnection url = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		// Ask the user if they'd like to check for an update...
		if (JOptionPane
				.showConfirmDialog(
						null,
						"Would you like to check for updates?\nPlease Note this requires an internet connection and the script will write files to your harddrive!") == 0) { // If
			// they
			// would,
			// continue
			try {
				// Open the version text file
				url = new URL("http://binaryx.nl/antic/AWarriorVERSION.txt")
						.openConnection();
				// Create an input stream for it
				in = new BufferedReader(new InputStreamReader(url
						.getInputStream()));
				// Check if the current version is outdated
				if (Double.parseDouble(in.readLine()) > getVersion()) {
					// If it is, check if the user would like to update.
					if (JOptionPane.showConfirmDialog(null,
							"Update found. Do you want to update?") == 0) {
						// If so, allow the user to choose the file to be
						// updated.
						JOptionPane
								.showMessageDialog(null,
										"Please choose 'AWarriorPRO.java' in your scripts folder and hit 'Open'");
						JFileChooser fc = new JFileChooser();
						// Make sure "Open" was clicked.
						if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							// If so, set up the URL for the .java file and set
							// up the IO.
							url = new URL(
									"http://binaryx.nl/antic/AWarriorPRO.java")
									.openConnection();
							in = new BufferedReader(new InputStreamReader(url
									.getInputStream()));
							out = new BufferedWriter(new FileWriter(fc
									.getSelectedFile().getPath()));
							String inp;
							/*
							 * Until we reach the end of the file, write the
							 * next line in the file and add a new line. Then
							 * flush the buffer to ensure we lose no data in the
							 * process.
							 */
							while ((inp = in.readLine()) != null) {
								out.write(inp);
								out.newLine();
								out.flush();
							}
							// Notify the user that the script has been updated,
							// and a recompile and reload is needed.
							JOptionPane
									.showMessageDialog(null,
											"Script successfully downloaded. Please recompile and reload your scripts!");
							return false;
						} else
							log("Update canceled");
					} else
						log("Update canceled");
				} else
					JOptionPane.showMessageDialog(null,
							"You have the latest version of AWarrior. :)"); // User
				// has
				// the
				// latest
				// version.
				// Tell
				// them!
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Problem getting version.");
				return false; // Return false if there was a problem
			}
		}

		gui = new GoatSlayerGUI();
		gui.setVisible(true);
		while (guiWait) {
			wait(100);
		}

		currentstrlvl = skills.getCurrentSkillLevel(STAT_STRENGTH);
		currentattlvl = skills.getCurrentSkillLevel(STAT_ATTACK);
		startTime = System.currentTimeMillis();
		if (!isRetaliateEnabled()) {
			if (getCurrentTab() != TAB_ATTACK) {
				openTab(TAB_ATTACK);
			}
			clickMouse(random(570, 700), random(360, 400), true);
			wait(random(1000, 1200));
		}
		wait(random(800, 900));
		if (!isRetaliateEnabled()) {
			if (getCurrentTab() != TAB_ATTACK) {
				openTab(TAB_ATTACK);
			}
			clickMouse(random(570, 700), random(360, 400), true);
			wait(random(1000, 1200));
		}

		log("Loading Item Prices...");
		for (int i = 0; i < lootarray.length; i++) {
			prices[i] = getMarketPriceByID(lootarray[i]);
			ItemPrice = price;
		}
		log("Done.");
		startTime = System.currentTimeMillis();
		if (killopt1 == true) {
			log("Killing men at Edgeville");
		} else {
			log("Killing Al-Kharid warriors");
		}
		Bot.disableBreakHandler = true;
		return true;
	}

	public double getVersion() {
		return properties.version();
	}

	private int getMarketPriceByID(final int ID) {
		return grandExchange.loadItemInfo(ID).getMarketPrice();
	}

	public void onFinish() {
		log(properties.name() + " Version " + properties.version());
		log("Gained " + MoneyGained + "GP");
		stopTime = System.currentTimeMillis();
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		log("Script ran " + hours + ":" + minutes + ":" + seconds + " Hours");
		Bot.getEventManager().removeListener(PaintListener.class, this);
	}

	public boolean needtobank() {
		if (getInventoryCount(foodid) == 0) {
			return true;
		}
		return false;
	}

	public int AntiBan() {
		if (random(0, 5) == 1) {
			moveMouseRandomly(1000);
			{
				return random(200, 250);
			}
		} else if (random(0, 20) == 10) {
			openTab(random(1, 24));
			wait(random(1000, 1200));
			return random(200, 400);

		} else if (random(0, 100) == 5
				&& skills.getCurrentSkillExp(STAT_DEFENSE) - DEFENSEstartexp > 0) {
			openTab(TAB_STATS);
			{
				moveMouse(581 + random(-10, 10), 284 + random(-10, 10));
				wait(random(1000, 1200));
				return random(200, 250);
			}
		} else if (random(0, 100) == 10
				&& skills.getCurrentSkillExp(STAT_STRENGTH) - STRENGTHstartexp > 0) {
			openTab(TAB_STATS);
			{
				moveMouse(576 + random(-10, 10), 251 + random(-10, 10));
				wait(random(1000, 1200));
				return random(200, 250);
			}
		} else if (random(0, 100) == 20
				&& skills.getCurrentSkillExp(STAT_RANGE) - RANGEstartexp > 0) {
			openTab(TAB_STATS);
			{
				moveMouse(574 + random(-10, 10), 311 + random(-10, 10));
				wait(random(1000, 1200));
				return random(200, 250);
			}
		} else if (random(0, 100) == 30
				&& skills.getCurrentSkillExp(STAT_ATTACK) - startexp > 0) {
			openTab(TAB_STATS);
			{
				moveMouse(582 + random(-10, 10), 222 + random(-10, 10));
				wait(random(1000, 1200));
				return random(200, 250);
			}
		} else if (random(0, 100) == 40
				&& skills.getCurrentSkillExp(STAT_HITPOINTS)
						- HITPOINTSstartexp > 0) {
			openTab(TAB_STATS);
			{
				moveMouse(641 + random(-10, 10), 224 + random(-10, 10));
				wait(random(1000, 1200));
				return random(200, 250);
			}
		} else if (random(0, 100) == 0) {
			wait(random(2000, 10000));
			return random(200, 400);
		}
		return random(2000, 3000);
	}

	private class xAntiBan implements Runnable {
		public boolean stopThread;

		public void run() {
			while (!stopThread) {
				try {
					if (random(0, 15) == 0) {
						final char[] LR = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT };
						final char[] UD = new char[] { KeyEvent.VK_DOWN,
								KeyEvent.VK_UP };
						final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP };
						final int random2 = random(0, 2);
						final int random1 = random(0, 2);
						final int random4 = random(0, 4);

						if (random(0, 3) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random(300, 600));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random(300, 600));
							} else {
								Thread.sleep(random(500, 900));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random(200, 2000));
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected int getMouseSpeed() {
		return random(5, 8);
	}

	public void Floor() {
		try {
			if (getPlane() == 1) {
				if (killopt1 == false) {
					RSObject stairs = getNearestObjectByID(35534);
					RSTile stairsloc = stairs.getLocation();
					if (distanceTo(stairsloc) > 5) {
						walkTo(randomizeTile(stairsloc, 2, 2));
						wait(random(500, 700));
					} else {
						atObject(stairs, "Climb-down");
						wait(random(700, 1000));
					}
				} else {
					RSObject stairs = getNearestObjectByID(26983);
					RSTile stairsloc = stairs.getLocation();
					if (distanceTo(stairsloc) > 5) {
						walkTo(randomizeTile(stairsloc, 2, 2));
						wait(random(500, 700));
					} else {
						atTile(new RSTile(3096, 3511), 40, random(0.1, 0.9),
								random(0.75, 0.8), "Climb-down");
						wait(random(700, 1000));
					}
				}
			}
		} catch (NullPointerException e) {
		}
	}

	public void hovermouse() {
		try {
			RSNPC goat = getNearestFreeNPCToAttackByName("Al-Kharid warrior");
			Point loc = goat.getScreenLocation();
			if (!pointOnScreen(loc)) {
				turnToCharacter(goat, 6);
				wait(random(200, 300));

			} else {

				while (getMyPlayer().getInteracting() != null
						&& getMyPlayer().getHPPercent() <= 30 + random(-10, 10)) {
					if (pointOnScreen(loc)) {
						RSNPC goat1 = getNearestFreeNPCToAttackByName("Al-Kharid warrior");
						Point loc1 = goat1.getScreenLocation();
						moveMouse(loc1);
					}
				}
			}
		} catch (NullPointerException e) {

		}
	}

	private boolean NPCIsInArea(int minX, int maxX, int minY, int maxY) {
		int y = getNearestNPCByName("Man").getLocation().getY();
		int x = getNearestNPCByName("Man").getLocation().getX();
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	private boolean isInArea(int minX, int maxX, int minY, int maxY) {
		return getMyPlayer().getLocation().getX() >= minX
				&& getMyPlayer().getLocation().getX() <= maxX
				&& getMyPlayer().getLocation().getY() >= minY
				&& getMyPlayer().getLocation().getY() <= maxY;
	}

	public void Attack() {
		try {
			RSNPC warrior = getNearestNPCToAttackByName("Al-Kharid warrior");
			RSTile loc = warrior.getLocation();

			if (!tileOnScreen(loc)) {
				if (getMyPlayer().isMoving()) {
					wait(random(700, 1200));
				} else {
					walkTo(randomizeTile(loc, 2, 2));
					wait(random(500, 1000));
				}
			} else {
				if (clickRSNPC(warrior, "Attack")) {
					wait(random(500, 800));
					moveMouseSlightly();
				}
			}
		} catch (NullPointerException e) {
		}
	}

	public void getInside() {
		try {
			if (killopt1 == true) {
				if (getNearestObjectByID(26910) != null && !needtobank()) {
					atDoorTiles(new RSTile(3101, 3509), new RSTile(3100, 3509));
					wait(random(800, 1000));
				} else if (getNearestObjectByID(26910) == null && !needtobank()) {
					toMen();
				}
			}
		} catch (NullPointerException e) {
		}
	}

	public void attackMen() {
		try {
			RSNPC warrior = getNearestFreeNPCToAttackByName("Man");
			RSTile loc = warrior.getLocation();

			if (!tileOnScreen(loc)) {
				if (getMyPlayer().isMoving()) {
					wait(random(700, 1200));
				} else {
					walkTo(randomizeTile(loc, 2, 2));
					wait(random(500, 1000));
				}
			} else {
				if (NPCIsInArea(3091, 3100, 3507, 3512)) {
					if (atNPC(warrior, "Attack", true)) {
						wait(random(500, 800));
						moveMouseSlightly();
					}
				} else {

				}
			}
		} catch (NullPointerException e) {
		}
	}

	public void toWarriors() {
		if (!isRunning()) {
			setRun(true);
		}
		RSTile[] path = (generateFixedPath(togoats));
		if (path != null
				&& (!getMyPlayer().isMoving() || distanceTo(getDestination()) < 5)) {
			walkPathMM(randomizePath(path, 1, 1), 15);
			wait(random(200, 500));
		}
	}

	public void toMen() {
		if (getNearestObjectByID(26910) != null) {
			if (distanceTo(getNearestObjectByID(26910)) > 5) {
				if (getMyPlayer().isMoving()) {
					wait(random(700, 1200));
				} else {
					walkTo(randomizeTile(toMen, 2, 2));
					wait(random(500, 1000));
				}
			} else {
				atDoorTiles(new RSTile(3101, 3509), new RSTile(3100, 3509));
				wait(random(800, 1000));
			}
		} else {
			RSTile[] path = (generateFixedPath(toMen2));
			if (path != null
					&& (!getMyPlayer().isMoving() || distanceTo(getDestination()) < 5)) {
				walkPathMM(randomizePath(path, 1, 1), 15);
				wait(random(200, 500));
			}
		}
	}

	public void toBank() { // Edgeville bank
		if (getNearestObjectByID(26910) != null
				&& isInArea(3091, 3100, 3507, 3512)) {
			if (getMyPlayer().isMoving()) {
				wait(random(700, 1200));
			} else {
				walkTo(randomizeTile(new RSTile(3099, 3510), 2, 2));
				wait(random(500, 1000));
			}
			wait(random(1000, 2000));
			atDoorTiles(new RSTile(3101, 3509), new RSTile(3100, 3509));
		} else {
			RSTile[] path = (generateFixedPath(toBank));
			if (path != null
					&& (!getMyPlayer().isMoving() || distanceTo(getDestination()) < 5)) {
				walkPathMM(randomizePath(path, 1, 1), 15);
				wait(random(200, 500));
			}
		}
	}

	public void tobank() {
		if (!isRunning()) {
			setRun(true);
		}
		RSTile[] path = (generateFixedPath(tobank));
		if (path != null
				&& (!getMyPlayer().isMoving() || distanceTo(getDestination()) < 5)) {
			walkPathMM(randomizePath(path, 1, 1), 15);
			wait(random(200, 500));
		}
	}

	public void special() {

	}

	public void arrowpickup() {
		if (arrowpickup) {
			if (getMyPlayer().getInteracting() == null) {
				RSTile tile = getNearestGroundItemByID(arrowid);
				if (!(getInventoryCount() == 28)) {
					if (distanceTo(tile) <= 5) {
						if (getMyPlayer().isMoving()) {
							wait(random(400, 800));
						} else {
							atTile(tile, "Take " + arrowname);
							wait(random(1000, 1500));
						}
					}
				}
			}
		}
	}

	public void arrowequip() {
		if (arrowpickup) {
			if (getInventoryCount(arrowid) > random(8, 24)) {
				atInventoryItem(arrowid, "Wield");
				wait(random(500, 1000));
			}
		}
	}

	public void boneloot() {
		try {
			if (bones) {
				if (getMyPlayer().getInteracting() == null) {
					RSTile tile = getNearestGroundItemByID(526);
					if (!(getInventoryCount() == 28)) {
						if (distanceTo(tile) <= 5) {
							atTile(tile, "Take Bones");
							wait(random(1000, 1500));
						}
					}
				}
			}
		} catch (NullPointerException e) {
		}
	}

	public void bury() {
		if (bury) {
			if (getMyPlayer().getInteracting() == null) {
				if (getInventoryCount(526) > 0 || needtobank()) {
					atInventoryItem(526, "Bury");
					wait(random(400, 600));
				}
			}
		}
	}

	public void looting() {
		if (getMyPlayer().getInteracting() == null) {
			RSItemTile tile;
			for (int i = 0; i < lootarray.length; i++) {
				while ((tile = getGroundItemByID(lootarray[i])) != null
						&& getMyPlayer().getAnimation() == -1) {
					if (!tileOnScreen(tile)) {
						walkTo(tile);
						wait(random(1000, 1500));
					} else {
						if (getInventoryCount() <= 27) {
							int inv = getInventoryCount(lootarray[i]);
							if (atTile(tile, "Take " + arraynames[i])) {
								wait(random(800, 1000));
								if (getInventoryCount(lootarray[i]) > inv) {
									ItemPrice = getMarketPriceByID(lootarray[i]);
									MoneyGained = MoneyGained + ItemPrice;
									looted++;
									wait(random(300, 500));
									while (getMyPlayer().isMoving()) {
										wait(random(100, 250));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void drink() {
		if (currentstrlvl == skills.getCurrentSkillLevel(STAT_STRENGTH)) {
			if (inventoryContains(113)) {
				atInventoryItem(113, "Drink");
				wait(random(800, 1400));
			} else if (inventoryContains(115)) {
				atInventoryItem(115, "Drink");
				wait(random(800, 1400));
			} else if (inventoryContains(117)) {
				atInventoryItem(117, "Drink");
				wait(random(800, 1400));
			} else if (inventoryContains(119)) {
				atInventoryItem(119, "Drink");
				wait(random(800, 1400));
			}
		}
		if (currentattlvl == skills.getCurrentSkillLevel(STAT_ATTACK)) {
			if (inventoryContains(2428)) {
				atInventoryItem(2428, "Drink");
				wait(random(800, 1400));
			}
			if (inventoryContains(121)) {
				atInventoryItem(121, "Drink");
				wait(random(800, 1400));
			}
			if (inventoryContains(123)) {
				atInventoryItem(123, "Drink");
				wait(random(800, 1400));
			}
			if (inventoryContains(125)) {
				atInventoryItem(125, "Drink");
				wait(random(800, 1400));
			}
		}
	}

	public void banking() {
		try {
		if (bank.isOpen()) {
			if (getInventoryCount() > 0) {
				if(inventoryContains(pots)) {
				bank.depositAllExcept(pots);
				} else {
					bank.depositAll();
				}
			}
            if (strpotion) {
                    bank.atItem(113, "Withdraw-1");
                    wait(random(500, 1000));
            }
            if (attpotion) {
                    bank.atItem(2428, "Withdraw-1");
                    wait(random(500, 1000));
            }
			wait(random(500, 1000));
			bank.withdraw(foodid, withdraw);
			wait(random(500, 1000));
			if (random(0, 1) == 1) {
				bank.close();
			}
		} else {
			bank.open();
			wait(random(500, 800));
		}
		} catch(NullPointerException e) {			
		}
	}

	public void eat() {
		if (getMyPlayer().getHPPercent() <= 50 + random(-10, 10)) {
			atInventoryItem(foodid, "Eat");
			wait(random(200, 250));
		}
	}

	public void stopScript() {
		if (logout1) {
			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			long seconds = millis / 1000;
			if (minutes > aftermin) {
				stopScript();
			}
		}
		if (logout2) {
			if (skills.getCurrentSkillExp(STAT_STRENGTH) > str) {
				if (getCurrentTab() != TAB_ATTACK) {
					openTab(TAB_ATTACK);
				}
				if (skills.getCurrentSkillExp(STAT_ATTACK) < att) {
					atInterface(818, 11);
					wait(random(500, 1000));
				} else if (skills.getCurrentSkillExp(STAT_DEFENSE) < def) {
					atInterface(818, 14);
					wait(random(500, 1000));
				} else {
					stopScript();
				}
			}
			if (skills.getCurrentSkillExp(STAT_ATTACK) > att) {
				if (getCurrentTab() != TAB_ATTACK) {
					openTab(TAB_ATTACK);
				}
				if (skills.getCurrentSkillExp(STAT_STRENGTH) < str) {
					atInterface(818, 12);
					wait(random(500, 1000));
				} else if (skills.getCurrentSkillExp(STAT_DEFENSE) < def) {
					atInterface(818, 14);
					wait(random(500, 1000));
				} else {
					stopScript();
				}
			}

			if (skills.getCurrentSkillExp(STAT_RANGE) > range) {
				stopScript();
			}

			if (skills.getCurrentSkillExp(STAT_DEFENSE) > def) {
				if (getCurrentTab() != TAB_ATTACK) {
					openTab(TAB_ATTACK);
				}
				if (skills.getCurrentSkillExp(STAT_ATTACK) < att) {
					atInterface(818, 11);
					wait(random(500, 1000));
				} else if (skills.getCurrentSkillExp(STAT_STRENGTH) < str) {
					atInterface(818, 12);
					wait(random(500, 1000));
				} else {
					stopScript();
				}
			}
		}
	}

	public int loop() {
		stopScript();
		arrowpickup();
		arrowequip();
		boneloot();
		bury();
		drink();
		getMouseSpeed();
		AntiBan();
		eat();
		Floor();
		getInside();
		if (!isRunning() && getEnergy() > 50) {
			setRun(true);
		}
		if (getInventoryCount() == 28 && inventoryContains(foodid)) {
			atInventoryItem(foodid, "Eat");
			wait(random(200, 250));
		}
		if (getMyPlayer().getInteracting() == null && !needtobank()) {
			looting();
		}
		if (killopt1 == false) {
			if (needtobank()) {
				if (distanceTo(getNearestObjectByID(35647)) > 8) {
					tobank();
				} else {
					banking();
				}
			}
			if (!needtobank()) {
				if (distanceTo(togoats) > 14) {
					toWarriors();
				} else if (getMyPlayer().getInteracting() == null) {
					Attack();
				}
			}
		} else if (killopt1 == true) {
			if (needtobank()) {
				if (distanceTo(getNearestObjectByID(26972)) > 5) {
					toBank();
				} else {
					banking();
				}
			} else {
				if (distanceTo(new RSTile(3096, 3510)) > 7) {
					toMen();
				} else if (getMyPlayer().getInteracting() == null) {
					attackMen();
				}
			}

		}
		return 100;
	}

	public void onRepaint(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (isLoggedIn()) {

			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			long seconds = millis / 1000;

			try {
				RSNPC goat = getNearestFreeNPCToAttackByName(
						"Al-Kharid warrior", "Man");
				Point Xx = goat.getLocation().getScreenLocation();
				if (pointOnScreen(Xx)) {
					g.setColor(new Color(0, 0, 0, 150));
					g.fillRect(Xx.x - 8, Xx.y - 18, 30, 30);
					g.setColor(Color.black);
					g.drawRect(Xx.x - 8, Xx.y - 18, 30, 30);
					org.rsbot.script.wrappers.RSPlayer Me = getMyPlayer();
					Point Xx1 = Me.getLocation().getScreenLocation();
					if (pointOnScreen(Xx1)) {
						g.setColor(new Color(200, 0, 0, 150));
						g.fillRect(Xx1.x - 8, Xx1.y - 15, 30, 30);
						g.setColor(Color.black);
						g.drawRect(Xx1.x - 8, Xx1.y - 15, 30, 30);
					}
					g.setColor(Color.white);
					g.drawLine(Xx.x, Xx.y, Xx1.x, Xx1.y);
				}
			} catch (NullPointerException e5) {
			}
			try {
				org.rsbot.script.wrappers.RSPlayer Me = getMyPlayer();
				Point Xx = Me.getLocation().getScreenLocation();
				if (pointOnScreen(Xx)) {
					g.setColor(new Color(200, 0, 0, 200));
					g.fillRect(Xx.x - 8, Xx.y - 15, 30, 30);
					g.setColor(Color.black);
					g.drawRect(Xx.x - 8, Xx.y - 15, 30, 30);
				}
			} catch (NullPointerException e5) {
			}
			int xpGained = 0;
			if (startexp == 0) {
				startexp = skills.getCurrentSkillExp(STAT_ATTACK);
			}
			int startLevel = skills.getCurrentSkillLevel(STAT_ATTACK);
			xpGained = skills.getCurrentSkillExp(STAT_ATTACK) - startexp;

			int STRENGTHxpGained = 0;
			if (STRENGTHstartexp == 0) {
				STRENGTHstartexp = skills.getCurrentSkillExp(STAT_STRENGTH);
			}
			int STRENGTHstartLevel = skills.getCurrentSkillLevel(STAT_STRENGTH);
			STRENGTHxpGained = skills.getCurrentSkillExp(STAT_STRENGTH)
					- STRENGTHstartexp;

			int DEFENSExpGained = 0;
			if (DEFENSEstartexp == 0) {
				DEFENSEstartexp = skills.getCurrentSkillExp(STAT_DEFENSE);
			}
			int DEFENSEstartLevel = skills.getCurrentSkillLevel(STAT_DEFENSE);
			DEFENSExpGained = skills.getCurrentSkillExp(STAT_DEFENSE)
					- DEFENSEstartexp;

			int HITPOINTSxpGained = 0;
			if (HITPOINTSstartexp == 0) {
				HITPOINTSstartexp = skills.getCurrentSkillExp(STAT_HITPOINTS);
			}
			int HITPOINTSstartLevel = skills
					.getCurrentSkillLevel(STAT_HITPOINTS);
			HITPOINTSxpGained = skills.getCurrentSkillExp(STAT_HITPOINTS)
					- HITPOINTSstartexp;

			int RANGExpGained = 0;
			if (RANGEstartexp == 0) {
				RANGEstartexp = skills.getCurrentSkillExp(STAT_RANGE);
			}
			int RANGEstartLevel = skills.getCurrentSkillLevel(STAT_RANGE);
			RANGExpGained = skills.getCurrentSkillExp(STAT_RANGE)
					- RANGEstartexp;

			Mouse m = Bot.getClient().getMouse();
			if (!(m.x >= 115 && m.x < 115 + 300 && m.y >= 278 && m.y < 278 + 60)) {

				int x15 = 165;
				int y15 = 299;
				g.setColor(new Color(0, 0, 0, 150));// 0, 0, 0,150
				g.fillRect(x15, y15 - 12, 200, 10);
				g.setColor(new Color(20, 20, 20, 150)); // 20, 20, 20, 150
				g.fillRect(x15, y15 - 22, 200, 10);
				g.drawRect(x15, y15 - 22, 200, 20);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString(properties.name() + " Version "
						+ properties.version(), x15 + 25, y15 -= 22);
				g.drawString("Time running: " + hours + ":" + minutes + ":"
						+ seconds, x15 + 40, y15 += 15);

				int x18 = 115;
				int y18 = 319;
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x18, y18 - 12, 150, 10);
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x18, y18 - 22, 150, 10);
				g.drawRect(x18, y18 - 22, 150, 20);
				g.setColor(new Color(255, 255, 255, 255));
				g
						.drawString("Money Gained: " + MoneyGained, x18 + 20,
								y18 -= 7);

				int x20 = 265;
				int y20 = 319;
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x20, y20 - 12, 150, 10);
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x20, y20 - 22, 150, 10);
				g.drawRect(x20, y20 - 22, 150, 20);
				g.setColor(new Color(255, 255, 255, 255));
				int ProfitPerHour = (int) (MoneyGained * 3600000D / (System
						.currentTimeMillis() - startTime));
				g.drawString("Profit hr: " + ProfitPerHour, x20 + 30, y20 -= 7);

				int x12 = 130;
				int y12 = 339;
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x12, y12 - 12, 135, 10);
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x12, y12 - 22, 135, 10);
				g.drawRect(x12, y12 - 22, 135, 20);
				g.setColor(new Color(255, 255, 255, 255));
				int totalxp = xpGained + STRENGTHxpGained + HITPOINTSxpGained
						+ DEFENSExpGained + RANGExpGained;
				g.drawString("Xp Gained: " + totalxp, x12 + 20, y12 -= 7);

				int x13 = 265;
				int y13 = 339;
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x13, y13 - 12, 135, 10);
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x13, y13 - 22, 135, 10);
				g.drawRect(x13, y13 - 22, 135, 20);
				g.setColor(new Color(255, 255, 255, 255));
				final int perHourXP = (int) (totalxp * 3600000D / (System
						.currentTimeMillis() - startTime));
				g.drawString("Xp/h: " + perHourXP, x13 + 40, y13 -= 7);
			}
			if (m.x >= 115 && m.x < 115 + 300 && m.y >= 278 && m.y < 278 + 60) {

				int x1 = 165;
				int y1 = 199;
				g.setColor(new Color(0, 0, 0, 150));// 0, 0, 0,150
				g.fillRect(x1, y1 - 12, 200, 10);
				g.setColor(new Color(20, 20, 20, 150)); // 20, 20, 20, 150
				g.fillRect(x1, y1 - 22, 200, 10);
				g.drawRect(x1, y1 - 22, 200, 20);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString(properties.name() + " Version "
						+ properties.version(), x1 + 25, y1 -= 22);
				g.drawString("Time running: " + hours + ":" + minutes + ":"
						+ seconds, x1 + 40, y1 += 15);

				int x8 = 115;
				int y8 = 219;
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x8, y8 - 12, 150, 10);
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x8, y8 - 22, 150, 10);
				g.drawRect(x8, y8 - 22, 150, 20);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString("Money Gained: " + MoneyGained, x8 + 20, y8 -= 7);

				int x20 = 265;
				int y20 = 219;
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x20, y20 - 12, 150, 10);
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x20, y20 - 22, 150, 10);
				g.drawRect(x20, y20 - 22, 150, 20);
				g.setColor(new Color(255, 255, 255, 255));
				int ProfitPerHour = (int) (MoneyGained * 3600000D / (System
						.currentTimeMillis() - startTime));
				g.drawString("Profit hr: " + ProfitPerHour, x20 + 30, y20 -= 7);

				int x2 = 130;
				int y2 = 239;
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x2, y2 - 12, 135, 10);
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x2, y2 - 22, 135, 10);
				g.drawRect(x2, y2 - 22, 135, 20);
				g.setColor(new Color(255, 255, 255, 255));
				int totalxp = xpGained + STRENGTHxpGained + HITPOINTSxpGained
						+ DEFENSExpGained + RANGExpGained;
				g.drawString("Xp Gained: " + totalxp, x2 + 20, y2 -= 7);

				int x3 = 265;
				int y3 = 239;
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x3, y3 - 12, 135, 10);
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x3, y3 - 22, 135, 10);
				g.drawRect(x3, y3 - 22, 135, 20);
				g.setColor(new Color(255, 255, 255, 255));
				final int perHourXP = (int) (totalxp * 3600000D / (System
						.currentTimeMillis() - startTime));
				g.drawString("Xp/h: " + perHourXP, x3 + 40, y3 -= 7);

				int x = 1;
				int y = 258;
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x, y - 20, 516, 10);
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x, y - 10, 516, 10);
				g.drawRect(x, y - 20, 516, 20);
				g.setColor(new Color(13, 13, 13, 25));
				g.drawRect(x, y - 20, 516, 10);
				g.setColor(new Color(50, 50, 50, 25));
				g.drawRect(x, y - 20, 516, 10);
				g.setColor(new Color(255, 255, 255, 50));
				g.fillRect(x + 5, y - 15, skills
						.getPercentToNextLevel(STAT_ATTACK) * 5, 12);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString(currentattlvl + " Attack "
						+ skills.getPercentToNextLevel(STAT_ATTACK) + "%",
						x + 230, y - 5);

				int x4 = 1;
				int y4 = 278;
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x4, y4 - 20, 516, 10);
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x4, y4 - 10, 516, 10);
				g.drawRect(x4, y4 - 20, 516, 20);
				g.setColor(new Color(13, 13, 13, 25));
				g.drawRect(x4, y4 - 20, 516, 10);
				g.setColor(new Color(50, 50, 50, 25));
				g.drawRect(x4, y4 - 20, 516, 10);
				g.setColor(new Color(255, 255, 255, 50));
				g.fillRect(x4 + 5, y4 - 15, skills
						.getPercentToNextLevel(STAT_STRENGTH) * 5, 12);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString(currentstrlvl + " Strength "
						+ skills.getPercentToNextLevel(STAT_STRENGTH) + "%",
						x4 + 230, y4 - 5);

				int x5 = 1;
				int y5 = 298;
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x5, y5 - 20, 516, 10);
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x5, y5 - 10, 516, 10);
				g.drawRect(x5, y5 - 20, 516, 20);
				g.setColor(new Color(13, 13, 13, 25));
				g.drawRect(x5, y5 - 20, 516, 10);
				g.setColor(new Color(50, 50, 50, 25));
				g.drawRect(x5, y5 - 20, 516, 10);
				g.setColor(new Color(255, 255, 255, 50));
				g.fillRect(x5 + 5, y5 - 15, skills
						.getPercentToNextLevel(STAT_DEFENSE) * 5, 12);
				g.setColor(Color.white);
				g.drawString(skills.getCurrentSkillLevel(STAT_DEFENSE)
						+ " Defence "
						+ skills.getPercentToNextLevel(STAT_DEFENSE) + "%",
						x5 + 230, y5 - 5);

				int x6 = 1;
				int y6 = 318;
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x6, y6 - 20, 516, 10);
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x6, y6 - 10, 516, 10);
				g.drawRect(x6, y6 - 20, 516, 20);
				g.setColor(new Color(13, 13, 13, 25));
				g.drawRect(x6, y6 - 20, 516, 10);
				g.setColor(new Color(50, 50, 50, 25));
				g.drawRect(x6, y6 - 20, 516, 10);
				g.setColor(new Color(255, 255, 255, 50));
				g.fillRect(x6 + 5, y6 - 15, skills
						.getPercentToNextLevel(STAT_HITPOINTS) * 5, 12);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString(skills.getCurrentSkillLevel(STAT_HITPOINTS)
						+ " HitPoints "
						+ skills.getPercentToNextLevel(STAT_HITPOINTS) + "%",
						x6 + 230, y6 - 5);

				int x7 = 1;
				int y7 = 338;
				g.setColor(new Color(20, 20, 20, 150));
				g.fillRect(x7, y7 - 20, 516, 10);
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(x7, y7 - 10, 516, 10);
				g.drawRect(x7, y7 - 20, 516, 20);
				g.setColor(new Color(13, 13, 13, 25));
				g.drawRect(x7, y7 - 20, 516, 10);
				g.setColor(new Color(50, 50, 50, 25));
				g.drawRect(x7, y7 - 20, 516, 10);
				g.setColor(new Color(255, 255, 255, 50));
				g.fillRect(x7 + 5, y7 - 15, skills
						.getPercentToNextLevel(STAT_RANGE) * 5, 12);
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString(skills.getCurrentSkillLevel(STAT_RANGE)
						+ " Range " + skills.getPercentToNextLevel(STAT_RANGE)
						+ "%", x7 + 230, y7 - 5);
			}
		}
	}

	// Credits to Unit
	public void launchURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);

			else { // assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape", "safari" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(
							new String[] { "which", browsers[count] })
							.waitFor() == 0)
						browser = browsers[count];
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else
					Runtime.getRuntime().exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			log("Failed to open URL");
		}
	}

	public String getNews() throws IOException {
		try {
			String line;
			String Text = "";

			url = new URL("http://binaryx.nl/antic/Changelog.txtt")
					.openConnection();

			in = new BufferedReader(new InputStreamReader(url.getInputStream()));
			while ((line = in.readLine()) != null) {
				Text += line + "\n";

			}
			return Text;
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			in.close();
		}
		return null;
	}

	public class GoatSlayerGUI extends JFrame {

		public GoatSlayerGUI() {
			initComponents();
		}

		private void StartBtn(ActionEvent e) {

			arrowpickup = checkBox8.isSelected();
			special = checkBox2.isSelected();
			strpotion = checkBox6.isSelected();
			attpotion = checkBox7.isSelected();
			bones = checkBox4.isSelected();
			bury = checkBox5.isSelected();
			logout1 = checkBox1.isSelected();
			logout2 = checkBox2.isSelected();
			spec1 = checkBox2.isSelected();
			spec2 = checkBox2.isSelected();
			specat = comboBox3.getSelectedItem().toString();
			try {
			aftermin = Integer.parseInt(textField8.getText());
			str = Integer.parseInt(textField3.getText());
			att = Integer.parseInt(textField4.getText());
			range = Integer.parseInt(textField5.getText());
			hp = Integer.parseInt(textField6.getText());
			def = Integer.parseInt(textField7.getText());
			arrowid = Integer.parseInt(textField8.getText());
			}catch(NumberFormatException e1) {				
			}
			try {
				foodid = Integer.parseInt(textField1.getText());
				withdraw = Integer.parseInt(textField9.getText());
			}catch(NumberFormatException e2) {				
			}			

			if (foodid == 0) {
				nofood = true;
			}

			if (arrowpickup) {
				if (arrowid == 882) {
					arrowname = "Bronze arrow";
				} else if (arrowid == 884) {
					arrowname = "Iron arrow";
				} else if (arrowid == 886) {
					arrowname = "Steel arrow";
				} else if (arrowid == 864) {
					arrowname = "Bronze knife";
				} else if (arrowid == 863) {
					arrowname = "Iron knife";
				} else if (arrowid == 865) {
					arrowname = "Steel knife";
				} else if (arrowid == 8882) {
					arrowname = "Bone bolts";
				} else if (arrowid == 877) {
					arrowname = "Bronze bolts";
				} else if (arrowid == 9140) {
					arrowname = "Iron bolts";
				} else if (arrowid == 9141) {
					arrowname = "Steel bolts";
				}
			}
			if (comboBox2.getSelectedIndex() == 0) {
				killopt1 = true;
			} else {
				killopt1 = false;
			}
			if (antiban2) {
				antiban = new xAntiBan();
				t = new Thread(antiban);

				if (!t.isAlive()) {
					t.start();
					log("AntiBan Enabled");
				}
			}
			guiWait = false;
			guiExit = true;
			dispose();
		}

		private void ExitBtn(ActionEvent e) {
			guiWait = false;
			guiExit = true;
			dispose();
		}

		private void ForumsBtn(ActionEvent e) {
			launchURL("http://binaryx.nl/");
		}

		private void LogBTN(ActionEvent e) {
			launchURL("http://binaryx.nl/?page_id=289");
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			label1 = new JLabel();
			button1 = new JButton();
			button2 = new JButton();
			button3 = new JButton();
			label5 = new JLabel();
			button4 = new JButton();
			tabbedPane2 = new JTabbedPane();
			panel1 = new JPanel();
			label2 = new JLabel();
			textField1 = new JTextField();
			label3 = new JLabel();
			label4 = new JLabel();
			comboBox2 = new JComboBox();
			checkBox1 = new JCheckBox();
			textField2 = new JTextField();
			label6 = new JLabel();
			checkBox2 = new JCheckBox();
			textField3 = new JTextField();
			textField4 = new JTextField();
			textField5 = new JTextField();
			label7 = new JLabel();
			label8 = new JLabel();
			label9 = new JLabel();
			textField6 = new JTextField();
			textField7 = new JTextField();
			label10 = new JLabel();
			label11 = new JLabel();
			textField9 = new JTextField();
			panel2 = new JPanel();
			checkBox3 = new JCheckBox();
			checkBox4 = new JCheckBox();
			checkBox5 = new JCheckBox();
			checkBox6 = new JCheckBox();
			checkBox7 = new JCheckBox();
			checkBox8 = new JCheckBox();
			textField8 = new JTextField();
			scrollPane1 = new JScrollPane();
			textArea1 = new JTextArea();
			checkBox9 = new JCheckBox();
			comboBox3 = new JComboBox();

			//======== this ========
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			//---- label1 ----
			label1.setText("AWarrior");
			label1.setFont(new Font("Viner Hand ITC", Font.BOLD, 48));
			contentPane.add(label1);
			label1.setBounds(90, 10, 280, 55);

			//---- button1 ----
			button1.setText("Start");
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					StartBtn(e);
				}
			});
			contentPane.add(button1);
			button1.setBounds(15, 330, 110, 33);

			//---- button2 ----
			button2.setText("Exit");
			button2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ExitBtn(e);
				}
			});
			contentPane.add(button2);
			button2.setBounds(130, 330, 110, 33);

			//---- button3 ----
			button3.setText("Forums");
			button3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ForumsBtn(e);
				}
			});
			contentPane.add(button3);
			button3.setBounds(245, 330, 110, 33);

			//---- label5 ----
			label5.setText("Antic");
			label5.setFont(new Font("Showcard Gothic", Font.PLAIN, 12));
			contentPane.add(label5);
			label5.setBounds(new Rectangle(new Point(330, 55), label5.getPreferredSize()));

			//---- button4 ----
			button4.setText("Changelog");
			button4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LogBTN(e);
				}
			});
			contentPane.add(button4);
			button4.setBounds(360, 330, 110, 33);

			//======== tabbedPane2 ========
			{
				tabbedPane2.setToolTipText("Main");

				//======== panel1 ========
				{
					panel1.setLayout(null);

					//---- label2 ----
					label2.setText("Food ID");
					label2.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label2);
					label2.setBounds(10, 15, 85, 20);
					panel1.add(textField1);
					textField1.setBounds(115, 10, 95, 25);

					//---- label3 ----
					label3.setText("Withdraw");
					label3.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label3);
					label3.setBounds(10, 60, 90, 20);

					//---- label4 ----
					label4.setText("Select the NPC to kill");
					label4.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label4);
					label4.setBounds(10, 100, 185, 25);

					//---- comboBox2 ----
					comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Edgeville men",
						"Al-Kharid warriors"
					}));
					comboBox2.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel1.add(comboBox2);
					comboBox2.setBounds(10, 130, 180, 25);

					//---- checkBox1 ----
					checkBox1.setText("Log out after");
					checkBox1.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(checkBox1);
					checkBox1.setBounds(260, 10, 135, 25);
					panel1.add(textField2);
					textField2.setBounds(265, 50, 95, 25);

					//---- label6 ----
					label6.setText("Minutes");
					label6.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label6);
					label6.setBounds(365, 55, 90, 20);

					//---- checkBox2 ----
					checkBox2.setText("Log out after");
					checkBox2.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(checkBox2);
					checkBox2.setBounds(260, 95, 135, 25);
					panel1.add(textField3);
					textField3.setBounds(210, 125, 55, 25);
					panel1.add(textField4);
					textField4.setBounds(210, 160, 55, 25);
					panel1.add(textField5);
					textField5.setBounds(210, 195, 55, 25);

					//---- label7 ----
					label7.setText("Strength");
					label7.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label7);
					label7.setBounds(270, 130, 70, 20);

					//---- label8 ----
					label8.setText("Attack");
					label8.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label8);
					label8.setBounds(270, 165, 90, 20);

					//---- label9 ----
					label9.setText("Range");
					label9.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label9);
					label9.setBounds(270, 200, 90, 20);
					panel1.add(textField6);
					textField6.setBounds(345, 125, 55, 25);
					panel1.add(textField7);
					textField7.setBounds(345, 160, 55, 25);

					//---- label10 ----
					label10.setText("HP");
					label10.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label10);
					label10.setBounds(405, 130, 70, 20);

					//---- label11 ----
					label11.setText("Defense");
					label11.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel1.add(label11);
					label11.setBounds(405, 165, 90, 20);
					panel1.add(textField9);
					textField9.setBounds(115, 55, 95, 25);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < panel1.getComponentCount(); i++) {
							Rectangle bounds = panel1.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
						}
						Insets insets = panel1.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel1.setMinimumSize(preferredSize);
						panel1.setPreferredSize(preferredSize);
					}
				}
				tabbedPane2.addTab("Main", panel1);


				//======== panel2 ========
				{
					panel2.setLayout(null);

					//---- checkBox3 ----
					checkBox3.setText("Special Attack 1");
					checkBox3.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					checkBox3.setEnabled(false);
					panel2.add(checkBox3);
					checkBox3.setBounds(10, 0, 155, checkBox3.getPreferredSize().height);

					//---- checkBox4 ----
					checkBox4.setText("Loot Bones");
					checkBox4.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel2.add(checkBox4);
					checkBox4.setBounds(180, 40, 120, 39);

					//---- checkBox5 ----
					checkBox5.setText("Bury Bones");
					checkBox5.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel2.add(checkBox5);
					checkBox5.setBounds(350, 40, 120, 39);

					//---- checkBox6 ----
					checkBox6.setText("Strength Potion");
					checkBox6.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel2.add(checkBox6);
					checkBox6.setBounds(10, 80, 155, 39);

					//---- checkBox7 ----
					checkBox7.setText("Attack Potion");
					checkBox7.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel2.add(checkBox7);
					checkBox7.setBounds(10, 40, 145, 39);

					//---- checkBox8 ----
					checkBox8.setText("Loot Arrows");
					checkBox8.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					panel2.add(checkBox8);
					checkBox8.setBounds(10, 120, 155, 40);
					panel2.add(textField8);
					textField8.setBounds(185, 125, 95, 25);

					//======== scrollPane1 ========
					{

						//---- textArea1 ----
						textArea1.setBackground(new Color(204, 204, 204));
						textArea1.setText("Thank you for using AWarriorPro. This script is still in development. Im planning to add more monsters, you can reguest some at my topic in RSBot. Info about the GUI at RSBot.");
						textArea1.setLineWrap(true);
						textArea1.setWrapStyleWord(true);
						scrollPane1.setViewportView(textArea1);
					}
					panel2.add(scrollPane1);
					scrollPane1.setBounds(15, 160, 450, 70);

					//---- checkBox9 ----
					checkBox9.setText("Special Attack 2");
					checkBox9.setFont(new Font("Traditional Arabic", Font.PLAIN, 20));
					checkBox9.setEnabled(false);
					panel2.add(checkBox9);
					checkBox9.setBounds(180, 0, 155, 39);

					//---- comboBox3 ----
					comboBox3.setModel(new DefaultComboBoxModel(new String[] {
						"25",
						"50",
						"75",
						"100"
					}));
					comboBox3.setEnabled(false);
					panel2.add(comboBox3);
					comboBox3.setBounds(350, 5, 95, 25);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < panel2.getComponentCount(); i++) {
							Rectangle bounds = panel2.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
						}
						Insets insets = panel2.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel2.setMinimumSize(preferredSize);
						panel2.setPreferredSize(preferredSize);
					}
				}
				tabbedPane2.addTab("Extra", panel2);

			}
			contentPane.add(tabbedPane2);
			tabbedPane2.setBounds(0, 65, 485, 260);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for(int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			setSize(490, 410);
			setLocationRelativeTo(getOwner());
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		private JLabel label1;
		private JButton button1;
		private JButton button2;
		private JButton button3;
		private JLabel label5;
		private JButton button4;
		private JTabbedPane tabbedPane2;
		private JPanel panel1;
		private JLabel label2;
		private JTextField textField1;
		private JLabel label3;
		private JLabel label4;
		private JComboBox comboBox2;
		private JCheckBox checkBox1;
		private JTextField textField2;
		private JLabel label6;
		private JCheckBox checkBox2;
		private JTextField textField3;
		private JTextField textField4;
		private JTextField textField5;
		private JLabel label7;
		private JLabel label8;
		private JLabel label9;
		private JTextField textField6;
		private JTextField textField7;
		private JLabel label10;
		private JLabel label11;
		private JTextField textField9;
		private JPanel panel2;
		private JCheckBox checkBox3;
		private JCheckBox checkBox4;
		private JCheckBox checkBox5;
		private JCheckBox checkBox6;
		private JCheckBox checkBox7;
		private JCheckBox checkBox8;
		private JTextField textField8;
		private JScrollPane scrollPane1;
		private JTextArea textArea1;
		private JCheckBox checkBox9;
		private JComboBox comboBox3;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}

	public void serverMessageRecieved(ServerMessageEvent arg0) {
		String serverString = arg0.getMessage();
		if (serverString.contains("You've just advanced a Strength level")) {
			log("Congratulations, you have advanced a Strength level!");
			currentstrlvl += 1;
		}
		if (serverString.contains("You've just advanced a Attack level")) {
			log("Congratulations, you have advanced a Attack level!");
			currentattlvl += 1;
		}
	}
}