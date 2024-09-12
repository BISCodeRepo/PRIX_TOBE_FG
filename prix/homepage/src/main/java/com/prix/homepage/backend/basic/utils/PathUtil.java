package com.prix.homepage.backend.basic.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathUtil {

    private static String libraryPath;
    public final static String ROOT_CONFIG = "/usr/local/server/apache-tomcat-8.0.14/webapps/ROOT/config/";

    //실제 위치로 수정해야합니다!
    @Value("${library.path}")
    public void setLibraryPath(String libraryPath) {
        PathUtil.libraryPath = libraryPath;
    }

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

    public static String getLibPath(){
        return libraryPath;
    }
}
