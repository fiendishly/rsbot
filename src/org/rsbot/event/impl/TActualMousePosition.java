package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TActualMousePosition extends Methods implements TextPaintListener {
	public final static TActualMousePosition inst = new TActualMousePosition();

	private TActualMousePosition() {
	}

	public int drawLine(final Graphics render, int idx) {
		final Mouse mouse = Bot.getClient().getMouse();
		if (mouse != null) {
			Methods.drawLine(render, idx++, "Actual Mouse Position: " + mouse.x + "," + mouse.y);
		}
		return idx;
	}
}
