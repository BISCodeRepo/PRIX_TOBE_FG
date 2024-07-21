package com.prix.homepage.backend.livesearch.mapper.dbond;

import com.prix.homepage.backend.livesearch.pojo.dbond.Database;
import com.prix.homepage.backend.livesearch.pojo.dbond.Enzyme;
import com.prix.homepage.backend.livesearch.pojo.dbond.PxData;
import org.apache.ibatis.annotations.Mapper;
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
}
