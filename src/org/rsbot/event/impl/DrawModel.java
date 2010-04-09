package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.accessors.LDModel;
import org.rsbot.accessors.Model;
import org.rsbot.accessors.RSAnimable;
import org.rsbot.accessors.RSInteractableDef;
import org.rsbot.accessors.RSObject;
import org.rsbot.accessors.StatusNode;
import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.internal.StatusNodeList;

//Draws a nice wire frame on interactable objects on screen.
//Thanks to Kosaki for his initial idea and part of this code:)
public class DrawModel extends Methods implements PaintListener {
	public static final DrawModel inst = new DrawModel();

	public void onRepaint(Graphics render) {
		StatusNodeList snl = new StatusNodeList(Bot.getClient().getRSInteractingDefList());
		for(StatusNode sn = snl.getFirst(); sn != null; sn = snl.getNext())
		{
			//See if it's an RSInteractableDef, since users reported it sometimes failed.
			//In my opinion that shouldn't be possible, unless the list doesn't work properly.
			//However better check then spitting out errors.
			if(!(sn instanceof RSInteractableDef))
				continue;
			
			RSInteractableDef cur = (RSInteractableDef) sn;
			if(cur.getRSInteractable() instanceof RSObject && cur.getRSInteractable() instanceof RSAnimable)
			{
				render.setColor(Color.YELLOW);
				
				RSObject object = (RSObject) cur.getRSInteractable();
				RSAnimable animable = (RSAnimable) object;
				Model model;
				try	{ model = object.getModel(); }catch(AbstractMethodError e){ continue; }
				if(model == null || !(model instanceof LDModel))
					continue;
				
				//Calculate screen coords of the model
				Point[] screenCoords = new Point[model.getXPoints().length];
				for(int i = 0; i < screenCoords.length; i++)
				{
					int x = model.getXPoints()[i] + animable.getX();
					int z = model.getZPoints()[i] + animable.getY();
					int y = model.getYPoints()[i] + Calculations.tileHeight(animable.getX(), animable.getY());
					screenCoords[i] = Calculations.w2s(x, y, z);
				}
				
				int[] xPoints = new int[4];
	            int[] yPoints = new int[4];

	            int length = ((LDModel) model).getIndices3().length;
	            for (int i = 0; i < length; i++) {
	            	int index1 = ((LDModel) model).getIndices1()[i];
	                if(screenCoords[index1].x == -1 || screenCoords[index1].y == -1)
	                	continue;

	                xPoints[0] = screenCoords[index1].x;
	                yPoints[0] = screenCoords[index1].y;
	                xPoints[3] = screenCoords[index1].x;
	                yPoints[3] = screenCoords[index1].y;

	                int index2 = ((LDModel) model).getIndices2()[i];
	                if(screenCoords[index2].x == -1 || screenCoords[index2].y == -1)
	                	continue;
	                
	                xPoints[1] = screenCoords[index2].x;
	                yPoints[1] = screenCoords[index2].y;

	                int index3 = ((LDModel) model).getIndices3()[i];
	                if(screenCoords[index3].x == -1 || screenCoords[index3].y == -1)
	                	continue;

	                xPoints[2] = screenCoords[index3].x;
	                yPoints[2] = screenCoords[index3].y;
	                
	                render.drawPolyline(xPoints, yPoints, 4);
	            }
			}
		}
	}
}
