package com.prix.homepage.backend.user.mapper;

import com.prix.homepage.backend.user.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     이메일과 비밀번호로 사용자를 찾습니다.
     @param email 사용자의 이메일
     @param password 사용자의 비밀번호
     @return 찾은 사용자 객체
     */
    @Select("SELECT * FROM px_account WHERE email = #{email} AND password = SHA2(#{password}, 256)")
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    /**
     이메일로 사용자를 찾습니다.
     @param email 사용자의 이메일
     @return 찾은 사용자 객체
     */
    @Select("SELECT * FROM px_account WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     새로운 사용자를 삽입합니다.
     @param email 사용자의 이메일
     @param password 사용자의 비밀번호
     @param level 사용자의 레벨 (회원가입 시 1로 고정)
     */
    @Insert("INSERT INTO px_account (email, password, level) VALUES (#{email}, SHA2(#{password}, 256), #{level})")
    void insertUser(@Param("email") String email, @Param("password") String password, @Param("level") int level);
}
