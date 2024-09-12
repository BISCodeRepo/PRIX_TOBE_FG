package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.entity.*;
import com.prix.homepage.backend.account.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    // database 관련 메소드들
    @Insert("INSERT INTO px_database (name, file, data_id) VALUES (#{name}, #{file}, -1)")
    void insertFile(@Param("name") String name, @Param("file") String file);

    @Select("SELECT * FROM px_database")
    Database[] selectAllFile();

    @Update("UPDATE px_database SET name = #{name} WHERE id = #{id}")
    void updateDatabase(@Param("id") int id, @Param("name") String name);

    @Delete("DELETE FROM px_database WHERE id = #{id}")
    void deleteDatabase(@Param("id") int id);

    // enzyme 관련 메소드들
    @Insert("INSERT INTO px_enzyme (user_id, name, nt_cleave, ct_cleave) VALUES (#{user_id}, #{name}, #{nt_cleave}, #{ct_cleave})")
    void insertEnzyme(@Param("user_id") int user_id, @Param("name") String name, @Param("nt_cleave") String nt_cleave, @Param("ct_cleave") String ct_cleave);

    @Select("SELECT * FROM px_enzyme")
    Enzyme[] selectAllEnzyme();

    @Update("UPDATE px_enzyme SET name = #{name}, nt_cleave = #{nt_cleave}, ct_cleave = #{ct_cleave} WHERE id = #{id}")
    void updateEnzyme(@Param("id") int id, @Param("name") String name, @Param("nt_cleave") String nt_cleave, @Param("ct_cleave") String ct_cleave);

    @Delete("DELETE FROM px_enzyme WHERE id = #{id}")
    void deleteEnzyme(@Param("id") int id);

    // 소프트웨어 로그 관련 메소드들
    @Select("SELECT name, date, version, file FROM px_software_log ORDER BY date, id")
    SoftwareLog[] selectAllSoftwareLogs();

    // 수정 로그 관련 메소드들
    @Select("SELECT date, version, file FROM px_modification_log")
    ModificationLog[] selectAllModificationLogs();

    // SearchLog 관련 메서드 추가
    @Select("SELECT l.title, l.date, " +
            "COALESCE(msData.name, '') as msFile, " +
            "COALESCE(dbData.name, '') as dbFile, " +
            "l.result, l.engine, " +
            "COALESCE(a.name, '') as userName, l.id " +
            "FROM px_search_log l " +
            "LEFT JOIN px_account a ON l.user_id = a.id " +
            "LEFT JOIN px_data msData ON l.msfile = msData.id " +
            "LEFT JOIN px_data dbData ON l.db = dbData.id " +
            "ORDER BY l.date DESC, l.id DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<SearchLog> findSearchLogs(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM px_search_log")
    int countSearchLogs();

//    users
    @Select("SELECT id, name, affiliation, email, level FROM px_account")
    List<User> findAllUsers();

    @Update("UPDATE px_account SET level = #{level} WHERE id = #{id}")
    void updateUserLevel(@Param("id") int id, @Param("level") int level);

    @Delete("DELETE FROM px_account WHERE id = #{id}")
    void deleteUserById(@Param("id") int id);

//    request
    @Select("SELECT * FROM px_software_request ORDER BY date DESC, id DESC")
    List<SoftwareRequest> findAllRequests();

    @Update("UPDATE px_software_request SET state = #{state}, senttime = NOW() WHERE id = #{requestId}")
    void updateRequestState(@Param("requestId") int requestId, @Param("state") int state);

    @Delete("DELETE FROM px_software_request WHERE id = #{requestId}")
    void deleteRequestById(@Param("requestId") int requestId);

//    mail
    @Select("SELECT message FROM px_software_msg WHERE id = #{softwareId}")
    String getMessageBySoftware(@Param("softwareId") String softwareId);

    @Update("UPDATE px_software_request SET state = #{state}, version = #{version}, senttime = NOW() WHERE id = #{requestId}")
    void updateRequestStateWithVersion(@Param("requestId") Integer requestId, @Param("state") int state, @Param("version") String version);

    @Update("UPDATE px_software_msg SET message = #{message} WHERE id = #{id}")
    void updateSoftwareMessage(@Param("id") String id, @Param("message") String message);

    @Insert("INSERT INTO px_database (name, file, data_id) VALUES (#{dbName}, #{dbPath}, #{dataId})")
    void insertDatabaseFile(@Param("dbName") String dbName, @Param("dbPath") String dbPath, @Param("dataId") int dataId);

}
