package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.listeners.PaintListener;

public class DrawMouse implements PaintListener {
	public final static DrawMouse inst = new DrawMouse();

	private DrawMouse() {
	}

	public void onRepaint(final Graphics render) {
		final Mouse mouse = Bot.getClient().getMouse();
		if (mouse == null)
			return;

		final int mouse_x = mouse.getMouseX();
		final int mouse_y = mouse.getMouseY();
		final int mouse_press_x = mouse.getMousePressX();
		final int mouse_press_y = mouse.getMousePressY();
		// int mouse_press_button = client.getMousePressButton();
		final long mouse_press_time = mouse.getMousePressTime();

		render.setColor(Color.YELLOW);
		render.drawLine(mouse_x - 7, mouse_y - 7, mouse_x + 7, mouse_y + 7);
		render.drawLine(mouse_x + 7, mouse_y - 7, mouse_x - 7, mouse_y + 7);
		if (System.currentTimeMillis() - mouse_press_time < 1000) {
			render.setColor(Color.RED);
			render.drawLine(mouse_press_x - 7, mouse_press_y - 7, mouse_press_x + 7, mouse_press_y + 7);
			render.drawLine(mouse_press_x + 7, mouse_press_y - 7, mouse_press_x - 7, mouse_press_y + 7);
		}

		if (mouse.present) {
			render.setColor(mouse.pressed ? Color.WHITE : Color.BLACK);
			render.drawLine(mouse.x - 7, mouse.y - 7, mouse.x + 7, mouse.y + 7);
			render.drawLine(mouse.x + 7, mouse.y - 7, mouse.x - 7, mouse.y + 7);
		}
	}
}
