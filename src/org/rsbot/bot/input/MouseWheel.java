package org.rsbot.bot.input;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public abstract class MouseWheel extends Listener implements MouseWheelListener {
	public abstract void _mouseWheelMoved(MouseWheelEvent e);

	public void mouseWheelMoved(final MouseWheelEvent e) {
		// System.out.println(("WHL");
		if (!Listener.blocked) {
			_mouseWheelMoved(e);
		}
		e.consume();
	}
}
