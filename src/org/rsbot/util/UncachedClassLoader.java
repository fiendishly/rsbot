package org.rsbot.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UncachedClassLoader extends ClassLoader {
	private final String url;

	public UncachedClassLoader(final String url, final ClassLoader parent) {
		super(parent);
		this.url = url;
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		try {
			final URL myUrl = new URL(url + name.substring(name.lastIndexOf('.') + 1) + ".class");
			final URLConnection connection = myUrl.openConnection();
			final InputStream input = connection.getInputStream();
			final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = input.read();

			while (data != -1) {
				buffer.write(data);
				data = input.read();
			}

			input.close();

			final byte[] classData = buffer.toByteArray();

			return defineClass(name, classData, 0, classData.length);

		} catch (final MalformedURLException e) {
			super.loadClass(name);
		} catch (final IOException e) {
			super.loadClass(name);
		}

		return super.loadClass(name);
	}
}
