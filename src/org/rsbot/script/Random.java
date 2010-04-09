package org.rsbot.script;

import java.util.logging.Level;

public abstract class Random extends Methods {
	public boolean isActive = false;
	public boolean isUsed = true;

	public abstract boolean activateCondition();

	public abstract int loop();

	@Override
	public void onFinish() {

	}

	public final boolean runRandom() {
		if (!isUsed)
			return false;
		try {
			if (!activateCondition())
				return false;
		} catch (final ThreadDeath td) {
			throw td;
		} catch (final Throwable e) {
			log.log(Level.WARNING, "", e);
			return false;
		}
		isActive = true;
		final String name = getClass().getAnnotation(ScriptManifest.class).name();
		log.info("Random event started: " + name);
		while (isActive) {
			try {
				final int timeOut = loop();
				if (timeOut == -1) {
					break;
				}
				wait(timeOut);
			} catch (final ThreadDeath td) {
				isActive = false;
				throw td;
			} catch (final Throwable e) {
				log.log(Level.WARNING, "", e);
				break;
			}
		}
		onFinish();
		log.info("Random event finished: " + name);
		return true;
	}
}
