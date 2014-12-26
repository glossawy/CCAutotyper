package com.mattc.autotyper;

import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_1;
import static java.awt.event.KeyEvent.VK_2;
import static java.awt.event.KeyEvent.VK_3;
import static java.awt.event.KeyEvent.VK_4;
import static java.awt.event.KeyEvent.VK_5;
import static java.awt.event.KeyEvent.VK_6;
import static java.awt.event.KeyEvent.VK_7;
import static java.awt.event.KeyEvent.VK_8;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_BACK_QUOTE;
import static java.awt.event.KeyEvent.VK_BACK_SLASH;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_CLOSE_BRACKET;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_EQUALS;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_G;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_K;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_OPEN_BRACKET;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_PERIOD;
import static java.awt.event.KeyEvent.VK_Q;
import static java.awt.event.KeyEvent.VK_QUOTE;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SEMICOLON;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_SLASH;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_T;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_U;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;
import com.mattc.autotyper.util.OS.MemoryUnit;

/**
 * Mimics the Keyboard to a large extent. <br />
 * <br />
 * Makes extensive use of {@link java.awt.Robot}, JNativeHooks and switching between
 * KeyboardMode's to mimic user input after being given a character, String or File.
 * To make use of JNativeHooks, Keyboard does implement {@link NativeKeyListener} but
 * must be manually registered. <br />
 * <br />
 * Making use of {@link KeyboardMode}, this Keyboard can exist in 1 of 3 states:
 * ACTIVE, PAUSED, or INACTIVE. ACTIVE means the keyboard is currently typing.
 * INACTIVE means the keyboard is not typing nor in the middle of a typing action,
 * and PAUSED means the Keyboard is not typing, but IS in the middle of a session.
 * 
 * @author Matthew
 */
public class Keyboard implements NativeKeyListener {

	private final Robot robo;
	private volatile KeyboardMode mode = KeyboardMode.INACTIVE;

	/**
	 * Create an instance of Keyboard with the given delay between Key Presses.
	 * 
	 * @param actionDelay
	 */
	public Keyboard(int actionDelay) {
		try {
			this.robo = new Robot();
			this.robo.setAutoDelay(actionDelay);
			this.robo.setAutoWaitForIdle(true);
		} catch (final AWTException e) {
			throw new IllegalStateException("Could not create java.awt.Robot!", e);
		}
	}

