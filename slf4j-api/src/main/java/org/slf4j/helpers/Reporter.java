package org.slf4j.helpers;

import java.io.PrintStream;

/**
 * An internally used class for reporting internal messages generated by SLF4J itself during initialization.
 *
 * <p>
 * Internal reporting is performed by calling the {@link #info(String)}, {@link #warn(String)} (String)}
 * {@link #error(String)} (String)}  and {@link  #error(String, Throwable)} methods.
 * </p>
 * <p>See {@link #SLF4J_INTERNAL_VERBOSITY_KEY} and {@link #SLF4J_INTERNAL_REPORT_STREAM_KEY} for
 * configuration options.</p>
 * <p>
 * <p>
 * Note that this system is independent of the logging back-end in use.
 *
 * @since 2.0.10
 */
public class Reporter {

    /**
     * this class is used internally by Reporter
     */
    private enum Level {
        INFO(1), WARN(2), ERROR(3);

        int levelInt;

        private Level(int levelInt) {
            this.levelInt = levelInt;
        }

        private int getLevelInt() {
            return levelInt;
        }
    }

    private enum TargetChoice {
        Stderr, Stdout;
    }

    static final String SLF4J_INFO_PREFIX = "SLF4J(I): ";
    static final String SLF4J_WARN_PREFIX = "SLF4J(W): ";
    static final String SLF4J_ERROR_PREFIX = "SLF4J(E): ";


    /**
     * This system property controls the target for internal reports output by SLF4J.
     * Recognized values for this key are "System.out", "stdout", "sysout", "System.err",
     * "stderr" and "syserr".
     *
     * <p>By default, output is directed to "stderr".</p>
     */
    public static final String SLF4J_INTERNAL_REPORT_STREAM_KEY = "slf4j.internal.report.stream";
    static private final String[] SYSOUT_KEYS = {"System.out", "stdout", "sysout"};

    /**
     * This system property controls the internal level of chattiness
     * of SLF4J. Recognized settings are "INFO", "WARN" and "ERROR". The default value is "INFO".
     */
    public static final String SLF4J_INTERNAL_VERBOSITY_KEY = "slf4j.internal.verbosity";


    static private final TargetChoice TARGET_CHOICE = initTargetChoice();

    static private final Level INTERNAL_VERBOSITY = initVerbosity();

    static private TargetChoice initTargetChoice() {
        String reportStreamStr = System.getProperty(SLF4J_INTERNAL_REPORT_STREAM_KEY);

        if(reportStreamStr == null || reportStreamStr.isEmpty()) {
            return TargetChoice.Stderr;
        }

        for(String s : SYSOUT_KEYS) {
            if(s.equalsIgnoreCase(reportStreamStr))
                return TargetChoice.Stdout;
        }
        return TargetChoice.Stderr;
    }


    static private Level initVerbosity() {
        String verbosityStr = System.getProperty(SLF4J_INTERNAL_VERBOSITY_KEY);

        if(verbosityStr == null || verbosityStr.isEmpty()) {
            return Level.INFO;
        }

        if(verbosityStr.equalsIgnoreCase("ERROR")) {
            return Level.ERROR;
        }


        if(verbosityStr.equalsIgnoreCase("WARN")) {
            return Level.WARN;
        }

        return Level.INFO;
    }

    static boolean isEnabledFor(Level level) {
        return (level.levelInt >= INTERNAL_VERBOSITY.levelInt);
    }

    static private PrintStream getTarget() {
        switch(TARGET_CHOICE) {
            case Stdout:
                return System.out;
            case Stderr:
            default:
                return System.err;
        }
    }

    /**
     * Report an internal message of level INFO. Message text is prefixed with the string "SLF4J(I)", with
     * (I) standing as a shorthand for INFO.
     *
     * <p>Messages of level INFO are be enabled when the {@link #SLF4J_INTERNAL_VERBOSITY_KEY} system property is
     * set to "INFO" and disabled when set to "WARN" or "ERROR". By default, {@link #SLF4J_INTERNAL_VERBOSITY_KEY} is
     * set to "INFO".</p>
     *
     * @param msg the message text
     */
    public static void info(String msg) {
        if(isEnabledFor(Level.INFO)) {
            getTarget().println(SLF4J_INFO_PREFIX + msg);
        }
    }


    /**
     * Report an internal message of level "WARN". Message text is prefixed with the string "SLF4J(W)", with
     * (W) standing as a shorthand for  WARN.
     *
     * <p>Messages of level WARN are be enabled when the {@link #SLF4J_INTERNAL_VERBOSITY_KEY} system property is
     * set to "INFO" or "WARN" and disabled when set to "ERROR". By default, {@link #SLF4J_INTERNAL_VERBOSITY_KEY} is
     * set to "INFO".</p>
     *
     * @param msg the message text
     */
    static final public void warn(String msg) {
        if(isEnabledFor(Level.WARN)) {
            getTarget().println(SLF4J_WARN_PREFIX + msg);
        }
    }


    /**
     * Report an internal message of level "ERROR  accompanied by a {@link Throwable}.
     * Message text is prefixed with the string "SLF4J(E)", with (E) standing as a shorthand for ERROR.
     *
     * <p>Messages of level ERROR are always enabled.
     *
     * @param msg the message text
     * @param t a Throwable
     */
    static final public void error(String msg, Throwable t) {
        // error cannot be disabled
        getTarget().println(SLF4J_ERROR_PREFIX + msg);
        getTarget().println(SLF4J_ERROR_PREFIX + "Reported exception:");
        t.printStackTrace(getTarget());
    }


    /**
     * Report an internal message of level "ERROR". Message text is prefixed with the string "SLF4J(E)", with
     * (E)  standing as a shorthand for ERROR.
     *
     * <p>Messages of level ERROR are always enabled.
     *
     * @param msg the message text
     */

    static final public void error(String msg) {
        // error cannot be disabled
        getTarget().println(SLF4J_ERROR_PREFIX + msg);
    }
}