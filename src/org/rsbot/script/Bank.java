package org.rsbot.script;

import java.awt.Point;
import java.util.List;

import org.rsbot.bot.Bot;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * This class is for all the Bank operations.
 */

public class Bank {
	private final Methods methods;

	public static final int[] Bankers = {  44, 45, 494, 495, 499, 958, 1036, 2271, 2354, 2355, 3824, 5488, 5901, 5912, 5913, 6362, 6532, 6533, 6534, 6535, 7605, 8948, 14367, };
	public static final int[] BankBooths = { 2213, 4483, 6084, 11402, 11758, 12759, 14367, 19230, 24914, 25808, 26972, 27663, 29085, 34752, 35647, };
	public static final int[] BankChests = { 4483, 12308, 21301, 27663, 42192, };
	public static final int[] BankDepositBox = { 9398, 20228, 26969, 36788, };

	public static final int INTERFACE_COLLECTION_BOX = 105;

	public static final int INTERFACE_COLLECTION_BOX_CLOSE = 13;

	Bank(final Methods methods) {
		this.methods = methods;
	}

	public boolean atBankBooth(final RSTile booth, final String option) {
		if (!methods.tileOnScreen(booth))
			return false;
		final Point p = Calculations.tileToScreen(booth);
		p.x += methods.random(-10, 11);
		p.y += methods.random(-40, 3);
		if ((p.x < 0) || (p.y < 0))
			return false;
		methods.moveMouse(p);
		final long time = System.currentTimeMillis() + methods.random(400, 800);
		List<String> itemsList = null;
		boolean found = false;
		while ((System.currentTimeMillis() < time) && !found) {
			itemsList = methods.getMenuItems();
			for (int i = 0; (i < itemsList.size()) && !found; i++) {
				found = itemsList.get(i).toLowerCase().contains(option.toLowerCase());
			}
		}
		if (!found)
			return false;
		if (itemsList.get(0).toLowerCase().contains(option)) {
			methods.clickMouse(true);
			return true;
		}
		methods.clickMouse(false);
		return methods.atMenu(option);
	}

	/**
	 * Performs a given action on the specified item ID.
	 * 
	 * @param itemID
	 *            The ID of the item.
	 * @param txt
	 *            The action to perform (see {@link Methods#atMenu}).
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
	 */
	public boolean atItem(final int itemID, final String txt) {
		if (!methods.isLoggedIn() || !isOpen())
			return false;
		// final int[] itemArray = getItemArray();
		// for (int off = 0; off < itemArray.length; off++) {
		// if (itemArray[off] == itemID) {
		// methods.clickMouse(getItemPoint(off), 5, 5, false);
		// return methods.atMenu(txt);
		// }
		// }
		final RSInterfaceComponent item = getItemByID(itemID);
		return (item != null) && item.isValid() && methods.atInterface(item, txt);

	}