	/**
	 * Type a Single Character
	 * 
	 * @param c
	 */
	public void type(char c) {
		// A bit verbose, but necessary.
		switch (c) {
		case 'a':
			doType(VK_A);
			break;
		case 'b':
			doType(VK_B);
			break;
		case 'c':
			doType(VK_C);
			break;
		case 'd':
			doType(VK_D);
			break;
		case 'e':
			doType(VK_E);
			break;
		case 'f':
			doType(VK_F);
			break;
		case 'g':
			doType(VK_G);
			break;
		case 'h':
			doType(VK_H);
			break;
		case 'i':
			doType(VK_I);
			break;
		case 'j':
			doType(VK_J);
			break;
		case 'k':
			doType(VK_K);
			break;
		case 'l':
			doType(VK_L);
			break;
		case 'm':
			doType(VK_M);
			break;
		case 'n':
			doType(VK_N);
			break;
		case 'o':
			doType(VK_O);
			break;
		case 'p':
			doType(VK_P);
			break;
		case 'q':
			doType(VK_Q);
			break;
		case 'r':
			doType(VK_R);
			break;
		case 's':
			doType(VK_S);
			break;
		case 't':
			doType(VK_T);
			break;
		case 'u':
			doType(VK_U);
			break;
		case 'v':
			doType(VK_V);
			break;
		case 'w':
			doType(VK_W);
			break;
		case 'x':
			doType(VK_X);
			break;
		case 'y':
			doType(VK_Y);
			break;
		case 'z':
			doType(VK_Z);
			break;
		case 'A':
			doType(VK_SHIFT, VK_A);
			break;
		case 'B':
			doType(VK_SHIFT, VK_B);
			break;
		case 'C':
			doType(VK_SHIFT, VK_C);
			break;
		case 'D':
			doType(VK_SHIFT, VK_D);
			break;
		case 'E':
			doType(VK_SHIFT, VK_E);
			break;
		case 'F':
			doType(VK_SHIFT, VK_F);
			break;
		case 'G':
			doType(VK_SHIFT, VK_G);
			break;
		case 'H':
			doType(VK_SHIFT, VK_H);
			break;
		case 'I':
			doType(VK_SHIFT, VK_I);
			break;
		case 'J':
			doType(VK_SHIFT, VK_J);
			break;
		case 'K':
			doType(VK_SHIFT, VK_K);
			break;
		case 'L':
			doType(VK_SHIFT, VK_L);
			break;
		case 'M':
			doType(VK_SHIFT, VK_M);
			break;
		case 'N':
			doType(VK_SHIFT, VK_N);
			break;
		case 'O':
			doType(VK_SHIFT, VK_O);
			break;
		case 'P':
			doType(VK_SHIFT, VK_P);
			break;
		case 'Q':
			doType(VK_SHIFT, VK_Q);
			break;
		case 'R':
			doType(VK_SHIFT, VK_R);
			break;
		case 'S':
			doType(VK_SHIFT, VK_S);
			break;
		case 'T':
			doType(VK_SHIFT, VK_T);
			break;
		case 'U':
			doType(VK_SHIFT, VK_U);
			break;
		case 'V':
			doType(VK_SHIFT, VK_V);
			break;
		case 'W':
			doType(VK_SHIFT, VK_W);
			break;
		case 'X':
			doType(VK_SHIFT, VK_X);
			break;
		case 'Y':
			doType(VK_SHIFT, VK_Y);
			break;
		case 'Z':
			doType(VK_SHIFT, VK_Z);
			break;
		case '`':
			doType(VK_BACK_QUOTE);
			break;
		case '0':
			doType(VK_0);
			break;
		case '1':
			doType(VK_1);
			break;
		case '2':
			doType(VK_2);
			break;
		case '3':
			doType(VK_3);
			break;
		case '4':
			doType(VK_4);
			break;
		case '5':
			doType(VK_5);
			break;
		case '6':
			doType(VK_6);
			break;
		case '7':
			doType(VK_7);
			break;
		case '8':
			doType(VK_8);
			break;
		case '9':
			doType(VK_9);
			break;
		case '-':
			doType(VK_MINUS);
			break;
		case '=':
			doType(VK_EQUALS);
			break;
		case '~':
			doType(VK_SHIFT, VK_BACK_QUOTE);
			break;
		case '!':
			doType(VK_SHIFT, VK_1);
			break;
		case '@':
			doType(VK_SHIFT, VK_1);
			break;
		case '#':
			doType(VK_SHIFT, VK_3);
			break;
		case '$':
			doType(VK_SHIFT, VK_4);
			break;
		case '%':
			doType(VK_SHIFT, VK_5);
			break;
		case '^':
			doType(VK_SHIFT, VK_6);
			break;
		case '&':
			doType(VK_SHIFT, VK_7);
			break;
		case '*':
			doType(VK_SHIFT, VK_8);
			break;
		case '(':
			doType(VK_SHIFT, VK_9);
			break;
		case ')':
			doType(VK_SHIFT, VK_0);
			break;
		case '_':
			doType(VK_SHIFT, VK_MINUS);
			break;
		case '+':
			doType(VK_SHIFT, VK_EQUALS);
			break;
		case '\t':
			doType(VK_TAB);
			break;
		case '\n':
			doType(VK_ENTER);
			break;
		case '\r':
			doType(VK_ENTER);
			break;
		case '[':
			doType(VK_OPEN_BRACKET);
			break;
		case ']':
			doType(VK_CLOSE_BRACKET);
			break;
		case '\\':
			doType(VK_BACK_SLASH);
			break;
		case '{':
			doType(VK_SHIFT, VK_OPEN_BRACKET);
			break;
		case '}':
			doType(VK_SHIFT, VK_CLOSE_BRACKET);
			break;
		case '|':
			doType(VK_SHIFT, VK_BACK_SLASH);
			break;
		case ';':
			doType(VK_SEMICOLON);
			break;
		case ':':
			doType(VK_SHIFT, VK_SEMICOLON);
			break;
		case '\'':
			doType(VK_QUOTE);
			break;
		case '"':
			doType(VK_SHIFT, VK_QUOTE);
			break;
		case ',':
			doType(VK_COMMA);
			break;
		case '<':
			doType(VK_SHIFT, VK_COMMA);
			break;
		case '.':
			doType(VK_PERIOD);
			break;
		case '>':
			doType(VK_SHIFT, VK_PERIOD);
			break;
		case '/':
			doType(VK_SLASH);
			break;
		case '?':
			doType(VK_SHIFT, VK_SLASH);
			break;
		case ' ':
			doType(VK_SPACE);
			break;
		default:
			throw new IllegalArgumentException("Cannot type character " + c);
		}
	}

