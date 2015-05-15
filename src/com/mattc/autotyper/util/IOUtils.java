package com.mattc.autotyper.util;

import com.google.common.io.Files;
import com.mattc.autotyper.gui.SingleStringProcessor;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 4/4/2015 at 12:40 AM
 */
public final class IOUtils {

    public static boolean checkConnectionSuccess(URL url) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");

        final int responseCode = conn.getResponseCode();
        conn.disconnect();

        return responseCode == 200;
    }

    public static boolean checkConnectionSuccess(String url) throws IOException {
        return checkConnectionSuccess(new URL(url));
    }

    public static String fileToString(File file, Charset charset) throws IOException {
        return Files.readLines(file, charset, new SingleStringProcessor());
    }

    public static String fileToString(File file) throws IOException {
        return fileToString(file, Charset.defaultCharset());
    }

    /**
     * Does some action with a Closeable and then closes it. Akin to the common file useage idiom
     * found in languages like Ruby and Python.
     *
     * @param item     - Item to Use
     * @param consumer - Action to do, takes the item as a parameter
     * @param <T>      - Type that implements Closeable
     * @return The outcome of the task
     */
    public static <T extends Closeable> Outcome doThenClose(T item, ExceptionalConsumer<T> consumer) {
        Outcome outcome = Outcome.success();
        try {
            consumer.accept(item);
        } catch (Exception ignore) {
            outcome = Outcome.failure(ignore);
        } finally {
            IOUtils.close(item);
        }

        return outcome;
    }

    /**
     * Silently closes a {@link Closeable}, any thrown IOException is ignored, other
     * RuntimeException's are allowed to propagate.
     *
     * @return The outcome of closing. Only fails if Closeable.close() throws an exception
     */
    public static Outcome close(Closeable closeable) {
        Outcome outcome = Outcome.success();

        try {
            closeable.close();
        } catch (IOException e) {
            outcome = Outcome.failure(e);
        }

        return outcome;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    @FunctionalInterface
    public interface ExceptionalConsumer<T> {
        void accept(T val) throws Exception;
    }

}
