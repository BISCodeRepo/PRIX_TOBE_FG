package com.prix.homepage.backend.livesearch.mapper.dbond;

import com.prix.homepage.backend.livesearch.pojo.dbond.Database;
import com.prix.homepage.backend.livesearch.pojo.dbond.DatabaseInfo;
import com.prix.homepage.backend.livesearch.pojo.dbond.Enzyme;
import com.prix.homepage.backend.livesearch.pojo.dbond.PxData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DBondMapper {
    
    @Select("SELECT id, name FROM px_enzyme WHERE user_id = #{userId}")
    List<Enzyme> findByUserId(Integer userId);

    @Select("SELECT id, name FROM px_database")
    List<Database> findAll();

    @Select("SELECT name, content FROM px_data WHERE id = #{id}")
    PxData findDataById(int id);

    @Select("SELECT content FROM px_data WHERE id = #{id}")
    byte[] getFileDataById(@Param("id") int id);

    @Select("SELECT data_id AS dataId, file AS fileName FROM px_database WHERE id = #{databaseId}")
    DatabaseInfo getDatabaseInfoById(@Param("databaseId") int databaseId);

    @Select("SELECT id FROM px_database WHERE data_id = #{dataId}")
    Integer findDatabaseIdByDataId(@Param("dataId") int dataId);
}
