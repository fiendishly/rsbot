import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

import java.util.*;
import javax.swing.*;

import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.event.listeners.*;

@ScriptManifest(authors = { "Conderoga" }, category = "Woodcutting", name = "Conderoga's Ivy Chopper", version = 1.605,
description = "<html><head>Conderoga's Ivy Chopper</head><body>All options in GUI. Start with an axe by the ivy at your chosen location.</body></html\n")
public class IvyCutC extends Script implements PaintListener {

	private boolean guiWait = true;
	private boolean guiExit = true;
	private String ivyLoc;
	private RSTile ivy1;
	private RSTile ivy2;
	private RSTile ivy3;

	private IvyCutGUI gui;
	private int[] BNIDs = { 5070, 5071, 5072, 5073, 5074, 5075, 5076, 7413,
			11966 };
	private int startLevel;
	private int startExp;
	private int expGained;
	private int lvlsGained;
	private long startTime;
	private double startTimeDbl;
	private int safety = 0;
	private String status;
	private int tempX = 0;
	private int tempY = 0;
	private Point p;

	public boolean onStart(final Map<String, String> args) {
		while (!isLoggedIn()) {
			login();
		}
		gui = new IvyCutGUI();
		gui.setVisible(true);
		while (guiWait) {
			wait(100);
		}
		log("Ivy Cutting Location: " + ivyLoc);
		startTime = System.currentTimeMillis();
		startTimeDbl = System.currentTimeMillis();
		setChoppingDirection(2);
		return !guiExit;
	}

	public void pickUpNest() {
		RSItemTile nestLocation = getGroundItemByID(BNIDs);
		if (nestLocation != null && !isInventoryFull()) {
			status = "Nest!";
			wait(random(600, 800));
			p = Calculations.tileToScreen(nestLocation);
			moveMouse(p);
			atMenu("Take");
			wait(random(600, 800));
			p = Calculations.tileToScreen(ivy2);
			moveMouse(p);
			atMenu("Walk");
		}
	}

	public boolean mouseHoverCheck() {
		final ArrayList<String> commandList = getMenuActions();
		wait(300);
		for (int i = 0; i < commandList.size(); i++)
			if (commandList.get(i).equals("Chop"))
				return true;

		return false;
	}

	public boolean chopCheck() {
		for (int i = 0; i < 5; i++) {
			if (getMyPlayer().getAnimation() == 870) {
				status = "Chopping";
				return true;
			}
			wait(random(200, 300));
		}
		status = "Looking for Ivy";
		return false;
	}

	public void setChoppingDirection(int rand) {
		if (rand == 2) {
			if (ivyLoc.equals("Yanille") || ivyLoc.equals("Grand Exchange")
					|| ivyLoc.equals("North Falador")) {
				setCompass('s');
			}
			if (ivyLoc.equals("Taverly") || ivyLoc.equals("Varrock Palace")) {
				setCompass('e');
			}
			if (ivyLoc.equals("Castle Wars") || ivyLoc.equals("South Falador")) {
				setCompass('n');
			}
		}
	}

	public void chop(int[] loc) {
		setChoppingDirection(random(1, 4));
		if (loc[0] == 0)
			moveToClosestIvyTile();

		else {
			moveMouse(loc[0], loc[1], 3, 3); // Moves mouse to next Ivy
			clickMouse(false);
			atMenu("Chop Ivy");
			moveMouseRandomly(random(50, 60));
			wait(random(1000, 1100));
		}
	}

	public void moveToClosestIvyTile() {
		int[] distances = new int[3];
		distances[0] = distanceTo(ivy1);
		distances[1] = distanceTo(ivy2);
		distances[2] = distanceTo(ivy3);
		int min = 0;
		for (int i = 0; i < 3; i++) {
			if (distances[i] < distances[min]) {
				min = i;
			}
		}
		if (min == 0)
			p = Calculations.tileToScreen(ivy1);
		else if (min == 1)
			p = Calculations.tileToScreen(ivy2);
		else
			p = Calculations.tileToScreen(ivy3);

		moveMouse(p);
		wait(random(600, 800));
		atMenu("Walk");
		chopCheck();
		setCameraAltitude(false);
	}

