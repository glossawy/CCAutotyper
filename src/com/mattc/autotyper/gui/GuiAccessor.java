package com.mattc.autotyper.gui;

import com.google.common.annotations.Beta;

/**
 * A (possibly temporary) simple interface to handle multiple types of GUIs and their
 * being unrelated to each other in any way. This allows a single common interface in
 * the main class instead of trying to handle two completely different GUIs from two
 * completely different API's.
 * 
 * @author Matthew
 *
 */
@Beta
public interface GuiAccessor {

	void doShow();

	void doHide();

}
