package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TMousePosition extends Methods implements TextPaintListener {
	public final static TMousePosition inst = new TMousePosition();

	private TMousePosition() {
	}

	public int drawLine(final Graphics render, int idx) {
		final Mouse mouse = Bot.getClient().getMouse();
		if (mouse != null) {
			final int mouse_x = mouse.getMouseX();
			final int mouse_y = mouse.getMouseY();
			Methods.drawLine(render, idx++, "Mouse Position " + mouse_x + "," + mouse_y);
		}

		return idx;
	}
}
