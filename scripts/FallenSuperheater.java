import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = { "Fallen" }, category = "Magic", name = "Fallen's Superheater", version = 4.0, description = "<html><head>"
		+ "</head><body style='font-family: Eurostile; margin: 8px;'>"
		+ "<center><img src=\"http://img245.imageshack.us/img245/4683/scriptlogo.png\" /></center>"
		+ "<br><strong>Which bars would you like to make? </strong> "
		+ "<br> "
		+ "<select name=\'Bars to make\'><option selected>Bronze<option>Iron<option>Steel<option>Silver<option>Gold<option>Mithril<option>Adamantite<option>Runite</select>"
		+ "<br> "
		+ "<br> "
		+ "<strong>Log out after: <input type=\"text\" name=\"CastsUntilLogout\" value=\"0\"> casts.</strong>"
		+ "<br> (0 = Until runs out of ores/runes.) "
		+ "<br> "
		+ "<br><strong><select name=\'Antiban\'><option>100<option>90<option>80<option>70<option>60<option selected>50<option>40<option>30<option>20<option>10<option>0</select>% Anti-Ban's activity rate.</strong>"
		+ "<br><strong><select name=\'Mouse\'><option>100<option>90<option>80<option>70<option>60<option selected>50<option>40<option>30<option>20<option>10</select>% Mousespeed.</strong>"
		+ "<br> "
		+ "<br><input type=\"checkbox\" name=\"profit\" value=\"true\" CHECKED> Calculate profits and losses? "
		+ "<br><input type=\"checkbox\" name=\"screen\" value=\"true\"> Take a screenshot when the script stops?"
		+ "<br> "
		+ "<br><strong>Version 4.0</strong></b>"
		+ "<br>This script uses the spell 'Superheat', to turn ores into bars."
		+ "<br><strong>Instructions: </strong></b>"
		+ "Start the script next to a bank, have required runes in your inventory (natures). Scroll your bank so that the ores can be seen."
		+ "</body></html>")
