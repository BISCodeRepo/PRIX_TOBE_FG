package com.prix.homepage.backend.livesearch.pojo;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSetting {
    private Integer userId;
    private Integer enzyme;
    private Byte mmc;
    private Byte met;
    private Float ptol;
    private PtolUnit ptolUnit;
    private Float ftol;
    private Float mmMin;
    private Float mmMax;
    private String engine;
    private String dataFormat;
    private String instrument;
    private String msResolution;
    private String msmsResolution;
}
