package com.ustudents.farmland.common;

import java.io.File;

public class FileUtil {
    /**
     * Creates directories recursively if needed (meaning one or more folder doesn't exist).
     *
     * @param path The path to use.
     */
    public static void createDirectoryIfNeeded(String path) throws Exception {
        File saveDirectory = new File(path);

        if (!saveDirectory.exists()) {
            if (!saveDirectory.mkdirs()) {
                throw new Exception("Cannot create directory at path: " + path + "!");
            }
        }
    }
}