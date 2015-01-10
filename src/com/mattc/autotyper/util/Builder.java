package com.mattc.autotyper.util;

/**
 * A Common Interface for Builders. Purely as an indicator that a class uses a
 * Builder.
 * 
 * @author Matthew
 *
 * @param <T>
 */
public interface Builder<T> {

	T build();

}
