package org.rsbot.script.randoms.antiban;

import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.util.GlobalConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

@ScriptManifest(authors = {"christel", "Sweed_Raver", "Taha", "regex", "pd", "sean"}, category = "AntiBan", name = "Break Handler", version = 3.0)
public class BreakHandler extends Random implements ServerMessageListener {
	protected class Break {
		private final long breakAtMax;
		private final long breakAtMin;
		private final long lengthMax;
		private final long lengthMin;
		private long randBreak = 0;

		Break(final long breakAtMin, final long breakAtMax, final long lengthMin, final long lengthMax) {
			this.breakAtMin = breakAtMin;
			this.breakAtMax = breakAtMax;
			this.lengthMin = lengthMin;
			this.lengthMax = lengthMax;
			randBreak = randBreakAt();
		}

		public long getBreakAtMax() {
			return breakAtMax;
		}

		public long getBreakAtMin() {
			return breakAtMin;
		}

		public long getLengthMax() {
			return lengthMax;
		}

		public long getLengthMin() {
			return lengthMin;
		}

		public long randBreakAt() {
			return randLong(breakAtMin, breakAtMax);
		}

		public long randLength() {
			return randLong(lengthMin, lengthMax);
		}

		private long randLong(final long min, final long max) {
			return min + (long) (java.lang.Math.random() * (max - min));
		}

		public boolean shouldBreak(final long startTime, final long curTime) {
			if (curTime - startTime > randBreak) {
				randBreak = randBreakAt();
				return true;
			} else
				return false;
		}
	}

	@Override
	public boolean isItemSelected() { // Credits to ByteCode
		for (final RSInterfaceComponent com : getInventoryInterface().getComponents()) {
			if (com.getBorderThickness() == 2)
				return true;
		}
		return false;
	}

	boolean tenSecondsWaiting = false;

	private final ArrayList<Break> breaks = new ArrayList<Break>();
	private Break curBreak;
	private long curTime = System.currentTimeMillis();
	private Iterator<Break> it;
	private boolean reset;
	private boolean setConfigs = true;
	private long startTime = System.currentTimeMillis();
	private File breaksFile = new File(GlobalConfiguration.Paths.getBreaksDirectory());
	private String[] props = new String[]{"15|45, 2|4", "75|105, 2|4", "135|165, 10|20", "205|235, 2|4", "265|295, 2|4", "330|360, 120|180", "540|570, 2|4", "600|630, 2|4", "660|690, 10|20", "750|780, 2|4", "810|840, 2|4", "900|960, 360|480"};

	@Override
	public boolean activateCondition() {
		if (getMyPlayer().isInCombat())
			return false;
		if (setConfigs) {
			getConfig();
			startTime = System.currentTimeMillis();
			setConfigs = false;
		}
		if (breaks.isEmpty())
			return false;
		if (reset) {
			it = breaks.iterator();
			startTime = System.currentTimeMillis();
			reset = false;
		}
		if (curBreak == null) {
			curBreak = it.next();
		}
		curTime = System.currentTimeMillis();
		return curBreak.shouldBreak(startTime, curTime);
	}

	private String cTime(long eTime) {
		final long hrs = eTime / 1000 / 3600;
		eTime -= hrs * 3600 * 1000;
		final long mins = eTime / 1000 / 60;
		eTime -= mins * 60 * 1000;
		final long secs = eTime / 1000;
		return String.format("%1$02d:%2$02d:%3$02d", hrs, mins, secs);
	}

	private String[] parseBreaks() {
		try {
			String[] temp = null;
			if (breaksFile.exists()) {
				final BufferedReader in = new BufferedReader(new FileReader(breaksFile));
				String line;
				while ((line = in.readLine()) != null) {
					if (line.contains(":")) {
						temp = line.split(":");
					}
				}
				in.close();
			}
			return temp;
		} catch (Exception e) {
			return null;
		}
	}

	public void getConfig() {
		if (!breaksFile.exists() || parseBreaks() == null || parseBreaks().length != 12) {
			try {
				if (breaksFile.createNewFile()) {
					final BufferedWriter out = new BufferedWriter(new FileWriter(breaksFile));
					for (int i = 0; i < props.length; i++) {
						out.write(props[i]);
						if (i + 1 < props.length) {
							out.write(":");
						}
					}
					out.close();
				}
			} catch (IOException ignored) {
			}
		} else {
			props = parseBreaks();
		}

		for (final String val : props) {
			final String breakVal = val.substring(0, val.indexOf(',')).trim();
			final String lengthVal = val.substring(val.indexOf(',') + 1).trim();
			long breakAtMin, breakAtMax, lengthMin, lengthMax;

			if (breakVal.indexOf('|') != -1) {
				breakAtMin = Long.parseLong(breakVal.substring(0, breakVal.indexOf('|')).trim());
				breakAtMax = Long.parseLong(breakVal.substring(breakVal.indexOf('|') + 1).trim());
			} else {
				breakAtMax = Long.parseLong(breakVal);
				breakAtMin = breakAtMax - breakAtMax / 4;
			}

			if (lengthVal.indexOf('|') != -1) {
				lengthMin = Long.parseLong(lengthVal.substring(0, lengthVal.indexOf('|')).trim());
				lengthMax = Long.parseLong(lengthVal.substring(lengthVal.indexOf('|') + 1).trim());
			} else {
				lengthMax = Long.parseLong(lengthVal);
				lengthMin = lengthMax / 2;
			}

			// convert to ms
			breakAtMin *= 60000;
			breakAtMax *= 60000;
			lengthMin *= 60000;
			lengthMax *= 60000;

			final Break b = new Break(breakAtMin, breakAtMax, lengthMin, lengthMax);
			breaks.add(b);
		}

		Collections.sort(breaks, new Comparator<Break>() {
			public int compare(final Break b1, final Break b2) {
				return (int) (b1.getBreakAtMin() - b2.getBreakAtMin());
			}
		});

		it = breaks.iterator();
	}

	@Override
	public int loop() {
		if (curBreak == null)
			return -1;
		final long breakLength = curBreak.randLength();
		log.info("Botted for: " + cTime(curTime - startTime) + "! Taking a break for: " + cTime(breakLength) + "!");
		do {
			for (int i = 0; tenSecondsWaiting && (i < 20); i++) {
				wait(random(500, 650));
				if (getMyPlayer().isInCombat()) {
					log("You were attacked while waiting for the ten seconds to pass. Returning to script.");
					return -1;
				}
			}
			logout();
			wait(random(2000, 4000));
		} while (isLoggedIn());
		curBreak = null;
		if (!it.hasNext()) {
			reset = true;
		}
		return (int) breakLength;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		if (e.getMessage().contains("10 seconds")) {
			tenSecondsWaiting = true;
		}
	}

	@Override
	public void onFinish() {
	}
}