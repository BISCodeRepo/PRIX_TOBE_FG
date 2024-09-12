package com.prix.homepage.backend.livesearch.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SearchLogMapper {

    @Insert("INSERT INTO px_search_log (user_id, title, date, msfile, db, result, engine) " +
            "VALUES (#{userId}, #{title}, NOW(), #{msIndex}, #{dbIndex}, #{prixIndex}, #{engine})")
    void insertSearchLog(@Param("userId") Integer userId,
                         @Param("title") String title,
                         @Param("msIndex") int msIndex,
                         @Param("dbIndex") int dbIndex,
                         @Param("prixIndex") int prixIndex,
                         @Param("engine") String engine);
}
