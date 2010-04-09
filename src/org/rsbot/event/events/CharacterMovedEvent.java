package org.rsbot.event.events;

import java.util.EventListener;

import org.rsbot.event.EventMulticaster;
import org.rsbot.event.listeners.CharacterMovedListener;

public class CharacterMovedEvent extends RSEvent {
	private static final long serialVersionUID = 8883312847545757405L;
	private final org.rsbot.accessors.RSCharacter character;
	private final int direction;

	private org.rsbot.script.wrappers.RSCharacter wrapped;

	public CharacterMovedEvent(final org.rsbot.accessors.RSCharacter character, final int direction) {
		this.character = character;
		this.direction = direction;
	}

	@Override
	public void dispatch(final EventListener el) {
		((CharacterMovedListener) el).characterMoved(this);
	}

	public org.rsbot.script.wrappers.RSCharacter getCharacter() {
		if (wrapped == null) {
			if (character instanceof org.rsbot.accessors.RSNPC) {
				final org.rsbot.accessors.RSNPC npc = (org.rsbot.accessors.RSNPC) character;
				wrapped = new org.rsbot.script.wrappers.RSNPC(npc);
			} else if (character instanceof org.rsbot.accessors.RSPlayer) {
				final org.rsbot.accessors.RSPlayer player = (org.rsbot.accessors.RSPlayer) character;
				wrapped = new org.rsbot.script.wrappers.RSPlayer(player);
			} else {
				wrapped = new org.rsbot.script.wrappers.RSCharacter(character);
			}
		}
		return wrapped;
	}

	/**
	 * <alowaniak> 0 = N-W <alowaniak> 1 = north <alowaniak> 2 = N-E <alowaniak>
	 * 3 = west <alowaniak> 4 = east <alowaniak> 5 = S-W <alowaniak> 6 = south
	 * <alowaniak> 7 = S-E
	 * */
	public int getDirection() {
		return direction;
	}

	@Override
	public long getMask() {
		return EventMulticaster.CHARACTER_MOVED_EVENT;
	}
}
