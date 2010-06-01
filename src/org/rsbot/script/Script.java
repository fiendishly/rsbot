package org.rsbot.script;

import java.util.EventListener;
import java.util.Map;
import java.util.logging.Level;

import org.rsbot.bot.Bot;
import org.rsbot.script.randoms.antiban.BreakHandler;
import org.rsbot.script.randoms.antiban.LoginBot;

public abstract class Script extends Methods implements EventListener {

	public int ID = -1;
	public volatile boolean isActive = false;
	public volatile boolean isPaused = false;

	/**
	 * The main loop. Called if you return true from main. Called until you
	 * return a negative number. Avoid causing execution to pause using wait()
	 * within this method in favor of returning the number of milliseconds to
	 * wait. This ensures that pausing and anti-randoms perform normally.
	 *
	 * @return The number of milliseconds that the manager should wait before
	 *         calling it again. Returning a negative number will stop the
	 *         script.
	 */
	public abstract int loop();

	/**
	 * Perform any clean up such as unregistering any event listeners.
	 */
	@Override
	public void onFinish() {
	}

	/**
	 * The start method. Called before loop() is first called. If <tt>false</tt>
	 * is returned, the script will not start and loop() will never be called.
	 *
	 * @param map The arguments passed in from the description.
	 * @return <tt>true</tt> if the script should be started.
	 */
	public boolean onStart(final Map<String, String> map) {
		return true;
	}

	/**
	 * The start method. Return true if you where able to start up successfully
	 * with the given arguments.
	 *
	 * @param args A comma-separated list of arguments.
	 * @return <tt>true</tt> if the script should be started.
	 * @deprecated Use {@link #onStart(Map)} instead.
	 */
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
		} catch (final Throwable ex) {
			log.log(Level.SEVERE, "Error starting script: ", ex);
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
						} catch (final Exception ex) {
							log.log(Level.WARNING, "Uncaught exception from script: ", ex);
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

	private boolean checkForRandoms() {
		for (final Random random : Bot.getScriptHandler().getRandoms()) {
			if (random instanceof BreakHandler) {
				if (Bot.disableBreakHandler) {
					continue;
				}
			} else if (random instanceof LoginBot) {
				if (Bot.disableAutoLogin) {
					continue;
				}
			} else if (Bot.disableRandoms) {
				continue;
			}
			if (random.runRandom()) {
				return true;
			}
		}
		return false;
	}

}
