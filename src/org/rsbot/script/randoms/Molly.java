package org.rsbot.script.randoms;

import java.awt.Point;
import java.util.ArrayList;

import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/*
 * This Script is PwnZ's
 * RcZhang converted it from another bot -> RSBot
 * Updated by aman (Nov 14, 09)
 * Updated by Equilibrium (Dec 13, 09)
 * Updated by Fred (Dec 25, 09)
 * Updated by Iscream(Jan 31, 10)
 * Updated by Iscream(Feb 20, 10)
 */
@ScriptManifest(authors = { "PwnZ", "RcZhang", "aman", "Equilibrium", "Taha", "Fred", "Iscream" }, name = "Molly Solver", version = 1.9)
		
public class Molly extends Random {
	private RSNPC molly;
	private RSObject controlPanel;
	private int mollyID = -1;
	private boolean cameraSet;
	static final int CLAW_ID = 14976;
	static final int CONTROL_PANEL_ID = 14978;
	static final int DOOR_ID = 14982;
	static final int MOLLY_CHATBOX_INTERFACEGROUP = 228;
	static final int MOLLY_CHATBOX_NOTHANKS = 3;
	static final int CONTROL_INTERFACEGROUP = 240;
	static final int CONTROLS_GRAB = 28;
	static final int CONTROLS_UP = 29;
	static final int CONTROLS_DOWN = 30;
	static final int CONTROLS_LEFT = 31;
	static final int CONTROLS_RIGHT = 32;
	static final int CONTROLS_EXIT = 33;
	private boolean talkedToMolly;
	private boolean finished;
	private long delayTime;

	@Override
	public boolean activateCondition() {
		return canRun();
	}

	public boolean canRun() {
		molly = getNearestNPCByName("Molly");
		controlPanel = getNearestObjectByID(Molly.CONTROL_PANEL_ID);
		return (molly != null && molly.isInteractingWithLocalPlayer()) || (controlPanel != null);
	}

	private boolean inControlInterface() {
		final RSInterface i = getInterface(Molly.CONTROL_INTERFACEGROUP);
		return (i != null) && i.isValid();
	}

	private boolean inControlRoom() {
		final RSObject o = getNearestObjectByID(DOOR_ID);
		return (o != null) && (getMyPlayer().getLocation().getX() > o.getLocation().getX());
	}

	@Override
	public int loop() {
		if (!canRun()) {
			log("Molly random finished!");
			return -1;
		}
		controlPanel = getNearestObjectByID(Molly.CONTROL_PANEL_ID);
		while (getMyPlayer().isMoving() || (getMyPlayer().getAnimation() != -1)) {
			wait(random(800, 1300));
		}
		if (mollyID == -1) {
			mollyID = molly.getID();
			log("Molly ID: " + Integer.toString(mollyID));
			log("Evil Twin ID:" + Integer.toString(mollyID - 40));
		}
		if (canContinue()) {
			setCamera();
			clickContinue();
			return random(500,800);
		}
		final RSInterfaceChild noThanksInterface = getInterface(Molly.MOLLY_CHATBOX_INTERFACEGROUP).getChild(Molly.MOLLY_CHATBOX_NOTHANKS);
		if ((noThanksInterface != null) && noThanksInterface.isValid()) {
			setCamera();
			wait(random(800, 1200));
			atInterface(noThanksInterface);
			return random(600, 1000);
		}
		if (!cameraSet) {
			setCameraAltitude(true);
			cameraSet = true;
			return (random(300, 500));
		}
		if (finished && !inControlRoom()) {
			atNPC(molly, "Talk");
			return (random(1000,1200));
		}
		if (finished && inControlRoom()) {
			if (!openDoor()) {
				walktodoor();
				return (random(400,500));
			}
			return (random(400,600));
		}
		if (!inControlRoom()) {
			if (talkedToMolly && !finished && RSInterface.getInterface(Molly.MOLLY_CHATBOX_INTERFACEGROUP).isValid() == false && RSInterface.getInterface(Molly.MOLLY_CHATBOX_NOTHANKS).isValid() == false) {
				openDoor();
				wait(random(800, 1200));
			} else {
				atNPC(molly, "Talk");
				talkedToMolly = true;
				wait(random(800, 1200));
			}
		} else {
			if (getNearestNPCByName("Molly") != null) {
				finished = true;
				wait(random(800, 1200));
			} else {
				if (!inControlInterface()) {
					if (tileOnScreen(controlPanel.getLocation())) {
						atObject(controlPanel, "Use");
						wait(random(1200, 2000));
					} else {
						walkTileOnScreen(controlPanel.getLocation());
						setCameraAltitude(true);
						turnToObject(controlPanel);
					}
				} else {
					navigateClaw();
					delayTime = System.currentTimeMillis();
					while (!canContinue() && (System.currentTimeMillis() - delayTime < 15000)) {
					}
					if (canContinue()) {
						clickContinue();
					}
					wait(random(300, 400));
				}
			}
		}
		return random(200, 400);
	}

