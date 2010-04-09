package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.input.Listener;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TUserInputAllowed extends Methods implements TextPaintListener {
	public final static TUserInputAllowed inst = new TUserInputAllowed();

	private TUserInputAllowed() {
	}

	public int drawLine(final Graphics render, int idx) {
		Methods.drawLine(render, idx++, "User Input: " + (Listener.blocked ? "[red]Disabled" : "[green]Enabled"));
		return idx;
	}
}
