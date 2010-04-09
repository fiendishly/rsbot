package org.rsbot.script;

import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceComponent;

/**
 * This class is for all the Store operations.
 */

public class Store {
	private final Methods methods;
	private int stock = 24;

	Store(final Methods methods) {
		this.methods = methods;
	}

	/**
	 * Performs a given action on the specified item id. Returns atMenu.
	 * 
	 * @param itemID
	 *            the id of the item
	 * @param txt
	 *            the action to perform (see {@link Methods#atMenu})
	 * @return true on success
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
		if ((item != null) && item.isValid())
			return methods.atInterface(item, txt);

		return false;
	}

	/**
	 * Tries to buy an item.
	 * 
	 * 0 is All. 1,5,10 use buy 1,5,10 while other numbers buy X.
	 * 
	 * @param itemID
	 *            The id of the item.
	 * @param count
	 *            The number to buy.
	 * @return true on success
	 */
	public boolean buy(final int itemID, final int count) {
		if (count < 0)
			throw new IllegalArgumentException("count < 0 " + count);
		if (!isOpen())
			return false;
		final int inventoryCount = methods.getInventoryCount(true);
		for (int tries = 0; tries < 5; tries++) {
			switch (count) {
				case 0: // Withdraw All
					atItem(itemID, "Buy All");
					break;
				case 1: // Withdraw 1
					atItem(itemID, "Buy 1");
					break;
				case 5: // Withdraw 5
					atItem(itemID, "Buy 5");
					break;
				case 10: // Withdraw 10
					atItem(itemID, "Buy 10");
					break;
				case 50: // Withdraw 50
					atItem(itemID, "Buy 50");
				default: // Withdraw x
					atItem(itemID, "Buy X");
					methods.wait(methods.random(900, 1100));
					Bot.getInputManager().sendKeys("" + count, true);
			}
			methods.wait(methods.random(500, 700));
			if (methods.getInventoryCount(true) > inventoryCount)
				return true;
		}
		return false;
	}

	/**
	 * Closes the Store interface. Temp Fix until interfaces are fixed
	 * 
	 * @return true if the interface is no longer open
	 */
	public boolean close() {
		if (!isOpen())
			return true;
		// methods.clickMouse(new Point(methods.random(481, 496),
		// methods.random(
		// 27, 42)), true);
		methods.atInterface(620, 18);
		methods.wait(methods.random(500, 600));
		return !isOpen();
	}

	/**
	 * Gets the store interface.
	 * 
	 * @return the store interface
	 */
	public RSInterface getInterface() {
		return RSInterface.getInterface(620);
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
	 * Makes it easier to get Items in the store Written by Fusion89k
	 * 
	 * @param id
	 *            ID of the item to get
	 * @return the component of the item
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
		final RSInterfaceComponent[] items = getInterface().getChild(stock).getComponents();
		if (items != null) {
			final String[] value = new String[items.length];
			for (int i = 0; i < items.length; i++) {
				value[items[i].getComponentIndex()] = items[i].getComponentName();
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
	 *            the index of the item
	 * @return the point of the item
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
		if ((getInterface() == null) || (getInterface().getChild(stock) == null))
			return new RSInterfaceComponent[0];

		return getInterface().getChild(stock).getComponents();
	}

	/**
	 * Gets the array of item stack sizes in the store
	 * 
	 * @return the stack sizes
	 */
	public int[] getStackSizes() {
		final RSInterfaceComponent[] items = getInterface().getChild(stock).getComponents();
		if (items != null) {
			final int[] value = new int[items.length];
			for (int i = 0; i < items.length; i++) {
				value[i] = items[i].getComponentStackSize();
			}
			return value;
		}

		return new int[0];
	}

	/**
	 * @return true if the store interface is open, false otherwise
	 */
	public boolean isOpen() {
		return getInterface().isValid();
	}

	/**
	 * Allows switching between main stock and player stock Written by Fusion89k
	 * 
	 * @param mainStock
	 *            <tt>true</tt> for MainStock; <tt>false</tt> for PlayerStock
	 */
	public void switchStock(final boolean mainStock) {
		stock = mainStock ? 24 : 26;
	}
}
