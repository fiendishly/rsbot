package org.rsbot.script;

import java.util.logging.Level;

public abstract class Random extends Methods {
	
	private volatile boolean active = false;

	/**
	 * Detects whether or not this anti-random should
	 * activate.
	 * 
	 * @return <tt>true</tt> if the current script
	 * should be paused and control passed to this
	 * anti-random's loop.
	 */
	public abstract boolean activateCondition();

	public abstract int loop();

	/**
	 * Override to provide a time limit in seconds for
	 * this anti-random to complete.
	 * 
	 * @return The number of seconds after activateCondition
	 * returns <tt>true</tt> before the anti-random should be
	 * detected as having failed. If this time is reached
	 * the random and running script will be stopped.
	 */
	public int getTimeout() {
		return 0;
	}
	
	public final boolean isActive() {
		return active;
	}
	
	public final boolean runRandom() {
		if (!activateCondition()) {
			return false;
		}
		active = true;
		final String name = getClass().getAnnotation(ScriptManifest.class).name();
		log.info("Random event started: " + name);
		int timeout = getTimeout();
		if (timeout > 0) {
			timeout *= 1000;
			timeout += System.currentTimeMillis();
		}
		while (active) {
			try {
				final int wait = loop();
				if (wait == -1) {
					break;
				}
				if (timeout > 0 && System.currentTimeMillis() >= timeout) {
					log.warning("Time limit reached for " + name + ".");
					stopScript();
				}
				wait(wait);
			} catch (final Exception ex) {
				log.log(Level.WARNING, "", ex);
				break;
			}
		}
		active = false;
		onFinish();
		log.info("Random event finished: " + name);
		return true;
	}
}
