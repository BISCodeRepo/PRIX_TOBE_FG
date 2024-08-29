package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.entity.Database;
import com.prix.homepage.backend.admin.entity.Enzyme;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {

//    database
    @Insert("INSERT INTO px_database (name, file, data_id) VALUES (#{name}, #{file}, -1)")
    void insertFile(@Param("name") String name, @Param("file") String file);

    @Select("SELECT * FROM px_database")
    Database[] selectAllFile();

    @Update("UPDATE px_database SET name = #{name} where id = #{id} ")
    void updateDatabase(@Param("id") int id , @Param("name") String name);

    @Delete("DELETE from px_database WHERE id = #{id}")
    void deleteDatabase(@Param("id") int id);

//    enzyme
    @Insert("INSERT INTO px_enzyme (user_id, name, nt_cleave, ct_cleave) VALUES (#{user_id}, #{name}, #{nt_cleave}, #{ct_cleave})")
    void insertEnzyme(@Param("user_id") int user_id, @Param("name") String name, @Param("nt_cleave") String nt_cleave, @Param("ct_cleave") String ct_cleave);

    @Select("SELECT * FROM px_enzyme")
    Enzyme[] selectAllEnzyme();

    @Update("UPDATE px_enzyme SET name = #{name}, nt_cleave = #{nt_cleave}, ct_cleave = #{ct_cleave} WHERE id = #{id}")
    void updateEnzyme(@Param("id") int id, @Param("name") String name, @Param("nt_cleave") String nt_cleave, @Param("ct_cleave") String ct_cleave);

    @Delete("DELETE FROM px_enzyme WHERE id = #{id}")
    void deleteEnzyme(@Param("id") int id);
}
