package com.mattc.autotyper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;

public class Downloader {

	public static final String PASTEBIN_URL = "http://www.pastebin.com/raw.php?i=%s";

	public static final File getPastebin(String pastebinCode) {
		return getFile(toURL(String.format(PASTEBIN_URL, pastebinCode)));
	}

	public static final File getFile(URL url) {
		try {
			return IOUtils.download(url, url.getFile().contains("=") ? url.getFile().split("=")[1] : "cc-auto-file");
		} catch (final IOException e) {
			Console.exception(e);
		}

		return null;
	}

	public static final File getFile(String url) {
		return getFile(toURL(url));
	}

	private static URL toURL(String url) {
		try {
			return new URL(url);
		} catch (final MalformedURLException e) {
			Console.exception(e);
		}

		return null;
	}

}
