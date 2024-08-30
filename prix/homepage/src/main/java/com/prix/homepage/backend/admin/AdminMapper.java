package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.entity.*;
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

    // 특정 메시지 가져오기
    @Select("SELECT message FROM px_software_msg WHERE id = 'mode'")
    String selectModaMessage();

    @Select("SELECT message FROM px_software_msg WHERE id = 'dbond'")
    String selectDbondMessage();

    @Select("SELECT message FROM px_software_msg WHERE id = 'nextsearch'")
    String selectNextSearchMessage();

    @Select("SELECT message FROM px_software_msg WHERE id = 'signature'")
    String selectSignatureMessage();

    // SearchLog 관련 메서드 추가
    @Select("SELECT l.title, l.date, l.msfile, l.db, l.result, l.engine, a.name as userName, l.id " +
            "FROM px_search_log l, px_account a WHERE l.user_id = a.id " +
            "ORDER BY l.date DESC, l.id DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<SearchLog> findSearchLogs(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM px_search_log")
    int countSearchLogs();
}
