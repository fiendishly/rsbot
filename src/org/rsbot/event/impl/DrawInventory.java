package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Methods;

public class DrawInventory implements PaintListener {
	public final static DrawInventory inst = new DrawInventory();

	private DrawInventory() {
	}

	public void onRepaint(final Graphics render) {
		final Methods m = new Methods();
		if (!m.isLoggedIn())
			return;

		/*
		 * Color[] cs = new
		 * Color[]{Color.red,Color.green,Color.blue,Color.black,Color.cyan};
		 * Client c = Bot.getClient(); Interface[][] iss =
		 * c.getInterfaceCache(); boolean[] valid = c.getValidInterfaceArray();
		 * int idx = 0; for (int j = 0; j < iss.length; j++) { Interface[] is =
		 * iss[j]; if (is == null || !valid[j]) continue;
		 * render.setColor(cs[idx%cs.length]); for (Interface i : is) { if (i ==
		 * null) continue; render.drawRect(i.getMasterX() + i.getX(),
		 * i.getMasterY() + i.getY(), i.getWidth(), i.getHeight()); } idx++; }
		 */

		if (m.getCurrentTab() != Constants.TAB_INVENTORY)
			return;

		render.setColor(Color.WHITE);
		final int[] inventory = m.getInventoryArray();
		for (int off = 0; off < inventory.length; off++) {
			if (inventory[off] != -1) {
				final Point location = m.getInventoryItemPoint(off);
				render.drawString("" + inventory[off], location.x, location.y);
			}
		}
	}
}
