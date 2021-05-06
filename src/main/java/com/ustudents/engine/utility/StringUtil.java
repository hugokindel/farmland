package com.ustudents.engine.utility;

import com.ustudents.engine.core.cli.print.Out;

/** Utility functions for strings. */
public class StringUtil {
    public static String parseValuesFromString(String string, Object[] values) {
        return parseValuesFromString(string, values, null);
    }

    public static String parseValuesFromString(String string, Object[] values, String id) {
        StringBuilder result = new StringBuilder();
        boolean isEscaped = false;
        int currentValue = 0;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c == '\\') {
                isEscaped = true;
            } else {
                if (c == '{') {
                    if (!isEscaped) {
                        StringBuilder valueNumber = new StringBuilder();

                        while (string.charAt(i) != '}') {
                            i++;

                            valueNumber.append(string.charAt(i));
                        }

                        valueNumber.deleteCharAt(valueNumber.length() - 1);

                        if (valueNumber.length() != 0) {
                            currentValue = Integer.parseInt(valueNumber.toString());

                        }

                        if (values.length > currentValue) {
                            result.append(values[currentValue]);
                        } else if (id == null) {
                            Out.printlnError("Not enough arguments to get " + currentValue);
                        } else {
                            Out.printlnError("Not enough arguments to get " + currentValue + " for string '" + id + "'");
                        }

                        currentValue++;
                    } else {
                        result.append("{");
                        isEscaped = false;
                    }
                } else if (isEscaped) {
                    result.append("\\");
                    result.append(c);
                    isEscaped = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

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

    public static String getBetweenFirstAndLast(String text, char first, char last) {
        int firstSep = text.indexOf(first);
        int lastSep = text.lastIndexOf(last);
        return text.substring(firstSep + 1, lastSep);
    }

    public static String removeWhitespaces(String string) {
        return string.replaceAll("\\s","");
    }
}