public class FallenSuperheater extends Script implements PaintListener,
		ServerMessageListener {

	private enum State {
		OPENBANK, BANK, SUPERHEAT
	}

	// Mostly paint int's
	public long logOutTime;
	public int status;
	public long startTime = System.currentTimeMillis();
	private long scriptStartTime = 0;
	private long runTime = 0;
	private long seconds = 0;
	private long minutes = 0;
	private long hours = 0;
	public int BarCounter = 0;
	private int startXPM;
	private int startXPS;
	public int startLevelM;
	public int levelsGainedM;
	public int startLevelS;
	public int levelsGainedS;
	public int currentLevelM;
	public int currentLevelS;
	public Image BKG;
	public Image Timer;
	public int toNextLvlS;
	public int nextLvlS;
	public int XPToLevelS;
	public int toNextLvlM;
	public int nextLvlM;
	public int XPToLevelM;

	public int BarEXP = 0;

	public String BarType;
	public int AmountOfCasts;
	public String ScreenShot;
	public String Calculate;
	public String Status;

	// Some variables & anti-ban
	boolean start = true;
	boolean checkObj = false;
	boolean checkXP = false;
	boolean doHover = false;
	public int Percentage = 100;
	public int Mouse1 = 50;
	public int Mouse2 = 8;

	boolean bankTwice = false;
	public int OpenedBank = 0;
	public boolean Continue = false;
	public boolean BankTime = false;
	public boolean DirectHeat = false;
	public boolean TabIsOpen = false;
	public boolean TakeAShot = false;
	public boolean DoTheMath = false;
	public int Option = 2;

	public int barType = 0;
	public int BarID = 0;
	public int Ore1 = 0;
	public int Ore2 = 0;
	public int Ore1WD = 0;
	public int Ore2WD = 0;
	public int Ore2PerSpell = 0;
	public int Ore1InvAm = 0;
	public int Ore2InvAm = 0;

	// Ore ID's
	public int copperOre = 436;
	public int tinOre = 438;
	public int ironOre = 440;
	public int silverOre = 442;
	public int goldOre = 444;
	public int coalOre = 453;
	public int mithrilOre = 447;
	public int adamantOre = 449;
	public int runeOre = 451;
	// Bar ID's
	public int bronzeBar = 2349;
	public int ironBar = 2351;
	public int steelBar = 2353;
	public int silverBar = 2355;
	public int goldBar = 2357;
	public int mithrilBar = 2359;
	public int adamantBar = 2361;
	public int runeBar = 2363;

	public int nature = 561;

	// Item prices from G.E
	public int BarPrice = 0;
	public int Ore1Price = 0;
	public int Ore2Price = 0;
	public int naturePrice = 0;

	@Override
	protected int getMouseSpeed() {
		return Mouse2;
	}

	/*-------------------------------------------------------------------
	 * ------------------   P   A   I   N   T   -------------------------
	 ------------------------------------------------------------------*/
	public void onRepaint(Graphics g) {
		final Mouse m = Bot.getClient().getMouse();
		final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
				.getClient().getMouse().y);
		final Rectangle toggleRectangle = new Rectangle(367, 90, 80, 25);
		if (toggleRectangle.contains(mouse) && (m.pressed)) {
			if (Option == 0) {
				Option = 1;
			} else if (Option == 1) {
				Option = 2;
			} else if (Option == 2) {
				Option = 0;
			}
		}
		runTime = System.currentTimeMillis() - scriptStartTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}
		if (startXPM == 0) {
			startXPM = skills.getCurrentSkillExp(STAT_MAGIC);
		}
		if (startXPS == 0) {
			startXPS = skills.getCurrentSkillExp(STAT_SMITHING);
		}
		if (startLevelM == 0) {
			startLevelM = skills.getCurrentSkillLevel(STAT_MAGIC);
		}
		if (startLevelS == 0) {
			startLevelS = skills.getCurrentSkillLevel(STAT_SMITHING);
		}
		int XPGainedSmithing = 0;
		int XPGainedMagic = 0;
		XPGainedMagic = (skills.getCurrentSkillExp(STAT_MAGIC) - startXPM);
		final int XPHRM = (int) ((XPGainedMagic) * 3600000D / (System
				.currentTimeMillis() - startTime));
		XPGainedSmithing = (skills.getCurrentSkillExp(STAT_SMITHING) - startXPS);
		final int XPHRS = (int) ((XPGainedSmithing) * 3600000D / (System
				.currentTimeMillis() - startTime));
		final int profit = (int) ((BarPrice) - (Ore1Price) - (Ore2Price) - (naturePrice));
		levelsGainedM = skills.getCurrentSkillLevel(STAT_MAGIC) - startLevelM;
		levelsGainedS = skills.getCurrentSkillLevel(STAT_SMITHING)
				- startLevelS;
		currentLevelM = skills.getCurrentSkillLevel(STAT_MAGIC);
		currentLevelS = skills.getCurrentSkillLevel(STAT_SMITHING);
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		// TITLE
		// Time
		g.drawImage(Timer, 303, 8, null);
		// ----------------------------------------------------------------------------
		if (Option == 1) {
			g.setColor(new Color(0, 0, 0, 130));
			g.fillRect(35, 253, 475, 77);
			// HOUR RATES
			g.setColor(Color.WHITE);
			g.setFont(new Font("Verdana", 0, 11));
			g.setColor(Color.CYAN);
			g.drawString("Magic EXP/H: " + XPHRM, 50, 270);
			g.setColor(Color.WHITE);
			g.drawString("Smith EXP/H: " + XPHRS, 218, 270);
			g.setColor(Color.YELLOW);
			if (DoTheMath == true) {
				g.drawString("Profit/H: " + XPHRM / 53 * +profit, 387, 270);
			} else {
				g.setFont(new Font("Verdana", 0, 9));
				g.drawString("" + BarType + " bars/Hour: " + XPHRM / 53, 387,
						270);
			}
		}
		if (Option == 2) {
			g.drawImage(BKG, 9, 185, null);
			// HOUR RATES
			g.setColor(Color.WHITE);
			g.setFont(new Font("Verdana", 0, 11));
			g.setColor(Color.CYAN);
			g.drawString("Magic EXP/H: " + XPHRM, 50, 214);
			g.setColor(Color.WHITE);
			g.drawString("Smith EXP/H: " + XPHRS, 218, 214);
			g.setColor(Color.YELLOW);
			if (DoTheMath == true) {
				g.drawString("Profit/H: " + XPHRM / 53 * +profit, 387, 214);
			} else {
				g.setFont(new Font("Verdana", 0, 9));
				g.drawString("" + BarType + " bars/Hour: " + XPHRM / 53, 387,
						214);
			}
			// Magic
			g.setFont(new Font("Verdana", 0, 10));
			g.setColor(Color.CYAN);
			g.drawString("Magic level: " + currentLevelM + " (" + levelsGainedM
					+ ")", 20, 256);
			g.drawString("Magic EXP gained: " + (BarCounter * 53), 20, 271);
			// Smithing
			g.setColor(Color.WHITE);
			g.drawString("Smithing level: " + currentLevelS + " ("
					+ levelsGainedS + ")", 193, 256);
			g
					.drawString("Smith EXP gained: " + (BarCounter * BarEXP),
							193, 271);
			// Bars & Profit
			if (DoTheMath == true) {
				g.setColor(Color.YELLOW);
				g.drawString("" + BarType + " bars/Hour: " + XPHRM / 53, 360,
						241);
				g.drawString("Bars made: " + BarCounter + " " + BarType, 360,
						256);
				g.drawString("Total profit: " + BarCounter * +profit, 360, 271);
			} else {
				g.setColor(Color.YELLOW);
				g.drawString("Bars made: " + BarCounter + " " + BarType, 360,
						271);
			}
		}
		// VERSION & AMOUNT OF CASTS
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", 0, 11));
		g.drawString("Version 4.0", 440, 39);
		if (AmountOfCasts != 0) {
			g.setFont(new Font("Verdana", 0, 14));
			g.setColor(Color.WHITE);
			g.drawString(
					"Time: " + hours + ":" + minutes + ":" + seconds + ".",
					355, 74);
			g.setFont(new Font("Verdana", 0, 9));
			g.setColor(Color.RED);
			g.drawString("Casts left: " + ((AmountOfCasts) - (BarCounter)),
					365, 57);
		} else {
			g.setFont(new Font("Verdana", 0, 14));
			g.setColor(Color.WHITE);
			g.drawString(
					"Time: " + hours + ":" + minutes + ":" + seconds + ".",
					355, 65);
		}
		// Anti-Ban activities
		if (Status != null) {
			g.setColor(Color.GREEN);
			g.setFont(new Font("Verdana", 0, 12));
			g.drawString("" + Status, 20, 237);
		}
		// PROGGY BARS
		toNextLvlS = skills.getPercentToNextLevel(STAT_SMITHING);
		XPToLevelS = skills.getXPToNextLevel(STAT_SMITHING);
		nextLvlS = skills.getCurrentSkillLevel(STAT_SMITHING) + 1;
		toNextLvlM = skills.getPercentToNextLevel(STAT_MAGIC);
		XPToLevelM = skills.getXPToNextLevel(STAT_MAGIC);
		nextLvlM = skills.getCurrentSkillLevel(STAT_MAGIC) + 1;
		// Mage bar
		g.setFont(new Font("Verdana", 0, 9));
		g.setColor(new Color(0, 0, 0));
		g.fillRect(49, 287, 443, 16);
		g.drawRect(48, 286, 444, 17);
		g.setColor(new Color(51, 51, 51));
		g.fillRect(49, 287, 443, 8);
		// g.setColor(new Color(0, 0, 255, 130));
		g.setColor(new Color(90, 90, 255, 100));
		g.fillRect(51, 289, (int) (toNextLvlM * 439 / 100.0), 12);
		g.setColor(Color.WHITE);
		g.drawString("" + toNextLvlM + "% to " + nextLvlM + " Magic", 56, 298);
		g.drawString(" - " + XPToLevelM + " XP", 165, 298);
		// Smith bar
		g.setColor(new Color(0, 0, 0));
		g.fillRect(49, 309, 443, 16);
		g.drawRect(48, 308, 444, 17);
		g.setColor(new Color(51, 51, 51));
		g.fillRect(49, 309, 443, 8);
		g.setColor(new Color(250, 150, 70, 100));
		g.fillRect(51, 311, (int) (toNextLvlS * 439 / 100.0), 12);
		g.setColor(Color.WHITE);
		g.drawString("" + toNextLvlS + "% to " + nextLvlS + " Smithing", 56,
				320);
		g.drawString(" - " + XPToLevelS + " XP", 165, 320);
	}

	/*------------------------------------------------------------
	 * ------------------  O N   S T A R T  ----------------------
	 -----------------------------------------------------------*/
	@Override
	public boolean onStart(final Map<String, String> args) {
		try {
			BKG = ImageIO.read(new URL(
					"http://img41.imageshack.us/img41/3554/newpaintbkg2.png"));
			// Timer = ImageIO.read(new
			// URL("http://img535.imageshack.us/img535/9061/originaltimer.png"));
			Timer = ImageIO
					.read(new URL(
							"http://img135.imageshack.us/img135/9061/originaltimer.png"));
		} catch (final java.io.IOException e) {
			e.printStackTrace();
		}
		// Options (ARGS)
		ScreenShot = args.get("screen");
		Calculate = args.get("profit");
		if (args.get("Bars to make").equals("Bronze")) {
			barType = 1;
			BarID = bronzeBar;
			Ore1 = copperOre;
			Ore2 = tinOre;
			Ore1WD = 13;
			Ore2WD = 13;
			bankTwice = true;
			BarType = "Bronze";
			BarEXP = (int) 6.25;
			Ore2PerSpell = 1;
			Ore1InvAm = 13;
			Ore2InvAm = 13;
		}
		if (args.get("Bars to make").equals("Iron")) {
			barType = 2;
			BarID = ironBar;
			Ore1 = ironOre;
			// Ore2 = ironOre;
			Ore1WD = 0;
			// Ore2WD = 0;
			bankTwice = false;
			BarType = "Iron";
			BarEXP = (int) 12.5;
			Ore2PerSpell = 0;
			Ore1InvAm = 27;
			Ore2InvAm = 0;
		}
		if (args.get("Bars to make").equals("Steel")) {
			barType = 3;
			BarID = steelBar;
			Ore1 = ironOre;
			Ore2 = coalOre;
			Ore1WD = 9;
			Ore2WD = 0;
			bankTwice = true;
			BarType = "Steel";
			BarEXP = (int) 17.5;
			Ore2PerSpell = 2;
			Ore1InvAm = 9;
			Ore2InvAm = 18;
		}
		if (args.get("Bars to make").equals("Silver")) {
			barType = 4;
			BarID = silverBar;
			Ore1 = silverOre;
			// Ore2 = silverOre;
			Ore1WD = 0;
			// Ore2WD = 0;
			bankTwice = false;
			BarType = "Silver";
			BarEXP = (int) 13.7;
			Ore2PerSpell = 0;
			Ore1InvAm = 27;
			Ore2InvAm = 0;
		}
		if (args.get("Bars to make").equals("Gold")) {
			barType = 5;
			BarID = goldBar;
			Ore1 = goldOre;
			// Ore2 = goldOre;
			Ore1WD = 0;
			// Ore2WD = 0;
			bankTwice = false;
			BarType = "Gold";
			BarEXP = (int) 22.5;
			Ore2PerSpell = 0;
			Ore1InvAm = 27;
			Ore2InvAm = 0;
		}
		if (args.get("Bars to make").equals("Mithril")) {
			barType = 6;
			BarID = mithrilBar;
			Ore1 = mithrilOre;
			Ore2 = coalOre;
			Ore1WD = 5;
			Ore2WD = 0;
			bankTwice = true;
			BarType = "Mithril";
			BarEXP = (int) 30;
			Ore2PerSpell = 4;
			Ore1InvAm = 5;
			Ore2InvAm = 22;
		}
		if (args.get("Bars to make").equals("Adamantite")) {
			barType = 7;
			BarID = adamantBar;
			Ore1 = adamantOre;
			Ore2 = coalOre;
			Ore1WD = 3;
			Ore2WD = 0;
			bankTwice = true;
			BarType = "Adamant";
			BarEXP = (int) 37.5;
			Ore2PerSpell = 6;
			Ore1InvAm = 3;
			Ore2InvAm = 24;
		}
		if (args.get("Bars to make").equals("Runite")) {
			barType = 8;
			BarID = runeBar;
			Ore1 = runeOre;
			Ore2 = coalOre;
			Ore1WD = 3;
			Ore2WD = 0;
			bankTwice = true;
			BarType = "Runite";
			BarEXP = (int) 50;
			Ore2PerSpell = 8;
			Ore1InvAm = 3;
			Ore2InvAm = 24;
		}
		if (ScreenShot != null) {
			TakeAShot = true;
		}
		if (Calculate != null) {
			DoTheMath = true;
		}
		AmountOfCasts = Integer.parseInt(args.get("CastsUntilLogout"));
		Percentage = Integer.parseInt(args.get("Antiban"));
		Mouse1 = Integer.parseInt(args.get("Mouse"));
		// MOUSESPEED
		if (Mouse1 == 100) {
			Mouse2 = 3;
		} else if (Mouse1 == 90) {
			Mouse2 = 4;
		} else if (Mouse1 == 80) {
			Mouse2 = 5;
		} else if (Mouse1 == 70) {
			Mouse2 = 6;
		} else if (Mouse1 == 60) {
			Mouse2 = 7;
		} else if (Mouse1 == 50) {
			Mouse2 = 8;
		} else if (Mouse1 == 40) {
			Mouse2 = 9;
		} else if (Mouse1 == 30) {
			Mouse2 = 10;
		} else if (Mouse1 == 20) {
			Mouse2 = 11;
		} else if (Mouse1 == 10) {
			Mouse2 = 12;
		}
		if (DoTheMath == true) {
			log("Obtaining G.E prices...");
			final GEItemInfo BarGE = grandExchange.loadItemInfo(BarID);
			BarPrice = BarGE.getMarketPrice();
			final GEItemInfo Ore1GE = grandExchange.loadItemInfo(Ore1);
			Ore1Price = Ore1GE.getMarketPrice();
			final GEItemInfo Ore2GE = grandExchange.loadItemInfo(Ore2);
			Ore2Price = Ore2GE.getMarketPrice() * Ore2PerSpell;
			final GEItemInfo natureGE = grandExchange.loadItemInfo(nature);
			naturePrice = natureGE.getMarketPrice();
			log("... prices retrieved!" + "  ||  Profit/Bar: " + (BarPrice)
					+ " - " + (Ore1Price) + " - " + (Ore2Price) + " - "
					+ (naturePrice) + " = "
					+ (BarPrice - Ore1Price - Ore2Price - naturePrice));
		}
		log("Magic XP/Cast: 53  ||  Smithing XP/" + BarType + " bar: " + BarEXP);
		getMouseSpeed();
		log(" ~ Anti-ban synchronized to " + Percentage
				+ "% efficiency! || Mousespeed set to " + Mouse1 + "%! ~");
		if (TakeAShot == true) {
			log("Will take a screenshot when finished, Fallen's Superheater is now running!");
		} else {
			log("Fallen's Superheater is now running!");
		}
		if (AmountOfCasts != 0) {
			log("Logging out after " + AmountOfCasts + " casts!");
		}
		log("------------------------------------------------");
		return true;
	}

	// ************************************************************************************************************
	private State getState() {
		if (DirectHeat == true || Continue == true) {
			return State.SUPERHEAT;
		} else if (DirectHeat == false && Continue == false) {
			final int ore1Count = getInventoryCount(Ore1);
			final int ore2Count = getInventoryCount(Ore2);
			if (ore1Count < 1 || ore2Count < Ore2PerSpell
					|| ore1Count > Ore1InvAm || ore2Count > Ore2InvAm) {
				if (bank.isOpen()) {
					return State.BANK;
				} else {
					return State.OPENBANK;
				}
			} else {
				if (!bank.isOpen()) {
					return State.SUPERHEAT;
				} else {
					return State.BANK;
				}
			}
		} else {
			return State.SUPERHEAT;
		}
	}

	// ************************************************************************************************************
	@Override
	public int loop() {
		final State state = getState(); // Gets the state
		switch (state) { // Switches between these states based on getState
		case SUPERHEAT:
			DirectHeat = false;
			TabIsOpen = false;
			if (getCurrentTab() != TAB_MAGIC) {
				openTab(TAB_MAGIC);
			}
			if (!bank.isOpen()) {
				atInterface(INTERFACE_TAB_MAGIC, Constants.SPELL_SUPERHEAT_ITEM);
				waitForTab(TAB_INVENTORY, 2000);
			} else {
				break;
			}
			if (!bank.isOpen() && getCurrentTab() == TAB_INVENTORY) {
				final int ore1Count = getInventoryCount(Ore1);
				final int ore2Count = getInventoryCount(Ore2);
				if ((ore1Count > 1 && bankTwice == false)
						|| (bankTwice == true && ore1Count > 1 && ore2Count > Ore2PerSpell)) {
					Continue = true;
					BankTime = false;
				} else if (ore1Count == 1 || ore1Count == 0) {
					Continue = false;
					BankTime = true;
				}
				if (atLastInventoryItem(Ore1, "Cast")) {
					AntiBan();
					waitForMageTabWO(2500);
					BarCounter++;
					if ((AmountOfCasts) != 0) {
						if (BarCounter >= AmountOfCasts) {
							Quit();
							log("Goal achieved!");
						}
					}
				} else {
					atLastInventoryItem(Ore1, "Cast");
					AntiBan();
					waitForMageTabWO(2500);
					BarCounter++;
					if ((AmountOfCasts) != 0) {
						if (BarCounter >= AmountOfCasts) {
							Quit();
							log("Goal achieved!");
						}
					}
				}
			}
			if (BankTime == true && Continue == false) {
				bank.open();
				moveMouseRandomly(50);
				waitForIface(getInterface(Constants.INTERFACE_BANK), 2000);
				wait(random(100, 200));
				BankTime = false;
				TabIsOpen = true;
			}
			break;
		case OPENBANK:
			if (OpenedBank < 2) {
				bank.open();
				moveMouseRandomly(50);
				waitForIface(getInterface(Constants.INTERFACE_BANK), 2000);
				wait(random(100, 200));
				OpenedBank++;
			} else {
				openTab(random(0, 17));
				wait(random(300, 600));
				openTab(TAB_INVENTORY);
				OpenedBank = 0;
			}
			break;
		case BANK:
			if (!bank.isOpen()) {
				break;
			}
			OpenedBank = 0;
			final int ore1Count2 = getInventoryCount(Ore1);
			final int ore2Count2 = getInventoryCount(Ore2);
			bank.depositAllExcept(nature, Ore1, Ore2);
			if (bankTwice == true) {
				if (ore1Count2 == 0) {
					CheckOres1();
					withdraw(Ore1, Ore1WD);
					waitForWithdrawnItem(Ore1, 1500);
					break;
				} else if (ore1Count2 > 0 && ore1Count2 < Ore1WD) {
					if (ore2Count2 <= Ore2InvAm) {
						bank.depositAllExcept(nature, Ore2);
					} else if (ore2Count2 > Ore2InvAm) {
						bank.depositAllExcept(nature);
					}
					CheckOres1();
					withdraw(Ore1, Ore1WD);
					waitForWithdrawnItem(Ore1, 1500);
					break;
				} else if (ore1Count2 > Ore1InvAm) {
					bank.depositAllExcept(nature, Ore2);
					CheckOres1();
					withdraw(Ore1, Ore1WD);
					waitForWithdrawnItem(Ore1, 1500);
					break;
				} else if (ore1Count2 == Ore1WD && ore2Count2 != Ore2InvAm) {
					if (ore2Count2 == 0) {
						CheckOres2();
						withdraw(Ore2, Ore2WD);
						waitForWithdrawnItem(Ore2, 1500);
						break;
					} else if (ore2Count2 > 7 && ore2Count2 != Ore2InvAm) {
						bank.depositAllExcept(nature, Ore1);
						CheckOres2();
						withdraw(Ore2, Ore2WD);
						waitForWithdrawnItem2(Ore2, 1500);
						break;
					} else if (ore2Count2 > 0 && ore2Count2 < 7) {
						CheckOres2();
						withdraw(Ore2, Ore2WD);
						waitForWithdrawnItem2(Ore2, 1500);
						break;
					} else if (ore2Count2 > Ore2InvAm) {
						bank.depositAllExcept(nature, Ore1);
						CheckOres2();
						withdraw(Ore2, Ore2WD);
						waitForWithdrawnItem2(Ore2, 1500);
						break;
					}
				} else if (ore1Count2 == Ore1InvAm && ore2Count2 == Ore2InvAm) {
					if (TabIsOpen == true) {
						DirectHeat = true;
					}
					BankTime = false;
					bank.close();
					break;
				}
			} else if (bankTwice == false) {
				if (ore1Count2 == 0) {
					CheckOres1();
					withdraw(Ore1, Ore1WD);
					waitForWithdrawnItem(Ore1, 1500);
					break;
				} else if (ore1Count2 > Ore1InvAm) {
					bank.depositAllExcept(nature);
					break;
				} else if (ore1Count2 > 0) {
					if (TabIsOpen == true) {
						DirectHeat = true;
					}
					BankTime = false;
					bank.close();
					break;
				}
			}
			break;
		}
		return 50;
	}

	/*
	 * --------------------------------------------------------------------------
	 * ----------------------------------------------------
	 * ----------------------
	 * ----------------------------------------------------
	 * ----------------------------------------------------
	 * --------------------------------------- M E T H O D S
	 * ----------------------------------------------------
	 * ----------------------
	 * ----------------------------------------------------
	 * ----------------------------------------------------
	 * ----------------------
	 * ----------------------------------------------------
	 * ----------------------------------------------------
	 */
	public boolean waitForMageTabWO(int timeout) {
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < timeout) {
			if (getCurrentTab() == TAB_MAGIC) {
				break;
			}
			wait(100);
		}
		if (getCurrentTab() != TAB_MAGIC)
			;
		return false;
	}

	public boolean waitForTab(int tab, int timeout) {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < timeout) {
			if (getCurrentTab() == tab) {
				break;
			}
			wait(100);
		}
		if (getCurrentTab() != tab)
			;
		return false;
	}

	public boolean atLastInventoryItem(int itemID, String option) {
		try {
			if (getCurrentTab() != TAB_INVENTORY
					&& !RSInterface.getInterface(INTERFACE_BANK).isValid()
					&& !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
				openTab(TAB_INVENTORY);
			}
			RSInterfaceChild inventory = getInventoryInterface();
			if (inventory == null || inventory.getComponents() == null)
				return false;

			java.util.List<RSInterfaceComponent> possible = new ArrayList<RSInterfaceComponent>();
			for (RSInterfaceComponent item : inventory.getComponents()) {
				if (item != null && item.getComponentID() == itemID) {
					possible.add(item);
				}
			}
			if (possible.size() == 0)
				return false;
			RSInterfaceComponent item = possible.get(random(
					possible.size() - 1, possible.size()));
			return atInterface(item, option);
		} catch (Exception e) {
			log("atInventoryItem(int itemID, String option) Error: " + e);
			return false;
		}
	}

	private boolean withdraw(int itemID, int amount) {
		String s = amount + "";
		if (bank.isOpen()) {
			if (amount > 0) {
				if (!bank.atItem(itemID, s)) {
					if (bank.atItem(itemID, "X")) {
						wait(random(1000, 1500));
						wait(random(100, 200));
						sendText(s, false);
						wait(random(800, 1000));
						sendText("", true);
						return true;
					}
					return true;
				}
			} else {
				return bank.atItem(itemID, "All");
			}
		}
		return false;
	}

	public int waitForWithdrawnItem(int item, int timeout) {
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < timeout) {
			if (getInventoryCount(item) > 0) {
				break;
			}
			wait(100);
		}
		return getInventoryCount(item);
	}

	public int waitForWithdrawnItem2(int item, int timeout) {
		final int currentAmount;
		long start = System.currentTimeMillis();
		currentAmount = getInventoryCount(item);

		while (System.currentTimeMillis() - start < timeout) {
			if (getInventoryCount(item) > currentAmount) {
				break;
			}
			wait(100);
		}
		return getInventoryCount(item);
	}

	// ************************************************************************************************************

	public void CheckOres1() {
		if (bank.isOpen() && bank.getCount(Ore1) < Ore1InvAm) {
			wait(random(1000, 1500));
			log("Out of ores - Check: 1/3");
			if (bank.isOpen() && bank.getCount(Ore1) < Ore1InvAm) {
				wait(random(1000, 1500));
				log("Out of ores - Check: 2/3");
				if (bank.isOpen() && bank.getCount(Ore1) < Ore1InvAm) {
					wait(random(1000, 1500));
					log("Out of ores - Check: 3/3!");
					bank.close();
					Quit();
				}
			}
		}
	}

	public void CheckOres2() {
		if (bankTwice == true) {
			if (bank.isOpen() && bank.getCount(Ore2) < Ore2InvAm) {
				wait(random(1000, 1500));
				log("Out of ores - Check: 1/3");
				if (bank.isOpen() && bank.getCount(Ore2) < Ore2InvAm) {
					wait(random(1000, 1500));
					log("Out of ores - Check: 2/3");
					if (bank.isOpen() && bank.getCount(Ore2) < Ore2InvAm) {
						wait(random(1000, 1500));
						log("Out of ores - Check: 3/3!");
						bank.close();
						Quit();
					}
				}
			}
		}
	}

	public void Quit() {
		if (TakeAShot == true) {
			ScreenshotUtil.takeScreenshot(true);
			log("Screenshot taken!");
			wait(500);
		}
		stopScript();
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------------------------
	 * ------------------------------------------------
	 * ---------------------------------------------------
	 * -------------------------------- A N T I - B A N
	 * ----------------------------------------------
	 * ----------------------------------------------------------- Standard &
	 * While banking --------------
	 * ----------------------------------------------
	 * ---------------------------------------------------
	 */
	boolean hoverPlayer() {
		RSPlayer player = null;
		int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();

		for (int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			player = new RSPlayer(players[element]);
			String playerName = player.getName();
			String myPlayerName = getMyPlayer().getName();
			if (playerName.equals(myPlayerName)) {
				continue;
			}
			try {
				RSTile targetLoc = player.getLocation();
				String name = player.getName();
				Point checkPlayer = Calculations.tileToScreen(targetLoc);
				if (pointOnScreen(checkPlayer) && checkPlayer != null) {
					clickMouse(checkPlayer, 5, 5, false);
					log("ANTIBAN - Hover Player - Right click on " + name);
				} else {
					continue;
				}
				return true;
			} catch (Exception ignored) {
			}
		}
		return player != null;
	}

	public void hoverObject() {
		examineRandomObject(5);
		wait(randGenerator(50, 1000));
		int moveMouseAfter2 = randGenerator(0, 4);
		wait(randGenerator(100, 800));
		if (moveMouseAfter2 == 1 && moveMouseAfter2 == 2) {
			moveMouse(1, 1, 760, 500);
		}
	}

	int randGenerator(int min, int max) {
		return min + (int) (java.lang.Math.random() * (max - min));
	}

	public RSTile examineRandomObject(int scans) {
		RSTile start = getMyPlayer().getLocation();
		ArrayList<RSTile> possibleTiles = new ArrayList<RSTile>();
		for (int h = 1; h < scans * scans; h += 2) {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < h; j++) {
					int offset = (h + 1) / 2 - 1;
					if (i > 0 && i < h - 1) {
						j = h - 1;
					}
					RSTile tile = new RSTile(start.getX() - offset + i, start
							.getY()
							- offset + j);
					RSObject objectToList = getObjectAt(tile);
					if (objectToList != null && objectToList.getType() == 0
							&& tileOnScreen(objectToList.getLocation())
							&& objectToList.getLocation().isValid()) {
						possibleTiles.add(objectToList.getLocation());
					}
				}
			}
		}
		if (possibleTiles.size() == 0) {
			return null;
		}
		if (possibleTiles.size() > 0 && possibleTiles != null) {
			final RSTile objectLoc = possibleTiles.get(randGenerator(0,
					possibleTiles.size()));
			Point objectPoint = objectLoc.getScreenLocation();
			if (objectPoint != null) {
				try {
					moveMouse(objectPoint);
					if (atMenu("xamine")) {
					} else {
					}
					wait(random(100, 500));
				} catch (NullPointerException ignored) {
				}
			}
		}
		return null;
	}

	public void AntiBan() {
		if (Percentage != 0) {
			int chckObj = random(1, (13000 / Percentage));
			int hover = random(1, (10000 / Percentage));
			int checkxp = random(1, (13000 / Percentage));
			int afk = random(1, (10000 / Percentage));
			int camera = random(1, (2000 / Percentage));
			int HoverObject = random(1, (8000 / Percentage));
			if (chckObj == 5) {
				waitForMageTabWO(1500);
				Status = "ANTIBAN - Checking objective.";
				openTab(Constants.TAB_SUMMONING);
				wait(random(300, 500));
				moveMouse(644, 394, 51, 6);
				wait(random(900, 1600));
				moveMouse(644, 394, 51, 6);
				wait(random(500, 1000));
				moveMouseRandomly(500);
				wait(random(400, 900));
				openTab(Constants.TAB_MAGIC);
			} else if (hover == 5) {
				Status = "ANTIBAN - Right-Clicking a player.";
				hoverPlayer();
				wait(random(1150, 2800));
				while (isMenuOpen()) {
					moveMouseRandomly(750);
					wait(random(400, 1000));
				}
			} else if (HoverObject == 5) {
				Status = "ANTIBAN - Checking a random object.";
				hoverObject();
				wait(random(1150, 2800));
				while (isMenuOpen()) {
					moveMouseRandomly(750);
					wait(random(400, 1000));
				}
			} else if (checkxp == 5) {
				waitForMageTabWO(1500);
				Status = "ANTIBAN - XP Check.";
				final int GambleInt5 = random(0, 100);
				if (GambleInt5 > 50) {
					openTab(TAB_STATS);
					wait(random(400, 800));
					moveMouse(584, 364, 20, 10); // Magic LvL
					wait(random(800, 1200));
					moveMouse(584, 364, 20, 10); // Magic LvL
					wait(random(900, 1750));
					moveMouseRandomly(700);
					wait(random(300, 800));
					openTab(Constants.TAB_MAGIC);
				} else if (GambleInt5 < 51) {
					openTab(TAB_STATS);
					wait(random(400, 800));
					moveMouse(707, 252, 20, 10); // Smithing LvL
					wait(random(800, 1200));
					moveMouse(707, 252, 20, 10); // Smithing LvL
					wait(random(900, 1750));
					moveMouseRandomly(700);
					wait(random(300, 800));
					openTab(Constants.TAB_MAGIC);
				}
			} else if (afk == 5) {
				switch (random(1, 4)) {
				case 1:
					Status = "ANTIBAN - AFK'ing.";
					wait(random(2250, 10000));
					break;
				case 2:
					Status = "ANTIBAN - AFK'ing.";
					wait(random(500, 1000));
					moveMouseRandomly(750);
					wait(random(2000, 10000));
					break;
				case 3:
					Status = "ANTIBAN - AFK'ing.";
					wait(random(0, 500));
					moveMouseRandomly(1000);
					wait(random(500, 1000));
					moveMouseRandomly(1500);
					wait(random(2000, 10000));
					break;
				}
			} else if (camera == 5) {
				int randomTurn = random(1, 4);
				switch (randomTurn) {
				case 1:
					new CameraRotateThread().start();
					break;
				case 2:
					new CameraHeightThread().start();
					break;
				case 3:
					int randomFormation = random(0, 2);
					if (randomFormation == 0) {
						new CameraRotateThread().start();
						new CameraHeightThread().start();
					} else {
						new CameraHeightThread().start();
						moveMouseRandomly(200);
						new CameraRotateThread().start();
					}
				}
			}
			Status = null;
			return;
		}
	}

	public class CameraRotateThread extends Thread {
		@Override
		public void run() {
			char LR = KeyEvent.VK_RIGHT;
			if (random(0, 2) == 0) {
				LR = KeyEvent.VK_LEFT;
			}
			Bot.getInputManager().pressKey(LR);
			try {
				Thread.sleep(random(450, 2600));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(LR);
		}
	}

	public class CameraHeightThread extends Thread {

		@Override
		public void run() {
			char UD = KeyEvent.VK_UP;
			if (random(0, 2) == 0) {
				UD = KeyEvent.VK_DOWN;
			}
			Bot.getInputManager().pressKey(UD);
			try {
				Thread.sleep(random(450, 1700));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(UD);
		}
	}

	public void bankingAntiBan() {
		if ((random(0, (400 / Percentage)) == 2)) {
			int randomTurn = random(1, 4);
			switch (randomTurn) {
			case 1:
				new CameraRotateThread().start();
				break;
			case 2:
				new CameraHeightThread().start();
				break;
			case 3:
				int randomFormation = random(0, 2);
				if (randomFormation == 0) {
					new CameraRotateThread().start();
					new CameraHeightThread().start();
				} else {
					new CameraHeightThread().start();
					new CameraRotateThread().start();
				}
				break;
			}
		}
	}

	public void serverMessageRecieved(ServerMessageEvent msg) {
		String message = msg.getMessage().toLowerCase();
		if (message.contains("have enough nat")) {
			log("Out of nature runes!");
			Quit();
		} else if (message.contains("have enough fir")) {
			log("Out of fire runes!");
			log("Please equip a staff with unlimited fire runes.");
			Quit();
		}
	}

	// ************************************************************************************************************
	@Override
	public void onFinish() {
		log(+BarCounter + " " + BarType + " bars made.");
		log("Thank you for using Fallen's Superheater.");
	}
}