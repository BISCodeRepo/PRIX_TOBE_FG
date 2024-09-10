package com.prix.homepage.backend.livesearch.dto;

import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettingDto {
    @Builder.Default
    private String version = "1.01";
    @Builder.Default
    private String enzyme = "";
    @Builder.Default
    private String missedCleavage = "1";
    @Builder.Default
    private String minNumEnzTerm = "2";
    @Builder.Default
    private String pTolerance = "0.5";
    @Builder.Default
    private String minChar = "2";
    @Builder.Default
    private String pUnit = "Da";
    @Builder.Default
    private String fTolerance = "0.5";
    @Builder.Default
    private String minIE = "0";
    @Builder.Default
    private String maxIE = "1";
    @Builder.Default
    private String minMM = "-150";
    @Builder.Default
    private String maxMM = "+250";
    @Builder.Default
    private String dataFormat = "";
    @Builder.Default
    private String instrument = "";
    @Builder.Default
    private String msResolution = "";
    @Builder.Default
    private String msmsResolution = "";

    public UserSettingDto(UserSetting userSetting) {
        this.enzyme = String.valueOf(userSetting.getEnzyme());
        this.missedCleavage = String.valueOf(userSetting.getMmc());
        this.minNumEnzTerm = String.valueOf(userSetting.getMet());
        this.pTolerance = String.valueOf(userSetting.getPtol());
        this.pUnit = String.valueOf(userSetting.getPtolUnit());
        this.fTolerance = String.valueOf(userSetting.getFtol());
        this.minMM = String.valueOf(userSetting.getMmMin());
        this.maxMM = String.valueOf(userSetting.getMmMax());
        this.dataFormat = userSetting.getDataFormat();
        this.instrument = userSetting.getInstrument();
        this.msResolution = userSetting.getMsResolution();
        this.msmsResolution = userSetting.getMsmsResolution();
    }
}
