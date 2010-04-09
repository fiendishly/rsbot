package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSObject;
/*
 * Updated by Iscream(Feb 3, 10)
 * Updated by Twistedmind(Feb 4, 10) Small camera turning issue...
 * Updated by Iscream(Feb 5, 10)
 * Updated by TwistedMind(Feb 7, '10) "What have you guys been smoking??? I cleaned the code and it worked again... Why atTile if there's atObject?"
 */
@ScriptManifest(authors = { "Aelin", "LM3", "IceCandle", "Taha", "Twistedmind", "Iscream" }, name = "Pinball", version = 2.7)

public class Pinball extends Random {

	private final int IFACE_PINBALL = 263;
	private final int OBJ_EXIT = 15010;
	private final int[] OBJ_PILLARS = { 15000, 15002, 15004, 15006, 15008 };

	@Override
	public boolean activateCondition() {
		return (isLoggedIn() && (getPillar() != null)) || (getNearestObjectByID(OBJ_PILLARS) != null) || (getNearestObjectByID(15007) != null);
	}


	private RSObject getPillar() {
		return getNearestObjectByID(OBJ_PILLARS);
	}

	private int getScore() {
		RSInterfaceChild score = RSInterface.getChildInterface(IFACE_PINBALL, 1);
		try{
			return Integer.parseInt(score.getText().split(" ")[1]);
		}catch(java.lang.ArrayIndexOutOfBoundsException t){
			return 10;
		}
	}

	@Override
	public int loop() {
		if (!activateCondition()) {
			return -1;
		}
		if (canContinue()) {
			clickContinue();
			return random(1000, 1200);
		}
		if (getMyPlayer().isMoving() || (getMyPlayer().getAnimation() != -1)) {
			return random(1000, 1600);
		}
		if (getScore() >= 10) {
			RSObject exit = getNearestObjectByID(OBJ_EXIT);
			if (exit != null) {
				if (tileOnScreen(exit.getLocation()) && atTile(exit.getLocation(), "Exit")) {
					wait(random(2000, 2200));
					atObject(exit, "Exit");
					return random(1000,1200);
				} else {
					setCompass('s');
					walkTileOnScreen(exit.getLocation());
					return random(1400,1500);
				}
			}
		}
		if (getNearestObjectByID(OBJ_PILLARS) != null) {
			if (!tileOnScreen(getNearestObjectByID(OBJ_PILLARS).getLocation())) {
				walkTileOnScreen(getNearestObjectByID(OBJ_PILLARS).getLocation());
				return random(500,600);
			}
			wait(random(400,500));
			atObject(getNearestObjectByID(OBJ_PILLARS), "Tag");
			for(int i = 0; i < 2; i++){
				if(getMyPlayer().getInteracting() != null){
					i = 0;
				}
				wait(random(936,1259));//Randomness ftw, I was bored :P
			}
			return random(1000,1300);
		}
		return random(200, 400);
	}

}