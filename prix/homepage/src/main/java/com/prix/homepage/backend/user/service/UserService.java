package com.prix.homepage.backend.user.service;

import com.prix.homepage.backend.user.domain.User;
import com.prix.homepage.backend.user.dto.RequestLoginDto;
import com.prix.homepage.backend.user.mapper.UserMapper;
import com.prix.homepage.backend.user.repository.TempUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TempUserRepository tempUserRepository;

    @Autowired
    private UserMapper userMapper;

    public User login(RequestLoginDto requestLoginDto) {
        if(requestLoginDto.getEmail().isEmpty()) {
            throw new RuntimeException ("id가 비어있습니다.");
        }
        if(requestLoginDto.getPassword().isEmpty()) {
            throw new RuntimeException ("비밀번호가 비어있습니다.");
        }
        if(requestLoginDto.getEmail().startsWith(" ")) {
            throw new RuntimeException ("잘못된 형식의 아이디입니다.");
        }

//        User user = userMapper.findByEmail(requestLoginDto.getEmail());

        User user = userMapper.findByEmailAndPassword(requestLoginDto.getEmail(), requestLoginDto.getPassword());

//        Optional<User> optionalUser = tempUserRepository.findByLoginId(requestLoginDto.getEmail());
//
        if(user == null) {
            throw new RuntimeException ("사용자를 찾을 수 없습니다.");
        }

//        User user = optionalUser.get();

        if(!user.getPassword().equals(requestLoginDto.getPassword())) {
            throw new RuntimeException ("비밀번호가 틀렸습니다.");
        }

        return user;
    }

    public User signUp(RequestLoginDto requestLoginDto) {
        if(requestLoginDto.getEmail().isEmpty()) {
            throw new RuntimeException ("id가 비어있습니다.");
        }
        if(requestLoginDto.getPassword().isEmpty()) {
            throw new RuntimeException ("비밀번호가 비어있습니다.");
        }
        if(requestLoginDto.getEmail().startsWith(" ")) {
            throw new RuntimeException ("잘못된 형식의 아이디입니다.");
        }

        Optional<User> optionalUser = tempUserRepository.findByLoginId(requestLoginDto.getEmail());

        if(optionalUser.isPresent()) {
            throw new RuntimeException ("이미 존재하는 id 입니다.");
        }

        User user = tempUserRepository.save(User.builder().build());

        return user;
    }

    public List<User> getUserTest() {
        return userMapper.findAll();
    }


}
