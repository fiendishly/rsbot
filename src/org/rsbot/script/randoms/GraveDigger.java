package org.rsbot.script.randoms;

/* Updates
 1.04 - Logout if inventory full
 1.1 - Hopefully fixed gravedigger. Next up, dropping. NEED ACCOUNTS!!!!
 1.2 - Stops getting stuck at the "Use Coffin -> Gravestone", maybe stopped the rapid start&stop bug.
 1.3 - Deposits EVERYTHING if inventory full
 1.31 - Deposits the lowest valued item only if inventory full
 1.32 - Deposits everything except axes, pickaxes, fishing equipment and hammers.
 1.33 - Added even more items not to deposit
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;

/**
 * <p>
 * This short-sighted gravedigger has managed to put five coffins in the wrong
 * graves. <br />
 * If he'd looked more closely at the headstones, he might have known where each
 * one was supposed to go! <br />
 * Help him by matching the contents of each coffin with the headstones in the
 * graveyard. Easy, huh?
 * </p>
 * 
 * @author Qauters
 */

/*
 * Updated by Iscream Feb 03,10
 * Updated by Iscream Feb 08,10
 */
@ScriptManifest(authors = { "Qauters", "Twisted", "Speed", "Taha"}, name = "Grave Digger", version = 1.5)
public class GraveDigger extends Random {
	class Group {
		// ID's used later
		int coffinID = -1;
		int graveID = -1;

		// General group data
		int graveStoneModelID;
		int[] coffinModelIDs;

		public Group(final int graveStoneModelID, final int[] coffinModelIDs) {
			this.graveStoneModelID = graveStoneModelID;
			this.coffinModelIDs = coffinModelIDs;
		}

		public boolean isGroup(final int graveStoneModelID) {
			return this.graveStoneModelID == graveStoneModelID;
		}

		public boolean isGroup(final int[] coffinModelIDs) {
			for (final int modelID : this.coffinModelIDs) {
				boolean found = false;

				for (final int coffinModelID : coffinModelIDs) {
					if (modelID == coffinModelID) {
						found = true;
					}
				}

				if (!found)
					return false;
			}

			return true;
		}

	}

	private final ArrayList<Group> groups = new ArrayList<Group>();

	private int tmpID = -1, tmpStatus = -1; // used to store some data across
	// loops
	private static final int[] coffinIDs = { 7587, 7588, 7589, 7590, 7591 };
	private static final int[] graveStoneIDs = { 12716, 12717, 12718, 12719, 12720 };
	private static final int[] filledGraveIDs = { 12721, 12722, 12723, 12724, 12725 };

	private static final int[] emptyGraveIDs = { 12726, 12727, 12728, 12729, 12730 };
	private static final int INTERFACE_READ_GRAVESTONE = 143;
	private static final int INTERFACE_READ_GRAVESTONE_MODEL = 2;

