package com.prix.homepage.backend.livesearch.service;

import com.prix.homepage.backend.livesearch.dto.UserSettingDto;
import com.prix.homepage.backend.livesearch.mapper.UserModificationMapper;
import com.prix.homepage.backend.livesearch.mapper.UserSettingMapper;
import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserModificationService {

    private final UserModificationMapper userModificationMapper;

    public Integer getUserModificationCount(Integer userId, Boolean variable, Boolean engine){
        return userModificationMapper.getUserModificationCount(userId, variable, engine);
    }

    public void deleteModificationsForAnonymousUser(int userId) {
        userModificationMapper.deleteByUserId(userId);
    }

    public void insertModifications(int id, List<Integer> modValues, boolean variable, boolean engine) {
        userModificationMapper.insertModifications(id, modValues, variable, engine);
    }

    public void deleteModifications(int userId, int engine, List<Integer> modIds) {
        userModificationMapper.deleteModifications(userId, engine, modIds);
    }

}
