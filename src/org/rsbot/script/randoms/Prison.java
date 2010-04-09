package org.rsbot.script.randoms;

import org.rsbot.bot.Bot;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
//228 child is 2 or 3
/*
 * Written by Iscream(Feb 4, 2010)
 * Updated by Iscream(Feb 7, 2010)
 * Updated by Iscream(Feb 8, 2010)
 * Updated by Iscream(Feb 17, 2010)
 * Updated by zzSleepzz(Mar 1, 2010 to remove false postives)
 */
@ScriptManifest(authors = { "Iscream" }, name = "Prison Pete", version = 1.4)
public class Prison extends Random {
	private static final int PRISON_MATE = 3118, LEVER_ID = 10817, DOOR_KEY = 6966;
	private int unlocked, state = 0;
	private RSNPC balloonToPop;
	private RSNPC pete;
	private boolean talkedtopete = false;
	private boolean key = false;
	private boolean lucky = false;

	@Override
	public boolean activateCondition() {
		pete = getNearestNPCByName("Prison Pete");
		RSObject lever = getNearestObjectByID(LEVER_ID);

		if (isLoggedIn() &&  lever!= null && pete!=null)  {
			return true;
		}
		
		return false;
	}

	@Override
	public int loop() {
		if (getNearestNPCByName("Prison Pete") == null) {
			return -1;
		}
		if (!talkedtopete) {
			setCameraAltitude(true);
			if ((getCameraAngle() < 175) || (getCameraAngle() > 185)) {
				setCameraRotation(random(175, 185));
				return random(500, 750);
			}
		}
		switch (state) {
		case 0:
			pete = getNearestNPCByName("Prison Pete");
			if (getInterface(ISgetInterface("Lucky you!")).isValid()
					&& getInterface(ISgetInterface("Lucky you!")).containsText("Lucky you!"))
			{
				if (canContinue()) {
					clickContinue();
				}
				state = 4;
				lucky = true;
				return random(500, 600);
			}
			if ((getInventoryCount(false) == 28)
					&& !inventoryContains(DOOR_KEY)) {
				log("Not enough space for this random. Depositing an Item");
				final RSObject depo = getNearestObjectByID(32924);
				if (depo != null) {
					if (!tileOnScreen(depo.getLocation())) {
						if (!walkTileMM(randomizeTile(depo.getLocation(), 3, 3))) {
							walkTo(randomizeTile(depo.getLocation(), 3, 3));
							return random(500, 700);
						}
						return random(1000, 1500);
					}
					turnToObject(depo, 20);
					if (atObject(depo, "Deposit")) {
						wait(random(1800, 2000));
						if (getMyPlayer().isMoving()) {
							wait(random(200, 500));
						}
						if (RSInterface.getInterface(INTERFACE_DEPOSITBOX)
								.isValid()) {
							wait(random(700, 1200));
							atComponent(getInterface(11).getChild(17), random(
									16, 17), "Dep");
							wait(random(700, 1200));
							atInterface(11, 15);
						}
						return random(400, 500);
					}
					return random(500, 800);
				}
				return random(500, 600);
			}

			if (getMyPlayer().isMoving()) {
				return random(250, 500);
			}
			if (getInterface(ISgetInterface("minutes")).isValid() && getInterface(ISgetInterface("minutes")).containsText("minutes")) {
				talkedtopete = true;
				if (canContinue()) {
					clickContinue();
					return random(500, 600);
				}
				return random(500, 600);
			}

			if (getInterface(228).isValid()
					&& getInterface(228).containsText("How do")) {
					atInterface(getInterface(228).getChild(3));
					return random(500, 600);
				}
			if (canContinue()) {
				clickContinue();
				return random(1000, 1200);
			}
			if (!talkedtopete && pete != null
					&& (getInterface(228).isValid()) == false && !canContinue()) {
				if (!tileOnScreen(pete.getLocation())) {
					walkTileMM(pete.getLocation());
					return random(1000, 1400);
				}
				if (atNPC(pete, "Talk")) {
					return random(1500, 1600);
				} else {
					turnToTile(pete.getLocation());
					return random(500, 600);
				}
			}
			if (unlocked == 3) {
				state = 4;
				return random(250, 500);
			}
			if (unlocked <= 2 && talkedtopete) {
				state = 1;
				return random(500, 600);
			}
			return random(350, 400);

		case 1:
			// Figures out the balloon
			final RSObject lever = getNearestObjectByID(LEVER_ID);
			if ((lever != null) && talkedtopete) {
				if (!tileOnScreen(lever.getLocation())) {
					walkTileMM(lever.getLocation());
					return random(1000, 1200);
				}
				if (!getMyPlayer().isMoving()
						&& tileOnScreen(lever.getLocation())) {
					if (atTile(lever.getLocation(), 170, 0.5, 0.5, "Pull")) {
						wait(random(1400, 1600));
						if (atLever()) {
							if (balloonToPop != null) {
								state = 2;
								return random(800, 900);
							}
							return random(500, 700);
						}
						return random(500, 600);
					} else {
						turnToTile(lever.getLocation());
						return random(500, 600);
					}
				}
			}
			if (!talkedtopete) {
				state = 0;
				return random(500, 600);
			}
			return random(500, 600);
		case 2:
			// Finds animal and pops it
			if (getInterface(242).isValid()
					&& getInterface(242).getChild(5).getText().contains(
							"Lucky you!")) {
				if (canContinue()) {
					clickContinue();
				}
				state = 4;
				lucky = true;
				return random(500, 600);
			}
			if (getMyPlayer().isMoving()) {
				return random(250, 500);
			}
			if (balloonToPop == null && unlocked <= 2) {
				state = 1;
				return random(500, 700);
			}
			if (unlocked == 3) {
				state = 4;
			}

			if (!inventoryContains(DOOR_KEY)) {
				if (tileOnScreen(balloonToPop.getLocation())) {
					atNPC(balloonToPop, "Pop");
					return random(1200, 1400);
				} else {
					if (!getMyPlayer().isMoving()) {
						walkTileMM(randomizeTile(balloonToPop.getLocation(), 2,
								2));
						return random(500, 750);
					}
					return random(500, 750);
				}
			}
			if (inventoryContains(DOOR_KEY)) {
				key = false;
				state = 3;
				return random(500, 700);
			}
			return random(350, 400);

		case 3:
			// Goes to pete
			pete = getNearestNPCByName("Prison Pete");
			if (getMyPlayer().isMoving()) {
				return random(250, 500);
			}
			if (getInterface(ISgetInterface("Hooray")).isValid()
					&& getInterface(ISgetInterface("Hooray")).containsText("Hooray")) {
				key = true;
				if (canContinue()) {
					clickContinue();
					return random(500, 600);
				}
			}
			if (pete != null && !tileOnScreen(pete.getLocation())
					&& (getInterface(242).isValid()) == false) {
				walkTileMM(pete.getLocation());
				return random(400, 600);
			}
			if (canContinue()
					&& (getInterface(ISgetInterface("Hooray")).containsText("Hooray")) == false) {
				if (getInterface(242).isValid()
						&& getInterface(242).getChild(5).getText().contains(
								"Lucky you!")) {
					if (canContinue()) {
						clickContinue();
					}
					lucky = true;
					state = 4;
					return random(500, 600);
				}
				clickContinue();
				return random(500, 600);
			}
			if (!inventoryContains(DOOR_KEY)
					&& (getNearestNPCByID(PRISON_MATE) != null)
					&& (unlocked <= 2) && key == true) {
				unlocked++;
				state = 0;
				balloonToPop = null;
				return random(350, 400);
			}

			if (inventoryContains(DOOR_KEY) && !getMyPlayer().isMoving()) {
				atInventoryItem(DOOR_KEY, "Return");
				return random(1000, 2000);
			}
			if (!inventoryContains(DOOR_KEY)
					&& (getNearestNPCByID(PRISON_MATE) != null)
					&& (unlocked <= 2) && key == false) {
				state = 0;
				balloonToPop = null;
				return random(350, 400);
			}

			return random(350, 400);
		case 4:
			// exits
			RSTile doorTile = new RSTile(2086, 4459);
			if (unlocked <= 2 && lucky == false) {
				state = 0;
				return random(500, 600);
			}
			if (!tileOnScreen(doorTile)) {
				walkTileMM(doorTile);
				return random(400, 500);
			}
			if (tileOnScreen(doorTile)) {
				atDoorTiles(new RSTile(2086, 4459), new RSTile(2085, 4459));
				return random(500, 600);
			}
			return random(200, 400);
		}
		return random(200, 400);
	}

