package com.mattc.autotyper;

import com.mattc.autotyper.gui.AutotyperWindow;
import com.mattc.autotyper.gui.GuiAccessor;
import com.mattc.autotyper.gui.fx.FXAutotyperWindow;
import com.mattc.autotyper.gui.fx.FXGuiUtils;
import com.mattc.autotyper.gui.fx.FXOptionPane;
import com.mattc.autotyper.robot.Keyboard;
import com.mattc.autotyper.robot.KeyboardMethodology;
import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;
import com.mattc.autotyper.util.OS;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;

import static com.google.common.base.StandardSystemProperty.*;
import static com.mattc.autotyper.Strings.*;
import static com.mattc.autotyper.util.Console.logToFile;

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

	private static GlobalScreen global;

	private final GuiAccessor gui;

	public Autotyper() {
		if (FXGuiUtils.canUseJavaFX()) {
			Console.debug("Using JavaFX GUI");
			this.gui = FXAutotyperWindow.getAccessor();
		} else {
			Console.debug("Using Swing GUI");
			this.gui = new AutotyperWindow();
		}

	}

	/**
	 * Take the Program Arguments and execute the Autotyping procedure. Either using
	 * the program args or by loading up a GUI interface. <br />
	 * 
	 * @param args
	 */
	public void launch(String[] args) {
		if (args[0].equalsIgnoreCase(FLAG_GUI)) {
			this.gui.doShow();

			// We have to terminate an FX GUI if it survives
			if (!(this.gui instanceof AutotyperWindow)) {
				System.exit(0);
			}
		} else {
			printCopyrightStatement(false);
			final Parameters params = parseArgs(args);
			final Keyboard keys = Keyboard.retrieveKeyboard(KeyboardMethodology.TYPING);
            keys.setInputDelay(params.inputDelay);
			final File f = params.file;

			try {
				IOUtils.sleep(params.waitTime);
				keys.typeFile(f);
			} catch (IOException e) {
				Console.exception(e);
			} finally {
				keys.destroy();
				System.exit(0);
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
	private Parameters parseArgs(String[] args) {

		File tmp;
		int waitTime = 5000, inputDelay = 40;

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
				waitTime = Integer.parseInt(args[i + 1]) * 1000;
				Console.debug("WaitTime set to " + waitTime + " milliseconds");
				break;
			case FLAG_INPUT_DELAY:
				inputDelay = Integer.parseInt(args[i + 1]);
				Console.debug("InputDelay set to " + inputDelay + " milliseconds");
				break;
			}
		}

		return new Parameters(waitTime, inputDelay, tmp);
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

		// Setup Native Libraries to handle Global Key Input
		try {
			GlobalScreen.registerNativeHook();
		} catch (final NativeHookException e) {
			Console.exception(e);
		} finally {
			global = GlobalScreen.getInstance();
		}

		// If initial setup is ready, print Copyright
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		}

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Console.exception(e, "Caught by UncaughtExceptionHandler"));
		Runtime.getRuntime().addShutdownHook(new Thread(Autotyper::onShutdown, "Shutdown"));

		printSysInfo();
		setLookAndFeel();
        new Autotyper().launch(args);
	}

	/**
	 * Print out how the program should be executed to the Command Line
	 */
	private static void printUsage() {
		final String std = "\t%-8s";

		System.out.println();
		System.out.println();
		System.out.println(Ref.TITLE + " | " + Ref.VERSION + " by " + Ref.AUTHOR);
		System.out.println("Usage: java -jar ccautotyper.jar [file|url|paste|gui] <location> [-wait] [-inputDelay]");
		System.out.println(String.format(std, FLAG_FILE) + " - Indicates the file is on the local filesystem");
		System.out.println(String.format(std, FLAG_URL)  + " - Indicates the file must be downloaded");
		System.out.println(String.format(std, FLAG_PASTE)+ " - Indicates the file is located on pastebin");
        System.out.println(String.format(std, FLAG_GUI)  + " - Display GUI, No Other Parameters Required");
        System.out.println(String.format(std, "Location")+ " - Either the path to the file, the url, or the pastebin code");
		System.out.println();
		System.out.println("\t[Optional Parameters -- Order Does Not Matter]");
		System.out.println(String.format(std, FLAG_WAIT) + "    - The Seconds the Autotyper should wait before typing. [Default: 10]");
		System.out.println(String.format(std, FLAG_INPUT_DELAY) + " - The Millisecond Delay between Key Strokes [Default: 40]");
		System.out.println();
		System.out.println("\tExample:");
		System.out.println("\t\t" + Strings.EXAMPLE_EXECUTION);
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

        // Do not bother logging to console
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
	public static void printCopyrightStatement(boolean gui) {
		if (!gui) {
			System.out.println();
		}
		InputStream is = null;
		ByteArrayOutputStream bos = null;

		try {
			final byte[] buf = new byte[1024];
			is = Autotyper.class.getClassLoader().getResourceAsStream("com/mattc/autotyper/license");
			bos = new ByteArrayOutputStream();

			if (is == null) {
				Console.error("Copyright Statement Not Found! Exiting...");
				IOUtils.closeSilently(bos);
				System.exit(-3);
			}

			for (int c = is.read(buf); c != -1; c = is.read(buf)) {
				bos.write(buf, 0, c);
			}

			if (gui) {
				if (FXGuiUtils.canUseJavaFX() && Platform.isFxApplicationThread()) {
					FXOptionPane.showMessage("Copyright & Warranty", bos.toString());
				} else {
					JOptionPane.showMessageDialog(null, bos.toString(), "Copyright & Warranty", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				System.out.println(bos.toString());
				System.out.println();
			}
		} catch (final Exception e) {
			Console.exception(e);
		} finally {
			IOUtils.closeSilently(is);
			IOUtils.closeSilently(bos);
		}
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
				Console.bigWarning("FAILURE! Failure to Set Look And Feel! Using Default...");
				Console.exception(ex);
			}
		}
	}

	private static void killSystemStreams() {
		// Filter out RTextAreaBase's 'yo'
		System.setOut(new PrintStream(System.out) {
			@Override
			public void print(String s) {
				super.print(s);
			}

			// Correct for RTextAreaBase Printing "Yo"
			@Override
			public void println(String s) {
				if (s.equalsIgnoreCase("yo"))
					return;
				else
					super.println(s);
			}
		});

        // Kill JNativeHooks incessant output
		System.setErr(new PrintStream(NULL_STREAM));
	}

    // Finalization Method called if JVM exits properly
    // is a Runtime Shutdown Hook
    private static void onShutdown() {
        Console.debug("Finalizing Registers...");
        JUnique.releaseLock(Ref.APP_ID);
        GlobalScreen.unregisterNativeHook();
    }

    // Do nothing with any input
	private static final OutputStream NULL_STREAM = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			return;
		}
	};

}
