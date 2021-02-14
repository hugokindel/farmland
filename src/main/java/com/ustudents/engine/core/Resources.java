package com.ustudents.engine.core;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.Font;
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
    private static final String playersDirectoryName = "players";
    private static final String fontsDirectoryName = "fonts";
    private static final String settingsFilename = "settings.json";
    private static final ReentrantReadWriteLock settingsLock = new ReentrantReadWriteLock();
    private static final Lock settingsReadLock = settingsLock.readLock();
    private static final Lock settingsWriteLock = settingsLock.writeLock();
    private static Map<String, Object> settings;
    private static Map<String, Shader> shaders;
    private static Map<String, Texture> textures;
    private static Map<String, Map<Integer, Font>> fonts;

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

    public static String getFontsDirectory() {
        return createPathIfNeeded(getDataDirectory() + "/" + fontsDirectoryName);
    }

    public static String getPlayersDirectoryName(){
        return createPathIfNeeded(getDataDirectory() + "/" + playersDirectoryName);
    }

    public static String getKindPlayerDirectoryName(String type){
        return createPathIfNeeded(getPlayersDirectoryName() + "/" + type);
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
            unloadShader(shaderSet.getKey(), false);
        }

        shaders.clear();

        for (Map.Entry<String, Texture> texuresSet : textures.entrySet()) {
            unloadTexture(texuresSet.getKey(), false);
        }

        textures.clear();

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
            if (Game.isDebugging()) {
                Out.printlnDebug("Shader loaded: " + getShadersDirectory() + "/" + fileName + ".(vert|frag)");
            }

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

        Shader shader = shaders.get(fileName);

        if (shader.isDestroyed()) {
            shaders.remove(fileName);
            return loadShader(fileName);
        }

        return shaders.get(fileName);
    }

    public static Shader getShader(String fileName) {
        return shaders.get(fileName);
    }

    public static void unloadShader(String fileName) {
        unloadShader(fileName, true);
    }

    static void unloadShader(String fileName, boolean removeFromList) {
        if (shaders.containsKey(fileName)) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Shader unloaded: " + getShadersDirectory() + "/" + fileName + ".(vert|frag)");
            }

            shaders.get(fileName).destroy();

            if (removeFromList) {
                shaders.remove(fileName);
            }
        }
    }

    public static Texture loadTexture(String filePath) {
        if (!textures.containsKey(filePath)) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Texture loaded: " + getTexturesDirectory() + "/" + filePath + "");
            }

            textures.put(filePath, new Texture(getTexturesDirectory() + "/" + filePath));
        }

        Texture texture = textures.get(filePath);

        if (texture.isDestroyed()) {
            textures.remove(filePath);
            return loadTexture(filePath);
        }

        return textures.get(filePath);
    }

    public static Texture getTexture(String filePath) {
        return textures.get(filePath);
    }

    public static void unloadTexture(String filePath) {
        unloadTexture(filePath, true);
    }

    static void unloadTexture(String filePath, boolean removeFromList) {
        if (textures.containsKey(filePath)) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Texture unloaded: " + getTexturesDirectory() + "/" + filePath + "");
            }

            textures.get(filePath).destroy();

            if (removeFromList) {
                textures.remove(filePath);
            }
        }
    }

    public static Font loadFont(String filePath, int fontSize) {
        if (!fonts.containsKey(filePath)) {
            fonts.put(filePath, new HashMap<>());
        }

        if (!fonts.get(filePath).containsKey(fontSize)) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Font loaded: " + getFontsDirectory() + "/" + filePath + "");
            }

            fonts.get(filePath).put(fontSize, new Font(getFontsDirectory() + "/" + filePath, fontSize));
        }

        Font font = fonts.get(filePath).get(fontSize);

        if (font.isDestroyed()) {
            fonts.remove(filePath);
            return loadFont(filePath, fontSize);
        }

        return font;
    }

    public static Font getFont(String filePath, int fontSize) {
        return fonts.get(filePath).get(fontSize);
    }

    public static void unloadFont(String filePath, int fontSize) {
        unloadFont(filePath, fontSize, true);
    }

    static void unloadFont(String filePath, int fontSize, boolean removeFromList) {
        if (fonts.containsKey(filePath) && fonts.get(filePath).containsKey(fontSize)) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Font unloaded: " + getFontsDirectory() + "/" + filePath + "");
            }

            fonts.get(filePath).get(fontSize).destroy();

            if (removeFromList) {
                fonts.get(filePath).remove(fontSize);

                if (fonts.get(filePath).isEmpty()) {
                    fonts.remove(filePath);
                }
            }
        }
    }
}