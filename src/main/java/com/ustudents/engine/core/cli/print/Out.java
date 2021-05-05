package com.ustudents.engine.core.cli.print;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.style.Style;
import com.ustudents.engine.core.cli.print.style.TextColor;
import com.ustudents.engine.core.Resources;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Contains every functions to send output to the user.
 * For it to work properly, the terminal used for running the program should support ANSI escape sequences,
 * most UNIX terminals should support it, and modern Windows Powershell and cmd.exe should too. */
@SuppressWarnings({"unused"})
public class Out {
    /** The prefix to use for every messages. */
    private static final String basePrefix = "[" + Game.getInstanceName().toLowerCase() + "]";

    /** The prefix to use for every info. */
    private static final String infoPrefix = "[info]";

    /** The prefix to use for every warnings. */
    private static final String warningPrefix = "[warning]";

    /** The prefix to use for every errors. */
    private static final String errorPrefix = "[error]";

    /** The prefix to use for every debugs.. */
    private static final String debugPrefix = "[debug]";

    /** The prefix to use for every verbose. */
    private static final String verbosePrefix = "[verbose]";

    /** The maximum number of log files in the log folder to authorize without deletion. */
    private static final int maxNumOfLogFiles = 8;

    /** The output file to print to. */
    private static PrintWriter fileOutput;

    /** Defines if we are at the start of a line (to know if we need to prefix the printing). */
    private static boolean isStartOfLine;

    /** Defines if we should authorize the print of ANSI codes or remove them. */
    private static boolean noAnsiCode = false;

    /**
     * Starts the output system.
     *
     * Needed at the start of the program!
     * It creates the logging file, and initialize various informations.
     */
    public static void start(String[] args, boolean clear, boolean useAnsiCode) {
        boolean helpOrVersion = Arrays.stream(args)
                .anyMatch(s -> s.equals("-h") || s.equals("--help") ||s.equals("-v") || s.equals("--version"));

        // Fix to check if we are running inside IntelliJ IDEA,
        // this information is used to avoid calling any clearing process from inside the IDE terminal,
        // because if we do, it displays an unknown "" symbol.
        boolean inIntelliJ = false;
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if ((arg.startsWith("-javaagent") && arg.contains("JetBrains")) || arg.startsWith("-Dide=JetBrains")) {
                inIntelliJ = true;
                break;
            }
        }

        // Fix permitting to use ANSI Escape sequences on Windows Powershell and cmd (at least on updated Windows 10).
        // and also clear the terminal (on Windows and UNIX platforms).
        if (!inIntelliJ) {
            try {
                if (!helpOrVersion) {
                    if (clear) {
                        Runtime.getRuntime().exec("clear");
                    }
                } else {
                    noAnsiCode = true;
                }
            } catch (Exception ignored) {

            }
        }

        if (!useAnsiCode) {
            noAnsiCode = true;
        }

