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


    /**
     * ACTG 검색 프로세스를 처리하는 메서드.
     * 주어진 파라미터와 파일들을 기반으로 프로세스를 실행하고 그 결과를 반환한다.
     *
     * @param id       사용자 ID
     * @param request  HTTP 요청 객체
     * @param params   요청 파라미터
     * @param files    업로드된 파일 (페프티드 및 돌연변이 파일)
     * @return ActgDto 검색 결과를 담고 있는 DTO
     * @throws IOException 파일 처리 중 오류 발생 시 예외 발생
     */
    public ActgDto process(String id, HttpServletRequest request, Map<String, String> params, MultipartFile[] files) throws IOException {
        ActgDto result = new ActgDto();

        String user = "";
        String title = params.get("title");

        // Environment
        String method = "";
        String peptideFile = "";
        String IL = "";

        // Protein DB
        String proteinDB = "";
        String SAV = "";

        // Variant Splice Graph DB
        String variantSpliceGraphDB = "";
        String mutation = "";
        String mutationFile = "";
        String exonSkipping = "";
        String altAD = "";
        String intron = "";

        // Six-frame translation
        String referenceGenome = "";

        String line = "";
        String rate = "0%";
        boolean finished = false;
        boolean failed = false;
        String output = "";

        // 로그 및 데이터베이스 디렉토리 경로 설정
        final String logDir = pathUtil.getGlobalDirectoryPath("/home/PRIX/ACTG_log/");
        final String dbDir = pathUtil.getGlobalDirectoryPath("/home/PRIX/ACTG_db/");
        log.info("logDir = {}",logDir);
        log.info("dbDir = {}",dbDir);

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


            for (Map.Entry<String, String> entry : params.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();

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

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                String fieldName = file.getName();
                if (fieldName.equals("peptideFile")) {
                    log.info("peptide");
                    peptideFile = "peptide_" + key + ".txt";
                    peptideFilePath = logDir + peptideFile;

                    if (file.getSize() > 1024 * 100) {
                        failed = true;
                        output = "The size of peptide list should not exceed 1KB.";
                        return result;
                    }

                    if (!file.isEmpty()) {
                        try {
                            File noPeptideFile = new File(peptideFilePath);
                            File parentDir = noPeptideFile.getParentFile();
                            if (!parentDir.exists()) {
                                parentDir.mkdirs();
                            }

                            if (!noPeptideFile.exists()) {
                                noPeptideFile.createNewFile();
                            }

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
                FileReader FR = new FileReader(dbDir + "template.xml");
                BufferedReader BR = new BufferedReader(FR);

                File xmlFile = new File(logDir + xmlPath);
                File parentDir = xmlFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
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

                        if (line.contains(logDir)) {
                            line = line.replace(logDir, "");
                        }
                        if (line.contains(dbDir)) {
                            line = line.replace(dbDir, "");
                        }

                        if (line.contains("%")) {
                            rate = line;
                        } else {
                            allWriter.append(line).append("\n");
                        }

                        writer.getBuffer().setLength(0);
                    } else {
                        writer.append(character);
                    }
                }

                if (writer.getBuffer().length() > 0) {
                    line = writer.toString();
                }

                output = allWriter.toString();

                if (finished) {
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

        }

        ActgDto processDto = ActgDto.builder().failed(failed).finished(finished).output(output)
                .processName(processName).prixIndex("").rate((rate)).title(title).build();

        return processDto;
    }

}
