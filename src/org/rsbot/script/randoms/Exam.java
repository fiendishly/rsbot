package org.rsbot.script.randoms;

import java.awt.Point;
import java.util.List;

import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.util.ScreenshotUtil;

/*
 * Updated by Iscream Feb 04,10
 * Updated by Iscream Feb 20,10
 * Updated by Iscream Mar 01,10
 */
@ScriptManifest(authors = { "PwnZ", "Megaalgos", "Taha", "Fred", "Poxer", "Iscream" }, name = "Exam", version = 2.1)
public class Exam extends Random {
	public class NextObjectQuestion {
		int One = -1;
		int Two = -1;
		int Three = -1;

		public NextObjectQuestion() {
		}

		public boolean arrayContains(final int[] arr, final int i) {
			boolean returnt = false;
			for (final int num : arr) {
				if (num == i) {
					returnt = true;
				}
			}

			return returnt;
		}

		public boolean clickAnswer() {
			int[] Answers;
			if ((Answers = returnAnswer()) == null)
				return false;

			for (int i = 10; i <= 13; i++) {
				if (arrayContains(Answers, nextObjectInterface.getChild(i)
						.getComponentID()))
					return atInterface(nextObjectInterface.getChild(i));
			}

			return false;
		}

		public boolean getObjects() {
			One = -1;
			Two = -1;
			Three = -1;
			One = nextObjectInterface.getChild(6).getComponentID();
			Two = nextObjectInterface.getChild(7).getComponentID();
			Three = nextObjectInterface.getChild(8).getComponentID();

			return (One != -1) && (Two != -1) && (Three != -1);
		}

		public void guess() {
			final int[] objects = new int[4];
			objects[0] = nextObjectInterface.getChild(10).getComponentID();
			objects[1] = nextObjectInterface.getChild(11).getComponentID();
			objects[2] = nextObjectInterface.getChild(12).getComponentID();
			objects[3] = nextObjectInterface.getChild(13).getComponentID();

			int lowest = 120;
			int click = 10;
			int compare = 0;
			if (compare <= 10) {
				atInterface(nextObjectInterface.getChild(random(10, 13)));
				return;
			}

			for (int i = 0; i < objects.length; i++) {
				if (Math.abs(objects[i] - compare) <= lowest) {
					lowest = Math.abs(objects[i] - compare);
				}
				click = 10 + i;
			}

			atInterface(nextObjectInterface.getChild(click));
		}

		public int[] returnAnswer() {
			final int[] count = new int[items.length];
			int firstcard = 0;
			int secondcard = 0;
			int thirdcard = 0;

			for (int i = 0; i < count.length; i++) {
				count[i] = 0;
			}
			// Will verify that all IDs are IDs which we currently have
			for (int i = 0; i < items.length; i++) {
				for (int j = 0; j < items[i].length; j++) {
					if (items[i][j] == One) {
						firstcard = 1;
					}
					if (items[i][j] == Two) {
						secondcard = 1;
					}
					if (items[i][j] == Three) {
						thirdcard = 1;
					}
				}
			}
			if (firstcard == 0) {
				log.severe("The first object is a new Object");
				log.severe("Please post this screenshot on the forums as well as the missing Object ID below.");
				log.severe("The Missing Object ID is :"
								+ Integer.toString(One));
				log.severe("Your RS name is covered within this picture");
				ScreenshotUtil.takeScreenshot(true);
			}
			if (secondcard == 0) {
				log.severe("The second object is a new Object");
				log.severe("Please post this screenshot on the forums as well as the missing Object ID below.");
				log.severe("The Missing Object ID is :"
								+ Integer.toString(Two));
				log.severe("Your RS name is covered within this picture");
				ScreenshotUtil.takeScreenshot(true);
			}
			if (thirdcard == 0) {
				log.severe("The third object is a new Object");
				log.severe("Please post this screenshot on the forums as well as the missing Object ID below.");
				log.severe("The Missing Object ID is :"
						+ Integer.toString(Three));
				log.severe("Your RS name is covered within this picture");
				ScreenshotUtil.takeScreenshot(true);
			}

			for (int i = 0; i < items.length; i++) {
				for (int j = 0; j < items[i].length; j++) {
					if (items[i][j] == One) {
						count[i]++;
					}
					if (items[i][j] == Two) {
						count[i]++;
					}
					if (items[i][j] == Three) {
						count[i]++;
					}
					if (count[i] >= 2) {
						log.info("Answer Type Found!");
						return items[i];
					}
				}
			}

			return null;
		}
	}

