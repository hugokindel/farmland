package com.ustudents.engine.core;

import com.ustudents.engine.graphic.Shader;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.core.json.JsonReader;
import com.ustudents.engine.core.json.JsonWriter;
import com.ustudents.engine.utility.FileUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Contains useful function to find various paths for the project's data. */
@SuppressWarnings({"unused"})
public class Resources {
    private static final String dataDirectoryName = "data";
    private static final String logsDirectoryName = "logs";
    private static final String shadersDirectoryName = "shaders";
    private static final String texturesDirectoryName = "textures";
    private static final String settingsFilename = "settings.json";
    private static final ReentrantReadWriteLock settingsLock = new ReentrantReadWriteLock();
    private static final Lock settingsReadLock = settingsLock.readLock();
    private static final Lock settingsWriteLock = settingsLock.writeLock();
    private static Map<String, Object> settings;
    private static Map<String, Shader> shaders;
    private static Map<String, Texture> textures;

    /**
     * Gets the data directory's path.
     *
     * @return the path.
     */
    public static String getDataDirectory() {
        return createPathIfNeeded(dataDirectoryName);
    }

    /**
     * Gets the logs directory's path.
     *
     * @return the path.
     */
    public static String getLogsDirectory() {
        return createPathIfNeeded(getDataDirectory() + "/" + logsDirectoryName);
    }

    public static String getShadersDirectory() {
        return createPathIfNeeded(getDataDirectory() + "/" + shadersDirectoryName);
    }

    public static String getTexturesDirectory() {
        return createPathIfNeeded(getDataDirectory() + "/" + texturesDirectoryName);
    }

    /**
     * Creates a path if needed.
     *
     * @param filepath The path to use.
     * @return the path.
     */
    private static String createPathIfNeeded(String filepath) {
        try {
            FileUtil.createDirectoryIfNeeded(filepath);
            return filepath;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

        shaders = new HashMap<>();
        textures = new HashMap<>();
    }

    /** Saves everything. */
    public static void saveAndUnload() {
        for (Map.Entry<String, Shader> shaderSet : shaders.entrySet()) {
            shaderSet.getValue().destroy();
        }

        for (Map.Entry<String, Texture> texuresSet : textures.entrySet()) {
            texuresSet.getValue().destroy();
        }

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

    public static Shader loadShader(String fileName) {
        if (!shaders.containsKey(fileName)) {
            try {
                String vertexShaderCode = Files.readString(
                        Paths.get(getShadersDirectory() + "/" + fileName + ".vert"));
                String fragmentShaderCode = Files.readString(
                        Paths.get(getShadersDirectory() + "/" + fileName + ".frag"));
                shaders.put(fileName, new Shader(vertexShaderCode, fragmentShaderCode));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return shaders.get(fileName);
    }

    public static Shader getShader(String fileName) {
        return shaders.get(fileName);
    }

    public static Texture loadTexture(String filePath) {
        if (!textures.containsKey(filePath)) {
            textures.put(filePath, new Texture(getTexturesDirectory() + "/" + filePath));
        }

        return textures.get(filePath);
    }

    public static Texture getTexture(String filePath) {
        return textures.get(filePath);
    }
}