package org.rsbot.script.randoms;

import java.util.LinkedList;
import java.util.List;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;

/**
 * This script will close: Stats; Report; GravrStone; PIN settings; Graphics; Quest; Quick Chat help;
 * Price Check; GE Collection Box; Clan chat Setup; Bank Helper; Audio; Death Items or World map Interface!
 * Official thread on forums: http://www.rsbot.org/vb/showthread.php?t=5099
 * Last updated 15.05.2010. 
 */
@ScriptManifest(authors = { "HeyyamaN" }, name = "Interface Closer", version = 1.6)
public class CloseAllInterface extends Random {
	
	private List<RSInterfaceChild> components = new LinkedList<RSInterfaceChild>();
	
	{
		addChild(743, 20); // Audio
		addChild(767, 10); // Bank help
		addChild(499, 29); // Stats
		addChild(594, 48); // Report
		addChild(742, 81); // Graphics
		addChild(275,  8); // Quest
		addChild(206, 16); // Price check
		addChild(266, 11); // Grove
		addChild(109, 13); // GE collection box
		addChild(102, 13); // Death items
		addChild(590,  3); // Clan setup
		addChild( 14,  3); // Pin settings
		addChild(157, 13); // Quick chat help
		addChild(764,  2); // Objectives
		addChild(895, 19); // Advisor
	}
	
	private void addChild(int parent, int idx) {
		components.add(RSInterface.getInterface(parent).getChild(idx));
	}

	public boolean activateCondition() {
		if (isLoggedIn()) {
			if (RSInterface.getInterface(755).getChild(45).isValid()) { // World map
				if (RSInterface.getChildInterface(755, 0).getComponents().length > 0) {
					return true;
				}
			}
			for (RSInterfaceChild c : components) {
				if (c.isValid()) {
					return true;
				}
			}
		}
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * TODO: Avoid literal coordinate values.
	 */
	public int loop() {
		if (RSInterface.getInterface(755).isValid() && (RSInterface.getChildInterface(755, 0).getComponents().length > 0)) {
			clickMouse(747 + random(-5, 5), 18 + random(-4, 4), true); // World map
			return random(500, 900);
		}

		if (RSInterface.getInterface(743).isValid()) {
			clickMouse(327 + random(-5, 5), 54 + random(-4, 4), true); // Audio
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(767).isValid()) {
			clickMouse(489 + random(-5, 5), 35 + random(-4, 4), true); // Bank Help
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
			clickMouse(498 + random(-5, 5), 13 + random(-4, 4), true); // Graphics
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(275).isValid()) {
			clickMouse(465 + random(-5, 5), 76 + random(-4, 4), true); // Quest
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(206).isValid()) {
			clickMouse(485 + random(-5, 5), 34 + random(-4, 4), true); // Price Check
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(266).isValid()) {
			clickMouse(495 + random(-5, 5), 25 + random(-4, 4), true); // Grave
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(109).isValid()) {
			clickMouse(426 + random(-5, 5), 74 + random(-4, 4), true); // GE Collection Box
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(102).isValid()) {
			clickMouse(493 + random(-5, 5), 30 + random(-4, 4), true); // Death items
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(590).isValid()) {
			clickMouse(487 + random(-5, 5), 44 + random(-4, 4), true); // Clan Setup
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(14).isValid()) {
			atInterface(RSInterface.getInterface(14).getChild(3)); // PIN settings
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(157).isValid()) {
			clickMouse(484 + random(-5, 5), 42 + random(-4, 4), true); // Quick Chat help
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(764).isValid()) {
			clickMouse(490 + random(-4, 4), 15 + random(-4, 4), true); // Objectives
			wait(random(500, 900));
		}
		if (RSInterface.getInterface(895).isValid()) {
			clickMouse(494 + random(-4, 4), 15 + random(-4, 4), true); // Advisor
			wait(random(500, 900));
		}

		return -1;
	}

}