	public class SimilarObjectQuestion {
		String question;
		int[] Answers;

		public SimilarObjectQuestion(final String q, final int[] Answers) {
			question = q.toLowerCase();
			this.Answers = Answers;
		}

		public boolean accept() {
			return atInterface(relatedCardsInterface.getChild(26));
		}

		public boolean activateCondition() {
			if (!relatedCardsInterface.isValid())
				return false;

			if (relatedCardsInterface.getChild(25).getText().toLowerCase()
					.contains(question)) {
				log.info("Question keyword: " + question);
				return true;
			}

			return false;
		}

		public boolean clickObjects() {
			int count = 0;
			for (int i = 42; i <= 56; i++) {
				for (final int answer : Answers) {
					if (relatedCardsInterface.getChild(i).getComponentID() == answer) {
						if (atInterface(relatedCardsInterface.getChild(i))) {
							try {
								wait(random(600, 1000));
							} catch (final Exception ignored) {
							}
						}
						count++;
						if (count >= 3)
							return true;
					}
				}
			}
			log.info("returns false");
			return false;
		}
	}

	public RSInterface nextObjectInterface = getInterface(103);
	public RSInterface relatedCardsInterface = getInterface(559);

	public int[] Ranged = { 11539, 11540, 11541, 11614, 11615, 11633 };

	public int[] Cooking = { 11526, 11529, 11545, 11549, 11550, 11555, 11560,
			11563, 11564, 11607, 11608, 11616, 11620, 11621, 11622, 11623,
			11628,

			11629, 11634, 11639, 11641, 11649, 11624 };

	public int[] Fishing = { 11527, 11574, 11578, 11580, 11599, 11600, 11601,
			11602, 11603,

			11604, 11605, 11606, 11625 };

	public int[] Combat = { 11528, 11531, 11536, 11537, 11579, 11591, 11592,
			11593, 11597, 11627, 11631, 11635, 11636, 11638, 11642, 11648,
			11617 };

	public int[] Farming = { 11530, 11532, 11547, 11548, 11554, 11556, 11571,
			11581, 11586, 11610, 11645 };

	public int[] Magic = { 11533, 11534, 11538, 11562, 11567, 11582 };

	public int[] Firemaking = { 11535, 11551, 11552, 11559, 11646 };

	public int[] Hats = { 11540, 11557, 11558, 11560, 11570, 11619, 11626,
			11630, 11632, 11637, 11654 };
	public int[] Pirate = { 11570, 11626, 11558 };

	public int[] Jewellery = { 11572, 11576, 11652 };

	public int[] Jewellery2 = { 11572, 11576, 11652 };

	public int[] Drinks = { 11542, 11543, 11544, 11644, 11647 };

	public int[] Woodcutting = { 11573, 11595 };

	public int[] Boots = { 11561, 11618, 11650, 11651 };

	public int[] Crafting = { 11546, 11553, 11565, 11566, 11568, 11569, 11572,
			11575, 11576, 11577, 11581, 11583, 11584, 11585, 11643, 11652,
			11653 };

	public int[] Mining = { 11587, 11588, 11594, 11596, 11598, 11609, 11610 };

	public int[] Smithing = { 11611, 11612, 11613 };
	public int[][] items = { Ranged, Cooking, Fishing, Combat, Farming, Magic,
			Firemaking, Hats, Drinks, Woodcutting, Boots, Crafting, Mining,
			Smithing };
	public int Key = 11589;
	public int Book = 11590;
	public int Bones = 11617; // Combat?
	public int Feather = 11624; // Cooking?
	public int Hook = 11626; // Added to hats for pirate stuff.
	public int Cape = 11627; // Added to combat (legends cape)
	// Missing 11640
	public int Talisman = 11643; // Added to crafting

	public int Candle = 11646; // WTF? (Firemaking)

	public int Vial = 11653; // Crafting?

