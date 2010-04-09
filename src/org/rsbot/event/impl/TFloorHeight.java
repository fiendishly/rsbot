package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TFloorHeight extends Methods implements TextPaintListener {
	public final static TFloorHeight inst = new TFloorHeight();

	private TFloorHeight() {
	}

	public int drawLine(final Graphics render, int idx) {
		final int floor = getPlane();
		Methods.drawLine(render, idx++, "Floor " + floor);
		return idx;
	}

}
