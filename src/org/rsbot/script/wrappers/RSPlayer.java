package org.rsbot.script.wrappers;

public class RSPlayer extends RSCharacter {
	/*
	 * private static RSPlayer ourPlayer;
	 * 
	 * public RSPlayer getOurPlayer() { if (ourPlayer == null) { ourPlayer = new
	 * RSPlayer(Bot.getClient().getMyPlayer()); } return ourPlayer; }
	 */

	org.rsbot.accessors.RSPlayer p;

	public RSPlayer(final org.rsbot.accessors.RSPlayer p) {
		super(p);
		this.p = p;
	}

	public int getCombatLevel() {
		return p.getLevel();
	}

	@Override
	public String getName() {
		return p.getName();
	}

	public int getTeam() {
		return p.getTeam();
	}

	public boolean isIdle() {
		return !isMoving() && (getAnimation() == -1) && !isInCombat();
	}

	@Override
	public String toString() {
		return "Player[" + getName() + "]" + super.toString();
	}
}
