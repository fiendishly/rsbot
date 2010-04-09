package org.rsbot.script;

import java.applet.Applet;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.rsbot.accessors.Client;
import org.rsbot.bot.Bot;
import org.rsbot.bot.input.CanvasWrapper;

public class InputManager {
	public static final Methods methods = new Methods();

	private static boolean isOnCanvas(final int x, final int y) {
		return (x > 0) && (x < CanvasWrapper.getGameWidth()) && (y > 0) && (y < CanvasWrapper.getGameHeight());
	}

	private byte dragLength = 0;
	private final MouseHandler mouseHandler = new MouseHandler(this);

	/**
	 * @deprecated since 2009/03/09.
	 */
	@Deprecated
	public int MouseSpeed = 3;

	boolean present = false;
	boolean pressed = false;

	private final java.util.Random random = new java.util.Random();

	public InputManager() {
	}

	public void clickMouse(final boolean left) {
		if (!present)
			return; // Cant click off the canvas
		pressMouse(getX(), getY(), left);
		sleepNoException(random(50, 100));
		releaseMouse(getX(), getY(), left);
	}

	/**
	 * Drag the mouse from the current position to a certain other position.
	 * 
	 * @param x
	 *            the x coordinate to drag to
	 * @param y
	 *            the y coordinate to drag to
	 */
	public void dragMouse(final int x, final int y) {
		pressMouse(getX(), getY(), true);
		sleepNoException(random(300, 500));
		windMouse(getX(), getY(), x, y);
		sleepNoException(random(300, 500));
		releaseMouse(x, y, true);
	}

	@SuppressWarnings("unused")
	private void gainFocus() {
		final CanvasWrapper cw = getCanvasWrapper();
		if (!cw.hasFocus) {
			cw.processEventReal(new FocusEvent(getTarget(), FocusEvent.FOCUS_GAINED));
		}
	}

	private CanvasWrapper getCanvasWrapper() {
		return (CanvasWrapper) getTarget().getComponent(0);
	}

	private Client getClient() {
		return Bot.getClient();
	}

	private char getKeyChar(final char c) {
		final int i = c;
		if ((i >= 36) && (i <= 40))
			return KeyEvent.VK_UNDEFINED;
		else
			return c;
	}

	/**
	 * @deprecated since 2009-03-09
	 */
	@Deprecated
	public double getRandomSpeed() {
		final int randomgen = random(0, 2);
		switch (randomgen) {
			case 0:
				return 1;
			case 1:
				return 1.1;
			case 3:
				return 1.2;
		}
		return 1;
	}

	private Applet getTarget() {
		return (Applet) getClient();
	}

	public int getX() {
		return getClient().getMouse().getMouseX();
	}

	public int getY() {
		return getClient().getMouse().getMouseY();
	}

	public void holdKey(final int keyCode, final int ms) {
		KeyEvent ke;
		ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, (char) keyCode);
		getClient().getKeyboard()._keyPressed(ke);

