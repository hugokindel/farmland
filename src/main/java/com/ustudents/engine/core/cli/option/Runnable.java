package com.ustudents.engine.core.cli.option;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.option.annotation.Command;
import com.ustudents.engine.core.cli.option.annotation.Option;
import com.ustudents.engine.core.cli.print.Out;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class defines a runnable command, which means it can read arguments back from the CLI and set any @Option
 * attributes. Some syntax is inspired from: https://picocli.info */
@SuppressWarnings({"unused"})
public abstract class Runnable {
    /** Option to show the help message. */
    @Option(names = {"-h", "--help"}, description = "Show this help message.")
    protected boolean showHelp;

    /** Option to show the version message. */
    @Option(names = {"-v", "--version"}, description = "Show the version number.")
    protected boolean showVersion;

    /** Class constructor. */
    public Runnable() {
        showHelp = false;
        showVersion = false;
    }

    /**
     * Run this command.
     *
     * @param args The arguments.
     * @return the return code.
     */
    public abstract int run(String[] args);

    /**
     * Read every arguments provided and try to see if any option is corresponding to define their values.
     *
     * @param args The arguments.
     * @param classWithArgs The child's class.
     * @param <T> The type of the child class.
     */
    protected <T extends Runnable> boolean readArguments(String[] args, Class<T> classWithArgs) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(Runnable.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(classWithArgs.getDeclaredFields()));
        if (classWithArgs.getSuperclass() == Game.class) {
            fields.addAll(Arrays.asList(classWithArgs.getSuperclass().getDeclaredFields()));
        }
        fields.removeIf(field -> !field.isAnnotationPresent(Option.class));

        for (String arg : args) {
            if (arg.startsWith("-")) {
                String[] parts = arg.split("=");
                boolean found = false;

                for (Field field : fields) {
                    for (String name : field.getAnnotation(Option.class).names()) {
                        if (parts[0].equals(name)) {
                            found = true;

                            try {
                                field.setAccessible(true);
                                if (field.getType() == boolean.class) {
                                    field.set(this, true);
                                } else if (parts.length == 1) {
                                    Out.printlnError("Option '" + name + "' called with no values when one was expected (please use the -h command).");
                                    return false;
                                } else {
                                    field.set(this, parse(parts[1], field.getType()));
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                    }

                    if (found) {
                        break;
                    }
                }

                if (!found) {
                    displayUnknownOption(parts[0], fields);
                }
            }
        }

        if (showHelp) {
            displayHelp(classWithArgs, fields);
        }

        if (showVersion) {
            displayVersion(classWithArgs);
        }

        return true;
    }

    /**
     * Shows the unknown option along with the closest one (if found).
     *
     * @param unknownOption The unknown option.
     * @param fields The fields to search in.
     */
    private void displayUnknownOption(String unknownOption, ArrayList<Field> fields) {
        int distance = -1;
        String nearest = "";

        for (Field field : fields) {
            Option option = field.getAnnotation(Option.class);

            for (String name : option.names()) {
                int optionDistance = calculateLevenshteinDistance(unknownOption, name);

                if (distance == -1 || optionDistance < distance) {
                    distance = optionDistance;
                    nearest = name;
                }
            }
        }

        Out.println("Unknown option '" + unknownOption + "'!");

        if (!nearest.isEmpty()) {
            Out.println("Did you mean '" + nearest + "'?");
        }
    }

    /**
     * Shows the program's version.
     *
     * @param classWithArgs The child's class.
     * @param <T> The type of the child's class.
     */
    private <T extends Runnable> void displayVersion(Class<T> classWithArgs) {
        Out.println("Version: " + classWithArgs.getAnnotation(Command.class).version());
    }

    /**
     * Shows the program's help message.
     *
     * @param classWithArgs The child's class.
     * @param fields The list of fields to show.
     * @param <T> The type of the child's class.
     */
    private <T extends Runnable> void displayHelp(Class<T> classWithArgs, ArrayList<Field> fields) {
        Out.println("usage: ./" + classWithArgs.getAnnotation(Command.class).name() + " [options...]");

        Out.println();

        for (String line : classWithArgs.getAnnotation(Command.class).description()) {
            Out.println(line);
        }

        Out.println();

        Out.println("Options:");
        for (Field field : fields) {
            Option option = field.getAnnotation(Option.class);
            int numberOfNames = option.names().length;

            Out.print(" \t");

            for (int i = 0; i < numberOfNames; i++) {
                Out.print(option.names()[i] + (i == numberOfNames - 1 ? "" : ", "));
            }

            if (!option.usage().isEmpty()) {
                Out.print("=" + option.usage());
            }

            Out.println();

            for (String line : option.description()) {
                Out.print(" \t\t");
                Out.println(line);
            }
        }
    }

    /**
     * Calculate the levenshtein distance.
     * Implementation from: http://rosettacode.org/wiki/Levenshtein_distance#Java
     *
     * @param s1 The first string to compare.
     * @param s2 The second string to compare.
     * @return the distance between s1 and s2.
     */
    public static int calculateLevenshteinDistance(String s1, String s2) {
        if(s1.length() == 0) {
            return s2.length();
        } else if(s2.length() == 0) {
            return s1.length();
        }

        if(s1.charAt(0) == s2.charAt(0)) {
            return calculateLevenshteinDistance(s1.substring(1), s2.substring(1));
        }

        int a = calculateLevenshteinDistance(s1.substring(1), s2.substring(1));
        int b = calculateLevenshteinDistance(s1, s2.substring(1));
        int c = calculateLevenshteinDistance(s1.substring(1), s2);

        if(a > b) {
            a = b;
        }

        if(a > c) {
            a = c;
        }

        return a + 1;
    }

    /**
     * Parse the value of an option.
     *
     * @param value The value (what comes after the '=' character in an option).
     * @param classType The class type of the option.
     * @return the value converted to the class type of the option.
     */
    public static Object parse(String value, Class<?> classType) {
        if (classType == String[].class) {
            return value.split(",");
        } else if (classType == String.class) {
            return value;
        } else if (classType == char.class) {
            return value.charAt(0);
        } else if (classType == boolean.class) {
            return !value.equals("0");
        } else if (classType == int.class) {
            return Integer.parseInt(value);
        } else if (classType == byte.class) {
            return Byte.parseByte(value);
        } else if (classType == short.class) {
            return Short.parseShort(value);
        } else if (classType == long.class) {
            return Long.parseLong(value);
        } else if (classType == float.class) {
            return Float.parseFloat(value);
        } else if (classType == double.class) {
            return Double.parseDouble(value);
        } else if (Arrays.stream(classType.getInterfaces()).anyMatch(t -> t == Parsable.class)) {
            try {
                return classType.getMethod("parseFromOption", String.class).invoke(null, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Out.printlnError("Cannot parse an unknown type.");

        return null;
    }
}
