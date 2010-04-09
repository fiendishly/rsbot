package org.rsbot.script.randoms;

import org.rsbot.script.Constants;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;

@ScriptManifest(authors = { "ToshiXZ" }, name = "Teleblock Closer", version = 1.0)
public class TeleotherCloser extends Random {

	@Override
	public boolean activateCondition() {
		final RSInterface iface = RSInterface.getInterface(326);
		return iface.isValid() && iface.getChild(2).getText().contains("wants to teleport");
	}

	@Override
	public int loop() {
		atInterface(RSInterface.getInterface(326).getChild(8));
		wait(random(500, 750));
		openTab(Constants.TAB_OPTIONS);
		wait(random(500, 750));
		log.info("Disabling accept aid");
		clickMouse(random(569, 603), random(415, 440), false);
		return -1;
	}
}
