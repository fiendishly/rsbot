package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Keilgo" }, name = "Drill Demon", version = 0.1)
public class DrillDemon extends Random {

	public int demonID = 2790;
	public int sign1;
	public int sign2;
	public int sign3;
	public int sign4;

	@Override
	public boolean activateCondition() {
		return playerInArea(3167, 4822, 3159, 4818);
	}

	@Override
	public int loop() {
		setCameraAltitude(true);
		setCompass('N');

		if (getMyPlayer().isMoving() || (getMyPlayer().getAnimation() != -1))
			return random(1200, 1500);

		final RSNPC demon = getNearestNPCByID(demonID);
		final RSObject mat1 = getNearestObjectByID(10076);
		final RSObject mat2 = getNearestObjectByID(10077);
		final RSObject mat3 = getNearestObjectByID(10078);
		final RSObject mat4 = getNearestObjectByID(10079);

		if (demon == null)
			return -1;

		myClickContinue();
		wait(random(750, 1000));

		if (RSInterface.getInterface(148).isValid()) {
			switch (getSetting(531)) {
				case 668:
					sign1 = 1;
					sign2 = 2;
					sign3 = 3;
					sign4 = 4;
					break;
				case 675:
					sign1 = 2;
					sign2 = 1;
					sign3 = 3;
					sign4 = 4;
					break;
				case 724:
					sign1 = 1;
					sign2 = 3;
					sign3 = 2;
					sign4 = 4;
					break;
				case 738:
					sign1 = 3;
					sign2 = 1;
					sign3 = 2;
					sign4 = 4;
					break;
				case 787:
					sign1 = 2;
					sign2 = 3;
					sign3 = 1;
					sign4 = 4;
					break;
				case 794:
					sign1 = 3;
					sign2 = 2;
					sign3 = 1;
					sign4 = 4;
					break;
				case 1116:
					sign1 = 1;
					sign2 = 2;
					sign3 = 4;
					sign4 = 3;
					break;
				case 1123:
					sign1 = 2;
					sign2 = 1;
					sign3 = 4;
					sign4 = 3;
					break;
				case 1228:
					sign1 = 1;
					sign2 = 4;
					sign3 = 2;
					sign4 = 3;
					break;
				case 1249:
					sign1 = 4;
					sign2 = 1;
					sign3 = 2;
					sign4 = 3;
					break;
				case 1291:
					sign1 = 2;
					sign2 = 4;
					sign3 = 1;
					sign4 = 3;
					break;
				case 1305:
					sign1 = 4;
					sign2 = 2;
					sign3 = 1;
					sign4 = 3;
					break;
				case 1620:
					sign1 = 1;
					sign2 = 3;
					sign3 = 4;
					sign4 = 2;
					break;
				case 1634:
					sign1 = 3;
					sign2 = 1;
					sign3 = 4;
					sign4 = 2;
					break;
				case 1676:
					sign1 = 1;
					sign2 = 4;
					sign3 = 3;
					sign4 = 2;
					break;
				case 1697:
					sign1 = 4;
					sign2 = 1;
					sign3 = 3;
					sign4 = 2;
					break;
				case 1802:
					sign1 = 3;
					sign2 = 4;
					sign3 = 1;
					sign4 = 2;
					break;
				case 1809:
					sign1 = 4;
					sign2 = 3;
					sign3 = 1;
					sign4 = 2;
					break;
				case 2131:
					sign1 = 2;
					sign2 = 3;
					sign3 = 4;
					sign4 = 1;
					break;
				case 2138:
					sign1 = 3;
					sign2 = 2;
					sign3 = 4;
					sign4 = 1;
					break;
				case 2187:
					sign1 = 2;
					sign2 = 4;
					sign3 = 3;
					sign4 = 1;
					break;
				case 2201:
					sign1 = 4;
					sign2 = 2;
					sign3 = 3;
					sign4 = 1;
					break;
				case 2250:
					sign1 = 3;
					sign2 = 4;
					sign3 = 2;
					sign4 = 1;
					break;
				case 2257:
					sign1 = 4;
					sign2 = 3;
					sign3 = 2;
					sign4 = 1;
					break;
			}
		}

		if (getInterface(148, 1).getText().contains("jumps")) {
			if (sign1 == 1) {
				if (distanceTo(new RSTile(3167, 4820)) < 2) {
					walkTo(randomizeTile(new RSTile(3160, 4820), 0, 0));
					atObject(mat1, "Use");
				} else {
					atObject(mat1, "Use");
				}
				return random(1000, 1500);
			} else if (sign2 == 1) {
				atObject(mat2, "Use");
				return random(1000, 1500);
			} else if (sign3 == 1) {
				atObject(mat3, "Use");
				return random(1000, 1500);
			} else if (sign4 == 1) {
				if (distanceTo(new RSTile(3159, 4820)) < 2) {
					walkTo(randomizeTile(new RSTile(3166, 4820), 0, 0));
					atObject(mat4, "Use");
				} else {
					atObject(mat4, "Use");
				}
				return random(1000, 1500);
			}
		} else if (getInterface(148, 1).getText().contains("push ups")) {
			if (sign1 == 2) {
				if (distanceTo(new RSTile(3167, 4820)) < 2) {
					walkTo(randomizeTile(new RSTile(3160, 4820), 0, 0));
					atObject(mat1, "Use");
				} else {
					atObject(mat1, "Use");
				}
				return random(1000, 1500);
			} else if (sign2 == 2) {
				atObject(mat2, "Use");
				return random(1000, 1500);
			} else if (sign3 == 2) {
				atObject(mat3, "Use");
				return random(1000, 1500);
			} else if (sign4 == 2) {
				if (distanceTo(new RSTile(3159, 4820)) < 2) {
					walkTo(randomizeTile(new RSTile(3166, 4820), 0, 0));
					atObject(mat4, "Use");
				} else {
					atObject(mat4, "Use");
				}
				return random(1000, 1500);
			}
		} else if (getInterface(148, 1).getText().contains("sit ups")) {
			if (sign1 == 3) {
				if (distanceTo(new RSTile(3167, 4820)) < 2) {
					walkTo(randomizeTile(new RSTile(3160, 4820), 0, 0));
					atObject(mat1, "Use");
				} else {
					atObject(mat1, "Use");
				}
				return random(1000, 1500);
			} else if (sign2 == 3) {
				atObject(mat2, "Use");
				return random(1000, 1500);
			} else if (sign3 == 3) {
				atObject(mat3, "Use");
				return random(1000, 1500);
			} else if (sign4 == 3) {
				if (distanceTo(new RSTile(3159, 4820)) < 2) {
					walkTo(randomizeTile(new RSTile(3166, 4820), 0, 0));
					atObject(mat4, "Use");
				} else {
					atObject(mat4, "Use");
				}
				return random(1000, 1500);
			}
		} else if (getInterface(148, 1).getText().contains("jog on")) {
			if (sign1 == 4) {
				if (distanceTo(new RSTile(3167, 4820)) < 2) {
					walkTo(randomizeTile(new RSTile(3160, 4820), 0, 0));
					atObject(mat1, "Use");
				} else {
					atObject(mat1, "Use");
				}
				return random(1000, 1500);
			} else if (sign2 == 4) {
				atObject(mat2, "Use");
				return random(1000, 1500);
			} else if (sign3 == 4) {
				atObject(mat3, "Use");
				return random(1000, 1500);
			} else if (sign4 == 4) {
				if (distanceTo(new RSTile(3159, 4820)) < 2) {
					walkTo(randomizeTile(new RSTile(3166, 4820), 0, 0));
					atObject(mat4, "Use");
				} else {
					atObject(mat4, "Use");
				}
				return random(1000, 1500);
			}
		}

		if (!myClickContinue()) {
			atNPC(demon, "Talk-to");
		}

		return random(1000, 1500);
	}

	public boolean myClickContinue() {
		wait(random(800, 1000));
		return atInterface(243, 7) || atInterface(241, 5) || atInterface(242, 6) || atInterface(244, 8) || atInterface(64, 5);
	}

	public boolean playerInArea(final int maxX, final int maxY, final int minX, final int minY) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if ((x >= minX) && (x <= maxX) && (y >= minY) && (y <= maxY))
			return true;
		return false;
	}
}