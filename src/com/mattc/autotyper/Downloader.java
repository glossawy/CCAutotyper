package com.mattc.autotyper;

import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Convenience Utilities to handle downloading Files and Pastebins to Temp Files
 * 
 * @author Matthew
 *
 */
public class Downloader {

	public static final String PASTEBIN_URL = "http://www.pastebin.com/raw.php?i=%s";

	/**
	 * Retrieve raw text file from Pastebin using the given Paste Code.
	 * 
	 * @param pastebinCode
	 * @return Pastebin File as TempFile
	 */
	public static final File getPastebin(String pastebinCode) {
		return getFile(toURL(String.format(PASTEBIN_URL, pastebinCode)));
	}

	/**
	 * Download file from the given URL.
	 * 
	 * @param url
	 * @return File as TempFile
	 */
	public static final File getFile(URL url) {
		try {
			return IOUtils.download(url, url.getFile().contains("=") ? url.getFile().split("=")[1] : "cc-auto-file");
		} catch (final IOException e) {
			Console.exception(e);
		}

		return null;
	}

	/**
	 * Download file from the given URL
	 * 
	 * @param url
	 * @return File as TempFile
	 */
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
