package com.mattc.autotyper.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.mattc.autotyper.gui.SingleStringProcessor;
import com.mattc.autotyper.util.OS.MemoryUnit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * A Collection of Utilities dedicated to the try-catch mess that is java.io. We
 * ignore java.nio since this application tries to maintain compatibility with at
 * least Java SE 1.6. <br />
 * <br />
 * Most of these methods are general use.
 * 
 * @author Matthew
 *
 */
public class IOUtils {

	/**
	 * Number of Seconds between Log Messages relating to Download Rate and State.
	 */
	public static final int DOWNLOAD_LOG_DELAY = 5;

	/**
	 * Default Buffer Size for any IOUtils created Byte[] or Char[] buffers. <br />
	 * By default this is 4096. Java's Default is 8192.
	 * 
	 * @see java.io.BufferedReader
	 */
	public static final int DEFAULT_BUFFER_SIZE = 4096;

	/**
	 * Send Properties to a File from a Properties object
	 * 
	 * @param from
	 * @param to
	 * @param comments
	 */
	public static final void saveProperties(Properties from, File to, String comments) {
		Preconditions.checkNotNull(from, "Properties is Null!");
		Preconditions.checkNotNull(to, "File to Save to is Null!");

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(to, false);
			from.store(fos, comments);
		} catch (final IOException e) {
			Console.exception(e);
		} finally {
			closeSilently(fos);
		}
	}

	/**
	 * Load Properties from a File into a Properties object.
	 * 
	 * @param to
	 * @param from
	 */
	public static final void loadProperties(Properties to, File from) {
		Preconditions.checkNotNull(to, "Properties is Null!");
		Preconditions.checkNotNull(from, "File to Load From is Null!");

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(from);
			to.load(fis);
		} catch (final IOException e) {
			Console.exception(e);
		} finally {
			closeSilently(fis);
		}
	}

	/**
	 * Loads Properties from a Properties File and stores them in a Properties object <br />
	 * which is then returned.
	 * 
	 * @param file
	 * @return Properties Object
	 */
	public static final Properties getPropertiesFromFile(File file) {
		Preconditions.checkNotNull(file, "File Can't Be Null!");
		Preconditions.checkArgument(file.exists(), "File Does Not Exist! - " + file.getPath());

		Properties properties = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			properties = new Properties();
			properties.load(fis);
		} catch (final IOException e) {
			Console.exception(e);
		} finally {
			closeSilently(fis);
		}

		return properties;
	}

	/**
	 * Write a String to a File.
	 * 
	 * @param file
	 * @param string
	 */
	public static final void write(File file, String string) {
		try {
			final FileWriter writer = new FileWriter(file);
			final StringReader reader = new StringReader(string);
			write(reader, writer, true);
		} catch (final IOException e) {
			Console.exception(e);
		}
	}

	// -- START READER->WRITER METHODS --//

	/**
	 * Writer data from Reader to Writer. Do Not Close Streams when Done!<br />
	 * <br />
	 * <b><i><u>WILL NOT CLOSE STREAMS BY DEFAULT!</u></i></b>
	 * 
	 * @param reader
	 * @param writer
	 */
	public static final void write(Reader reader, Writer writer) {
		write(reader, writer, false);
	}

	/**
	 * Write data from Reader to Writer.
	 * 
	 * @param reader
	 * @param writer
	 * @param closeStreams
	 *            - Close Streams when Finished?
	 */
	public static final void write(Reader reader, Writer writer, boolean closeStreams) {
		write(reader, closeStreams, writer, closeStreams);
	}

	/**
	 * Completely write all data from a Reader object into the given Writer object. <br />
	 * Can close writer and reader to help clean up try-catch blocks.
	 * 
	 * @param reader
	 *            - Reader Object to Read from
	 * @param closeReader
	 *            - Close Reader when done?
	 * @param writer
	 *            - Writer Object to Write to
	 * @param closeWriter
	 *            - Close Writer when done?
	 */
	public static final void write(Reader reader, boolean closeReader, Writer writer, boolean closeWriter) {
		if (closeReader && closeWriter) {
			write(reader, writer, true);
		} else {
			try {
				doWrite(reader, writer);
			} catch (final IOException e) {
				Console.exception(e);
			} finally {
				if (closeReader) {
					closeSilently(reader);
				}
				if (closeWriter) {
					closeSilently(writer);
				}
			}
		}
	}

	// -- END READER->WRITER METHODS -- //

	// -- START InputStream->OutputStream METHODS -- //

	/**
	 * Writer data from Reader to Writer. Do Not Close Streams when Done!<br />
	 * <br />
	 * <b><i><u>WILL NOT CLOSE STREAMS BY DEFAULT!</u></i></b>
	 * 
	 * @param input
	 * @param output
	 */
	public static final void write(InputStream input, OutputStream output) {
		write(input, output, false);
	}

	/**
	 * Write data from Reader to Writer.
	 * 
	 * @param input
	 * @param output
	 * @param closeStreams
	 *            - Close Streams when Finished?
	 */
	public static final void write(InputStream input, OutputStream output, boolean closeStreams) {
		write(input, closeStreams, output, closeStreams);
	}

	/**
	 * Completely write all data from an InputStream into the given OutputStream. <br />
	 * Can close writer and reader to help clean up try-catch blocks.
	 * 
	 * @param input
	 *            - Input
	 * @param closeInputStream
	 *            - Close InputStream when done?
	 * @param output
	 *            - Output
	 * @param closeOutputStream
	 *            - Close OutputStream when done?
	 */
	public static final void write(InputStream input, boolean closeInputStream, OutputStream output, boolean closeOutputStream) {
		if (closeInputStream && closeOutputStream) {
			write(input, output, true);
		} else {
			try {
				doStreamWrite(input, output);
			} catch (final IOException e) {
				Console.exception(e);
			} finally {
				if (closeInputStream) {
					closeSilently(input);
				}
				if (closeOutputStream) {
					closeSilently(output);
				}
			}
		}
	}

	// -- END InputStream->OutputStream METHODS -- //

	// Handles Simply Moving Data from Reader to Writer using a Char Buffer
	private static final void doWrite(Reader reader, Writer writer) throws IOException {
		final char[] cbuf = new char[DEFAULT_BUFFER_SIZE];
		for (int c = reader.read(cbuf); c != -1; c = reader.read(cbuf)) {
			writer.write(cbuf, 0, c);
		}
	}

	// Handles Simply Moving Data from InputStream to OutputStream using a Byte
	// Buffer
	private static final void doStreamWrite(InputStream is, OutputStream os) throws IOException {
		final byte[] b = new byte[DEFAULT_BUFFER_SIZE];
		for (int c = is.read(b); c != -1; c = is.read(b)) {
			os.write(b, 0, c);
		}
	}

	private static final Splitter endLineSplitter = Splitter.onPattern("\r?\n");

	/**
	 * Split a String into a String Iterable made up of it's individual lines.
	 * 
	 * @param source
	 * @return
	 */
	public static final Iterable<String> getLines(String source) {
		return endLineSplitter.split(source);
	}

	/**
	 * Read in the entirety of a file and return an Iterable made up of all of <br />
	 * the individual lines.
	 * 
	 * @param source
	 * @return
	 */
	public static final Iterable<String> getLines(File source) {
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		String result = null;

		try {
			final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
			fis = new FileInputStream(source);
			bos = new ByteArrayOutputStream();

			for (int c = fis.read(buf); c != -1; c = fis.read(buf)) {
				bos.write(buf, 0, c);
			}

			result = bos.toString(Charsets.UTF_16.displayName());
		} catch (final IOException e) {
			Console.exception(e);
		} finally {
			IOUtils.closeSilently(fis);
			IOUtils.closeSilently(bos);
		}

		if (result == null) throw new IllegalStateException("Failed to Parse Lines of File!");

		return getLines(result);
	}

	/**
	 * Recursively Empties then Deletes a Folder. <br />
	 * <br />
	 * The boolean this method returns depends on whether or not the Directory passed
	 * is deleted. <br />
	 * If the directory can not be deleted then it returns false, though some of its
	 * contents may have been deleted anyway. <br />
	 * 
	 * @param directory
	 * @return Whether or Not the Directory passed was Deleted
	 */
	public static final boolean deleteDirectory(File directory) {
		if (!directory.isDirectory()) throw new IllegalArgumentException(directory.getAbsolutePath() + " is NOT a Directory!");

		final File[] entries = directory.listFiles();

		for (final File file : entries) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				file.delete();
			}
		}

		return directory.delete();
	}

	/**
	 * Copy Src File to Dest, if Dest Exists it will be Overwritten
	 * 
	 * @param src
	 * @param dest
	 * @return Success/Failure
	 */
	public static final boolean copyFile(File src, File dest) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		boolean success = true;

		if (!src.exists()) throw new IllegalStateException("Source File does Not Exist!");
		if (!dest.exists()) {
			try {
				dest.getParentFile().mkdirs();	// Ensure Trunk of Path Exists
				dest.createNewFile();			// Create File
			} catch (final IOException e) {
				Console.exception(e);
			}
		}

		try {
			final byte[] buffer = new byte[8192];
			fis = new FileInputStream(src);
			fos = new FileOutputStream(dest);

			for (int c = fis.read(buffer); c > 0; c = fis.read(buffer)) {
				fos.write(buffer, 0, c);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			success = false;
		} finally {
			closeSilently(fis);
			closeSilently(fos);
		}

		return success;
	}

	public static final String getSuffix(File file) {
		final String name = file.getName();

		if (!name.contains("."))
			return "";
		else
			return name.substring(name.lastIndexOf('.') + 1);
	}

	/**
	 * Closes any Closeable, meant for Streams to clean up finally blocks.
	 * 
	 * @param stream
	 */
	public static final void closeSilently(Closeable stream) {
		if (stream == null) return;

		try {
			stream.close();
		} catch (final IOException e) {
		}
	}

	public static File copyTo(File f, File archivesDir) throws IOException {
		final File target = new File(archivesDir, f.getName());

		Files.copy(f, target);
		return target;
	}

	/**
	 * Download file found at the given URL and store it in File at filename.
	 * 
	 * @param url
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static final File download(URL url, String filename) throws IOException {

		final File temp = File.createTempFile(filename, "cctemp");

		BufferedInputStream bis = null;
		FileOutputStream fos = null;

		temp.createNewFile();	// Create Temporary File
		temp.deleteOnExit();	// Delete Temporary File Once Finished

		try {
			Console.info("Downloading " + filename + " from " + url + "...");
			bis = new BufferedInputStream(url.openStream());
			fos = new FileOutputStream(temp);
			final MemoryUnit unit = MemoryUnit.KILOBYTES;
			final byte[] buf = new byte[8192];
			long curTime = System.currentTimeMillis();
			long tarTime = System.currentTimeMillis() + (IOUtils.DOWNLOAD_LOG_DELAY * 1000);
			long total = 0;
			int accumulator = 0;
			int c = 0;

			for (c = bis.read(buf); c != -1; c = bis.read(buf)) {
				fos.write(buf, 0, c);
				accumulator += c;

				if ((curTime = System.currentTimeMillis()) >= tarTime) {
					// Inform of Bytes read every DOWNLOAD_LOG_DELAY seconds
					// Inform of B/s every DOWNLOAD_LOG_DELAY seconds
					total += accumulator;
					final long totalAdj = MemoryUnit.MEGABYTES.convert(total, MemoryUnit.BYTES);
					final long accAdj = unit.convert(accumulator, MemoryUnit.BYTES);
					final long rateAdj = unit.convert(Math.round((accumulator / (5.0d + (((double) curTime - (double) tarTime) / 1000.0d)))), MemoryUnit.BYTES);
					final String msg = String.format("%,d KB (at %,d KB/s) read for %s, Total: %,d MB...", accAdj, rateAdj, filename, totalAdj);
					Console.info(msg);
					accumulator = 0;
					tarTime = System.currentTimeMillis() + 5000;
				}
			}

			Console.info("Finished!");
		} catch (final IOException e) {
			Console.exception(e);
			throw e;
		} finally {
			IOUtils.closeSilently(bis);
			IOUtils.closeSilently(fos);
		}

		return temp;
	}

	/**
	 * Small Utility to consume the InterruptedException associated wiht Thread.sleep
	 * 
	 * @param l
	 */
	public static void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (final InterruptedException e) {
		}
	}

	public static void printStackTrace(StackTraceElement[] stackTrace) {
		for (int i = 2; i < stackTrace.length; i++) {
			Console.warn(stackTrace[i]);
		}
	}

	public static String fileToString(File file, Charset charset) throws IOException {
		return Files.readLines(file, charset, new SingleStringProcessor());
	}

	public static String fileToString(File file) throws IOException {
		return fileToString(file, Charset.defaultCharset());
	}

	public static boolean checkConnectionSuccess(URL url) throws IOException {
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("HEAD");

		final int responseCode = conn.getResponseCode();
		conn.disconnect();

		return responseCode == 200;
	}

	public static boolean checkConnectionSuccess(String url) throws IOException {
		return checkConnectionSuccess(new URL(url));
	}

}