	public int[] getNextIvy(int rand) {
		int[] loc = { 0, 0 };

		// Checks straight ahead first
		tempX = random(269, 271);
		tempY = random(130, 150);
		moveMouse(tempX, tempY, 0, 0);
		wait(random(300, 400));
		if (mouseHoverCheck()) {
			loc[0] = tempX;
			loc[1] = tempY;
		}
		// wait(random(300,400));
		if (rand == 1) {
			if (loc[0] == 0) // Checks 1 to the right
			{
				tempX = random(320, 340);
				tempY = random(130, 150);
				moveMouse(tempX, tempY, 0, 0);
				wait(random(300, 400));
				if (mouseHoverCheck()) {
					loc[0] = tempX;
					loc[1] = tempY;
				}
			}
			// wait(random(300,400));
			if (loc[0] == 0) // Checks 1 to the left
			{
				tempX = random(200, 220);
				tempY = random(130, 150);
				moveMouse(tempX, tempY, 0, 0);
				wait(random(300, 400));

				if (mouseHoverCheck()) {
					loc[0] = tempX;
					loc[1] = tempY;
				}
			}
			// wait(random(300,400));
			if (loc[0] == 0) // Checks 2 to the left
			{
				tempX = random(140, 150);
				tempY = random(130, 150);
				moveMouse(tempX, tempY, 0, 0);
				wait(random(300, 400));
				if (mouseHoverCheck()) {
					loc[0] = tempX;
					loc[1] = tempY;
				}
			}
			// wait(random(300,400));
			if (loc[0] == 0) // Checks 2 to the right
			{
				tempX = random(390, 420);
				tempY = random(130, 150);
				moveMouse(tempX, tempY, 0, 0);
				wait(random(300, 400));
				if (mouseHoverCheck()) {
					loc[0] = tempX;
					loc[1] = tempY;
				}
			}

		} else {

			if (loc[0] == 0) // Checks 1 to the left
			{
				tempX = random(200, 220);
				tempY = random(130, 150);
				moveMouse(tempX, tempY, 0, 0);
				wait(random(300, 400));

				if (mouseHoverCheck()) {
					loc[0] = tempX;
					loc[1] = tempY;
				}
			}
			// wait(random(300,400));
			// Checks 1 to the right
			if (loc[0] == 0) {
				tempX = random(320, 340);
				tempY = random(130, 150);
				moveMouse(tempX, tempY, 0, 0);
				wait(random(300, 400));
				if (mouseHoverCheck()) {
					loc[0] = tempX;
					loc[1] = tempY;
				}
			}
			// wait(random(300,400));
			if (loc[0] == 0) // Checks 2 to the right
			{
				tempX = random(390, 420);
				tempY = random(130, 150);
				moveMouse(tempX, tempY, 0, 0);
				wait(random(300, 400));
				if (mouseHoverCheck()) {
					loc[0] = tempX;
					loc[1] = tempY;
				}
			}
			// wait(random(300,400));
			if (loc[0] == 0) // Checks 2 to the left
			{
				tempX = random(140, 150);
				tempY = random(130, 150);
				moveMouse(tempX, tempY, 0, 0);
				wait(random(300, 400));
				if (mouseHoverCheck()) {
					loc[0] = tempX;
					loc[1] = tempY;
				}
			}
		}

		return loc;
	}

	public void setTiles() {
		if (ivyLoc.equals("Yanille")) {
			ivy1 = new RSTile(2593, 3111);
			ivy2 = new RSTile(2592, 3111);
			ivy3 = new RSTile(2591, 3111);
		}
		if (ivyLoc.equals("Taverly")) {
			ivy1 = new RSTile(2943, 3419);
			ivy2 = new RSTile(2943, 3418);
			ivy3 = new RSTile(2943, 3417);
		}
		if (ivyLoc.equals("Varrock Palace")) {
			ivy1 = new RSTile(3233, 3461);
			ivy2 = new RSTile(3233, 3460);
			ivy3 = new RSTile(3233, 3459);
		}
		if (ivyLoc.equals("Castle Wars")) {
			ivy1 = new RSTile(2424, 3068);
			ivy2 = new RSTile(2425, 3068);
			ivy3 = new RSTile(2426, 3068);
		}
		if (ivyLoc.equals("Grand Exchange")) {
			ivy1 = new RSTile(3218, 3498);
			ivy2 = new RSTile(3217, 3498);
			ivy3 = new RSTile(3216, 3498);
		}
		if (ivyLoc.equals("North Falador")) {
			ivy1 = new RSTile(3017, 3392);
			ivy2 = new RSTile(3016, 3392);
			ivy3 = new RSTile(3015, 3392);
		}
		if (ivyLoc.equals("South Falador")) {
			ivy1 = new RSTile(3047, 3328);
			ivy2 = new RSTile(3048, 3328);
			ivy3 = new RSTile(3049, 3328);
		}
	}

