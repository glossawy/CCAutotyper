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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.OS.MemoryUnit;

public class Keyboard implements ClipboardOwner {

	private final Robot robo;

	public Keyboard(int actionDelay) {
		try {
			this.robo = new Robot();
			this.robo.setAutoDelay(actionDelay);
			this.robo.setAutoWaitForIdle(true);
		} catch (final AWTException e) {
			throw new IllegalStateException("Could not create java.awt.Robot!", e);
		}
	}

	public void type(char c) {
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

	public void type(String text) {
		final char[] chars = text.toCharArray();

		for (final char c : chars) {
			type(c);
		}
	}

	public void typeFile(File f) throws IOException {
		final List<String> lines = Files.readAllLines(Paths.get(f.toURI()));

		final MemoryUnit mem = MemoryUnit.KILOBYTES;
		final long size = mem.convert(f.length(), MemoryUnit.BYTES);
		Console.info(String.format("Writing File of Size %,d KB consisting of %,d lines", size, lines.size()));

		boolean block = true;
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

			type(l.trim());
			doType(VK_ENTER);
		}
	}

	public void setInputDelay(int delay) {
		this.robo.setAutoDelay(delay);
	}

	public int getInputDelay() {
		return this.robo.getAutoDelay();
	}

	public void writeCrashImage() {
		try {
			final File crashFile = new File("logs", "cc-autotyper-crash.png");
			final BufferedImage img = this.robo.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ImageIO.write(img, "PNG", crashFile);
		} catch (final IOException e) {
			Console.exception(e);
		}
	}

	private void doType(int... keycodes) {
		doType(keycodes, 0, keycodes.length);
	}

	private void doType(int[] codes, int offset, int length) {
		if (length == 0) return;

		this.robo.keyPress(codes[offset]);
		doType(codes, offset + 1, length - 1);
		this.robo.keyRelease(codes[offset]);
	}

	public Transferable toTransferable(String text) {
		return new Transferable() {

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return false;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[0];
			}

			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				return text;
			}
		};
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {

	}
}
