package com.prix.homepage.backend.livesearch.mapper;


import com.prix.homepage.backend.livesearch.pojo.UpdateTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UpdateTableMapper {

    @Select("SELECT * FROM pm_update_table WHERE dbname = #{dbname}")
    UpdateTable selectByDbName(@Param("dbname") String dbname);


}
