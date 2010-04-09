package org.rsbot.event.impl;

import java.util.logging.Logger;

import org.rsbot.event.events.CharacterMovedEvent;
import org.rsbot.event.listeners.CharacterMovedListener;

public class CharacterMovedLogger implements CharacterMovedListener {
	public final static CharacterMovedLogger inst = new CharacterMovedLogger();
	private final Logger log = Logger.getLogger(CharacterMovedLogger.class.getName());

	private CharacterMovedLogger() {
	}

	public void characterMoved(final CharacterMovedEvent e) {
		log.info("Character Moved: " + String.format("%2d %s", e.getDirection(), e.getCharacter().toString()));
	}
}
