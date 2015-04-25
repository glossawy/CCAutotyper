package com.mattc.autotyper.robot;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 4/3/2015 at 5:08 PM
 */
public enum KeyboardMethodology {

    TYPING{
        @Override
        TypingMethodology create(Keyboard keys) {
            return new TypingMethodology(keys);
        }
    };

    abstract Methodology create(Keyboard keys);

}
