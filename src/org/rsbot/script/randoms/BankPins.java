package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;

@ScriptManifest(authors = { "Holo", "Gnarly" }, name = "Bank pins manager", version = 2.1)
public class BankPins extends Random {

	private String pin;

	@Override
	public boolean activateCondition() {
		pin = getAccountPin();
		if (RSInterface.getInterface(13).isValid()) {
			if ((pin == null) || (pin.length() != 4)) {
				log.severe("You must add a bank pin to your account.");
				stopScript();
			} else {
				return true;
			}
		}
		return false;
	}

	public void enterCode(final String pin) {
		if (!RSInterface.getInterface(13).isValid())
			return;
		final RSInterfaceChild[] children = RSInterface.getInterface(13).getChildren();
		int state = 0;
		for (int i = 21; i < 25; i++) {
			if (children[i].containsText("?")) {
				state++;
			}
		}
		state = 4 - state;
		for (int i = 11; i < 21; i++) {
			if (children[i].containsText(pin.substring(state, state + 1))) {
				atInterface(children[i - 10]);
				wait(random(500, 1000));
				break;
			} else {
				if (random(0, 5) == 0) {
					moveMouseSlightly();
				}
			}
		}
	}

	@Override
	public int loop() {
		if (RSInterface.getInterface(14).isValid() || !RSInterface.getInterface(13).isValid()) {
			atInterface(RSInterface.getInterface(14).getChild(3));
			return -1;
		}
		enterCode(pin);
		if (RSInterface.getInterface(211).isValid()) {
			atInterface(RSInterface.getInterface(211).getChild(3));
		} else if (RSInterface.getInterface(217).isValid()) {
			wait(random(10500, 12000));
		}
		return 500;
	}
}