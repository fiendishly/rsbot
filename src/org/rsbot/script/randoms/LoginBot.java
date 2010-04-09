package org.rsbot.script.randoms;

import java.util.ArrayList;

import org.rsbot.bot.Bot;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
/**
 * @author Iscream
 */
@ScriptManifest(authors = { "Iscream" }, name = "LoginBot", version = 1)
public class LoginBot extends Random {

	private final int MAIN_LOGIN_SCREEN = 905;
	private final int MAIN_LOGIN_SCREEN_CHILD = 59;
	private final int MAIN_LOGIN_SCREEN_CHILD_COMPONENT_ID = 4;
	private final int LOGIN_SCREEN = 596;
	private final int LOGIN_SCREEN_USERNAME_INTERFACE = 71;
	private final int LOGIN_SCREEN_PASSWORD_INTERFACE = 91;
	private final int LOGIN_SCREEN_BUTTON_LOGIN = 75;
	private final int LOGIN_SCREEN_RETURN_TEXT = 30;

	private String accountUsername;
	private String returninterfacetext;

	private int invalidcount;
	private int fullworldcount;
	private int loginScreenTexture;
	private int textlength;
	private int loginbuttonfailsafe;

	private Boolean HasLoggedIn;
	final java.util.List<Integer> Changes = new ArrayList<Integer>();
	@Override
	public boolean activateCondition() {
		accountUsername = Bot.getAccountName().replaceAll("_", " ").toLowerCase().trim();
		/*RSInterface a = RSInterface.getInterface(669);
		for (int c = 0;c < a.getChildCount() ;c++) {
			Changes.add(a.getChild(c).getBackgroundColor());
		}*/
		HasLoggedIn = false;
		return getLoginIndex() == 2;
	}
	private boolean atloginscreen() {
		loginScreenTexture = RSInterface.getInterface(LOGIN_SCREEN).getChild(75).getBackgroundColor();
		if (loginScreenTexture == 2524) {
			return true;
		}
		return false;
	}
	@SuppressWarnings("unused")
	private void interfacechanges(int Interface) {
		RSInterface a = RSInterface.getInterface(Interface);
		for (int c = 0;c < a.getChildCount() ;c++) {
			if (c != -1) {
				if (Changes.get(c) != a.getChild(c).getBackgroundColor()) {
				}
				Changes.add(a.getChild(c).getBackgroundColor());
			}
		}
	}
	@Override
	public int loop() {
		returninterfacetext = RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_RETURN_TEXT).getText().toLowerCase();
		if (getLoginIndex() != 2) {
			if (!isWelcomeScreen()) {
				wait(random(1000,2000));
			}
			return -1;
		}
		if (!isLoggedIn()) {
			if (returninterfacetext.contains("update")) {
				log("Runescape has been updated, please reload RSBot.");
				stopScript(false);
			}
			if (returninterfacetext.contains("disable")) {
				log("Your account is banned/disabled.");
				stopScript(false);
			}
			if (returninterfacetext.contains("members")) {
				log("We have attempted to log into a members world as a free to play player, stopping script.");
				stopScript(false);
			}
			if (returninterfacetext.contains("incorrect")) {
				log("Failed to login five times in a row. Stopping all scripts.");
				stopScript(false);
			}
			if (returninterfacetext.contains("invalid")) {
				if (invalidcount > 6) {
					log("Unable To Login After 6 Attempts, Stopping Script.");
					log("Please verify that your RSBot account profile is correct.");
					stopScript(false);
				}
				atInterface(RSInterface.getInterface(LOGIN_SCREEN).getChild(32));
				atInterface(RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_USERNAME_INTERFACE));
				textlength = RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_USERNAME_INTERFACE).getText().length() + random(0, 4);
				for (int i = 0; i <= textlength; i++) {
					sendText("\b", false);
					if (random(0, 2) == 1) {
						wait(random(50,100));
					}
				}
				sendText("",true);
				textlength = RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_PASSWORD_INTERFACE).getText().length() + random(0, 4);
				for (int i = 0; i <= textlength + random(0, 3); i++) {
					sendText("\b", false);
					if (random(0, 2) == 1) {
						wait(random(50,100));
					}
				}
				wait(random(500, 2000));
				invalidcount++;
			}
			if (returninterfacetext.contains("full")) {
				if (fullworldcount > 30) {
					log("World Is Full. Waiting for 15 seconds.");
					wait(random(10000,20000));
					fullworldcount = 0;
				}
				wait(random(1000,1200));
				fullworldcount++;
			}
			if (returninterfacetext.contains("world")) {
				return random(1000,1200);
			}
			if (returninterfacetext.contains("performing login")) {
				return random(1000,1200);
			}
		}
		if (getLoginIndex() == 2) {
			if (!HasLoggedIn) {
				atComponent(RSInterface.getChildInterface(MAIN_LOGIN_SCREEN,MAIN_LOGIN_SCREEN_CHILD).getComponents()[MAIN_LOGIN_SCREEN_CHILD_COMPONENT_ID],"");
				HasLoggedIn = true;
				return random(500,600);
			}
			if (RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_USERNAME_INTERFACE).getText().toLowerCase().equalsIgnoreCase(accountUsername) && RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_PASSWORD_INTERFACE).getText().toLowerCase().length() == AccountManager.getPassword(Bot.getAccountName()).length()){
				atInterface(RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_BUTTON_LOGIN));
			}
			if (!RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_USERNAME_INTERFACE).getText().toLowerCase().equalsIgnoreCase(accountUsername)){
				atInterface(RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_USERNAME_INTERFACE));
				textlength = RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_USERNAME_INTERFACE).getText().length() + random(0, 4);
				for (int i = 0; i <= textlength; i++) {
					sendText("\b", false);
					if (random(0, 2) == 1) {
						wait(random(50,100));
					}
				}
				sendText(accountUsername,false);
			}
			if (RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_USERNAME_INTERFACE).getText().toLowerCase().equalsIgnoreCase(accountUsername) && RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_PASSWORD_INTERFACE).getText().toLowerCase().length() != AccountManager.getPassword(Bot.getAccountName()).length()){
				atInterface(RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_PASSWORD_INTERFACE));
				textlength = RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_PASSWORD_INTERFACE).getText().length() + random(0, 4);
				for (int i = 0; i <= textlength; i++) {
					sendText("\b", false);
					if (random(0, 2) == 1) {
						wait(random(50,100));
					}
				}
				sendText(AccountManager.getPassword(Bot.getAccountName()),false);
			}
			if (!RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_USERNAME_INTERFACE).getText().toLowerCase().equalsIgnoreCase(accountUsername) && RSInterface.getInterface(LOGIN_SCREEN).getChild(LOGIN_SCREEN_PASSWORD_INTERFACE).getText().toLowerCase().length() != AccountManager.getPassword(Bot.getAccountName()).length()) {
				if (loginbuttonfailsafe >= 2) {
					HasLoggedIn = false;
					loginbuttonfailsafe = 0;
				} 
				loginbuttonfailsafe++;
			}
		}
		return random(500,2000);
	}
}