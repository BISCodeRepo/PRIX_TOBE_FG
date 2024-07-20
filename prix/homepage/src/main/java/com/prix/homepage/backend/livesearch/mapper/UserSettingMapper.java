package com.prix.homepage.backend.livesearch.mapper;

import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserSettingMapper {

    @Select("SELECT * FROM px_user_setting WHERE user_id = #{id} AND engine = 'modeye'")
    UserSetting findByUserId(Integer id);

    @Select("SELECT name FROM px_account WHERE id = #{id}")
    String findAccountNameById(Integer id);
}