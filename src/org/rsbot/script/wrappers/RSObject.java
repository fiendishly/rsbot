package org.rsbot.script.wrappers;

import org.rsbot.script.Calculations;

public class RSObject {
	final org.rsbot.accessors.RSObject obj;
	int x, y, type;

	public RSObject(final org.rsbot.accessors.RSObject obj, final int x, final int y, final int type) {
		this.obj = obj;
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public RSObjectDef getDef() {
		return new RSObjectDef((org.rsbot.accessors.RSObjectDef) Calculations.findNodeByID(getID()));
	}

	public int getID() {
		return obj.getID();
	}
	
	public org.rsbot.accessors.Model getModel()
	{
		try
		{
			return obj.getModel(); 
		}
		catch(AbstractMethodError e) //Not implemented yet on all types of objects
		{ 
			return null;
		}
	}
	
	public org.rsbot.accessors.RSObject getObject()
	{
		return this.obj;
	}

	public RSTile getLocation() {
		return new RSTile(x, y);
	}

	/**
	 * Returns a number between 0 and 4.</br> 0: Interactable object like trees
	 * 1: Ground decorations 2: Fences / walls 3: Unknown 4: Unknown
	 */
	public int getType() {
		return type;
	}

}
