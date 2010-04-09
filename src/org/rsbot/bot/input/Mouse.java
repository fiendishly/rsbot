package org.rsbot.bot.input;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class Mouse extends Focus implements MouseListener, MouseMotionListener {
	public int clientX;
	public int clientY;
	public boolean present;
	public boolean pressed;
	public int x;
	public int y;

	private int mousePressX = -1;
	private int mousePressY = -1;
	private long mousePressTime = -1;

	public abstract void _mouseClicked(MouseEvent e);

	public abstract void _mouseDragged(MouseEvent e);

	public abstract void _mouseEntered(MouseEvent e);

	public abstract void _mouseExited(MouseEvent e);

	public abstract void _mouseMoved(MouseEvent e);

	public abstract void _mousePressed(MouseEvent e);

	public abstract void _mouseReleased(MouseEvent e);

	public abstract Component getComponent();

	public abstract org.rsbot.accessors.NodeList getMouseDataList();

	public long getMousePressTime() {
		return mousePressTime;
	}

	public int getMousePressX() {
		return mousePressX;
	}

	public int getMousePressY() {
		return mousePressY;
	}

	public abstract int getMouseX();

	public abstract int getMouseY();

	public abstract boolean isLoggingMouseMovements();

	public final void mouseClicked(final MouseEvent e) {
		// System.out.println(("MC");
		x = e.getX();
		y = e.getY();

		if (!Listener.blocked) {
			clientX = x;
			clientY = y;
			_mouseClicked(e);
		}
		e.consume();
	}

	public final void mouseDragged(final MouseEvent e) {
		// System.out.println(("MD");
		x = e.getX();
		y = e.getY();

		if (!Listener.blocked) {
			clientX = x;
			clientY = y;
			_mouseDragged(e);
		}
		e.consume();
	}

	public final void mouseEntered(final MouseEvent e) {
		// System.out.println(("MEnt");
		present = true;

		x = e.getX();
		y = e.getY();

		if (!Listener.blocked) {
			clientX = x;
			clientY = y;
			_mouseEntered(e);
		}
		e.consume();
	}

	public final void mouseExited(final MouseEvent e) {
		// System.out.println(("MExt");
		present = false;

		x = e.getX();
		y = e.getY();

		if (!Listener.blocked) {
			clientX = x;
			clientY = y;
			_mouseExited(e);
		}
		e.consume();
	}

	public final void mouseMoved(final MouseEvent e) {
		// System.out.println(("MM");
		x = e.getX();
		y = e.getY();

		if (!Listener.blocked) {
			clientX = x;
			clientY = y;
			_mouseMoved(e);
		}
		e.consume();
	}

	public final void mousePressed(final MouseEvent e) {
		// System.out.println(("MP");
		pressed = true;

		x = e.getX();
		y = e.getY();

		if (!Listener.blocked) {
			clientX = x;
			clientY = y;
			_mousePressed(e);
		}
		e.consume();
	}

	public final void mouseReleased(final MouseEvent e) {
		// System.out.println(("MR");
		pressed = false;

		x = e.getX();
		y = e.getY();

		if (!Listener.blocked) {
			clientX = x;
			clientY = y;

			mousePressX = x;
			mousePressY = y;
			mousePressTime = System.currentTimeMillis();

			_mouseReleased(e);
		}
		e.consume();
	}

	public final void sendEvent(final MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_CLICKED) {
			_mouseClicked(e);
		} else if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
			_mouseDragged(e);
		} else if (e.getID() == MouseEvent.MOUSE_ENTERED) {
			_mouseEntered(e);
		} else if (e.getID() == MouseEvent.MOUSE_EXITED) {
			_mouseExited(e);
		} else if (e.getID() == MouseEvent.MOUSE_MOVED) {
			_mouseMoved(e);
		} else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			_mousePressed(e);
		} else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
			mousePressX = e.getX();
			mousePressY = e.getY();
			mousePressTime = System.currentTimeMillis();

			_mouseReleased(e);
		} else
			throw new InternalError(e.toString());
	}
}