	@Override
	public void onFinish() {
	}

	public int setItemIDs(final int b2p) {
		// sets the proper balloon id
		switch (b2p) {
		case 10749:
			return 3119;
		case 10750:
			return 3120;
		case 10751:
			return 3121;
		case 10752:
			return 3122;
		}
		return 0;
	}

	public int ISgetChildInterface(final String a) {
		if (Bot.getClient().getRSInterfaceCache() == null) {
			return 0;
		}
		final RSInterface[] valid = RSInterface.getAllInterfaces();
		for (final RSInterface iface : valid) {
			final int len = iface.getChildCount();
			for (int i = 0; i < len; i++) {
				final RSInterfaceChild child = iface.getChild(i);
				if (child.containsText(a)) {
					return child.getIndex();
				}
			}
		}
		return 0;
	}
	public int ISgetInterface(final String a) {
		if (Bot.getClient().getRSInterfaceCache() == null) {
			return 0;
		}
		final RSInterface[] valid = RSInterface.getAllInterfaces();
		for (final RSInterface iface : valid) {
			if (iface.containsText(a)) {
				log ("Interface Found");
				return iface.getIndex();
			}
		}
		return 0;
	}
	public boolean atLever() {
		if (RSInterface.getInterface(273).getChild(3).isValid()) {
			balloonToPop = getNearestNPCByID(setItemIDs(RSInterface
					.getInterface(273).getChild(3).getModelID()));
			if (balloonToPop != null) {
				return true;
			}
		}
		return false;
	}

}