	public int loop() {
		try {
			if (safety < 1) {
				setTiles();
				setCameraAltitude(false);
				safety = 1;
			}

			antiBan(random(1, 100));

			if (!isInventoryFull())
				pickUpNest();

			if (!chopCheck()) {
				chop(getNextIvy(random(1, 2)));
			}
		} catch (Exception e) {
		}
		return random(300, 500);
	}

	public void antiBan(int rand) {

		if (rand == 2)
			if (random(1, 8) == 2)
				moveMouseRandomly(random(400, 800));
		if (rand == 3)
			if (random(1, 8) == 2)
				moveMouseRandomly(random(200, 700));
		if (rand == 4) // THIS CHECKS THE WOODCUTTING STAT
			if (random(1, 8) == 2) {
				openTab(Constants.TAB_STATS);
				moveMouse(random(681, 690), random(365, 370), 0, 0);
				wait(random(900, 1000));
			}
		if (rand == 5) // THIS CLICKS THE XP BUTTON UNDER THE COMPASS
			if (random(1, 8) == 2) {
				moveMouse(random(527, 540), random(58, 65), 0, 0);
				clickMouse(true);
				moveMouseRandomly(random(20, 50));
				wait(random(900, 1000));
				moveMouse(random(527, 540), random(58, 65), 0, 0);
				clickMouse(true);
			}
		if (rand == 6) // THIS CHECKS CURRENT GOAL
			if (random(1, 8) == 2) {
				moveMouse(random(527, 543), random(471, 496), 0, 0);
				wait(random(300, 1000));
				moveMouse(random(604, 630), random(394, 399), 0, 0);
				wait(random(900, 1000));
			}

	}

	public void onRepaint(Graphics g) {
		if (isLoggedIn()) {
			if (startExp == 0) {
				startExp = skills.getCurrentSkillExp(STAT_WOODCUTTING);
				startLevel = skills.getCurrentSkillLevel(STAT_WOODCUTTING);
			}

			lvlsGained = skills.getCurrentSkillLevel(STAT_WOODCUTTING)
					- startLevel;
			expGained = skills.getCurrentSkillExp(STAT_WOODCUTTING) - startExp;

			// setting up the time
			long ms = System.currentTimeMillis() - startTime;
			double ms2 = System.currentTimeMillis() - startTimeDbl;
			long hours = ms / 3600000;
			ms = ms - (hours * 3600000);
			long minutes = ms / 60000;
			ms = ms - (minutes * 60000);
			long seconds = ms / 1000;
			// |
			// Background v transparency!
			g.setColor(new Color(0, 0, 0, 205));
			g.fillRoundRect(333, 175, 181, 164, 6, 6); // Background
			long expToLvl = skills.getXPToNextLevel(Skills.getStatIndex("woodcutting"));
			long time2Lvl = 0;
			long time2LvlHrs = 0;
			long time2LvlMins = 0;
			long time2LvlSec = 0;
			if (ms2 != 0 && expGained != 0) {
				time2Lvl = (long) (expToLvl / (expGained / (ms2 / 3600000)) * 3600000);
				time2LvlHrs = time2Lvl / 3600000;
				time2Lvl -= time2LvlHrs * 3600000;
				time2LvlMins = time2Lvl / 60000;
				time2Lvl -= time2LvlMins * 60000;
				time2LvlSec = time2Lvl / 1000;
			}
			// Text Color and Output
			g.setColor(new Color(255, 0, 0, 255));
			g.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			g.drawString("Conderoga's Ivy Chopper " + "v1.605", 338, 192);
			g.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
			g.drawString("Levels Gained: " + lvlsGained, 344, 207);
			g.drawString("Ivy Chopped: " + (int) ((expGained + 1) / 332.5),
					344, 222);
			g.drawString("Exp Gained: " + expGained, 344, 237);
			g.drawString("Time Running: " + hours + ":" + minutes + ":"
					+ seconds, 344, 252);

			// Progress Bar
			g.fillRoundRect(344, 256, 150, 20, 8, 8); // Bar background
			g.setColor(new Color(0, 255, 0, 255)); // GREEN
			g.fillRoundRect(344, 256, (int) (skills
					.getPercentToNextLevel(STAT_WOODCUTTING) * 1.5), 20, 8, 8);
			g.setColor(new Color(0, 0, 0, 255));
			g.setColor(new Color(255, 255, 255, 100));
			g.drawString(skills.getPercentToNextLevel(STAT_WOODCUTTING)
					+ "% to: "
					+ (skills.getCurrentSkillLevel(STAT_WOODCUTTING) + 1), 348,
					271);
			g.fillRoundRect(345, 266, 148, 10, 8, 8);
			g.setColor(new Color(0, 0, 0, 255));
			g.drawString(skills.getPercentToNextLevel(STAT_WOODCUTTING)
					+ "% to: "
					+ (skills.getCurrentSkillLevel(STAT_WOODCUTTING) + 1), 347,
					270);
			g.setColor(new Color(255, 0, 0, 255));
			if (ms2 != 0)
				g.drawString("Exp/Hr: " + (int) (expGained / (ms2 / 3600000)),
						344, 289);
			g.drawString("Status: " + status, 344, 304);
			g.drawString("Exp to Lvl: " + expToLvl, 344, 319);
			g.drawString("Est. Time to Lvl: " + time2LvlHrs + ":"
					+ time2LvlMins + ":" + time2LvlSec, 344, 334);
		}
	}

