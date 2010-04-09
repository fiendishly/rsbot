package org.rsbot.bot;

import org.rsbot.accessors.Client;
import org.rsbot.event.EventManager;
import org.rsbot.event.events.CharacterMovedEvent;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.InputManager;
import org.rsbot.script.ScriptHandler;

public class Bot {
	private static String account;
	private static BotStub botStub;
	private static Client client;
	private static EventManager eventManager;
	private static InputManager im;
	private static RSLoader loader;
	private static ScriptHandler sh;

	public static boolean disableRandoms = false;
	public static boolean disableBreakHandler = false;

	public static void characterMoved(final org.rsbot.accessors.RSCharacter c, final int i) {
		try {
			final CharacterMovedEvent e = new CharacterMovedEvent(c, i);
			Bot.eventManager.addToQueue(e);
		} catch (final Throwable e) { // protect rs
			e.printStackTrace();
		}
	}

	public static String getAccountName() {
		return Bot.account;
	}

	public static Client getClient() {
		return Bot.client;
	}

	public static EventManager getEventManager() {
		return Bot.eventManager;
	}

	public static InputManager getInputManager() {
		return Bot.im;
	}

	public static ScriptHandler getScriptHandler() {
		return Bot.sh;
	}

	public static void notifyServerMessage(final String s) {
		try {
			final ServerMessageEvent e = new ServerMessageEvent(s);
			Bot.eventManager.addToQueue(e);
		} catch (final Throwable e) { // protect rs
			e.printStackTrace();
		}
	}

	public static boolean setAccount(final String name) {
		boolean exist = false;
		for (final String s : AccountManager.getAccountNames()) {
			if (s.toLowerCase().equals(name.toLowerCase())) {
				exist = true;
			}
		}
		if (!exist)
			return false;
		Bot.account = name;
		return true;
	}

	// Constructor
	public Bot() {
		Bot.account = "";
		init();
	}

	public BotStub getBotStub() {
		return Bot.botStub;
	}

	public RSLoader getLoader() {
		return Bot.loader;
	}

	public void init() {
		Bot.im = new InputManager();
		Bot.loader = new RSLoader();
		Bot.botStub = new BotStub(Bot.loader);
		Bot.loader.setStub(Bot.botStub);
		Bot.loader.setCallback(new Runnable() {
			public void run() {
				setClient((Client) Bot.loader.getClient());
			}
		});
		Bot.sh = new ScriptHandler();
		Bot.eventManager = new EventManager();
		Bot.eventManager.start();
	}

	public void setClient(final Client cl) {
		Bot.client = cl;
		Bot.client.setCallback(new CallbackImpl(this));
	}

	public void startClient() {
		Bot.botStub.setActive(true);
		final ThreadGroup tg = new ThreadGroup("RSClient");
		final Thread thread = new Thread(tg, Bot.loader, "Loader");
		thread.start();
	}
}
