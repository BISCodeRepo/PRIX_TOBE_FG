package com.prix.homepage.backend.livesearch.mapper;

import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserModificationMapper {

    @Select("SELECT count(1) " +
            "FROM px_user_modification " +
            "WHERE user_id = #{userId} " +
            "AND variable = #{variable} " +
            "AND engine = #{engine}")
    Integer getUserModificationCount(Integer userId, Boolean variable, Boolean engine);

    @Insert({
            "<script>",
            "INSERT INTO px_user_modification (user_id, mod_id, variable, engine) VALUES ",
            "<foreach collection='modValues' item='modValue' separator=','>",
            "(#{id}, #{modValue}, #{variable}, #{engine})",
            "</foreach>",
            "</script>"
    })
    void insertModifications(
            @Param("id") int id,
            @Param("modValues") List<Integer> modValues,
            @Param("variable") boolean variable,
            @Param("engine") boolean engine
    );

    @Delete("DELETE FROM px_user_modification WHERE user_id = #{userId}")
    void deleteByUserId(int userId);

    @Delete({
            "<script>",
            "DELETE FROM px_user_modification",
            "WHERE user_id = #{userId}",
            "AND engine = #{engine}",
            "AND mod_id IN",
            "<foreach item='modId' collection='modIds' open='(' separator=',' close=')'>",
            "#{modId}",
            "</foreach>",
            "</script>"
    })
    void deleteModifications(@Param("userId") int userId, @Param("engine") int engine, @Param("modIds") List<Integer> modIds);

}