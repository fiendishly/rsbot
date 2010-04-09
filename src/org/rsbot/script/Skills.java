package org.rsbot.script;

/**
 * This class is for all the skill calculations.
 * 
 * Use Skills.getSkillMaxExp() with the stat name to get the index. Then use the
 * index to perform basic operations. <br>
 * <br>
 * private static int WOODCUTTING_SKILL = Skills.getSkillMaxExp("woodcutting"); <br>
 * Then somewhere later in code: <br>
 * skills.getXPToNextLevel(WOODCUTTING_SKILL) <br>
 * <br>
 * Possible skill names: "attack", "defence", "strength", "hitpoints", "range",
 * "prayer", "magic", "cooking", "woodcutting", "fletching", "fishing",
 * "firemaking", "crafting", "smithing", "mining", "herblore", "agility",
 * "thieving", "slayer", "farming", "runecrafting", "hunter", "construction",
 * "summoning", "-unused-"
 * */
public class Skills {
	/**
	 * This is the old stats array. Most skills are the same but some will need
	 * to be added for example construction, hunting and summoning.
	 */
	public static String[] statsArray = { "attack", "defence", "strength", "hitpoints", "range", "prayer", "magic", "cooking", "woodcutting", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblore", "agility", "thieving", "slayer", "farming", "runecrafting", "hunter", "construction", "summoning", "-unused-" };

	/**
	 * The xp you have at each level. From Chemfy/Linux_Communist
	 * */
	private static int[] xpTable = { 0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431 };

	/**
	 * Returns the index of a stat. Case doesn't matter.
	 * */
	public static int getStatIndex(final String statName) {
		for (int i = 0; i < Skills.statsArray.length; i++) {
			if (Skills.statsArray[i].equalsIgnoreCase(statName))
				return i;
		}
		return -1;
	}

	private final Methods methods;

	Skills(final Methods methods) {
		this.methods = methods;
	}

	/**
	 * Gets the current exp for a stat.
	 * 
	 * @return -1 if the skill stat is unavailable
	 */
	public int getCurrentSkillExp(final int index) {
		final int[] skills = methods.getClient().getSkillExperiences();

		if (index > skills.length - 1)
			return -1;

		return methods.getClient().getSkillExperiences()[index];
	}

	/**
	 * Get the current level for a stat.
	 * */
	public int getCurrentSkillLevel(final int index) {
		return methods.getClient().getSkillLevels()[index];
	}

	/**
	 * Returns the level when given the exp.
	 */
	public int getLvlByExp(final int exp) {
		for (int i = Skills.xpTable.length - 1; i > 0; i--) {
			if (exp > Skills.xpTable[i])
				return i;
		}
		return 1;
	}

	/**
	 * Returns the percent of the way to the next level. 0 when you are at 99.
	 * */
	public int getPercentToNextLevel(final int index) {
		final int lvl = getRealSkillLevel(index);
		if (lvl == 99)
			return 0;
		final int xpTotal = Skills.xpTable[lvl + 1] - Skills.xpTable[lvl];
		if (xpTotal == 0)
			return 0;
		final int xpDone = getCurrentSkillExp(index) - Skills.xpTable[lvl];
		return 100 * xpDone / xpTotal;
	}

	/**
	 * Returns the real skill level.
	 */
	public int getRealSkillLevel(final int index) {
		return getLvlByExp(getCurrentSkillExp(index));
	}

	/**
	 * No idea what it does. If you know then please PM one of the authors.
	 * */
	public int getSkillMax(final int index) {
		return methods.getClient().getSkillLevelMaxes()[index];
	}

	/**
	 * No idea what it does. If you know then please PM one of the authors.
	 * */
	public int getSkillMaxExp(final int index) {
		return methods.getClient().getSkillExperiencesMax()[index];
	}

	/**
	 * Returns the amount of XP required to get to the next level. 0 at level
	 * 99.
	 * */
	public int getXPToNextLevel(final int index) {
		final int lvl = getRealSkillLevel(index);
		if (lvl == 99)
			return 0;
		return Skills.xpTable[lvl + 1] - getCurrentSkillExp(index);
	}
}
