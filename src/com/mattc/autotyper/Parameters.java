package com.mattc.autotyper;

import java.io.File;

public class Parameters {

	public static final int DEFAULT_WAIT = 5000;
	public static final int DEFAULT_DELAY = 40;

	public final int waitTime, inputDelay;
	public final File file;

	protected Parameters(int waitTime, int inputDelay, File file) {
		this.waitTime = waitTime;
		this.inputDelay = inputDelay;
		this.file = file;
	}

}
