package org.rsbot.script;

public interface Constants {

	public static final int CHAT_BUTTON_CENTER_Y = 491;
	public static final int CHAT_BUTTON_CENTER_X = 33;
	public static final int CHAT_BUTTON_DIFF_X = 58;
	public static final int CHAT_BUTTON_MAX_DY = 8;
	public static final int CHAT_BUTTON_MAX_DX = 23;

	public static final int INVENTORY_X = 149;
	public static final int INVENTORY_Y = 0;
	public static final int INVENTORY_COM_X = 763;
	public static final int INVENTORY_COM_Y = 0;

	public static final int INTERFACE_EQUIPMENT = 387;

	public static final int INTERFACE_HP_ORB = 748;
	public static final int INTERFACE_PRAYER_ORB = 749;
	public static final int INTERFACE_RUN_ORB = 750;

	public static final int TAB_ATTACK = 0;
	public static final int TAB_STATS = 1;
	public static final int TAB_QUESTS = 2;
	public static final int TAB_ACHIEVEMENTDIARIES = 3;
	public static final int TAB_INVENTORY = 4;
	public static final int TAB_EQUIPMENT = 5;
	public static final int TAB_PRAYER = 6;
	public static final int TAB_MAGIC = 7;
	public static final int TAB_SUMMONING = 8;
	public static final int TAB_FRIENDS = 9;
	public static final int TAB_IGNORE = 10;
	public static final int TAB_CLAN = 11;
	public static final int TAB_OPTIONS = 12;
	public static final int TAB_CONTROLS = 13;
	public static final int TAB_MUSIC = 14;
	public static final int TAB_NOTES = 15;
	public static final int TAB_LOGOUT = 16;

	public static final int STAT_ATTACK = 0;
	public static final int STAT_DEFENSE = 1;
	public static final int STAT_STRENGTH = 2;
	public static final int STAT_HITPOINTS = 3;
	public static final int STAT_RANGE = 4;
	public static final int STAT_PRAYER = 5;
	public static final int STAT_MAGIC = 6;
	public static final int STAT_COOKING = 7;
	public static final int STAT_WOODCUTTING = 8;
	public static final int STAT_FLETCHING = 9;
	public static final int STAT_FISHING = 10;
	public static final int STAT_FIREMAKING = 11;
	public static final int STAT_CRAFTING = 12;
	public static final int STAT_SMITHING = 13;
	public static final int STAT_MINING = 14;
	public static final int STAT_HERBLORE = 15;
	public static final int STAT_AGILITY = 16;
	public static final int STAT_THIEVING = 17;
	public static final int STAT_SLAYER = 18;
	public static final int STAT_FARMING = 19;
	public static final int STAT_RUNECRAFTING = 20;
	public static final int STAT_HUNTER = 21;
	public static final int STAT_CONSTRUCTION = 22;
	public static final int STAT_SUMMONING = 23;

	public static final int INTERFACE_TAB_CLAN = 589;
	public static final int INTERFACE_TAB_IGNORE = 551;
	public static final int INTERFACE_TAB_FRIENDS = 550;
	public static final int INTERFACE_TAB_MUSIC = 187;
	public static final int INTERFACE_TAB_OPTIONS = 261;
	public static final int INTERFACE_TAB_EMOTES = 464;
	public static final int INTERFACE_TAB_MAGIC = 192;
	public static final int INTERFACE_TAB_QUESTS = 274;
	public static final int INTERFACE_TAB_STATS = 320;
	public static final int INTERFACE_TAB_COMBAT = 92;
	public static final int INTERFACE_TAB_EQUIPMENT = 387;
	public static final int INTERFACE_TAB_PRAYER = 271;
	public static final int INTERFACE_TAB_LOGOUT = 182;

	public static final String[] TAB_NAMES = new String[] { "Combat Styles",
			"Stats", "Quest List", "Achievement Diaries", "Inventory",
			"Worn Equipment", "Prayer List", "Magic Spellbook", "Objectives",
			"Friends List", "Ignore List", "Clan Chat", "Options", "Emotes",
			"Music Player", "Notes", "Log Out" };

	public static final int INTERFACE_CHAT_BOX = 137;
	public static final int INTERFACE_TRADE = 279;
	public static final int INTERFACE_GAME_SCREEN = 548;
	public static final int INTERFACE_LEVELUP = 740;

