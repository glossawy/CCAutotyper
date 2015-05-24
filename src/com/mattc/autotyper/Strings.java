package com.mattc.autotyper;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.mattc.autotyper.util.Console;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Enumerates all Strings used in this Program that should be altered. <br />
 * <br />
 * Could possibly be used for Localization in the future. [Localization Keys]
 *
 * @author Matthew
 */
public class Strings {

    /**
     * GitHub URL to open when the 'info' button is pressed
     */
    public static final String GITHUB_URL = "https://github.com/Matt529/CCAutotyper/blob/master/README.md";
    public static final String DEVSITE_URL = "https://www.matthewcrocco.us";

    public static final String FLAG_GUI = "gui";
    public static final String FLAG_FILE = "file";
    public static final String FLAG_URL = "url";
    public static final String FLAG_PASTE = "paste";

    public static final String FLAG_WAIT = "-wait";
    public static final String FLAG_INPUT_DELAY = "-inDelay";

    public static final String EXAMPLE_EXECUTION = "java -jar ccautotyper.jar paste JCR8YTww -inDelay 10 -wait 5";

    /**
     * Ghost Text to Display in TextField when File Button Selected
     */
    public static final String GHOST_TEXT_FSELECT = "Relative or Absolute File Path";
    /**
     * Ghost Text to Display in TextField when URL Button Selected
     */
    public static final String GHOST_TEXT_USELECT = "Valid HTTP URL to Downloadable File";
    /**
     * Ghost Text to Display in TextField when Pastebin Button Selected
     */
    public static final String GHOST_TEXT_PSELECT = "Valid Pastebin File Code";
    /**
     * Ghost Text to Display in TextField when Auto Button Selected
     */
    public static final String GHOST_TEXT_ASELECT = "File Path, HTTP URL or Pastebin File Code";

    // Preferences Keys
    public static final String PREFS_GUI_VERSION = "app_version";
    public static final String PREFS_GUI_WAIT = "wait_time";
    public static final String PREFS_GUI_INPUTDELAY = "input_delay";
    public static final String PREFS_GUI_MEMORY = "locations_";
    public static final String PREFS_GUI_SELECTED = "selected_radio";
    public static final String PREFS_GUI_MINIFY = "minify_code_option";

    public static final String PREFS_CONFIRM_THEME = "confirm_theme";
    public static final String PREFS_CONFIRM_MODE = "confirm_mode";

    public static final String PREFS_GUI_CONFIRM = "do_confirm_file";

    /**
     * Creates Resource objects that represent the URL and Stream of various media
     */
    public static final class Resources {

        public static final String LICENSE = "com/mattc/autotyper/license";
        private static final Image[] img = new Image[4];

        public static void setAppIcons(Stage stage) {
            if (img[0] == null) {
                for (int i = 0, size = 32; (i < img.length) && (size <= 128); size += 16) {
                    final Resource res = Resources.getImage("icon" + size + ".png");

                    if ((res.url() != null) && (res.stream() != null)) {
                        Console.debug("Found icon" + size + ".png");
                        img[i++] = new Image(res.stream());
                    } else if ((((size % 32) == 0) || (size == 48)) && (size != 96)) {
                        Console.error("Could not find icon" + size + ".png!");
                    }
                }

            }

            stage.getIcons().addAll(img);
        }

        public static Resource getLicense() {
            return new Resource() {
                @Override
                public URL url() {
                    return Strings.class.getClassLoader().getResource(Resources.LICENSE);
                }
            };
        }

        /**
         * Get CSS File Resource found in com/mattc/autotyper/gui/fx/css package
         */
        public static Resource getCSS(final String name) {
            return new Resource() {

                @Override
                public URL url() {
                    return Strings.class.getClassLoader().getResource("com/mattc/autotyper/gui/fx/css/" + name + (name.endsWith(".css") ? "" : ".css"));
                }
            };
        }

        /**
         * Get Image File found in the res package. NOT FOR LOCAL FILES.
         */
        public static Resource getImage(final String name) {
            return new Resource() {

                @Override
                public URL url() {
                    return getClass().getClassLoader().getResource("res/" + name);
                }
            };
        }

        /**
         * Get Arbitrary File in relative to Root Directory
         */
        public static Resource getRootFile(final String name) {
            return new Resource() {
                @Override
                public URL url() {
                    try {
                        return new File(name).toURI().toURL();
                    } catch (final MalformedURLException e) {
                        Console.exception(e);
                        return null;
                    }
                }
            };
        }

        /**
         * Container for URL and Streams
         */
        public static abstract class Resource {
            public abstract URL url();

            public InputStream stream() {
                final URL url = url();

                try {
                    return url != null ? url.openStream() : null;
                } catch (final IOException e) {
                    Console.exception(e);
                }

                return null;
            }
        }
    }

}
