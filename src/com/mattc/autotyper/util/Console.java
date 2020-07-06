package com.mattc.autotyper.util;

import com.google.common.base.Strings;
import com.mattc.autotyper.Ref;
import com.mattc.autotyper.meta.IORuntimeException;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.joda.time.DateTime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * A Centralized Logging utility utilizing Log4j. <br />
 * <br />
 * By default this application attempts to maintain a Log in the Console and <br />
 * a File Log on every run. It will, by default, maintain a maximum of 6 Log Files. <br />
 * <br />
 * Additional Logs can be created using the {@link #addLogTarget(Appender)}
 * method.
 * Alternatively <br />
 * a PrintStream connected to this log can be obtained using
 * {@link #getLogPrintStream(boolean)}.
 *
 * @author Glossawy
 */
public final class Console {

    private static final Logger logger;
    private static final Path logFile;
    private static PrintStream logStream;
    private static PrintStream combinedStream;

    public static final Path LOG_DIR = Paths.get(".", "logs");
    /**
     * Max Number of .log Files allowed to exist in logs directory
     */
    public static final int MAX_LOG_COUNT = 6;
    /**
     * The Pattern Passed to EnhancedPatternLayout for Log Formatting
     */
    public static final String LOG_PATTERN = "%d{HH:mm:ss} - [%-5t][%-5p]: %m%n";

    static {
        final DateTime cur = new DateTime();
        final String logName = String.format("%s-%s-%s-%s-%s#%s#%s.log", Ref.APP_NAME, cur.getMonthOfYear(), cur.getDayOfMonth(), cur.getYearOfCentury(), cur.getHourOfDay(), cur.getMinuteOfHour(), cur.getSecondOfMinute());
        final Layout layout = new EnhancedPatternLayout(LOG_PATTERN);

        logFile = LOG_DIR.resolve(logName);

        Thread.currentThread().setName(Ref.APP_NAME);
        logger = Logger.getLogger(Ref.APP_NAME);
        logger.setLevel(Level.ALL);

        try {
            final Appender fileApp = new RollingFileAppender(layout, logFile.toString());
            final Appender consApp = new ConsoleAppender(layout);
            fileApp.setName(Ref.APP_NAME + "-LogFileAppender");

            logger.addAppender(fileApp);
            logger.addAppender(consApp);
            initialize();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void logToFile(Object msg) {
        synchronized (logger) {
            getLogPrintStream(false).print(String.valueOf(msg));
        }
    }

    /**
     * Does the same as {@link #log(Object, Level)} but goes further by taking a
     * Throwable and <br />
     * passing it to {@link Logger#log(org.apache.log4j.Priority, Object,
     * Throwable)}
     * to be parsed. <br />
     * <br />
     * The Throwable is parsed independently by each Appender. <br />
     * In a normal use case this is ConsoleAppender and RollingFileAppender. <br />
     */
    public static void log(Object msg, Throwable t, Level level) {
        synchronized (logger) {
            logger.log(level, String.valueOf(msg), t);
        }
    }

    /**
     * Takes an Object as a message and obtains its String Value <br />
     * via {@link String#valueOf(Object)} which is null-safe. <br />
     * <br />
     * The String Message and Level are passed to
     * {@link Logger#log(org.apache.log4j.Priority, Object)} <br />
     * to be printed to the Console and Log File (If created) <br />
     */
    public static void log(Object msg, Level level) {
        synchronized (logger) {
            logger.log(level, String.valueOf(msg));
        }
    }

    public static void empty() {
        info("");
    }

    /**
     * Indicates a certain point has been reached successfully and may <br />
     * print state information. <br />
     * <br />
     * This is the Level that should be generally used in normal cases. <br />
     */
    public static void info(Object msg) {
        log(msg, Level.INFO);
    }

    /**
     * Indicates a Debug Message meant for the Programmer alone.
     */
    public static void debug(Object msg) {
        log(msg, Level.DEBUG);
    }

    /**
     * Write a trace to the log with no extra information.
     */
    public static void trace() {
        trace("");
    }

    /**
     * Write a Stack Trace to the log with the given additional message. Writes 16
     * Stack Trace Elements at most.
     */
    public static void trace(Object msg) {
        int lines = 0;
        final Thread current = Thread.currentThread();
        final StackTraceElement[] trace = current.getStackTrace();
        final String border = "========================================";

        debug("");
        debug(border);
        debug(String.format("|| Detailed Stack Trace of %s[%s] Thread", current.getName(), current.getId()));
        debug("|| Trace Message: " + String.valueOf(msg));
        debug(border);
        for (int i = 2; (i < 18) && (i < trace.length); i++) {
            debug(String.format("||   at %s%s", trace[i].toString(), (i < 15) && (i < (trace.length - 1)) ? "..." : ""));
            lines++;
        }
        debug(border);
        debug("|| Resolved " + lines + " elements...");
        debug(border);
        debug("");
    }

    /**
     * Does the same as {@link #bigWarning(Object)} but prints "null" as the message.
     */
    public static void bigWarning() {
        bigWarning("");
    }

    /**
     * Prints a very noticeable warning that is bordered. <br />
     * <br />
     * Indicates the same thing as {@link #warn(Object)} but also prints<br />
     * a 6 line Stack Trace (will not include the call to this method). <br />
     * <br />
     * <code>
     * **************************************** <br/>
     * * Message Here<br/>
     * *  at trace(class:line)<br/>
     * *  at trace(class:line)<br/>
     * *  at trace(class:line)<br/>
     * *  at trace(class:line)<br/>
     * *  at trace(class:line)<br/>
     * *  at trace(class:Line)<br/>
     * **************************************** <br/>
     * </code>Much Thanks to the Minecraft Forge Team for the idea!
     *
     * @param msg
     */
    public static void bigWarning(Object msg) {
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        final String border = "****************************************";
        warn("");
        warn(border);
        warn("* Warning! - " + String.valueOf(msg));
        warn(border);
        for (int i = 2; (i < 8) && (i < trace.length); i++) {
            warn(String.format("*   at %s%s", trace[i].toString(), (i < 7) && (i < (trace.length - 1)) ? "..." : ""));
        }
        warn(border);
        warn("");
    }

    /**
     * Indicates that although the program can continue as expected, <br />
     * the program may act unexpectedly due to receiving a valid, but <br />
     * unexpected result or value.
     */
    public static void warn(Object msg) {
        log(msg, Level.WARN);
    }

    /**
     * Indicates an Error that is recoverable but should be noted to the <br />
     * user or programmer since this is likely a programmer error. <br />
     */
    public static void error(Object msg) {
        log(msg, Level.ERROR);
    }

    /**
     * Indicates a Fatal Error that has caused the program to terminate <br />
     * since the error is unrecoverable.
     */
    public static void fatal(Object msg) {
        log(msg, Level.FATAL);
    }

    /**
     * Write an Exception to the Log with the full Stack Trace.
     */
    public static void exception(Throwable e) {
        exception(e, null);
    }

    /**
     * Write an Exception to the Log with the full Stack Trace and the given details.
     */
    public static void exception(Throwable e, Object details) {
        final StackTraceElement[] elements = e.getStackTrace();
        final String header = "===============EXCEPTION===============";
        final String separator = "=======================================";
        final Throwable t = e.getCause();

        error(header);
        error("Exception of type " + e.getClass().getName() + " caught!");
        error("Error Message: " + e.getLocalizedMessage());
        if (details != null) {
            error(String.valueOf(details));
        }
        error(separator);
        error("Stack Trace: ");
        error("   " + elements[0]);
        for (int i = 1; i < elements.length; i++) {
            error(" \t" + elements[i]);
        }

        if (t != null) {
            cause(t, t.getLocalizedMessage() + " causing a " + e.getClass().getName(), 1);
        }

        error(header);
    }

    private static void cause(Throwable t, String details, int depth) {
        final StackTraceElement[] elements = t.getStackTrace();
        final String tabs = Strings.repeat("\t", depth);
        final String causer = tabs + "===============CAUSED BY===============";
        final String separator = tabs + "=======================================";
        final Throwable cause = t.getCause();

        error(causer);
        error(tabs + "Exception of type " + t.getClass().getName() + "!");
        error(tabs + "Error Message: " + t.getLocalizedMessage());
        if (details != null) {
            error(tabs + String.valueOf(details));
        }
        error(separator);
        error(tabs + "Stack Trace: ");
        error(tabs + "   " + elements[0]);
        for (int i = 1; i < elements.length; i++) {
            error(" " + tabs + "\t" + elements[i]);
        }

        if (cause != null) {
            cause(cause, cause.getLocalizedMessage() + " causing a " + t.getClass().getName(), ++depth);
        }

        error(causer);
    }

    /**
     * Add an Appender as a another Logging Target in addition to <br />
     * the Console and Log File.
     */
    public static synchronized void addLogTarget(Appender appender) {
        logger.addAppender(appender);
    }

    /**
     * Ensures there are no more than MAX_LOG_COUNT files in the Logs directory. <br />
     * <br />
     * This deletes by age and attempts to delete the file immediately. If that <br />
     * fails, then File.deleteOnExit() is called and the program resumes. This <br />
     * is likely a temporary implementation since, at the moment, a user editing <br />
     * or opening a log file will likely cause the file.lastModified() value to <br />
     * change. This may or may not change in the future to account for said problem.
     */
    private static void initialize() throws IOException {

        Path[] paths = Files.list(LOG_DIR)
                .filter((path) -> path.getFileName().toString().endsWith(".log"))
                .sorted((p1, p2) -> {
                    try {
                        return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
                    } catch (IOException e) {
                        throw new IORuntimeException(e.getMessage(), e);
                    }
                }).toArray(Path[]::new);

        // Delete Log Files until we have 10 or Less
        int size = paths.length;
        for (int i = 0; (size > MAX_LOG_COUNT) && (i < paths.length); i++) {
            Files.delete(paths[i]);
            size--;
        }
    }

    /**
     * Get a PrintStream connected to this Logging Utility. <br />
     * <br />
     * If combined == true then a PrintStream will be returned that will print to <br />
     * all Appenders which would be the Console, Log File and any Appenders added
     * using {@link #addLogTarget(Appender)}. <br />
     * <br />
     * If combined == false then a PrintStream will be returned connected ONLY to the
     * Log File.
     *
     * @param combined - Whether or not to return a "combined" PrintStream
     */
    public static synchronized PrintStream getLogPrintStream(boolean combined) {
        // This is really simple.
        // We only want ONE instance of these streams to EVER exist.

        if (combined) {
            if (combinedStream == null) {
                // When Printing simply pass the text to the Logger
                // let it handle formatting.
                combinedStream = new PrintStream(System.out) {
                    // Pre-compile End Line Pattern
                    Pattern pattern = Pattern.compile("(\r?\n)+");

                    @Override
                    public void print(String s) {
                        Console.info(s);
                    }

                    @Override
                    public void print(Object obj) {
                        Console.info(obj);
                    }

                    @Override
                    public void println(String s) {
                        // Since having Line Endings would destroy the look of the
                        // Log
                        // replace all of the line endings with " "
                        Console.info(this.pattern.matcher(s).replaceAll(" "));
                    }

                    @Override
                    public void println(Object obj) {
                        println(String.valueOf(obj));
                    }
                };
            }

            return combinedStream;
        } else {
            if (logStream == null) {
                try {
                    // Even Easier, Set up a PrintStream for the LogFile only.
                    // Simply alter it to use a separate logger.
                    logStream = new PrintStream(logFile.toFile()) {
                        Logger logger = Logger.getLogger(Ref.APP_NAME + "-Stream");    // Stream
                        // Logger
                        Pattern pattern = Pattern.compile("(\r?\n)+");        // Pre-Compiled
                        // Pattern

                        // On Initialization, let the new Logger use the old Loggers
                        // RollingFileAppender
                        {
                            logger.addAppender(Console.logger.getAppender(Ref.APP_NAME + "-LogFileAppender"));
                        }

                        @Override
                        public void print(String s) {
                            this.logger.info(s);
                        }

                        @Override
                        public void print(Object obj) {
                            print(String.valueOf(obj));
                        }

                        @Override
                        public void println(String s) {
                            // Replace All \r or \n or \r\n with
                            // " " to prevent it of destroying log readability
                            s = this.pattern.matcher(s).replaceAll(" ");
                            this.logger.info(s);
                        }

                        @Override
                        public void println(Object obj) {
                            println(String.valueOf(obj));
                        }

                    };
                } catch (final FileNotFoundException e) {
                    Console.exception(e);
                }
            }
            return logStream;
        }
    }
}
