package org.rsbot.script.wrappers;

import java.awt.Point;

import org.rsbot.script.Calculations;

public class RSTile {
	private final int x;
	private final int y;

	public RSTile(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof RSTile) {
			final RSTile tile = (RSTile) obj;
			return (tile.x == x) && (tile.y == y);
		}
		return false;
	}

	public Point getScreenLocation() {
		return Calculations.tileToScreen(this, 0);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	public boolean isValid() {
		return (x != -1) && (y != -1);
	}

	/**
	 * Randomize the tile.
	 * 
	 * @param maxXDeviation
	 *            Max X distance from tile x.
	 * @param maxYDeviation
	 *            Max Y distance from tile y.
	 * @return The randomized tile
	 */
	public RSTile randomizeTile(final int maxXDeviation, final int maxYDeviation) {
		int x = getX();
		int y = getY();
		if (maxXDeviation > 0) {
			double d = Math.random() * 2 - 1.0;
			d *= maxXDeviation;
			x += (int) d;
		}
		if (maxYDeviation > 0) {
			double d = Math.random() * 2 - 1.0;
			d *= maxYDeviation;
			y += (int) d;
		}
		return new RSTile(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
