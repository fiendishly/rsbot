/**
 * 
 */
package org.rsbot.script.wrappers;

import java.awt.Point;

/**
 * This class handles the components of an interface. <br>
 * Can be used for banking/shops etc.
 * 
 * @author Qauters
 */
public class RSInterfaceComponent extends RSInterfaceChild {
	protected int componentIndex;

	/**
	 * @param parent
	 * @param index
	 * @param componentIndex
	 */
	public RSInterfaceComponent(final RSInterface parent, final int index, final int componentIndex) {
		super(parent, index);
		this.componentIndex = componentIndex;
	}

	/**
	 * The ID of this component
	 * 
	 * @return The ID of this component, or -1 if component == null
	 */
	@Override
	public int getComponentID() {
		final org.rsbot.accessors.RSInterface component = getInterfaceInternal();
		if (component != null)
			return component.getComponentID();

		return -1;
	}

	/**
	 * The index of this component
	 * 
	 * @return The index of this component, or -1 if component == null
	 */
	public int getComponentIndex() {
		final org.rsbot.accessors.RSInterface component = getInterfaceInternal();
		if (component != null)
			return component.getComponentIndex();

		return -1;
	}

	/**
	 * The name of this component
	 * 
	 * @return The name of this component, or "" if component == null
	 */
	public String getComponentName() {
		final org.rsbot.accessors.RSInterface component = getInterfaceInternal();
		if (component != null)
			return component.getComponentName();

		return "";
	}

	/**
	 * The stack size of this component
	 * 
	 * @return The stack size of this component, or -1 if component == null
	 */
	public int getComponentStackSize() {
		final org.rsbot.accessors.RSInterface component = getInterfaceInternal();
		if (component != null)
			return component.getComponentStackSize();

		return -1;
	}

	/**
	 * Gets the client interface represented by this object.
	 * 
	 * @return The interface represented by this object.
	 */
	@Override
	org.rsbot.accessors.RSInterface getInterfaceInternal() {
		final org.rsbot.accessors.RSInterface child = super.getInterfaceInternal();
		if (child != null) {
			final org.rsbot.accessors.RSInterface[] components = child.getComponents();
			if ((components != null) && (componentIndex >= 0) && (componentIndex < components.length))
				return components[componentIndex];
		}

		return null;
	}

	/**
	 * Returns the center point of the Component. Written by Fusion89k.
	 * 
	 * @return The center point of the Component
	 */
	public Point getPoint() {
		return new Point(getAbsoluteX() + getWidth() / 2, getAbsoluteY() + getHeight() / 2);
	}
}
