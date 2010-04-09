package org.rsbot.script.wrappers;

import java.awt.Point;
import java.util.LinkedList;

/**
 * Creates a RSArea.
 * 
 * @author SuF
 * @version 1.0
 * 
 */
public class RSArea {

	private RSTile[][] tiles;

	private RSTile[] weightedTiles;
	private final int X, Y, Width, Height;

	/**
	 * Creates a new RSArea based on the x and y coordinates of the southwest
	 * corner, the width and the height of the rectangle.
	 * 
	 * @param x
	 *            The x coordinate of the southwest corner of the rectangle.
	 * @param y
	 *            The y coordinate of the southwest corner of the rectangle.
	 * @param width
	 *            The width of the rectangle.
	 * @param height
	 *            The height of the rectangle.
	 */
	public RSArea(final int x, final int y, final int width, final int height) {
		X = x;
		Y = y;
		Width = width;
		Height = height;
	}

	/**
	 * Creates a new RSArea based on the corners of the Rectangle.
	 * 
	 * @param sw
	 *            The south-west corner tile of the rectangle.
	 * @param ne
	 *            The north-east corner tile of the rectangle.
	 */
	public RSArea(final RSTile sw, final RSTile ne) {
		this(sw.getX(), sw.getY(), ne.getX() - sw.getX(), ne.getY() - sw.getY());
	}

	/**
	 * Determines, whether a given x and y is located in the area
	 * 
	 * @param x
	 *            the x to check for
	 * @param y
	 *            the y to check for
	 * @return true, if it's located in the area
	 */
	public boolean contains(final int x, final int y) {
		return (x >= X) && (x <= X + Width) && (y >= Y) && (y <= Y + Height);
	}

	/**
	 * Determines, whether a given point is located in the area
	 * 
	 * @param point
	 *            the point to check for
	 * @return true if it's located in the area
	 */
	public boolean contains(final Point point) {
		return contains(point.x, point.y);
	}

	/**
	 * Determines, whether a given RSTile is located in the area
	 * 
	 * @param tile
	 *            The RSTile to check for
	 * @return true if it's located in the area
	 */
	public boolean contains(final RSTile tile) {
		return contains(tile.getX(), tile.getY());
	}

	private RSTile[][] createTileRectagle(final int x, final int y, final int width, final int height) {
		final RSTile[][] tiles = new RSTile[width][height];
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				tiles[i][j] = new RSTile(x + i, y + j);
			}
		}
		return tiles;
	}

	/**
	 * 
	 * @param x
	 *            An array of integers that corresponds to the x coordinates.
	 *            The numbers in the array represent how many times the specific
	 *            tile should be placed in the new array.
	 * @param y
	 *            An array of integers that corresponds to the y coordinates.
	 *            The numbers in the array represent how many times the specific
	 *            tile should be placed in the new array.
	 */
	public void createWeightedTileArray(final int[] x, final int[] y) {
		final LinkedList<RSTile> f = new LinkedList<RSTile>();

		final RSTile[][] tiles = getTiles();

		for (int a = 0; a < tiles.length; a++) {
			for (int b = 0; b < tiles[a].length; b++) {
				for (int c = 0; c < y[a]; c++) {
					for (int d = 0; d < x[b]; d++) {
						f.add(tiles[a][b]);
					}
				}
			}
		}

		final RSTile[] e = new RSTile[f.size()];
		for (int a = 0; a < f.size(); a++) {
			e[a] = f.get(a);
		}
		weightedTiles = e;
	}

	/**
	 * @return Returns null if createWeightedTileArray has not been called.
	 *         Returns a random tile if it has.
	 */
	public RSTile getRandomTile() {
		final int minX = X;
		final int minY = Y;
		final int maxX = X + Width;
		final int maxY = Y + Height;

		final int possibleX = maxX - minX;
		final int possibleY = maxY - minY;
		if (possibleY == 0) {
			final int[] tileX = new int[possibleX];
			for (int i = 0; i < tileX.length; i++) {
				tileX[i] = minX + i;
			}
			final int x = (int) (Math.random() * possibleX + 0);
			return new RSTile(tileX[x], minY);

		}
		if (possibleX == 0) {
			final int[] tileY = new int[possibleY];
			for (int i = 0; i < tileY.length; i++) {
				tileY[i] = minY + i;
			}
			final int y = (int) (Math.random() * possibleY + 0);
			return new RSTile(minX, tileY[y]);

		}
		final int[] tileY = new int[possibleY];
		final int[] tileX = new int[possibleX];
		for (int i = 0; i < tileY.length; i++) {
			tileY[i] = minY + i;
		}
		for (int i = 0; i < tileX.length; i++) {
			tileX[i] = minX + i;
		}
		final int x = (int) (Math.random() * possibleX + 0);
		final int y = (int) (Math.random() * possibleY + 0);
		return new RSTile(tileX[x], tileY[y]);
	}

	/**
	 * @return Returns a 2d array of the tiles in the rectangle.
	 */
	public RSTile[][] getTiles() {
		if (tiles == null) {
			tiles = createTileRectagle(X, Y, Width, Height);
		}

		return tiles;
	}

	/**
	 * @return Returns the array of weighted tiles.
	 */
	public RSTile[] getWeightedTiles() {
		return weightedTiles;
	}

}
