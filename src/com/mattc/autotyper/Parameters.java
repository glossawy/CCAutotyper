package com.mattc.autotyper;

import java.io.File;

/**
 * Simple Parameter Definition Wrapper
 */
public class Parameters {

	public static final int DEFAULT_WAIT = 5000;
	public static final int DEFAULT_DELAY = 40;

    public static final int MIN_WAIT = 1;
    public static final int MIN_DELAY = 1;

	public final int waitTime, inputDelay;
	public final File file;

	protected Parameters(int waitTime, int inputDelay, File file) {
		this.waitTime = Math.max(waitTime, MIN_WAIT);
		this.inputDelay = Math.max(inputDelay, MIN_DELAY);
		this.file = file;
	}

}
