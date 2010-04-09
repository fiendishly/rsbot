package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TLoginIndex extends Methods implements TextPaintListener {
	public final static TLoginIndex inst = new TLoginIndex();

	private TLoginIndex() {
	}

	public int drawLine(final Graphics render, int idx) {
		Methods.drawLine(render, idx++, "Login Index: " + getLoginIndex());
		return idx;
	}
}
