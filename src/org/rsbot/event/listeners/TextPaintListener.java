package org.rsbot.event.listeners;

import java.awt.Graphics;
import java.util.EventListener;

public interface TextPaintListener extends EventListener {
	/**
	 * Argument is the line number to start drawing from.
	 * 
	 * Returns the line number + number of lines actually drawn.
	 * */
	public int drawLine(Graphics render, int idx);
}
