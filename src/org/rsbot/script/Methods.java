package org.rsbot.script;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.rsbot.accessors.ActionDataNode;
import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSAnimableNode;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.accessors.Settings;
import org.rsbot.bot.Bot;
import org.rsbot.bot.input.CanvasWrapper;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.internal.NodeList;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

/**
 * Methods that can be used by RSBot scripts.
 * <p/>
 * If you want to edit this methods file from the official RSBot download, then
 * please create an issue at: http://code.google.com/p/rsbotsvn/issues/list
 *
 * @author This is an open-source project, therefore there are too many to list.
 */
public class Methods implements Constants {

	public enum CHAT_MODE {
		VIEW, ON, FRIENDS, OFF, HIDE
	}

	private static final String[] COLOURS_STR = new String[]{"red", "green", "cyan", "purple", "white"};

	private static Pattern stripFormatting = Pattern.compile("\\<.+?\\>");

	/**
	 * The singleton of skills.
	 */
	public final Skills skills = new Skills(this);

	/**
	 * The singleton of bank
	 */
	public final Bank bank = new Bank(this);

	/**
	 * The singleton of store
	 */
	public final Store store = new Store(this);

	/**
	 * The singleton of Grand Exchange
	 */
	public final GrandExchange grandExchange = new GrandExchange();

	/**
	 * The reference to the Bot's input manager.
	 */
	public final InputManager input = Bot.getInputManager();

	/**
	 * The instance of {@link java.util.Random} for random number generation.
	 */
	private final java.util.Random random = new java.util.Random();

	/**
	 * The {@link java.util.logging.Logger} for this class.
	 */
	protected final Logger log = Logger.getLogger(this.getClass().getName());

	private ArrayList<WalkerNode> nodes = new ArrayList<WalkerNode>();

	private RSTile lastDoor = null;
	private boolean mapLoaded;
	private long lastTry = 0;
	private int tryCount = 0;

	private static final Map<String, Color> COLOR_MAP = new HashMap<String, Color>();

	// Menu items are cached after each frame
	private static final Object menuCacheLock = new Object();
	private static ArrayList<String> menuOptionsCache = new ArrayList<String>(0);
	private static ArrayList<String> menuActionsCache = new ArrayList<String>(0);
	private static boolean menuListenerStarted = false;

	/**
	 * Returns the distance between the two tiles.
	 *
	 * @param t1 The first tile.
	 * @param t2 The second tile.
	 * @return The distance between the tiles.
	 */
	public static int distanceBetween(RSTile t1, RSTile t2) {
		return (int) Math.hypot(t2.getX() - t1.getX(), t2.getY() - t1.getY());
	}

	/**
	 * Draws a line on the screen at the specified index. Default is green.
	 * <p/>
	 * Available colours: red, green, cyan, purple, white.
	 *
	 * @param render The Graphics object to be used.
	 * @param row	The index where you want the text.
	 * @param text   The text you want to render. Colours can be set like [red].
	 */
	public static void drawLine(Graphics render, int row, String text) {
		FontMetrics metrics = render.getFontMetrics();
		int height = metrics.getHeight() + 4; // height + gap
		int y = row * height + 15 + 19;
		String[] texts = text.split("\\[");
		int xIdx = 7;
		Color cur = Color.GREEN;
		for (String t : texts) {
			for (@SuppressWarnings("unused") String element : COLOURS_STR) {
				// String element = COLOURS_STR[i];
				// Don't search for a starting '[' cause it they don't exists.
				// we split on that.
				int endIdx = t.indexOf(']');
				if (endIdx != -1) {
					String colorName = t.substring(0, endIdx);
					if (COLOR_MAP.containsKey(colorName)) {
						cur = COLOR_MAP.get(colorName);
					} else {
						try {
							Field f = Color.class.getField(colorName);
							int mods = f.getModifiers();
							if (Modifier.isPublic(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods)) {
								cur = (Color) f.get(null);
								COLOR_MAP.put(colorName, cur);
							}
						} catch (Exception ignored) {
						}
					}
					t = t.replace(colorName + "]", "");
				}
			}
			render.setColor(Color.BLACK);
			render.drawString(t, xIdx, y + 1);
			render.setColor(cur);
			render.drawString(t, xIdx, y);
			xIdx += metrics.stringWidth(t);
		}
	}

	/**
	 * Removes HTML tags.
	 *
	 * @param input The string you want to parse.
	 * @return The parsed {@code String}.
	 */
	public static String stripFomatting(String input) {
		return stripFormatting.matcher(input).replaceAll("");
	}

	/**
	 * Checks if the player's animation is one of the IDs sent.
	 *
	 * @param ids The animation IDs to check.
	 * @return <tt>true</tt> if the animation is in the array; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean animationIs(int... ids) {
		for (int id : ids) {
			if (getMyPlayer().getAnimation() == id)
				return true;
		}
		return false;
	}

	/**
	 * Searches a String array to see if it contains a search String.
	 *
	 * @param items		The {@code String} array to check.
	 * @param searchString The {@code String} to search for.
	 * @return <tt>true</tt> if the searchString appears in items; otherwise
	 *         <tt>false</tt>.
	 */
	private boolean arrayContains(String[] items, String searchString) {
		for (String item : items) {
			if (item.equalsIgnoreCase(searchString))
				return true;
		}
		return false;
	}

	/**
	 * Clicks the component and then the action in the menu.
	 *
	 * @param ChildInterface The child containing the component.
	 * @param ComponentID	The component to be clicked.
	 * @param action		 The menu action to be done.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>.
	 */
	public boolean atComponent(RSInterfaceChild ChildInterface, int ComponentID, String action) {
		return clickRSComponent(ChildInterface, ComponentID, false) && atMenu(action);
	}

	/**
	 * Clicks the component and then the action in the menu.
	 *
	 * @param comp   The component you are going to click.
	 * @param action The menu action to be done.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>.
	 */
	public boolean atComponent(RSInterfaceComponent comp, String action) {
		return clickRSComponent(comp, false) && atMenu(action);
	}

	/**
	 * Opens a door given the ID and the direction in which the door is facing.
	 *
	 * @param id		The object ID of the door.
	 * @param direction The direction the door is on the tile.
	 * @return <tt>true</tt> if the door was the door was opened; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean atDoor(int id, char direction) {
		RSObject theDoor = getNearestObjectByID(id);
		if (theDoor == null)
			return false;
		RSTile location = theDoor.getLocation();
		int x = location.getX(), y = location.getY();
		boolean fail = false;
		switch (direction) {
			case 'N':
			case 'n':
				y++;
				break;
			case 'W':
			case 'w':
				x--;
				break;
			case 'E':
			case 'e':
				x++;
				break;
			case 'S':
			case 's':
				y--;
				break;
			default:
				fail = true;
		}
		if (fail)
			throw new IllegalArgumentException();
		return atDoorTiles(location, new RSTile(x, y));
	}

	public boolean atDoorTiles(RSTile a, RSTile b) {
		if (a != lastDoor) {
			lastTry = 0;
			tryCount = 0;
			lastDoor = a;
		}
		tryCount++;
		if (System.currentTimeMillis() - lastTry > random(20000, 40000)) {
			tryCount = 1;
		}
		lastTry = System.currentTimeMillis();
		if (tryCount > 4) {
			if (random(0, 10) < random(2, 4)) {
				setCameraRotation(getCameraAngle() + (random(0, 9) < random(6, 8) ? random(-20, 20) : random(-360, 360)));
			}
			if (random(0, 14) < random(0, 2)) {
				setCameraAltitude(random(0, 100));
			}
		}
		if (tryCount > 100) {
			log("Problems finding door....");
			stopScript();
		}
		if (!tileOnScreen(a) || !tileOnScreen(b) || distanceTo(a) > random(4, 7)) {
			if (tileOnMap(a)) {
				walkTo(randomizeTile(a, 3, 3));
				wait(random(750, 1250));
			} else {
				log("Cannot find door tiles...");
				return false;
			}
		} else {
			ArrayList<RSTile> theObjs = new ArrayList<RSTile>();
			theObjs.add(a);
			theObjs.add(b);
			try {
				Point[] thePoints = new Point[theObjs.size()];
				for (int c = 0; c < theObjs.size(); c++) {
					thePoints[c] = Calculations.tileToScreen(theObjs.get(c));
				}
				float xTotal = 0;
				float yTotal = 0;
				for (Point thePoint : thePoints) {
					xTotal += thePoint.getX();
					yTotal += thePoint.getY();
				}
				Point location = new Point((int) (xTotal / thePoints.length), (int) (yTotal / thePoints.length) - random(0, 40));
				if (location.x == -1 || location.y == -1)
					return false;
				if (Math.sqrt(Math.pow((getMouseLocation().getX() - location.getX()), 2) + Math.pow((getMouseLocation().getY() - location.getY()), 2)) < random(20, 30)) {
					ArrayList<String> commands = getMenuItems();
					for (String command : commands) {
						if (command.contains("Open")) {
							if (atMenu("Open")) {
								lastTry = 0;
								tryCount = 0;
								return true;
							}
						}
					}
				}
				moveMouse(location, 7, 7);
				if (atMenu("Open")) {
					lastTry = 0;
					tryCount = 0;
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean atDropMenu(String option, int column) {
		int idx = getMenuIndex(option);
		if (!isMenuOpen()) {
			if (idx == -1)
				return false;
			if (idx == 0) {
				clickMouse(true);
			} else {
				clickMouse(false);
				atMenuItem(idx);
			}
			return true;
		} else {
			if (idx == -1) {
				while (isMenuOpen()) {
					moveMouseRandomly(750);
					wait(random(100, 500));
				}
				return false;
			} else {
				atDropMenuItem(idx, column);
				return true;
			}
		}
	}

	public boolean atDropMenuItem(int i, int column) {
		if (!isMenuOpen())
			return false;
		try {
			RSTile menu = getMenuLocation();
			List<String> items = getMenuItems();
			int longest = 0;
			for (String s : items) {
				if (s.length() > longest) {
					longest = s.length();
				}
			}
			int xOff = 563 + column * 42 + random(0, 32);
			int yOff = random(21, 29) + 15 * i;
			clickMouse(xOff, menu.getY() + yOff, 2, 2, true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Performs an action on a given equipped item ID by clicking it in the
	 * equipment tab. Written by Garrett.
	 *
	 * @param itemID The ID of the item to look for.
	 * @param action The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 */
	public boolean atEquippedItem(int itemID, String action) {
		if (getCurrentTab() != TAB_EQUIPMENT) {
			openTab(TAB_EQUIPMENT);
			wait(random(900, 1500));
		}
		RSInterfaceChild[] equip = getEquipmentInterface().getChildren();
		for (int i = 0; i < 11; i++) {
			if (equip[i * 3 + 8].getComponentID() == itemID) {
				int x = equip[i * 3 + 8].getAbsoluteX() + 2;
				int y = equip[i * 3 + 8].getAbsoluteY() + 2;
				int width = equip[i * 3 + 8].getWidth() - 2;
				int height = equip[i * 3 + 8].getHeight() - 2;
				moveMouse(new Point(random(x, x + width), random(y, y + height)));
				wait(random(50, 100));
				return atMenu(action);
			}
		}
		return false;
	}

	/**
	 * Left-clicks the child interface with the given parent ID and child ID if
	 * it is showing (valid).
	 *
	 * @param iface The parent interface ID.
	 * @param child The child interface ID.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 * @see #atInterface(RSInterfaceChild, String)
	 * @see #atInterface(int, int, String)
	 */
	public boolean atInterface(int iface, int child) {
		return atInterface(RSInterface.getChildInterface(iface, child));
	}

	/**
	 * Performs the provided action on the child interface with the given parent
	 * ID and child ID if it is showing (valid).
	 *
	 * @param iface		  The parent interface ID.
	 * @param child		  The child interface ID.
	 * @param actionContains The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 * @see #atInterface(RSInterfaceChild, String)
	 * @see #atInterface(int, int)
	 */
	public boolean atInterface(int iface, int child, String actionContains) {
		return atInterface(RSInterface.getChildInterface(iface, child), actionContains);
	}

	/**
	 * Left-clicks the provided RSInterfaceChild if it is showing (valid).
	 *
	 * @param i The child interface to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 * @see #atInterface(RSInterfaceChild, String)
	 */
	public boolean atInterface(RSInterfaceChild i) {
		return atInterface(i, true);
	}

