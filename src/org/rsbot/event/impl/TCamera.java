package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TCamera extends Methods implements TextPaintListener {
	public final static TCamera inst = new TCamera();

	private TCamera() {
	}

	public int drawLine(final Graphics render, int idx) {
		final String camPos = "Camera Position (x,y,z): (" + Bot.getClient().getCamPosX() + ", " + Bot.getClient().getCamPosY() + ", " + Bot.getClient().getCamPosZ() + ")";
		final String camAngle = "Camera Angle (pitch, yaw): (" + Bot.getClient().getCameraPitch() + ", " + Bot.getClient().getCameraYaw() + ")";
		//final String detailLvl = "Detail lvl: " + (Bot.getClient().getDetailInfo() != null ? Bot.getClient().getDetailInfo().getDetailLevel() : "null");

		Methods.drawLine(render, idx++, camPos);
		Methods.drawLine(render, idx++, camAngle);
		//Methods.drawLine(render, idx++, detailLvl);
		return idx;
	}
}