	/**
	 * Type an entire String out, prints character by character to mimic user input.
	 * 
	 * @param text
	 */
	public void type(String text) {
		final char[] chars = text.toCharArray();
		this.mode = KeyboardMode.ACTIVE;

		for (final char c : chars) {
			while ((this.mode == KeyboardMode.PAUSED) || this.alt) {
				IOUtils.sleep(1000);
			}
			if (this.mode == KeyboardMode.INACTIVE) {
				break;
			}

			type(c);
		}

		this.mode = KeyboardMode.INACTIVE;
	}

	/**
	 * Take an entire file and type the entirety of it's contents. This will print
	 * character by character to mimic user input.
	 * 
	 * @param f
	 * @throws IOException
	 */
	public void typeFile(File f) throws IOException {
		final List<String> lines = Files.readAllLines(Paths.get(f.toURI()), StandardCharsets.UTF_8);

		this.mode = KeyboardMode.ACTIVE;
		final MemoryUnit mem = MemoryUnit.KILOBYTES;
		final long size = mem.convert(f.length(), MemoryUnit.BYTES);
		Console.info(String.format("Writing File of Size %,d KB consisting of %,d lines", size, lines.size()));

		boolean block = false;
		outer:
			for (final String l : lines) {
				// Ignore Empty Lines and Comments
				if (l.length() == 0) {
					continue;
				} else if (l.startsWith("--[[")) {
					block = true;
					continue;
				} else if (block && (l.endsWith("]]") || l.endsWith("]]--"))) {
					block = false;
					continue;
				} else if (l.startsWith("--")) {
					continue;
				}

				// Basically a copy of type(String) but this gives us more control
				// to pause and stop on a per character basis, not a per line basis.
				Console.info(this.mode);
				final char[] characters = l.trim().toCharArray();
				for (final char c : characters) {
					while ((this.mode == KeyboardMode.PAUSED) || this.alt) {
						IOUtils.sleep(1000);
					}
					if (this.mode == KeyboardMode.INACTIVE) {
						break outer;
					}

					type(c);
				}
				doType(VK_ENTER);
			}

		Console.debug("FINISHED");
		this.mode = KeyboardMode.INACTIVE;
	}

	/**
	 * Alter the delay between key presses in milliseconds
	 * 
	 * @param delay
	 */
	public void setInputDelay(int delay) {
		this.robo.setAutoDelay(delay);
	}

	/**
	 * Alter the current state of the Keyboard
	 * 
	 * @param mode
	 */
	public void setKeyboardMode(KeyboardMode mode) {
		this.mode = mode;
	}

	/**
	 * Get the delay between key presses in milliseconds
	 * 
	 * @return
	 */
	public int getInputDelay() {
		return this.robo.getAutoDelay();
	}

