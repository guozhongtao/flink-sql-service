package org.frank.flinksql.service.util;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * An utility class for I/O related functionality.
 */
@Slf4j
public final class IOUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private IOUtils() {
    }

    public static void writeToFile(final String fileName, final String contents) {
        Preconditions.checkNotNull(fileName, "Provided file name for writing must NOT be null.");
        Preconditions.checkNotNull(contents, "Unable to write null contents.");
        final File newFile = new File(fileName);
        try {
            Files.write(contents.getBytes(), newFile);
        } catch (IOException e) {
            log.error("ERROR trying to write to file {} for {} ", fileName, e.getMessage(), e);
        } finally {
        }

    }
}
