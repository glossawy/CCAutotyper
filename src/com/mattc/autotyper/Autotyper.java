package com.mattc.autotyper;

import static com.google.common.base.StandardSystemProperty.JAVA_CLASS_PATH;
import static com.google.common.base.StandardSystemProperty.JAVA_CLASS_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_COMPILER;
import static com.google.common.base.StandardSystemProperty.JAVA_HOME;
import static com.google.common.base.StandardSystemProperty.JAVA_IO_TMPDIR;
import static com.google.common.base.StandardSystemProperty.JAVA_SPECIFICATION_NAME;
import static com.google.common.base.StandardSystemProperty.JAVA_SPECIFICATION_VENDOR;
import static com.google.common.base.StandardSystemProperty.JAVA_SPECIFICATION_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VENDOR;
import static com.google.common.base.StandardSystemProperty.JAVA_VENDOR_URL;
import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_NAME;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_SPECIFICATION_NAME;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_SPECIFICATION_VENDOR;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_SPECIFICATION_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_VENDOR;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_VERSION;
import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.google.common.base.StandardSystemProperty.OS_VERSION;
import static com.mattc.autotyper.Strings.FLAG_FILE;
import static com.mattc.autotyper.Strings.FLAG_GUI;
import static com.mattc.autotyper.Strings.FLAG_INPUT_DELAY;
import static com.mattc.autotyper.Strings.FLAG_PASTE;
import static com.mattc.autotyper.Strings.FLAG_URL;
import static com.mattc.autotyper.Strings.FLAG_WAIT;
import static com.mattc.autotyper.util.Console.logToFile;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;

import com.mattc.autotyper.gui.AutotyperWindow;
import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;
import com.mattc.autotyper.util.OS;

/**
 * The Main Class of the Application meant to allow Minecraft Players who use
 * ComputerCraft to use their favorite programs, even if the server blocks HTTP. <br />
 * <br />
 * The application uses JNativeHooks as an attempt ot provide cross-compatible
 * keybindings that reach outside of the JVM (so a Swing GUI is not particularly
 * required). As such some static methods are available for registering
 * NativeKeyListeners as well as retrieving the Autotyper instance. It is meant to be
 * a singleton.
 * 
 * @author Matthew
 */
public class Autotyper {

	private static final int DEFAULT_DELAY = 40;

	private static GlobalScreen global;
	private static Autotyper __instance;
	private final Keyboard keyboard;

	private final AutotyperWindow gui;
	private int waitTime = 10_000;

	public Autotyper() {
		this.keyboard = new Keyboard(DEFAULT_DELAY);
		this.gui = new AutotyperWindow(this.keyboard);

		global.addNativeKeyListener(this.keyboard);
	}

	/**
	 * Take the Program Arguments and execute the Autotyping procedure. Either using
	 * the program args or by loading up a GUI interface.
	 * 
	 * @param args
	 */
	public void launch(String[] args) {
		if (args[0].equalsIgnoreCase(FLAG_GUI)) {
			this.gui.setVisible(true);
		} else {
			final File f = parseArgs(args);
			try {
				Thread.sleep(this.waitTime);
				// JCR8YTww -- Bubbles (Concise, Good Test)
				// 6gyLvm4K -- Milkshake GUI (Lots of Long Code)
				// nAinUn1h -- Advanced Calculator (Lots of Complex Tables)
				this.keyboard.typeFile(f);
				System.exit(0);
			} catch (IOException | InterruptedException e) {
				Console.exception(e);
			}
		}
	}

