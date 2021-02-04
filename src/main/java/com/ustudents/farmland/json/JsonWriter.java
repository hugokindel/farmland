package com.ustudents.farmland.json;

import com.ustudents.farmland.common.StringUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * This class is used to write Json data format.
 * If you need more info on Json, please look at JsonReader. */
@SuppressWarnings({"unchecked", "unused"})
public class JsonWriter {
    /** The output file to print to. */
    private final PrintWriter file;

    /** The current prefix (space alignment) to use. */
    private String prefix = "";

    /** Defines if we currently are writing in an array (only useful for a beautification of two-dimensional arrays). */
    private boolean parentIsArray = false;

    /** The data. */
    private String data = "";

    /** Class constructor. */
    private JsonWriter() {
        file = null;
    }

    /**
     * Class constructor.
     *
     * @param filepath The file path to use.
     */
    private JsonWriter(String filepath) throws FileNotFoundException {
        file = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filepath), StandardCharsets.UTF_8)));
    }

    /**
     * Writes a map to file in NJSon format.
     *
     * @param filepath The file path to use.
     * @param map The map to write.
     */
    public static void writeToFile(String filepath, Map<String, Object> map) {
        try {
            JsonWriter writer = new JsonWriter(filepath);
            writer.writeMap(map);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a list to file in NJSon format.
     *
     * @param filepath The file path to use.
     * @param array The array to write.
     */
    public static void writeToFile(String filepath, List<Object> array) {
        try {
            JsonWriter writer = new JsonWriter(filepath);
            writer.writeArray(array);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a map to a string in NJSon format.
     *
     * @param map The map to write.
     */
    public static String writeToString(Map<String, Object> map) {
        try {
            JsonWriter writer = new JsonWriter();
            writer.writeMap(map);
            return writer.data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Writes a list to a string in NJSon format.
     *
     * @param array The array to write.
     */
    public static String  writeToString(List<Object> array) {
        try {
            JsonWriter writer = new JsonWriter();
            writer.writeArray(array);
            return writer.data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Writes a map.
     *
     * @param map The map to write.
     */
    private void writeMap(Map<String, Object> map) {
        parentIsArray = false;

        write("{\n", false);

        String oldPrefix = prefix;
        prefix += "\t";

        int i = 1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            writeMapElement(entry);

            if (i != map.size()) {
                write(",", false);
            }

            write("\n", false);

            i++;
        }

        prefix = oldPrefix;

        write("}");
    }

    /**
     * Writes a map element.
     *
     * @param element The element pair (string, object) to write.
     */
    private void writeMapElement(Map.Entry<String, Object> element) {
        write("\"" + element.getKey() + "\": ");
        writeValue(element.getValue());
    }

    /**
     * Writes an array.
     * Takes care of space alignment with a tweak for two-dimensional arrays as they are used a lot through our data.
     *
     * @param array The array to write.
     */
    private void writeArray(List<Object> array) {
        boolean beforeParentIsArray = parentIsArray;

        if (beforeParentIsArray) {
            write("[", false);
        } else {
            write("[\n", false);
        }

        if (!array.isEmpty() && array.get(0) instanceof List) {
            parentIsArray = true;
        }

        String oldPrefix = prefix;
        prefix += "\t";

        int i = 1;
        for (Object object : array) {
            if (beforeParentIsArray && array.get(0) instanceof List) {
                write("\n", false);
                write("");
            } else if (!beforeParentIsArray) {
                write("");
            } else if (i != 1) {
                write(" ", false);
            }

            writeValue(object);

            if (i != array.size()) {
                write(",", false);
            }

            if (!beforeParentIsArray) {
                write("\n", false);
            }

            i++;
        }

        prefix = oldPrefix;

        if (beforeParentIsArray && array.get(0) instanceof List) {
            write("\n", false);
            write("]");
        } else {
            write("]", !beforeParentIsArray);
        }

        if (!array.isEmpty() && array.get(0) instanceof List) {
            parentIsArray = false;
        }
    }

    /**
     * Writes a value of any supported type (integer, double, string, character, list, map).
     *
     * @param value The value to write.
     */
    private void writeValue(Object value) {
        if (value instanceof Integer || value instanceof Double) {
            writeNumber(value);
        } else if (value instanceof String) {
            writeString(value);
        } else if (value instanceof Character) {
            writeCharacter(value);
        } else if (value instanceof List) {
            writeArray((List<Object>)value);
        } else if (value instanceof Map) {
            writeMap((Map<String, Object>)value);
        } else if (value instanceof Boolean) {
            writeBoolean(value);
        } else if (value == null) {
            writeNull();
        }
    }

    /**
     * Writes a number.
     *
     * @param value The number to write.
     */
    private void writeNumber(Object value) {
        write(value.toString(), false);
    }

    /**
     * Writes a string.
     *
     * @param value The string to write.
     */
    private void writeString(Object value) {
        write("\"" + StringUtil.getUnescaped(value.toString()) + "\"", false);
    }

    /**
     * Writes a character.
     *
     * @param value The character to write.
     */
    private void writeCharacter(Object value) {
        write("'" + StringUtil.getUnescaped(value.toString()) + "'", false);
    }

    /** Writes a null value. */
    private void writeBoolean(Object object) {
        boolean bool = (boolean) object;
        write(bool ? "true" : "false", false);
    }

    /** Writes a null value. */
    private void writeNull() {
        write("null", false);
    }

    /** Closes the file. */
    private void close() {
        if (file != null) {
            file.write(data);
            file.close();
        }
    }

    /**
     * Write a text to file.
     *
     * @param text The text to print.
     */
    private void write(String text) {
        write(text, true);
    }

    /**
     * Write a text to file.
     *
     * @param text The text to print.
     * @param usePrefix Defines if we should write the prefix first.
     */
    private void write(String text, boolean usePrefix) {
        data += (usePrefix ? prefix : "") + text;
    }
}
