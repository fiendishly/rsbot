package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSTile;

public class TPlayerPosition extends Methods implements TextPaintListener {
	public final static TPlayerPosition inst = new TPlayerPosition();

	private TPlayerPosition() {
	}

	public int drawLine(final Graphics render, int idx) {
		final RSTile position = getMyPlayer().getLocation();
		Methods.drawLine(render, idx++, "Position " + position);
		return idx;
	}
}
