package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TTab extends Methods implements TextPaintListener {
	public final static TTab inst = new TTab();

	private TTab() {
	}

	public int drawLine(final Graphics render, int idx) {
		final int cTab = getCurrentTab();
		Methods.drawLine(render, idx++, "Current Tab: " + cTab + (cTab != -1 ? " (" + TAB_NAMES[cTab] + ")" : ""));
		return idx;
	}

}