	public void onFinish() {
		log("Exp gained: " + expGained);
		log("Levels gained: " + lvlsGained);
		log("Thanks for using Conderoga's Ivy Chopper!");
	}

	public class IvyCutGUI extends JFrame {

		private static final long serialVersionUID = 1L;

		public IvyCutGUI() {
			initComponents();
		}

		private void button2ActionPerformed(ActionEvent e) {
			guiWait = false;
			guiExit = true;
			dispose();
		}

		private void button1ActionPerformed(ActionEvent e) {
			ivyLoc = comboBox1.getSelectedItem().toString();
			guiExit = false;
			guiWait = false;
			dispose();
		}

		private void initComponents() {
			// GEN-BEGIN:initComponents
			label1 = new JLabel();
			label2 = new JLabel();
			comboBox1 = new JComboBox();
			label3 = new JLabel();
			button1 = new JButton();
			button2 = new JButton();

			// ======== this ========
			setTitle("Conderoga's Ivy Chopper");
			setResizable(false);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ---- label1 ----
			label1.setText("Conderoga's Ivy Cutter Settings");
			label1.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
			contentPane.add(label1);
			label1.setBounds(10, -10, 380, 70);

			// ---- label2 ----
			label2.setText("Select the location: ");
			contentPane.add(label2);
			label2.setBounds(new Rectangle(new Point(15, 50), label2
					.getPreferredSize()));

			// ---- comboBox1 ----
			comboBox1.setMaximumRowCount(7);
			comboBox1.setModel(new DefaultComboBoxModel(new String[] {
					"Yanille", "Taverly", "Varrock Palace", "Grand Exchange",
					"Castle Wars", "North Falador", "South Falador"

			}));
			contentPane.add(comboBox1);
			comboBox1.setBounds(175, 45, 125, 25);

			// ---- label3 ----
			label3.setText("v1.605");
			contentPane.add(label3);
			label3.setBounds(15, 80, 124, label3.getPreferredSize().height);

			// ---- button1 ----
			button1.setText("Start!");
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button1ActionPerformed(e);
				}
			});
			contentPane.add(button1);
			button1.setBounds(40, 115, 75, 30);

			// ---- button2 ----
			button2.setText("Exit");
			button2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button2ActionPerformed(e);
				}
			});
			contentPane.add(button2);
			button2.setBounds(200, 115, 75, 30);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width,
							preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height,
							preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			setSize(325, 190);
			setLocationRelativeTo(getOwner());
			// GEN-END:initComponents
		}

		// GEN-BEGIN:variables
		private JLabel label1;
		private JLabel label2;
		private JComboBox comboBox1;
		private JLabel label3;
		private JButton button1;
		private JButton button2;
		// GEN-END:variables
	}

}