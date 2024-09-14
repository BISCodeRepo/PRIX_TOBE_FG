package com.prix.homepage.backend.basic.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathUtil {

    private static String libraryPath;

//    실제 경로에 맞게 수정해야합니다.
    public final static String BASE_PATH =
            "/usr/local/server/apache-tomcat-8.0.14/webapps/ROOT";

    public final static String PATH_CONFIG =
            BASE_PATH + "/config/";
    public final static String PATH_SW_RELEASE =
            BASE_PATH + "/software_archive/release/";
    public final static String PATH_SW_DEPRECATED =
            BASE_PATH + "/software_archive/deprecated/";

    public final static String PATH_ACTG_DB =
            BASE_PATH + "/ACTG_db/";

    public final static String PATH_ACTG_LOG =
            BASE_PATH + "/ACTG_log/";

    public  final static String PATH_ACTG_SEARCH =
            BASE_PATH + "/ACTG_SEARCH/";


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
