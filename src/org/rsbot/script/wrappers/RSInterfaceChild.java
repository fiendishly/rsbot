/**
 * 
 */
package org.rsbot.script.wrappers;

import java.awt.Point;
import java.awt.Rectangle;

import org.rsbot.accessors.RSInterfaceNode;
import org.rsbot.bot.Bot;
import org.rsbot.script.internal.NodeCache;

/**
 * This class handles an Interface Child The class RSInterface references to
 * this one a lot
 * 
 * @author Qauters
 */
public class RSInterfaceChild {
	/**
	 * The index of this interface in the parent.
	 * */
	private final int index;
	/**
	 * The parent interface containing this child.
	 * */
	private final RSInterface parInterface;

	/**
	 * Initializes the child.
	 * 
	 * @param parent
	 *            The parent interface.
	 * @param index
	 *            The child index of this child.
	 */
	RSInterfaceChild(final RSInterface parent, final int index) {
		parInterface = parent;
		this.index = index;
	}

	/**
	 * Checks the actions of the child for a given text phrase
	 * 
	 * @param phrase
	 *            The phrase to check for
	 * @return true if found
	 */
	public boolean containsAction(final String phrase) {
		for (final String action : getActions()) {
			if (action.toLowerCase().contains(phrase.toLowerCase()))
				return true;
		}
		return false;
	}

