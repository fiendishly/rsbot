package org.rsbot.script;

import java.util.ArrayList;

public class WalkerNode {
	ArrayList<WalkerNode> neighbours = new ArrayList<WalkerNode>();
	WalkerNode previous;
	int x, y, distance;

	public WalkerNode(final int x, final int y) {
		this.x = x;
		this.y = y;
		previous = null;
	}

	public int distance(final WalkerNode neighbour) {
		final int dx = x - neighbour.x;
		final int dy = y - neighbour.y;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}
}
