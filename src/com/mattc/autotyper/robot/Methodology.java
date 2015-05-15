package com.mattc.autotyper.robot;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.File;
import java.io.IOException;

/**
 * An interface describing the necessary functions of how a keyboard should receive input,
 * it is recommended that {@link BaseMethodology} be extended over implementing this interface as the
 * logic for managing various commonly desired key presses and key combinations. A default implementation
 * of {@link #type(char)} is also provided, removing the rote task of defining every character case.<br />
 * <br />
 * This class should only be implemented if special functionality is required or if overriding native events
 * and making "super" calls is ineffective.
 *
 * @author Matthew
 *         Created 4/3/2015 at 11:44 AM
 */
public interface Methodology extends NativeKeyListener {

    /**
     * Type a Character onto the Screen
     *
     * @param c Character
     */
    void type(char c);

    /**
     * Type an entire line onto the screen. How this is done is implementation dependent. {@link TypingMethodology}
     * types character by character, listening to events between presses.
     *
     * @param line Line of Text
     */
    void typeLine(String line);

    /**
     * Type the entire contents of a file onto the screen. How this is done is implementation dependent. {@link TypingMethodology}
     * types character by character, listening to events between presses.
     *
     * @param file File to type
     * @throws IOException If reading the file fails
     */
    void typeFile(File file) throws IOException;

    /**
     * @return The current state of the Keyboard (STOPPED, PAUSED, ACTIVE).
     * @see Keyboard.KeyboardMode
     */
    Keyboard.KeyboardMode mode();

    /**
     * Destroy all destroyable resources, rendering the keyboard unusable and freeing reserved
     * memory (if required).
     */
    void destroy();

    @Override
    void nativeKeyReleased(NativeKeyEvent nativeKeyEvent);

    @Override
    void nativeKeyTyped(NativeKeyEvent nativeKeyEvent);

    @Override
    void nativeKeyPressed(NativeKeyEvent nativeKeyEvent);
}
