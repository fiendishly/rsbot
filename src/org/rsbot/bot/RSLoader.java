package org.rsbot.bot;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rsbot.accessors.Loader;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.Injector;

/**
 * Used in place of a loader JAR.
 * 
 * @author Qauters
 */
public class RSLoader extends Applet implements Runnable, Loader {

	private static final long serialVersionUID = 6288499508495040201L;
	
	private final Logger log = Logger.getLogger(RSLoader.class.getName());
	
	/*
	 * The client Applet.
	 */
	private Applet client;
	
	/*
	 * The Runnable to be called after the client has
	 * been instantiated.
	 */
	private Runnable loadedCallback;

	/*
	 * The injector used to modify client classes
	 * before they are loaded.
	 */
	private Injector injector = new Injector();

	/*
	 * The ClassLoader used to load client classes.
	 */
	private ClassLoader clientClassLoader;

	/**
	 * @param r The Runnable to be called after the
	 * client is instantiated in the run() method.
	 */
	public void setCallback(final Runnable r) {
		loadedCallback = r;
	}

	/**
	 * @return The client Applet.
	 */
	public Applet getClient() {
		return client;
	}
	
	Injector getInjector() {
		return injector;
	}

	@Override
	public final synchronized void init() {
		if (client != null) {
			client.init();
		}
	}

	@Override
	public final void paint(final Graphics graphics) {
		if (client != null) {
			client.paint(graphics);
		} else {
			Font font = new Font("Helvetica", 1, 13);
			FontMetrics fontMetrics = getFontMetrics(font);
			graphics.setColor(Color.black);
			graphics.fillRect(0, 0, 768, 503);
			graphics.setColor(Color.RED);
			graphics.drawRect(232, 232, 303, 33);
			String s = "Loading";
			graphics.setFont(new Font("Helvetica", 1, 13));
			graphics.setColor(Color.WHITE);
			graphics.drawString(s, (768 - fontMetrics.stringWidth(s)) / 2, 255);
		}
	}

	public void run() {
		try {
			clientClassLoader = new RSClassLoader(injector);
			
			/*
			 * Pre-#611 Loading: final Class<?> signLink =
			 * rSClassLoader.loadClass("SignLink"); final Constructor<?> sl =
			 * signLink.getConstructors()[0]; int i = 32; try { final String
			 * mode = getParameter("modewhat"); if (mode != null &&
			 * !mode.isEmpty()) { i += Integer.parseInt(mode); } } catch (final
			 * NumberFormatException e) { log.log(Level.WARNING, "", e); } final
			 * Object slInitialized = sl.newInstance(this, i,
			 * injector.generateTargetName(), 32);
			 */
			
			/*
			 * Since: #611
			 * This instance is provided as the loader applet.
			 * ClientDiskCache is not referenced here as it is
			 * reinitialized by the client in Signlink.
			 */

			final Class<?> c = clientClassLoader.loadClass("client");

			// Run client
			client = (Applet) c.newInstance();
			loadedCallback.run();
			c.getMethod("provideLoaderApplet",
					new Class[] { java.applet.Applet.class }).
					invoke(null, new Object[] { this });
			client.init();
			client.start();
		} catch (final Exception e) {
			log.log(Level.SEVERE,
						"Unable to load client, please check your firewall and internet connection");
			//e.printStackTrace();
			File versionFile = new File(GlobalConfiguration.Paths
					.getVersionCache());
			versionFile.delete();
		}
	}

	@Override
	public final synchronized void start() {
		if (client != null) {
			client.start();
		}
	}

	@Override
	public final synchronized void stop() {
		if (client != null) {
			client.stop();
		}
	}

	@Override
	public final void update(final Graphics graphics) {
		if (client != null) {
			client.update(graphics);
		}
	}

	@Override
	public final synchronized void destroy() {
		if (client != null) {
			client.destroy();
		}
	}
}
