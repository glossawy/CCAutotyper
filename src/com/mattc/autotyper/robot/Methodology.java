package com.mattc.autotyper.robot;

import org.jnativehook.keyboard.NativeKeyListener;

import java.io.File;
import java.io.IOException;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 4/3/2015 at 11:44 AM
 */
interface Methodology extends NativeKeyListener {

    void type(char c);

    void typeLine(String line);

    void typeFile(File file) throws IOException;

    Keyboard.KeyboardMode mode();

    void destroy();

}
