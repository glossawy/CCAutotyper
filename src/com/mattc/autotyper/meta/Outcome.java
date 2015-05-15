package com.mattc.autotyper.meta;

import com.google.common.annotations.Beta;

@Beta
public class Outcome {

    public final Object invoker;
    public final String reason;
    public final boolean success;
    public final Exception recommendedException;

    public Outcome(Object owner, String reason, boolean success, Exception exception) {
        this.invoker = owner;
        this.reason = reason;
        this.success = success;
        this.recommendedException = exception;
    }

    public Outcome(Object owner, String reason, boolean success) {
        this(owner, reason, success, null);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean isFailure() {
        return !this.success;
    }

    public String getReason() {
        return this.reason;
    }

    public static Outcome createFailure(Object owner, String reason) {
        return new Outcome(owner, reason, false);
    }

    public static Outcome createFailure(String reason) {
        return new Outcome(null, reason, false);
    }

    public static Outcome createSuccess(Object owner) {
        return new Outcome(owner, "", true);
    }

    public static Outcome createSuccess() {
        return new Outcome(null, "", true);
    }

    @Override
    public String toString() {
        return String.format("{OUTCOME: %s | %s}", this.success, String.valueOf(this.reason));
    }

}
