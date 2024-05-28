package com.prix.homepage.backend.livesearch.mapper;

import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserModificationMapper {

    @Select("SELECT count(1) " +
            "FROM px_user_modification " +
            "WHERE user_id = #{userId} " +
            "AND variable = #{variable} " +
            "AND engine = #{engine}")
    Integer getUserModificationCount(Integer userId, Boolean variable, Boolean engine);

}