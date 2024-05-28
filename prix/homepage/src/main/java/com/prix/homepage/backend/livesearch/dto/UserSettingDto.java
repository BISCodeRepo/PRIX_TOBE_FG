package com.prix.homepage.backend.livesearch.dto;

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
    private String pTolerance = "10";
    @Builder.Default
    private String minChar = "2";
    @Builder.Default
    private String pUnit = "ppm";
    @Builder.Default
    private String fTolerance = "0.05";
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
}
