package org.rsbot.script.wrappers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.rsbot.accessors.Client;
import org.rsbot.bot.Bot;

/**
 * This is the class that handles an Interface. Notice it handles a whole
 * interface, through this you can access his children.
 * 
 * @author Qauters
 */
public class RSInterface implements Iterable<RSInterfaceChild> {
	// A cache of all the interfaces. Only as big as the maximum size of the
	// client's cache.
	private static RSInterface[] mainCache = new RSInterface[0];
	// If it doesn't fit in the above cache.
	private static Map<Integer, RSInterface> sparseMap = new HashMap<Integer, RSInterface>();

	/**
	 * Enlarges the cache if there are more interfaces than the cache size.
	 * */
	private synchronized static void enlargeCache() {
		final Client c = Bot.getClient();
		if (c == null)
			return;
		final org.rsbot.accessors.RSInterface[][] inters = c.getRSInterfaceCache();
		if ((inters != null) && (RSInterface.mainCache.length < inters.length)) { // enlarge
			// cache
			RSInterface.mainCache = Arrays.copyOf(RSInterface.mainCache, inters.length);
			for (int i = RSInterface.mainCache.length; i < RSInterface.mainCache.length; i++) {
				final RSInterface tmp = RSInterface.sparseMap.get(i);
				if (tmp != null) {
					RSInterface.sparseMap.remove(i);
					RSInterface.mainCache[i] = tmp;
				}
			}
		}
	}

	/**
	 * @return All the valid interfaces.
	 * */
	public synchronized static RSInterface[] getAllInterfaces() {
		RSInterface.enlargeCache();
		final org.rsbot.accessors.RSInterface[][] inters = Bot.getClient().getRSInterfaceCache();
		if (inters == null)
			return new RSInterface[0];
		final List<RSInterface> out = new ArrayList<RSInterface>();
		for (int i = 0; i < inters.length; i++) {
			if (inters[i] != null) {
				final RSInterface in = RSInterface.getInterface(i);
				if (in.isValid()) {
					out.add(in);
				}
			}
		}
		return out.toArray(new RSInterface[out.size()]);
	}

	public static RSInterfaceChild getChildInterface(final int id) {
		final int x = id >> 16;
		final int y = id & 0xFFFF;

		return RSInterface.getInterface(x).getChild(y);
	}

	/**
	 * @param index
	 *            The parent interface index
	 * @param indexChild
	 *            The child interface index
	 * @return The child for the given index and indexChild
	 * */
	public static RSInterfaceChild getChildInterface(final int index, final int indexChild) {
		return RSInterface.getInterface(index).getChild(indexChild);
	}

	/**
	 * @param index
	 *            The index of the interface.
	 * @return The interface for the given index.
	 * */
	public synchronized static RSInterface getInterface(final int index) {
		RSInterface inter;
		final int cacheLen = RSInterface.mainCache.length;
		if (index < cacheLen) {
			inter = RSInterface.mainCache[index];
			if (inter == null) {
				inter = new RSInterface(index, true);
				RSInterface.mainCache[index] = inter;
			}
		} else {
			inter = RSInterface.sparseMap.get(index);
			if (inter == null) {
				RSInterface.enlargeCache();
				if (index < cacheLen) {
					inter = RSInterface.mainCache[index];
					if (inter == null) {
						inter = new RSInterface(index, true);
						RSInterface.mainCache[index] = inter;
					}
				} else {
					inter = new RSInterface(index, true);
					RSInterface.sparseMap.put(index, inter);
				}
			}
		}
		return inter;
	}

	/**
	 * @return The maximum *known* interface cache size.
	 * */
	public synchronized static int getMaxInterfaceCacheSize() {
		RSInterface.enlargeCache();
		return RSInterface.mainCache.length;
	}

	/**
	 * Cache of this interface's children.
	 * */
	private RSInterfaceChild[] childCache = new RSInterfaceChild[0];

	private final Object childLock = new Object();

	/**
	 * The index of this interface.
	 * */
	private int index;

	/**
	 * The init method. Use RSInterface.getInterface().
	 * 
	 * @param iface
	 *            the id of the interface, like 149
	 */
	@Deprecated
	public RSInterface(final int iface) {
		if (iface < 0)
			throw new IndexOutOfBoundsException(iface + " < 0");
		index = iface;
	}

	/**
	 * The init method. Only statics should use this.
	 * 
	 * @param iface
	 *            The id of the interface, e.g. 149.
	 * @param b
	 *            Here until we can drop the public method.
	 */
	private RSInterface(final int iface, final boolean b) {
		if (iface < 0)
			throw new IndexOutOfBoundsException(iface + " < 0");
		index = iface;
	}

	/**
	 * The init method. Never use this.
	 * 
	 * @param iface
	 *            the main interface like getInterfaceCache()[149]
	 */
	@Deprecated
	public RSInterface(final org.rsbot.accessors.RSInterface[] iface) {
		final org.rsbot.accessors.RSInterface[][] cache = Bot.getClient().getRSInterfaceCache();
		if (cache != null) {
			for (int i = 0; i < cache.length; i++) {
				if (cache[i] == iface) {
					index = i;
					return;
				}
			}
		}
		throw new RuntimeException("Could not find specified interface.");
	}

