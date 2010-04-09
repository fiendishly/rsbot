package org.rsbot.bot;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.rsbot.accessors.Callback;
import org.rsbot.accessors.Render;
import org.rsbot.accessors.RenderData;
import org.rsbot.event.events.CharacterMovedEvent;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.script.Calculations;

public class CallbackImpl implements Callback {
	private final Bot bot;
	private final Logger log = Logger.getLogger(CallbackImpl.class.getName());

	public CallbackImpl(final Bot bot) {
		this.bot = bot;
	}

	public Bot getBot() {
		return bot;
	}

	public void notifyServerMessage(final String s) {
		try {
			final ServerMessageEvent e = new ServerMessageEvent(s);
			Bot.getEventManager().addToQueue(e);
		} catch (final Throwable e) { // protect rs
			log.log(Level.SEVERE, "", e);
		}
	}

	public void rsCharacterMoved(final org.rsbot.accessors.RSCharacter c, final int i) {
		try {
			final CharacterMovedEvent e = new CharacterMovedEvent(c, i);
			Bot.getEventManager().addToQueue(e);
		} catch (final Throwable e) { // protect rs
			log.log(Level.SEVERE, "", e);
		}
	}

	public void updateRenderInfo(final Render r, final RenderData rd) {
		Calculations.updateRenderInfo(r, rd);
	}
}
