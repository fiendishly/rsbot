package org.rsbot.script.wrappers;

import java.awt.Point;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;

public class RSCharacter {
	public final Methods methods = new Methods();
	protected org.rsbot.accessors.RSCharacter c;

	public RSCharacter(final org.rsbot.accessors.RSCharacter c) {
		this.c = c;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof org.rsbot.script.wrappers.RSCharacter) {
			final org.rsbot.script.wrappers.RSCharacter cha = (org.rsbot.script.wrappers.RSCharacter) obj;
			return cha.c == c;
		}
		return false;
	}

	public int getAnimation() {
		return c.getAnimation();
	}

	public int getHeight() {
		return c.getHeight();
	}

	/**
	 * @return The % of HP; 100 if not in combat.
	 */
	public int getHPPercent() {
		return isInCombat() ? c.getHPRatio() * 100 / 255 : 100;
	}

	public RSCharacter getInteracting() {
		final int interact = c.getInteracting();
		if (interact == -1) {
			return null;
		}
		if (interact < 32768) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSInterfaceNC(), interact);
			if(node == null || !(node instanceof RSNPCNode)) {
				return null;
			}
			return new org.rsbot.script.wrappers.RSNPC(((RSNPCNode) node).getRSNPC());
		} else {
			int index = interact - 32768;
			if (index == Bot.getClient().getSelfInteracting()) {
				index = 2047;
			}
			return new org.rsbot.script.wrappers.RSPlayer(Bot.getClient().getRSPlayerArray()[index]);
		}
	}

	public RSTile getLocation() {
		if (c == null)
			return new RSTile(-1, -1);
		final int x = Bot.getClient().getBaseX() + (c.getX() >> 7);
		final int y = Bot.getClient().getBaseY() + (c.getY() >> 7);
		return new RSTile(x, y);
	}

	public String getMessage() {
		return c.getMessage();
	}

	/**
	 * Get's the minimap location, of the character. Note: This does work when
	 * it's walking!
	 *
	 * @return The location of the character on the minimap.
	 */
	public Point getMinimapLocation() {
		final int cX = Bot.getClient().getBaseX() + (c.getX() / 32 - 2) / 4;
		final int cY = Bot.getClient().getBaseY() + (c.getY() / 32 - 2) / 4;
		return Calculations.worldToMinimap(cX, cY);
	}

	public String getName() {
		return "UNDEFINED"; // should be overridden, obviously
	}

	public int getLevel() {
		return -1; // should be overridden as well
	}

	public Point getScreenLocation() {
		// RSTile loc = getLocation();
		// return Calculations.tileToScreen(loc.getX(), loc.getY(),
		// 0.5, 0.9, (c.getHeight() / 2));
		return Calculations.worldToScreen(c.getX(), c.getY(), -c.getHeight() / 2);
	}

	/**
	 * Currently unhooked
	 *
	 * @return 0
	 */
	public int getTurnDirection() {
		return 0;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(c);
	}

	public boolean isInCombat() {
		return methods.isLoggedIn() && Bot.getClient().getLoopCycle() < c.getLoopCycleStatus();
	}

	public boolean isInteractingWithLocalPlayer() {
		return c.getInteracting() - 32768 == Bot.getClient().getSelfInteracting();
	}

	public boolean isMoving() {
		return c.isMoving() != 0;
	}

	public boolean isOnScreen() {
		return methods.tileOnScreen(getLocation());
	}

	public boolean isValid() {
		return c != null;
	}

	@Override
	public String toString() {
		final RSCharacter inter = getInteracting();
		return "[anim=" + getAnimation() + ",msg=" + getMessage() + ",interact=" + (inter == null ? "null" : inter.isValid() ? inter.getMessage() : "Invalid") + "]";
	}
}
