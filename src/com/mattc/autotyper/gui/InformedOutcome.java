package com.mattc.autotyper.gui;

public class InformedOutcome {

	public final Object invoker;
	public final String reason;
	public final boolean success;
	public final Exception recommendedException;

	public InformedOutcome(Object owner, String reason, boolean success, Exception exception) {
		this.invoker = owner;
		this.reason = reason;
		this.success = success;
		this.recommendedException = exception;
	}

	public InformedOutcome(Object owner, String reason, boolean success) {
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

	public static InformedOutcome createFailure(Object owner, String reason) {
		return new InformedOutcome(owner, reason, false);
	}

	public static InformedOutcome createFailure(String reason) {
		return new InformedOutcome(null, reason, false);
	}

	public static InformedOutcome createSuccess(Object owner) {
		return new InformedOutcome(owner, "", true);
	}

	public static InformedOutcome createSuccess() {
		return new InformedOutcome(null, "", true);
	}

}
