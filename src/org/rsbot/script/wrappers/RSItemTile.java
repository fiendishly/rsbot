package org.rsbot.script.wrappers;

public class RSItemTile extends RSTile {
	RSItem groundItem;

	public RSItemTile(final int x, final int y, final RSItem groundItem) {
		super(x, y);
		this.groundItem = groundItem;
	}

	public RSItem getItem() {
		return groundItem;
	}

}