	public SimilarObjectQuestion[] simObjects = {
			new SimilarObjectQuestion("I never leave the house without some sort of jewellery.",Jewellery),
			new SimilarObjectQuestion("There is no better feeling than",Jewellery2),
			new SimilarObjectQuestion("I'm feeling dehydrated", Drinks),
			new SimilarObjectQuestion("All this work is making me thirsty",Drinks),
			new SimilarObjectQuestion("quenched my thirst", Drinks),
			new SimilarObjectQuestion("light my fire", Firemaking),
			new SimilarObjectQuestion("fishy", Fishing),
			new SimilarObjectQuestion("fishing for answers", Fishing),
			new SimilarObjectQuestion("fish out of water", Drinks),
			new SimilarObjectQuestion("strange headgear", Hats),
			new SimilarObjectQuestion("tip my hat", Hats),
			new SimilarObjectQuestion("thinking cap", Hats),
			new SimilarObjectQuestion("wizardry here", Magic),
			new SimilarObjectQuestion("rather mystical", Magic),
			new SimilarObjectQuestion("abracada", Magic),
			new SimilarObjectQuestion("hide one's face", Hats),
			new SimilarObjectQuestion("shall unmask", Hats),
			new SimilarObjectQuestion("hand-to-hand", Combat),
			new SimilarObjectQuestion("melee weapon", Combat),
			new SimilarObjectQuestion("prefers melee", Combat),
			new SimilarObjectQuestion("me hearties", Pirate),
			new SimilarObjectQuestion("puzzle for landlubbers", Pirate),
			new SimilarObjectQuestion("mighty pirate", Pirate),
			new SimilarObjectQuestion("mighty archer", Ranged),
			new SimilarObjectQuestion("as an arrow", Ranged),
			new SimilarObjectQuestion("Ranged attack", Ranged),
			new SimilarObjectQuestion("shiny things", Crafting),
			new SimilarObjectQuestion("igniting", Firemaking),
			new SimilarObjectQuestion("sparks from my synapses.", Firemaking),
			new SimilarObjectQuestion("fire.", Firemaking),
			new SimilarObjectQuestion("disguised", Hats),
			// added diguised Feb 04,2010
			
			
			
			// Default questions just incase the bot gets stuck
			new SimilarObjectQuestion("range", Ranged),
			new SimilarObjectQuestion("arrow", Ranged),
			new SimilarObjectQuestion("drink", Drinks),
			new SimilarObjectQuestion("logs", Firemaking),
			new SimilarObjectQuestion("light", Firemaking),
			new SimilarObjectQuestion("headgear", Hats),
			new SimilarObjectQuestion("hat", Hats),
			new SimilarObjectQuestion("cap", Hats),
			new SimilarObjectQuestion("mine", Mining),
			new SimilarObjectQuestion("mining", Mining),
			new SimilarObjectQuestion("ore", Mining),
			new SimilarObjectQuestion("fish", Fishing),
			new SimilarObjectQuestion("fishing", Fishing),
			new SimilarObjectQuestion("thinking cap", Hats),
			new SimilarObjectQuestion("cooking", Cooking),
			new SimilarObjectQuestion("cook", Cooking),
			new SimilarObjectQuestion("bake", Cooking),
			new SimilarObjectQuestion("farm", Farming),
			new SimilarObjectQuestion("farming", Farming),
			new SimilarObjectQuestion("cast", Magic),
			new SimilarObjectQuestion("magic", Magic),
			new SimilarObjectQuestion("craft", Crafting),
			new SimilarObjectQuestion("boot", Boots),
			new SimilarObjectQuestion("chop", Woodcutting),
			new SimilarObjectQuestion("cut", Woodcutting),
			new SimilarObjectQuestion("tree", Woodcutting),

	};

	public RSObject door = null;

	@Override
	public boolean activateCondition() {
		door = null;
		final RSNPC mordaut = getNearestNPCByName("Mr. Mordaut");
		return mordaut != null;
	}

	/*
	 * Don't use this with any other monster.I edited for this script only cause
	 * Mr. Mordaunt doesn't move
	 */
	@Override
	public boolean clickCharacter(final RSCharacter c, final String action) {
		try {
			Point screenLoc = null;
			screenLoc = c.getScreenLocation();

			if (!c.isValid() || !pointOnScreen(screenLoc)) {
				System.out.println("Not on screen " + action);
				return false;
			}

			moveMouse(screenLoc);

			screenLoc = c.getScreenLocation();

			final List<String> items = getMenuItems();

			if (items.get(0).toLowerCase().contains(action.toLowerCase())) {
				clickMouse(screenLoc, true);
				return true;
			} else {
				clickMouse(screenLoc, false);
				return atMenu(action);
			}

		} catch (final NullPointerException ignored) {
		}
		return true;
	}

