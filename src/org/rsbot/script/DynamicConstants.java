package org.rsbot.script;

import org.rsbot.accessors.RSInterface;
import org.rsbot.bot.Bot;

/**
 * This class finds interfaces on the fly.</br> All methods are synchronized for
 * thread safety.</br> For internal use only!
 * 
 * @author Qauters
 */
public class DynamicConstants implements Constants {
	static {
		// Perform full reset on initialization
		DynamicConstants.resetIDs();
	}

	private static int ind_GUI;
	private static int ind_Minimap;
	private static int ind_Compass;
	private static int[] ind_Tabs;

	private static synchronized void checkGUI() {
		if (DynamicConstants.ind_GUI != Bot.getClient().getGUIRSInterfaceIndex()) {
			DynamicConstants.resetIDs();
			DynamicConstants.ind_GUI = Bot.getClient().getGUIRSInterfaceIndex();
		}
	}

	static synchronized RSInterface getCompass() {
		// Check for GUI changes
		DynamicConstants.checkGUI();

		// Get GUI interface
		final RSInterface[] gui = DynamicConstants.ind_GUI != -1 ? Bot.getClient().getRSInterfaceCache()[DynamicConstants.ind_GUI] : null;
		if (gui == null)
			return null;

		// Check if we need to find a new compass index
		if (DynamicConstants.ind_Compass == -1) {
			for (int i = 0; i < gui.length; i++) {
				if ((gui[i] != null) && (gui[i].getActions() != null) && (gui[i].getActions().length == 1) && gui[i].getActions()[0].equals("Face North")) {
					DynamicConstants.ind_Compass = i;
					break;
				}
			}
		}

		// Return the compass interface
		if (DynamicConstants.ind_Compass != -1)
			return gui[DynamicConstants.ind_Compass];

		return null;
	}

	static synchronized RSInterface getMinimapInterface() {
		// Check for GUI changes
		DynamicConstants.checkGUI();

		// Get the GUI interface
		final RSInterface[] gui = DynamicConstants.ind_GUI != -1 ? Bot.getClient().getRSInterfaceCache()[DynamicConstants.ind_GUI] : null;
		if (gui == null)
			return null;

		// Check if we need to find the new minimap index
		if (DynamicConstants.ind_Minimap == -1) {
			for (int i = 0; i < gui.length; i++) {
				if ((gui[i] != null) && (gui[i].getSpecialType() == 1338)) {
					DynamicConstants.ind_Minimap = i;
					break;
				}
			}
		}

		// Return minimap interface
		if (DynamicConstants.ind_Minimap != -1)
			return gui[DynamicConstants.ind_Minimap];

		return null;
	}

	static synchronized RSInterface getTab(final int id) {
		// Check argument
		if ((id < 0) || (id >= DynamicConstants.ind_Tabs.length))
			return null;

		// Check for GUI changes
		DynamicConstants.checkGUI();

		// Get GUI interface
		final RSInterface[] gui = DynamicConstants.ind_GUI != -1 ? Bot.getClient().getRSInterfaceCache()[DynamicConstants.ind_GUI] : null;
		if (gui == null)
			return null;

		// Check if we need to find a new tab index
		if (DynamicConstants.ind_Tabs[id] == -1) {
			for (int i = 0; i < gui.length; i++) {
				if ((gui[i] != null) && (gui[i].getActions() != null) && (gui[i].getActions().length > 0) && gui[i].getActions()[0].equals(Constants.TAB_NAMES[id])) {
					DynamicConstants.ind_Tabs[id] = i;
					break;
				}
			}
		}

		// Return the tab interface
		if (DynamicConstants.ind_Tabs[id] != -1)
			return gui[DynamicConstants.ind_Tabs[id]];

		return null;
	}

	private static synchronized void resetIDs() {
		DynamicConstants.ind_GUI = -1;
		DynamicConstants.ind_Minimap = -1;
		DynamicConstants.ind_Compass = -1;

		DynamicConstants.ind_Tabs = new int[17];
		for (int i = 0; i < DynamicConstants.ind_Tabs.length; i++) {
			DynamicConstants.ind_Tabs[i] = -1;
		}
	}
}