	/**
	 * Get the current state of the Keyboard
	 * 
	 * @return
	 */
	public KeyboardMode getKeyboardMode() {
		return this.mode;
	}

	/**
	 * Take a picture of the User's entire desktop and save it to
	 * cc-autotyper-crash.png <br />
	 * <br />
	 * This would be used in the case of an Uncaught Exception but currently is
	 * unused.
	 */
	public void writeCrashImage() {
		try {
			final File crashFile = new File("logs", "cc-autotyper-crash.png");
			final BufferedImage img = this.robo.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ImageIO.write(img, "PNG", crashFile);
		} catch (final IOException e) {
			Console.exception(e);
		}
	}

	/**
	 * Internal Method for pressing keys. Very convenient for Key Strokes. (i.e.
	 * Shift + 1 to get !). <br />
	 * <br />
	 * See {@link #doType(int[], int, int)}
	 * 
	 * @param keycodes
	 */
	private void doType(int... keycodes) {
		doType(keycodes, 0, keycodes.length);
	}

	/**
	 * Recursively Press and Release Keys to mimic Key Strokes.
	 * 
	 * @param codes
	 * @param offset
	 * @param length
	 */
	private void doType(int[] codes, int offset, int length) {
		if (length == 0) return;

		this.robo.keyPress(codes[offset]);
		doType(codes, offset + 1, length - 1);
		this.robo.keyRelease(codes[offset]);
	}

	private volatile boolean alt = false;
	private volatile boolean bspace = false;

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {

		// Ignore if alt is not pressed, it's all we care about.
		if (!this.alt) return;

		String log = this.mode.name() + "...";
		if (this.alt && (e.getKeyChar() == 'p')) {
			// Toggle for Alt + P
			switch (this.mode) {
			case ACTIVE:
				this.mode = KeyboardMode.PAUSED;
				break;
			case PAUSED:
				this.mode = KeyboardMode.ACTIVE;
				break;
			default:
				break;
			}
		} else if (this.alt && (e.getKeyChar() == 's')) {
			// Terminate Current Session for Alt + S
			this.mode = KeyboardMode.INACTIVE;
		}

		log = "Keyboard set to " + this.mode.name() + " from " + log;
		Console.debug(log);
		IOUtils.sleep(20);
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		// If Keyboard is Active and Left Alt is released
		if ((e.getKeyCode() == NativeKeyEvent.VC_ALT_L) && (this.mode == KeyboardMode.ACTIVE)) {
			this.alt = false;

			// Delete the 1 or 2 stray characters
			// Alt + P will print P in computer craft, this deletes the P
			// if the user did not.
			if (!this.bspace) {
				doType(KeyEvent.VK_BACK_SPACE);
			} else {
				this.bspace = false;
			}

			doType(KeyEvent.VK_BACK_SPACE);
		}
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		// If Left Alt is Pressed, set the Alt Flag to true.
		if (e.getKeyCode() == NativeKeyEvent.VC_ALT_L) {
			this.alt = true;
		} else if (this.alt && (e.getKeyCode() == NativeKeyEvent.VC_BACKSPACE)) {
			this.bspace = true;
		}
	}

	/**
	 * Enumerates the 3 State in which a Keyboard can exist:
	 * <ul>
	 * <li><b>ACTIVE</b> - The Keyboard is Typing and is in an Active Session</li>
	 * <li><b>PAUSED</b> - The Keyboard is Not Typing but is still in an Active
	 * Session</li>
	 * <li><b>INACTIVE</b> - The Keyboard is Not Typing and is NOT in an Active
	 * Session. Idle.</li>
	 * </ul>
	 * 
	 * @author Matthew
	 */
	public enum KeyboardMode {
		/** Is Typing & Is In an Active Session */
		ACTIVE,
		/** Is Not Typing & Is In An Active Session */
		PAUSED,
		/** Is Not Typing & Is Not In An Active Session */
		INACTIVE;
	}

}