	/**
	 * Take Arguments and parse flags as provided. Optional arguments are evaluated
	 * in the order they are provided. Ideally none should conflict.
	 * 
	 * @param args
	 * @return The File to extract the information from.
	 */
	private File parseArgs(String[] args) {
		File tmp;

		Console.info("Received Arguments: " + Arrays.toString(args));

		// Parse Required Arguments which MUST be in the [flag|url|paste] <location>
		// order.
		switch (args[0]) {
		case FLAG_FILE:
			tmp = new File(args[1]);
			if (!tmp.exists()) throw new IllegalArgumentException("No File Found At " + args[1] + "!");

			break;
		case FLAG_URL:
			tmp = Downloader.getFile(args[1]);
			break;
		case FLAG_PASTE:
			tmp = Downloader.getPastebin(args[1]);
			break;
		default:
			tmp = null;
			System.err.println("Invalid Arguments: " + Arrays.toString(args));
			printUsage();
			System.exit(-2);
		}

		// Parse Optional Arguments
		for (int i = 2; i < args.length; i++) {
			switch (args[i]) {
			case FLAG_WAIT:
				this.waitTime = Integer.parseInt(args[i + 1]) * 1000;
				Console.debug("WaitTime set to " + this.waitTime + " milliseconds");
				break;
			case FLAG_INPUT_DELAY:
				this.keyboard.setInputDelay(Integer.parseInt(args[i + 1]));
				Console.debug("InputDelay set to " + this.keyboard.getInputDelay() + " milliseconds");
				break;
			}
		}

		return tmp;
	}

	/**
	 * Get the only Instance of Autotyper.
	 * 
	 * @return
	 */
	public static Autotyper instance() {
		return __instance;
	}

	/**
	 * Register a NativeKeyListener to receive ALL KeyEvents
	 * 
	 * @param listener
	 */
	public static void registerGlobalKeyListener(NativeKeyListener listener) {
		global.addNativeKeyListener(listener);
	}

	/**
	 * Unregister a NativeKeyListener
	 * 
	 * @param listener
	 */
	public static void unregisterGlobalKeyListener(NativeKeyListener listener) {
		global.removeNativeKeyListener(listener);
	}

	public static void main(String[] args) {
		// Kill Error Stream [Prevent JNativeHooks Spam]
		killSystemStreams();

		// Acquire Application Lock or Fail Fast
		try {
			JUnique.acquireLock(Ref.APP_ID);
		} catch (final AlreadyLockedException e) {
			JOptionPane.showMessageDialog(null, String.format("Instance of %s Already Running!", Ref.TITLE), "Instance Collision!", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}

		// TODO Ensure that if this fails, the program will still run fine.
		// Setup Native Libraries to handle Global Key Input
		try {
			GlobalScreen.registerNativeHook();
			global = GlobalScreen.getInstance();
		} catch (final NativeHookException e) {
			Console.exception(e);
		}

		// If initial setup is ready, print Copyright
		printCopyrightStatement();
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		}

		// Setup System Failsafes
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Console.exception(e, "Caught by UncaughtExceptionHandler");

				// if(__instance != null)
				// __instance.keyboard.writeCrashImage();
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread(SHUTDOWN_HOOK, "Shutdown"));

