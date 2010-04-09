package org.rsbot.script;

import java.awt.Point;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSItemDefFactory;
import org.rsbot.accessors.TileData;
import org.rsbot.bot.Bot;
import org.rsbot.bot.input.CanvasWrapper;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSTile;

public class Calculations {
	static class Render {
		static float absoluteX1 = 0, absoluteX2 = 0;
		static float absoluteY1 = 0, absoluteY2 = 0;
		static int xMultiplier = 512, yMultiplier = 512;
		static int zNear = 50, zFar = 3500;
	}

	static class RenderData {
		static float xOff = 0, xX = 32768, xY = 0, xZ = 0;
		static float yOff = 0, yX = 0, yY = 32768, yZ = 0;
		static float zOff = 0, zX = 0, zY = 0, zZ = 32768;
	}

	private static final int[] CURVESIN = new int[16384];

	private static final int[] CURVECOS = new int[16384];
	static {
		final double d = 0.00038349519697141029D;
		for (int i = 0; i < 16384; i++) {
			Calculations.CURVESIN[i] = (int) (32768D * Math.sin(i * d));
			Calculations.CURVECOS[i] = (int) (32768D * Math.cos(i * d));
		}
	}

	public static boolean canReach(final int startX, final int startY, final int destX, final int destY, final boolean isObject) {
		// Documentation part:
		// The blocks info
		// When you can walk freely it's 0, also used to create a noclip
		final int[][] via = new int[104][104];
		final int[][] cost = new int[104][104];
		final int[] tileQueueX = new int[4000];
		final int[] tileQueueY = new int[4000];

		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				via[xx][yy] = 0;
				cost[xx][yy] = 99999999;
			}
		}

		int curX = startX;
		int curY = startY;
		via[startX][startY] = 99;
		cost[startX][startY] = 0;
		int head = 0;
		int tail = 0;
		tileQueueX[head] = startX;
		tileQueueY[head] = startY;
		head++;
		final int pathLength = tileQueueX.length;
		final int blocks[][] = Bot.getClient().getRSGroundDataArray()[Bot.getClient().getPlane()].getBlocks();
		while (tail != head) {
			curX = tileQueueX[tail];
			curY = tileQueueY[tail];

			if (!isObject && (curX == destX) && (curY == destY))
				return true;
			else if (isObject) {
				if (((curX == destX) && (curY == destY + 1)) || ((curX == destX) && (curY == destY - 1)) || ((curX == destX + 1) && (curY == destY)) || ((curX == destX - 1) && (curY == destY)))
					return true;
			}
			tail = (tail + 1) % pathLength;

			// Big and ugly block of code
			final int thisCost = cost[curX][curY] + 1;
			// Can go south (by determining, whether the north side of the
			// south tile is blocked :P)
			if ((curY > 0) && (via[curX][curY - 1] == 0) && ((blocks[curX][curY - 1] & 0x1280102) == 0)) {
				tileQueueX[head] = curX;
				tileQueueY[head] = curY - 1;
				head = (head + 1) % pathLength;
				via[curX][curY - 1] = 1;
				cost[curX][curY - 1] = thisCost;
			}
			// Can go west
			if ((curX > 0) && (via[curX - 1][curY] == 0) && ((blocks[curX - 1][curY] & 0x1280108) == 0)) {
				tileQueueX[head] = curX - 1;
				tileQueueY[head] = curY;
				head = (head + 1) % pathLength;
				via[curX - 1][curY] = 2;
				cost[curX - 1][curY] = thisCost;
			}
			// Can go north
			if ((curY < 104 - 1) && (via[curX][curY + 1] == 0) && ((blocks[curX][curY + 1] & 0x1280120) == 0)) {
				tileQueueX[head] = curX;
				tileQueueY[head] = curY + 1;
				head = (head + 1) % pathLength;
				via[curX][curY + 1] = 4;
				cost[curX][curY + 1] = thisCost;
			}
			// Can go east
			if ((curX < 104 - 1) && (via[curX + 1][curY] == 0) && ((blocks[curX + 1][curY] & 0x1280180) == 0)) {
				tileQueueX[head] = curX + 1;
				tileQueueY[head] = curY;
				head = (head + 1) % pathLength;
				via[curX + 1][curY] = 8;
				cost[curX + 1][curY] = thisCost;
			}
			// Can go southwest
			if ((curX > 0) && (curY > 0) && (via[curX - 1][curY - 1] == 0) && ((blocks[curX - 1][curY - 1] & 0x128010e) == 0) && ((blocks[curX - 1][curY] & 0x1280108) == 0) && ((blocks[curX][curY - 1] & 0x1280102) == 0)) {
				tileQueueX[head] = curX - 1;
				tileQueueY[head] = curY - 1;
				head = (head + 1) % pathLength;
				via[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = thisCost;
			}
			// Can go northwest
			if ((curX > 0) && (curY < 104 - 1) && (via[curX - 1][curY + 1] == 0) && ((blocks[curX - 1][curY + 1] & 0x1280138) == 0) && ((blocks[curX - 1][curY] & 0x1280108) == 0) && ((blocks[curX][curY + 1] & 0x1280120) == 0)) {
				tileQueueX[head] = curX - 1;
				tileQueueY[head] = curY + 1;
				head = (head + 1) % pathLength;
				via[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = thisCost;
			}
			// Can go southeast
			if ((curX < 104 - 1) && (curY > 0) && (via[curX + 1][curY - 1] == 0) && ((blocks[curX + 1][curY - 1] & 0x1280183) == 0) && ((blocks[curX + 1][curY] & 0x1280180) == 0) && ((blocks[curX][curY - 1] & 0x1280102) == 0)) {
				tileQueueX[head] = curX + 1;
				tileQueueY[head] = curY - 1;
				head = (head + 1) % pathLength;
				via[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = thisCost;
			}
			// can go northeast
			if ((curX < 104 - 1) && (curY < 104 - 1) && (via[curX + 1][curY + 1] == 0) && ((blocks[curX + 1][curY + 1] & 0x12801e0) == 0) && ((blocks[curX + 1][curY] & 0x1280180) == 0) && ((blocks[curX][curY + 1] & 0x1280120) == 0)) {
				tileQueueX[head] = curX + 1;
				tileQueueY[head] = curY + 1;
				head = (head + 1) % pathLength;
				via[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = thisCost;
			}
		}
		return false;
	}

	public static boolean canReach(final RSTile destination, final boolean isObject) {
		final Methods m = new Methods();
		return Calculations.canReach(m.getMyPlayer().getLocation(), destination, isObject);
	}

	public static boolean canReach(final RSTile source, final RSTile destination, final boolean isObject) {
		return Calculations.canReach(source.getX() - Bot.getClient().getBaseX(), source.getY() - Bot.getClient().getBaseY(), destination.getX() - Bot.getClient().getBaseX(), destination.getY() - Bot.getClient().getBaseY(), isObject);
	}

	public static Node findNodeByID(final long id) {
		final RSItemDefFactory factory = Bot.getClient().getRSItemDefFactory();
		if ((factory == null) || (factory.getMRUNodes() == null))
			return null;

		return Calculations.findNodeByID(factory.getMRUNodes().getNodeCache(), id);
	}

	public static Node findNodeByID(final org.rsbot.accessors.NodeCache nc, final long id) {
		try {
			if ((nc == null) || (nc.getCache() == null) || (id < 0))
				return null;

			final Node n = nc.getCache()[(int) (id & nc.getCache().length - 1)];
			for (Node node = n.getPrevious(); node != n; node = node.getPrevious()) {
				if (node.getID() == id)
					return node;
			}
		} catch (final Exception e) {
		}

		return null;
	}

	/**
	 * @param startX
	 *            the startX (0 < startX < 104)
	 * @param startY
	 *            the startY (0 < startY < 104)
	 * @param destX
	 *            the destX (0 < destX < 104)
	 * @param destY
	 *            the destY (0 < destY < 104)
	 * @param isObject
	 *            if it's an object, it will find path which touches it.
	 * @return The distance of the shortet path to the destination; or -1 if no
	 *         valid path to the destination was found.
	 */
	public static int getRealDistanceTo(final int startX, final int startY, final int destX, final int destY, final boolean isObject) {
		final int[][] via = new int[104][104];
		final int[][] cost = new int[104][104];
		final int[] tileQueueX = new int[4000];
		final int[] tileQueueY = new int[4000];

		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				via[xx][yy] = 0;
				cost[xx][yy] = 99999999;
			}
		}

		int curX = startX;
		int curY = startY;
		via[startX][startY] = 99;
		cost[startX][startY] = 0;
		int head = 0;
		int tail = 0;
		tileQueueX[head] = startX;
		tileQueueY[head++] = startY;
		boolean foundPath = false;
		final int pathLength = tileQueueX.length;
		final int blocks[][] = Bot.getClient().getRSGroundDataArray()[Bot.getClient().getPlane()].getBlocks();
		while (tail != head) {
			curX = tileQueueX[tail];
			curY = tileQueueY[tail];

			if (!isObject && (curX == destX) && (curY == destY)) {
				foundPath = true;
				break;
			} else if (isObject) {
				if (((curX == destX) && (curY == destY + 1)) || ((curX == destX) && (curY == destY - 1)) || ((curX == destX + 1) && (curY == destY)) || ((curX == destX - 1) && (curY == destY))) {
					foundPath = true;
					break;
				}
			}
			tail = (tail + 1) % pathLength;

			// Big and ugly block of code
			final int thisCost = cost[curX][curY] + 1;
			if ((curY > 0) && (via[curX][curY - 1] == 0) && ((blocks[curX][curY - 1] & 0x1280102) == 0)) {
				tileQueueX[head] = curX;
				tileQueueY[head] = curY - 1;
				head = (head + 1) % pathLength;
				via[curX][curY - 1] = 1;
				cost[curX][curY - 1] = thisCost;
			}
			if ((curX > 0) && (via[curX - 1][curY] == 0) && ((blocks[curX - 1][curY] & 0x1280108) == 0)) {
				tileQueueX[head] = curX - 1;
				tileQueueY[head] = curY;
				head = (head + 1) % pathLength;
				via[curX - 1][curY] = 2;
				cost[curX - 1][curY] = thisCost;
			}
			if ((curY < 104 - 1) && (via[curX][curY + 1] == 0) && ((blocks[curX][curY + 1] & 0x1280120) == 0)) {
				tileQueueX[head] = curX;
				tileQueueY[head] = curY + 1;
				head = (head + 1) % pathLength;
				via[curX][curY + 1] = 4;
				cost[curX][curY + 1] = thisCost;
			}
			if ((curX < 104 - 1) && (via[curX + 1][curY] == 0) && ((blocks[curX + 1][curY] & 0x1280180) == 0)) {
				tileQueueX[head] = curX + 1;
				tileQueueY[head] = curY;
				head = (head + 1) % pathLength;
				via[curX + 1][curY] = 8;
				cost[curX + 1][curY] = thisCost;
			}
			if ((curX > 0) && (curY > 0) && (via[curX - 1][curY - 1] == 0) && ((blocks[curX - 1][curY - 1] & 0x128010e) == 0) && ((blocks[curX - 1][curY] & 0x1280108) == 0) && ((blocks[curX][curY - 1] & 0x1280102) == 0)) {
				tileQueueX[head] = curX - 1;
				tileQueueY[head] = curY - 1;
				head = (head + 1) % pathLength;
				via[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = thisCost;
			}
			if ((curX > 0) && (curY < 104 - 1) && (via[curX - 1][curY + 1] == 0) && ((blocks[curX - 1][curY + 1] & 0x1280138) == 0) && ((blocks[curX - 1][curY] & 0x1280108) == 0) && ((blocks[curX][curY + 1] & 0x1280120) == 0)) {
				tileQueueX[head] = curX - 1;
				tileQueueY[head] = curY + 1;
				head = (head + 1) % pathLength;
				via[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = thisCost;
			}
			if ((curX < 104 - 1) && (curY > 0) && (via[curX + 1][curY - 1] == 0) && ((blocks[curX + 1][curY - 1] & 0x1280183) == 0) && ((blocks[curX + 1][curY] & 0x1280180) == 0) && ((blocks[curX][curY - 1] & 0x1280102) == 0)) {
				tileQueueX[head] = curX + 1;
				tileQueueY[head] = curY - 1;
				head = (head + 1) % pathLength;
				via[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = thisCost;
			}
			if ((curX < 104 - 1) && (curY < 104 - 1) && (via[curX + 1][curY + 1] == 0) && ((blocks[curX + 1][curY + 1] & 0x12801e0) == 0) && ((blocks[curX + 1][curY] & 0x1280180) == 0) && ((blocks[curX][curY + 1] & 0x1280120) == 0)) {
				tileQueueX[head] = curX + 1;
				tileQueueY[head] = curY + 1;
				head = (head + 1) % pathLength;
				via[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = thisCost;
			}
		}
		if (foundPath)
			return cost[curX][curY];
		return -1;
	}

	public static boolean onScreen(final Point check) {
		final int x = check.x, y = check.y;
		return (x > 4) && (x < CanvasWrapper.getGameWidth() - 253) && (y > 4) && (y < CanvasWrapper.getGameHeight() - 169);
	}

	public static int tileHeight(final int X, final int Z) {
		int p = Bot.getClient().getPlane();
		final int x = X >> 7;
		final int z = Z >> 7;

		if ((x < 0) || (x > 104) || (z < 0) || (z > 104))
			return 0;

		if ((p < 3) && ((Bot.getClient().getGroundByteArray()[1][x][z] & 2) != 0)) {
			p++;
		}

		final TileData[] dataArray = Bot.getClient().getTileData();

		if ((dataArray.length > p) && (dataArray[p] != null))
			return dataArray[p].getHeight(X, Z);
		return 0;
	}

	// Calculations
	public static Point tileToScreen(final int x, final int y, final double dX, final double dY, final int height) {
		return Calculations.worldToScreen((int) ((x - Bot.getClient().getBaseX() + dX) * 128), (int) ((y - Bot.getClient().getBaseY() + dY) * 128), height);
	}

	public static Point tileToScreen(final int x, final int y, final int height) {
		return Calculations.tileToScreen(x, y, 0.5, 0.5, height);
	}

	public static Point tileToScreen(final RSTile t) {
		return Calculations.tileToScreen(t.getX(), t.getY(), 0);
	}

	public static Point tileToScreen(final RSTile t, final double dX, final double dY, final int height) {
		return Calculations.tileToScreen(t.getX(), t.getY(), dX, dY, height);
	}

	public static Point tileToScreen(final RSTile t, final int h) {
		return Calculations.tileToScreen(t.getX(), t.getY(), h);
	}

	public static void updateRenderInfo(final org.rsbot.accessors.Render r, final org.rsbot.accessors.RenderData rd) {
		if ((r == null) || (rd == null))
			return;

		Render.absoluteX1 = r.getAbsoluteX1();
		Render.absoluteX2 = r.getAbsoluteX2();
		Render.absoluteY1 = r.getAbsoluteY1();
		Render.absoluteY2 = r.getAbsoluteY2();

		Render.xMultiplier = r.getXMultiplier();
		Render.yMultiplier = r.getYMultiplier();

		Render.zNear = r.getZNear();
		Render.zFar = r.getZFar();

		RenderData.xOff = rd.getXOff();
		RenderData.xX = rd.getXX();
		RenderData.xY = rd.getXY();
		RenderData.xZ = rd.getXZ();

		RenderData.yOff = rd.getYOff();
		RenderData.yX = rd.getYX();
		RenderData.yY = rd.getYY();
		RenderData.yZ = rd.getYZ();

		RenderData.zOff = rd.getZOff();
		RenderData.zX = rd.getZX();
		RenderData.zY = rd.getZY();
		RenderData.zZ = rd.getZZ();
	}

	public static Point worldToMinimap(double x, double y) {
		x -= Bot.getClient().getBaseX();
		y -= Bot.getClient().getBaseY();
		final int calculatedX = (int) (x * 4 + 2) - Bot.getClient().getMyRSPlayer().getX() / 32;
		final int calculatedY = (int) (y * 4 + 2) - Bot.getClient().getMyRSPlayer().getY() / 32;

		try {
			final org.rsbot.accessors.RSInterface mm = DynamicConstants.getMinimapInterface();
			if (mm == null)
				return new Point(-1, -1);
			final RSInterfaceChild mm2 = RSInterface.getChildInterface(mm.getID());

			final int actDistSq = calculatedX * calculatedX + calculatedY * calculatedY;

			final int mmDist = 10 + Math.max(mm2.getWidth() / 2, mm2.getHeight() / 2);
			if (mmDist * mmDist >= actDistSq) {
				int angle = 0x3fff & (int) Bot.getClient().getMinimapOffset();
				if (Bot.getClient().getMinimapSetting() != 4) {
					angle = 0x3fff & Bot.getClient().getMinimapAngle() + (int) Bot.getClient().getMinimapOffset();
				}

				int cs = Calculations.CURVESIN[angle];
				int cc = Calculations.CURVECOS[angle];
				if (Bot.getClient().getMinimapSetting() != 4) {
					final int fact = 256 + Bot.getClient().getMinimapScale();
					cs = 256 * cs / fact;
					cc = 256 * cc / fact;
				}

				final int calcCenterX = cc * calculatedX + cs * calculatedY >> 15;
				final int calcCenterY = cc * calculatedY - cs * calculatedX >> 15;

				final int screenx = calcCenterX + mm2.getAbsoluteX() + mm2.getWidth() / 2;
				final int screeny = -calcCenterY + mm2.getAbsoluteY() + mm2.getHeight() / 2;

				// Check if point is within the circel of the minimap instead of
				// the
				// rectangle!
				if ((Math.max(calcCenterY, -calcCenterY) <= mm2.getWidth() / 2.0 * .8) && (Math.max(calcCenterX, -calcCenterX) <= mm2.getHeight() / 2 * .8))
					return new Point(screenx, screeny);
				else
					return new Point(-1, -1);
			}
		} catch (final NullPointerException npe) {
		}

		return new Point(-1, -1);// not on minimap
	}

	public static Point w2s(int x, int y, int z)
	{
		if(Bot.getClient().getDetailInfo() == null)
			return new Point(-1, -1);
		
		int detail_lvl = Bot.getClient().getDetailInfo().getDetailLevel();
		
		if(detail_lvl == 0 || detail_lvl == 2)
		{
			int _z = (int) (RenderData.zOff + ((int) (RenderData.zX * x + RenderData.zY * y + RenderData.zZ * z) >> 15));
			if( (_z < Render.zNear) || (_z > Render.zFar) )
				return new Point(-1, -1);

			int _x = Render.xMultiplier * ((int) RenderData.xOff + ((int) (RenderData.xX * x + RenderData.xY * y + RenderData.xZ * z) >> 15)) / _z;
			int _y = Render.yMultiplier * ((int) RenderData.yOff + ((int) (RenderData.yX * x + RenderData.yY * y + RenderData.yZ * z) >> 15)) / _z;

			if ((_x >= Render.absoluteX1) && (_x <= Render.absoluteX2) && (_y >= Render.absoluteY1) && (_y <= Render.absoluteY2))
				return new Point((int) (_x - Render.absoluteX1), (int) (_y - Render.absoluteY1));
		}
		else if(detail_lvl == 1)
		{
			float _z = RenderData.zX * x + RenderData.zY * y + RenderData.zZ * z + RenderData.zOff;
			if ( (_z < Render.zNear) || (_z > Render.zFar) )
				return new Point(-1, -1);

			final int _x = (int) (Render.xMultiplier * (RenderData.xX * x + RenderData.xY * y + RenderData.xZ * z + RenderData.xOff) / _z);
			final int _y = (int) (Render.yMultiplier * (RenderData.yX * x + RenderData.yY * y + RenderData.yZ * z + RenderData.yOff) / _z);

			if ((_x >= Render.absoluteX1) && (_x <= Render.absoluteX2) && (_y >= Render.absoluteY1) && (_y <= Render.absoluteY2))
				return new Point((int) (_x - Render.absoluteX1), (int) (_y - Render.absoluteY1));
		}
		
		return new Point(-1, -1);
	}
	public static Point worldToScreen(final int x, final int z, final int height) {

		if ((Bot.getClient().getGroundByteArray() == null) || (Bot.getClient().getTileData() == null) || (Bot.getClient().getDetailInfo() == null) || (x < 128) || (z < 128) || (x > 13056) || (z > 13056))
			return new Point(-1, -1);

		int y = Calculations.tileHeight(x, z) - height;

		Point p = w2s(x, y, z);

		if ((p != null) && Calculations.onScreen(p))
			return p;

		return new Point(-1, -1);
	}

	private Calculations() {
	}
}
