package com.prix.homepage.backend.livesearch.service.dbond;

import com.prix.homepage.backend.livesearch.dto.dbond.DBondResultDto;
import com.prix.homepage.backend.livesearch.mapper.dbond.DBondResultMapper;
import com.prix.homepage.backend.livesearch.pojo.dbond.PxData;
import com.prix.homepage.constants.DBond.Modification;
import com.prix.homepage.constants.DBond.PeptideLine;
import com.prix.homepage.constants.DBond.ProteinInfo;
import com.prix.homepage.constants.DBond.ProteinSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class DBondResultService {

    private final DBondResultMapper dBondResultMapper;

    public DBondResultDto getResult(Integer id, HttpServletRequest request){
        HttpSession session = request.getSession();
        // 파일 이름 가져오기, 없으면 기본값 "45" 설정
        String fileName = request.getParameter("file");
        if (fileName == null) {
            fileName = "45";
        }

        ProteinSummary summary = new ProteinSummary();

        // 데이터베이스 접근
        String databasePath = "";

        Integer level = (Integer) session.getAttribute("level");
        if (level == null) {
            try {
                level = dBondResultMapper.getUserLevel(id.toString());
                session.setAttribute("level", level);
            } catch (Exception e) {
                log.error("Error fetching user level", e);
            }
        }

        if (level == null) {
            level = 0;
        }

        Integer userId = null;
        try {
            userId = dBondResultMapper.getUserIdByResult(fileName);
        } catch (Exception e) {
            log.error("Error fetching user ID", e);
        }

        if (!"390".equals(fileName) && !"396".equals(fileName) && level <= 1 && (userId == null || !id.equals(userId))) {
            // 권한이 없는 경우 처리
            return DBondResultDto.builder()
                    .notauthorized(true)
                    .build();
        }

        try {
            // 첫 번째 쿼리: fileName에 해당하는 데이터 조회
            PxData pxData = dBondResultMapper.getProteinSummary(Integer.parseInt(fileName));
//            log.info("pxData ={}",pxData);
            if (pxData != null) {
                // 첫 번째 쿼리의 결과가 있을 경우 처리
                InputStream is = new ByteArrayInputStream(pxData.getContent());
                InputStreamReader reader = new InputStreamReader(is);
                summary.read(reader);
            }

            // 두 번째 쿼리: summary에서 얻은 databasePath에 해당하는 데이터 조회
            databasePath = summary.getDatabasePath();
            PxData databasePxData = dBondResultMapper.getProteinDatabase(Integer.parseInt(databasePath));
//            log.info("pxData ={}",databasePxData);
            if (databasePxData != null) {
                // 두 번째 쿼리의 결과가 있을 경우 처리
                InputStream is = new ByteArrayInputStream(databasePxData.getContent());
                summary.readProtein(is);
                databasePath = databasePxData.getName();
            }
        } catch (Exception e) {
            log.error("Error fetching protein summary or database", e);
        }

        boolean isDBond = false;
        if (summary.getEngineName() != null && summary.getEngineName().compareToIgnoreCase("DBOND") == 0) {
            isDBond = true;
        }

        String minScore = request.getParameter("minscore");
        String targetDecoy = request.getParameter("targetdecoy");
        String minFDR = request.getParameter("minfdr");
        String maxHit = request.getParameter("maxhit");
        if (minScore == null) minScore = "0.1";
        if (minFDR == null) minFDR = "1";
        if (maxHit == null) maxHit = "All";
        if (targetDecoy == null && summary.isTargetDecoyed()) targetDecoy = "O";

        int maxProteins = 0;
        if (!"All".equals(maxHit)) {
            maxProteins = Integer.parseInt(maxHit);
        }
        double minPeptideScore = Double.parseDouble(minScore);
        boolean useTargetDecoy = targetDecoy != null;
        double minFDRate = Double.parseDouble(minFDR) / 100;

        Modification[] mods = summary.getModifications();
        ProteinInfo[] proteins = summary.getProteins();

        if (useTargetDecoy) {
            double[] scores = summary.getDecoyScores();
            int fdr = Integer.parseInt(minFDR) - 1;
            if (fdr >= scores.length)
                fdr = scores.length - 1;
            minPeptideScore = scores[fdr];
            minScore = Double.toString(minPeptideScore);
        }

        if (proteins != null) {
            for (int i = 0; i < proteins.length; i++) {
                if (proteins[i] != null) {
                    proteins[i].makePeptideLines(summary, mods, minPeptideScore, isDBond, i, true);
                }
            }
        }

        // Sorting the proteins
        String sort = request.getParameter("sort");
        if (sort == null) {
            sort = "pm";
        }
        Comparator<ProteinInfo> by = new PepMatchComparator();
        if ("pi".equals(sort)) {
            by = new IDComparator();
        } else if ("sc".equals(sort)) {
            by = new SeqCovComparator();
        } else if ("psm".equals(sort)) {
            by = new PSMMatchComparator();
        }
        if (proteins != null) {
            Arrays.sort(proteins, by);
        }

        double proteinMassMax = summary.getProteinMassMax();

        mods = summary.getModifications();

        int num = 0;
        List<ProteinInfo> proteinInfos = new ArrayList<>();
        if (proteins != null) {
            for (int i = 0; i < proteins.length; i++) {
                if (maxProteins > 0 && num >= maxProteins)
                    break;

                ProteinInfo info = proteins[i];
                if (info == null)
                    continue;
                num++;

                PeptideLine[] peptides = info.getPeptideLines();
                if (peptides == null || peptides.length == 0)
                    continue;

                proteinInfos.add(info);
            }
        }

        num = 0;
        boolean[][] coverageCodes = (proteins != null) ? new boolean[proteins.length][] : new boolean[maxProteins][];
        List<Double> coveragePercentages = new ArrayList<>();
        List<ProteinInfo> peptideHitsInfos = new ArrayList<>();

        if (proteins != null) {
            for (ProteinInfo info : proteins) {
                if (maxProteins > 0 && num >= maxProteins)
                    break;

                if (info == null)
                    continue;

                PeptideLine[] peptides = info.getPeptideLines();
                if (peptides == null || peptides.length == 0)
                    continue;
                num++;

                Arrays.sort(peptides, new PeptideComparator());

                boolean[] code = info.getCoverageCode();
                coverageCodes[num - 1] = code;

                int coverageCounts = 0;
                for (boolean c : code) {
                    if (c) {
                        coverageCounts++;
                    }
                }

                double coveragePercentage = code.length == 0 ? 0 : (double) coverageCounts * 100 / code.length;
                coveragePercentages.add(coveragePercentage);
                peptideHitsInfos.add(info);
            }
        }




        return DBondResultDto.builder()
                .summary(summary)
                .fileName(fileName)
                .level(level)
                .userId(userId)
                .notauthorized(false)
                .isDBond(isDBond)
                .minScore(minScore)
                .maxHit(maxHit)
                .minFDR(minFDR)
                .sort(sort)
                .max(proteinMassMax)
                .targetDecoy(targetDecoy)
                .maxProteins(maxProteins)
                .minPeptideScore(minPeptideScore)
                .useTargetDecoy(useTargetDecoy)
                .minFDRate(minFDRate)
                .mods(mods)
                .proteins(proteins)
                .infos(proteinInfos)
                .peptideHitsInfos(peptideHitsInfos)
                .coveragePercentages(coveragePercentages)
                .coverageCodes(coverageCodes)
                .build();
    }
}

