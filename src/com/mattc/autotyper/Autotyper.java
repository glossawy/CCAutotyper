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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.OS;

public class Autotyper {

	private static final int DEFAULT_DELAY = 40;
	private static Autotyper __instance;
	private final Keyboard keyboard;

	private int waitTime = 10_000;

	public Autotyper() {
		this.keyboard = new Keyboard(DEFAULT_DELAY);
	}

	public void launch(String[] args) {
		if (args[0].equalsIgnoreCase(FLAG_GUI)) {
			// TODO GUI
		} else {
			final File f = parseArgs(args);
			try {
				Thread.sleep(this.waitTime);
				// JCR8YTww -- Bubbles (Concise, Good Test)
				// 6gyLvm4K -- Milkshake GUI (Lots of Long Code)
				// nAinUn1h -- Advanced Calculator (Lots of Complex Tables)
				this.keyboard.typeFile(f);
			} catch (IOException | InterruptedException e) {
				Console.exception(e);
			}
		}
	}

	private File parseArgs(String[] args) {
		File tmp;

		Console.info("Received Arguments: " + Arrays.toString(args));

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

		for (int i = 2; i < args.length; i += 2) {
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

	public static void main(String[] args) {
		try {
			JUnique.acquireLock(Ref.APP_ID);
		} catch (final AlreadyLockedException e) {
			JOptionPane.showMessageDialog(null, String.format("Instance of %s Already Running!", Ref.TITLE), "Instance Collision!", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}

		if (args.length < 2) {
			printUsage();
			System.exit(0);
		}

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Console.exception(e, "Caught by UncaughtExceptionHandler");

				// if(__instance != null)
				// __instance.keyboard.writeCrashImage();
			}
		});

		printSysInfo();
		setLookAndFeel();
		__instance = new Autotyper();
		__instance.launch(args);
	}

	private static void printUsage() {
		final String std = "\t%s";

		System.out.println();
		System.out.println(Ref.TITLE + " | " + Ref.VERSION + " by " + Ref.AUTHOR);
		System.out.println("Usage: java -jar ccautotyper.jar [file|url|paste] <location> [-wait] [-inputDelay]");
		System.out.println(String.format(std, FLAG_FILE) + "     - Indicates the file is on the local filesystem");
		System.out.println(String.format(std, FLAG_URL) + "      - Indicates the file must be downloaded");
		System.out.println(String.format(std, FLAG_PASTE) + "\tpaste    - Indicates the file is located on pastebin");
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
}
