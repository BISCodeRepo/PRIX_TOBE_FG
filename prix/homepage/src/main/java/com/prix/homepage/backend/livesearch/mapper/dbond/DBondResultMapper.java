package com.prix.homepage.backend.livesearch.mapper.dbond;

import com.prix.homepage.backend.livesearch.pojo.dbond.PxData;
import com.prix.homepage.constants.DBond.ProteinSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DBondResultMapper {

    @Select("SELECT level FROM px_account WHERE id = #{id}")
    Integer getUserLevel(@Param("id") String id);

    @Select("SELECT user_id AS userId FROM px_search_log WHERE result = #{fileName}")
    Integer getUserIdByResult(@Param("fileName") String fileName);

    @Select("SELECT name, content FROM px_data WHERE id = #{fileName}")
    PxData getProteinSummary(@Param("fileName") Integer fileName);

    @Select("SELECT name, content FROM px_data WHERE id = #{databasePath}")
    PxData getProteinDatabase(@Param("databasePath") Integer databasePath);
}