	public boolean atInterface(RSInterfaceChild i, boolean leftClick) {
		if (!i.isValid())
			return false;
		Rectangle pos = i.getArea();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1)
			return false;
		Point p = getMouseLocation();
		int px = (int) p.getX();
		int py = (int) p.getY();
		if (px > pos.x && px < pos.x + pos.width && py > pos.y && py < pos.y + pos.height) {
			clickMouse(true);
			return true;
		}
		// zzSleepzz - Base the randomization on the center of the area rather
		// than the upper left edge. This provides more room for lag-induced
		// mousing errors.
		int dx = (int) (pos.getWidth() - 4) / 2;
		int dy = (int) (pos.getHeight() - 4) / 2;
		int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
		clickMouse(midx + random(-dx, dx), midy + random(-dy, dy), leftClick);
		return true;
	}

	/**
	 * Performs the given action on the provided RSInterfaceChild if it is
	 * showing (valid).
	 *
	 * @param i			  The child interface to click.
	 * @param actionContains The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 * @see #atInterface(RSInterfaceChild)
	 * @see #atInterface(int, int, String)
	 */
	public boolean atInterface(RSInterfaceChild i, String actionContains) {
		if (!i.isValid())
			return false;
		Rectangle pos = i.getArea();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1)
			return false;
		int dx = (int) (pos.getWidth() - 4) / 2;
		int dy = (int) (pos.getHeight() - 4) / 2;
		int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
		moveMouse(midx + random(-dx, dx), midy + random(-dy, dy));
		wait(random(50, 60));
		return atMenu(actionContains);
	}

	/**
	 * Performs the provided action on a random inventory item with the given
	 * ID.
	 *
	 * @param itemID The ID of the item to look for.
	 * @param option The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 */
	public boolean atInventoryItem(int itemID, String option) {
		try {
			if (getCurrentTab() != TAB_INVENTORY && !RSInterface.getInterface(INTERFACE_BANK).isValid() && !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
				openTab(TAB_INVENTORY);
			}

			RSInterfaceChild inventory = getInventoryInterface();
			if (inventory == null || inventory.getComponents() == null)
				return false;

			java.util.List<RSInterfaceComponent> possible = new ArrayList<RSInterfaceComponent>();
			for (RSInterfaceComponent item : inventory.getComponents()) {
				if (item != null && item.getComponentID() == itemID) {
					possible.add(item);
				}
			}

			if (possible.size() == 0)
				return false;

			RSInterfaceComponent item = possible.get(random(0, Math.min(2, possible.size())));
			return atInterface(item, option);
		} catch (Exception e) {
			log("atInventoryItem(int itemID, String option) Error: " + e);
			return false;
		}
	}

	/**
	 * Clicks the menu option. Will left-click if the menu item is the first,
	 * otherwise open menu and click the option.
	 *
	 * @param optionContains The option to click.
	 * @return <tt>true</tt> if the menu item was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean atMenu(String optionContains) {
		int idx = getMenuIndex(optionContains);
		if (!isMenuOpen()) {
			if (idx == -1)
				return false;
			if (idx == 0) {
				clickMouse(true);
			} else {
				clickMouse(false);
				atMenuItem(idx);
			}
			return true;
		} else {
			if (idx == -1) {
				while (isMenuOpen()) {
					moveMouseRandomly(750);
					wait(random(100, 500));
				}
				return false;
			} else {
				atMenuItem(idx);
				return true;
			}
		}
	}

	/**
	 * Search a menu for multiple Strings and click the first occurrence that is
	 * found.
	 *
	 * @param items The Strings to search the menu for.
	 * @return <tt>true</tt> if the menu item was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean atMenu(String[] items) {
		for (String target : getMenuItems()) {
			if (arrayContains(items, target))
				return atMenu(target);
		}
		while (Bot.getClient().isMenuOpen()) {
			moveMouseSlightly();
			wait(random(500, 1000));
		}
		return false;
	}

	/**
	 * Left clicks at the given index.
	 *
	 * @param i The index of the item.
	 * @return <tt>true</tt> if the mouse was clicked; otherwise <tt>false</tt>.
	 */
	public boolean atMenuItem(int i) {
		if (!isMenuOpen())
			return false;
		try {
			ArrayList<String> items = getMenuItems();
			if (items.size() <= i)
				return false;
			RSTile menu = getMenuLocation();
			int xOff = random(4, items.get(i).length() * 4);
			int yOff = 21 + 15 * i + random(3, 12);
			moveMouse(menu.getX() + xOff, menu.getY() + yOff, 2, 2);
			if (!isMenuOpen())
				return false;
			clickMouse(true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Clicks a humanoid character (tall and skinny) without any randomly
	 * generated mousepaths.
	 *
	 * @param someNPC The RSNPC to be clicked.
	 * @param option  The option to be clicked (If available).
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 * @see #atNPC(RSNPC, String, boolean)
	 */
	public boolean atNPC(RSNPC someNPC, String option) {
		return atNPC(someNPC, option, false);
	}

	/**
	 * Clicks a humanoid character (tall and skinny).
	 *
	 * @param someNPC   The RSNPC to be clicked.
	 * @param option	The option to be clicked (If available).
	 * @param mousepath Whether or not to use {@link #moveMouseByPath(Point)} rather
	 *                  than {@link #moveMouse(Point)}.
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 * @see #moveMouseByPath(Point)
	 * @see #atMenu(String)
	 */
	public boolean atNPC(RSNPC someNPC, String option, boolean mousepath) {
		for (int i = 0; i < 20; i++) {
			if (someNPC == null || !pointOnScreen(someNPC.getScreenLocation()) || !tileOnScreen(someNPC.getLocation()))
				return false;
			if (!mousepath) {
				moveMouse(new Point((int) Math.round(someNPC.getScreenLocation().getX()) + random(-5, 5), (int) Math.round(someNPC.getScreenLocation().getY()) + random(-5, 5)));
			} else {
				moveMouseByPath(new Point((int) Math.round(someNPC.getScreenLocation().getX()) + random(-5, 5), (int) Math.round(someNPC.getScreenLocation().getY()) + random(-5, 5)));
			}
			if (getMenuItems().get(0).toLowerCase().contains(option.toLowerCase())) {
				clickMouse(true);
				return true;
			} else {
				ArrayList<String> menuItems = getMenuItems();
				for (String item : menuItems) {
					if (item.toLowerCase().contains(option.toLowerCase())) {
						clickMouse(false);
						return atMenu(option);
					}
				}
			}
		}
		return false;
	}

	public boolean atObject(RSObject object, String action) {
		return atTile(object.getLocation(), action);
	}

	/**
	 * Clicks on the player with specified action Walks to the player if not on
	 * screen.
	 *
	 * @param character The RSCharacter you want to click.
	 * @param action	Action command to use on the Character (e.g "Attack" or
	 *                  "Trade").
	 * @return <tt>true</tt> if the Character was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean atPlayer(RSCharacter character, String action) {
		if (character == null)
			return false;
		RSTile tile = character.getLocation();
		if (!tile.isValid())
			return false;
		if (distanceTo(tile) > 5) {
			walkTo(tile);
		}
		return clickCharacter(character, action);
	}

	public boolean atTile(RSTile tile, int h, double xd, double yd, String action) {
		try {
			Point location = Calculations.tileToScreen(tile.getX(), tile.getY(), xd, yd, h);
			if (location.x == -1 || location.y == -1)
				return false;
			moveMouse(location, 3, 3);
			if (getMenuItems().get(0).toLowerCase().contains(action.toLowerCase())) {
				clickMouse(true);
			} else {
				clickMouse(false);
				if (!atMenu(action))
					return false;
			}
			wait(random(500, 1000));
			while (true) {
				if (!getMyPlayer().isMoving()) {
					break;
				}
				wait(random(500, 1000));
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean atTile(RSTile tile, String action) {
		return atTile(tile, action, false);
	}

	/**
	 * Clicks a tile if it is on screen. It will left-click if the action is
	 * available as the default option, otherwise it will right-click and check
	 * for the action in the context menu.
	 *
	 * @param tile	  The RSTile that you want to click.
	 * @param action	Action command to use on the Character (e.g "Attack" or
	 *                  "Trade").
	 * @param mousePath Whether or not you want it to move the mouse using
	 *                  {@link #moveMouseByPath(Point)}.
	 * @return <tt>true</tt> if the Character was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean atTile(RSTile tile, String action, boolean mousePath) {
		try {
			int counter = 0;
			try {
				Point location = Calculations.tileToScreen(tile);
				if (location.x == -1 || location.y == -1)
					return false;
				if (!mousePath) {
					moveMouse(location, 5, 5);
				} else {
					moveMouseByPath(location, 5, 5);
				}
				while (!getMenuItems().get(0).toLowerCase().contains(action.toLowerCase()) && counter < 5) {
					location = Calculations.tileToScreen(tile);
					moveMouse(location, 5, 5);
					counter++;
				}
				if (getMenuItems().get(0).toLowerCase().contains(action.toLowerCase())) {
					clickMouse(true);
				} else {
					clickMouse(false);
					atMenu(action);
				}
				return true;
			} catch (Exception e) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * Clicks a tree object.
	 */

	public boolean atTree(RSObject tree, String action) {
		RSTile loc1 = tree.getLocation();
		RSTile loc4 = new RSTile(loc1.getX() + 1, loc1.getY() + 1);
		Point sloc1 = Calculations.tileToScreen(loc1.getX(), loc1.getY(), 10);
		Point sloc2 = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
		Point screenLoc = new Point((sloc1.x + sloc2.x) / 2, (sloc1.y + sloc2.y) / 2);
		if (screenLoc.x == -1 || screenLoc.y == -1)
			return false;
		moveMouse(screenLoc, 3, 3);
		return atMenu(action);
	}

	/**
	 * Autocasts a spell via interfaces. Written by: Mouchicc
	 *
	 * @param spell The spell to cast.
	 * @return <tt>true</tt> if the "Autocast" interface option was clicked;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean autoCastSpell(int spell) {
		if (getSetting(43) != 4) {
			if (getCurrentTab() != TAB_MAGIC) {
				openTab(TAB_MAGIC);
				wait(random(150, 250));
			}
			return atInterface(INTERFACE_TAB_MAGIC, spell, "Autocast");
		}
		return false;
	}

	public double calculateDistance(RSTile curr, RSTile dest) {
		return Math.sqrt((curr.getX() - dest.getX()) * (curr.getX() - dest.getX()) + (curr.getY() - dest.getY()) * (curr.getY() - dest.getY()));
	}

	public boolean canContinue() {
		return getContinueChildInterface() != null;
	}

	public boolean canReach(Object obj, boolean isObject) {
		if (obj instanceof RSCharacter)
			return Calculations.canReach(((RSCharacter) obj).getLocation(), isObject);
		else if (obj instanceof RSTile)
			return Calculations.canReach((RSTile) obj, isObject);
		else if (obj instanceof RSObject)
			return Calculations.canReach(((RSObject) obj).getLocation(), isObject);
		else if (obj instanceof Point)
			return Calculations.canReach(new RSTile(((Point) obj).x, ((Point) obj).y), isObject);
		return false; // Couldn't recognize object
	}

	/**
	 * Clicks a specified spell, opens magic tab if not open and uses interface
	 * of the spell to click it, so it works if the spells are layout in any
	 * sway.
	 *
	 * @param spell Spell number. This can be found in (e.g SPELL_HOME_TELEPORT).
	 * @return <tt>true</tt> if the spell was clicked; otherwise <tt>false</tt>.
	 */
	public boolean castSpell(int spell) {
		if (getCurrentTab() != TAB_MAGIC) {
			openTab(TAB_MAGIC);
			for (int i = 0; i < 100; i++) { // waits for up to 2 secs
				wait(20);
				if (getCurrentTab() == TAB_MAGIC) {
					break;
				}
			}
			wait(random(150, 250));
		}
		return getCurrentTab() == TAB_MAGIC && atInterface(INTERFACE_TAB_MAGIC, spell);
	}

	/**
	 * This method will remove any duplicates tiles in a RSTile[] path. This is
	 * preferably to be used with generateFixedPath. For instance:
	 * walkPathMM(cleanPath(generatedFixedPath(tile)));
	 * <p/>
	 * Written by: Taha
	 *
	 * @param path The messy RSTile[] path with duplicate tiles.
	 * @return The cleaned RSTile[] path with no duplicate tiles.
	 */
	public RSTile[] cleanPath(RSTile[] path) {
		ArrayList<RSTile> tempPath = new ArrayList<RSTile>();
		for (int i = 0; i < path.length; i++)
			if (!tempPath.contains(path[i])) {
				tempPath.add(path[i]);
			}
		RSTile[] cleanedPath = new RSTile[tempPath.size()];
		for (int i = 0; i < tempPath.size(); i++) {
			cleanedPath[i] = tempPath.get(i);
		}
		return cleanedPath;
	}

	/**
	 * Searches the game screen for the RSCharacter by checking the menu list.
	 * Clicks the Character if the menu option was found.
	 *
	 * @param c	  The RSCharacter you want to click.
	 * @param action Action command to use on the Character (e.g "Attack" or
	 *               "Trade").
	 * @return true if the Character was clicked.
	 */
	public boolean clickCharacter(RSCharacter c, String action) {
		try {
			Point screenLoc;
			for (int i = 0; i < 20; i++) {
				screenLoc = c.getScreenLocation();
				if (!c.isValid() || !pointOnScreen(screenLoc)) {
					log.info("Not on screen " + action);
					return false;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
				moveMouse(screenLoc);
			}
			screenLoc = c.getScreenLocation();
			if (!getMouseLocation().equals(screenLoc))
				return false;
			List<String> items = getMenuItems();
			if (items.size() <= 1)
				return false;
			if (items.get(0).toLowerCase().contains(action.toLowerCase())) {
				clickMouse(screenLoc, true);
				return true;
			} else {
				clickMouse(screenLoc, false);
				return atMenu(action);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean clickContinue() {
		RSInterfaceChild cont = getContinueChildInterface();
		return !(cont != null && cont.isValid()) || atInterface(cont);
	}

	public void clickMouse(boolean leftClick) {
		clickMouse(leftClick, MouseHandler.DEFAULT_MAX_MOVE_AFTER);
	}

	public void clickMouse(boolean leftClick, int moveAfterDist) {
		input.clickMouse(leftClick);
		if (moveAfterDist > 0) {
			wait(random(60, 450));
			Point pos = getMouseLocation();
			moveMouse(pos.x - moveAfterDist, pos.y - moveAfterDist, moveAfterDist * 2, moveAfterDist * 2);
		}
	}

	public void clickMouse(int x, int y, boolean leftClick) {
		clickMouse(x, y, 0, 0, leftClick);
	}

	public void clickMouse(int x, int y, int width, int height, boolean leftClick) {
		moveMouse(x, y, width, height);
		wait(random(60, 450));
		clickMouse(leftClick, MouseHandler.DEFAULT_MAX_MOVE_AFTER);
	}

	public void clickMouse(int x, int y, int width, int height, boolean leftClick, int moveAfterDist) {
		moveMouse(x, y, width, height);
		wait(random(60, 450));
		clickMouse(leftClick, moveAfterDist);
	}

	public void clickMouse(Point p, boolean leftClick) {
		clickMouse(p.x, p.y, leftClick);
	}

	public void clickMouse(Point p, int x2, int y2, boolean leftClick) {
		clickMouse(p.x, p.y, x2, y2, leftClick);
	}

	public void clickMouse(Point p, int x2, int y2, boolean leftClick, int moveAfterDist) {
		clickMouse(p.x, p.y, x2, y2, leftClick, moveAfterDist);
	}

	/**
	 * Clicks an RSComponent.
	 *
	 * @param ChildInterface The child containing the component.
	 * @param ComponentID	The component to be clicked.
	 * @param leftclick	  true to left-click, false to right-click.
	 * @return true if it successfully clicked the component.
	 */
	public boolean clickRSComponent(RSInterfaceChild ChildInterface, int ComponentID, boolean leftclick) {
		if (!ChildInterface.isValid())
			return false;
		RSInterfaceComponent[] comps = ChildInterface.getComponents();
		if (comps.length == 0 || comps.length < ComponentID - 1)
			return false;
		RSInterfaceComponent comp = comps[ComponentID];
		if (comp == null || comp.getPoint() == null || comp.getPoint().x == -1 || comp.getPoint().y == -1)
			return false;
		clickMouse(new Point(comp.getPoint().x + random(-5, 6), comp.getPoint().y + random(-5, 6)), leftclick);
		return true;
	}

	/**
	 * Clicks an RSComponent.
	 *
	 * @param comp	  The component you are going to click
	 * @param leftclick true to left click; false to right click.
	 * @return true if the component was clicked.
	 */
	public boolean clickRSComponent(RSInterfaceComponent comp, boolean leftclick) {

		if (comp == null || comp.getPoint() == null || comp.getPoint().x == -1 || comp.getPoint().y == -1)
			return false;

		clickMouse(new Point(comp.getPoint().x + random(-5, 6), comp.getPoint().y + random(-5, 6)), leftclick);
		return true;
	}

	/**
	 * Attempts to click all the components.
	 *
	 * @param leftclick  true to left-click; false to right click.
	 * @param components All the components to be clicked.
	 * @return <tt>true</tt> if all components were clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean clickRSComponents(boolean leftclick, RSInterfaceComponent... components) {
		boolean allSuccessful = true;
		for (RSInterfaceComponent comp : components) {
			if (!comp.isValid())
				return false;
			if (comp.getPoint() == null || comp.getPoint().x == -1 || comp.getPoint().y == -1)
				return false;
			if (!clickRSComponent(comp, leftclick)) {
				allSuccessful = false;
			}
		}
		return allSuccessful;
	}

	/**
	 * Searches the RS game screen for the NPC by checking the menu list Clicks
	 * NPC once found
	 *
	 * @param npc	The RSNPC you want to click.
	 * @param action Action command to use on the NPC (e.g "Attack" or "Talk").
	 * @return <tt>true</tt> if the NPC was clicked; otherwise <tt>false</tt>.
	 */
	public boolean clickRSNPC(RSNPC npc, String action) {
		return clickRSNPC(npc, action, null);
	}

	/**
	 * Searches the RS game screen for the NPC by checking the menu list.
	 * Performs the provided action on the NPC once found.
	 *
	 * @param npc	The RSNPC you want to click.
	 * @param action Action command to use on the NPC (e.g "Attack" or "Talk").
	 * @param name   The name of the NPC.
	 * @return <tt>true</tt> if the NPC was clicked; otherwise <tt>false</tt>.
	 */
	public boolean clickRSNPC(RSNPC npc, String action, String name) {
		try {
			int a;
			String fullCommand = action + " " + (name == null ? npc.getName() : name);
			for (a = 10; a-- >= 0;) {
				List<String> menuItems = getMenuItems();
				if (menuItems.size() > 1) {
					if (listContainsString(menuItems, fullCommand)) {
						if (menuItems.get(0).contains(fullCommand)) {
							clickMouse(true);
							return true;
						} else
							return atMenu(fullCommand);
					}
				}
				Point screenLoc = npc.getScreenLocation();
				if (!pointOnScreen(screenLoc))
					return false;
				moveMouse(screenLoc);
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private int distance(WalkerNode startNode, int endX, int endY) {
		int dx = startNode.x - endX;
		int dy = startNode.y - endY;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	public int distanceTo(RSCharacter c) {
		return c == null ? -1 : distanceTo(c.getLocation());
	}

	public int distanceTo(RSObject o) {
		return o == null ? -1 : distanceTo(o.getLocation());
	}

	public int distanceTo(RSTile t) {
		return t == null ? -1 : distanceBetween(getMyPlayer().getLocation(), t);
	}

	/**
	 * Drag the mouse from the current position to a certain other position.
	 *
	 * @param x The x coordinate to drag to.
	 * @param y The y coordinate to drag to.
	 */
	public void dragMouse(int x, int y) {
		input.dragMouse(x, y);
	}

	/**
	 * Drag the mouse from the current position to a certain other position.
	 *
	 * @param p The point to drag to.
	 */
	public void dragMouse(Point p) {
		input.dragMouse(p.x, p.y);
	}

	public void dropAllExcept(boolean leftToRight, int... items) {
		while (!inventoryEmptyExcept(items)) {
			if (!leftToRight) {
				for (int c = 0; c < 4; c++) {
					for (int r = 0; r < 7; r++) {
						boolean found = false;
						for (int i = 0; i < items.length && !found; i++) {
							found = items[i] == getInventoryArray()[c + r * 4];
						}
						if (!found) {
							dropItem(c, r);
						}
					}
				}
			} else {
				for (int r = 0; r < 7; r++) {
					for (int c = 0; c < 4; c++) {
						boolean found = false;
						for (int i = 0; i < items.length && !found; i++) {
							found = items[i] == getInventoryArray()[c + r * 4];
						}
						if (!found) {
							dropItem(c, r);
						}
					}
				}
			}
			wait(random(500, 800));
		}
	}

	public boolean dropAllExcept(int... items) {
		dropAllExcept(false, items);
		return true;
	}

	public void dropItem(int col, int row) {
		if (RSInterface.getInterface(210).getChild(2).getText().equals("Click here to continue")) {
			wait(random(800, 1300));
			if (RSInterface.getInterface(210).getChild(2).getText().equals("Click here to continue")) {
				atInterface(RSInterface.getInterface(210).getChild(2));
				wait(random(150, 200));
			}
		}
		if (getCurrentTab() != TAB_INVENTORY && !RSInterface.getInterface(INTERFACE_BANK).isValid() && !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
			openTab(TAB_INVENTORY);
		}
		if (col < 0 || col > 3 || row < 0 || row > 6)
			return;
		if (getInventoryArray()[col + row * 4] == -1)
			return;
		Point p;
		p = getMouseLocation();
		if (p.x < 563 + col * 42 || p.x >= 563 + col * 42 + 32 || p.y < 213 + row * 36 || p.y >= 213 + row * 36 + 32) {
			//moveMouse(563 + col * 42 + random(0, 32), 213 + row * 36 + random(0, 32));
			moveMouse(getInventoryInterface().getComponents()[row * 4 + col].getPoint(), 10, 10);
		}
		clickMouse(false);
		wait(random(10, 25));
		atDropMenu("drop", col);
		wait(random(25, 50));
	}

	public boolean equipmentContains(int... itemID) {
		int[] equipID = getEquipmentArray();
		int count = 0;
		for (int item : itemID) {
			for (int equip : equipID) {
				if (equip == item) {
					count++;
					break;
				}
			}
		}
		return count == itemID.length;
	}

	/**
	 * Checks if the player has one (or more) of the given items equipped.
	 *
	 * @param items The IDs of items to check for.
	 * @return <tt>true</tt> if the player has one (or more) of the given items
	 *         equipped; otherwise <tt>false</tt>.
	 */
	public boolean equipmentContainsOneOf(int... items) {
		for (int item : getEquipmentArray()) {
			for (int id : items) {
				if (item == id)
					return true;
			}
		}
		return false;
	}

	/**
	 * @deprecated Please use getNearestObjectByID/getNearestObjectByName
	 *             instead.
	 */
	@Deprecated
	public RSObject findObject(int... ids) {
		RSObject cur = null;
		int dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSObject o = getObjectAt(x + Bot.getClient().getBaseX(), y + Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (int id : ids) {
						if (o.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						int distTmp = getRealDistanceTo(o.getLocation(), true);
						if (distTmp != -1) {
							if (cur == null) {
								dist = distTmp;
								cur = o;
							} else if (distTmp < dist) {
								cur = o;
								dist = distTmp;
							}
						}
					}
				}
			}
		}
		return cur;
	}

	/**
	 * @deprecated Please use getNearestObjectByID/getNearestObjectByName
	 *             instead.
	 */
	@Deprecated
	public RSObject findObject(int range, int id) {
		int minX = getMyPlayer().getLocation().getX() - range;
		int minY = getMyPlayer().getLocation().getY() - range;
		int maxX = getMyPlayer().getLocation().getX() + range;
		int maxY = getMyPlayer().getLocation().getY() + range;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				RSObject o = getObjectAt(x, y);
				if (o != null) {
					if (o.getID() == id)
						return o;
				}
			}
		}
		return null;
	}

	/**
	 * @deprecated Please use getNearestObjectByID/getNearestObjectByName
	 *             instead.
	 */
	@Deprecated
	public RSObject findObject(int range, int[] ids) {
		int minX = getMyPlayer().getLocation().getX() - range;
		int minY = getMyPlayer().getLocation().getY() - range;
		int maxX = getMyPlayer().getLocation().getX() + range;
		int maxY = getMyPlayer().getLocation().getY() + range;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				RSObject o = getObjectAt(x, y);
				if (o != null) {
					for (int id : ids) {
						if (o.getID() == id)
							return o;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @deprecated Please use getNearestObjectByID/getNearestObjectByName
	 *             instead.
	 */
	@Deprecated
	public RSObject findObjectByName(String... names) {
		RSObject cur = null;
		int dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSObject o = getObjectAt(x + Bot.getClient().getBaseX(), y + Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (String name : names) {
						if (o.getDef().getName().toLowerCase().contains(name.toLowerCase())) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						int distTmp = getRealDistanceTo(o.getLocation(), true);
						if (distTmp != -1) {
							if (cur == null) {
								dist = distTmp;
								cur = o;
							} else if (distTmp < dist) {
								cur = o;
								dist = distTmp;
							}
						}
					}
				}
			}
		}
		return cur;
	}

	private WalkerNode[] findPath(WalkerNode startNode, WalkerNode endNode) {
		if (!mapLoaded) {
			loadMap();
		}
		try {
			ArrayList<WalkerNode> Q = new ArrayList<WalkerNode>();
			for (WalkerNode thisNode : nodes) {
				thisNode.distance = Integer.MAX_VALUE;
				thisNode.previous = null;
				Q.add(thisNode);
			}
			startNode.distance = 0;
			while (!Q.isEmpty()) {
				WalkerNode nearestNode = Q.get(0);
				for (WalkerNode thisNode : Q) {
					if (thisNode.distance < nearestNode.distance) {
						nearestNode = thisNode;
					}
				}
				Q.remove(Q.indexOf(nearestNode));
				if (nearestNode == endNode) {
					break;
				} else {
					for (WalkerNode neighbourNode : nearestNode.neighbours) {
						int alt = nearestNode.distance + nearestNode.distance(neighbourNode);
						if (alt < neighbourNode.distance) {
							neighbourNode.distance = alt;
							neighbourNode.previous = nearestNode;
						}
					}
				}
			}
			ArrayList<WalkerNode> nodePath = new ArrayList<WalkerNode>();
			nodePath.add(endNode);
			WalkerNode previousNode = endNode.previous;
			while (previousNode != null) {
				nodePath.add(previousNode);
				previousNode = previousNode.previous;
			}
			if (nodePath.size() == 1)
				return null;
			WalkerNode[] nodeArray = new WalkerNode[nodePath.size()];
			for (int i = nodePath.size() - 1; i >= 0; i--) {
				nodeArray[nodePath.size() - i - 1] = nodePath.get(i);
			}
			return nodeArray;
		} catch (Exception e) {
			log("GenerateProperPath: error");
		}
		return null;
	}

	public RSTile[] fixPath(RSTile[] path) {
		ArrayList<RSTile> newPath = new ArrayList<RSTile>();
		for (int i = 0; i < path.length - 1; i++) {
			newPath.addAll(fixPath2(path[i], path[i + 1]));
		}
		return newPath.toArray(new RSTile[newPath.size()]);
	}

	/*
	 * Credits: Aftermath
	 */

	public List<RSTile> fixPath2(int startX, int startY, int destinationX, int destinationY) {
		double dx, dy;
		ArrayList<RSTile> list = new ArrayList<RSTile>();
		list.add(new RSTile(startX, startY));
		while (Math.hypot(destinationY - startY, destinationX - startX) > 8) {
			dx = destinationX - startX;
			dy = destinationY - startY;
			int gamble = random(14, 17);
			while (Math.hypot(dx, dy) > gamble) {
				dx *= .95;
				dy *= .95;
			}
			startX += (int) dx;
			startY += (int) dy;
			list.add(new RSTile(startX, startY));
		}
		list.add(new RSTile(destinationX, destinationY));
		return list;
	}

	public List<RSTile> fixPath2(RSTile tile) {
		return fixPath2(getLocation(), tile);
	}

	public List<RSTile> fixPath2(RSTile tile, RSTile tile2) {
		return fixPath2(tile.getX(), tile.getY(), tile2.getX(), tile2.getY());
	}

	public RSTile[] generateFixedPath(int x, int y) {
		return fixPath(generateProperPath(x, y));
	}

	public RSTile[] generateFixedPath(RSTile t) {
		return fixPath(generateProperPath(t));
	}

	/**
	 * Generates a randomized point array, used as a mouse path.
	 *
	 * @param Amount	  - The amount of points for the mouse path to contain,
	 *                    including the destination.
	 * @param destination - The destination.
	 * @return A point array, used as a mouse path or null if failed (Most
	 *         likely because the amount was negative or 0). If you enter 1 as
	 *         amount, the return would be the destination.
	 */
	public Point[] generateMousePath(int Amount, Point destination) {
		try {
			if (Amount <= 0)
				return null;
			Point[] path = new Point[Amount];
			Point curPos = getMouseLocation();
			for (int i = 0; i < path.length; i++) {
				path[i] = new Point();
				if (i == path.length - 1) {
					path[i].setLocation(destination);
				} else if (i != 0) {
					path[i].setLocation(path[i - 1].x > destination.x ? random(destination.x, path[i - 1].x) : random(path[i - 1].x, destination.x), path[i - 1].y > destination.y ? random(destination.y, path[i - 1].y) : random(path[i - 1].y, destination.y));
				} else {
					path[i].setLocation(curPos.x > destination.x ? random(destination.x, curPos.x) : random(curPos.x, destination.x), curPos.y > destination.y ? random(destination.y, curPos.y) : random(curPos.y, destination.y));
				}
			}
			Vector<Point> unsorted = new Vector<Point>();
			unsorted.addAll(Arrays.asList(path));
			Vector<Point> sorted = new Vector<Point>();
			for (Point element : path) {
				if (element == null) {
					continue;
				}
				int b = 0;
				int dist = 0;
				for (int a = 0; a < unsorted.size(); a++) {
					if ((int) Math.hypot(unsorted.get(a).getX() - destination.getX(), unsorted.get(a).getY() - destination.getY()) >= dist) {
						dist = (int) Math.hypot(unsorted.get(a).getX() - destination.getX(), unsorted.get(a).getY() - destination.getY());
						b = a;
					}
				}
				sorted.add(unsorted.get(b));
				unsorted.remove(b);
			}
			Point[] Path = new Point[sorted.size()];
			for (int i = 0; i < sorted.size(); i++) {
				Path[i] = sorted.get(i);
			}
			return Path;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public RSTile[] generateProperPath(int targetX, int targetY) {
		if (!mapLoaded) {
			loadMap();
		}
		int mx = getMyPlayer().getLocation().getX();
		int my = getMyPlayer().getLocation().getY();
		WalkerNode target = new WalkerNode(targetX, targetY);
		WalkerNode startNode = nodes.get(0), endNode = startNode;
		int shortestDistance = distance(startNode, mx, my);
		for (WalkerNode node : nodes) {
			if (distance(node, mx, my) < shortestDistance) {
				startNode = node;
				shortestDistance = distance(node, mx, my);
			}
		}
		shortestDistance = distance(endNode, targetX, targetY);
		for (WalkerNode node : nodes) {
			if (node.distance(target) < shortestDistance) {
				endNode = node;
				shortestDistance = node.distance(target);
			}
		}
		WalkerNode[] nodePath = findPath(startNode, endNode);
		if (nodePath == null)
			return new RSTile[]{new RSTile(mx, my), new RSTile(targetX, targetY)};
		else {
			RSTile[] tilePath = new RSTile[nodePath.length];
			tilePath[0] = new RSTile(mx, my);
			for (int i = 1; i < tilePath.length - 1; i++) {
				tilePath[i] = new RSTile(nodePath[i - 1].x, nodePath[i - 1].y);
			}
			tilePath[tilePath.length - 1] = new RSTile(targetX, targetY);
			return tilePath;
		}
	}

	public RSTile[] generateProperPath(RSTile t) {
		return generateProperPath(t.getX(), t.getY());
	}

	public String getAccountName() {
		return Bot.getAccountName();
	}

	/**
	 * Gets the pin for the account
	 *
	 * @return Pin or -1 if no pin
	 */
	public String getAccountPin() {
		return AccountManager.getPin(getAccountName());
	}

	/**
	 * Returns the angle to a given RSCharacter (RSNPC or RSPlayer).
	 *
	 * @param n the RSCharacter
	 * @return The angle
	 */
	public int getAngleToCharacter(RSCharacter n) {
		return getAngleToTile(n.getLocation());
	}

	/**
	 * Returns the angle to a given coordinate pair.
	 *
	 * @param x2 X coordinate
	 * @param y2 Y coordinate
	 * @return The angle
	 */
	public int getAngleToCoordinates(int x2, int y2) {
		int x1 = getMyPlayer().getLocation().getX();
		int y1 = getMyPlayer().getLocation().getY();
		int x = x1 - x2;
		int y = y1 - y2;
		double angle = Math.toDegrees(Math.atan2(y, x));
		if (x == 0 && y > 0) {
			angle = 180;
		}
		if (x < 0 && y == 0) {
			angle = 90;
		}
		if (x == 0 && y < 0) {
			angle = 0;
		}
		if (x < 0 && y == 0) {
			angle = 270;
		}
		if (x < 0 && y > 0) {
			angle += 270;
		}
		if (x > 0 && y > 0) {
			angle += 90;
		}
		if (x < 0 && y < 0) {
			angle = Math.abs(angle) - 180;
		}
		if (x > 0 && y < 0) {
			angle = Math.abs(angle) + 270;
		}
		if (angle < 0) {
			angle = 360 + angle;
		}
		if (angle >= 360) {
			angle -= 360;
		}
		return (int) angle;
	}

	/**
	 * Returns the angle to a given object
	 *
	 * @param o The RSObject
	 * @return The angle
	 */
	public int getAngleToObject(RSObject o) {
		return getAngleToTile(o.getLocation());
	}

	/**
	 * Returns the angle to a given tile
	 *
	 * @param t The RSTile
	 * @return The angle
	 */
	public int getAngleToTile(RSTile t) {
		return getAngleToCoordinates(t.getX(), t.getY());
	}

	public int getCameraAngle() {
		return Bot.getClient().getCameraYaw() / 45;
	}

	public RSInterfaceChild getChildInterface(int index, int indexChild) {
		return RSInterface.getChildInterface(index, indexChild);
	}

	org.rsbot.accessors.Client getClient() {
		return Bot.getClient();
	}

	/**
	 * Use by looping walkTo with this method as the argument.
	 *
	 * @param tile The destination tile.
	 * @return Returns the closest tile to the destination on the minimap.
	 */
	public RSTile getClosestTileOnMap(RSTile tile) {
		if (!tileOnMap(tile) && isLoggedIn()) {
			try {
				RSTile loc = getMyPlayer().getLocation();
				RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc.getY() + tile.getY()) / 2);
				return tileOnMap(walk) ? walk : getClosestTileOnMap(walk);
			} catch (StackOverflowError ignored) {
			}
		}
		return tile;
	}

	public RSInterfaceChild getContinueChildInterface() {
		if (Bot.getClient().getRSInterfaceCache() == null)
			return null;
		RSInterface[] valid = RSInterface.getAllInterfaces();
		for (RSInterface iface : valid) {
			if (iface.getIndex() != 137) {
				int len = iface.getChildCount();
				for (int i = 0; i < len; i++) {
					RSInterfaceChild child = iface.getChild(i);
					if (child.containsText("Click here to continue"))
						return child;
				}
			}
		}
		return null;
	}

	public RSInterface getContinueInterface() {
		if (Bot.getClient().getRSInterfaceCache() == null)
			return null;
		RSInterface[] valid = RSInterface.getAllInterfaces();
		for (RSInterface iface : valid) {
			if (iface.containsText("Click here to continue"))
				// || iface.containsAction("Click here to continue")) {
				return iface;
		}
		return null;
	}

	public int getCurrentTab() {
		for (int i = 0; i < TAB_NAMES.length; i++) {
			// Logout button, since we can't check it.
			if (i == TAB_LOGOUT)
				return TAB_LOGOUT;

			// Get tab
			org.rsbot.accessors.RSInterface tab = DynamicConstants.getTab(i);
			if (tab == null) {
				continue;
			}

			// Check if tab is selected
			if (tab.getTextureID() != -1)
				return i;
		}

		return -1; // no selected ones. (never happens, always return
		// TAB_LOGOUT)
	}

	/**
	 * Gets the destination tile. Where the flag is. WARNING: This method can
	 * return null.
	 *
	 * @return The current detination tile.
	 */
	public RSTile getDestination() { // ngovil21
		if (Bot.getClient().getDestX() <= 0)
			return null;
		return new RSTile(getClient().getDestX() + getClient().getBaseX(), getClient().getDestY() + getClient().getBaseY());
	}

	public int getEnergy() {
		try {
			return Integer.parseInt(RSInterface.getChildInterface(750, 5).getText());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Gets the equipment array.
	 *
	 * @return an array containing all equipped items
	 */
	public int[] getEquipmentArray() {
		RSInterfaceChild[] equip = getEquipmentInterface().getChildren();
		int[] equipmentIDs = new int[11];
		for (int i = 0; i < 11; i++) {
			equipmentIDs[i] = equip[i * 3 + 8].getComponentID();
		}
		return equipmentIDs;
	}

	public int getEquipmentCount() {
		return 11 - getEquipmentCount(-1);
	}

	public int getEquipmentCount(int itemID) {
		int count = 0;
		for (int item : getEquipmentArray()) {
			if (item == itemID) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Gets the equipment interface accessor.
	 *
	 * @return the equipment interface
	 */
	public RSInterface getEquipmentInterface() {
		// Tab needs to be open for it to update it's content -.-
		if (getCurrentTab() != TAB_EQUIPMENT) {
			if (bank.isOpen()) {
				bank.close();
			}
			openTab(TAB_EQUIPMENT);
			wait(random(900, 1500));
		}
		return getInterface(INTERFACE_EQUIPMENT);
	}

	public int[] getEquipmentStackArray() {
		return new int[0];
	}

	public int getFightMode() {
		return getSetting(SETTING_COMBAT_STYLE);
	}

	/**
	 * Returns the first (but not the closest) item found in a square within
	 * (range) away from you.
	 *
	 * @param range The maximum distance.
	 * @return The first ground item found; or null if none were found.
	 */
	public RSItemTile getGroundItem(int range) {
		int pX = getMyPlayer().getLocation().getX();
		int pY = getMyPlayer().getLocation().getY();
		int minX = pX - range;
		int minY = pY - range;
		int maxX = pX + range;
		int maxY = pY + range;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				RSItemTile[] items = getGroundItemsAt(x, y);
				if (items.length > 0)
					return items[0];
			}
		}
		return null;
	}

	public RSItemTile[] getGroundItemArray(int range) {
		ArrayList<RSItemTile> temp = new ArrayList<RSItemTile>();
		int pX = getMyPlayer().getLocation().getX();
		int pY = getMyPlayer().getLocation().getY();
		int minX = pX - range;
		int minY = pY - range;
		int maxX = pX + range;
		int maxY = pY + range;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				RSItemTile[] items = getGroundItemsAt(x, y);
				if (items.length > 0) {
					temp.add(items[0]);
				}
			}
		}
		if (temp.size() < 1)
			return null;
		RSItemTile[] array = new RSItemTile[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			array[i] = temp.get(i);
		}
		return array;
	}

	/**
	 * Returns the first (but not the closest) item with a specified id in the
	 * playable(visible) area.
	 *
	 * @param id The ID of the item to look for.
	 * @return The first matching ground item found; or null if none were found.
	 */
	public RSItemTile getGroundItemByID(int id) {
		return getGroundItemByID(52, new int[]{id});
	}

	/**
	 * Returns the first (but not the closest) item with a specified id found in
	 * a square within (range) away from you.
	 *
	 * @param range The maximum distance.
	 * @param id	The ID of the item to look for.
	 * @return The first matching ground item found; or null if none were found.
	 */
	public RSItemTile getGroundItemByID(int range, int id) {
		return getGroundItemByID(range, new int[]{id});
	}

	/**
	 * Returns the first (but not the closest) item with any of the specified
	 * IDs in a square within (range) away from you.
	 *
	 * @param range The maximum distance.
	 * @param ids   The IDs of the items to look for.
	 * @return The first matching ground item found; or null if none were found.
	 */
	public RSItemTile getGroundItemByID(int range, int[] ids) {
		int pX = getMyPlayer().getLocation().getX();
		int pY = getMyPlayer().getLocation().getY();
		int minX = pX - range;
		int minY = pY - range;
		int maxX = pX + range;
		int maxY = pY + range;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				RSItemTile[] items = getGroundItemsAt(x, y);
				for (RSItemTile item : items) {
					int iId = item.getItem().getID();
					for (int id : ids) {
						if (iId == id)
							return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the first (but not the closest) item with a specified id in the
	 * playable(visible) area.
	 *
	 * @param ids The IDs of the items to look for.
	 * @return The first matching ground item found; or null if none were found.
	 */
	public RSItemTile getGroundItemByID(int[] ids) {
		return getGroundItemByID(52, ids);
	}

	/**
	 * Returns all the ground items at a tile on the current plane.
	 *
	 * @param x The x position of the tile in the world.
	 * @param y The y position of the tile in the world.
	 * @return An array of the ground items on the specified tile.
	 */
	public RSItemTile[] getGroundItemsAt(int x, int y) {
		if (!isLoggedIn())
			return new RSItemTile[0];

		List<RSItemTile> list = new ArrayList<RSItemTile>();

		org.rsbot.accessors.NodeCache itemNC = Bot.getClient().getRSItemNodeCache();
		int sx = x;// - Bot.getClient().getBaseX();
		int sy = y;// - Bot.getClient().getBaseY();
		// int id = getPlane() << 28 << (sx << 14) | sy;
		int id = sx | sy << 14 | Bot.getClient().getPlane() << 28;

		org.rsbot.accessors.NodeListCache itemNLC = (org.rsbot.accessors.NodeListCache) Calculations.findNodeByID(itemNC, id);

		if (itemNLC == null)
			return new RSItemTile[0];

		NodeList itemNL = new NodeList(itemNLC.getNodeList());
		for (org.rsbot.accessors.RSItem item = (org.rsbot.accessors.RSItem) itemNL.getFirst(); item != null; item = (org.rsbot.accessors.RSItem) itemNL.getNext()) {
			list.add(new RSItemTile(x, y, new RSItem(item)));
		}

		return list.toArray(new RSItemTile[list.size()]);
	}

	/**
	 * Returns all the ground items at a tile on the current plane.
	 *
	 * @param t The tile.
	 * @return An array of the ground items on the specified tile.
	 */
	public RSItemTile[] getGroundItemsAt(RSTile t) {
		return getGroundItemsAt(t.getX(), t.getY());
	}

	/**
	 * Credits to Mouchicc.
	 */
	public String getIDtoName(int... ids) {
		try {
			for (int r : ids) {
				URL url = new URL("http://itemdb-rs.runescape.com/viewitem.ws?obj=" + r);
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

				String line;
				int i = 0;
				while ((line = reader.readLine()) != null) {
					if (line.equals("<div class=" + '"' + "subsectionHeader" + '"' + ">")) {
						i++;
						continue;
					}
					if (i == 1) {
						reader.close();
						return line;
					}
				}
			}
		} catch (Exception e) {
			log("Error converting ID to Name! Error: " + e);
		}
		return null;
	}

	public RSInterface getInterface(int in) {
		return RSInterface.getInterface(in);
	}

	public RSInterfaceChild getInterface(int index, int indexChild) {
		return RSInterface.getChildInterface(index, indexChild);
	}

	public RSInterface[] getInterfacesContainingText(String text) {
		List<RSInterface> results = new LinkedList<RSInterface>();
		for (RSInterface iface : RSInterface.getAllInterfaces()) {
			if (iface.getText().toLowerCase().contains(text.toLowerCase())) {
				results.add(iface);
			}
		}
		return results.toArray(new RSInterface[results.size()]);
	}

	/**
	 * Gets the inventory array.
	 *
	 * @return an array containing all items
	 */
	public int[] getInventoryArray() {
		RSInterfaceChild invIface = getInventoryInterface();
		if (invIface != null) {
			if (invIface.getComponents().length > 0) {
				int len = 0;
				for (RSInterfaceComponent com : invIface.getComponents()) {
					if (com.getType() == 5) {
						len++;
					}
				}

				int[] inv = new int[len];
				for (int i = 0; i < len; i++) {
					try {
						RSInterfaceComponent item = invIface.getComponents()[i];
						inv[item.getComponentIndex()] = item.getComponentID();
					} catch (Exception e) {
						wait(random(500, 700));
						return getInventoryArray();
					}
				}

				return inv;
			}
		}

		return new int[0]; // give scripters as few nulls as possible!
	}

	/**
	 * Gets the count of all items in your inventory
	 *
	 * @return the count of all inventory items
	 */
	public int getInventoryCount() {
		return getInventoryCount(false);
	}

	/**
	 * Gets the count of all items in your inventory.
	 *
	 * @param includeStacks <tt>false</tt> if stacked items should be counted as a single
	 *                      item; otherwise <tt>true</tt>.
	 * @return The count of all inventory items (+ stacks).
	 */
	public int getInventoryCount(boolean includeStacks) {
		int count = 0;
		int[] items = getInventoryArray();
		int[] itemStacks = getInventoryStackArray();
		for (int off = 0; off < items.length; off++) {
			int item = items[off];
			if (item != -1) {
				if (includeStacks) {
					count += itemStacks[off];
				} else {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Gets the count of a specific item in your inventory.
	 *
	 * @param itemIDs the item id to check
	 * @return the inventory count of the specified items
	 */
	public int getInventoryCount(int... itemIDs) {
		int total = 0;

		for (RSItem item : getInventoryItems()) {
			if (item == null) {
				continue;
			}

			for (int ID : itemIDs) {
				if (item.getID() == ID) {
					total += item.getStackSize();
				}
			}
		}

		return total;
	}

	public int getInventoryCountExcept(int... ids) {
		int[] items = getInventoryArray();
		int count = 0;
		for (int i : items) {
			if (i == -1) {
				continue;
			}
			boolean skip = false;
			for (int id : ids) {
				if (i == id) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Gets the inventory interface accessor.
	 *
	 * @return the inventory interface
	 */
	public RSInterfaceChild getInventoryInterface() {
		if (getInterface(INVENTORY_COM_X).isValid())
			return RSInterface.getChildInterface(INVENTORY_COM_X, INVENTORY_COM_Y);

		// Tab needs to be open for it to update it's content -.-
		if (getCurrentTab() != TAB_INVENTORY) {
			openTab(TAB_INVENTORY);
			wait(random(900, 1500));
		}

		return RSInterface.getChildInterface(INVENTORY_X, INVENTORY_Y);
	}

	public RSItem getInventoryItemByID(int... ids) {
		RSItem[] items = getInventoryItems();
		for (RSItem item : items) {
			for (int id : ids) {
				if (item.getID() == id)
					return item;
			}
		}
		return null;
	}

	/**
	 * Gets the ID of an item in the inventory. Written by Speed.
	 *
	 * @param name The name of the item you wish to find.
	 * @return The ID of the item or -1 if not in inventory.
	 */
	public int getInventoryItemIDByName(String name) {
		RSItem[] items = getInventoryItems();
		int slot = -1;
		for (RSItem item : items) {
			if (item.getDefinition().getName().contains(name)) {
				slot = item.getID();
			}
		}
		return slot;
	}

	/**
	 * Gets the top left position of the item
	 *
	 * @param invIndex The index of the item in the inventory array.
	 * @return A Point representing the screen location.
	 */
	public Point getInventoryItemPoint(int invIndex) {
		RSInterfaceChild invIface = getInventoryInterface();
		if (invIface.getComponents() == null || invIndex < 0 || invIndex >= invIface.getComponents().length)
			return new Point(-1, -1);

		return invIface.getComponents()[invIndex].getPosition();
	}

	public RSItem[] getInventoryItems() {
		RSInterfaceChild invIface = getInventoryInterface();
		if (invIface != null) {
			if (invIface.getComponents().length > 0) {
				int len = 0;
				for (RSInterfaceComponent com : invIface.getComponents()) {
					if (com.getType() == 5) {
						len++;
					}
				}

				RSItem[] inv = new RSItem[len];
				for (int i = 0; i < len; i++) {
					try {
						RSInterfaceComponent item = invIface.getComponents()[i];
						inv[item.getComponentIndex()] = new RSItem(item);
					} catch (Exception e) {
						wait(random(500, 700));
						return getInventoryItems();
					}
				}

				return inv;
			}
		}

		return new RSItem[0]; // give scripters as few nulls as possible!
	}

	/**
	 * Gets the inventory stack array.
	 *
	 * @return an array containing all item stacks
	 */
	public int[] getInventoryStackArray() {
		RSInterfaceChild invIface = getInventoryInterface();
		if (invIface != null) {
			if (invIface.getComponents().length > 0) {
				int len = 0;
				for (RSInterfaceComponent com : invIface.getComponents()) {
					if (com.getType() == 5) {
						len++;
					}
				}

				int[] inv = new int[len];
				for (int i = 0; i < len; i++) {
					try {
						RSInterfaceComponent item = invIface.getComponents()[i];
						inv[item.getComponentIndex()] = item.getComponentStackSize();
					} catch (Exception e) {
						wait(random(500, 700));
						return getInventoryStackArray();
					}
				}

				return inv;
			}
		}

		return new int[0]; // give scripters as few nulls as possible!
	}

	/**
	 * Access the last message spoken by a player.
	 *
	 * @return The last message spoken by a player or "" if none
	 */
	public String getLastMessage() {
		RSInterface chatBox = RSInterface.getInterface(INTERFACE_CHAT_BOX);
		for (int i = 157; i >= 58; i--) {// Valid text is from 58 to 157
			String text = chatBox.getChild(i).getText();
			if (!text.isEmpty() && text.contains("<"))
				return text;
		}
		return "";
	}

	/**
	 * Gets the player's current location.
	 *
	 * @return The RSTile that represents the player's location.
	 */
	public RSTile getLocation() {
		return getMyPlayer().getLocation();
	}

	public int getLoginIndex() {
		return Bot.getClient().getLoginIndex();
	}

	/**
	 * Returns an ArrayList of the first parts of each item in the current
	 * context menu actions.
	 *
	 * @return Returns the first half, for example "Walk here" "Follow".
	 */
	public ArrayList<String> getMenuActions() {
		ArrayList<String> actionsList = new ArrayList<String>();
		NodeList menu = new NodeList(Bot.getClient().getActionDataList());

		for (ActionDataNode adn = (ActionDataNode) menu.getFirst(); adn != null; adn = (ActionDataNode) menu.getNext()) {
			actionsList.add(adn.getMenuAction());
		}

		String[] actions = actionsList.toArray(new String[actionsList.size()]);
		ArrayList<String> output = new ArrayList<String>();
		// Don't remove the commented line, Jagex switches every few updates, so
		// it's there for quick fixes.
		for (int i = actions.length - 1; i >= 0; i--) {
			// for (int i = 0; i < actions.length; i++) {
			String action = actions[i];
			if (action != null) {
				String text = stripFomatting(action);
				output.add(text);
			} else {
				output.add("");
			}
		}

		return output;
	}

	/**
	 * Returns the index (starts at 0) in the menu for a given action. -1 when
	 * invalid.
	 *
	 * @param optionContains The String or a substring of the String that you want the
	 *                       index of.
	 * @return The index of the given option in the context menu; or -1 if the
	 *         option was not found.
	 */
	public int getMenuIndex(String optionContains) {
		optionContains = optionContains.toLowerCase();
		java.util.List<String> actions = getMenuItems();
		for (int i = 0; i < actions.size(); i++) {
			String action = actions.get(i);
			if (action.toLowerCase().contains(optionContains))
				return i;
		}
		return -1;
	}

	/**
	 * Returns an ArrayList of each item in the current context menu actions.
	 *
	 * @return First half + second half. As displayed in rs.
	 */
	public ArrayList<String> getMenuItems() {
		String[] options;
		String[] actions;
		synchronized (menuCacheLock) {
			options = menuOptionsCache.toArray(new String[menuOptionsCache.size()]);
			actions = menuActionsCache.toArray(new String[menuActionsCache.size()]);
		}
		ArrayList<String> output = new ArrayList<String>();
		//for(int i = Math.min(options.length, actions.length) - 1; i >= 0; i--) {
		for (int i = 0; i < Math.min(options.length, actions.length); i++) {
			String option = options[i];
			String action = actions[i];
			if (option != null && action != null) {
				String text = stripFomatting(action) + ' ' + stripFomatting(option);
				output.add(text);
			}
		}
		return output;
	}

	/**
	 * Returns the location of the menu. Returns null if not open.
	 *
	 * @return The RSTile over which the menu is currently located.
	 */
	public RSTile getMenuLocation() {
		if (!isMenuOpen())
			return null;
		int x = Bot.getClient().getMenuX();
		int y = Bot.getClient().getMenuY();
		x += 4;
		y += 4;
		return new RSTile(x, y);
	}

	/**
	 * Returns an ArrayList of the second parts of each item in the current
	 * context menu actions.
	 *
	 * @return The second half. "<user name>".
	 */
	public ArrayList<String> getMenuOptions() {
		ArrayList<String> optionsList = new ArrayList<String>();
		NodeList menu = new NodeList(Bot.getClient().getActionDataList());

		for (ActionDataNode adn = (ActionDataNode) menu.getFirst(); adn != null; adn = (ActionDataNode) menu.getNext()) {
			optionsList.add(adn.getMenuOption());
		}

		String[] options = optionsList.toArray(new String[optionsList.size()]);
		ArrayList<String> output = new ArrayList<String>();
		// Don't remove the commented line, Jagex switches every few updates, so
		// it's there for quick fixes.
		for (int i = options.length - 1; i >= 0; i--) {
			// for (int i = 0; i < options.length; i++) {
			String option = options[i];
			if (option != null) {
				String text = stripFomatting(option);
				output.add(text);
			} else {
				output.add("");
			}
		}

		return output;
	}

	/**
	 * Gets the location of the Mouse in the Screen. <br />
	 * <b>Note: </b>The X & Y coords in the Point retuned could be -1.
	 *
	 * @return A Point containing the mouse X & Y coordinates.
	 */
	public Point getMouseLocation() {
		return new Point(input.getX(), input.getY());
	}

	/**
	 * @return <tt>true</tt> if Sweed_Raver's mouse methods should be used by
	 *         default; otherwise <tt>false</tt>.
	 */
	protected boolean getMousePath() {
		return !MouseHandler.DEFAULT_MOUSE_PATH;
	}

	/**
	 * Get the mouse speed for this instance. This should be overrided if a
	 * change in speed is desired.
	 *
	 * @return the mouse speed to use for all operations.
	 * @see #moveMouse(int, int, int, int, int)
	 */
	protected int getMouseSpeed() {
		return MouseHandler.DEFAULT_MOUSE_SPEED;
	}

	/**
	 * Returns an RSPlayer object representing the current player.
	 *
	 * @return An RSPlayer object representing the player.
	 */
	public RSPlayer getMyPlayer() {
		return new RSPlayer(Bot.getClient().getMyRSPlayer());
	}

	/*
	 * Credits to Mouchicc.
	 */

	public int getNametoID(String name) {
		int ID = 0;
		try {
			URL url = new URL("http://services.runescape.com/m=itemdb_rs/results.ws?query=" + name + "&price=all&members=");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains('"' + name + '"') && line.contains("sprite.gif?")) {
					String str = line;
					str = str.substring(str.indexOf("id=") + 3, str.indexOf("\" alt=\""));
					ID = Integer.parseInt(str);
					reader.close();
					return ID;
				}
			}
		} catch (Exception e) {
			log("Error converting Name to ID! Error: " + e);
		}
		return ID;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided ID(s), that is not currently in combat. Can return null.
	 *
	 * @param ids The ID(s) of the NPCs that you are searching.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs that is not in combat; or null if there are no
	 *         mathching NPCs in the current region.
	 * @see #getNearestNPCByID(int...)
	 * @see #getNearestNPCToAttackByID(int...)
	 * @see #getNearestFreeNPCToAttackByID(int...)
	 */
	public RSNPC getNearestFreeNPCByID(int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (int id : ids) {
					if (id != Monster.getID() || Monster.isInCombat()) {
						continue;
					}
					int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (Exception ignored) {
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided name(s), that is not currently in combat. Can return null.
	 *
	 * @param names The name(s) of the NPCs that you are searching.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided names that is not in combat; or null if there are no
	 *         mathching NPCs in the current region.
	 * @see #getNearestNPCByID(int...)
	 * @see #getNearestFreeNPCByID(int...)
	 * @see #getNearestNPCToAttackByID(int...)
	 * @see #getNearestFreeNPCToAttackByID(int...)
	 */
	public RSNPC getNearestFreeNPCByName(String... names) {
		int Dist = 20;
		RSNPC closest = null;
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (String name : names) {
					if (name == null || !name.equals(Monster.getName()) || Monster.isInCombat()) {
						continue;
					}
					int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided ID(s), that is not currently in combat and does not have 0% HP.
	 * Can return null.
	 *
	 * @param ids The ID(s) of the NPCs that you are searching.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs that is not in combat and does not have 0% HP (is
	 *         attackable); or null if there are no mathching NPCs in the
	 *         current region.
	 * @see #getNearestNPCByID(int...)
	 * @see #getNearestFreeNPCByID(int...)
	 * @see #getNearestNPCToAttackByID(int...)
	 */
	public RSNPC getNearestFreeNPCToAttackByID(int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (int id : ids) {
					if (id != Monster.getID() || Monster.isInCombat() || Monster.getHPPercent() == 0) {
						continue;
					}
					int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (Exception ignored) {
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided name(s), that is not currently in combat and does not have 0%
	 * HP. Can return null.
	 *
	 * @param names The names(s) of the NPCs that you are searching.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided namess that is not in combat and does not have 0% HP (is
	 *         attackable); or null if there are no mathching NPCs in the
	 *         current region.
	 * @see #getNearestNPCByID(int...)
	 * @see #getNearestFreeNPCByID(int...)
	 * @see #getNearestNPCToAttackByID(int...)
	 * @see #getNearestFreeNPCToAttackByID(int...)
	 */
	public RSNPC getNearestFreeNPCToAttackByName(String... names) {
		int Dist = 20;
		RSNPC closest = null;
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (String name : names) {
					if (name == null || !name.equals(Monster.getName()) || Monster.isInCombat() || Monster.getHPPercent() == 0) {
						continue;
					}
					int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return closest;
	}

	/**
	 * Returns an RSItemTile representing the nearest item on the ground with an
	 * ID that matches any of the IDS provided. Can return null. RSItemTile is a
	 * subclass of RSTile.
	 *
	 * @param ids The IDs to look for.
	 * @return RSItemTile of the nearest item with the an ID that matches any in
	 *         the array of IDs provided; or null if no matching ground items
	 *         were found.
	 */
	public RSItemTile getNearestGroundItemByID(int... ids) {
		int dist = 9999999;
		int pX = getMyPlayer().getLocation().getX();
		int pY = getMyPlayer().getLocation().getY();
		int minX = pX - 52;
		int minY = pY - 52;
		int maxX = pX + 52;
		int maxY = pY + 52;
		RSItemTile itm = null;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				RSItemTile[] items = getGroundItemsAt(x, y);
				for (RSItemTile item : items) {
					int iId = item.getItem().getID();
					for (int id : ids) {
						if (iId == id && distanceTo(item) < dist) {
							dist = distanceTo(item);
							itm = item;
						}
					}
				}
			}
		}
		return itm;
	}

	/**
	 * Searches for an item on the ground within the specified area.
	 */
	// New getNearestItemByID method (Credits to RSHelper!)
	public RSItemTile getNearestGroundItemInAreaByID(RSArea toSearch, int... ids) {
		int dist = 9999999;
		RSTile[][] areaTile = toSearch.getTiles();
		RSItemTile itm = null;
		for (RSTile[] element : areaTile) {
			for (int y = 0; y < element.length; y++) {
				RSItemTile[] items = getGroundItemsAt(element[y]);
				for (RSItemTile item : items) {
					int iId = item.getItem().getID();
					for (int id : ids) {
						if (iId == id && distanceTo(item) < dist) {
							dist = distanceTo(item);
							itm = item;
						}
					}
				}
			}
		}
		return itm;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided ID(s). Can return null.
	 *
	 * @param ids The ID(s) of the NPCs that you are searching.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs; or null if there are no mathching NPCs in the
	 *         current region.
	 * @see #getNearestFreeNPCByID(int...)
	 * @see #getNearestNPCToAttackByID(int...)
	 * @see #getNearestFreeNPCToAttackByID(int...)
	 */
	public RSNPC getNearestNPCByID(int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (int id : ids) {
					if (id != Monster.getID()) {
						continue;
					}
					int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (Exception ignored) {
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided name(s). Can return null.
	 *
	 * @param names The name(s) of the NPCs that you are searching.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided names; or null if there are no mathching NPCs in the
	 *         current region.
	 * @see #getNearestNPCByID(int...)
	 * @see #getNearestFreeNPCByID(int...)
	 * @see #getNearestNPCToAttackByID(int...)
	 * @see #getNearestFreeNPCToAttackByID(int...)
	 */
	public RSNPC getNearestNPCByName(String... names) {
		int Dist = 20;
		RSNPC closest = null;
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (String name : names) {
					if (name == null || !name.equals(Monster.getName())) {
						continue;
					}
					int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided ID(s), that does not have 0% HP. Can return null.
	 *
	 * @param ids The ID(s) of the NPCs that you are searching.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs that does not have 0% HP (is attackable); or null if
	 *         there are no matching NPCs in the current region.
	 * @see #getNearestNPCByID(int...)
	 * @see #getNearestFreeNPCByID(int...)
	 * @see #getNearestNPCToAttackByID(int...)
	 * @see #getNearestFreeNPCToAttackByID(int...)
	 */
	public RSNPC getNearestNPCToAttackByID(int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (int id : ids) {
					if (id != Monster.getID() || Monster.getHPPercent() == 0) {
						continue;
					}
					int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (Exception ignored) {
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided names(s), that does not have 0% HP (is attackable). Can return
	 * null.
	 *
	 * @param names The name(s) of the NPCs that you are searching.
	 * @return An RSNPC object representing the nearest RSNPC with one of the
	 *         provided name(s) that does not have 0% HP (is attackable); or
	 *         null if there are no mathching NPCs in the current region.
	 * @see #getNearestNPCByID(int...)
	 * @see #getNearestFreeNPCByID(int...)
	 * @see #getNearestNPCToAttackByID(int...)
	 * @see #getNearestFreeNPCToAttackByID(int...)
	 */
	public RSNPC getNearestNPCToAttackByName(String... names) {
		int Dist = 20;
		RSNPC closest = null;
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (String name : names) {
					if (name == null || !name.equals(Monster.getName()) || Monster.getHPPercent() == 0) {
						continue;
					}
					int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return closest;
	}

	public RSObject getNearestObjectByID(int... ids) {
		RSObject cur = null;
		double dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSObject o = getObjectAt(x + Bot.getClient().getBaseX(), y + Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (int id : ids) {
						if (o.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						double distTmp = calculateDistance(getMyPlayer().getLocation(), o.getLocation());
						if (cur == null) {
							dist = distTmp;
							cur = o;
						} else if (distTmp < dist) {
							cur = o;
							dist = distTmp;
						}
					}
				}
			}
		}
		return cur;
	}

	public RSObject getNearestObjectByName(String... names) {
		RSObject cur = null;
		double dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSObject o = getObjectAt(x + Bot.getClient().getBaseX(), y + Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (String name : names) {
						if (o.getDef().getName().toLowerCase().contains(name.toLowerCase())) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						double distTmp = calculateDistance(getMyPlayer().getLocation(), o.getLocation());
						if (cur == null) {
							dist = distTmp;
							cur = o;
						} else if (distTmp < dist) {
							cur = o;
							dist = distTmp;
						}
					}
				}
			}
		}
		return cur;
	}

	public RSPlayer getNearestPlayerByLevel(int level) {
		int Dist = 20;
		RSPlayer closest = null;
		int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		org.rsbot.accessors.RSPlayer[] players = Bot.getClient().getRSPlayerArray();

		for (int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			RSPlayer player = new RSPlayer(players[element]);
			try {
				if (level != player.getCombatLevel()) {
					continue;
				}
				int distance = distanceTo(player);
				if (distance < Dist) {
					Dist = distance;
					closest = player;
				}
			} catch (Exception ignored) {
			}
		}
		return closest;
	}

	public RSPlayer getNearestPlayerByLevel(int min, int max) {
		int Dist = 20;
		RSPlayer closest = null;
		int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		org.rsbot.accessors.RSPlayer[] players = Bot.getClient().getRSPlayerArray();

		for (int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			RSPlayer player = new RSPlayer(players[element]);
			try {
				if (player.getCombatLevel() < min && player.getCombatLevel() > max) {
					continue;
				}
				int distance = distanceTo(player);
				if (distance < Dist) {
					Dist = distance;
					closest = player;
				}
			} catch (Exception ignored) {
			}
		}
		return closest;
	}

	public RSPlayer getNearestPlayerByName(String name) {
		int Dist = 20;
		RSPlayer closest = null;
		int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		org.rsbot.accessors.RSPlayer[] players = Bot.getClient().getRSPlayerArray();

		for (int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			RSPlayer player = new RSPlayer(players[element]);
			try {
				if (!name.equals(player.getName())) {
					continue;
				}
				int distance = distanceTo(player);
				if (distance < Dist) {
					Dist = distance;
					closest = player;
				}
			} catch (Exception ignored) {
			}
		}
		return closest;
	}

	public RSNPC[] getNPCArray(boolean excludeInteractingNPC) {
		int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
		ArrayList<RSNPC> realNPCs = new ArrayList<RSNPC>();
		for (int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode) || excludeInteractingNPC && getMyPlayer().getInteracting() != null &&
					getMyPlayer().getInteracting().equals(new RSNPC(((RSNPCNode) node).getRSNPC()))) {
				continue;
			}
			realNPCs.add(new RSNPC(((RSNPCNode) node).getRSNPC()));
		}
		RSNPC[] temp = new RSNPC[realNPCs.size()];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = realNPCs.get(i);
		}
		return temp;
	}

	public RSObject getObjectAt(int x, int y) {
		org.rsbot.accessors.Client client = Bot.getClient();
		if (client.getRSGroundArray() == null)
			return null;

		try {
			org.rsbot.accessors.RSGround rsGround = client.getRSGroundArray()[client.getPlane()][x - client.getBaseX()][y - client.getBaseY()];

			if (rsGround != null) {
				org.rsbot.accessors.RSObject rsObj;
				org.rsbot.accessors.RSInteractable obj;

				// Interactable objects like trees
				for (RSAnimableNode node = rsGround.getRSAnimableList(); node != null; node = node.getNext()) {
					obj = node.getRSAnimable();
					if (obj != null) {
						rsObj = (org.rsbot.accessors.RSObject) obj;
						if (rsObj.getID() != -1)
							return new RSObject(rsObj, x, y, 0);
					}
				}

				// Ground Decorations
				obj = rsGround.getRSObject1();
				if (obj != null) {
					rsObj = (org.rsbot.accessors.RSObject) obj;
					if (rsObj.getID() != -1)
						return new RSObject(rsObj, x, y, 1);
				}

				// Fences / Walls
				{
					obj = rsGround.getRSObject2_0();
					if (obj != null) {
						rsObj = (org.rsbot.accessors.RSObject) obj;
						if (rsObj.getID() != -1)
							return new RSObject(rsObj, x, y, 2);
					}

					obj = rsGround.getRSObject2_1();
					if (obj != null) {
						rsObj = (org.rsbot.accessors.RSObject) obj;
						if (rsObj.getID() != -1)
							return new RSObject(rsObj, x, y, 2);
					}
				}

				{
					obj = rsGround.getRSObject3_0();
					if (obj != null) {
						rsObj = (org.rsbot.accessors.RSObject) obj;
						if (rsObj.getID() != -1)
							return new RSObject(rsObj, x, y, 3);
					}

					obj = rsGround.getRSObject3_1();
					if (obj != null) {
						rsObj = (org.rsbot.accessors.RSObject) obj;
						if (rsObj.getID() != -1)
							return new RSObject(rsObj, x, y, 3);
					}
				}

				obj = rsGround.getRSObject4();
				if (obj != null) {
					rsObj = (org.rsbot.accessors.RSObject) obj;
					if (rsObj.getID() != -1)
						return new RSObject(rsObj, x, y, 4);
				}
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	// End Menu

	public RSObject getObjectAt(RSTile t) {
		return getObjectAt(t.getX(), t.getY());
	}

	/**
	 * Gets the plane we are currently on. Typically 0 (ground level), but will
	 * increase when going up ladders. You cannot be on a negative plane. Most
	 * dungeons/basements are on plane 0 elsewhere on the world map.
	 *
	 * @return The current plane.
	 */
	public int getPlane() {
		return Bot.getClient().getPlane();
	}

	public int getRandomMouseX(int maxDistance) {
		Point p = getMouseLocation();
		if (random(0, 2) == 0)
			return p.x - random(0, p.x < maxDistance ? p.x : maxDistance);
		else
			return p.x + random(1, 762 - p.x < maxDistance ? 762 - p.x : maxDistance);
	}

	public int getRandomMouseY(int maxDistance) {
		Point p = getMouseLocation();
		if (random(0, 2) == 0)
			return p.y - random(0, p.y < maxDistance ? p.y : maxDistance);
		else
			return p.y + random(1, 500 - p.y < maxDistance ? 500 - p.y : maxDistance);
	}

	public int getRealDistanceTo(RSTile t, boolean isObject) {
		RSTile curPos = getMyPlayer().getLocation();
		return Calculations.getRealDistanceTo(curPos.getX() - Bot.getClient().getBaseX(), // startX
				curPos.getY() - Bot.getClient().getBaseY(), // startY
				t.getX() - Bot.getClient().getBaseX(), // destX
				t.getY() - Bot.getClient().getBaseY(), // destY
				isObject); // if it's an object, calculate path to it
	}

	@Deprecated
	public int getSelectedItemID() {
		return Bot.getClient().isItemSelected();
	}

	public String getSelectedItemName() {
		return Bot.getClient().getSelectedItemName();
	}

	/**
	 * Returns an array of RSInterfaceComponents representing the prayers that
	 * are selected. Written by Bool.
	 *
	 * @return An <code>RSInterfaceComponent</code> array containing all the
	 *         components that represent selected prayers.
	 */
	public RSInterfaceComponent[] getSelectedPrayers() {
		ArrayList<RSInterfaceComponent> selected = new ArrayList<RSInterfaceComponent>();
		RSInterfaceComponent[] prayers = RSInterface.getChildInterface(INTERFACE_TAB_PRAYER, 7).getComponents();
		for (RSInterfaceComponent prayer : prayers) {
			if (prayer.getBackgroundColor() != -1) {
				selected.add(prayer);
			}
		}
		return selected.toArray(new RSInterfaceComponent[selected.size()]);
	}

	/**
	 * Returns true if designated prayer is turned on. Written by Iscream.
	 *
	 * @param index The prayer to check.
	 */
	public boolean isPrayerOn(int index) {
		RSInterfaceComponent[] prayers = RSInterface.getChildInterface(INTERFACE_TAB_PRAYER, 7).getComponents();
		for (RSInterfaceComponent prayer : prayers) {
			if (prayer.getComponentIndex() == index && prayer.getBackgroundColor() != -1) {
				return true;
			}
		}
		return false;
	}

	public int getSetting(int setting) {
		int[] settings = getSettingArray();
		if (setting < settings.length)
			return settings[setting];
		return -1;
	}

	public int[] getSettingArray() {
		Settings settingArray = Bot.getClient().getSettingArray();
		if (settingArray == null || settingArray.getData() == null)
			return new int[0];
		return settingArray.getData().clone(); // NEVER return pointer
	}

	// Input methods TODO: Remove and use input.*()

	public RSInterfaceChild getTalkInterface() {
		for (int talk : INTERFACE_TALKS) {
			RSInterfaceChild child = RSInterface.getChildInterface(talk, 0);
			if (child.isValid())
				return child;
		}
		return null;
	}

	/**
	 * Will return the closest tile that is on screen to the given tile.
	 *
	 * @param tile Tile you want to get to.
	 * @return Tile that is onScreen.
	 */
	public RSTile getTileOnScreen(RSTile tile) {
		try {
			if (tileOnScreen(tile))
				return tile;
			else {
				RSTile halfWayTile = new RSTile((tile.getX() + getLocation().getX()) / 2, (tile.getY() + getLocation().getY()) / 2);
				if (tileOnScreen(halfWayTile))
					return halfWayTile;
				else
					return getTileOnScreen(halfWayTile);
			}
		} catch (StackOverflowError soe) {
			log("getTileOnScreen() error: " + soe.getMessage());
			return null;
		}
	}

	public RSTile getTileUnderMouse() {
		Point p = getMouseLocation();
		if (!pointOnScreen(p))
			return null;
		RSTile close = null;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSTile t = new RSTile(x + Bot.getClient().getBaseX(), y + Bot.getClient().getBaseY());
				Point s = Calculations.tileToScreen(t);
				if (s.x != -1 && s.y != -1) {
					if (close == null) {
						close = t;
					}
					if (Calculations.tileToScreen(close).distance(p) > Calculations.tileToScreen(t).distance(p)) {
						close = t;
					}
				}
			}
		}
		return close;
	}

	/**
	 * Gets the current Wilderness Level. Written by Speed.
	 *
	 * @return The current wilderness level otherwise, 0.
	 */
	public int getWildernessLevel() {
		return RSInterface.getInterface(381).getChild(1).isValid() ? Integer.parseInt(RSInterface.getInterface(381).getChild(1).getText().replace("Level: ", "").trim()) : 0;
	}

	/**
	 * Checks if your inventory contains the specific items.
	 *
	 * @param itemID The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory contains at least one of all of
	 *         the item IDs provided; otherwise <tt>false</tt>.
	 * @see #inventoryContainsOneOf(int...)
	 */
	public boolean inventoryContains(int... itemID) {
		for (int i : itemID) {
			if (getInventoryItemByID(i) == null)
				return false;
		}
		return true;
	}

	public boolean inventoryContainsOneOf(int... itemID) {
		int[] items = getInventoryArray();
		for (int item : items) {
			for (int i : itemID) {
				if (item == i)
					return true;
			}
		}
		return false;
	}

	public boolean inventoryEmptyExcept(int... ids) {
		if (ids == null)
			return getInventoryCount() == 0;
		boolean no = false;
		outer:
		for (int items : getInventoryArray()) {
			if (items == -1) {
				continue;
			}
			for (int id : ids) {
				if (items == id) {
					continue outer;
				}
			}
			no = true;
			break;
		}
		return !no;
	}

	public boolean isCarryingItem(int... items) {
		return equipmentContains(items) || inventoryContains(items);
	}

	/**
	 * Checks whether or not the player is currently idle.
	 *
	 * @return <tt>true</tt> if the player is neither moving nor performing an
	 *         animation; otherwise <tt>false</tt>.
	 */
	public boolean isIdle() {
		return !getMyPlayer().isMoving() && getMyPlayer().getAnimation() == -1;
	}

	/**
	 * Checks if your inventory is full.
	 *
	 * @return <tt>true</tt> if your inventory is contains 28 items; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isInventoryFull() {
		return getInventoryCount() == 28;
	}

	public boolean isItemSelected() { // Credits to ByteCode for function
		for (RSInterfaceComponent com : getInventoryInterface().getComponents()) {
			if (com.getBorderThickness() == 2)
				return true;
		}
		return false;
	}

	/**
	 * Gets the client login status.
	 *
	 * @return <tt>true</tt> if logged in; otherwise <tt>false</tt>.
	 */
	public boolean isLoggedIn() {
		org.rsbot.accessors.Client client = Bot.getClient();
		int index = client == null ? -1 : client.getLoginIndex();
		return index == 9 || index == 10;
	}

	/**
	 * @return <tt>true</tt> if the client is showing the login screen;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean isLoginScreen() {
		return Bot.getClient().getLoginIndex() == 3;
	}

	/**
	 * @return <tt>true</tt> if the context menu is open; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isMenuOpen() {
		return Bot.getClient().isMenuOpen();
	}

	/**
	 * @return <tt>true</tt> if auto retaliate is enabled; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isRetaliateEnabled() {
		return getSetting(172) == 0;
	}

	/**
	 * @return <tt>true</tt> if run mode is enabled; otherwise <tt>false</tt>.
	 */
	public boolean isRunning() {
		return getSetting(173) == 1;
	}

	/**
	 * @return <tt>true</tt> if the client is showing the welcome screen;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean isWelcomeScreen() {
		return RSInterface.getInterface(WELCOME_SCREEN_ID).getChild(150).getAbsoluteY() > 2;
	}

	private boolean listContainsString(List<String> list, String string) {
		try {
			for (String command : list) {
				if (command.contains(string))
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void loadLinks() {
		String[] matrix = Walker.getWalkerLinks().split(" ");
		for (int i = 0; i < matrix.length; i += 2) {
			int x = Integer.parseInt(matrix[i]);
			int y = Integer.parseInt(matrix[i + 1]);
			WalkerNode node = nodes.get(x);
			node.neighbours.add(nodes.get(y));
			node = nodes.get(y);
			node.neighbours.add(nodes.get(x));
		}
	}

	private void loadMap() {
		mapLoaded = true;
		loadNodes();
		loadLinks();
	}

	private void loadNodes() {
		String[] matrix = Walker.getWalkerNodes().split(" ");
		for (int i = 0; i < matrix.length; i += 2) {
			nodes.add(new WalkerNode(Integer.parseInt(matrix[i]), Integer.parseInt(matrix[i + 1])));
		}
	}

	protected void log(String message) {
		log.info(message);
	}

	public boolean login() {
		return new org.rsbot.script.randoms.antiban.LoginBot().runRandom();
	}

	/**
	 * Closes the bank if it is open and logs out.
	 *
	 * @return True if the player was logged out.
	 */
	public boolean logout() {
		while (bank.isOpen()) {
			bank.close();
			wait(random(200, 400));
		}
		while (Bot.getClient().isSpellSelected() || isItemSelected()) {
			while (RSInterface.getInterface(620).isValid()) {
				atInterface(620, 7);
				wait(random(1000, 1300));
			}
			int currentTab = getCurrentTab();
			int randomTab = random(1, 6);
			while (randomTab == currentTab) {
				randomTab = random(1, 6);
			}
			do {
				openTab(randomTab);
				wait(random(400, 800));
			} while (Bot.getClient().isSpellSelected() || isItemSelected() == true);
		}
		while (getCurrentTab() != TAB_LOGOUT) {
			openTab(TAB_LOGOUT);
			int timesToWait = 0;
			while (getCurrentTab() != TAB_LOGOUT && timesToWait < 5) {
				wait(random(200, 400));
				timesToWait++;
			}
		}
		atInterface(INTERFACE_TAB_LOGOUT, 6);
		wait(random(1000, 1500));
		return !isLoggedIn();
	}

	/**
	 * Click chat button. Saves space, actually works.
	 *
	 * @param button Which button? Left-to right, 0 to 6. 7 Would land you on
	 *               Report Abuse.
	 * @param left   Left or right button? Left = true. Right = false.
	 */
	public void mouseChatButton(int button, boolean left) {
		int x = CHAT_BUTTON_CENTER_X + CHAT_BUTTON_DIFF_X * button;
		x = random(x - CHAT_BUTTON_MAX_DX, x + CHAT_BUTTON_MAX_DX);
		int y = CHAT_BUTTON_CENTER_Y;
		y = random(y - CHAT_BUTTON_MAX_DY, y + CHAT_BUTTON_MAX_DY);
		moveMouse(x, y);
		wait(random(200, 300));
		clickMouse(left);
	}

	public void moveMouse(int x, int y) {
		moveMouse(x, y, 0, 0);
	}

	public void moveMouse(int x, int y, boolean mousePaths) {
		moveMouse(getMouseSpeed(), x, y, 0, 0, 0, mousePaths);
	}

	public void moveMouse(int x, int y, int afterOffset) {
		moveMouse(getMouseSpeed(), x, y, 0, 0, afterOffset);
	}

	public void moveMouse(int x, int y, int randX, int randY) {
		moveMouse(getMouseSpeed(), x, y, randX, randY, 0);
	}

	public void moveMouse(int speed, int x, int y, int randX, int randY) {
		moveMouse(speed, x, y, randX, randY, 0);
	}

	public void moveMouse(int speed, int x, int y, int randX, int randY, int afterOffset) {
		moveMouse(speed, x, y, randX, randY, afterOffset, getMousePath());
	}

	/**
	 * Moves the mouse to the specified point at a certain sped.
	 *
	 * @param speed	   The lower, the faster.
	 * @param x		   The x destination.
	 * @param y		   The y destination.
	 * @param randX	   X-axis randomness (added to x).
	 * @param randY	   X-axis randomness (added to y).
	 * @param afterOffset The maximum distance in pixels to move on both axes shortly
	 *                    after moving to the destination.
	 * @param mousePaths  <tt>true</tt> to enable Sweed's mouse splines, otherwise
	 *                    <tt>false</tt>.
	 * @see #getMouseSpeed()
	 */
	public void moveMouse(int speed, int x, int y, int randX, int randY, int afterOffset, boolean mousePaths) {
		if (x != -1 || y != -1) {
			input.moveMouse(speed, x, y, randX, randY, mousePaths);
			if (afterOffset > 0) {
				wait(random(60, 300));
				Point pos = getMouseLocation();
				moveMouse(pos.x - afterOffset, pos.y - afterOffset, afterOffset * 2, afterOffset * 2);
			}
		}
	}

	public void moveMouse(int Speed, Point p, boolean mousePaths) {
		moveMouse(Speed, p.x, p.y, 0, 0, 0, mousePaths);
	}

	public void moveMouse(Point p) {
		moveMouse(p.x, p.y, 0, 0);
	}

	public void moveMouse(Point p, boolean mousePaths) {
		moveMouse(getMouseSpeed(), p.x, p.y, 0, 0, 0, mousePaths);
	}

	public void moveMouse(Point p, int afterOffset, boolean mousePaths) {
		moveMouse(getMouseSpeed(), p.x, p.y, 0, 0, afterOffset, mousePaths);
	}

	public void moveMouse(Point p, int randX, int randY) {
		moveMouse(p.x, p.y, randX, randY);
	}

	public void moveMouse(Point p, int randX, int randY, boolean mousePaths) {
		moveMouse(getMouseSpeed(), p.x, p.y, randX, randY, 0, mousePaths);
	}

	public void moveMouse(Point p, int randX, int randY, int afterOffset) {
		moveMouse(getMouseSpeed(), p.x, p.y, randX, randY, afterOffset);
	}

	/**
	 * Moves mouse through a mouse path containing 1 to 5 points (randomized) to
	 * a specified destination point.
	 *
	 * @param p The destination.
	 * @see #moveMouseByPath(Point, int, int, int)
	 */
	public void moveMouseByPath(Point p) {
		moveMouseByPath(p, random(1, 6), 0, 0);
	}

	/**
	 * Moves mouse through a mouse path containing a specified number of points
	 * to a specified destination point.
	 *
	 * @param p			   The destination.
	 * @param pathPointAmount The amount of points in the path.
	 */
	public void moveMouseByPath(Point p, int pathPointAmount) {
		moveMouseByPath(p, pathPointAmount, 0, 0);
	}

	/**
	 * Moves mouse through a mouse path containing 1 to 5 points (randomized) to
	 * a specified destination point.
	 *
	 * @param p	 The destination.
	 * @param randX The amount the X-axis can be randomized (in pixels).
	 * @param randY The amount the Y-axis can be randomized (in pixels).
	 * @see #moveMouseByPath(Point, int, int, int)
	 */
	public void moveMouseByPath(Point p, int randX, int randY) {
		moveMouseByPath(p, random(1, 6), randX, randY);
	}

	/**
	 * Moves mouse through a mouse path containing a specified amount of points
	 * to the specific destination point. Uses the
	 * {@link #moveMousePath(Point[], int, int)} and
	 * {@link #generateMousePath(int, Point)}  These methods were
	 * written by Sweed_Raver.
	 *
	 * @param p			   The destination.
	 * @param pathPointAmount The amount of points in the path.
	 * @param randX		   The amount the X-axis can be randomized (in pixels).
	 * @param randY		   The amount the Y-axis can be randomized (in pixels).
	 */
	public void moveMouseByPath(Point p, int pathPointAmount, int randX, int randY) {
		moveMousePath(generateMousePath(pathPointAmount, p), randX, randY);
	}

	/**
	 * Moves mouse through a specified mouse path.
	 *
	 * @param path  The path to move mouse through.
	 * @param randX The amount each point can be randomized in the X-axis.
	 * @param randY The amount each point can be randomized in the Y-axis.
	 */
	public void moveMousePath(Point[] path, int randX, int randY) {
		for (Point p : path) {
			moveMouse(p, randX, randY);
		}
	}

	public boolean moveMouseRandomly(int maxDistance) {
		if (maxDistance == 0)
			return false;
		maxDistance = random(1, maxDistance);
		Point p = new Point(getRandomMouseX(maxDistance), getRandomMouseY(maxDistance));
		if (p.x < 1 || p.y < 1) {
			p.x = p.y = 1;
		}
		moveMouse(p);
		return random(0, 2) != 0 && moveMouseRandomly(maxDistance / 2);
	}

	/**
	 * Moves the mouse slightly depending on where it currently is.
	 * <p/>
	 * Credits: Gnarly
	 */
	public void moveMouseSlightly() {
		Point p = new Point((int) (getMouseLocation().getX() + (Math.random() * 50 > 25 ? 1 : -1) * (30 + Math.random() * 90)), (int) (getMouseLocation().getY() + (Math.random() * 50 > 25 ? 1 : -1) * (30 + Math.random() * 90)));
		if (p.getX() < 1 || p.getY() < 1 || p.getX() > 761 || p.getY() > 499) {
			moveMouseSlightly();
			return;
		}
		moveMouse(p);
	}

	public RSTile nextTile(RSTile path[]) {
		return nextTile(path, 16);
	}

	public RSTile nextTile(RSTile path[], int maxDist) {
		return nextTile(path, maxDist, true);
	}

	/**
	 * Returns the next tile to walk to on a path.
	 *
	 * @param path		  The path.
	 * @param maxDist	   The maximum distance that a path tile should be from the
	 *                      player in order for it to be considered the next tile. The
	 *                      method searches from the end of the path to the beginning.
	 * @param enableMaxDist If false, this method will ignore the int maxDist.
	 * @return The next tile to walk to on the provided path.
	 */
	public RSTile nextTile(RSTile path[], int maxDist, boolean enableMaxDist) {
		int randomdis = random(3, 5);
		int closest = -1, sDist = -1;
		if (distanceTo(path[path.length - 1]) <= randomdis)
			return null;
		for (int i = path.length - 1; i >= 0; i--) {
			int dist = distanceTo(path[i]);
			if (sDist == -1 || dist < sDist) {
				if (getMyPlayer().getLocation().equals(path[i]) || distanceTo(path[i]) < 4)
					return path[i + 1];
				else {
					sDist = dist;
					closest = i;
				}
			}
			if (enableMaxDist && dist <= maxDist)
				return path[i];
		}
		return path[closest];
	}

	/**
	 * Calls {@link #stopScript()} by default. You may override this in a
	 * subclass of Script in order to execute code whenever the script is
	 * stopped.
	 */
	public void onFinish() {
		stopScript();
	}

	/*
	 * Made by countvidal.
	 */

	public boolean onTile(RSTile tile, String search, String action) {
		if (!tile.isValid())
			return false;
		Point checkScreen = Calculations.tileToScreen(tile);
		if (!pointOnScreen(checkScreen)) {
			walkTo(tile);
			wait(random(340, 700));
		}
		try {
			Point screenLoc;
			for (int i = 0; i < 30; i++) {
				screenLoc = Calculations.tileToScreen(tile);
				if (!pointOnScreen(screenLoc))
					return false;
				if (getMenuItems().get(0).toLowerCase().contains(search.toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
				moveMouse(screenLoc);
			}
			if (getMenuItems().size() <= 1)
				return false;
			if (getMenuItems().get(0).toLowerCase().contains(action.toLowerCase())) {
				clickMouse(true);
				return true;
			} else {
				clickMouse(false);
				return atMenu(action);
			}
		} catch (Exception e) {
			log("onTile() error: " + e.getMessage());
			return false;
		}
	}

	public void openTab(int tab) {
		if (tab == getCurrentTab())
			return;
		org.rsbot.accessors.RSInterface iTab = DynamicConstants.getTab(tab);
		if (iTab == null)
			return;
		atInterface(RSInterface.getChildInterface(iTab.getID()));
	}

	/**
	 * Checks if the player is holding one (or more) of the given items in their
	 * inventory or equipment. Great for checking for axes, picks, etc.
	 *
	 * @param items The IDs of items to check for.
	 * @return <tt>true</tt> if the player has one (or more) of the items in
	 *         either their equipment or their inventory; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean playerHasOneOf(int... items) {
		if (getCurrentTab() == TAB_INVENTORY)
			return inventoryContainsOneOf(items) || equipmentContainsOneOf(items);
		else if (getCurrentTab() == TAB_EQUIPMENT)
			return equipmentContainsOneOf(items) || inventoryContainsOneOf(items);
		return inventoryContainsOneOf(items) || equipmentContainsOneOf(items);
	}

	/**
	 * Checks whether a point is within the rectangle that determines the bounds
	 * of game screen. This will work fine when in fixed mode. In resizeable
	 * mode it will exclude any points that are less than 253 pixels from the
	 * right of the screen or less than 169 pixels from the bottom of the
	 * screen, giving a rough area.
	 *
	 * @param check The point to check.
	 * @return <tt>true</tt> if the point is within the rectangle; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean pointOnScreen(Point check) {
		int x = check.x, y = check.y;
		return x > 4 && x < CanvasWrapper.getGameWidth() - 253 && y > 4 && y < CanvasWrapper.getGameHeight() - 169;
	}

	/**
	 * Returns a random double in a specified range
	 *
	 * @param min Minimum value (inclusive).
	 * @param max Maximum value (exclusive).
	 * @return The random <code>double</code> generated.
	 */
	public double random(double min, double max) {
		return Math.min(min, max) + random.nextDouble() * Math.abs(max - min);
	}

	/**
	 * Returns a random integer in a specified range.
	 *
	 * @param min Minimum value (inclusive).
	 * @param max Maximum value (exclusive).
	 * @return The random <code>int</code> generated.
	 */
	public int random(int min, int max) {
		int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}

	public Point randomiseInventoryItemPoint(Point inventoryPoint) {
		return new Point(inventoryPoint.x + random(-10, 10), inventoryPoint.y + random(-10, 10));
	}

	/**
	 * Randomises a path of tiles.
	 *
	 * @param path		  The RSTiles to randomise.
	 * @param maxXDeviation Max X distance from tile.getX().
	 * @param maxYDeviation Max Y distance from tile.getY().
	 * @return The new, randomised path.
	 */
	public RSTile[] randomizePath(RSTile[] path, int maxXDeviation, int maxYDeviation) {
		RSTile[] rez = new RSTile[path.length];
		for (int i = 0; i < path.length; i++) {
			rez[i] = randomizeTile(path[i], maxXDeviation, maxYDeviation);
		}
		return rez;
	}

	/**
	 * Randomises a single tile.
	 *
	 * @param tile		  The RSTile to randomise.
	 * @param maxXDeviation Max X distance from tile.getX().
	 * @param maxYDeviation Max Y distance from tile.getY().
	 * @return The randomised tile.
	 */
	public RSTile randomizeTile(RSTile tile, int maxXDeviation, int maxYDeviation) {
		int x = tile.getX();
		int y = tile.getY();
		if (maxXDeviation > 0) {
			double d = random.nextDouble() * 2;
			d -= 1.0;
			d *= maxXDeviation;
			x += (int) d;
		}
		if (maxYDeviation > 0) {
			double d = random.nextDouble() * 2;
			d -= 1.0;
			d *= maxYDeviation;
			y += (int) d;
		}
		return new RSTile(x, y);
	}

	/**
	 * Rests until 100% energy
	 *
	 * @return <tt>true</tt> if rest was enabled; otherwise false.
	 * @see #rest(int)
	 */

	public boolean rest() {
		return rest(100);
	}

	/**
	 * Rests until a certain amount of energy is reached.
	 *
	 * @param stopEnergy Amount of energy at which it should stop resting.
	 * @return <tt>true</tt> if rest was enabled; otherwise false.
	 */
	public boolean rest(int stopEnergy) {
		int energy = getEnergy();
		for (int d = 0; d < 5; d++) {
			atInterface(INTERFACE_RUN_ORB, 1, "Rest");
			moveMouseSlightly();
			wait(random(400, 600));
			if (getMyPlayer().getAnimation() == 12108 || getMyPlayer().getAnimation() == 2033 || getMyPlayer().getAnimation() == 2716 || getMyPlayer().getAnimation() == 11786 || getMyPlayer().getAnimation() == 5713) {
				break;
			}
			if (d == 4) {
				log("Rest failed!");
				return false;
			}
		}
		while (energy < stopEnergy) {
			wait(random(250, 500));
			energy = getEnergy();
		}
		return true;
	}

	public RSTile[] reversePath(RSTile[] other) {
		RSTile[] t = new RSTile[other.length];
		for (int i = 0; i < t.length; i++) {
			t[i] = other[other.length - i - 1];
		}
		return t;
	}

	public void sendKey(char c) {
		input.sendKey(c);
	}

	public void sendText(String text, boolean pressEnter) {
		input.sendKeys(text, pressEnter);
	}

	public boolean setAssistMode(CHAT_MODE mode) {
		if (mode.equals(CHAT_MODE.HIDE))
			throw new IllegalArgumentException("Bad mode: HIDE");
		mouseChatButton(6, false);
		return atMenu(mode.toString());
	}

	public void setCameraAltitude(boolean maxAltitude) {
		char key = (char) (maxAltitude ? KeyEvent.VK_UP : KeyEvent.VK_DOWN);
		Bot.getInputManager().pressKey(key);
		wait(random(1000, 1500));
		Bot.getInputManager().releaseKey(key);
	}

	/**
	 * Set the camera to a certain percentage of the maximum altitude. Don't
	 * rely on the return value too much - it should return whether the camera
	 * was successfully set, but it isn't very accurate near the very extremes
	 * of the height.
	 * <p/>
	 * This also depends on the maximum camera angle in a region, as it changes
	 * depending on situation and surroundings. So in some areas, 68% might be
	 * the maximum altitude. This method will do the best it can to switch the
	 * camera altitude to what you want, but if it hits the maximum or stops
	 * moving for any reason, it will return.
	 * <p/>
	 * Mess around a little to find the altitude percentage you like. In later
	 * versions, there will be easier-to-work-with methods regarding altitude.
	 *
	 * @param altPercent The percentage of the maximum altitude to set the camera to.
	 * @return <tt>true</tt> if the camera was successfully moved; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean setCameraAltitude(double altPercent) {
		int alt = (int) (altPercent / 100 * -1237 - 1226);
		int curAlt = Bot.getClient().getCamPosZ();
		int lastAlt = 0;
		if (curAlt == alt)
			return true;
		else if (curAlt > alt) {
			Bot.getInputManager().pressKey((char) KeyEvent.VK_UP);
			long start = System.currentTimeMillis();
			while (curAlt > alt && System.currentTimeMillis() - start < 30) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;

				wait(1);
				curAlt = Bot.getClient().getCamPosZ();
			}
			Bot.getInputManager().releaseKey((char) KeyEvent.VK_UP);
			return true;
		} else {
			Bot.getInputManager().pressKey((char) KeyEvent.VK_DOWN);
			long start = System.currentTimeMillis();
			while (curAlt < alt && System.currentTimeMillis() - start < 30) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;
				wait(1);
				curAlt = Bot.getClient().getCamPosZ();
			}
			Bot.getInputManager().releaseKey((char) KeyEvent.VK_DOWN);
			return true;
		}
	}

	/**
	 * Rotates the camera to a specific angle in the closest direction.
	 *
	 * @param degrees The angle to rotate to.
	 */
	public void setCameraRotation(int degrees) {
		char left = 37;
		char right = 39;
		char whichDir = left;
		int start = getCameraAngle();
		/*
		 * Some of this shit could be simplified, but it's easier to wrap my
		 * mind around it this way
		 */
		if (start < 180) {
			start += 360;
		}
		if (degrees < 180) {
			degrees += 360;
		}
		if (degrees > start) {
			if (degrees - 180 < start) {
				whichDir = right;
			}
		} else if (start > degrees) {
			if (start - 180 >= degrees) {
				whichDir = right;
			}
		}
		degrees %= 360;
		Bot.getInputManager().pressKey(whichDir);
		int timeWaited = 0;
		while (getCameraAngle() > degrees + 5 || getCameraAngle() < degrees - 5) {
			wait(10);
			timeWaited += 10;
			if (timeWaited > 500) {
				int time = timeWaited - 500;
				if (time == 0) {
					Bot.getInputManager().pressKey(whichDir);
				} else if (time % 40 == 0) {
					Bot.getInputManager().pressKey(whichDir);
				}
			}
		}
		Bot.getInputManager().releaseKey(whichDir);
	}

	public boolean setClanMode(CHAT_MODE mode) {
		if (mode.equals(CHAT_MODE.HIDE))
			throw new IllegalArgumentException("Bad mode: HIDE");
		mouseChatButton(4, false);
		return atMenu(mode.toString());
	}

	public void setCompass(char direction) {
		switch (direction) {
			case 'n':
				if (random(0, 2) == 0) {
					atInterface(RSInterface.getChildInterface(DynamicConstants.getCompass().getID()));
				} else {
					setCameraRotation(359);
				}
				break;
			case 'w':
				setCameraRotation(89);
				break;
			case 's':
				setCameraRotation(179);
				break;
			case 'e':
				setCameraRotation(269);
				break;
			default:
				setCameraRotation(359);
				break;
		}
	}

	public void setFightMode(int fightMode) {
		if (fightMode != getFightMode()) {
			openTab(TAB_ATTACK);
			if (fightMode == 0) {
				clickMouse(577, 253, 55, 35, true);
			} else if (fightMode == 1) {
				clickMouse(661, 253, 55, 35, true);
			} else if (fightMode == 2) {
				clickMouse(576, 306, 55, 35, true);
			} else if (fightMode == 3) {
				clickMouse(662, 308, 55, 35, true);
			}
		}
	}

	/**
	 * Activates/deactivates a prayer via interfaces.
	 *
	 * @param pray	 The integer that represents the prayer by counting from left
	 *                 to right.
	 * @param activate <tt>true</tt> to activate; <tt>false</tt> to deactivate.
	 * @return <tt>true</tt> if the interface was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean setPrayer(int pray, boolean activate) {
		return RSInterface.getChildInterface(271, 5).getComponents()[pray].getBackgroundColor() == -1 && atInterface(RSInterface.getChildInterface(271, 5).getComponents()[pray], activate ? "Activate" : "Deactivate");
	}

	public boolean setPrivateChat(CHAT_MODE mode) {
		if (mode.equals(CHAT_MODE.HIDE))
			throw new IllegalArgumentException("Bad mode: HIDE");
		mouseChatButton(3, false);
		return atMenu(mode.toString());
	}

	public boolean setPublicChat(CHAT_MODE mode) {
		mouseChatButton(2, false);
		return atMenu(mode.toString());
	}

	/**
	 * Turns run on or off using the new l33t minimap controls :3
	 *
	 * @param enable Turns run on if true, off if false.
	 */
	public void setRun(boolean enable) {
		if (isRunning() == enable)
			return;

		atInterface(INTERFACE_RUN_ORB, 0);
	}

	public boolean setTradeMode(CHAT_MODE mode) {
		if (mode.equals(CHAT_MODE.HIDE))
			throw new IllegalArgumentException("Bad mode: HIDE");
		mouseChatButton(5, false);
		return atMenu(mode.toString());
	}

	public void setupMenuListener() {
		if (menuListenerStarted)
			return;

		menuListenerStarted = true;
		Bot.getEventManager().registerListener(new PaintListener() {
			public void onRepaint(Graphics g) {
				synchronized (menuCacheLock) {
					menuOptionsCache = getMenuOptions();
					menuActionsCache = getMenuActions();
				}
			}
		});
	}

	public void showAllChatMessages() {
		mouseChatButton(0, true);
	}

	public void showGameChatMessages() {
		mouseChatButton(1, true);
	}

	/**
	 * @deprecated Use {@link #stopScript()} instead.
	 */
	@Deprecated
	public void stopAllScripts() {
		stopScript();
	}

	public void stopScript() {
		stopScript(true);
	}

	public void stopScript(boolean logout) {
		log.info("Script stopped.");
		if (bank.isOpen()) {
			bank.close();
		}
		if (isLoggedIn() && logout) {
			logout();
		}
		Bot.getScriptHandler().stopScript();
	}

	/**
	 * Switches worlds. Because the old method was shit.
	 *
	 * @param world World to switch to.
	 */
	public void switchWorld(int world) {
		if (isLoggedIn()) {
			logout();
		}
		if (world > 121) {
			world -= 1;
		}

		clickMouse(random(346, 421), random(231, 243), true);
		wait(random(2000, 3000));
		clickMouse(59 + world / 24 * 93 + random(0, 93), 35 + 19 * (world % 24 - 1) + random(0, 19), true);

		log.info("World: " + world); // wrong when world >121
	}

	public boolean tileOnMap(RSTile t) {
		Point p = tileToMinimap(t);
		return p != null && p.x != -1 && p.y != -1;
	}

	public boolean tileOnScreen(RSTile t) {
		Point p = Calculations.tileToScreen(t, 0);
		return p.getX() > 0 && p.getY() > 0;
	}

	public Point tileToMinimap(RSTile t) {
		return worldToMinimap(t.getX(), t.getY());
	}

	/**
	 * Turns to an RSCharacter (RSNPC or RSPlayer).
	 *
	 * @param c The RSCharacter to turn to.
	 */
	public void turnToCharacter(RSCharacter c) {
		int angle = getAngleToCharacter(c);
		setCameraRotation(angle);
	}

	/**
	 * Turns to within a few degrees of an RSCharacter (RSNPC or RSPlayer).
	 *
	 * @param c   The RSCharacter to turn to.
	 * @param dev The maximum difference in the angle.
	 */
	public void turnToCharacter(RSCharacter c, int dev) {
		int angle = getAngleToCharacter(c);
		angle = random(angle - dev, angle + dev + 1);
		setCameraRotation(angle);
	}

	/**
	 * Turns the camera to a tile specified by x and y coordinates.
	 *
	 * @param x The tile x coordinate
	 * @param y The tile y coordinate
	 */
	public void turnToCoordinates(int x, int y) {
		turnToTile(new RSTile(x, y));
	}

	/**
	 * Turns the camera to within a few degrees of a tile specified by x and y
	 * coordinates.
	 *
	 * @param x   The tile x coordinate
	 * @param y   The tile y coordinate
	 * @param dev Maximum difference in angle between actual and chosen
	 *            rotation.
	 */
	public void turnToCoordinates(int x, int y, int dev) {
		turnToTile(new RSTile(x, y), dev);
	}

	/**
	 * Turns to an RSObject
	 *
	 * @param o The RSObject to turn to.
	 */
	public void turnToObject(RSObject o) {
		int angle = getAngleToObject(o);
		setCameraRotation(angle);
	}

	/**
	 * Turns to within a few degrees of an RSObject.
	 *
	 * @param o   The RSObject to turn to.
	 * @param dev The maximum difference in the turn angle.
	 */
	public void turnToObject(RSObject o, int dev) {
		int angle = getAngleToObject(o);
		angle = random(angle - dev, angle + dev + 1);
		setCameraRotation(angle);
	}

	/**
	 * Turns to a specific RSTile.
	 *
	 * @param tile Tile to turn to.
	 */
	public void turnToTile(RSTile tile) {
		int angle = getAngleToTile(tile);
		setCameraRotation(angle);
	}

	/**
	 * Turns within a few degrees to a specific RSTile.
	 *
	 * @param tile Tile to turn to.
	 * @param dev  Maximum deviation from the angle to the tile.
	 */
	public void turnToTile(RSTile tile, int dev) {
		int angle = getAngleToTile(tile);
		angle = random(angle - dev, angle + dev + 1);
		setCameraRotation(angle);
	}

	public boolean useItem(RSItem item, RSItem targetItem) {
		if (getCurrentTab() != TAB_INVENTORY) {
			openTab(TAB_INVENTORY);
		}
		return atInventoryItem(item.getID(), "Use") &&
				atInventoryItem(targetItem.getID(), "Use");
	}

	public boolean useItem(RSItem item, RSObject targetObject) {
		if (getCurrentTab() != TAB_INVENTORY) {
			openTab(TAB_INVENTORY);
		}
		return atInventoryItem(item.getID(), "Use") &&
				atObject(targetObject, "Use");
	}

	/**
	 * Pauses for a specified number of milliseconds. Try not to use this
	 * method.
	 *
	 * @param toSleep Time in milliseconds to pause.
	 * @see #waitToMove(int)
	 * @see #waitForAnim(int)
	 * @see #waitForIface(RSInterface, int)
	 */
	public void wait(int toSleep) {
		try {
			long start = System.currentTimeMillis();
			Thread.sleep(toSleep);

			// Guarantee minimum sleep
			long now;
			while (start + toSleep > (now = System.currentTimeMillis())) {
				Thread.sleep(start + toSleep - now);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Waits up to timeout millis for an animation to trigger. Will return the
	 * instant an animation begins.
	 *
	 * @param timeout Maximum time to wait for an animation (in milliseconds).
	 * @return The animation if an animation triggered, or -1 if there was no
	 *         animation.
	 */
	public int waitForAnim(int timeout) {
		long start = System.currentTimeMillis();
		RSPlayer myPlayer = getMyPlayer();
		int anim = -1;

		while (System.currentTimeMillis() - start < timeout) {
			if ((anim = myPlayer.getAnimation()) != -1) {
				break;
			}
			wait(15);
		}
		return anim;
	}

	/**
	 * Waits the value of the <code>timeout</code> parameter, until the
	 * <code>RSIterface</code> in <code>iface</code> becomes "valid".<br />
	 * If <code>iface</code> is <code>null</code> the method will return false.
	 * Written by pwnaz0r.
	 *
	 * @param iface   The RSInterface to wait for.
	 * @param timeout The time in milliseconds to wait for iface to become "valid".
	 * @return True - if <code>iface</code> is valid before <code>timeout</code>
	 *         expires.<br />
	 *         False - if <code>timeout</code> expires before <code>iface</code>
	 *         is valid.<br />
	 *         False - if <code>iface</code> is <code>null</code>.<br />
	 *         False - if <code>timeout</code> is lesser than 0.
	 * @see #waitForInterface(RSInterfaceChild, int)
	 * @since 1.0 - 08-03-2009
	 */
	public boolean waitForIface(RSInterface iface, int timeout) {
		if (timeout < 0)
			return false;

		if (iface == null)
			return false;

		long startTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - startTime <= timeout) {
			if (iface.isValid())
				return true;
			wait(125);
		}

		return false;
	}

	/**
	 * Waits the value of the <code>timeout</code> parameter, until the
	 * <code>RSIterface</code> in <code>iface</code> becomes "valid".<br />
	 * If <code>iface</code> is <code>null</code> the method will return false.
	 *
	 * @param interfaceChild The RSInterface to wait for.
	 * @param timeout		The time in milliseconds to wait for iface to become "valid".
	 * @return True - if <code>iface</code> is valid before <code>timeout</code>
	 *         expires.<br />
	 *         False - if <code>timeout</code> expires before <code>iface</code>
	 *         is valid.<br />
	 *         False - if <code>iface</code> is <code>null</code>.<br />
	 *         False - if <code>timeout</code> is lesser than 0.
	 * @see #waitForIface(RSInterface, int)
	 * @since 1.0 - 08-03-2009
	 */
	public boolean waitForInterface(RSInterfaceChild interfaceChild, int timeout) {
		if (timeout < 0)
			return false;
		if (interfaceChild == null)
			return false;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime <= timeout) {
			if (interfaceChild.isValid())
				return true;
			wait(125);
		}
		return false;
	}

	/**
	 * Waits up to timeout millis to start moving. This will return the instant
	 * movement starts. You can handle waiting a random amount afterwards by
	 * yourself.
	 *
	 * @param timeout Maximum time to wait to start moving (in milliseconds).
	 * @return True if we started moving, false if we reached the timeout.
	 */
	public boolean waitToMove(int timeout) {
		long start = System.currentTimeMillis();
		RSPlayer myPlayer = getMyPlayer();
		while (System.currentTimeMillis() - start < timeout) {
			if (myPlayer.isMoving())
				return true;
			wait(15);
		}
		return false;
	}

	/**
	 * Walks to the end of a path. This method should be looped.
	 *
	 * @param path The path to walk along.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean walkPathMM(RSTile[] path) {
		return walkPathMM(path, 16);
	}

	/**
	 * Walks to the end of a path. This method should be looped.
	 *
	 * @param path	The path to walk along.
	 * @param maxDist See {@link #nextTile(RSTile[], int)}.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean walkPathMM(RSTile[] path, int maxDist) {
		return walkPathMM(path, maxDist, 2, 2);
	}

	public boolean walkPathMM(RSTile[] path, int randX, int randY) {
		return walkPathMM(path, 16, randX, randY);
	}

	public boolean walkPathMM(RSTile[] path, int maxDist, int randX, int randY) {
		try {
			RSTile next = nextTile(path, maxDist);
			return next != null && walkTileMM(next, randX, randY);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean walkPathOnScreen(RSTile[] path) {
		return walkPathOnScreen(path, 16);
	}

	/**
	 * Walks a path using onScreen clicks and not the MiniMap. If the next tile
	 * is not on the screen, it will find the closest tile that is on screen and
	 * it will walk there instead.
	 *
	 * @param path	Path to walk.
	 * @param maxDist Max distance between tiles in the path.
	 * @return True if successful.
	 */
	public boolean walkPathOnScreen(RSTile[] path, int maxDist) {
		try {
			RSTile next = nextTile(path, maxDist);
			if (next != null)
				return atTile(getTileOnScreen(next), "alk");
			else
				return false;
		} catch (Exception e) {
			log("walkPathOnScreen() error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Walks to the given tile using the minimap with 1 tile randomness.
	 *
	 * @param t The tile to walk to.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean walkTileMM(RSTile t) {
		return walkTileMM(t, 2, 2);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 *
	 * @param t The tile to walk to.
	 * @param x The x randomness (between 0 and x-1).
	 * @param y The y randomness (between 0 and y-1).
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean walkTileMM(RSTile t, int x, int y) {
		RSTile dest = new RSTile(t.getX() +
				random(0, x), t.getY() + random(0, x));
		Point p = tileToMinimap(dest);
		if (p.x != -1 && p.y != -1) {
			moveMouse(p);
			Point p2 = tileToMinimap(dest);
			if (p2.x != -1 && p2.y != -1) {
				clickMouse(p2, true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Walks to a tile using onScreen clicks and not the MiniMap. If the tile is
	 * not on the screen, it will find the closest tile that is on screen and it
	 * will walk there instead.
	 *
	 * @param tile Tile to walk.
	 * @return True if successful.
	 */
	public boolean walkTileOnScreen(RSTile tile) {
		return atTile(getTileOnScreen(tile), "alk");
	}

	/**
	 * Walks to the provided tile by generating the shortest path to it, and
	 * walking along it.
	 *
	 * @param t The destination tile.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 * @see #walkTo(RSTile, int, int)
	 */
	public boolean walkTo(RSTile t) {
		return walkTo(t, 2, 2);
	}

	/**
	 * Walks to the provided tile by generating the shortest path to it, and
	 * walking along it. This method will immediately return false if progress
	 * is not made in 10 loop iterations. When the destination is reached, true
	 * will be returned. In order to walk to a destination, you can loop this
	 * method until true is returned. Be careful not to loop this without
	 * returning from the loop() method, otherwise the bot will not be able to
	 * check for random events (i.e. looping this with a nested while() is
	 * discouraged).
	 *
	 * @param t The destination tile.
	 * @param x The x randomness passed to
	 *          {@link #walkTileMM(RSTile, int, int)}.
	 * @param y The y randomness passed to
	 *          {@link #walkTileMM(RSTile, int, int)}.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean walkTo(RSTile t, int x, int y) {
		Point p = tileToMinimap(t);
		if (p.x == -1 || p.y == -1) {
			RSTile[] temp = cleanPath(generateFixedPath(t));
			for (int i = 0; i < 10; i++) {
				if (distanceTo(temp[temp.length - 1]) < 6)
					return true;
				RSTile next = nextTile(temp, 16);
				if (next != null) {
					if (walkTileMM(next, x, y))
						return true;
				} else {
					walkTileMM(nextTile(temp, 20));
				}
				wait(random(200, 400));
			}
			return false;
		}
		clickMouse(p, x, y, true);
		return true;
	}

	public boolean walkToClosestTile(RSTile[] t) {
		return walkToClosestTile(t, 2, 2);
	}

	/**
	 * Finds the closest tile in the path based on the player's destination.
	 * Walks to the tile by generating the shortest path to it. While this
	 * method is walking, false will be returned. When the destination is
	 * reached, true will be returned.
	 *
	 * @param t The destination tile.
	 * @param x The x randomness passed to
	 *          {@link #walkTo(RSTile, int, int)}.
	 * @param y The y randomness passed to
	 *          {@link #walkTo(RSTile, int, int)}.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean walkToClosestTile(RSTile[] t, int x, int y) {
		RSTile next = nextTile(t, 16, false);
		return next != null && walkTo(next, x, y);
	}

	public Point worldToMinimap(int x, int y) {
		return Calculations.worldToMinimap(x, y);
	}

}