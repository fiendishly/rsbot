package org.rsbot.script.randoms.antiban;

import java.awt.Rectangle;

import org.rsbot.bot.Bot;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;

/**
 * @author Iscream
 */
@ScriptManifest(authors = {"Iscream"}, name = "LoginBot", version = 1.1)
public class LoginBot extends Random {

	private static final int INTERFACE_MAIN = 905;
	private static final int INTERFACE_MAIN_CHILD = 59;
	private static final int INTERFACE_MAIN_CHILD_COMPONENT_ID = 4;
	private static final int INTERFACE_LOGIN_SCREEN = 596;
	private static final int INTERFACE_USERNAME = 71;
	private static final int INTERFACE_USERNAME_WINDOW = 67;
	private static final int INTERFACE_PASSWORD = 91;
	private static final int INTERFACE_PASSWORD_WINDOW = 74;
	private static final int INTERFACE_BUTTON_LOGIN = 75;
	private static final int INTERFACE_TEXT_RETURN = 30;
	private static final int INTERFACE_WELCOME_SCREEN = 906;
	private static final int INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_1 = 168;
	private static final int INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_2 = 170;

	private static final int INDEX_LOGGED_OUT = 2;
	private static final int INDEX_LOBBY = 6;

	private int invalidCount, worldFullCount;

	public boolean activateCondition() {
		int idx = getLoginIndex();
		if (idx == INDEX_LOGGED_OUT || idx == INDEX_LOBBY) {
			return true;
		}
		return false;
	}

	public int loop() {
		String username = Bot.getAccountName().replaceAll("_", " ").toLowerCase().trim();
		String returnText = RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).
				getChild(INTERFACE_TEXT_RETURN).getText().toLowerCase();
		int textlength;
		if (getLoginIndex() != INDEX_LOGGED_OUT) {
			if (!isWelcomeScreen()) {
				wait(random(1000, 2000));
			}
			if (getLoginIndex() == INDEX_LOBBY) {
				RSInterface welcome_screen = getInterface(INTERFACE_WELCOME_SCREEN);
				RSInterfaceChild welcome_screen_button_play_1 = welcome_screen.getChild(INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_1);
				RSInterfaceChild welcome_screen_button_play_2 = welcome_screen.getChild(INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_2);

				clickMouse(
						welcome_screen_button_play_1.getAbsoluteX(),
						welcome_screen_button_play_1.getAbsoluteY(),
						welcome_screen_button_play_2.getAbsoluteX() + welcome_screen_button_play_2.getWidth() - welcome_screen_button_play_1.getAbsoluteX(),
						welcome_screen_button_play_1.getHeight(),
						true
				);

				for (int i = 0; i < 4 && getLoginIndex() == 6; i++)
					wait(500);
			}
			return -1;
		}
		if (!isLoggedIn()) {
			if (returnText.contains("update")) {
				log("Runescape has been updated, please reload RSBot.");
				stopScript(false);
			}
			if (returnText.contains("disable")) {
				log("Your account is banned/disabled.");
				stopScript(false);
			}
			if (returnText.contains("members")) {
				log("We have attempted to log into a members world as a free to play player, stopping script.");
				stopScript(false);
			}
			if (returnText.contains("incorrect")) {
				log("Failed to login five times in a row. Stopping all scripts.");
				stopScript(false);
			}
			if (returnText.contains("invalid")) {
				if (invalidCount > 6) {
					log("Unable To Login After 6 Attempts, Stopping Script.");
					log("Please verify that your RSBot account profile is correct.");
					stopScript(false);
				}
				atInterface(RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(38));
				invalidCount++;
				return (random(500, 2000));
			}
			if (returnText.contains("full")) {
				if (worldFullCount > 30) {
					log("World Is Full. Waiting for 15 seconds.");
					wait(random(10000, 15000));
					worldFullCount = 0;
				}
				wait(random(1000, 1200));
				worldFullCount++;
			}
			if (returnText.contains("world")) {
				return random(1000, 1200);
			}
			if (returnText.contains("performing login")) {
				return random(1000, 1200);
			}
		}
		if (getLoginIndex() == 2) {
			if (!atLoginScreen()) {
				atComponent(RSInterface.getChildInterface(INTERFACE_MAIN, INTERFACE_MAIN_CHILD).getComponents()[INTERFACE_MAIN_CHILD_COMPONENT_ID], "");
				return random(500, 600);
			}
			if (isUsernameFilled() && isPasswordFilled()) {
				atInterface(RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_BUTTON_LOGIN));
				return random(500,600);
			}
			if (!isUsernameFilled()) {
				atLoginInterface(RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_USERNAME_WINDOW));
				wait(random(500,700));
				textlength = RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_USERNAME).getText().length() + random(3, 5);
				for (int i = 0; i <= textlength + random(1,5); i++) {
					sendText("\b", false);
					if (random(0, 2) == 1) {
						wait(random(25, 100));
					}
				}
				sendText(username, false);
			}
			if (isUsernameFilled() && !isPasswordFilled()) {
				atLoginInterface(RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_PASSWORD_WINDOW));
				wait(random(500,700));
				textlength = RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_PASSWORD).getText().length() + random(3, 5);
				for (int i = 0; i <= textlength + random(1,5); i++) {
					sendText("\b", false);
					if (random(0, 2) == 1) {
						wait(random(25, 100));
					}
				}
				sendText(AccountManager.getPassword(Bot.getAccountName()), false);
			}
		}
		return random(500, 2000);
	}

	//Clicks past all of the letters
	private boolean atLoginInterface(RSInterfaceChild i) {
		if (!i.isValid())
			return false;
		Rectangle pos = i.getArea();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1)
			return false;
		int dy = (int) (pos.getHeight() - 4) / 2;
		int maxRandomX = (int) (pos.getMaxX() - pos.getCenterX());
		int midx = (int) (pos.getCenterX());
		int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
		if (i.getIndex() == INTERFACE_PASSWORD_WINDOW) {
		clickMouse(MinX(i), midy + random(-dy, dy), true);
		} else {
		clickMouse(midx + random(1,maxRandomX), midy + random(-dy, dy), true);
		}
		return true;
	}

	/*
	 * Returns x int based on the letters in a Child
	 * Only the password text is needed as the username text cannot reach past the middle of the interface
	 */
	private int MinX(RSInterfaceChild a) {
		int x = 0;
		Rectangle pos = a.getArea();
		int dx = (int) (pos.getWidth() - 4) / 2;
		int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1)
			return 0;
		for (int i = 0; i < RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_PASSWORD).getText().length(); i++) {
			x += 11;
		}
		if (x  > 44) {
		return (int) (pos.getMinX() + x + 15);
		} else {
		return midx + random(-dx,dx);
		}
	}

	private boolean atLoginScreen() {
		return getInterface(596).isValid();
	}

	private boolean isUsernameFilled() {
		String username = Bot.getAccountName().replaceAll("_", " ").toLowerCase().trim();
		return RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_USERNAME).getText().toLowerCase().equalsIgnoreCase(username);
	}
	
	private boolean isPasswordFilled() {
		return RSInterface.getInterface(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_PASSWORD).getText().toLowerCase().length() == AccountManager.getPassword(Bot.getAccountName()).length();
	}
}