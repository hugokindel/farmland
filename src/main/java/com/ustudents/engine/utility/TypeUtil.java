package com.ustudents.engine.utility;

import java.util.List;

/** Utility functions for types. */
public class TypeUtil {
    /**
     * Creates an instance of a class with the given arguments.
     *
     * @param classType The class type to create.
     * @param args The arguments to pass to the constructor.
     * @param <T> The type.
     *
     * @return the created variable.
     */
    public static <T> T createInstance(Class<T> classType, Object... args) {
        try {
            Class<?>[] argsTypes = new Class[args.length];

            for (int i = 0; i < argsTypes.length; i++) {
                argsTypes[i] = args[i].getClass();
            }

            return classType.getConstructor(argsTypes).newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
