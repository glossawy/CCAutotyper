package com.mattc.autotyper.meta;

public class ImpossibleInputException extends Exception {

    private static final long serialVersionUID = 733531840874817781L;

    public ImpossibleInputException(String reason) {
        super(reason);
    }

    public ImpossibleInputException(String reason, Throwable t) {
        super(reason, t);
    }

}