	public static final int INTERFACE_BANK = 762;
	public static final int INTERFACE_BANK_BUTTON_CLOSE = 42;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_BEAST_INVENTORY = 36;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_CARRIED_ITEMS = 32;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_WORN_ITEMS = 34;
	public static final int INTERFACE_BANK_BUTTON_HELP = 43;
	public static final int INTERFACE_BANK_BUTTON_INSERT = 15;
	public static final int INTERFACE_BANK_BUTTON_ITEM = 19;
	public static final int INTERFACE_BANK_BUTTON_NOTE = 19;
	public static final int INTERFACE_BANK_BUTTON_SEARCH = 17;
	public static final int INTERFACE_BANK_BUTTON_SWAP = 15;
	public static final int INTERFACE_BANK_INVENTORY = 92;
	public static final int INTERFACE_BANK_ITEM_FREE_COUNT = 23;
	public static final int INTERFACE_BANK_ITEM_FREE_MAX = 24;
	public static final int INTERFACE_BANK_ITEM_MEMBERS_COUNT = 25;
	public static final int INTERFACE_BANK_ITEM_MEMBERS_MAX = 26;
	public static final int INTERFACE_BANK_SCROLLBAR = 114;
	public static final int INTERFACE_BANK_SEARCH = 17;

	public static final int INTERFACE_INVENTORY = 149;

	int[] INTERFACE_BANK_TAB = { 61, 59, 57, 55, 53, 51, 49, 47, 45 };
	int[] INTERFACE_BANK_TAB_FIRST_ITEM = { 78, 79, 80, 81, 82, 83, 84, 85, 86 };

	public static final int INTERFACE_DEPOSITBOX = 11;
	public static final int INTERFACE_DEPOSITBOX_INTERFACE = 61;
	public static final int INTERFACE_DEPOSITBOX_BUTTON_CLOSE = 62;

	public static final int INTERFACE_SMELTINGSCREEN = 311;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_RUNE = 12;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_ADAMANT = 11;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_MITHRIL = 10;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_GOLD = 9;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_STEEL = 8;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_SILVER = 7;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_IRON = 6;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_BLURITE = 5;
	public static final int INTERFACE_SMELTINGSCREEN_BUTTON_BRONZE = 4;

	public static final int INTERFACE_STORE = 620;
	public static final int INTERFACE_STORE_BUTTON_CLOSE = 7;
	public static final int INTERFACE_STORE_BUTTON_PLAYERSTORE = 17;
	public static final int INTERFACE_STORE_BUTTON_MAINSTORE = 20;

	public static final int WELCOME_SCREEN_ID = 378;
	public static final int WELCOME_SCREEN_BUTTON_PLAY = 18;

	public static final int[] INTERFACE_OPTIONS = new int[] { 230, 228 };
	public static final int[] INTERFACE_TALKS = new int[] { 211, 241, 251, 101,
			242, 102, 161, 249, 243, 64, 65, 244, 255, 249, 230, 372, 421 };

	public static final int SETTING_TOGGLE_ACCEPT_AID = 427;
	public static final int SETTING_TOGGLE_RUN = 173;
	public static final int SETTING_MOUSE_BUTTONS = 170;
	public static final int SETTING_CHAT_EFFECTS = 171;
	public static final int SETTING_SPLIT_PRIVATE_CHAT = 287;
	public static final int SETTING_ADJUST_SCREEN_BRIGHTNESS = 166;
	public static final int SETTING_ADJUST_MUSIC_VOLUME = 168;
	public static final int SETTING_ADJUST_SOUND_EFFECT_VOLUME = 169;
	public static final int SETTING_ADJUST_AREA_SOUND_EFFECT_VOLUME = 872;
	public static final int SETTING_COMBAT_STYLE = 43;
	public static final int SETTING_AUTO_RETALIATE = 172;
	public static final int SETTING_SWAP_QUEST_DIARY = 1002;
	public static final int SETTING_PRAYER_THICK_SKIN = 83;
	public static final int SETTING_TOGGLE_LOOP_MUSIC = 19;
	public static final int SETTING_BANK_TOGGLE_REARRANGE_MODE = 304;
	public static final int SETTING_BANK_TOGGLE_WITHDRAW_MODE = 115;
	public static final int SETTING_TYPE_SHOP = 118;
	public static final int SETTING_SPECIAL_ATTACK_ENABLED = 301;

	public static final int ITEM_BRONZE_PICKAXE = 1265;
	public static final int ITEM_IRON_PICKAXE = 1267;
	public static final int ITEM_STEEL_PICKAXE = 1269;
	public static final int ITEM_MITHRIL_PICKAXE = 1273;
	public static final int ITEM_ADAMANT_PICKAXE = 1271;
	public static final int ITEM_RUNE_PICKAXE = 1275;
	public static final int ITEM_BRONZE_AXE = 1351;
	public static final int ITEM_TINDERBOX = 590;
	public static final int ITEM_ASHES = 592;
	public static final int ITEM_SMALL_FISHING_NET = 303;
	public static final int ITEM_RAW_SHRIMPS = 2514;
	public static final int ITEM_BURNT_SHRIMPS = 7954;
	public static final int ITEM_SHRIMPS = 315;
	public static final int ITEM_POT_OF_FLOUR = 2516;