	private static final int INTERFACE_READ_GRAVESTONE_CLOSE = 3;
	private static final int INTERFACE_CHECK_COFFIN = 141;
	private static final int[] INTERFACE_CHECK_COFFIN_ITEMS = { 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	private static final int INTERFACE_CHECK_COFFIN_CLOSE = 12;
	@SuppressWarnings("unused")
	private static final int[] NOT_TO_DEPOSIT = { 1351, 1349, 1353, 1361, 1355, 1357, 1359, 4031, 6739, 13470, 14108, 1265, 1267, 1269, 1296, 1273, 1271, 1275, 15259, 303, 305, 307, 309, 311, 10129, 301, 13431, 313, 314, 2347, 995, 10006, 10031, 10008, 10012, 11260, 10150, 10010, 556, 558, 555, 557, 554, 559, 562, 560, 565, 8013, 4251, 8011, 8010, 8009, 8008, 8007 };

	public GraveDigger() {
		groups.add(new Group(7614, new int[] { 7603, 7605, 7612 }));
		groups.add(new Group(7615, new int[] { 7600, 7601, 7604 }));
		groups.add(new Group(7616, new int[] { 7597, 7606, 7607 }));
		groups.add(new Group(7617, new int[] { 7602, 7609, 7610 }));
		groups.add(new Group(7618, new int[] { 7599, 7608, 7613 }));
	}

	@Override
	public boolean activateCondition() {
		if ((getSetting(696) != 0) && (getNearestObjectByID(12731) != null)) {
			tmpID = tmpStatus = -1;
			return true;
		}
		return false;
	}

	public boolean atInventoryItem2(final int itemID, final String option) {
		if ((getCurrentTab() != Constants.TAB_INVENTORY) && !RSInterface.getInterface(Constants.INTERFACE_BANK).isValid() && !RSInterface.getInterface(Constants.INTERFACE_STORE).isValid()) {
			openTab(Constants.TAB_INVENTORY);
		}
		final int[] items = getInventoryArray();
		final java.util.List<Integer> possible = new ArrayList<Integer>();
		for (int i = 0; i < items.length; i++) {
			if (items[i] == itemID) {
				possible.add(i);
			}
		}
		if (possible.size() == 0)
			return false;
		final int idx = possible.get(random(0, possible.size()));
		final Point t = getInventoryItemPoint(idx);
		clickMouse(t, 5, 5, false);
		wait(random(150, 350));
		return atMenu(option);
	}



	@Override
	public int loop() {
		if (getNearestNPCByName("Leo") == null) {
			return -1;
		}
if (getInventoryCountExcept(GraveDigger.coffinIDs) > 23) {
		if (canContinue()) {
			clickContinue();
			wait(random(1500,2000));
		}
		RSObject depo = getNearestObjectByID(12731);
		if (depo != null) {
			if (!tileOnScreen(depo.getLocation())) {
				walkTo(depo.getLocation());
				turnToObject(depo);
			} else {
				atObject(depo,"Deposit");
			}
		}
		if (RSInterface.getInterface(INTERFACE_DEPOSITBOX).isValid()) {
wait (random(700,1200));
atComponent(getInterface(11).getChild(17),27,"Dep");
wait (random(700,1200));
atComponent(getInterface(11).getChild(17),26,"Dep");
wait (random(700,1200));
atComponent(getInterface(11).getChild(17),25,"Dep");
wait (random(700,1200));
atComponent(getInterface(11).getChild(17),24,"Dep");
wait (random(700,1200));
atComponent(getInterface(11).getChild(17),23,"Dep");
wait (random(700,1200));
atInterface(11, 15);
return random(500,700);
		}
		return (random(2000,3000));
}

		if (getMyPlayer().isMoving()) {
			;
		} else if (getMyPlayer().getAnimation() == 827) {
			;
		} else if (RSInterface.getInterface(242).isValid()) {
			// Check if we finished before
			if (RSInterface.getInterface(242).containsText("ready to leave")) {
				tmpStatus++;
			}

			atInterface(242, 6);
		} else if (RSInterface.getInterface(64).isValid()) {
			atInterface(64, 5);
		} else if (RSInterface.getInterface(241).isValid()) {
			atInterface(241, 5);
		} else if (RSInterface.getInterface(243).isValid()) {
			atInterface(243, 7);
		} else if (RSInterface.getInterface(220).isValid()) {
			atInterface(220, 16);
		} else if (RSInterface.getInterface(236).isValid()) {
			if (RSInterface.getInterface(236).containsText("ready to leave")) {
				atInterface(236, 1);
			} else {
				atInterface(236, 2);
			}
		} else if (RSInterface.getInterface(GraveDigger.INTERFACE_CHECK_COFFIN).isValid()) {
			if (tmpID >= 0) {
				final int[] items = new int[GraveDigger.INTERFACE_CHECK_COFFIN_ITEMS.length];

				final org.rsbot.accessors.RSInterface[] interfaces = Bot.getClient().getRSInterfaceCache()[GraveDigger.INTERFACE_CHECK_COFFIN];
				for (int i = 0; i < GraveDigger.INTERFACE_CHECK_COFFIN_ITEMS.length; i++) {
					items[i] = interfaces[GraveDigger.INTERFACE_CHECK_COFFIN_ITEMS[i]].getComponentID();
				}

				for (final Iterator<Group> it = groups.iterator(); it.hasNext() && (tmpID >= 0);) {
					final Group g = it.next();
					if (g.isGroup(items)) {
						g.coffinID = tmpID;
						tmpID = -1;
					}
				}
			}

			atInterface(GraveDigger.INTERFACE_CHECK_COFFIN, GraveDigger.INTERFACE_CHECK_COFFIN_CLOSE);
		} else if (RSInterface.getInterface(GraveDigger.INTERFACE_READ_GRAVESTONE).isValid()) {
			final int modelID = Bot.getClient().getRSInterfaceCache()[GraveDigger.INTERFACE_READ_GRAVESTONE][GraveDigger.INTERFACE_READ_GRAVESTONE_MODEL].getComponentID();
			for (final Group g : groups) {
				if (g.isGroup(modelID)) {
					g.graveID = tmpID;
				}
			}

			atInterface(GraveDigger.INTERFACE_READ_GRAVESTONE, GraveDigger.INTERFACE_READ_GRAVESTONE_CLOSE);
		} else if ((tmpStatus == 0) && (tmpID != -1)) {
			for (final Group g : groups) {
				if (g.graveID == tmpID) {
					final RSObject obj = getNearestObjectByID(g.graveID);
					if ((obj == null) || !setObjectInScreen(obj)) {
						log.info("Couldn't find grave, shutting down.");
						logout();
						return -1;
					}

					if (Bot.getClient().isItemSelected() > 0) {
						atInventoryItem(GraveDigger.coffinIDs[g.coffinID], "Cancel");
					}

					useItem2(getInventoryItemByID(GraveDigger.coffinIDs[g.coffinID]), obj);

					// Wait for about 10s to finish
					final long cTime = System.currentTimeMillis();
					while (System.currentTimeMillis() - cTime < 10000) {
						if (getInventoryItemByID(GraveDigger.coffinIDs[g.coffinID]) == null) {
							break;
						}

						wait(random(400, 700));
					}

					break;
				}
			}

			tmpID = -1;
		} else if ((tmpStatus == -1) && (getNearestObjectByID(GraveDigger.filledGraveIDs) != null)) {
			final RSObject obj = getNearestObjectByID(GraveDigger.filledGraveIDs);
			if ((obj == null) || !setObjectInScreen(obj)) {
				log.severe("Couldn't find grave, shutting down.");
				logout();
				return -1;
			}
			atObject(obj, "Take-coffin");
		} else if ((tmpStatus == 0) && (getNearestObjectByID(GraveDigger.emptyGraveIDs) != null)) {
			final RSObject obj = getNearestObjectByID(GraveDigger.emptyGraveIDs);
			final int id = obj.getID();
			for (int i = 0; i < GraveDigger.emptyGraveIDs.length; i++) {
				if (GraveDigger.emptyGraveIDs[i] == id) {
					final RSObject objGS = getNearestObjectByID(GraveDigger.graveStoneIDs[i]);
					if ((objGS == null) || !setObjectInScreen(objGS)) {
						log.severe("Couldn't find grave stone, shutting down.");
						logout();
						return -1;
					}

					tmpID = obj.getID();

					if (Bot.getClient().isItemSelected() == 1) {
						atObject(objGS, "Use");
					}

					atObject(objGS, "Read");
				}
			}
		} else if (tmpStatus == -1) {
			final ArrayList<Integer> agc = new ArrayList<Integer>();
			for (int i = 0; i < GraveDigger.coffinIDs.length; i++) {
				agc.add(i);
			}

			for (final Group g : groups) {
				if (g.coffinID != -1) {
					agc.remove(new Integer(g.coffinID));
				}
			}

			if ((tmpStatus == -1) && (agc.size() == 0)) {
				tmpStatus++;
			}

			while (tmpStatus == -1) {
				final int i = random(0, agc.size());
				if (getInventoryCount(GraveDigger.coffinIDs[agc.get(i)]) > 0) {
					tmpID = agc.get(i);
					atInventoryItem(GraveDigger.coffinIDs[agc.get(i)], "Check");

					return random(1800, 2400); // We are looking at the model
				}
			}
		} else if (tmpStatus == 0) {
			// Done
			final RSNPC leo = getNearestNPCByName("Leo");
			if ((leo == null) || !setCharacterInScreen(leo)) {
				log.severe("Couldn't find Leo, shutting down.");
				logout();
				return -1;
			}

			atNPC(leo, "Talk-to");
		}
		return random(1400, 1800);
	}

	public boolean setCharacterInScreen(final RSCharacter ch) {
		// Check if it's on screen, if not make it on screen.
		for (int i = 0; i < 3; i++) {
			final Point screenLocation = ch.getScreenLocation();
			if (!Calculations.onScreen(screenLocation)) {
				switch (i) {
					case 0:
						turnToCharacter(ch);

						wait(random(200, 500));

						break;
					case 1:
						walkTo(randomizeTile(ch.getLocation(), 2, 2));

						waitToMove(random(1800, 2000));

						while (getMyPlayer().isMoving()) {
							wait(random(200, 500));
						}
						break;
					default:
						return false;
				}

			}
		}

		return true;
	}

	public boolean setObjectInScreen(final RSObject obj) {
		// Check if it's on screen, if not make it on screen.
		for (int i = 0; i < 3; i++) {
			final Point screenLocation = Calculations.tileToScreen(obj.getLocation());
			if (!Calculations.onScreen(screenLocation)) {
				switch (i) {
					case 0:
						turnToObject(obj);

						wait(random(200, 500));

						break;
					case 1:
						walkTo(randomizeTile(obj.getLocation(), 2, 2));

						waitToMove(random(1800, 2000));

						while (getMyPlayer().isMoving()) {
							wait(random(200, 500));
						}
						break;
					default:
						return false;
				}

			}
		}

		return true;
	}

	public boolean useItem2(final RSItem item, final RSObject targetObject) {
		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			openTab(Constants.TAB_INVENTORY);
		}

		if (atInventoryItem2(item.getID(), "Use"))
			return atObject(targetObject, "Use");
		else {
			atInventoryItem2(item.getID(), "Use");
			return atObject(targetObject, "Use");
		}
	}
}