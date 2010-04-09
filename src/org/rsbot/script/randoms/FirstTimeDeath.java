package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Taha" }, name = "First Time Death", version = 1.1)
public class FirstTimeDeath extends Random {
	private int step, portalID = 45803;
	private boolean exit;
	private RSNPC reaper;

	@Override
	public boolean activateCondition() {
		return ((reaper = getNearestNPCByID(8869)) != null) || ((reaper = getNearestNPCByID(8870)) != null);
	}

	@Override
	public int loop() {
		if (!activateCondition())
			return -1;
		setCameraAltitude(true);
		if (canContinue() && !exit) {
			if (getInterface(241, 4).getText().contains("Yes?")) {
				step++;
				exit = true;
				return random(200, 400);
			} else if (getInterface(242, 5).getText().contains("Enjoy!")) {
				step++;
				exit = true;
			}
			clickContinue();
			return random(200, 400);
		}
		switch (step) {
			case 0:
				RSObject reaperChair = getNearestObjectByID(45802);
				atObject(reaperChair, "Talk-to");
				wait(random(1000, 1200));
				if (!canContinue()) {
					walkTileOnScreen(new RSTile(reaper.getLocation().getX() + 2, reaper.getLocation().getY() + 1));
					turnToObject(reaperChair);
				}
				break;

			case 1:
				RSObject portal = getNearestObjectByID(portalID);
				RSTile loc = getMyPlayer().getLocation();
				atObject(portal, "Enter");
				wait(random(1000, 1200));
				if (distanceTo(loc) < 10) {
					turnToObject(portal);
					if (!tileOnScreen(portal.getLocation())) {
						walkTileOnScreen(portal.getLocation());
					}
				}
				break;
		}
		return random(200, 400);
	}

	@Override
	public void onFinish() {
	}

}
