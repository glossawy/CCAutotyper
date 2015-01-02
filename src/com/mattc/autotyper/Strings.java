package com.mattc.autotyper;

/**
 * Enumerates all Strings used in this Program that should be altered. <br />
 * <br />
 * Could possibly be used for Localization in the future. [Localization Keys]
 * 
 * @author Matthew
 */
public class Strings {

	public static final String GITHUB_URL = "https://github.com/Matt529/CCAutotyper/blob/master/README.md";

	public static final String FLAG_GUI = "gui";
	public static final String FLAG_FILE = "file";
	public static final String FLAG_URL = "url";
	public static final String FLAG_PASTE = "paste";

	public static final String FLAG_WAIT = "-wait";
	public static final String FLAG_INPUT_DELAY = "-inDelay";

	public static final String EXAMPLE_EXECUTION = "java -jar ccautotyper.jar paste JCR8YTww -inDelay 10 -wait 5";

	public static final String GHOST_TEXT_FSELECT = "Relative or Absolute File Path";
	public static final String GHOST_TEXT_USELECT = "Valid HTTP URL to Downloadable File";
	public static final String GHOST_TEXT_PSELECT = "Valid Pastebin File Code";
	public static final String GHOST_TEXT_ASELECT = "File Path, HTTP URL or Pastebin File Code";

	public static final String PREFS_GUI_WAIT = "wait_time";
	public static final String PREFS_GUI_INPUTDELAY = "input_delay";
	public static final String PREFS_GUI_MEMORY = "locations_";
	public static final String PREFS_GUI_SELECTED = "selected_radio";

	public static final String PREFS_GUI_CONFIRM = "do_confirm_file";

}
