package com.prix.homepage.backend.livesearch.mapper.ACTG;

import com.prix.homepage.backend.livesearch.pojo.SearchLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;

@Mapper
public interface SearchLogMapper {

    @Insert("INSERT INTO px_search_log (user_id, title, date, msfile, db, result, engine) " +
            "VALUES (#{userId}, #{title}, NOW(), #{msFile}, #{db}, #{result}, #{engine})")
    void insertSearchLog(
            @Param("userId") int userId,
            @Param("title") String title,
            @Param("msFile") int msFile,
            @Param("db") int db,
            @Param("result") String result,
            @Param("engine") String engine
    );

    @Select("SELECT title, user_id AS userId, date FROM px_search_log WHERE result = #{index}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "date", column = "date", javaType = LocalDate.class)  // date 필드를 명시적으로 매핑
    })
    SearchLog getSearchLogByActgIndex(@Param("index") String index);

}
