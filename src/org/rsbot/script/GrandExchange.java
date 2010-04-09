package org.rsbot.script;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 
 * Obtains information on tradeable items from the Grand Exchange website.
 * 
 * This class should never be used directly by the end-user or scripter, it
 * should instead be used in the script via the already instantiated Grand
 * Exchange object declared in Methods via grandExchange.loadItemInfo();
 * 
 * @author Aelin
 */
public class GrandExchange {

	/**
	 * This method loads a item's info from the grand exchange website.
	 * 
	 * @param itemID
	 *            Item to load
	 * @return GEItemInfo containing item information
	 */
	public GEItemInfo loadItemInfo(final int itemID) {
		int minPrice = 0;
		int maxPrice = 0;
		int marketPrice = 0;
		String changeSeven = "";
		String changeThirty = "";

		try {
			final URL url = new URL("http://services.runescape.com/m=itemdb_rs/viewitem.ws?obj=" + itemID);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			String line;

			while ((line = reader.readLine()) != null) {
				if (line.contains("Minimum price:")) {
					line = line.replace("<b>Minimum price:</b> ", "");
					line = line.replace(",", "");
					if (line.contains("k")) {
						line = line.replace(".", "");
						line = line.replace("k", "");
						line = line.trim();
						minPrice = Integer.parseInt(line) * 100;
					} else if (line.contains("m")) {
						line = line.replace(".", "");
						line = line.replace("m", "");
						line = line.trim();
						minPrice = Integer.parseInt(line) * 100000;
					} else {
						minPrice = Integer.parseInt(line);
					}
				}

				if (line.contains("Market price:")) {
					line = line.replace("<b>Market price:</b> ", "");
					line = line.replace(",", "");
					if (line.contains("k")) {
						line = line.replace(".", "");
						line = line.replace("k", "");
						line = line.trim();
						marketPrice = Integer.parseInt(line) * 100;
					} else if (line.contains("m")) {
						line = line.replace(".", "");
						line = line.replace("m", "");
						line = line.trim();
						marketPrice = Integer.parseInt(line) * 100000;
					} else {
						marketPrice = Integer.parseInt(line);
					}
				}

				if (line.contains("Maximum price:")) {
					line = line.replace("<b>Maximum price:</b> ", "");
					line = line.replace(",", "");
					if (line.contains("k")) {
						line = line.replace(".", "");
						line = line.replace("k", "");
						line = line.trim();
						maxPrice = Integer.parseInt(line) * 100;
					} else if (line.contains("m")) {
						line = line.replace(".", "");
						line = line.replace("m", "");
						line = line.trim();
						maxPrice = Integer.parseInt(line) * 100000;
					} else {
						maxPrice = Integer.parseInt(line);
					}
				}

				if (line.contains("7 Days:")) {
					line = line.replace("<b>7 Days:</b> <span class=\"stay\">", "");
					line = line.replace("<b>7 Days:</b> <span class=\"stay\">", "");
					line = line.replace("</span>", "");
					line = line.trim();
					changeSeven = line;
				}

				if (line.contains("30 Days:")) {
					line = line.replace("<b>30 Days:</b> <span class=\"stay\">", "");
					line = line.replace("<b>30 Days:</b> <span class=\"stay\">", "");
					line = line.replace("</span>", "");
					line = line.trim();
					changeThirty = line;
				}
			}
		} catch (final Exception ignored) {
		}

		return new GEItemInfo(itemID, minPrice, maxPrice, marketPrice, changeSeven, changeThirty);
	}
}
