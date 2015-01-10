package com.mattc.autotyper.meta;

import com.google.common.annotations.Beta;
import com.mattc.autotyper.util.Console;

@Beta
public interface ExceptionHandler {

	/**
	 * Takes the exception and calls {@link Console#exception(Throwable)}
	 */
	public static final ExceptionHandler DEFAULT_HANDLER = new ExceptionHandler() {
		@Override
		public void handle(Exception e) {
			Console.exception(e);
		}
	};

	/**
	 * Simply consumes the Exception and does nothing with it.
	 */
	public static final ExceptionHandler NULL_HANDLER = new ExceptionHandler() {
		@Override
		public void handle(Exception e) {
		}
	};

	void handle(Exception e);

}
