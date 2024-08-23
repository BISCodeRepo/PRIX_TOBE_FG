package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.dto.Database;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

@Mapper
public interface AdminMapper {

    @Insert("INSERT INTO px_database (name, file, data_id) VALUES (#{name}, #{file}, -1)")
    void insertFile(@Param("name") String name, @Param("file") String file);

    @Select("SELECT * FROM px_database")
    Database[] selectAllFile();

    @Update("UPDATE px_database SET name = #{name} where id = #{id} ")
    void updateDatabase(@Param("id") int id , @Param("name") String name);

    @Delete("DELETE from px_database WHERE id = #{id}")
    void deleteDatabase(@Param("id") int id);
}
