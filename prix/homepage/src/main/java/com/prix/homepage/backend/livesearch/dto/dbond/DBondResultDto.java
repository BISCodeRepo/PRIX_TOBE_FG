package com.prix.homepage.backend.livesearch.dto.dbond;

import lombok.*;
import com.prix.homepage.constants.DBond.ProteinSummary;
import com.prix.homepage.constants.DBond.Modification;
import com.prix.homepage.constants.DBond.ProteinInfo;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DBondResultDto {
    private ProteinSummary summary;           // ProteinSummary 객체
    private String fileName;                  // 파일 이름
    private Integer level;                    // 사용자 레벨
    private Integer userId;                   // 사용자 ID
    private boolean notauthorized;            // 권한이 없는지 여부
    private boolean isDBond;                  // DBond 사용 여부
    private String minScore;                  // 최소 점수
    private String maxHit;                    // 최대 히트 수
    private String minFDR;                    // 최소 FDR
    private String sort;                      // 정렬 기준
    private double max;                       // 최대 값 (Protein Mass Max)
    private String targetDecoy;               // Target-Decoy 사용 여부
    private int maxProteins;                  // 최대 Protein 수
    private double minPeptideScore;           // 최소 Peptide 점수
    private boolean useTargetDecoy;           // Target-Decoy 사용 여부
    private double minFDRate;                 // 최소 FDR 비율
    private Modification[] mods;              // Modification 배열
    private ProteinInfo[] proteins;           // ProteinInfo 배열
    private List<ProteinInfo> infos;
    private List<ProteinInfo> peptideHitsInfos;
    private boolean[][] coverageCodes;
    private List<Double> coveragePercentages;
}
