package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;

/*
 * Updated by TwistedMind (Feb 8, '10) ~ It wasn't exiting...
 * Updated by Iscream (Feb 8, 10) Fixed some loop issues.
 * Updated by Iscream (Feb 09,10)
 * Updated by Iscream (Feb 17,10) Fixed Exiting Issues
 */

@ScriptManifest(authors = { "Keilgo", "Taha", "Equilibrium", "Twistedmind" }, name = "Cap'n Arnav", version = 0.9)
public class CapnArnav extends Random {

	public int startValue3;
	public int startValue2;
	public int startValue1;

	public int currValue3;
	public int currValue2;
	public int currValue1;

	private boolean thirdColFound = false;
	private boolean secondColFound = false;
	private boolean firstColFound = false;

	private boolean reel1done = false;
	private boolean reel2done = false;
	private boolean reel3done = false;

	private boolean talkedto = false;
	private static final int CAPTAIN_ID = 2308;
	private static final int PORTAL_ID = 11369;

	private boolean done = false;

	@Override
	public boolean activateCondition() {
		final RSNPC captain = getNearestNPCByID(CAPTAIN_ID);
		
		if (captain != null) {
			wait(random(1500,1600));
			RSObject portal = getNearestObjectByID(PORTAL_ID);
			
			if (portal==null)  {
				return false;
			}

			return true;
		}

		return false;
	}

