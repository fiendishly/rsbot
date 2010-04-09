package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

//illusion
public class TAnimation extends Methods implements TextPaintListener {
	public final static TAnimation inst = new TAnimation();

	private TAnimation() {
	}

	public int drawLine(final Graphics render, int idx) {
		int animation;
		if (isLoggedIn()) {
			animation = getMyPlayer().getAnimation();
		} else {
			animation = -1;
		}
		Methods.drawLine(render, idx++, "Animation " + animation);
		return idx;
	}
}