		if (ms > 500) {
			ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + 500, 0, keyCode, (char) keyCode);
			getClient().getKeyboard()._keyPressed(ke);
			final int ms2 = ms - 500;
			for (int i = 37; i < ms2; i += random(20, 40)) {
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + i + 500, 0, keyCode, (char) keyCode);
				getClient().getKeyboard()._keyPressed(ke);
			}
		}
		final int delay2 = ms + random(-30, 30);
		ke = new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, 0, keyCode, (char) keyCode);
		getClient().getKeyboard()._keyReleased(ke);
	}

	public void hopMouse(final int x, final int y) {
		moveMouse(x, y);
	}

	@SuppressWarnings("unused")
	private void loseFocus() {
		final CanvasWrapper cw = getCanvasWrapper();
		if (cw.hasFocus) {
			cw.processEventReal(new FocusEvent(getTarget(), FocusEvent.FOCUS_LOST));
		}
	}

	private void moveMouse(final int x, final int y) {
		// Firstly invoke drag events
		if (pressed) {
			final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, x, y, 0, false);
			getClient().getMouse().sendEvent(me);
			if ((dragLength & 0xFF) != 0xFF) {
				dragLength++;
			}
		}

		if (!present) {
			if (InputManager.isOnCanvas(x, y)) { // Entered
				final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, x, y, 0, false);
				present = true;
				getClient().getMouse().sendEvent(me);
			} else
				return;
		}
		if (!InputManager.isOnCanvas(x, y)) {
			final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, x, y, 0, false);
			present = false;
			getClient().getMouse().sendEvent(me);
			return;
		}
		if (!pressed) {
			final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false);
			getClient().getMouse().sendEvent(me);
		}

	}

	/**
	 * @see #moveMouse(int, int, int, int, int)
	 */
	public void moveMouse(final int x, final int y, final int randomX, final int randomY) {
		moveMouse(MouseHandler.DEFAULT_MOUSE_SPEED, x, y, randomX, randomY);
	}

	public void moveMouse(final int speed, final int x, final int y, final int randomX, final int randomY) {
		moveMouse(speed, x, y, randomX, randomY, InputManager.methods.getMousePath());
	}

	/**
	 * Moves the mouse to the specified point at a certain sped.
	 * 
	 * @param speed
	 *            the lower, the faster.
	 * @param x
	 *            the x value
	 * @param y
	 *            the y value
	 * @param randomX
	 *            x-axis randomness (gets added to x)
	 * @param randomY
	 *            y-axis randomness (gets added to y)
	 * @param MousePaths
	 *            Whether or not to use Mouse Path generator
	 */
	public void moveMouse(final int speed, final int x, final int y, final int randomX, final int randomY, final boolean MousePaths) {
		int thisX = getX(), thisY = getY();
		if (!InputManager.isOnCanvas(thisX, thisY)) {
			switch (random(1, 5)) { // on which side of canvas should it enter
				case 1:
					thisX = -1;
					thisY = random(0, CanvasWrapper.getGameHeight());
					break;
				case 2:
					thisX = random(0, CanvasWrapper.getGameWidth());
					thisY = CanvasWrapper.getGameHeight() + 1;
					break;
				case 3:
					thisX = CanvasWrapper.getGameWidth() + 1;
					thisY = random(0, CanvasWrapper.getGameHeight());
					break;
				case 4:
					thisX = random(0, CanvasWrapper.getGameWidth());
					thisY = -1;
					break;
			}
		}
		if (MousePaths) {
			final Point[] path = mouseHandler.generateMousePath((int) Math.hypot(thisX - x, thisX - y) / 100 + random(1, 3), new Point(thisX, thisY), new Point(x, y));
			if (path == null) {
				new Exception("Mouse paths were enabled, and the path was returned null. Please report on forums: ").printStackTrace();
			}
			windMouse(speed, thisX, thisY, path[0].x, path[0].y);
			for (int i = 1; i < path.length; i++) {
				try {
					if (i == path.length - 1) {
						windMouse(speed, path[i - 1].x, path[i - 1].y, random(path[i].x, path[i].x + randomX), random(path[i].y, path[i].y + randomY));
					} else {
						windMouse(speed, path[i - 1].x, path[i - 1].y, path[i].x, path[i].y);
					}

				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			windMouse(speed, thisX, thisY, random(x, x + randomX), random(y, y + randomY));
		}
	}

	public void pressKey(final char ch) {
		KeyEvent ke;
		ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, ch, getKeyChar(ch));
		getClient().getKeyboard()._keyPressed(ke);
	}

	private void pressMouse(final int x, final int y, final boolean left) {
		if (pressed || !present)
			return;
		final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
		pressed = true;
		getClient().getMouse().sendEvent(me);
	}

	public int random(final int min, final int max) {
		final int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}

	public void releaseKey(final char ch) {
		KeyEvent ke;
		ke = new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), InputEvent.ALT_DOWN_MASK, ch, getKeyChar(ch));
		getClient().getKeyboard()._keyReleased(ke);
	}

	private void releaseMouse(final int x, final int y, final boolean leftClick) {
		if (!pressed)
			return;
		MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false, leftClick ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
		pressed = false;
		getClient().getMouse().sendEvent(me);

		if ((dragLength & 0xFF) <= 3) {
			me = new MouseEvent(getTarget(), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false, leftClick ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
			getClient().getMouse().sendEvent(me);
		}
		// reset
		dragLength = 0;
	}

	public void sendKey(final char c) {
		sendKey(c, 0);
	}

	private void sendKey(final char ch, final int delay) {
		boolean shift = false;
		int code = ch;
		if ((ch >= 'a') && (ch <= 'z')) {
			code -= 32;
		} else if ((ch >= 'A') && (ch <= 'Z')) {
			shift = true;
		}
		KeyEvent ke;
		if ((code == KeyEvent.VK_LEFT) || (code == KeyEvent.VK_UP) || (code == KeyEvent.VK_UP) || (code == KeyEvent.VK_DOWN)) {
			ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, 0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
			getClient().getKeyboard()._keyPressed(ke);
			final int delay2 = random(50, 120) + random(0, 100);
			ke = new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, 0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
			getClient().getKeyboard()._keyReleased(ke);
		} else {
			if (!shift) {
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, 0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
				getClient().getKeyboard()._keyPressed(ke);
				// Event Typed
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + 0, 0, 0, ch, 0);
				getClient().getKeyboard()._keyTyped(ke);
				// Event Released
				final int delay2 = random(50, 120) + random(0, 100);
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, 0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
				getClient().getKeyboard()._keyReleased(ke);
			} else {
				// Event Pressed for shift key
				final int s1 = random(25, 60) + random(0, 50);
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + s1, InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_SHIFT, (char) KeyEvent.VK_UNDEFINED, KeyEvent.KEY_LOCATION_LEFT);
				getClient().getKeyboard()._keyPressed(ke);

				// Event Pressed for char to send
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, InputEvent.SHIFT_DOWN_MASK, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
				getClient().getKeyboard()._keyPressed(ke);
				// Event Typed for char to send
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + 0, InputEvent.SHIFT_DOWN_MASK, 0, ch, 0);
				getClient().getKeyboard()._keyTyped(ke);
				// Event Released for char to send
				final int delay2 = random(50, 120) + random(0, 100);
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, InputEvent.SHIFT_DOWN_MASK, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
				getClient().getKeyboard()._keyReleased(ke);

				// Event Released for shift key
				final int s2 = random(25, 60) + random(0, 50);
				ke = new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + s2, InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_SHIFT, (char) KeyEvent.VK_UNDEFINED, KeyEvent.KEY_LOCATION_LEFT);
				getClient().getKeyboard()._keyReleased(ke);
			}
		}
	}

	public void sendKeys(final String text, final boolean pressEnter) {
		sendKeys(text, pressEnter, 100, 200);
	}

	public void sendKeys(final String text, final boolean pressEnter, final int delay) {
		sendKeys(text, pressEnter, delay, delay);
	}

	public void sendKeys(final String text, final boolean pressEnter, final int minDelay, final int maxDelay) {
		final char[] chs = text.toCharArray();
		for (final char element : chs) {
			sendKey(element, random(minDelay, maxDelay));
			sleepNoException(random(minDelay, maxDelay));
		}
		if (pressEnter) {
			sendKey((char) KeyEvent.VK_ENTER, random(minDelay, maxDelay));
		}
	}

	public void sendKeysInstant(final String text, final boolean pressEnter) {
		for (final char c : text.toCharArray()) {
			sendKey(c, 0);
		}
		if (pressEnter) {
			sendKey((char) KeyEvent.VK_ENTER, 0);
		}
	}

	public void sleepNoException(final long t) {
		try {
			Thread.sleep(t);
		} catch (final Exception e) {
		}
	}

	/**
	 * @see #windMouse(int, int, int, int, int)
	 */
	public void windMouse(final int curX, final int curY, final int targetX, final int targetY) {
		windMouse(MouseHandler.DEFAULT_MOUSE_SPEED, curX, curY, targetX, targetY);
	}

	/**
	 * Moves the mouse from a certain point to another, with specified speed.
	 * 
	 * @param speed
	 *            the lower, the faster.
	 * @param curX
	 *            the x value to move from
	 * @param curY
	 *            the y value to move from
	 * @param targetX
	 *            the x value to move to
	 * @param targetY
	 *            the y value to move to
	 */
	public void windMouse(final int speed, final int curX, final int curY, final int targetX, final int targetY) {
		mouseHandler.moveMouse(speed, curX, curY, targetX, targetY, 0, 0);
	}
}
