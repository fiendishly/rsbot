package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

public class DrawObjects extends Methods implements PaintListener {
	public final static DrawObjects inst = new DrawObjects();

	private DrawObjects() {
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
				final Point screen = Calculations.tileToScreen(x, y, 0);
				if (!pointOnScreen(screen)) {
					continue;
				}
				final RSObject object = getObjectAt(x, y);
				if ((object != null) && (object.getID() != 0)) {
					render.setColor(Color.RED);
					render.fillRect((int) screen.getX() - 1, (int) screen.getY() - 1, 2, 2);
					final String s = "" + object.getID();
					final int ty = screen.y - tHeight / 2;
					final int tx = screen.x - metrics.stringWidth(s) / 2;
					render.setColor(Color.WHITE);
					render.drawString(s, tx, ty);
				}
			}
		}
	}
}
