package com.prix.homepage.backend.livesearch.dto.dbond;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProcessRequestDto {
    private Integer id;
    private Boolean finished;
    private Boolean failed;
    private String title;
    private String dataFormat;
    private String instrument;
    private String msResolution;
    private String msmsResolution;
    private String database;
    private String decoy;
    private String fastaDecoy;
    private String enzyme;
    private String missedCleavage;
    private String minNumEnzTerm;
    private String pTolerance;
    private String pUnit;
    private String fTolerance;
    private String minMM;
    private String maxMM;
    private String modMap;
    private String multiStage;
    private String cysteinAlkylation;
    private String alkylationName;
    private String alkylationMass;
    private String engine;
    private Integer msIndex;
    private Integer dbIndex;
    private String logPath;
    private String xmlPath;
    private String msPath;
    private String dbPath;
    private String decoyPath;
    private String multiPath;
    private String execute;
    private String output;
    private Integer rate;
    private String address;
}
