package org.rsbot.script.randoms;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.*;

/**
 * @version 2.3 - 15/4/10 Fix by Iscream
 */
@ScriptManifest(authors = {"Pwnaz0r", "Taha", "zqqou", "Zach"}, name = "Freaky Forester", version = 2.3)
public class FreakyForester extends Random implements ServerMessageListener {

	private RSNPC forester;
	private static final int FORESTER_ID = 2458;
	private static final int SEARCH_INTERFACE_ID = 242;
	private static final int PORTAL_ID = 8972;
	private static final RSTile WALK_TO_TILE = new RSTile(2610, 4775);
	private boolean unequip = false;
	private final RSInterface EquipInterface = RSInterface.getInterface(Constants.INTERFACE_EQUIPMENT);
	int phe = -1;

	boolean done = false;

	@Override
	public boolean activateCondition() {
		if (!isLoggedIn())
			return false;

		forester = getNearestNPCByID(FORESTER_ID);
		if (forester != null) {
			wait(random(2000, 3000));
			if (getNearestNPCByID(FORESTER_ID) != null) {
				RSObject portal = getNearestObjectByID(PORTAL_ID);
				return portal != null;

			}
		}
		return false;
	}

	public int getState() {
		if (done)
			return 3;
		else if (IScanContinue())
			return 1;
		else if (phe == -1)
			return 0;
		else if (inventoryContains(6178))
			return 0;
		else if (phe != -1)
			return 2;
		else
			return 0;
	}

	@Override
	public int loop() {
		forester = getNearestNPCByID(FORESTER_ID);
		if (forester == null)
			return -1;

		if (getMyPlayer().getAnimation() != -1)
			return random(3000, 5000);
		else if (getMyPlayer().isMoving())
			return random(200, 500);

		if (!done) {
			done = searchText(241, "Thank you") || getInterface(242, 4).containsText("leave");
		}

		if (inventoryContains(6179)) {
			phe = -1;
			atInventoryItem(6179, "rop");
			return random(500, 900);
		}
		if (unequip && (getInventoryCount(false) != 28)) {
			if (getCurrentTab() != Constants.TAB_EQUIPMENT) {
				openTab(Constants.TAB_EQUIPMENT);
				wait(random(1000, 1500));
				atInterface(EquipInterface.getChild(17));
				return (random(1000, 1500));
			}
			return (random(100, 500));
		}

		if ((getInventoryCount(false) == 28) && !inventoryContains(6178)) {
			final RSObject Deposit = getNearestObjectByID(32931);
			if ((!tileOnScreen(Deposit.getLocation()) && ((distanceTo(getDestination())) < 8)) || (distanceTo(getDestination()) > 40)) {
				if (!walkTileMM(randomizeTile(Deposit.getLocation(), 3, 3))) {
					walkTo(randomizeTile(Deposit.getLocation(), 3, 3));
				}
				wait(random(1200, 1400));
			}
			if (atObject(Deposit, "Deposit")) {
				wait(random(1800, 2000));
				clickMouse(410 + random(2, 4), 235 + random(2, 1), false);
				atMenu("Deposit-1");
				wait(random(1200, 1400));
				clickMouse(435 + random(2, 4), 40 + random(2, 1), true);
				return random(800, 1200);
			}
		}

		switch (getState()) {
			case 0: // Talk to forester
				if (tileOnScreen(forester.getLocation()) && (distanceTo(forester.getLocation()) <= 5)) {
					atNPC(forester, "Talk");
				} else if (distanceTo(forester.getLocation()) >= 5) {
					walkTo(randomizeTile(forester.getLocation(), 3, 3));
					turnToTile(randomizeTile(forester.getLocation(), 3, 3));
				}
				return random(500, 800);
			case 1: // Talking
				if (searchText(SEARCH_INTERFACE_ID, "one-")) {
					phe = 2459;
				} else if (searchText(SEARCH_INTERFACE_ID, "two-")) {
					phe = 2460;
				} else if (searchText(SEARCH_INTERFACE_ID, "three-")) {
					phe = 2461;
				}
				if (searchText(SEARCH_INTERFACE_ID, "four-")) {
					phe = 2462;
				}
				if (phe != -1) {
					log.info("Pheasant ID: " + phe);
				}
				if (myClickContinue())
					return random(500, 800);
				return random(200, 500);
			case 2: // Kill pheasant
				if (phe == -1)
					return random(200, 500);
				final RSNPC Pheasant = getNearestFreeNPCByID(phe);
				final RSItemTile tile = getGroundItemByID(6178);
				if (tile != null) {
					atTile(tile, "Take");
					return random(600, 900);
				} else if (Pheasant != null) {
					if (tileOnScreen(Pheasant.getLocation()) && (distanceTo(Pheasant.getLocation()) <= 5)) {
						atNPC(Pheasant, "ttack");
						return random(1000, 1500);
					} else if (distanceTo(Pheasant.getLocation()) >= 5) {
						walkTo(randomizeTile(Pheasant.getLocation(), 3, 3));
						turnToTile(randomizeTile(Pheasant.getLocation(), 3, 3));
					}
				} else
					return random(2000, 5000);
			case 3: // Get out
				if (!tileOnScreen(WALK_TO_TILE)) {
					if (tileOnMap(WALK_TO_TILE)) {
						walkTo(WALK_TO_TILE);
					} else {
						walkTo(randomizeTile(forester.getLocation(), 5, 5));
					}
					return random(900, 1200);
				}

				final RSObject Portal = getNearestObjectByID(PORTAL_ID);

				if (Portal == null) {
					log.info("Could not find portal.");
					return random(800, 1200);
				}

				if (atObject(Portal, "Enter"))
					return random(800, 1200);
				return random(200, 500);
		}
		return random(1000, 1500);
	}

	public boolean myClickContinue() {
		wait(random(800, 1000));
		return atInterface(243, 7) || atInterface(241, 5) || atInterface(242, 6) || atInterface(244, 8) || atInterface(64, 5);
	}

	public boolean searchText(final int interfac, final String text) {
		final RSInterface talkFace = getInterface(interfac);
		if (!talkFace.isValid())
			return false;
		for (int i = 0; i < talkFace.getChildCount(); i++) {
			if (talkFace.getChild(i).containsText(text))
				return true;
		}

		return false;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String serverString = e.getMessage();
		if (serverString.contains("no ammo left")) {
			unequip = true;
		}

	}

	private boolean IScanContinue() {
		return ISgetContinueChildInterface() != null;
	}

	private RSInterfaceChild ISgetContinueChildInterface() {
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
}