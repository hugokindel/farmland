package com.ustudents.engine.core;

import com.ustudents.engine.Game;
import com.ustudents.engine.GameConfig;
import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.Shader;
import com.ustudents.engine.graphic.Spritesheet;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.i18n.Language;
import com.ustudents.engine.utility.FileUtil;
import com.ustudents.engine.utility.StringUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Contains useful function to find various paths for the project's data. */
@SuppressWarnings({"unused"})
public class Resources {
    private static final String dataDirectoryName = "data";
    private static final String logsDirectoryName = "logs";
    private static final String shadersDirectoryName = "shaders";
    private static final String texturesDirectoryName = "textures";
    private static final String fontsDirectoryName = "fonts";
    private static final String soundsDirectoryName = "sounds";
    private static final String savesDirectoryName = "saves";
    private static final String localizationsDirectoryName = "i18n";
    private static final String settingsFilename = "settings.json";
    private static final ReentrantReadWriteLock settingsLock = new ReentrantReadWriteLock();
    private static final Lock settingsReadLock = settingsLock.readLock();
    private static final Lock settingsWriteLock = settingsLock.writeLock();
    private static Map<String, Shader> shaders;
    private static Map<String, Language> languages;
    private static Map<String, Texture> textures;
    private static Map<String, Sound> sounds;
    private static Map<String, Map<Integer, Font>> fonts;
    private static Map<String, Spritesheet> spritesheets;
    private static GameConfig config;
    private static List<String> languagesList;

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

    public static String getSoundsDirectoryName() {
        return createPathIfNeeded(getDataDirectory() + "/" + soundsDirectoryName);
    }

