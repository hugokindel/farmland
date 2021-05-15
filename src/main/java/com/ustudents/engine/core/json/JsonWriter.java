package com.ustudents.engine.core.json;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.utility.Pair;
import com.ustudents.engine.utility.StringUtil;
import com.ustudents.engine.utility.Triplet;
import org.joml.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    boolean newLine = true;

    boolean tab = true;

    boolean space = true;

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

    public static void writeToFile(String filepath, Map<String, Object> map, String message) {
        try {
            JsonWriter writer = new JsonWriter(filepath);
            writer.writeMessage(message);
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

    public static String writeToString(Map<String, Object> map, boolean newLine, boolean tab, boolean space) {
        try {
            JsonWriter writer = new JsonWriter();
            writer.newLine = newLine;
            writer.tab = tab;
            writer.space = space;
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

    private void writeMessage(String message) {
        write(message, false);
    }

    /**
     * Writes a map.
     *
     * @param map The map to write.
     */
    private void writeMap(Map<String, Object> map) {
        parentIsArray = false;

        if (map.isEmpty()) {
            write("{", false);
        } else {
            write("{" + (newLine ? "\n" : ""), false);
        }

        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");

        int i = 1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            writeMapElement(entry);

            if (i != map.size()) {
                write(",", false);
            }

            write((newLine ? "\n" : ""), false);

            i++;
        }

        prefix = oldPrefix;

        if (map.isEmpty()) {
            write("}", false);
        } else {
            write("}");
        }
    }

    /**
     * Writes a map element.
     *
     * @param element The element pair (string, object) to write.
     */
    private void writeMapElement(Map.Entry<String, Object> element) {
        write("\"" + element.getKey() + "\":" + (space ? " " : ""));
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
            if (!array.isEmpty() && array.get(0).getClass().isAnnotationPresent(JsonSerializable.class)) {
                write("[" + (newLine ? "\n" : "") + prefix + (tab ? "\t" : ""), false);
            } else {
                write("[", false);
            }
        } else {
            if (array.isEmpty()) {
                write("[", false);
            } else {
                write("[" + (newLine ? "\n" : ""), false);
            }
        }

        if (!array.isEmpty() && array.get(0) instanceof List) {
            parentIsArray = true;
        }

        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");

        int i = 1;
        for (Object object : array) {
            if (beforeParentIsArray && array.get(0) instanceof List) {
                write( (newLine ? "\n" : ""), false);
                write("");
            } else if (!beforeParentIsArray) {
                write("");
            } else if (i != 1 && !array.get(0).getClass().isAnnotationPresent(JsonSerializable.class)) {
                write(" ", false);
            }

            writeValue(object);

            if (i != array.size()) {
                if (beforeParentIsArray && !array.isEmpty() && array.get(0).getClass().isAnnotationPresent(JsonSerializable.class)) {
                    write("," + (newLine ? "\n" : "") + prefix, false);
                } else {
                    write(",", false);
                }
            }

            if (!beforeParentIsArray) {
                write((newLine ? "\n" : "") , false);
            }

            i++;
        }

        prefix = oldPrefix;

        if (beforeParentIsArray && !array.isEmpty() && array.get(0) instanceof List) {
            write((newLine ? "\n" : ""), false);
            write("]");
        } else if (beforeParentIsArray && !array.isEmpty() && array.get(0).getClass().isAnnotationPresent(JsonSerializable.class)) {
            write((newLine ? "\n" : ""), false);
            write("]");
        } else if (!beforeParentIsArray && array.isEmpty()) {
            write("]", false);
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
        if (value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof Long) {
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
        } else if (value instanceof Vector2f) {
            writeVector2f(value);
        } else if (value instanceof Vector3f) {
            writeVector3f(value);
        } else if (value instanceof Vector4f) {
            writeVector4f(value);
        } else if (value instanceof Vector2i) {
            writeVector2i(value);
        } else if (value instanceof Vector3i) {
            writeVector3i(value);
        } else if (value instanceof Vector4i) {
            writeVector4i(value);
        } else if (value instanceof Matrix4f) {
            writeMatrix4f(value);
       } else if (value != null && value.getClass().isEnum()) {
            writeEnum(value);
        } else if (value == null) {
            writeNull();
        } else if (value.getClass().isAnnotationPresent(JsonSerializable.class)) {
            writeMap(Objects.requireNonNull(Json.serialize(value)));
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

    /**
     * Writes a 2D vector of float values.
     *
     * @param value The vector to write.
     */
    private void writeVector2f(Object value) {
        write("{" + (newLine ? "\n" : ""), false);
        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");
        write("\"x\": " + ((Vector2f)value).x + "," + (newLine ? "\n" : ""));
        write("\"y\": " + ((Vector2f)value).y + (newLine ? "\n" : ""));
        prefix = oldPrefix;
        write("}");
    }

    /**
     * Writes a 3D vector of float values.
     *
     * @param value The vector to write.
     */
    private void writeVector3f(Object value) {
        write("{" + (newLine ? "\n" : ""), false);
        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");
        write("\"x\":" + (space ? " " : "") + ((Vector3f)value).x + "," + (newLine ? "\n" : ""));
        write("\"y\":" + (space ? " " : "") + ((Vector3f)value).y + "," + (newLine ? "\n" : ""));
        write("\"z\":" + (space ? " " : "") + ((Vector3f)value).z + (newLine ? "\n" : ""));
        prefix = oldPrefix;
        write("}");
    }

    /**
     * Writes a 4D vector of float values.
     *
     * @param value The vector to write.
     */
    private void writeVector4f(Object value) {
        write("{" + (newLine ? "\n" : ""), false);
        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");
        write("\"x\":" + (space ? " " : "") + ((Vector4f)value).x + "," + (newLine ? "\n" : ""));
        write("\"y\":" + (space ? " " : "") + ((Vector4f)value).y + "," + (newLine ? "\n" : ""));
        write("\"z\":" + (space ? " " : "") + ((Vector4f)value).z + "," + (newLine ? "\n" : ""));
        write("\"w\":" + (space ? " " : "") + ((Vector4f)value).w + (newLine ? "\n" : ""));
        prefix = oldPrefix;
        write("}");
    }

    /**
     * Writes a 2D vector of integer values.
     *
     * @param value The vector to write.
     */
    private void writeVector2i(Object value) {
        write("{" + (newLine ? "\n" : ""), false);
        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");
        write("\"x\":" + (space ? " " : "") + ((Vector2i)value).x + "," + (newLine ? "\n" : ""));
        write("\"y\":" + (space ? " " : "") + ((Vector2i)value).y + (newLine ? "\n" : ""));
        prefix = oldPrefix;
        write("}");
    }

    /**
     * Writes a 3D vector of integer values.
     *
     * @param value The vector to write.
     */
    private void writeVector3i(Object value) {
        write("{" + (newLine ? "\n" : ""), false);
        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");
        write("\"x\":" + (space ? " " : "") + ((Vector3i)value).x + "," + (newLine ? "\n" : ""));
        write("\"y\":" + (space ? " " : "") + ((Vector3i)value).y + "," + (newLine ? "\n" : ""));
        write("\"z\":" + (space ? " " : "") + ((Vector3i)value).z + (newLine ? "\n" : ""));
        prefix = oldPrefix;
        write("}");
    }

    /**
     * Writes a 4D vector of integer values.
     *
     * @param value The vector to write.
     */
    private void writeVector4i(Object value) {
        write("{" + (newLine ? "\n" : ""), false);
        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");
        write("\"x\":" + (space ? " " : "") + ((Vector4i)value).x + "," + (newLine ? "\n" : ""));
        write("\"y\":" + (space ? " " : "") + ((Vector4i)value).y + "," + (newLine ? "\n" : ""));
        write("\"z\":" + (space ? " " : "") + ((Vector4i)value).z + "," + (newLine ? "\n" : ""));
        write("\"w\":" + (space ? " " : "") + ((Vector4i)value).w + (newLine ? "\n" : ""));
        prefix = oldPrefix;
        write("}");
    }

    /**
     * Writes a 4x4 matrix of float values.
     *
     * @param value The matrix to write.
     */
    private void writeMatrix4f(Object value) {
        write("{" + (newLine ? "\n" : ""), false);
        String oldPrefix = prefix;
        prefix += "" + (tab ? "\t" : "");
        write("\"m00\":" + (space ? " " : "") + ((Matrix4f)value).m00() + "," + (newLine ? "\n" : ""));
        write("\"m01\":" + (space ? " " : "") + ((Matrix4f)value).m01() + "," + (newLine ? "\n" : ""));
        write("\"m02\":" + (space ? " " : "") + ((Matrix4f)value).m02() + "," + (newLine ? "\n" : ""));
        write("\"m03\":" + (space ? " " : "") + ((Matrix4f)value).m03() + "," + (newLine ? "\n" : ""));
        write("\"m10\":" + (space ? " " : "") + ((Matrix4f)value).m10() + "," + (newLine ? "\n" : ""));
        write("\"m11\":" + (space ? " " : "") + ((Matrix4f)value).m11() + "," + (newLine ? "\n" : ""));
        write("\"m12\":" + (space ? " " : "") + ((Matrix4f)value).m12() + "," + (newLine ? "\n" : ""));
        write("\"m13\":" + (space ? " " : "") + ((Matrix4f)value).m13() + "," + (newLine ? "\n" : ""));
        write("\"m20\":" + (space ? " " : "") + ((Matrix4f)value).m20() + "," + (newLine ? "\n" : ""));
        write("\"m21\":" + (space ? " " : "") + ((Matrix4f)value).m21() + "," + (newLine ? "\n" : ""));
        write("\"m22\":" + (space ? " " : "") + ((Matrix4f)value).m22() + "," + (newLine ? "\n" : ""));
        write("\"m23\":" + (space ? " " : "") + ((Matrix4f)value).m23() + "," + (newLine ? "\n" : ""));
        write("\"m30\":" + (space ? " " : "") + ((Matrix4f)value).m30() + "," + (newLine ? "\n" : ""));
        write("\"m31\":" + (space ? " " : "") + ((Matrix4f)value).m31() + "," + (newLine ? "\n" : ""));
        write("\"m32\":" + (space ? " " : "") + ((Matrix4f)value).m32() + "," + (newLine ? "\n" : ""));
        write("\"m33\":" + (space ? " " : "") + ((Matrix4f)value).m33() + (newLine ? "\n" : ""));
        prefix = oldPrefix;
        write("}");
    }

    private void writeEnum(Object value) {
        write(value.getClass().getName() + "::" + ((Enum)value).name(), false);
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
