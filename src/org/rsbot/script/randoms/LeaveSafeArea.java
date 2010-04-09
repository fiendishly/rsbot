package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;

@ScriptManifest(name = "Leave Safe Area", authors = "Taha", version = 1.0)
public class LeaveSafeArea extends Random {

	@Override
	public boolean activateCondition() {
		return (getInterface(212, 2).containsText("things can get more") && (getInterface(212, 2).getAbsoluteY() > 380) && (getInterface(212, 2).getAbsoluteY() < 410)) || (getInterface(236, 1).containsText("the starting area") && (getInterface(236, 1).getAbsoluteY() > 390) && (getInterface(236, 1).getAbsoluteY() < 415));
	}

	@Override
	public int loop() {
		if (canContinue()) {
			clickContinue();
			wait(random(1000, 1200));
		}
		atInterface(236, 1);
		return -1;
	}

}