    public static String getSavesDirectoryName() {
        return createPathIfNeeded(getDataDirectory() + "/" + savesDirectoryName);
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

    /** Loads everything. */
    public static void loadAndInitialize() {
        loadConfig();
        loadLanguages();

        shaders = new HashMap<>();
        textures = new HashMap<>();
        fonts = new HashMap<>();
        sounds = new HashMap<>();
        spritesheets = new HashMap<>();
    }

    public static void loadDefaultResources() {
        loadFont("ui/debug.ttf", 16);
        loadSpritesheet("ui/button_default.json");
        loadSpritesheet("ui/button_focused.json");
        loadSpritesheet("ui/button_down.json");
    }

    /** Saves everything. */
    public static void saveAndUnload(String message) {
        for (Map.Entry<String, Shader> shaderSet : shaders.entrySet()) {
            unloadShader(shaderSet.getKey(), false);
        }

        shaders.clear();

        for (Map.Entry<String, Texture> texturesSet : textures.entrySet()) {
            unloadTexture(texturesSet.getKey(), false);
        }

        textures.clear();
        
        for (Map.Entry<String, Map<Integer, Font>> fontMapSet : fonts.entrySet()) {
            for (Map.Entry<Integer, Font> fontSet : fontMapSet.getValue().entrySet()) {
                unloadFont(fontMapSet.getKey(), fontSet.getKey(), false);
            }
        }

        fonts.clear();

        for (Map.Entry<String, Sound> soundSet : sounds.entrySet()) {
            unloadSound(soundSet.getKey(), false);
        }

        sounds.clear();

        spritesheets.clear();

        saveConfig(message);
    }

    /** Loads the settings into memory. */
    private static void loadConfig() {
        if (config == null) {
            try {
                File file = new File(getDataDirectory() + "/" + settingsFilename);

                if (file.exists()) {
                    config = Json.deserialize(getDataDirectory() + "/" + settingsFilename, GameConfig.class);
                } else {
                    config = new GameConfig();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Saves the settings on the hard drive. */
    private static void saveConfig(String message) {
        try {
            if (config != null) {
                Json.serialize(getDataDirectory() + "/" + settingsFilename, config, message);
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
                String vertexShaderCode = FileUtil.readFileToString(getShadersDirectory() + "/" + fileName + ".vert");
                String fragmentShaderCode = FileUtil.readFileToString(getShadersDirectory() + "/" + fileName + ".frag");
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

        return shader;
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
                Out.printlnDebug("Texture loaded: " + getTexturesDirectory() + "/" + filePath);
            }

            textures.put(filePath, new Texture(getTexturesDirectory() + "/" + filePath));
        }

        Texture texture = textures.get(filePath);

        if (texture.isDestroyed()) {
            textures.remove(filePath);
            return loadTexture(filePath);
        }

        return texture;
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
                Out.printlnDebug("Texture unloaded: " + getTexturesDirectory() + "/" + filePath);
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
                Out.printlnDebug("Font loaded: " + getFontsDirectory() + "/" + filePath);
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
                Out.printlnDebug("Font unloaded (size " + fontSize + "px): " + getFontsDirectory() + "/" + filePath);
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

    public static Sound loadSound(String filePath) {
        if (!sounds.containsKey(filePath)) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Sound loaded: " + getSoundsDirectoryName() + "/" + filePath);
            }

            sounds.put(filePath, new Sound(getSoundsDirectoryName() + "/" + filePath));
        }

        Sound sound = sounds.get(filePath);

        if (sound.isDestroyed()) {
            sounds.remove(filePath);
            return loadSound(filePath);
        }

        return sound;
    }

    public static Sound getSound(String filePath) {
        return sounds.get(filePath);
    }

    public static void unloadSound(String filePath) {
        unloadSound(filePath, true);
    }

    static void unloadSound(String filePath, boolean removeFromList) {
        if (sounds.containsKey(filePath)) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Sound unloaded: " + getSoundsDirectoryName() + "/" + filePath);
            }

            sounds.get(filePath).destroy();

            if (removeFromList) {
                sounds.remove(filePath);
            }
        }
    }
    
    public static Spritesheet loadSpritesheet(String filePath) {
        if (!spritesheets.containsKey(filePath)) {
            if (Game.isDebugging()) {
                Out.printlnDebug("Spritesheet loaded: " + getTexturesDirectory() + "/" + filePath);
            }

            spritesheets.put(filePath, Json.deserialize(getTexturesDirectory() + "/" + filePath, Spritesheet.class));
        }

        Spritesheet spritesheet = spritesheets.get(filePath);

        if (spritesheet == null) {
            //spritesheets.remove(filePath);
            //return loadSpritesheet(filePath);

            throw new IllegalStateException("Can't find spritesheet '" + filePath + "'");
        }

        return spritesheet;
    }
    
    public static Spritesheet getSpritesheet(String filePath) {
        return spritesheets.get(filePath);
    }

    public static GameConfig getConfig() {
        return config;
    }

    public static void loadLanguages() {
        languages = new HashMap<>();
        languagesList = new ArrayList<>();

        for (final File fileEntry : new File(getDataDirectory() + "/" + localizationsDirectoryName).listFiles()) {
            languagesList.add(FileUtil.getFileNameWithoutExtension(fileEntry));
            languages.put(FileUtil.getFileNameWithoutExtension(fileEntry), Json.deserialize(fileEntry.getAbsolutePath(), Language.class));
        }
    }

    public static String getLocalizedText(String textId, Object... values) {
        if (languages.containsKey(config.language) && languages.get(config.language).content.containsKey(textId)) {
            return StringUtil.parseValuesFromString(languages.get(config.language).content.get(textId), values, textId);
        }

        return textId;
    }

    public static void chooseNextLanguage() {
        for (int i = 0; i < languagesList.size(); i++) {
            if (languagesList.get(i).equals(config.language)) {
                if (i+1 >= languagesList.size()) {
                    config.language = languagesList.get(0);
                    break;
                }
                else{
                    config.language = languagesList.get(i+1);
                    break;
                }
            }
        }
    }

    public static void chooseDefaultLanguage() {
        config.language = languagesList.get(1);
    }

    public static void resetConfig() {
        config = new GameConfig();
    }
}
