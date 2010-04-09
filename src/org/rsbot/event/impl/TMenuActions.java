package org.rsbot.event.impl;

import java.awt.Graphics;
import java.util.List;

import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TMenuActions extends Methods implements TextPaintListener {
	public final static TMenuActions inst = new TMenuActions();

	private TMenuActions() {
	}

	public int drawLine(final Graphics render, int idx) {
		final List<String> items = getMenuItems();
		int i = 0;
		for (final String item : items) {
			Methods.drawLine(render, idx++, i++ + ": [red]" + item);
		}
		return idx;
	}
}
