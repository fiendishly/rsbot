package org.rsbot.script.wrappers;

import org.rsbot.script.Calculations;

/**
 * Inventory/Bank/Shop item.
 * */
public class RSItem {
	int id;
	int stack;

	public RSItem(final int id, final int stack) {
		this.id = id;
		this.stack = stack;
	}

	public RSItem(final org.rsbot.accessors.RSItem item) {
		id = item.getID();
		stack = item.getStackSize();
	}

	public RSItem(final RSInterfaceComponent item) {
		id = item.getComponentID();
		stack = item.getComponentStackSize();
	}

	public RSItemDef getDefinition() {
		try {
			return new RSItemDef((org.rsbot.accessors.RSItemDef) Calculations.findNodeByID(id));
		} catch (final ClassCastException e) {
			return null;
		}
	}

	public int getID() {
		return id;
	}

	public int getStackSize() {
		return stack;
	}

	public boolean hasDefinition() {
		return getDefinition() != null;
	}

}