		printSysInfo();
		setLookAndFeel();
		__instance = new Autotyper();
		__instance.launch(args);
	}

	/**
	 * Print out how the program should be executed to the Command Line
	 */
	private static void printUsage() {
		final String std = "\t%s";

		System.out.println();
		System.out.println();
		System.out.println(Ref.TITLE + " | " + Ref.VERSION + " by " + Ref.AUTHOR);
		System.out.println("Usage: java -jar ccautotyper.jar [file|url|paste] <location> [-wait] [-inputDelay]");
		System.out.println(String.format(std, FLAG_FILE) + "     - Indicates the file is on the local filesystem");
		System.out.println(String.format(std, FLAG_URL) + "      - Indicates the file must be downloaded");
		System.out.println(String.format(std, FLAG_PASTE) + "    - Indicates the file is located on pastebin");
		System.out.println("\tlocation - Either the path to the file, the url, or the pastebin code");
		System.out.println();
		System.out.println("\t[Optional Parameters -- Order Does Not Matter]");
		System.out.println(String.format(std, FLAG_WAIT) + "    - The Seconds the Autotyper should wait before typing. [Default: 10]");
		System.out.println(String.format(std, FLAG_INPUT_DELAY) + " - The Millisecond Delay between Keypresses [Default: 40]");
		System.out.println();
		System.out.println("\tExample:");
		System.out.println("\t\tjava -jar ccautotyper.jar paste JCR8YTww -inDelay 10 -wait 5");
		System.out.println("\t\tGUI Coming Soon!");
	}

	/**
	 * Print out a variety of System Properties to our log file.
	 */
	private static void printSysInfo() {
		final String home = "JAVA_HOME: " + JAVA_HOME.value();
		final String vendor = "JAVA_VEND: " + JAVA_VENDOR.value();
		final String vendor_url = "JAVA_VURL: " + JAVA_VENDOR_URL.value();
		final String jversion1 = "RUNNING Java " + JAVA_VERSION.value() + " compiled to class version " + JAVA_CLASS_VERSION.value();
		final String jversion2 = "USING " + JAVA_VM_NAME.value() + " " + JAVA_VM_VERSION.value() + " by " + JAVA_VM_VENDOR.value() + " w/ compiler " + JAVA_COMPILER.value();
		final String jversion3 = "SPECIFIED " + JAVA_SPECIFICATION_NAME.value() + " " + JAVA_SPECIFICATION_VERSION.value() + " by " + JAVA_SPECIFICATION_VENDOR.value();
		final String jversion4 = "SPECIFIED " + JAVA_VM_SPECIFICATION_NAME.value() + " " + JAVA_VM_SPECIFICATION_VERSION.value() + " by " + JAVA_VM_SPECIFICATION_VENDOR.value();
		final String os = "OS: " + OS_NAME.value() + " " + String.valueOf(OS.getArch()) + " " + OS_VERSION.value();
		final String classpath = "CP: " + JAVA_CLASS_PATH.value();
		final String cores = "TN: " + OS.processorCount() + " cores TMP_DIR: " + JAVA_IO_TMPDIR.value();

		logToFile("");
		logToFile("========== SYS INFO ==========");
		logToFile(home);
		logToFile(vendor);
		logToFile(vendor_url);
		logToFile(jversion1);
		logToFile(jversion2);
		logToFile(os);
		logToFile(classpath);
		logToFile(cores);
		logToFile(jversion3);
		logToFile(jversion4);
		logToFile("==============================");
		logToFile("");
	}

	/**
	 * Grab and Print out Copyright Statement or Fail Fast
	 */
	private static void printCopyrightStatement() {
		System.out.println();
		InputStream is = null;
		ByteArrayOutputStream bos = null;

		try {
			final byte[] buf = new byte[1024];
			is = Autotyper.class.getClassLoader().getResourceAsStream("com/mattc/autotyper/license");
			bos = new ByteArrayOutputStream();

			if (is == null) {
				Console.error("Copyright Statement Not Found! Exiting...");
				System.exit(-3);
			}

			for (int c = is.read(buf); c != -1; c = is.read(buf)) {
				bos.write(buf, 0, c);
			}

			System.out.println(bos.toString());
		} catch (final Exception e) {
			Console.exception(e);
		} finally {
			IOUtils.closeSilently(is);
			IOUtils.closeSilently(bos);
		}

		System.out.println();
	}

	/**
	 * Attempt to use System Look And Feel to look Most "Natural".
	 * 
	 * If all else fails, attempt to create a consistent experience using the Cross
	 * Platform Look and Feel.
	 * 
	 * If that fails... somehow... then use the Default Look and Feel.
	 */
	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Console.info("SUCCESS! LookAndFeel set to System LookAndFeel -- " + UIManager.getSystemLookAndFeelClassName() + "!");
		} catch (final Exception e) {
			Console.warn("Unable to Set LookAndFeel to System LookAndFeel -- " + e.getMessage() + "...");
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				Console.info("SUCCESS! Look and Feel Set to Cross-Platform LookAndFeel!");
			} catch (final Exception ex) {
				Console.bigWarning("FAILURE! Failure to Set Look And Feel!");
				Console.exception(ex);
			}
		}
	}

	private static void killSystemStreams() {
		// Ignore JNativeHook's Constant Output
		System.setErr(new PrintStream(NULL_STREAM));
	}

	private static final Runnable SHUTDOWN_HOOK = new Runnable() {
		@Override
		public void run() {
			Console.debug("Finalizing Registers...");
			JUnique.releaseLock(Ref.APP_ID);
			GlobalScreen.unregisterNativeHook();
		}
	};

	private static final OutputStream NULL_STREAM = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			return;
		}
	};

}
