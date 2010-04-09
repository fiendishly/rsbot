package org.rsbot.script.randoms;

import org.rsbot.bot.Bot;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;

/**
 * @author Qauters Solves Sandwhich Lady Random, Drizzt1112 AND TwistedMind 
 */
/*
 * Updated by Iscream (Feb 06,10)
 * Updated by Iscream (Feb 07,10)
 * Updated by TwistedMind (Feb 7, '10) ~ Not exiting
 * Updated by Iscream (Feb 20,10)
 */
@ScriptManifest(authors = { "Qauters", "Drizzt1112", "TwistedMind" }, name = "Sandwhich Lady", version = 2.3)
public class SandwhichLady extends Random {
	final static int ID_InterfaceSandwhichWindow = 297;
	final static int ID_InterfaceSandwhichWindowText = 48;
	final static int ID_InterfaceTalk = 243;
	final static int ID_InterfaceTalkText = 7;
	final static int[] ID_Items = { 10728, 10732, 10727, 10730, 10726, 45666, 10731 };
	final static int ID_SandwhichLady = 8630;
	final static String[] Name_Items = { "chocolate", "triangle", "roll", "pie", "baguette", "doughnut", "square" };
	final boolean DEBUG = true; // Set to true for more info!

	@Override
	public boolean activateCondition() {
		final RSNPC lady = getNearestNPCByID(SandwhichLady.ID_SandwhichLady);
		if (lady == null) {
			return false;
		}
		if (lady != null) {
			return true;
		}
		return false;
	}

	@Override
	public int loop() {
		final RSInterface Chat1 = RSInterface.getInterface(243);
		if (Chat1.isValid()) {
			atInterface(243, 7);
			return random(900, 1200);
		}
		if (!activateCondition()) {
			return -1;
		}
		//Leaves random
		int[] portalID = {12731, 11373};
		if(RSInterface.getInterface(242).getChild(4).containsText("The exit portal's")){
			RSObject portal = getNearestObjectByID(portalID);
			if(portal != null){
				if(!tileOnScreen(portal.getLocation())){
					walkTileOnScreen(portal.getLocation());
				}else{
					atObject(portal, "Enter");
					return random(1000,1500);
				}
			}
		}
		// Check if we need to press continue, on the talk interface
		if (Bot.getClient().getValidRSInterfaceArray()[SandwhichLady.ID_InterfaceTalk]) {
			atInterface(SandwhichLady.ID_InterfaceTalk, SandwhichLady.ID_InterfaceTalkText);
			return random(900, 1200);
		}

		// Check if the sandwhich window is open
		if (Bot.getClient().getValidRSInterfaceArray()[SandwhichLady.ID_InterfaceSandwhichWindow]) {
			final RSInterface window = RSInterface.getInterface(SandwhichLady.ID_InterfaceSandwhichWindow);
			int offset = -1;
			final String txt = window.getChild(SandwhichLady.ID_InterfaceSandwhichWindowText).getText();
			for (int off = 0; off < SandwhichLady.Name_Items.length; off++) {
				if (txt.contains(SandwhichLady.Name_Items[off])) {
					offset = off;
					if (DEBUG) {
						log.info("Found: " + SandwhichLady.Name_Items[off] + " - ID: " + SandwhichLady.ID_Items[off]);
					}
				}
			}
			for (int i = 7; i < 48; i++) {
				final RSInterfaceChild inf = window.getChild(i);

				if (DEBUG) {
					log.info("child[" + i + "] ID: " + inf.getModelID() + " == " + SandwhichLady.ID_Items[offset]);
				}

				if (inf.getModelID() == SandwhichLady.ID_Items[offset]) {
					atInterface(inf);
					wait(random(900, 1200)); // Yea, use a wait here! (Wait's
					// are allowed in randoms.)

					if (!Bot.getClient().getValidRSInterfaceArray()[SandwhichLady.ID_InterfaceSandwhichWindow]) {
						log.info("Solved the Sandwich Lady, by eating a " + SandwhichLady.Name_Items[offset]);
						return random(6000, 7000);
					}
				}

			}
			return random(900, 1200);
		}
		final RSInterface Chat2 = RSInterface.getInterface(242);
		if (Chat2.isValid()) {
			atInterface(242, 6);
			return random(900, 1200);
		}
		// Talk to the lady
		final RSNPC lady = getNearestNPCByID(SandwhichLady.ID_SandwhichLady);
		if (lady.getAnimation() == -1) {
			if(lady != null){
				if(!tileOnScreen(lady.getLocation())){
					walkTileOnScreen(lady.getLocation());
				} else {
					clickCharacter(lady, "Talk");
					return random(1000,1500);
				}
			}
		}
		return random(900, 1200);
	}

}