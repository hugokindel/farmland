package com.ustudents.farmland.common;

import com.ustudents.farmland.json.JsonReader;
import com.ustudents.farmland.json.JsonWriter;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Contains useful function to find various paths for the project's data. */
@SuppressWarnings({"unused"})
public class Resources {
    private static final String dataDirectoryName = "data";
    private static final String logsDirectoryName = "logs";
    private static final String settingsFilename = "settings.json";
    private static final ReentrantReadWriteLock settingsLock = new ReentrantReadWriteLock();
    private static final Lock settingsReadLock = settingsLock.readLock();
    private static final Lock settingsWriteLock = settingsLock.writeLock();
    private static Map<String, Object> settings;

    /**
     * Gets the data directory's path.
     *
     * @return the path.
     */
    public static String getDataDirectory() throws Exception {
        return createPathIfNeeded(dataDirectoryName);
    }

    /**
     * Gets the logs directory's path.
     *
     * @return the path.
     */
    public static String getLogsDirectory() throws Exception {
        return createPathIfNeeded(getDataDirectory() + "/" + logsDirectoryName);
    }

    /**
     * Creates a path if needed.
     *
     * @param filepath The path to use.
     * @return the path.
     */
    private static String createPathIfNeeded(String filepath) throws Exception {
        FileUtil.createDirectoryIfNeeded(filepath);
        return filepath;
    }

    /**
     * Gets a specific setting.
     *
     * @param name The name of the setting.
     * @return the object containing the setting's value.
     */
    public static Object getSetting(String name) {
        settingsReadLock.lock();

        try {
            return settings.get(name);
        } finally {
            settingsReadLock.unlock();
        }
    }

    /**
     * Sets the specific setting with the given value.
     *
     * @param name The name of the setting.
     * @param value The value of use.
     */
    public static void setSetting(String name, Object value) {
       settingsWriteLock.lock();

       try {
           settings.put(name, value);
       } finally {
           settingsWriteLock.unlock();
       }
    }

    /** Loads everything. */
    public static void load() {
        loadSettings();
    }

    /** Saves everything. */
    public static void save() {
        saveSettings();
    }

    /** Loads the settings into memory. */
    private static void loadSettings() {
        if (settings == null) {
            try {
                File file = new File(getDataDirectory() + "/" + settingsFilename);

                if (file.exists()) {
                    settings = JsonReader.readMap(getDataDirectory() + "/" + settingsFilename);
                } else {
                    settings = new HashMap<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Saves the settings on the hard drive. */
    private static void saveSettings() {
        try {
            if (settings != null) {
                JsonWriter.writeToFile(getDataDirectory() + "/" + settingsFilename, settings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}