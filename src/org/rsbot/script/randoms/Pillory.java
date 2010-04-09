package org.rsbot.script.randoms;

import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Illusion + Pwnaz0r & Fix by Lowbird
 * @version 3.2 - 04/03/09
 * @version 3.3 - 06/03/09
 * @version 3.4 - 26/05/09 Fix by Lowbird
 * @version 3.5 - 16/01/10 Fix by Pervy Shuya
 * @version 3.6 - 30/01/10 Fix by Iscream
 * @version 3.7 - 08/02/10 Update by Iscream
 */
@ScriptManifest(authors = { "illusion", "Pwnaz0r" }, name = "Pillory", version = 3.7)
public class Pillory extends Random implements ServerMessageListener {

	public int fail = 0;
	private final RSInterface GameInterface = RSInterface.getInterface(189);
	public boolean inCage = false;
	public int[] myLoc;
	private String pilloryMessage = "Solve the Pillory";

	public RSTile South = new RSTile(2606, 3105);
	RSTile cagetiles[] = {new RSTile(2608,3105),new RSTile(2606,3105),new RSTile(2604,3105),new RSTile(3226,3407),new RSTile(3228,3407),new RSTile(3230,3407),new RSTile(2685,3489),new RSTile(2683,3489),new RSTile(2681,3489)};
	@Override
	public boolean activateCondition() {
		    if (!isLoggedIn()) {
      return false;
    }
		    for (int i = 0; i < cagetiles.length; i++) {
		        if (getMyPlayer().getLocation().equals(cagetiles[i])) {
		          return true;
		        }
		      }
    if (inCage != true) {
      inCage = getInterface(372, 3).getText().contains(
          "Solve the pillory");
    }
    if (inCage != true) {
      inCage = getInterface(372, 3).getText().contains(
          "swinging");
    }
    return inCage;
	}

	public RSObject findMYObject(final int... ids) {
		// Changed to find the nearest, reachable!
		// fixed, lol, getObjectAt want a real xy not this one
		RSObject cur = null;
		int dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final RSObject o = getObjectAt(x + Bot.getClient().getBaseX(), y + Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (final int id : ids) {
						if (o.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						final int distTmp = distanceTo(o.getLocation());
						if (distTmp != -1) {
							if (cur == null) {
								dist = distTmp;
								cur = o;
							} else if (distTmp < dist) {
								cur = o;
								dist = distTmp;
							}
						}
					}
				}
			}
		}
		return cur;
	}

	private int getKey() {
		int key = 0;
		log.info("\tKey needed :");
		switch (GameInterface.getChild(4).getModelID()) {
			case 9753:
				key = 9749;
				log.info("\t   Diamond");
				break;
			case 9754:
				key = 9750;
				log.info("\t   Square");
				break;
			case 9755:
				key = 9751;
				log.info("\t   Circle");
				break;
			case 9756:
				key = 9752;
				log.info("\t   Triangle");
				break;
		}
		if (GameInterface.getChild(5).getModelID() == key)
			return 1;
		if (GameInterface.getChild(6).getModelID() == key)
			return 2;
		if (GameInterface.getChild(7).getModelID() == key)
			return 3;
		return -1;
	}

	

	@Override
	public int loop() {
		setCameraAltitude(true);
		if (distanceTo(South) <= 10) {
			setCameraRotation(180);
		} else {
			setCameraRotation(360);
		}
		if (fail > 20) {
			log.info("Failed Wayyy to many Times..");
			log.info("Report this to illusion so He can Fix the Pillory.");
			shutdown();
		}
		if (myLoc == null) {
			log.info("getting Location..");
			myLoc = new int[] { getMyPlayer().getLocation().getX(), getMyPlayer().getLocation().getY() };
			log.info(myLoc[0] + "," + myLoc[1]);
			return random(1000, 2000);
		}
		if ((getMyPlayer().getLocation().getX() != myLoc[0]) || (getMyPlayer().getLocation().getY() != myLoc[1])) {
			log.info(getMyPlayer().getLocation().toString());
			log.info("Solved It..");
			pilloryMessage = null;
			inCage = false;
			return -1;
		}
		if (!RSInterface.getInterface(189).isValid()) {
			final Point ObjectPoint = new Point(Calculations.tileToScreen(new RSTile(myLoc[0], myLoc[1])));
			final Point Lock = new Point((int) ObjectPoint.getX() + 10, (int) ObjectPoint.getY() - 30);
			clickMouse(Lock.x, Lock.y + random(0, 15), false);
			wait(random(600, 800));
			if (atMenu("unlock")) {
				log.info("Successfully opened the lock!");
				return random(1000, 2000);
			} else {
				fail++;
			}
		}
		if (RSInterface.getInterface(189).isValid()) {
			final int key = getKey();
			log.info(String.valueOf(key));
			switch (key) {
				case 1:			
					clickMouse(GameInterface.getChild(5).getArea().getLocation().x + random(10,13),GameInterface.getChild(5).getArea().getLocation().y + random(46,65),true);
					break;
				case 2:
					clickMouse(GameInterface.getChild(6).getArea().getLocation().x + random(10,13),GameInterface.getChild(6).getArea().getLocation().y + random(46,65),true);
					break;
				case 3:
					clickMouse(GameInterface.getChild(7).getArea().getLocation().x + random(10,13),GameInterface.getChild(7).getArea().getLocation().y + random(46,65),true);
					break;
				default:
					log.info("Bad Combo?");
					fail++;
					break;
			}
			return random(1000, 1600);
		}
		return -1;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String serverString = e.getMessage();
		if (serverString.contains(pilloryMessage)) {
			inCage = true;
		}

	}

	public void shutdown() {
		System.exit(1);
	}
}