	/**
	 * Searches all it's actions, to find your phrase
	 * 
	 * @param phrase
	 *            Text to search for
	 * @return true if found
	 */
	public boolean containsAction(final String phrase) {
		for (final RSInterfaceChild child : getChildren()) {
			if (child == null) {
				continue;
			}
			if (child.getActions() == null)
				return false;
			for (final String action : child.getActions()) {
				/*
				 * Loggable.getDefaultSystem.out.println()
				 * .System.out.println("RSInterface", "Action: " + action);
				 */
				if (action == null) {
					continue;
				}
				if (action.toLowerCase().contains(phrase.toLowerCase()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Searches all it's text, to find your phrase
	 * 
	 * @param phrase
	 *            Text to search for
	 * @return true if found, false if null
	 */
	public boolean containsText(final String phrase) {
		return getText().contains(phrase);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof RSInterface) {
			final RSInterface inter = (RSInterface) obj;
			return inter.index == index;
		}
		return false;
	}

	/**
	 * Get's the child you desire
	 * 
	 * @param id
	 *            The id of the child like in getInterfaceCache()[149][id]
	 * @return The child
	 */
	public RSInterfaceChild getChild(final int id) { // TODO sparseMap
		synchronized (childCache) {
			final org.rsbot.accessors.RSInterface[] children = getChildrenInternal();
			final int ensureLen = Math.max(children != null ? children.length : 0, id + 1);
			if (childCache.length < ensureLen) { // extend if necessary
				final int prevLen = childCache.length;
				childCache = Arrays.copyOf(childCache, ensureLen);
				for (int i = prevLen; i < ensureLen; i++) {
					childCache[i] = new RSInterfaceChild(this, i);
				}
			}
			return childCache[id];
		}
	}

	/**
	 * Get's the amount of children, for use in getChild(id)
	 * 
	 * @return the amount of children, or 0 if null (0, because of
	 *         IOOBException)
	 */
	public int getChildCount() {
		final org.rsbot.accessors.RSInterface[] children = getChildrenInternal();
		if (children != null)
			return children.length;
		return 0;
	}

	/**
	 * Get's the interface his children.
	 * 
	 * @return the children to the extent known
	 */
	public RSInterfaceChild[] getChildren() {
		synchronized (childLock) {
			final org.rsbot.accessors.RSInterface[] children = getChildrenInternal();
			if (children == null)
				return childCache.clone(); // return as is
			else {
				if (childCache.length < children.length) { // extend if
					// necessary
					final int prevLen = childCache.length;
					childCache = Arrays.copyOf(childCache, children.length);
					for (int i = prevLen; i < childCache.length; i++) {
						childCache[i] = new RSInterfaceChild(this, i);
					}
				}
				return childCache.clone();
			}
		}
	}

	/**
	 * Safely gets the array of children.
	 * 
	 * @return The child interfaces of the client.
	 * */
	org.rsbot.accessors.RSInterface[] getChildrenInternal() {
		final Client c = Bot.getClient();
		if (c == null)
			return null;
		final org.rsbot.accessors.RSInterface[][] inters = c.getRSInterfaceCache();
		if ((inters != null) && (index < inters.length))
			return inters[index];
		return null;
	}

	/**
	 * @return The index of this interface.
	 * */
	public int getIndex() {
		return index;
	}

	/**
	 * Get's the location of the interface
	 * 
	 * @return the exact location of the interface, return (-1, -1) if interface
	 *         was null
	 */
	public Point getLocation() {
		final org.rsbot.accessors.RSInterface[] children = getChildrenInternal();
		if (children != null) {
			for (final org.rsbot.accessors.RSInterface child : children) {
				if (child != null) {
					if ((child.getMasterX() != -1) && (child.getMasterY() != -1))
						return new Point(child.getMasterX(), child.getMasterY());
				}
			}
		}
		return new Point(-1, -1);
	}

	/**
	 * Finds all the text in it, searches all his children for it.
	 * 
	 * @return all the text found seperated by newlines, empty if null
	 */
	public String getText() {
		final StringBuilder sb = new StringBuilder();
		final org.rsbot.accessors.RSInterface[] children = getChildrenInternal();
		if (children != null) {
			for (final org.rsbot.accessors.RSInterface child : children) {
				String string;
				if ((child != null) && ((string = child.getText()) != null)) {
					sb.append(string);
					sb.append("\r\n");
				}
			}
		}
		if (sb.length() > 2) {
			sb.setLength(sb.length() - 2);
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return index;
	}

	/**
	 * Checks whether or not the interface is equal to null
	 * 
	 * @return true if the interface == null
	 */
	@Deprecated
	public boolean isNull() {
		return !isValid();
	}

	/**
	 * Checks whether or not the interface is valid or not
	 * 
	 * @return true if its valid
	 */
	public boolean isValid() {
		// everything is thread hot so make sure you copy pointers to it
		if (getChildrenInternal() == null)
			return false;
		final int idx = getIndex();
		final boolean[] validArray = Bot.getClient().getValidRSInterfaceArray();
		return (validArray != null) && (idx < validArray.length) && validArray[idx];
	}

	/**
	 * Iterated over the children of the interface. Will never return null even
	 * if the underlying interface is null.
	 */
	public Iterator<RSInterfaceChild> iterator() {
		return new Iterator<RSInterfaceChild>() {
			private int nextIdx = 0;

			public boolean hasNext() {
				return !isNull() && (getChildCount() >= nextIdx);
			}

			public RSInterfaceChild next() {
				final RSInterfaceChild child = getChild(nextIdx);
				nextIdx++;
				return child;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	void setChild(final RSInterfaceChild child) {
		synchronized (childLock) { // safe that the index isn't execisve since
			// it comes from child
			final int idx = child.getIndex();
			if (idx >= childCache.length) {
				getChild(idx);
				childCache[idx] = child;
			}
		}
	}
}
