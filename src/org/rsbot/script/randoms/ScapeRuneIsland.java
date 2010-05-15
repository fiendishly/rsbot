package org.rsbot.script.randoms;

import org.rsbot.bot.Bot;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;

/*
 * Updated by Iscream(Feb 17, 2010)
 * Updated by Iscream(Mar 01, 2010)
 * Updated by Iscream(Apr 15, 2010)
 * Updated by Iscream(May 11, 2010)
 */
@ScriptManifest(authors = { "endoskeleton", "Johnnei" }, name = "ScapeRune Island", version = 1.27)
public class ScapeRuneIsland extends Random {

	public RSObject statue1, statue2, statue3, statue4, direction;
	public int[] statueid = { 8992, 8993, 8990, 8991 };
	public RSNPC servant;
	public boolean finished;
	RSObject a;

	public boolean activateCondition() {
		return (servant = getNearestNPCByID(2481)) != null;
	}

	public int loop() {
		if (!activateCondition()) {
			return -1;
		}
		if (statue1 == null) {
			statue1 = getNearestObjectByID(statueid[0]);
			statue2 = getNearestObjectByID(statueid[1]);
			statue3 = getNearestObjectByID(statueid[2]);
			statue4 = getNearestObjectByID(statueid[3]);
			log("Setting statues");
		}
		if (getMyPlayer().isMoving() || getMyPlayer().getAnimation() == 620) {
			return random(550, 700);
		}
		if (canContinueIS()) {
			if (RSInterface.getChildInterface(241, 4).containsText("catnap")) {
				finished = true;
			}
			clickContinue();
			return random(1000, 1200);
		}
		if (finished) {
			final RSObject exit = getNearestObjectByID(8987);
			if (exit != null) {
				if (distanceTo(exit.getLocation()) > 4) {
					walkTo(exit.getLocation());
					return random(400, 800);
				}
				if (!tileOnScreen(exit.getLocation())) {
					walkTo(exit.getLocation());
					return random(400, 800);
				}
				atTile(exit.getLocation(), "Enter");
				return random(5500, 6000);
			}
		}
		if (getInventoryCount(6202) > 0) {
			final RSObject pot = getNearestObjectByID(8985);
			if (pot != null) {
				if (distanceTo(pot.getLocation()) > 4) {
					walkTo(pot.getLocation());
					return random(400, 800);
				}
				atInventoryItem(6202, "Use");
				wait(random(800, 1000));
				atTile(pot.getLocation(), "Use");
				return random(2000, 2400);
			}
		}
		if (getInventoryCountExcept(6209, 6202, 6200) >= 27) {
			log("Not enough space for this random. Depositing 2 Items");
			final RSObject depo = getNearestObjectByID(32930);
			if (!tileOnScreen(depo.getLocation())) {
				if (!walkTileMM(randomizeTile(depo.getLocation(), 3, 3))) {
					walkTo(randomizeTile(depo.getLocation(), 3, 3));
				}
				wait(random(1000, 1500));
			}
			if (!getInterface(11).isValid()) {
				atObject(depo, "Deposit");
				wait(random(2000, 2500));
			}
			if (getInterface(11).isValid()) {
				atComponent(getInterface(11).getChild(17), 25, "Dep");
				wait(random(1000, 1200));
				atComponent(getInterface(11).getChild(17), 26, "Dep");
				wait(random(1000, 1500));
				atInterface(11, 15);
				wait(random(1000, 1500));
			}
			return random(400, 1200);
		}
		if (getInventoryCount(6209) == 0) {
			final RSItemTile net = getGroundItemByID(6209);
			if (net != null) {
				if (distanceTo(net) > 5) {
					walkTo(net);
					return random(800, 1000);
				} else {
					atTile(net, "Take");
					return random(800, 1000);
				}
			}
		}

		if (RSInterface.getChildInterface(246, 5).containsText("contains")
				&& getSetting(334) == 1 && direction == null) {
			wait(2000);
			if (tileOnScreen(statue1.getLocation())) {
				direction = statue1;
			}
			if (tileOnScreen(statue2.getLocation())) {
				direction = statue2;
			}
			if (tileOnScreen(statue3.getLocation())) {
				direction = statue3;
			}
			if (tileOnScreen(statue4.getLocation())) {
				direction = statue4;
			}
			log("Checking direction");
			return 3000;
		}

		if (direction != null && getInventoryCount(6200) < 1) {
			// 6206, 6202
			// (want), 6200
			// (cooked) anim
			// 620
			wait(1200);
			if (distanceTo(direction.getLocation()) > 4) {
				walkTo(direction.getLocation());
				return random(400, 600);
			}
			final RSObject spot = getNearestObjectByID(8986);
			if (spot != null) {
				if (!tileOnScreen(spot.getLocation())) {
					turnToTile(spot.getLocation());
				}
				atTile(spot.getLocation(), "Net");
				return random(1000, 1200);
			}
		}

		if (getInventoryCount(6200) > 0 && !canContinueIS()) {
			final RSNPC cat = getNearestNPCByID(2479);
			if (cat != null) {
				if (!tileOnScreen(cat.getLocation())) {
					turnToTile(cat.getLocation());
					walkTo(cat.getLocation());
				}
				atInventoryItem(6200, "Use");
				wait(random(500, 1000));
				atTile(cat.getLocation(), "Use Raw fish-like thing -> Evil bob");
			}
			return random(1900, 2200);
		}
		if (servant != null && direction == null && getSetting(344) == 0
				&& !canContinueIS()) {
			if (!tileOnScreen(servant.getLocation())) {
				walkTo(servant.getLocation());
				return 700;
			}
			atNPC(servant, "Talk-to");
			return random(2000, 2100);
		}
		log("Setting 344: " + getSetting(344));
		return random(800, 1200);
	}

	private boolean canContinueIS() {
		return getContinueChildInterfaceIS() != null;
	}

	private RSInterfaceChild getContinueChildInterfaceIS() {
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