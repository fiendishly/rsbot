package org.rsbot.script;

import java.util.EventListener;
import java.util.Map;
import java.util.logging.Level;

import org.rsbot.bot.Bot;
import org.rsbot.script.randoms.antiban.BreakHandler;

public abstract class Script extends Methods implements EventListener {
	public int ID = -1;
	public volatile boolean isActive = false;
	public volatile boolean isPaused = false;

	private boolean checkForRandoms() {
		if (Bot.disableRandoms)
			return false;
		for (final Random random : Bot.getScriptHandler().getRandoms()) {
			if (Bot.disableBreakHandler && (random instanceof BreakHandler)) {
				continue;
			}
			if (random.runRandom())
				return true;
		}
		return false;
	}

	/**
	 * By default returns a basic description with space to enter arguments.
	 * 
	 * @return A description of this script. HTML is permitted.
	 * @deprecated Use ScriptManifest instead.
	 * */
	@Deprecated
	public String getScriptDescription() {
		return "This script has no description.";
	}

	/**
	 * The main loop. Called if you return true from main. Called until you
	 * return a negative number.
	 * 
	 * @return The number of milliseconds that the manager should wait before
	 *         calling it again. Returning a negative number will stop the
	 *         script.
	 * */
	public abstract int loop();

	/**
	 * Perform any clean up such as unregistering any event listeners.
	 * */
	@Override
	public void onFinish() {
	}

	/**
	 * The start method. By default calls the old onStart method with the args.
	 * 
	 * @param map
	 *            The arguments passed in from the description.
	 * @return True if the script should be started.
	 * */
	public boolean onStart(final Map<String, String> map) {
		final String args = map.get("args");
		return args == null ? onStart(new String[0]) : onStart(args.split(","));
	}

	/**
	 * The start method. Return true if you where able to start up successfully
	 * with the given arguments.
	 * 
	 * @deprecated Use {@link #onStart(Map)} instead.
	 * */
	@Deprecated
	public boolean onStart(final String[] args) {
		return true;
	}

	public final void run(final Map<String, String> map) {
		Bot.getEventManager().registerListener(this);
		setupMenuListener();
		log.info("Script started.");
		boolean start = false;
		try {
			start = onStart(map);
		} catch (final ThreadDeath ignored) {
		} catch (final Throwable e) {
			log.log(Level.SEVERE, "Error starting script: ", e);
		}
		if (start) {
			isActive = true;
			try {
				while (isActive) {
					if (!isPaused) {
						if (checkForRandoms()) {
							continue;
						}
						int timeOut = -1;
						try {
							timeOut = loop();
						} catch (final ThreadDeath td) {
							break;
						} catch (final Throwable e) {
							log.log(Level.WARNING, "Uncaught exception from script: ", e);
						}
						if (timeOut == -1) {
							break;
						}
						try {
							wait(timeOut);
						} catch (final ThreadDeath td) {
							break;
						} catch (final RuntimeException e) {
							e.printStackTrace();
							break;
						}
					} else {
						try {
							wait(1000);
						} catch (final ThreadDeath td) {
							break;
						} catch (final RuntimeException e) {
							e.printStackTrace();
							break;
						}
					}
				}
				try {
					onFinish();
				} catch (final ThreadDeath ignored) {
				} catch (final RuntimeException e) {
					e.printStackTrace();
				}
			} catch (final ThreadDeath td) {
				onFinish();
			}
			isActive = false;
			log.info("Script stopped.");
		} else {
			log.severe("Failed to start up.");
		}
		Bot.getEventManager().removeListener(this);
		Bot.getScriptHandler().removeScript(ID);
	}
}
