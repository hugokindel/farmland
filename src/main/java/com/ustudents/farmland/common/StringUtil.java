package com.ustudents.farmland.common;

/** Utility functions for strings. */
public class StringUtil {
    /**
     * Gets an unescaped string from a string.
     *
     * @param string The string to unescape.
     * @return the unescaped string.
     */
    public static String getUnescaped(String string) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);

            if (character == '\b') {
                result.append("\\b");
            } else if (character == '\t') {
                result.append("\\t");
            } else if (character == '\n') {
                result.append("\\n");
            } else if (character == '\f') {
                result.append("\\f");
            } else if (character == '\r') {
                result.append("\\r");
            } else if (character == '\"') {
                result.append("\\\"");
            } else if (character == '\'') {
                result.append("\\'");
            } else if (character == '\\') {
                result.append("\\\\");
            } else {
                result.append(character);
            }
        }

        return result.toString();
    }

    /**
     * Gets an escaped string from a string.
     *
     * @param string The string to unescape.
     * @return the unescaped string.
     */
    public static String getEscaped(String string) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);

            if (character == '\\') {
                character = string.charAt(++i);
                if (character == 'b') {
                    result.append("\b");
                } else if (character == 't') {
                    result.append("\t");
                } else if (character == 'n') {
                    result.append("\n");
                } else if (character == 'f') {
                    result.append("\f");
                } else if (character == 'r') {
                    result.append("\r");
                } else if (character == '"') {
                    result.append("\"");
                } else if (character == '\'') {
                    result.append("'");
                } else if (character == '\\') {
                    result.append("\\");
                }
            } else {
                result.append(character);
            }
        }

        return result.toString();
    }
}
