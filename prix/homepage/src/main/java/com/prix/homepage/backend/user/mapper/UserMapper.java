package com.prix.homepage.backend.user.mapper;

import com.prix.homepage.backend.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
//    @Select("SELECT * FROM users")
    List<User> findAll();
}
