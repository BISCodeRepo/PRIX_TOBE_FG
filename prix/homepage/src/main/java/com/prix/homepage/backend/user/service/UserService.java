package com.prix.homepage.backend.user.service;

import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.dto.RequestLoginDto;
import com.prix.homepage.backend.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // UserMapper 의존성 주입을 위한 필드 선언
    private final UserMapper userMapper;

    // UserService 생성자
    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     사용자 로그인 처리 메소드

     @param requestLoginDto 로그인 요청 DTO
     @param level 기대하는 역할 레벨
     @return 로그인된 사용자 정보
     */
    public User login(RequestLoginDto requestLoginDto, int level) {
        if (!validateRequest(requestLoginDto)) {
            return null;
        }
        User user = userMapper.findByEmailAndPassword(requestLoginDto.getEmail(), requestLoginDto.getPassword());
        if (!validateUserRole(user, level)) {
            return null;
        }
        return user;
    }

    /**
     사용자 회원가입 처리 메소드

     @param requestLoginDto 회원가입 요청 DTO
     */
    public int signUp(RequestLoginDto requestLoginDto) {
        if (!validateRequest(requestLoginDto)) {
            return 1;
        }

        if (userMapper.findByEmail(requestLoginDto.getEmail()) != null) {
            return 2;
        }

        userMapper.insertUser(requestLoginDto.getEmail(), requestLoginDto.getPassword(), 1);
        return 0;
    }

    /**
     사용자 역할 검증 메소드

     @param user 검증할 사용자
     @param level 기대하는 역할 레벨
     @return 역할이 일치하는 경우 true, 그렇지 않은 경우 false
     */
    public boolean validateUserRole(User user, int level) {
        if (user == null) {
            return false;
        }
        return user.getLevel() == level;
    }

    /**
     입력값 검증 메소드

     @param requestLoginDto 요청 DTO
     @return 유효한 경우 true, 그렇지 않은 경우 false
     */
    public boolean validateRequest(RequestLoginDto requestLoginDto) {
        String email = requestLoginDto.getEmail();
        String password = requestLoginDto.getPassword();

        // 이메일이 null이거나 비어있는지 검증
        if (email == null || email.isEmpty()) {
            return false;
        }

        // 비밀번호가 null이거나 8자 이상인지 검증
        if (password == null || password.length() < 8) {
            return false;
        }

        // 비밀번호에 문자, 숫자, 특수문자가 포함되어 있는지 검증
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }

            if (hasLetter && hasDigit && hasSpecialChar) {
                return true;
            }
        }

        return false;
    }
}
