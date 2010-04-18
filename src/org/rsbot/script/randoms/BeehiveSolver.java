package org.rsbot.script.randoms;

import java.awt.Point;
import java.awt.Rectangle;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;

/**
 * Update by Iscream (Apr 15,2010)
 * @author Pwnaz0r & Velocity
 * @version 2.3 - 04/03/09
 */
@ScriptManifest(authors = { "Pwnaz0r", "Velocity" }, name = "Bee Hive Random", version = 2.4)
public class BeehiveSolver extends Random {

	RSNPC BeehiveKeeper;
	private static int BEEHIVE_KEEPER_ID = 8649;
	private static int[] DEST_INTERFACE_IDS = { 16, 17, 18, 19 };
	private static int ID_DOWN = 16034;
	private static int ID_MIDDOWN = 16022;
	private static int ID_MIDUP = 16025;
	private static int ID_TOP = 16036;
	private static int[] BEEHIVE_ARRAYS = { ID_TOP, ID_MIDUP, ID_MIDDOWN, ID_DOWN };
	RSInterfaceChild INTERFACE_BUILDBEEHIVE = getInterface(420, 40);
	private static String[] MODEL_NAMES = { "Top", "Middle Up", "Middle Down", "Down" };
	boolean solved;
	private static int[] START_INTERFACE_IDS = { 12, 13, 14, 15 };

	@Override
	public boolean activateCondition() {
		if (!isLoggedIn())
			return false;

		BeehiveKeeper = getNearestNPCByID(BEEHIVE_KEEPER_ID);
		if ((BeehiveKeeper != null) || getBeehiveInterface().isValid()) {
			wait(random(1000, 1500));
			BeehiveKeeper = getNearestNPCByID(BEEHIVE_KEEPER_ID);
			if ((BeehiveKeeper != null) || getBeehiveInterface().isValid()) {
				solved = false;
				wait(random(1000, 1500));
				return true;
			}
		}

		return false;
	}

	public boolean dragInterfaces(final RSInterfaceChild child1, final RSInterfaceChild child2) {
		final Point start = returnMidInterface(child1);
		final Point finish = returnMidInterface(child2);

		moveMouse(start);
		dragMouse(finish);
		return true;
	}

	public RSInterface getBeehiveInterface() {
		return getInterface(420);
	}

	@Override
	public int loop() {
		if (solved) {
			wait(random(1000, 1500));
			solved = false;
			return -1;
		}
		BeehiveKeeper = getNearestNPCByID(BEEHIVE_KEEPER_ID);
		if (BeehiveKeeper == null) {
			log.severe("Could not find beekeeper.");
			return -1;
		}

		if (myClickContinue())
			return 200;

		if (atInterface(236, 2))
			return random(800, 1200);

		if (getBeehiveInterface().isValid()) {
			for (int i = 1; i < 5; i++) {
				log.info("Checking ID: " + i);
				final int id = returnIdAtSlot(i);
				dragInterfaces(getBeehiveInterface().getChild(START_INTERFACE_IDS[i - 1]), getBeehiveInterface().getChild(returnDragTo(id)));
			}
			solved = true;
			if (atInterface(INTERFACE_BUILDBEEHIVE))
				return random(900, 1600);
			log.info("Returning -1");
			return -1;
		} else {
			log.info("Interfaces not valid.");
		}

		if (getMyPlayer().getInteracting() == null) {
			final RSNPC npc = getNearestNPCByID(BEEHIVE_KEEPER_ID);
			if (npc != null) {
				if (!clickCharacter(npc, "Talk-to")) {
					setCameraRotation(getCameraAngle() + random(-30, 30));
				}
			}
		}

		return random(500, 1000);
	}

	public boolean myClickContinue() {
		wait(random(800, 1000));
		return atInterface(243, 7) || atInterface(241, 5) || atInterface(242, 6) || atInterface(244, 8) || atInterface(64, 5);
	}

	public int returnDragTo(final int Model) {
		switch (Model) {
			case 16036:
				return DEST_INTERFACE_IDS[0];
			case 16025:
				return DEST_INTERFACE_IDS[1];
			case 16022:
				return DEST_INTERFACE_IDS[2];
			case 16034:
				return DEST_INTERFACE_IDS[3];
			default:
				return -1;
		}
	}

	public int returnIdAtSlot(final int slot) {
		if ((slot < 1) || (slot > 4)) {
			log.info("Invalid Slot.");
			stopScript();
		}

		int Model_ID = getBeehiveInterface().getChild(returnSlotId(slot)).getModelID();

		if (Model_ID == -1) {
			log.info("Could not retrieve ID.");
			atInterface(INTERFACE_BUILDBEEHIVE);
			stopScript();
		}

		for (int i = 0; i < BEEHIVE_ARRAYS.length; i++) {
			if (Model_ID == BEEHIVE_ARRAYS[i]) {
				log.info("Slot " + slot + " contains section: " + MODEL_NAMES[i]);
				return Model_ID;
			}
		}

		return -1;
	}

	public Point returnMidInterface(final RSInterfaceChild child) {
		Point point = new Point(-1, -1);
		final Rectangle rect = child.getArea();
		if (rect != null) {
			point = new Point((int) rect.getCenterX(), (int) rect.getCenterY());
		}
		return point;
	}

	public int returnSlotId(final int slot) {
		switch (slot) {
			case 1:
				return 25;
			case 2:
				return 22;
			case 3:
				return 23;
			case 4:
				return 21;
			default:
				log.info("Invalid slot ID.");
				stopScript();
				break;
		}
		return -1;
	}
}