	/**
	 * Checks the text of the child for a given text phrase
	 * 
	 * @param phrase
	 *            The phrase to check for
	 * @return Whether the text contained the phrase or niot
	 */
	public boolean containsText(final String phrase) {
		return getText().contains(phrase);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof RSInterfaceChild) {
			final RSInterfaceChild child = (RSInterfaceChild) obj;
			return (index == child.index) && child.parInterface.equals(parInterface);
		}
		return false;
	}

	/**
	 * Get's the absolute x position of the child, calculated from the beginning
	 * of the rs screen
	 * 
	 * @return the absolute x or -1 if null
	 */
	public int getAbsoluteX() {
		// Get internal Interface
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter == null)
			return -1;

		// Define x
		int x = 0;

		// Find parentX
		final int parentID = getParentID();
		if (parentID != -1) {
			x = RSInterface.getChildInterface(parentID >> 16, parentID & 0xFFFF).getAbsoluteX();
		} else // No parentX so get the baseX, using bounds or masterX
		{
			// Get bounds array
			final Rectangle[] bounds = Bot.getClient().getRSInterfaceBoundsArray();

			// Get bounds array index
			final int bi = inter.getBoundsArrayIndex();
			if ((bi >= 0) && (bounds != null) && (bi < bounds.length) && (bounds[bi] != null))
				return bounds[bi].x; // Return x here, since it already contains
			// our x!
			else {
				x = inter.getMasterX();
			}
		}

		// Add our x
		x += inter.getX();

		// Return x
		return x;
	}

	/**
	 * Get's the absolute y position of the child, calculated from the beginning
	 * of the rs screen
	 * 
	 * @return the absolute y position or -1 if null
	 */
	public int getAbsoluteY() {
		// Get internal Interface
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter == null)
			return -1;

		// Define y
		int y = 0;

		// Find parentY
		final int parentID = getParentID();
		if (parentID != -1) {
			y = RSInterface.getChildInterface(parentID >> 16, parentID & 0xFFFF).getAbsoluteY();
		} else // No parentY so get the baseY, using bounds or masterY
		{
			// Get bounds array
			final Rectangle[] bounds = Bot.getClient().getRSInterfaceBoundsArray();

			// Get bounds array index
			final int bi = inter.getBoundsArrayIndex();
			if ((bi >= 0) && (bounds != null) && (bi < bounds.length) && (bounds[bi] != null))
				return bounds[bi].y; // Return y here, since it already contains
			// our y!
			else {
				y = inter.getMasterY();
			}
		}

		// Add our y
		y += inter.getY();

		// Return y
		return y;
	}

	/**
	 * Get's the actions of the child. The elements will never be null.
	 * 
	 * @return the actions or an empty array if null
	 */
	public String[] getActions() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getActions();
		return new String[0];
	}

	/**
	 * DOESN'T EXIST ANYMORE, RETURNS -1 Get's the action type of the child
	 * 
	 * @return the action type or -1 if null
	 * @deprecated
	 */
	@Deprecated
	public int getActionType() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
		}
		return -1;
	}

	/**
	 * Get's the Area of the child, calculated from it's absolute position
	 * 
	 * @return the area or new Rectangle(-1, -1, -1, -1) if null
	 */
	public Rectangle getArea() {
		return new Rectangle(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
	}

	/**
	 * Get's the background color of the child
	 * 
	 * @return the background color or -1 if null
	 */
	public int getBackgroundColor() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getTextureID();
		return -1;
	}

	public int getBorderThickness() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getBorderThickness();
		return -1;
	}

	public int getBoundsArrayIndex() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getBoundsArrayIndex();

		return -1;
	}

	/**
	 * Return component ID of child used for "unsolvable" randoms. Written by
	 * PwnZ.
	 * 
	 * @return Component ID.
	 */
	public int getComponentID() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getComponentID();
		return -1;
	}

	/**
	 * The components (bank items etc) of this interface.
	 * 
	 * @return The components or RSInterfaceComponent[0] if this child is
	 *         invalid.
	 */
	public RSInterfaceComponent[] getComponents() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if ((inter != null) && (inter.getComponents() != null)) {
			final RSInterfaceComponent[] components = new RSInterfaceComponent[inter.getComponents().length];
			for (int i = 0; i < components.length; i++) {
				components[i] = new RSInterfaceComponent(parInterface, index, i);
			}
			return components;
		}
		return new RSInterfaceComponent[0];
	}

	/**
	 * Get's the height of the child
	 * 
	 * @return the height of the child or -1 if null
	 */
	public int getHeight() {
		final org.rsbot.accessors.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null)
			return childInterface.getHeight() - 4;
		return -1;
	}

	public int getHorizontalScrollBarSize() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getHorizontalScrollBarSize();
		return -1;

	}

	public int getHorizontalScrollBarThumbPosition() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getHorizontalScrollBarThumbPosition();
		return -1;

	}

	public int getHorizontalScrollBarThumbSize() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getHorizontalScrollBarThumbSize();
		return -1;

	}

	public int getID() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getID();
		return -1;

	}

	/**
	 * @return The index of this interface.
	 * */
	public int getIndex() {
		return index;
	}

	/**
	 * @return The interface represented by this object.
	 * */
	org.rsbot.accessors.RSInterface getInterfaceInternal() {
		final org.rsbot.accessors.RSInterface[] children = parInterface.getChildrenInternal();
		if ((children != null) && (index < children.length))
			return children[index];
		return null;
	}

	/**
	 * Get's the inventory of the child, automatically corrects item ids.
	 * 
	 * @return The inventory of the child or new int[0] if null
	 * @deprecated Not existing anymore, inventory always exists out of
	 *             components now.
	 */
	@Deprecated
	public int[] getInventory() {
		return new int[0];
	}

	/**
	 * returns the amount of specified items, you have in your inventory
	 * 
	 * @param itemIDs
	 *            The item Ids to search for
	 * @return The amount of items you have, it checks it stack size aswell
	 * @deprecated Not existing anymore, inventory always exists out of
	 *             components now
	 */
	@Deprecated
	public int getInventoryItemCount(final int... itemIDs) {
		return 0;
	}

	/**
	 * Get's the inventory stack sizes of the child
	 * 
	 * @return The inventory stack sizes or new int[0] if null
	 * @deprecated Not existing anymore, inventory always exists out of
	 *             components now
	 */
	@Deprecated
	public int[] getInventoryStackSizes() {
		return new int[0];
	}

	/**
	 * Get's the inventory sprite pad of the child God know's what it get, I'll
	 * check it out later
	 * 
	 * @return The inventory sprite pad or new Point(-1, -1) if null
	 * @deprecated Not existing anymore, inventory always exists out of
	 *             components now
	 */
	@Deprecated
	public Point getInvSpritePad() {
		return new Point(-1, -1);
	}

	/**
	 * Get's the model ID of the child
	 * 
	 * @return the model ID or -1 if null
	 */
	public int getModelID() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getModelID();

		return -1;
	}

	/**
	 * Get's the model type of the child
	 * 
	 * @return the model type or -1 if null
	 */
	public int getModelType() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getModelType();

		return -1;
	}

	public int getModelZoom() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getModelZoom();
		return -1;

	}

	/**
	 * Get's the parent id of this interface. It will first look at the internal
	 * parentID, if that's -1 then it will search the RSInterfaceNC to find it's
	 * parent.
	 * 
	 * @return the parentID or -1 if none
	 */
	public int getParentID() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter == null)
			return -1;

		if (inter.getParentID() != -1)
			return inter.getParentID();

		final int mainID = getID() >>> 16;
		final NodeCache ncI = new NodeCache(Bot.getClient().getRSInterfaceNC());

		for (RSInterfaceNode node = (RSInterfaceNode) ncI.getFirst(); node != null; node = (RSInterfaceNode) ncI.getNext()) {
			if (mainID == node.getMainID())
				return (int) node.getID();
		}

		return -1;
	}

	/**
	 * @return The parent interface.
	 * */
	public RSInterface getParInterface() {
		return parInterface;
	}

	/**
	 * Get's the absolute position of the child
	 * 
	 * @return the absolute position or new Point(-1, -1) if null
	 */
	public Point getPosition() {
		return new Point(getAbsoluteX(), getAbsoluteY());
	}

	/**
	 * Get's the relative x position of the child, calculated from the beginning
	 * of the interface
	 * 
	 * @return the relative x position or -1 if null
	 */
	public int getRelativeX() {
		final org.rsbot.accessors.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null)
			return childInterface.getX();
		return -1;
	}

	/**
	 * Get's the relative y position of the child, calculated from the beginning
	 * of the interface
	 * 
	 * @return the relative y position -1 if null
	 */
	public int getRelativeY() {
		final org.rsbot.accessors.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null)
			return childInterface.getY();
		return -1;
	}

	/**
	 * Get's the selected action name of the child
	 * 
	 * @return the selected action name or "" if null
	 */
	public String getSelectedActionName() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getSelectedActionName();
		return "";
	}

	public int getShadowColor() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getShadowColor();
		return -1;

	}

	public int getSpecialType() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getSpecialType();

		return -1;
	}

	/**
	 * Get's the spell name of the child
	 * 
	 * @return the spell name or "" if null
	 */
	public String getSpellName() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getSpellName();
		return "";
	}

	/**
	 * Get's the text of the child
	 * 
	 * @return the txt or "" if null
	 */
	public String getText() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getText();
		return "";
	}

	/**
	 * Get's the text color of the child
	 * 
	 * @return the text color or -1 if null
	 */
	public int getTextColor() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getTextColor();
		return -1;
	}

	/**
	 * Get's the tooltip of the child
	 * 
	 * @return the tooltip or "" if null
	 */
	public String getTooltip() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getToolTip();
		return "";
	}

	/**
	 * Get's the type of the child
	 * 
	 * @return the type or -1 if null
	 */
	public int getType() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getType();
		return -1;
	}

	/**
	 * Get's the value index array of the child Haven't checked what it does yet
	 * 
	 * @return the value index array or new int[0][0] if null
	 */
	public int[][] getValueIndexArray() {
		final org.rsbot.accessors.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			final int[][] vindex = childInterface.getValueIndexArray();
			if (vindex != null) { // clone does NOT deep copy
				final int[][] out = new int[vindex.length][0];
				for (int i = 0; i < vindex.length; i++) {
					final int[] cur = vindex[i];
					if (cur != null) {
						out[i] = cur.clone();
					}
				}
				return out;
			}
		}
		// clone, otherwise you have a pointer
		return new int[0][0];
	}

	public int getVerticalScrollBarPosition() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getVerticalScrollBarPosition();
		return -1;

	}

	public int getVerticalScrollBarSize() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getVerticalScrollBarSize();
		return -1;

	}

	public int getVerticalScrollBarThumbSize() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getVerticalScrollBarThumbSize();
		return -1;

	}

	/**
	 * Get's the width of the child
	 * 
	 * @return the width of the child or -1 if null
	 */
	public int getWidth() {
		final org.rsbot.accessors.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null)
			return childInterface.getWidth() - 4;
		return -1;
	}

	/**
	 * Get the xRotation of the interface.
	 * 
	 * @return xRotation of the interface
	 */
	public int getXRotation() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getXRotation();
		return -1;

	}

	public int getYRotation() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getYRotation();
		return -1;

	}

	public int getZRotation() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		if (inter != null)
			return inter.getZRotation();
		return -1;
	}

	@Override
	public int hashCode() {
		return parInterface.getIndex() * 31 + index;
	}

	public boolean isHorizontallyFlipped() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		return (inter != null) && inter.isHorizontallyFlipped();

	}

	/**
	 * Whether the child is an inventory interface or not
	 * 
	 * @return True if it's an inventory interface, else false
	 */
	public boolean isInventoryRSInterface() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		return (inter != null) && inter.isInventoryRSInterface();
	}

	/**
	 * Check's whether or not the child is valid
	 * 
	 * @return true if not valid
	 */
	@Deprecated
	public boolean isNull() {
		return !isValid();
	}

	/**
	 * @return Whether or not the child is valid.
	 */
	public boolean isValid() {
		return parInterface.isValid() && (parInterface.getChildrenInternal() != null);
	}

	public boolean isVerticallyFlipped() {
		final org.rsbot.accessors.RSInterface inter = getInterfaceInternal();
		return (inter != null) && inter.isVerticallyFlipped();
	}
}
