package org.rsbot.bot.input;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.rsbot.bot.Bot;
import org.rsbot.event.EventMulticaster;
import org.rsbot.event.events.PaintUpdateEvent;
import org.rsbot.event.events.TextPaintEvent;

public class CanvasWrapper extends Canvas {

	private final Logger log = Logger.getLogger(CanvasWrapper.class.getName());
	private static final long serialVersionUID = -2276037172265300477L;
	public static boolean slowGraphics = false;
	private static BufferedImage botBuffer;
	private static int gameWidth = 765;
	private static int gameHeight = 503;
	private boolean didGraphicsCheck = false;

	// private InputManager inputManager;
	private EventMulticaster eventMulticaster;
	private BufferedImage gameBuffer;
	public boolean hasFocus = false;
	private PaintUpdateEvent paintEvent;
	private TextPaintEvent textPaintEvent;

	public CanvasWrapper() {
		super();
		setup();
	}

	public CanvasWrapper(final GraphicsConfiguration config) {
		super(config);
		setup();
	}

	public static BufferedImage getBotBuffer() {
		return CanvasWrapper.botBuffer;
	}

	public static int getGameWidth() {
		return CanvasWrapper.gameWidth;
	}

	public static int getGameHeight() {
		return CanvasWrapper.gameHeight;
	}

	@Override
	public final Graphics getGraphics() {
		if(!this.didGraphicsCheck)
		{
			if(Bot.getClient().getDetailInfo() != null && Bot.getClient().getDetailInfo().getDetailLevel() != 0)
			{
				JOptionPane.showMessageDialog(this, 
						new String[] { 
							"We detected that your graphics detail is not set to 'Safe Mode'.",
							"Please go to 'Graphics Options' and select 'Safe Mode'.",
						}, "Graphics Options", JOptionPane.WARNING_MESSAGE);
				
				this.didGraphicsCheck = true;
			}
		}
		try {
			if (CanvasWrapper.slowGraphics) {
				Thread.sleep(70);
			}
		} catch (final InterruptedException e) {
			log.log(Level.SEVERE, "", e);
		}
		final Graphics render = CanvasWrapper.botBuffer.getGraphics();
		render.drawImage(gameBuffer, 0, 0, null);

		try {
			if (eventMulticaster.isEnabled(EventMulticaster.PAINT_EVENT)) {
				paintEvent.graphics = render;
				eventMulticaster.fireEvent(paintEvent);
			}
			if (eventMulticaster.isEnabled(EventMulticaster.TEXT_PAINT_EVENT)) {
				textPaintEvent.graphics = render;
				textPaintEvent.idx = 0;
				eventMulticaster.fireEvent(textPaintEvent);
			}
		} catch (final Throwable e) {
			log.log(Level.WARNING, "", e);
		}
		render.dispose();
		final Graphics g = super.getGraphics();
		try {
			g.drawImage(CanvasWrapper.botBuffer, 0, 0, null);
		} catch (final NullPointerException e) {
		}
		final Graphics g2 = gameBuffer.getGraphics();
		if ((getWidth() != CanvasWrapper.gameWidth) || (getHeight() != CanvasWrapper.gameHeight)) {
			createBufferedImages(getWidth(), getHeight());
		}
		return g2;
	}

	@Override
	protected final void processEvent(final AWTEvent e) {
		if ((e.getID() == MouseEvent.MOUSE_PRESSED) && !hasFocus()) {
			requestFocus();
		}
		if (e instanceof KeyEvent) {
			processEventReal(e);
		} else if (e instanceof MouseEvent) {// TODO move the filtering here
			processEventReal(e);
		} else if (e instanceof FocusEvent) {
			if (!Listener.blocked) { // Block redundant events
				if (e.getID() == FocusEvent.FOCUS_GAINED) {
					if (!hasFocus) {
						processEventReal(e);
					}
				} else if (e.getID() == FocusEvent.FOCUS_LOST) {
					if (hasFocus) {
						processEventReal(e);
					}
				}
			}
		} else {
			log.warning("Unknown event: " + e);
			processEventReal(e);
		}
	}

	public final void processEventReal(final AWTEvent e) {
		// System.out.println("REAL: " + e);
		if (e.getID() == FocusEvent.FOCUS_GAINED) {
			hasFocus = true;
		} else if (e.getID() == FocusEvent.FOCUS_LOST) {
			hasFocus = false;
		}
		super.processEvent(e);
	}

	private void createBufferedImages(final int width, final int height) {
		CanvasWrapper.botBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		gameBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		CanvasWrapper.gameWidth = width;
		CanvasWrapper.gameHeight = height;
	}

	private void setup() {
		try {
			/*
			 * Class<?> c = Class.forName("client", false,
			 * getClass().getClassLoader()); Callback callback = (Callback)
			 * c.getField("callback").get(null); Bot bot = callback.getBot();
			 * inputManager = bot.getInputManager();
			 * inputManager.setTarget(this); eventMulticaster =
			 * bot.getEventManager().getMulticaster();
			 */
			// inputManager = Bot.getInputManager();
			// inputManager.setTarget(this);
			createBufferedImages(CanvasWrapper.gameWidth, CanvasWrapper.gameHeight);
			eventMulticaster = Bot.getEventManager().getMulticaster();
			textPaintEvent = new TextPaintEvent();
			paintEvent = new PaintUpdateEvent();
		} catch (final Throwable e) {
			log.log(Level.SEVERE, "", e);
		}
	}
}
