package com.mattc.autotyper.robot;

import org.jnativehook.keyboard.NativeKeyListener;

import java.io.File;
import java.io.IOException;

/**
 * A Common Interface for any Keyboard Implementations.
 * 
 * @author Matthew
 * @see SwingKeyboard
 * @see FXKeyboard
 *
 */
public interface Keyboard extends NativeKeyListener {

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
		INACTIVE
	}

	/**
	 * Type a Single Character to the screen
	 * 
	 * @param c
	 */
	void type(char c);

	/**
	 * Type all characters in the string to the screen.
	 * 
	 * @param str
	 */
	void type(String str);

	/**
	 * Type an entire file's contents to the screen.
	 * 
	 * @param f
	 * @throws IOException
	 */
	void typeFile(File f) throws IOException;

	/**
	 * Take image of screen and save.
	 */
	@Deprecated
	void writeCrashImage();

	/**
	 * Set Input Delay (On a per Keystroke Basis)
	 * 
	 * @param msDelay
	 */
	void setInputDelay(int msDelay);

	/**
	 * Get Input Delay (On a per Keystroke Basis)
	 * 
	 * @return
	 */
	int getInputDelay();

	/**
	 * Set the KeyboardMode to either start, pause or terminate a procedure.
	 * 
	 * @param mode
	 */
	void setKeyboardMode(KeyboardMode mode);

	/**
	 * Get current Keyboard State
	 * 
	 * @return
	 */
	KeyboardMode getKeyboardMode();

	/**
	 * Destroy any native or destroyable assets.
	 */
	void destroy();

}
