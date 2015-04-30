package com.mattc.autotyper.robot;

import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;
import com.mattc.autotyper.util.OS;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 4/3/2015 at 5:05 PM
 */
class TypingMethodology extends BaseMethodology {

    private final Keyboard keys;

    TypingMethodology(Keyboard keys) {
        this.keys = keys;
    }

    @Override
    public void type(char c) {
        keys.type(c);
    }

    @Override
    public void typeLine(String line) {
        final char[] chars = line.toCharArray();
        this.start();

        for (final char c : chars) {
            while (this.isPaused() || this.isAltDown()) {
                IOUtils.sleep(200);
            }

            while(!this.isScheduleEmpty())
                keys.doType(this.nextScheduleKey());

            if (this.isInactive()) {
                this.clearSchedule();
                break;
            }

            type(c);
        }

        this.end();
    }

    @Override
    public void typeFile(File file) throws IOException {
		final List<String> lines = Files.readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8);

        this.start();

		final OS.MemoryUnit mem = OS.MemoryUnit.KILOBYTES;
		final long size = mem.convert(file.length(), OS.MemoryUnit.BYTES);
		Console.info(String.format("Writing File of Size %,d KB consisting of %,d lines", size, lines.size()));

		boolean block = false;
		outer:
			for (final String l : lines) {
				// Ignore Empty Lines and Comments
				if (l.length() == 0) {
					continue;
				} else if (l.startsWith("--[[")) {
					block = true;
					continue;
				} else if (block && (l.endsWith("]]") || l.endsWith("]]--"))) {
					block = false;
					continue;
				} else if (l.startsWith("--")) {
					continue;
				}

				// Basically a copy of type(String) but this gives us more control
				// to pause and stop on a per character basis, not a per line basis.
				final char[] characters = l.trim().toCharArray();
				int commentChars = 0;
                for (final char c : characters) {
                    // Cancel typing once we hit a comment
                    if(c == '-')
                        commentChars ++;
                    else
                        commentChars = 0;

                    if(commentChars >= 2)
                        break;

                    // Pause Loop
					while (this.isPaused() || this.isAltDown()) {
						// We have to yield CPU time, 200 ms just sounds nice. (could be 10 ms or less)
                        IOUtils.sleep(200);
					}

                    // Handle Scheduled Key Events of the NativeHooks Dispatch Thread
                    while(!this.isScheduleEmpty())
                        keys.doType(this.nextScheduleKey());

                    // Kill Switch
                    if (this.isInactive()) {
						this.clearSchedule();
                        break outer;
					}

					type(c);
				}

				keys.doType(KeyEvent.VK_ENTER);
			}

		Console.debug("FINISHED");
		this.end();
    }

    @Override
    public void destroy() {
        this.end();
    }
}
