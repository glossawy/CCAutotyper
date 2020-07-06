package com.mattc.autotyper.robot;

/**
 * A Simplification for internal use, originally used to abstract away complicated initialization
 * procedures. <br />
 * <br />
 * <b>May be removed in a future release.</b>
 *
 * @author Glossawy
 *         Created 4/3/2015 at 5:08 PM
 */
public enum KeyboardMethodology {

    /**
     * A Methodology that types character by character.
     */
    TYPING {
        @Override
        TypingMethodology create(Keyboard keys) {
            return new TypingMethodology(keys);
        }
    };


    /**
     * Instantiate this Methodology using the given Keyboard
     *
     * @param keys {@link Keyboard}
     * @return Methodology for the given Keyboard
     */
    abstract Methodology create(Keyboard keys);

}