	/**
	 * Closes the bank interface. Temp Fix until interfaces are fixed.
	 * 
	 * @return <tt>true</tt> if the interface is no longer open.
	 */
	public boolean close() {
		if (!isOpen())
			return true;
		// methods.clickMouse(new Point(methods.random(481, 496),
		// methods.random(
		// 27, 42)), true);
		methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_CLOSE);
		methods.wait(methods.random(500, 600));
		return !isOpen();
	}

	public boolean deposit(final int itemID, final int numberToDeposit) {
		if (numberToDeposit < 0)
			throw new IllegalArgumentException("numberToDepsoit < 0 (" + numberToDeposit + ")");
		if (!isOpen())
			return false;
		final int inventoryCount = methods.getInventoryCount(true);
		switch (numberToDeposit) {
			case 0: // Deposit All
				methods.atInventoryItem(itemID, "Deposit-All");
				break;
			case 1: // Deposit 1
				methods.atInventoryItem(itemID, "Deposit-1");
				break;
			case 5: // Deposit 5
				methods.atInventoryItem(itemID, "Deposit-5");
				break;
			case 10: // Deposit 10
				methods.atInventoryItem(itemID, "Deposit-10");
				break;
			default: // Deposit x
				if (!methods.atInventoryItem(itemID, "Deposit-" + numberToDeposit)) {
					if (methods.atInventoryItem(itemID, "Deposit-X")) {
						methods.wait(methods.random(1000, 1300));
						Bot.getInputManager().sendKeys("" + numberToDeposit, true);
					}
				}
				break;
		}
		if ((methods.getInventoryCount(true) < inventoryCount) || (methods.getInventoryCount() == 0))
			return true;
		return false;
	}

	/**
	 * Deposits all items in inventory.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean depositAll() {
		return isOpen() && methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_DEPOSIT_CARRIED_ITEMS);
	}

	/**
	 * Deposit everything your player has equipped.
	 * 
	 * @return <tt>true</tt> on success.
	 * @since 6 March 2009.
	 */
	public boolean depositAllEquipped() {
		return isOpen() && methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_DEPOSIT_WORN_ITEMS);
	}

	/**
	 * Deposits all items in inventory except for the given IDs.
	 * 
	 * @param items
	 *            The items not to deposit.
	 * @return <tt>true</tt> on success.
	 */
	public boolean depositAllExcept(final int... items) {
		int inventoryCount = methods.getInventoryCount();
		int[] inventoryArray = methods.getInventoryArray();
		outer: for (int off = 0; off < inventoryArray.length; off++) {
			if (inventoryArray[off] == -1) {
				continue;
			}
			for (final int item : items) {
				if (inventoryArray[off] == item) {
					continue outer;
				}
			}

			for (int tries = 0; tries < 5; tries++) {
				methods.atInventoryItem(inventoryArray[off], "Deposit-All");
				methods.wait(methods.random(500, 700));
				if (methods.getInventoryCount() < inventoryCount) {
					break;
				}
			}
			if (methods.getInventoryCount() >= inventoryCount)
				return false;
			inventoryArray = methods.getInventoryArray();
			inventoryCount = methods.getInventoryCount();
		}
		return true;
	}

	/**
	 * Deposits everything your familiar is carrying.
	 * 
	 * @return <tt>true</tt> on success
	 * @since 6 March 2009.
	 */
	public boolean depositAllFamiliar() {
		return isOpen() && methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_DEPOSIT_BEAST_INVENTORY);
	}

	/**
	 * Returns the sum of the count of the given items in the bank.
	 * 
	 * @param items
	 *            The array of items.
	 * @return The sum of the stacks of the items.
	 */
	public int getCount(final int... items) {
		int itemCount = 0;
		final int[] inventoryArray = getItemArray();
		for (int off = 0; off < inventoryArray.length; off++) {
			for (final int item : items) {
				if (inventoryArray[off] == item) {
					itemCount += getStackSizes()[off];
				}
			}
		}
		return itemCount;
	}

	/**
	 * Get current tab open in the bank.
	 * 
	 * @return int of tab (0-8), or -1 if none are selected (bank is not open).
	 */
	public int getCurrentTab() {
		for (int i = 0; i < Constants.INTERFACE_BANK_TAB.length; i++) {
			if (RSInterface.getInterface(Constants.INTERFACE_BANK).getChild(Constants.INTERFACE_BANK_TAB[i] - 1).getBackgroundColor() == 1419)
				return i;
		}
		return -1; // no selected ones. Bank may not be open.
	}

	/**
	 * Gets the bank interface.
	 * 
	 * @return The bank <code>RSInterface</code>.
	 */
	public RSInterface getInterface() {
		return RSInterface.getInterface(Constants.INTERFACE_BANK);
	}

	public RSInterfaceComponent getItem(final int index) {
		final RSInterfaceComponent[] items = getItems();
		if (items != null) {
			for (final RSInterfaceComponent item : items) {
				if (item.getComponentIndex() == index)
					return item;
			}
		}

		return null;
	}

	/**
	 * Gets the array of item IDs in the bank.
	 * 
	 * @return The item IDs array.
	 */
	public int[] getItemArray() {
		final RSInterfaceComponent[] items = getInterface().getChild(Constants.INTERFACE_BANK_INVENTORY).getComponents();
		if (items != null) {
			final int[] value = new int[items.length];
			for (final RSInterfaceComponent item : items) {
				value[item.getComponentIndex()] = item.getComponentID();
			}
			return value;
		}

		return new int[0];
	}

	/**
	 * Makes it easier to get Items in the bank. Written by Fusion89k.
	 * 
	 * @param id
	 *            ID of the item to get.
	 * @return The component of the item.
	 */
	public RSInterfaceComponent getItemByID(final int id) {
		final RSInterfaceComponent[] items = getItems();
		if (items != null) {
			for (final RSInterfaceComponent item : items) {
				if (item.getComponentID() == id)
					return item;
			}
		}

		return null;
	}

	public String[] getItemNames() {
		final RSInterfaceComponent[] items = getInterface().getChild(Constants.INTERFACE_BANK_INVENTORY).getComponents();
		if (items != null) {
			final String[] value = new String[items.length];
			for (final RSInterfaceComponent item : items) {
				value[item.getComponentIndex()] = item.getComponentName();
			}
			return value;
		}

		return new String[0];
	}

	/**
	 * Gets the point on the screen for a given item. Numbered left to right
	 * then top to bottom. Written by Qauters.
	 * 
	 * @param slot
	 *            The index of the item.
	 * @return The point of the item.
	 */
	public Point getItemPoint(final int slot) {
		// And I will strike down upon thee with great vengeance and furious
		// anger those who attempt to replace the following code with fixed
		// constants!

		if (slot < 0)
			throw new IllegalArgumentException("slot < 0 " + slot);

		final RSInterfaceComponent item = getItem(slot);
		if (item != null)
			return item.getPosition();

		return new Point(-1, -1);
	}

	public RSInterfaceComponent[] getItems() {
		if ((getInterface() == null) || (getInterface().getChild(Constants.INTERFACE_BANK_INVENTORY) == null))
			return new RSInterfaceComponent[0];

		return getInterface().getChild(Constants.INTERFACE_BANK_INVENTORY).getComponents();
	}

	/**
	 * Gets the array of item stack sizes in the bank.
	 * 
	 * @return The stack sizes array.
	 */
	public int[] getStackSizes() {
		final RSInterfaceComponent[] items = getInterface().getChild(Constants.INTERFACE_BANK_INVENTORY).getComponents();
		if (items != null) {
			final int[] value = new int[items.length];
			for (final RSInterfaceComponent item : items) {
				value[item.getComponentIndex()] = item.getComponentStackSize();
			}
			return value;
		}

		return new int[0];
	}

	/**
	 * @return <tt>true</tt> if the bank interface is open; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isOpen() {
		return getInterface().isValid();
	}

	/**
	 * Opens one of the supported banker NPCs, booths, or chests nearby. If they
	 * are not nearby, and they are not null, it will automatically walk to the
	 * closest one. Written by: Taha
	 * 
	 * @return <tt>true</tt> if the bank was opened; otherwise <tt>false</tt>.
	 */
	public boolean open() {
		try {
			if (methods.isMenuOpen()) {
				methods.moveMouseSlightly();
				methods.wait(methods.random(20, 30));
			}
			RSObject bankBooth = methods.getNearestObjectByID(Bank.BankBooths);
			RSNPC banker = methods.getNearestNPCByID(Bank.Bankers);
			final RSObject bankChest = methods.getNearestObjectByID(Bank.BankChests);
			int lowestDist = methods.distanceTo(bankBooth);
			if ((banker != null) && (methods.distanceTo(banker) < lowestDist)) {
				lowestDist = methods.distanceTo(banker);
				bankBooth = null;
			}
			if ((bankChest != null) && (methods.distanceTo(bankChest) < lowestDist)) {
				bankBooth = null;
				banker = null;
			}
			if (((bankBooth != null) && (methods.distanceTo(bankBooth) < 8) && methods.tileOnMap(bankBooth.getLocation()) && methods.canReach(bankBooth, true)) || ((banker != null) && (methods.distanceTo(banker) < 8) && methods.tileOnMap(banker.getLocation()) && methods.canReach(banker, true)) || ((bankChest != null) && (methods.distanceTo(bankChest) < 8) && methods.tileOnMap(bankChest.getLocation()) && methods.canReach(bankChest, true) && !isOpen())) {
				if (bankBooth != null) {
					if (methods.atObject(bankBooth, "Use-Quickly")) {
						methods.wait(methods.random(200, 400));
					} else {
						methods.turnToObject(bankChest);
					}
				} else if (banker != null) {
					if (methods.atNPC(banker, "Bank ")) {
						methods.wait(methods.random(200, 400));
					} else {
						methods.turnToCharacter(banker, 20);
					}
				} else if (bankChest != null) {
					if (methods.atObject(bankChest, "Bank")) {
						methods.wait(methods.random(200, 400));
					} else {
						methods.turnToObject(bankChest);
					}
				}
			} else {
				if (bankBooth != null) {
					methods.walkTo(bankBooth.getLocation(), 1, 1);
					while (methods.distanceTo(methods.getDestination()) > 8) {
						methods.wait(methods.random(200, 400));
					}
					return open();
				} else if (banker != null) {
					methods.walkTo(banker.getLocation(), 1, 1);
					while (methods.distanceTo(methods.getDestination()) > 8) {
						methods.wait(methods.random(200, 400));
					}
					return open();
				} else if (bankChest != null) {
					methods.walkTo(bankChest.getLocation(), 1, 1);
					while (methods.distanceTo(methods.getDestination()) > 8) {
						methods.wait(methods.random(200, 400));
					}
					return open();
				}
			}
			return isOpen();
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Opens the bank tab.
	 * 
	 * @param tabNumber
	 *            The tab number - e.g. view all is 1.
	 * @return <tt>true</tt> on success.
	 * @since 6 March 2009.
	 */
	public boolean openTab(final int tabNumber) {
		return isOpen() && methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_TAB[tabNumber - 1]);
	}

	/**
	 * @return <tt>true</tt> if currently searching the bank.
	 */
	public boolean isSearchOpen() {
		//Setting 1248 is -2147483648 when search is enabled and -2013265920
		return (methods.getSetting(1248) == -2147483648);
	}

	/**
	 * Searches for an item in the bank. Returns true if succeeded (does not
	 * necessarily mean it was found).
	 *
	 * @param itemName
	 *            The item name to find.
	 * @return <tt>true</tt> on success.
	 */
	public boolean searchItem(final String itemName) {
		if (!isOpen())
			return false;

		methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_SEARCH, "Search");
		methods.wait(methods.random(1000, 1500));
		if (!isSearchOpen()) {
			methods.wait(500);
		}
		if (isOpen() && isSearchOpen()) {
			Bot.getInputManager().sendKeys(itemName, false);
			methods.wait(methods.random(300, 700));
			return true;
		}
		return false;
	}

	/**
	 * Sets the bank rearrange mode to insert.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setRearrangeModeToInsert() {
		if (!isOpen())
			return false;
		if (methods.getSetting(Constants.SETTING_BANK_TOGGLE_REARRANGE_MODE) != 1) {
			methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_INSERT);
			methods.wait(methods.random(500, 700));
		}
		return methods.getSetting(Constants.SETTING_BANK_TOGGLE_REARRANGE_MODE) == 1;
	}

	/**
	 * Sets the bank rearrange mode to swap.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setRearrangeModeToSwap() {
		if (!isOpen())
			return false;
		if (methods.getSetting(Constants.SETTING_BANK_TOGGLE_REARRANGE_MODE) != 0) {
			methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_SWAP);
			methods.wait(methods.random(500, 700));
		}
		return methods.getSetting(Constants.SETTING_BANK_TOGGLE_REARRANGE_MODE) == 0;
	}

	/**
	 * Sets the bank withdraw mode to item.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setWithdrawModeToItem() {
		if (!isOpen())
			return false;
		if (methods.getSetting(Constants.SETTING_BANK_TOGGLE_WITHDRAW_MODE) != 0) {
			methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_ITEM);
			methods.wait(methods.random(500, 700));
		}
		return methods.getSetting(Constants.SETTING_BANK_TOGGLE_WITHDRAW_MODE) == 0;
	}

	/**
	 * Sets the bank withdraw mode to note.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setWithdrawModeToNote() {
		if (!isOpen())
			return false;
		if (methods.getSetting(Constants.SETTING_BANK_TOGGLE_WITHDRAW_MODE) != 1) {
			methods.atInterface(Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_BUTTON_NOTE);
			methods.wait(methods.random(500, 700));
		}
		return methods.getSetting(Constants.SETTING_BANK_TOGGLE_WITHDRAW_MODE) == 1;
	}

	/**
	 * Tries to withdraw an item.
	 * 
	 * 0 is All. 1,5,10 use Withdraw 1,5,10 while other numbers Withdraw X.
	 * 
	 * @param itemID
	 *            The ID of the item.
	 * @param count
	 *            The number to withdraw.
	 * @return <tt>true</tt> on success.
	 */
	public boolean withdraw(final int itemID, final int count) {
		if (count < 0)
			throw new IllegalArgumentException("count < 0 (" + count + ")");
		if (!isOpen())
			return false;
		final RSInterfaceComponent item = getItemByID(itemID);
		if ((item == null) || !item.isValid())
			return false;
		final int inventoryCount = methods.getInventoryCount(true);
		switch (count) {
			case 0: // Withdraw All
				methods.atInterface(item, "Withdraw-All");
				break;
			case 1: // Withdraw 1
			case 5: // Withdraw 5
			case 10: // Withdraw 10
				methods.atInterface(item, "Withdraw-" + count);
				break;
			default: // Withdraw x
				if (methods.atInterface(item, false)) {
					methods.wait(methods.random(600, 900));
					java.util.ArrayList<String> mactions = methods.getMenuActions();
					boolean found = false;
					for (int i = 0; i < mactions.size(); i++) {
						if (mactions.get(i).equalsIgnoreCase("Withdraw-" + count)) {
							found = true;
							methods.atMenuItem(i);
							break;
						}
					}
					if (!found && methods.atInterface(item, "Withdraw-X")) {
						methods.wait(methods.random(1000, 1300));
						Bot.getInputManager().sendKeys("" + count, true);
					}
				}
				break;
		}
		if ((methods.getInventoryCount(true) > inventoryCount) || (methods.getInventoryCount(true) == 28))
			return true;
		return false;
	}
}