	private void walktodoor() {
		final RSObject door = getNearestObjectByID(Molly.DOOR_ID);
		if (door == null)
			return;
		final RSTile loc = door.getLocation();
		final RSTile counter = new RSTile(loc.getX() + 1, loc.getY());
		walkTileOnScreen(counter);
	}
	private void navigateClaw() {
		if (!inControlInterface() || (mollyID < 1))
			return;
		RSObject claw;
		RSNPC suspect;
		if (((claw = getNearestObjectByID(Molly.CLAW_ID)) == null) || ((suspect = getNearestNPCByID(mollyID - 40)) == null))
			return;
		while (getNearestNPCByID(mollyID - 40) != null && (claw = getNearestObjectByID(Molly.CLAW_ID)) != null && (suspect = getNearestNPCByID(mollyID - 40)) != null) {
			claw = getNearestObjectByID(Molly.CLAW_ID);
			suspect = getNearestNPCByID(mollyID - 40);
			final RSTile clawLoc = claw.getLocation();
			final RSTile susLoc = suspect.getLocation();
			final ArrayList<Integer> options = new ArrayList<Integer>();
			if (susLoc.getX() > clawLoc.getX()) {
				options.add(Molly.CONTROLS_LEFT);
			}
			if (susLoc.getX() < clawLoc.getX()) {
				options.add(Molly.CONTROLS_RIGHT);
			}
			if (susLoc.getY() > clawLoc.getY()) {
				options.add(Molly.CONTROLS_DOWN);
			}
			if (susLoc.getY() < clawLoc.getY()) {
				options.add(Molly.CONTROLS_UP);
			}
			if (options.isEmpty()) {
				options.add(Molly.CONTROLS_GRAB);
			}
			final RSInterface i = getInterface(Molly.CONTROL_INTERFACEGROUP);
			if ((i != null) && i.isValid()) {
				atInterface(i.getChild(options.get(random(0, options.size()))));
			}
			delayTime = System.currentTimeMillis();
			while (!hasClawMoved(clawLoc) && (System.currentTimeMillis() - delayTime < 3500)) {
				wait(10);
			}
		}
		return;
	}

	private boolean hasClawMoved(RSTile prevClawLoc) {
		if (getNearestObjectByID(Molly.CLAW_ID) == null)
			return false;
		RSTile currentClawLoc = getNearestObjectByID(Molly.CLAW_ID).getLocation();
		if ((currentClawLoc.getX() - prevClawLoc.getX() != 0) || (currentClawLoc.getY() - prevClawLoc.getY() != 0))
			return true;
		return false;
	}

	private boolean openDoor() {
		final RSObject door = getNearestObjectByID(Molly.DOOR_ID);
		if (door == null)
			return false;
		final RSTile loc = door.getLocation();
		final RSTile counter = new RSTile(loc.getX() + 1, loc.getY());
		int i = 0;
		while (i < 20) {
			i++;
			if (i % 5 == 0) {
				setCameraRotation(random(0, 359));
			}
			final Point midpoint = new Point((int) ((Calculations.tileToScreen(loc).getX() + Calculations.tileToScreen(counter).getX()) / 2), (int) ((Calculations.tileToScreen(loc).getY() + Calculations.tileToScreen(counter).getY()) / 2));
			moveMouse(midpoint, (int) (midpoint.getX() - Calculations.tileToScreen(loc).getX()), 5);
			wait(random(300, 500));
			try {
				for (final String s : getMenuActions()) {
					if (s.contains("pen")) {
						final boolean b = atMenu("Open");
						wait(random(600, 1200));
						return b;
					}
				}
			} catch (final NullPointerException noMenuActions) {
				return false;
			}
		}
		return false;
	}

	private void setCamera() {
		if ((random(0, 6) == 3) && !cameraSet) {
			setCameraAltitude(true);
			cameraSet = true;
		}
	}
}