	@Override
	public int loop() {
		final RSNPC captain = getNearestNPCByID(CAPTAIN_ID);
		
		if (!activateCondition()) {
			return -1;
		}
		if (getMyPlayer().isMoving() || (getMyPlayer().getAnimation() != -1)) {
			return random(1200, 1500);
		}
		if (searchText(241, "yer foot")) {
			final RSObject Chest = getNearestObjectByID(42337);
			talkedto = true;
			atObject(Chest, "Open");
			return random(800, 1200);
		}
		if (getInterface(241).isValid()) {
		if (getInterface(241).containsText("haul") || getInterface(241).containsText("Just hop")) {
			done = true;
			log ("Finished CapnArnav Random ~ Exiting");
			if (canContinue()) {
				clickContinue();
				return random(600,700);
			}
			return random(500,700);
		}
		}
		if (done) {
			final RSObject Portal = getNearestObjectByID(11369);
			if (Portal == null) {
				log("Can't find portal!");
				turnToObject(Portal);
				return random(800, 1200);
			}
			if (atObject(Portal, "Enter")) {
				return random(4500,4900);
				} else {
					turnToObject(Portal);
					return random(600,700);
				}
		}
		if (RSInterface.getInterface(185).isValid()) {
			log("Setting default position. Coins Coins Coins!");
			while (!firstColFound) {
				while (!thirdColFound) {
					startValue3 = getSetting(809);
					wait(random(500, 700));
					clickMouse(random(295, 315), random(13, 33), true);
					wait(random(800, 1000));
					currValue3 = getSetting(809);
					if (currValue3 < startValue3) {
						thirdColFound = true;
					}
				}

				while (!secondColFound) {
					startValue2 = getSetting(809);
					wait(random(500, 700));
					clickMouse(random(203, 223), random(13, 33), true);
					wait(random(800, 1000));
					currValue2 = getSetting(809);
					if (currValue2 < startValue2) {
						secondColFound = true;
					}
				}

				while (!firstColFound) {
					startValue1 = getSetting(809);
					wait(random(500, 700));
					clickMouse(random(117, 137), random(13, 33), true);
					wait(random(800, 1000));
					currValue1 = getSetting(809);
					if (currValue1 < startValue1) {
						firstColFound = true;
					}
				}
			}
		}

		if (RSInterface.getInterface(185).isValid()) {
			if (searchText(185, "Bar")) {
				while (!reel1done) {
					clickMouse(random(117, 137), random(13, 33), true);
					wait(random(800, 1000));
					clickMouse(random(117, 137), random(13, 33), true);
					wait(random(800, 1000));
					log("Reel 1 Bar Found!");
					reel1done = true;
				}

				while (!reel2done) {
					clickMouse(random(203, 223), random(13, 33), true);
					wait(random(800, 1000));
					clickMouse(random(203, 223), random(13, 33), true);
					wait(random(800, 1000));
					log("Reel 2 Bar Found!");
					reel2done = true;
				}

				while (!reel3done) {
					clickMouse(random(295, 315), random(13, 33), true);
					wait(random(800, 1000));
					clickMouse(random(295, 315), random(13, 33), true);
					wait(random(800, 1000));
					log("Reel 3 Bar Found!");
					reel3done = true;
				}

				if (RSInterface.getInterface(185).isValid()) {
					atInterface(RSInterface.getInterface(185).getChild(28));
					wait(random(700, 1000));
				}
			}
		}

		if (RSInterface.getInterface(185).isValid()) {
			if (searchText(185, "Coins")) {
				while (!reel1done) {
					log("Reel 1 Coins Found!");
					reel1done = true;
				}

				while (!reel2done) {
					log("Reel 2 Coins Found!");
					reel2done = true;
				}

				while (!reel3done) {
					log("Reel 3 Coins Found!");
					reel3done = true;
				}

				if (RSInterface.getInterface(185).isValid()) {
					atInterface(RSInterface.getInterface(185).getChild(28));
					wait(random(700, 1000));
				}
			}
		}

		if (RSInterface.getInterface(185).isValid()) {
			if (searchText(185, "Bowl")) {
				while (!reel1done) {
					clickMouse(random(117, 137), random(13, 33), true);
					wait(random(800, 1000));
					log("Reel 1 Bowl Found!");
					reel1done = true;
				}

				while (!reel2done) {
					clickMouse(random(203, 223), random(13, 33), true);
					wait(random(800, 1000));
					log("Reel 2 Bowl Found!");
					reel2done = true;
				}

				while (!reel3done) {
					clickMouse(random(295, 315), random(13, 33), true);
					wait(random(800, 1000));
					log("Reel 3 Bowl Found!");
					reel3done = true;
				}

				if (RSInterface.getInterface(185).isValid()) {
					atInterface(RSInterface.getInterface(185).getChild(28));
					wait(random(700, 1000));
				}
			}
		}

		if (RSInterface.getInterface(185).isValid()) {
			if (searchText(185, "Ring")) {
				while (!reel1done) {
					clickMouse(random(117, 137), random(296, 316), true);
					wait(random(800, 1000));
					log("Reel 1 Ring Found!");
					reel1done = true;
				}

				while (!reel2done) {
					clickMouse(random(203, 223), random(296, 316), true);
					wait(random(800, 1000));
					log("Reel 2 Ring Found!");
					reel2done = true;
				}

				while (!reel3done) {
					clickMouse(random(295, 315), random(296, 316), true);
					wait(random(800, 1000));
					log("Reel 3 Ring Found!");
					reel3done = true;
				}

				if (RSInterface.getInterface(185).isValid()) {
					atInterface(RSInterface.getInterface(185).getChild(28));
					wait(random(700, 1000));
				}
			}
		}

		if (RSInterface.getInterface(228).isValid()) {
			final int x = random(220, 310), y = random(427, 437);
			clickMouse(x, y, true);
		}
		if (!myClickContinue() && !talkedto && !canContinue()) {
			atNPC(captain, "Talk-to");
			return random(500,700);
		}
		if (canContinue()) {
			clickContinue();
			return random(1000,1200);
		}
		if (!done && talkedto && RSInterface.getInterface(185).isValid() == false && RSInterface.getInterface(241).isValid() == false && !canContinue() && getMyPlayer().isInteractingWithLocalPlayer() == false) {
			atNPC(captain, "Talk-to");
			return random(500,700);
		}
		return random(1000, 1500);
	}

	public boolean myClickContinue() {
		wait(random(800, 1000));
		return atInterface(243, 7) || atInterface(241, 5) || atInterface(242, 6) || atInterface(244, 8) || atInterface(64, 5) || atInterface(236, 1) || atInterface(230, 4) || atInterface(228, 3);
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
}