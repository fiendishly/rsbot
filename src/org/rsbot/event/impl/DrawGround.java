package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

public class DrawGround implements PaintListener {
	public final static DrawGround inst = new DrawGround();

	private DrawGround() {
	}

	public void onRepaint(final Graphics render) {
		final Methods m = new Methods();
		if (!m.isLoggedIn())
			return;
		final RSPlayer player = m.getMyPlayer();
		if (player == null)
			return;
		render.setColor(Color.WHITE);
		final RSTile location = player.getLocation();
		for (int x = location.getX() - 25; x < location.getX() + 25; x++) {
			for (int y = location.getY() - 25; y < location.getY() + 25; y++) {
				final RSItemTile[] item = m.getGroundItemsAt(x, y);
				if ((item == null) || (item.length == 0)) {
					continue;
				}
				final Point screen = Calculations.tileToScreen(item[0]);
				if (!m.pointOnScreen(screen)) {
					continue;
				}
				render.drawString("" + item[0].getItem().getID(), location.getX() - 10, location.getY());
			}
		}
	}
}
