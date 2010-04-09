package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSPlayer;

public class DrawPlayers extends Methods implements PaintListener {
	public final static DrawPlayers inst = new DrawPlayers();

	private DrawPlayers() {
	}

	public void onRepaint(final Graphics render) {
		if (!isLoggedIn())
			return;
		final org.rsbot.accessors.RSPlayer[] players = Bot.getClient().getRSPlayerArray();
		if (players == null)
			return;
		final FontMetrics metrics = render.getFontMetrics();
		for (final org.rsbot.accessors.RSPlayer element : players) {
			if (element == null) {
				continue;
			}
			final RSPlayer npc = new RSPlayer(element);
			final Point location = npc.getScreenLocation();
			if (!pointOnScreen(location)) {
				continue;
			}
			render.setColor(Color.RED);
			render.fillRect((int) location.getX() - 1, (int) location.getY() - 1, 2, 2);
			String s = "" + npc.getName() + " (" + npc.getCombatLevel() + ")";
			render.setColor(npc.isInCombat() ? Color.red : npc.isMoving() ? Color.green : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() / 2);

			if (npc.getAnimation() != -1) {
				s = "(" + npc.getAnimation() + ")";
				render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() * 3 / 2);
			}
		}
	}
}
