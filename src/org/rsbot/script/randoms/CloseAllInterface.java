package org.rsbot.script.randoms;

//Hello! This script will close: Stats; Report; GravrStone; PIN settings; Graphics; Quest; Quick Chat help; -->
//  -->  Price Check; GE Collection Box; Clan chat Setup; Bank Helper; Audio; Death Items or World map Interface!

/**
 IMPORTANT! If you do have any of these files, mentioned below, in AntiBan or AntiRandom folder, then DELETE them! */
//DELETE these from AntiBan or AntiRandom folder if you haven't yet!---> "StatCloser.java & StatCloser.class" ;  -->
//  -->  "ReportCloser.java & ReportCloser.class" ; "GraphicsCloser.java & GraphicsCloser.class" ; "CloseWorldMap.java & CloseWorldMap.class" ;  -->
//  -->  "CloseGrave.java & CloseGrave.class" ; "BankHelpCloser.java & BankHelpCloser.class" ; "AudioCloser.java & AudioCloser.class"
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;

/**
 * Offical Threads in Forum: http://www.rsbot.org/vb/showthread.php?t=5099
 */

@ScriptManifest(authors = { "HeyyamaN" }, name = "Interface Closer", version = 1.5)
public class CloseAllInterface extends Random {

	@Override
	public boolean activateCondition() {
		if (!isLoggedIn())
			return false;
		if (RSInterface.getInterface(755).getChild(45).isValid()) { // World map
			if (RSInterface.getChildInterface(755, 0).getComponents().length > 0)
				return true;
		}
		if (RSInterface.getInterface(743).getChild(20).isValid())
			return true;
		if (RSInterface.getInterface(767).getChild(10).isValid())
			return true;
		if (RSInterface.getInterface(499).getChild(29).isValid())
			return true;
		if (RSInterface.getInterface(594).getChild(48).isValid())
			return true;
		if (RSInterface.getInterface(742).getChild(25).isValid())
			return true;
		if (RSInterface.getInterface(275).getChild(8).isValid())
			return true;
		if (RSInterface.getInterface(206).getChild(16).isValid())
			// Check
			return true;
		if (RSInterface.getInterface(266).getChild(11).isValid())
			return true;
		if (RSInterface.getInterface(109).getChild(13).isValid())
			// Collection
			// Box
			return true;
		if (RSInterface.getInterface(102).getChild(13).isValid())
			// items
			return true;
		if (RSInterface.getInterface(590).getChild(3).isValid())
			return true;
		if (RSInterface.getInterface(14).getChild(3).isValid())
			// settings
			return true;
		if (RSInterface.getInterface(157).getChild(13).isValid())
			// Chat help
			return true;

		return false;
	}

	@Override
	public int loop() {
		if (RSInterface.getInterface(755).isValid() && (RSInterface.getChildInterface(755, 0).getComponents().length > 0)) {
			clickMouse(747 + random(-5, 5), 18 + random(-4, 4), true); // World
			// map
			return random(500, 900);
		}

		if (RSInterface.getInterface(743).isValid()) {
			clickMouse(327 + random(-5, 5), 54 + random(-4, 4), true); // Audio
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(767).isValid()) {
			clickMouse(489 + random(-5, 5), 35 + random(-4, 4), true); // Bank
			// Help
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(499).isValid()) {
			clickMouse(499 + random(-5, 5), 21 + random(-4, 4), true); // Stats
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(594).isValid()) {
			clickMouse(441 + random(-5, 5), 67 + random(-4, 4), true); // Report
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(742).isValid()) {
			clickMouse(493 + random(-5, 5), 36 + random(-4, 4), true); // Graphics
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(275).isValid()) {
			clickMouse(465 + random(-5, 5), 76 + random(-4, 4), true); // Quest
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(206).isValid()) {
			clickMouse(485 + random(-5, 5), 34 + random(-4, 4), true); // Price
			// Check
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(266).isValid()) {
			clickMouse(495 + random(-5, 5), 25 + random(-4, 4), true); // Grave
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(109).isValid()) {
			clickMouse(426 + random(-5, 5), 74 + random(-4, 4), true); // GE
			// Collection
			// Box
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(102).isValid()) {
			clickMouse(493 + random(-5, 5), 30 + random(-4, 4), true); // Death
			// items
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(590).isValid()) {
			clickMouse(487 + random(-5, 5), 44 + random(-4, 4), true); // Clan
			// Setup
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(14).isValid()) {
			atInterface(RSInterface.getInterface(14).getChild(3)); // PIN
			// settings
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(157).isValid()) {
			clickMouse(484 + random(-5, 5), 42 + random(-4, 4), true); // Quick
			// Chat
			// help
			wait(random(500, 900));
		}

		return -1;
	}

}
