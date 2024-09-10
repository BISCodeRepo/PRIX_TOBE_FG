package com.prix.homepage.backend.livesearch.service;

import com.prix.homepage.backend.basic.utils.PathUtil;
import com.prix.homepage.backend.livesearch.dto.ActgDto;
import com.prix.homepage.backend.livesearch.mapper.ACTG.SearchLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class ActgProcService {

    private final PathUtil pathUtil;
    private final SearchLogMapper searchLogMapper;

    public ActgDto process(String id, HttpServletRequest request, Map<String, String> params, MultipartFile[] files) throws IOException {
        ActgDto result = new ActgDto();

        // 파라미터 및 변수 초기화 (기존 JSP 흐름 유지)
        String user = "";
        String title = params.get("title");
        System.out.println("title: " + title);

        // Environment 관련 변수 설정
        String method = "";
        String peptideFile = "";
        String IL = "";

        // Protein DB 관련 변수
        String proteinDB = "";
        String SAV = "";

        // Variant Splice Graph 관련 변수
        String variantSpliceGraphDB = "";
        String mutation = "";
        String mutationFile = "";
        String exonSkipping = "";
        String altAD = "";
        String intron = "";

        // Six-frame translation 관련 변수
        String referenceGenome = "";

        String line = "";
        String rate = "0%";
        boolean finished = false;
        boolean failed = false;
        String output = "";


        final String logDir = pathUtil.getGlobalDirectoryPath("/home/PRIX/ACTG_log/");
        final String dbDir = pathUtil.getGlobalDirectoryPath("/home/PRIX/ACTG_db/");
        log.info("logDir = {}",logDir);
        log.info("dbDir = {}",dbDir);

        // processName 파라미터 가져오기
        String processName = params.get("process");
        String processPath = logDir + processName;
        if (params.get("execute") == null) {
            Date date = new Date();
            String key = id + "_" + date.getTime();
            processPath = "process_" + key + ".proc";
            processName = processPath;
            String xmlPath = "param_" + key + ".xml";
            String proteinDBPath = "";
            String peptideFilePath = "";
            String variantSpliceGraphDBPath = "";
            String mutationFilePath = "";
            String referenceGenomePath = "";
            String outputPath = logDir;


            // 파라미터 처리
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();

                // 파라미터에 따른 처리
                switch (name) {
                    case "user":
                        user = value;
                        break;
                    case "title":
                        title = value;
                        break;
                    case "method":
                        method = value;
                        break;
                    case "IL":
                        IL = value;
                        break;
                    case "proteinDB":
                        proteinDB = value;
                        proteinDBPath = dbDir + proteinDB;
                        break;
                    case "SAV":
                        SAV = value;
                        break;
                    case "variantSpliceGraphDB":
                        variantSpliceGraphDB = value;
                        variantSpliceGraphDBPath = dbDir + variantSpliceGraphDB;
                        break;
                    case "mutation":
                        mutation = value;
                        break;
                    case "exonSkipping":
                        exonSkipping = value;
                        break;
                    case "altAD":
                        altAD = value;
                        break;
                    case "intron":
                        intron = value;
                        break;
                    case "referenceGenome":
                        referenceGenome = value;
                        referenceGenomePath = dbDir + referenceGenome;
                        break;
                    default:
                        break;
                }
            }

            // 파일 처리 (파일을 처리하는 부분을 기존 로직과 동일하게 유지)
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                String fieldName = file.getName();  // 필드명 가져오기
                if (fieldName.equals("peptideFile")) {
                    log.info("peptide");
                    peptideFile = "peptide_" + key + ".txt";
                    peptideFilePath = logDir + peptideFile;

                    if (file.getSize() > 1024 * 100) {
                        failed = true;
                        output = "The size of peptide list should not exceed 1KB.";
                        return result;
                    }

                    // 파일 저장
// 파일 저장
                    if (!file.isEmpty()) {
                        try {
                            // 파일 경로에 해당하는 디렉터리가 없으면 생성
                            File noPeptideFile = new File(peptideFilePath);
                            File parentDir = noPeptideFile.getParentFile();
                            if (!parentDir.exists()) {
                                parentDir.mkdirs();  // 상위 디렉토리 경로가 없으면 디렉토리 생성
                            }

                            // 파일이 없으면 파일 생성
                            if (!noPeptideFile.exists()) {
                                noPeptideFile.createNewFile();
                            }

                            // 파일에 데이터 쓰기
                            try (FileOutputStream fos = new FileOutputStream(peptideFilePath);
                                 OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
                                 InputStream is = file.getInputStream()) {

                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = is.read(buffer)) != -1) {
                                    writer.write(new String(buffer, 0, bytesRead, "UTF-8"));
                                }
                            }
                            log.info("peptideFile in actg process service done");

                        } catch (Exception e) {
                            log.warn("error in writing peptideFile: {}", e.getMessage());
                        }
                    }

                } else if (fieldName.equals("mutationFile")) {
                    log.info("mutation");
                    mutationFile = "mutation_" + key + ".txt";
                    mutationFilePath = "/home/PRIX/ACTG_log/" + mutationFile;

                    if (file.getSize() > 20971520) {
                        result.setFailed(true);
                        failed = true;
                        output = "The size of VCF should not exceed 20MB.";
                        return result;
                    }

                    // 파일 저장
                    try (FileOutputStream fos = new FileOutputStream(mutationFilePath);
                         InputStream is = file.getInputStream()) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }

            try {
                // template.xml이라는 파일 필요 2024
                FileReader FR = new FileReader(dbDir + "template.xml");
                BufferedReader BR = new BufferedReader(FR);

                File xmlFile = new File(logDir + xmlPath);
                File parentDir = xmlFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();  // 상위 디렉토리 생성
                }

                FileWriter FW = new FileWriter(logDir + xmlPath);
                BufferedWriter BW = new BufferedWriter(FW);

                String line_ = null;

                while ((line_ = BR.readLine()) != null) {
                    if (line_.contains("[METHOD]")) {
                        line_ = line_.replace("[METHOD]", method);
                    } else if (line_.contains("[IL]")) {
                        line_ = line_.replace("[IL]", IL);
                    } else if (line_.contains("[PEPTIDE_FILE]")) {
                        line_ = line_.replace("[PEPTIDE_FILE]", peptideFilePath);
                    } else if (line_.contains("[PROTEIN_DB]")) {
                        line_ = line_.replace("[PROTEIN_DB]", proteinDBPath);
                    } else if (line_.contains("[SAV]")) {
                        line_ = line_.replace("[SAV]", SAV);
                    } else if (line_.contains("[VARIANT_SPLICE_GRAPH_DB]")) {
                        line_ = line_.replace("[VARIANT_SPLICE_GRAPH_DB]", variantSpliceGraphDBPath);
                    } else if (line_.contains("[ALT_AD]")) {
                        line_ = line_.replace("[ALT_AD]", altAD);
                    } else if (line_.contains("[EXON_SKIPPING]")) {
                        line_ = line_.replace("[EXON_SKIPPING]", exonSkipping);
                    } else if (line_.contains("[INTRON]")) {
                        line_ = line_.replace("[INTRON]", intron);
                    } else if (line_.contains("[MUTATION]")) {
                        line_ = line_.replace("[MUTATION]", mutation);
                    } else if (line_.contains("[MUTATION_FILE]")) {
                        line_ = line_.replace("[MUTATION_FILE]", mutationFilePath);
                    } else if (line_.contains("[REFERENCE_GENOME]")) {
                        line_ = line_.replace("[REFERENCE_GENOME]", referenceGenomePath);
                    } else if (line_.contains("[OUTPUT]")) {
                        line_ = line_.replace("[OUTPUT]", outputPath);
                    }

                    BW.append(line_);
                    BW.newLine();
                }

                BR.close();
                FR.close();
                BW.close();
                FW.close();
            } catch (IOException e) {
                output = e + "\n";
                log.error("error = {}",e.getMessage());
            }


            if (!failed) {
                Runtime runtime = Runtime.getRuntime();
                String logXmlPath = logDir + xmlPath;
                String logProcessPath = logDir + processPath;
                Path logXmlFilePath = Paths.get(logXmlPath);
                if (Files.exists(logXmlFilePath)) {
                    System.out.println("logDir + xmlPath 파일이 존재합니다: " + logXmlPath);
                } else {
                    System.out.println("logDir + xmlPath 파일이 존재하지 않습니다: " + logXmlPath);
                }

                // logDir + processPath 파일 존재 여부 확인
                Path logProcessFilePath = Paths.get(logProcessPath);
                if (Files.exists(logProcessFilePath)) {
                    System.out.println("logDir + processPath 파일이 존재합니다: " + logProcessPath);
                } else {
                    System.out.println("logDir + processPath 파일이 존재하지 않습니다: " + logProcessPath);
                }
                String jarPath = "/Users/wook/Downloads/prix_test/ACTG_Search.jar";
                String[] command = {"/bin/bash", "-c",
                        "java -Xss2M -Xmx10G -jar " + jarPath + " " + logDir + xmlPath + " " + logDir +processPath};
                Process process = runtime.exec(command);
            }

        } else {
            log.info("name = {}",processName);
            log.info("path = {}",processPath);

            try (FileInputStream fis = new FileInputStream(processPath);
                 StringWriter writer = new StringWriter();
                 StringWriter allWriter = new StringWriter()) {

                int c;
                while ((c = fis.read()) != -1) {
                    char character = (char) c;
                    if (character == '\n') {
                        line = writer.toString();

                        // 에러 처리
                        if (line.contains("ERROR") || line.contains("Exception")) {
                            failed = true;
                        } else if (line.startsWith("Elapsed Time")) {
                            finished = true;
                        }

                        // logDir 및 dbDir 치환
                        if (line.contains(logDir)) {
                            line = line.replace(logDir, "");
                        }
                        if (line.contains(dbDir)) {
                            line = line.replace(dbDir, "");
                        }

                        // 진행률 정보 추출
                        if (line.contains("%")) {
                            rate = line;
                        } else {
                            allWriter.append(line).append("\n");
                        }

                        // 새로운 라인 처리
                        writer.getBuffer().setLength(0);
                    } else {
                        writer.append(character);
                    }
                }

                // 마지막 라인 처리
                if (writer.getBuffer().length() > 0) {
                    line = writer.toString();
                }

                output = allWriter.toString();

                if (finished) {// 모든 작업이 정상 종료
                    String prixIndex = processPath.replace("process_" + id + "_", "");
                    prixIndex = prixIndex.replace(logDir, "");
                    prixIndex = prixIndex.replace(".proc", "");
                    searchLogMapper.insertSearchLog(
                            Integer.parseInt(id), title.replace("'", "\\'"), 0, 0, prixIndex, "ACTG");

                    ActgDto processDto =
                            ActgDto.builder()
                            .failed(failed)
                            .finished(finished)
                            .output(output)
                            .processName(processName)
                            .prixIndex(prixIndex)
                            .rate((rate))
                            .title(title)
                            .build();

                    return processDto;
                }

            }



            // XML 파일 생성 및 프로세스 실행 로직은 기존과 동일하게 유지

            // 나머지 기존 로직 유지 (예: XML 생성 및 실행 등)

        }

        ActgDto processDto = ActgDto.builder().failed(failed).finished(finished).output(output)
                .processName(processName).prixIndex("").rate((rate)).title(title).build();

        return processDto;
    }

}
