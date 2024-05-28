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

    public UserSettingDto findUserSettingById(Integer id){
        UserSettingDto anonyuserSettingDto = new UserSettingDto();

        Optional<UserSetting> optionalUserSetting
                = Optional.ofNullable(userSettingMapper.findByUserId(id));

        if(!optionalUserSetting.isPresent()) return anonyuserSettingDto;

        UserSetting userSetting = optionalUserSetting.get();

        return UserSettingDto.builder()
                .enzyme(String.valueOf(userSetting.getEnzyme()))
                .missedCleavage(String.valueOf(userSetting.getMmc()))
                .minNumEnzTerm(String.valueOf(userSetting.getMet()))
                .pTolerance(String.valueOf(userSetting.getPtol()))
                .pUnit(String.valueOf(userSetting.getPtolUnit()))
                .fTolerance(String.valueOf(userSetting.getFtol()))
                .minMM(String.valueOf(userSetting.getMmMin()))
                .maxMM(String.valueOf(userSetting.getMmMax()))
                .dataFormat(userSetting.getDataFormat())
                .instrument(userSetting.getInstrument())
                .msResolution(userSetting.getMsResolution())
                .msmsResolution(userSetting.getMsmsResolution())
                .build();
    }
}
