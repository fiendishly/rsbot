package org.rsbot.script.randoms;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
/*
 * Updated by Iscream Feb 22,10
 */
@ScriptManifest(authors = { "Nightmares18", "joku.rules", "Taha", "Fred" }, name = "Frog Cave", version = 2.3)
public class FrogCave extends Random {
	private RSNPC frog;
	private boolean talkedToHerald;
	private int tries;

	@Override
	public boolean activateCondition() {
		if (!isLoggedIn())
			return false;
		else if ((getNearestNPCByName("Frog Herald") != null) && (getNearestObjectByID(5917) != null)) {
			wait(random(2000, 3000));
			return (getNearestNPCByName("Frog Herald") != null) && (getNearestObjectByID(5917) != null);
		}
		return false;
	}

	private RSNPC findFrog() {
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
		for (final int npcIndex : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), npcIndex);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC npc = new RSNPC(((RSNPCNode) node).getRSNPC());
			try { // checking for name doesn't work, because getName() returns
				// "null"
				if (npc.isMoving()) {
					//log("NPC Moving, Unable To Check Height"); - Script was in constant loop because of this. It will now skip moving npcs.
					//It will find the princess as soon as she stops moving.
					continue;
				}
				if (// npc.getName().equals("Frog") &&
				npc.getHeight() == -68) {
					return npc;
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public int loop() {
		try {
			if (!activateCondition()) {
				talkedToHerald = false;
				frog = null;
				tries = 0;
				return -1;
			}
			if (canContinue()) {
				talkedToHerald = true;
				clickContinue();
				return random(600, 800);
			}
			if (getMyPlayer().isMoving()) {
				return random(600, 800);
			}
			if (!talkedToHerald) {
				final RSNPC herald = getNearestNPCByName("Frog Herald");
				if (distanceTo(herald) < 5) {
					if (!tileOnScreen(herald.getLocation())) {
						turnToCharacter(herald);
					}
					clickRSNPC(herald, "Talk-to");
					return random(500,1000);
				} else {
					walkTo(herald.getLocation());
					return random(500, 700);
				}
			}
			if (frog == null) {
				frog = findFrog();
				if (frog != null) {
				log("Princess found! ID: " + frog.getID());
				}
			}
			if (frog != null && frog.getLocation() != null) {
				if (distanceTo(frog) < 5) {
					if (!tileOnScreen(frog.getLocation())) {
						turnToCharacter(frog);
					}
					clickRSNPC(frog, "Talk-to", "Frog");
					return random(900,1000);
				} else {
					walkTo(frog.getLocation());
					return random(500, 700);
				}
			} else {
				tries++;
				if (tries > 200) {
					tries = 0;
					talkedToHerald = false;
				}
				return random(200, 400);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return random(200, 400);
	}
}