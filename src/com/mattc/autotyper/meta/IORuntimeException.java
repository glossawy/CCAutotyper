package com.mattc.autotyper.meta;

import java.io.IOException;

/**
 * Needs Documentation
 *
 * @author Matthew Crocco
 *         Created on 3/22/2015
 */
public class IORuntimeException extends RuntimeException {

    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(IOException e) {
        super(e.getMessage(), e);
    }

    public IORuntimeException(String message, Throwable t) {
        super(message, t);
    }

}
