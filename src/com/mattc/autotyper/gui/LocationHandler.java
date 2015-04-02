package com.mattc.autotyper.gui;

import com.mattc.autotyper.Downloader;
import com.mattc.autotyper.meta.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * Handles downloading and obtaining files from different locations on different
 * formats. Including Web, Pastebin and URL. Also can Auto-Detect based on a string
 * using the {@link #canHandle(String) canHandle} method in each LocationHandler.
 * 
 * @author Matthew
 */
@FXCompatible
@SwingCompatible
public enum LocationHandler {

	FILE ("Local File:") {
		@Override
		public File handle(String text) {
			final File f = new File(text);
			return f.exists() ? f : null;
		}

		@Override
		public Outcome canHandle(String text) {
			final File f = new File(text);
			final String reason = String.format("File not found at '%s'!", text);

			if (f.exists())
				return Outcome.createSuccess();
			else
				return new Outcome(this, reason, false, new FileNotFoundException(reason));
		}
	},
	URL ("Web URL:") {
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
		public Outcome canHandle(String text) {
			try {
				final java.net.URL url = new java.net.URL(text);
				final HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("HEAD");

				final int response = con.getResponseCode();
				con.disconnect();

				if (response == 200)
					return Outcome.createSuccess();
				else
					throw new IOException("Web Response was not 200 (Success). It was " + response + "!");
			} catch (final MalformedURLException e) {
				return new Outcome(this, text + " is an invalid URL!", false, e);
			} catch (final IOException e) {
				return new Outcome(this, "Connection Failure: " + text, false, e);
			}
		}
	},
	PASTEBIN ("Pastebin:"){
		@Override
		public File handle(String text) {
			return Downloader.getPastebin(text);
		}

		@Override
		public Outcome canHandle(String text) {
			final String urlStr = String.format(Downloader.PASTEBIN_URL, text);
			try {
				final java.net.URL url = new java.net.URL(urlStr);
				final HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("HEAD");

				final int response = con.getResponseCode();
				con.disconnect();

				if (response == 200)
					return Outcome.createSuccess();
				else
					throw new IOException("Web Response was not 200 (Success). It was " + response + "!");
			} catch (final MalformedURLException e) {
				return new Outcome(this, "Bad URL: " + urlStr, false, e);
			} catch (final IOException e) {
				return new Outcome(this, "Connection Failure: " + urlStr, false, e);
			}
		}
	};

    private final String tag;

    private LocationHandler(String tag) {
        this.tag = tag;
    }

	/**
	 * Take Location and Obtain File
	 * 
	 * @param text
	 * @return
	 */
	abstract public File handle(String text);

	/**
	 * Take Location and determine if it can be handled by this LocationHandler.
	 * Return an {@link com.mattc.autotyper.meta.Outcome} object as appropriate.
	 * 
	 * @param text
	 * @return
	 */
	abstract public Outcome canHandle(String text);

    public String tag() {
        return tag;
    }

	/**
	 * Auto-Detect the LocationHandler to use by using the {@link #canHandle(String)
	 * canHandle} method. Exceptions may occur.
	 * 
	 * @param text
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	public static LocationHandler detect(String text, ExceptionHandler handler) throws Exception {
		for (final LocationHandler lh : LocationHandler.values()) {

            if(text.startsWith(lh.tag))
                return lh;

			final Outcome out = lh.canHandle(text);

			if (out.isSuccess())
				return lh;
			else if (out.recommendedException != null) {
				handler.handle(out.recommendedException);
			}
		}

		final Exception e = new ImpossibleInputException("The Location '" + text + "' could not be auto-detected!");
		handler.handle(e);
		throw e;
	}

	/**
	 * Takes an object and determines the LocationHandler to use using the
	 * {@link #canHandle(String) canHandle} method. <br />
	 * this differs from the {@link #detect(String, ExceptionHandler) other} version
	 * of this method by defaulting to an {@link ExceptionHandler} that just consumes
	 * Exceptions.
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static LocationHandler detect(String text) throws Exception {
		return detect(text, ExceptionHandler.NULL_HANDLER);
	}

}
