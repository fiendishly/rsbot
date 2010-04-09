package org.rsbot.script.wrappers;

public class RSItemDef {
	org.rsbot.accessors.RSItemDef id;

	public RSItemDef(final org.rsbot.accessors.RSItemDef id) {
		this.id = id;
	}

	public String[] getActions() {
		return id.getActions();
	}

	public String[] getGroundActions() {
		return id.getGroundActions();
	}

	public String getName() {
		return id.getName();
	}

	public int getTeam() {
		// return id.getTeam();
		return -1;
	}

	public boolean isMembers() {
		return id.isMembersObject();
	}

}
