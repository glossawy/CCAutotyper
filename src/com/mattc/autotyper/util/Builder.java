package com.mattc.autotyper.util;

/**
 * A Common Interface for Builders. Purely as an indicator that a class uses a
 * Builder.
 *
 * @param <T>
 * @author Matthew
 */
public interface Builder<T> {

    T build();

}
