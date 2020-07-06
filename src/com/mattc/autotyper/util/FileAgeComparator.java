package com.mattc.autotyper.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Created by matthew on 5/14/15.
 */
public enum FileAgeComparator implements Comparator<Path> {
    INSTANCE {
        @Override
        public int compare(Path o1, Path o2) {
            try {
                return Files.getLastModifiedTime(o2).compareTo(Files.getLastModifiedTime(o1));
            } catch (IOException e) {
                Console.exception(e);
            }

            return o2.compareTo(o1);
        }
    }
}
