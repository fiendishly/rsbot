package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

public class DrawItems extends Methods implements PaintListener {
	public final static DrawItems inst = new DrawItems();

	private DrawItems() {
	}

	public void onRepaint(final Graphics render) {
		if (!isLoggedIn())
			return;
		final RSPlayer player = getMyPlayer();
		if (player == null)
			return;
		final FontMetrics metrics = render.getFontMetrics();
		final RSTile location = player.getLocation();
		final int locX = location.getX();
		final int locY = location.getY();
		final int tHeight = metrics.getHeight();
		for (int x = locX - 25; x < locX + 25; x++) {
			for (int y = locY - 25; y < locY + 25; y++) {
				final Point screen = Calculations.tileToScreen(new RSTile(x, y));
				if (!pointOnScreen(screen)) {
					continue;
				}
				final RSItemTile[] items = getGroundItemsAt(x, y);
				for (int i = 0; i < items.length; i++) {
					render.setColor(Color.RED);
					render.fillRect((int) screen.getX() - 1, (int) screen.getY() - 1, 2, 2);
					final String s = "" + items[i].getItem().getID();
					final int ty = screen.y - tHeight * (i + 1) + tHeight / 2;
					final int tx = screen.x - metrics.stringWidth(s) / 2;
					render.setColor(Color.green);
					render.drawString(s, tx, ty);
				}
			}
		}
	}
}
