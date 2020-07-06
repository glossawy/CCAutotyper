package com.mattc.autotyper.util;

/**
 * Needs Documentation
 *
 * @author Glossawy
 *         Created 4/6/2015 at 7:29 PM
 */
public final class Outcome {

    private final Exception e;
    private final boolean success;

    private Outcome(Exception e) {
        this.e = e;
        this.success = (e == null);
    }

    public static Outcome success() {
        return new Outcome(null);
    }

    public static Outcome failure(Exception e) {
        return new Outcome(e);
    }

    public Exception getError() {
        return e;
    }

    public boolean isSuccess() {
        return success;
    }


}
