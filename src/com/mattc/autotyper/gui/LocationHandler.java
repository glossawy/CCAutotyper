package com.mattc.autotyper.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import com.mattc.autotyper.Downloader;

public enum LocationHandler {

	FILE {
		@Override
		public File handle(String text) {
			final File f = new File(text);
			return f.exists() ? f : null;
		}

		@Override
		public InformedOutcome canHandle(String text) {
			final File f = new File(text);
			final String reason = String.format("File not found at '%s'!", text);

			if (f.exists())
				return InformedOutcome.createSuccess();
			else
				return new InformedOutcome(this, reason, false, new FileNotFoundException(reason));
		}
	},
	URL {
		@Override
		public File handle(String text) {
			try {
				final java.net.URL url = new java.net.URL(text);
				return Downloader.getFile(url);
			} catch (final MalformedURLException e) {
				throw new RuntimeException(String.format("URL '%s' is invalid! Please use the canHandle Method before handle!", e));
			}
		}

		@Override
		public InformedOutcome canHandle(String text) {
			try {
				new java.net.URL(text);
				return InformedOutcome.createSuccess();
			} catch (final MalformedURLException e) {
				return new InformedOutcome(this, text + " is an invalid URL!", false, e);
			}
		}
	},
	PASTEBIN {
		@Override
		public File handle(String text) {
			return Downloader.getPastebin(text);
		}

		@Override
		public InformedOutcome canHandle(String text) {
			final String urlStr = String.format(Downloader.PASTEBIN_URL, text);
			try {
				final java.net.URL url = new java.net.URL(urlStr);
				final HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("HEAD");

				final int response = con.getResponseCode();

				if (response == 200)
					return InformedOutcome.createSuccess();
				else
					throw new IOException("Web Response was not 200 (Success). It was " + response + "!");
			} catch (final MalformedURLException e) {
				return new InformedOutcome(this, "Bad URL: " + urlStr, false, e);
			} catch (final IOException e) {
				return new InformedOutcome(this, "Connection Failure: " + urlStr, false, e);
			}
		}
	};

	public File handle(String text) {
		throw new AbstractMethodError();
	}

	public InformedOutcome canHandle(String text) {
		throw new AbstractMethodError();
	}

	public static LocationHandler detect(String text) throws Exception {
		for (final LocationHandler lh : LocationHandler.values()) {
			final InformedOutcome out = lh.canHandle(text);

			if (out.isSuccess()) return lh;
		}

		throw new ImpossibleInputException("The Location '" + text + "' could not be auto-detected!");
	}

}
