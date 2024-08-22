package com.prix.homepage.backend.basic.utils;

public class PathUtil {

    public static String getGlobalDirectoryPath(String path) {
        String os = System.getProperty("os.name").toLowerCase();
        String homeDir = System.getProperty("user.home");

        if (os.contains("win")) {
            path = path.replace("/home", homeDir).replace("/", "\\");
        } else {
            path = path.replace("/home", homeDir);
        }

        return path;
    }
}