        try {
            removeOldestLogFile();
            fileOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    Resources.getLogsDirectory() +
                            "/" + Game.getInstanceName().toLowerCase() +
                            "-" + new SimpleDateFormat("yyyy_MM_dd-HH-mm-ss")
                            .format(new Date()) + ".log", false), StandardCharsets.UTF_8)));
        } catch (Exception e) {
            fileOutput = null;
        }

        isStartOfLine = true;
    }

    /** Shutdowns the output system.
     *
     * Needed at the end of the program!
     * It saves the logging file.
     */
    public static void end() {
        // Saves and close the output file if it exists.
        try {
            if (fileOutput != null) {
                fileOutput.flush();
                fileOutput.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void print(Object object) {
        printAndResetColor((isStartOfLine ? basePrefix + " ": "") + object);
        isStartOfLine = false;
    }

    /**
     * Prints a new line to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void println(Object object) {
        print(object + "\n");
        isStartOfLine = true;
    }

    /**
     * Prints an error to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printError(Object object) {
        printAndResetColor(TextColor.Red + (isStartOfLine ? basePrefix : "") +
                errorPrefix + Style.Reset + " " + object);
        isStartOfLine = false;
    }

    /**
     * Prints a new line of error to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printlnError(Object object) {
        printError(object + "\n");
        isStartOfLine = true;
    }

    /**
     * Prints a warning to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printWarning(Object object) {
        printAndResetColor(TextColor.Yellow + (isStartOfLine ? basePrefix : "") +
                warningPrefix + Style.Reset + " " + object);
        isStartOfLine = false;
    }

    /**
     * Prints a new line of warning to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printlnWarning(Object object) {
        printWarning(object + "\n");
        isStartOfLine = true;
    }

    /**
     * Prints an info to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printInfo(Object object) {
        printAndResetColor(TextColor.Blue + (isStartOfLine ? basePrefix : "") +
                infoPrefix + Style.Reset + " " + object);
        isStartOfLine = false;
    }

    /**
     * Prints a new line of info to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printlnInfo(Object object) {
        printInfo(object + "\n");
        isStartOfLine = true;
    }

    /**
     * Prints an info to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printDebug(Object object) {
        printAndResetColor(TextColor.White + (isStartOfLine ? basePrefix : "") +
                debugPrefix + Style.Reset + " " + object);
        isStartOfLine = false;
    }

    /**
     * Prints a new line of info to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printlnDebug(Object object) {
        printDebug(object + "\n");
        isStartOfLine = true;
    }

    /**
     * Prints an info to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printVerbose(Object object) {
        printAndResetColor(TextColor.Purple + (isStartOfLine ? basePrefix : "") +
                verbosePrefix + Style.Reset + " " + object);
        isStartOfLine = false;
    }

    /**
     * Prints a new line of info to output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printlnVerbose(Object object) {
        printVerbose(object + "\n");
        isStartOfLine = true;
    }


    /** Print a new line to output. */
    public static void println() {
        print("\n");
        isStartOfLine = true;
    }

    /** Clear the console. */
    public static void clear() {
        System.out.print("\033[H\033[2J");
    }

    /** Simulates a new line (used only for various scenarios in logging file). */
    static void simulateNewLine() {
        isStartOfLine = true;
        fileOutput.println();
    }

    /**
     * Prints to file output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printToFile(Object object) {
        fileOutput.print(object);
    }


    /**
     * Prints a new line to file output.
     *
     * @param object The object (with toString()) to print.
     */
    public static void printlnToFile(Object object) {
        fileOutput.println(object);
    }

    /** @return the prefix to use for every messages. */
    static String getBasePrefix() {
        return basePrefix;
    }

    /** @return the prefix to use for every warnings. */
    public static String getErrorPrefix() {
        return errorPrefix;
    }

    /** @return the prefix to use for every errors. */
    public static String getWarningPrefix() {
        return warningPrefix;
    }

    /**
     * Prints text to both system output and logging file, then reset ANSI color.
     *
     * @param object The object (with toString()) to print.
     */
    private static void printAndResetColor(Object object) {
        String text = object + Style.Reset;

        if (noAnsiCode) {
            System.out.print(getTextWithoutAnsiCode(text));
        } else {
            System.out.print(text);
        }

        if (fileOutput != null) {
            try {
                // We don't want to write ANSI escape sequences on the log file, as it wouldn't be recognized.
                fileOutput.print(getTextWithoutAnsiCode(text));
            } catch (Exception ignored) {

            }
        }
    }

    /** Removes the oldest log files in there is more than 8 log files. */
    private static void removeOldestLogFile() throws Exception {
        File[] logFiles = new File(Resources.getLogsDirectory()).listFiles();

        if (logFiles == null || logFiles.length < maxNumOfLogFiles) {
            return;
        }

        long oldestDate = -1;
        File oldestFile = null;

        for(File file : logFiles) {
            if(oldestDate == -1 || file.lastModified() < oldestDate) {
                oldestDate = file.lastModified();
                oldestFile = file;
            }
        }

        try {
            if (!oldestFile.delete()) {
                throw new Exception("Can't remove log file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the ANSI codes within a string using a regex expression.
     * Implementation from: https://stackoverflow.com/questions/14693701/how-can-i-remove-the-ansi-escape-sequences-from-a-string-in-python
     */
    private static String getTextWithoutAnsiCode(String text) {
        return text.replaceAll("(\\x9B|\\x1B\\[)[0-?]*[ -/]*[@-~]", "");
    }

    public static void canUseAnsiCode(boolean can) {
        noAnsiCode = !can;
    }
}
