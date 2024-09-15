package com.prix.homepage.backend.basic.utils;

import org.springframework.stereotype.Component;

@Component
public class PathUtil {

    // 로컬 경로로 수정 필요
    private final static String LIBRARY_PATH =
            "C:\\\\Users\\\\82108\\\\prix\\\\lib";
    // 로컬 경로로 수정 필요
    public final static String BASE_PATH =
            "/usr/local/server/apache-tomcat-8.0.14/webapps/ROOT";

    //파생 경로들
    //software
    public final static String PATH_CONFIG =
            BASE_PATH + "/config/";
    public final static String PATH_SW_RELEASE =
            BASE_PATH + "/software_archive/release/";
    public final static String PATH_SW_DEPRECATED =
            BASE_PATH + "/software_archive/deprecated/";

    //ACTG
    public final static String PATH_ACTG_DB =
            BASE_PATH + "/ACTG_db/";

    public final static String PATH_ACTG_LOG =
            BASE_PATH + "/ACTG_log/";

    public  final static String PATH_ACTG_SEARCH =
            BASE_PATH + "/ACTG_SEARCH/";

    //DBOND
    public final static String PATH_DBOND_PROCESS_DIR =
            BASE_PATH + "/home/PRIX/data/";
    public final static String PATH_DBOND_PROESS_DB_DIR =
            BASE_PATH + "config";


    //PRIX_DATA_WRITER
    public final static String PATH_DATA_WRITER_LOG_DIR =
            BASE_PATH + "/home/prix/log/";


    //DBOND LIBRARY
    //실제 위치로 수정해야합니다!
//    @Value("${library.path}")
//    public void setLibraryPath(String libraryPath) {
//        PathUtil.libraryPath = libraryPath;
//    }

//    public static String getGlobalDirectoryPath(String path) {
//        String os = System.getProperty("os.name").toLowerCase();
//        String homeDir = System.getProperty("user.home");
//
//        if (os.contains("win")) {
//            path = path.replace("/home", homeDir).replace("/", "\\");
//        } else {
//            path = path.replace("/home", homeDir);
//        }
//
//        return path;
//    }

    public static String getLibPath(){
        return LIBRARY_PATH;
    }
}