// ProteinInfo를 PeptidesMatched 기준으로 비교
class PepMatchComparator implements Comparator<ProteinInfo> {
    public int compare(ProteinInfo l, ProteinInfo r) {
        int diff;
        if (l == null && r == null) return 0;
        else if (l == null)
            return 1;
        else if (r == null)
            return -1;
        else {
            diff = r.getNumberOfMatchedPeptides() - l.getNumberOfMatchedPeptides();
            if (diff == 0)
                diff = r.getPeptideLines().length - l.getPeptideLines().length;
        }
        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}

// ProteinInfo를 PSMsMatched 기준으로 비교
class PSMMatchComparator implements Comparator<ProteinInfo> {
    public int compare(ProteinInfo l, ProteinInfo r) {
        int diff;
        if (l == null && r == null) return 0;
        else if (l == null)
            return 1;
        else if (r == null)
            return -1;
        else {
            diff = r.getPeptideLines().length - l.getPeptideLines().length;
            if (diff == 0)
                diff = r.getNumberOfMatchedPeptides() - l.getNumberOfMatchedPeptides();
        }
        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}

// ProteinInfo를 SequenceCoverage 기준으로 비교
class SeqCovComparator implements Comparator<ProteinInfo> {
    public int compare(ProteinInfo l, ProteinInfo r) {
        int diff;
        if (l == null && r == null) return 0;
        else if (l == null)
            return 1;
        else if (r == null)
            return -1;
        else {
            boolean[] hits = l.getCoverageCode();
            int lc = 0, rc = 0;
            for (int i = 0; i < hits.length; i++)
                if (hits[i])
                    lc++;
            int ll = hits.length;
            hits = r.getCoverageCode();
            for (int i = 0; i < hits.length; i++)
                if (hits[i])
                    rc++;
            int rl = hits.length;
            diff = rc * ll - lc * rl;
            if (diff == 0)
                diff = r.getNumberOfMatchedPeptides() - l.getNumberOfMatchedPeptides();
        }
        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}

// ProteinInfo를 ID 기준으로 비교
class IDComparator implements Comparator<ProteinInfo> {
    public int compare(ProteinInfo l, ProteinInfo r) {
        int diff;
        if (l == null && r == null) return 0;
        else if (r == null)
            return 1;
        else if (l == null)
            return -1;
        else {
            diff = l.getName().compareTo(r.getName());
            if (diff == 0)
                diff = r.getNumberOfMatchedPeptides() - l.getNumberOfMatchedPeptides();
        }
        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}

// PeptideLine을 시작 위치(Start) 기준으로 비교
class PeptideComparator implements Comparator<PeptideLine> {
    public int compare(PeptideLine l, PeptideLine r) {
        int diff = l.getStart() - r.getStart();
        if (diff == 0) {
            diff = l.getEnd() - r.getEnd();
            if (diff == 0)
                diff = l.getIndex() - r.getIndex();
        }
        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}

// PeptideLine을 Score 기준으로 비교
class PeptideDecoyComparator implements Comparator<PeptideLine> {
    public int compare(PeptideLine l, PeptideLine r) {
        int diff = (int) ((r.getScore() - l.getScore()) * 10000);
        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}