	public static final int SPELL_HOME_TELEPORT = 24;
	public static final int SPELL_WIND_STRIKE = 25;
	public static final int SPELL_CONFUSE = 26;
	public static final int SPELL_ENCHANT_CROSSBOW_BOLT = 27;
	public static final int SPELL_WATER_STRIKE = 28;
	public static final int SPELL_LVL1_ENCHANT = 29;
	public static final int SPELL_EARTH_STRIKE = 30;
	public static final int SPELL_WEAKEN = 31;
	public static final int SPELL_FIRE_STRIKE = 32;
	public static final int SPELL_BONES_TO_BANANAS = 33;
	public static final int SPELL_WIND_BOLT = 34;
	public static final int SPELL_CURSE = 35;
	public static final int SPELL_BIND = 36;
	public static final int SPELL_MOBILISING_ARMIES_TELEPORT = 37;
	public static final int SPELL_LOW_LEVEL_ALCHEMY = 38;
	public static final int SPELL_WATER_BOLT = 39;
	public static final int SPELL_VARROCK_TELEPORT = 40;
	public static final int SPELL_LVL2_ENCHANT = 41;
	public static final int SPELL_EARTH_BOLT = 42;
	public static final int SPELL_LUMBRIDGE_TELEPORT = 43;
	public static final int SPELL_TELEKINETIC_GRAB = 44;
	public static final int SPELL_FIRE_BOLT = 45;
	public static final int SPELL_FALADOR_TELEPORT = 46;
	public static final int SPELL_CRUMBLE_UNDEAD = 47;
	public static final int SPELL_TELEPORT_TO_HOUSE = 48;
	public static final int SPELL_WIND_BLAST = 49;
	public static final int SPELL_SUPERHEAT_ITEM = 50;
	public static final int SPELL_CAMELOT_TELEPORT = 51;
	public static final int SPELL_WATER_BLAST = 52;
	public static final int SPELL_LVL3_ENCHANT = 53;
	public static final int SPELL_IBAN_BLAST = 54;
	public static final int SPELL_SNARE = 55;
	public static final int SPELL_MAGIC_DART = 56;
	public static final int SPELL_ARDOUGNE_TELEPORT = 57;
	public static final int SPELL_EARTH_BLAST = 58;
	public static final int SPELL_HIGH_LEVEL_ALCHEMY = 59;
	public static final int SPELL_CHARGE_WATER_ORB = 60;
	public static final int SPELL_LVL4_ENCHANT = 61;
	public static final int SPELL_WATCHTOWER_TELEPORT = 62;
	public static final int SPELL_FIRE_BLAST = 63;
	public static final int SPELL_CHARGE_EARTH_ORB = 64;
	public static final int SPELL_BONES_TO_PEACHES = 65;
	public static final int SPELL_SARADOMIN_STRIKE = 66;
	public static final int SPELL_CLAWS_OF_GUTHIX = 67;
	public static final int SPELL_FLAMES_OF_ZAMORAK = 68;
	public static final int SPELL_TROLLHEIM_TELEPORT = 69;
	public static final int SPELL_WIND_WAVE = 70;
	public static final int SPELL_CHARGE_FIRE_ORB = 71;
	public static final int SPELL_TELEPORT_TO_APE_ATOL = 72;
	public static final int SPELL_WATER_WAVE = 73;
	public static final int SPELL_CHARGE_AIR_ORB = 74;
	public static final int SPELL_VULNERABILITY = 75;
	public static final int SPELL_LVL5_ENCHANT = 76;
	public static final int SPELL_EARTH_WAVE = 77;
	public static final int SPELL_ENFEEBLE = 78;
	public static final int SPELL_TELEOTHER_LUMBRIDGE = 79;
	public static final int SPELL_FIRE_WAVE = 80;
	public static final int SPELL_ENTANGLE = 81;
	public static final int SPELL_STUN = 82;
	public static final int SPELL_CHARGE = 83;
	public static final int SPELL_TELEOTHER_FALADOR = 84;
	public static final int SPELL_TELEPORT_BLOCK = 85;
	public static final int SPELL_LVL6_ENCHANT = 86;
	public static final int SPELL_TELEOTHER_CAMELOT = 87;

}
