package com.mattc.autotyper.robot;

import com.mattc.autotyper.Parameters;
import com.mattc.autotyper.gui.fx.FXGuiUtils;
import com.mattc.autotyper.util.Console;

import java.io.File;
import java.io.IOException;

/**
 * A Common Interface for any Keyboard Implementations.
 * 
 * @author Matthew
 * @see SwingKeyboard
 *
 */
public abstract class Keyboard  {

    public static Keyboard retrieveKeyboard(KeyboardMethodology preferred) {
        Console.info("SwingKeyboard Created!");
        Keyboard keyboard = new SwingKeyboard(Parameters.DEFAULT_DELAY);

        Console.info("Keyboard Methodology Created for " + preferred + "...");
        keyboard.setMethod(preferred);
        return keyboard;
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
	enum KeyboardMode {
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
	public abstract void type(char c);

	/**
	 * Type all characters in the string to the screen.
	 * 
	 * @param str
	 */
    public abstract void type(String str);

	/**
	 * Type an entire file's contents to the screen.
	 * 
	 * @param f
	 * @throws IOException
	 */
    public abstract void typeFile(File f) throws IOException;

	/**
	 * Take image of screen and save.
	 */
	@Deprecated
    public abstract void writeCrashImage();

	/**
	 * Set Input Delay (On a per Keystroke Basis)
	 * 
	 * @param msDelay
	 */
    public abstract void setInputDelay(int msDelay);

	/**
	 * Get Input Delay (On a per Keystroke Basis)
	 * 
	 * @return
	 */
    public abstract int getInputDelay();

	/**
	 * Get current Keyboard State
	 * 
	 * @return
	 */
    public abstract KeyboardMode getKeyboardMode();

	/**
	 * Destroy any native or destroyable assets.
	 */
    public abstract void destroy();

    public abstract void setMethod(KeyboardMethodology method);
    abstract void press(int code);
    abstract void release(int code);
    abstract void doType(int... codes);

}
