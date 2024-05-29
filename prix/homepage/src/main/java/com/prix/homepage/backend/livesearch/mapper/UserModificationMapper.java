package com.prix.homepage.backend.livesearch.mapper;

import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
            "INSERT INTO px_user_modification (user_id, mod_id, var, engine) VALUES ",
            "<foreach collection='modIds' item='modId' separator=','>",
            "(#{userId}, #{modId}, #{var}, #{engine})",
            "</foreach>",
            "</script>"
    })
    void insertUserModifications(String userId, @Param("modIds") List<String> modIds,Boolean var,Boolean engine);

}