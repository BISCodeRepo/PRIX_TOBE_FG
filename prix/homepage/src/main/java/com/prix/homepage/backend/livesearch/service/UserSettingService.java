package com.prix.homepage.backend.livesearch.service;

import com.prix.homepage.backend.livesearch.dto.UserSettingDto;
import com.prix.homepage.backend.livesearch.mapper.UserSettingMapper;
import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.prix.homepage.constants.prixConst.anony;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserSettingService {

    private final UserSettingMapper userSettingMapper;

    public UserSettingDto findUserSettingById(Integer id) {
        return Optional.ofNullable(userSettingMapper.findByUserId(id))
                .map(UserSettingDto::new)
                .orElse(new UserSettingDto());
    }

    public String findAccountNameById(Integer id){
        return userSettingMapper.findAccountNameById(id);
    }
}
