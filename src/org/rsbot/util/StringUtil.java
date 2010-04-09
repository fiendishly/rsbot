package org.rsbot.util;

public class StringUtil {
	public static String Join(final String[] s) {
		final int l = s.length;
		switch (l) {
			case 0:
				return "";
			case 1:
				return s[0];
		}
		final String d = ", ";
		final int x = d.length();
		int n = 0, i;
		for (i = 0; i < l; i++) {
			n += s[i].length() + x;
		}
		final StringBuffer buf = new StringBuffer(n - x);
		i = 0;
		boolean c = true;
		while (c) {
			buf.append(s[i]);
			i++;
			c = i < l;
			if (c) {
				buf.append(d);
			}
		}
		return buf.toString();
	}
}