	// My clickObject, like clickCharacter, and faster than atObject.
	public boolean clickObject(final RSObject c, final String action) {
		try {
			Point screenLoc = null;
			int X = (int) Calculations.tileToScreen(c.getLocation()).getX();
			int Y = (int) Calculations.tileToScreen(c.getLocation()).getY();

			screenLoc = new Point(X, Y);

			if ((c == null) || !pointOnScreen(screenLoc)) {
				log("Not on screen " + action);
				return false;
			}

			moveMouse(screenLoc);

			X = (int) Calculations.tileToScreen(c.getLocation()).getX();
			Y = (int) Calculations.tileToScreen(c.getLocation()).getY();
			screenLoc = new Point(X, Y);
			if (!getMouseLocation().equals(screenLoc))
				return false;

			final List<String> items = getMenuItems();
			if (items.size() <= 1)
				return false;
			if (items.get(0).toLowerCase().contains(action.toLowerCase())) {
				clickMouse(screenLoc, true);
				return true;
			} else {
				clickMouse(screenLoc, false);
				return atMenu(action);
			}
		} catch (final NullPointerException ignored) {
		}
		return true;
	}

	@Override
	public int loop() {
		final RSNPC mordaut = getNearestNPCByName("Mr. Mordaut");
		if (mordaut == null) {
			return -1;
		}

		if (getMyPlayer().isMoving() || (getMyPlayer().getAnimation() != -1)) {
			return random(800, 1200);
		}
		if (!nextObjectInterface.isValid() && !getMyPlayer().isMoving()
				&& !relatedCardsInterface.isValid() && !canContinue()
				&& mordaut != null && door == null) {
			if (distanceTo(mordaut) > 4) {
				walkTo(mordaut.getLocation());
			}
			if (!tileOnScreen(mordaut.getLocation())) {
				walkTileMM(mordaut.getLocation());
			}
			clickCharacter(mordaut, "Talk-to");
			return (random(1500, 1700));
		}
		if (nextObjectInterface.isValid()) {
			log.info("Question Type: Next Object");
			final NextObjectQuestion noq = new NextObjectQuestion();
			if (noq.getObjects()) {
				if (noq.clickAnswer())
					return random(800, 1200);
				else {
					noq.guess();
					return random(800, 1200);
				}
			} else {
				log.info("Could not find get object. Making educated guess.");
				noq.guess();
				return random(800, 1200);
			}
		}

		if (relatedCardsInterface.isValid()) {
			log.info("Question Type: Similar Objects");
			int z = 0;
			for (final SimilarObjectQuestion obj : simObjects) {
				if (obj.activateCondition()) {
					z = 1;
					if (obj.clickObjects()) {
						obj.accept();
					}
				}
			}
			if (z == 0) {
				log.severe("This is a new question.");
				log.severe("Please post this screenshot on the forums.");
				log.severe("The Missing Question is :");
				log(relatedCardsInterface.getChild(25).getText().toLowerCase());
				log.severe("Your RS name is covered within this picture");
				ScreenshotUtil.takeScreenshot(true);
				z = 1;
			}
			return random(800, 1200);
		}

		if (door != null) {
			if (distanceTo(door) > 3) {
				walkTo(door.getLocation());
				wait(random(1400, 2500));
			}
			if (!tileOnScreen(door.getLocation())) {
				walkTileMM(door.getLocation());
				wait(random(1400, 2500));
			}
			if (door.getID() == 2188) {
				setCompass('w');
			}
			if (door.getID() == 2193) {
				setCompass('e');
			}
			if (door.getID() == 2189) {
				setCompass('w');
			}
			if (door.getID() == 2192) {
				setCompass('n');
			}
			clickObject(door, "Open");
			return random(500, 1000);
		}

		final RSInterfaceChild inter = searchInterfacesText("door");
		if (inter != null) {
			if (inter.getText().toLowerCase().contains("red")) {
				door = getNearestObjectByID(2188);
			}
			if (inter.getText().toLowerCase().contains("green")) {
				door = getNearestObjectByID(2193);
			}
			if (inter.getText().toLowerCase().contains("blue")) {
				door = getNearestObjectByID(2189);
			}
			if (inter.getText().toLowerCase().contains("purple")) {
				door = getNearestObjectByID(2192);
			}
		}

		if (clickContinue()) {
			return random(800, 3500);
		}

		return random(800, 1200);
	}

	public RSInterfaceChild searchInterfacesText(final String string) {
		final RSInterface[] inters = RSInterface.getAllInterfaces();
		for (final RSInterface inter : inters) {
			for (final RSInterfaceChild interfaceChild : inter) {
				if (interfaceChild.getText().toLowerCase().contains(
						string.toLowerCase()))
					return interfaceChild;
			}
		}

		return null;
	}
}