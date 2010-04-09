package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
/*
 * Updated by Iscream Mar 04,10
 */
@ScriptManifest(authors = { "Qauters", "Gnarly" }, name = "Welcome Screen", version = 1.6)
public class WelcomeScreen extends Random {
	private static final int INTERFACE_WELCOME_SCREEN = 906;
	private static final int INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_1 = 168;
	private static final int INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_2 = 170;

	@Override
	public boolean activateCondition() {
		return getLoginIndex() == 6;
	}

	@Override
	public int loop() {
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
		
		for(int i = 0; i < 4 && getLoginIndex() == 6; i++)
			wait(500);
		
		return -1;
	}

}
