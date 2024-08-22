package com.prix.homepage.backend.livesearch.service.dbond;

import com.prix.homepage.backend.basic.utils.PathUtil;
import com.prix.homepage.backend.basic.utils.PrixDataWriter;
import com.prix.homepage.backend.livesearch.dto.dbond.ProcessRequestDto;
import com.prix.homepage.backend.livesearch.mapper.ModificationMapper;
import com.prix.homepage.backend.livesearch.mapper.SearchLogMapper;
import com.prix.homepage.backend.livesearch.mapper.UserSettingMapper;
import com.prix.homepage.backend.livesearch.mapper.dbond.DBondMapper;
import com.prix.homepage.backend.livesearch.mapper.dbond.EnzymeMapper;
import com.prix.homepage.backend.livesearch.pojo.Modification;
import com.prix.homepage.backend.livesearch.pojo.dbond.Database;
import com.prix.homepage.backend.livesearch.pojo.dbond.DatabaseInfo;
import com.prix.homepage.backend.livesearch.pojo.dbond.Enzyme;
import com.prix.homepage.backend.livesearch.pojo.dbond.PxData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class DBondService {

    private final DBondMapper dBondMapper;
    private final EnzymeMapper enzymeMapper;
    private final ModificationMapper modificationMapper;
    private final UserSettingMapper userSettingMapper;
    private final SearchLogMapper searchLogMapper;

    public List<Enzyme> getUserEnzymes(Integer userId) {
        return dBondMapper.findByUserId(userId);
    }
    public List<Database> getDatabaseList() {
        return dBondMapper.findAll();
    }

    public PxData getDataById(int id) {
        return dBondMapper.findDataById(id);
    }

    public  ProcessRequestDto processFilesAndData(Integer id, Map<String, String> params, MultipartFile[] files) {
        String user = "";
        String title = "";
        String dataFormat = "";
        String instrument = "";
        String msResolution = "";
        String msmsResolution = "";
        String database = "";
        String decoy = "";
        String fastaDecoy = "";
        String enzyme = "";
        String missedCleavage = "";
        String minNumEnzTerm = "";
        String pTolerance = "";
        String pUnit = "";
        String fTolerance = "";
        String minMM = "";
        String maxMM = "";
        String modMap = "";
        String multiStage = "";
        String cysteinAlkylation = "";
        String alkylationName = "";
        String alkylationMass = "";
        String engine = params.get("engine");

        String msFile = "";
        String dbFile = "";
        String decoyFile = "";
        int msIndex = -10;
        int dbIndex = -1;

        if (params.get("mi") != null) {
            msIndex = Integer.parseInt(params.get("mi"));
        }
        if (params.get("di") != null) {
            dbIndex = Integer.parseInt(params.get("di"));
        }

        String logPath = params.get("log");
        String xmlPath = params.get("xml");
        String msPath = params.get("ms");
        String dbPath = params.get("db");
        String decoyPath = params.get("dec");
        String multiPath = params.get("mul");

        String line = "";
        int rate = 0;
        boolean finished = false;
        boolean failed = false;
        String output = "";

        if (params.get("execute") == null) {
            final String dir = PathUtil.getGlobalDirectoryPath("/home/PRIX/data/");
            final String dbDir = PathUtil.getGlobalDirectoryPath("/usr/local/server/apache-tomcat-8.0.14/webapps/ROOT/config/");

            log.info("dir = {}",dir);
            Date date = new Date();
            String tempdate = String.valueOf(date.getTime());
            logPath = dir + "modi_output_" + id + "_" + tempdate + ".log";
            xmlPath = dir + "modi_input_" + id + "_" + tempdate + ".xml";
            msPath = dir + "ms_" + id + "_" + tempdate;
            dbPath = dir + "db_" + id + "_" + tempdate + ".fasta";
            decoyPath = dir + "decoy_" + id + "_" + tempdate + ".fasta";

            List<Map.Entry<String, String>> items = new ArrayList<>(params.entrySet());
            for (Map.Entry<String, String> item : items) {
                String name = item.getKey();
                String value = item.getValue();

                switch (name) {
                    case "user":
                        user = value;
                        break;
                    case "title":
                        title = value;
                        break;
                    case "ms_format":
                        dataFormat = value;
                        msPath += "." + dataFormat;
                        break;
                    case "ms_instrument":
                        instrument = value;
                        break;
                    case "ms_resolution":
                        msResolution = value;
                        break;
                    case "msms_resolution":
                        msmsResolution = value;
                        break;
                    case "database":
                        database = value;
                        break;
                    case "decoy":
                        decoy = value;
                        break;
                    case "fasta_decoy":
                        fastaDecoy = value;
                        break;
                    case "enzyme":
                        enzyme = value;
                        break;
                    case "missed_cleavage":
                        missedCleavage = value;
                        break;
                    case "ntt":
                        minNumEnzTerm = value;
                        break;
                    case "pept_tolerance":
                        pTolerance = value;
                        break;
                    case "unit":
                        pUnit = value;
                        break;
                    case "frag_tolerance":
                        fTolerance = value;
                        break;
                    case "min_modified_mass":
                        minMM = value;
                        break;
                    case "max_modified_mass":
                        maxMM = value;
                        break;
                    case "modmap":
                        modMap = value;
                        break;
                    case "multistage":
                        multiStage = value;
                        break;
                    case "cystein_alkylation":
                        cysteinAlkylation = value;
                        break;
                    case "alkylation_name":
                        alkylationName = value;
                        break;
                    case "alkylation_mass":
                        alkylationMass = value;
                        break;
                    case "engine":
                        engine = value;
                        break;
                    case "msindex":
                        msIndex = Integer.parseInt(value);
                        handleFileFromDatabase(msIndex, msPath);
                        break;
                    case "dbindex":
                        dbIndex = Integer.parseInt(value);
                        handleFileFromDatabase(dbIndex, dbPath);
                        break;
                }

            }
            for (MultipartFile file : files) {
                String name = file.getName();
                if (name.equals("ms_file")) {
                    msFile = file.getOriginalFilename();
                    if (file.getSize() > 209715200 * 2.5) {
                        failed = true;
                        output = "MS file size should not exceed 500MB.";
                        break;
                    }
                    if (msFile.length() > 0) {
                        try {
                            msIndex = writeDataToDatabase(dataFormat, msFile, file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            saveFileToDisk(file, msPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else if (name.equals("fasta")) {
                    dbFile = file.getOriginalFilename();
                    if (file.getSize() > 52428800) {
                        failed = true;
                        output = "Database file size should not exceed 50MB.";
                        break;
                    }
                    if (dbFile.length() > 0) {
                        try {
                            dbIndex = writeDataToDatabase("fasta", dbFile, file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            saveFileToDisk(file, dbPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            if (!failed) {
                try (PrintStream ps = new PrintStream(new FileOutputStream(xmlPath), false, "UTF-8")) {
                    ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    if (title == null || title.isEmpty())
                        title = "prix" + msIndex;
                    ps.println("<search user=\"" + user + "\" title = \"" + title + "\">");
                    ps.println("\t<dataset name=\"" + msFile + "\" format=\"" + dataFormat + "\" instrument=\"" + instrument + "\" local_path=\"" + msPath + "\" db_index=\"" + msIndex + "\"/>");

                    if (!dbFile.isEmpty())
                        ps.println("\t<database name=\"" + dbFile + "\" local_path=\"" + dbPath + "\" db_index=\"" + dbIndex + "\"/>");
                    else {
                        String dbName = "";
                        DatabaseInfo dbInfo = dBondMapper.getDatabaseInfoById(Integer.parseInt(database));
                        if (dbInfo != null) {
                            dbIndex = dbInfo.getDataId();
                            dbName = dbInfo.getFileName();
                        }
                        ps.println("\t<database name=\"" + dbName + "\" local_path=\"" + dbDir + dbName + "\" db_index=\"" + dbIndex + "\"/>");
                    }

                    Enzyme enzymeData = enzymeMapper.getEnzymeById(Integer.parseInt(enzyme));
                    if (enzymeData != null) {
                        String nt = enzymeData.getNtCleave().trim();
                        if (nt.isEmpty()) {
                            ps.println("\t<enzyme name=\"" + enzymeData.getName() + "\" cut=\"" + enzymeData.getCtCleave() + "\" sence=\"C\"/>");
                        } else {
                            ps.println("\t<enzyme name=\"" + enzymeData.getName() + "\" cut=\"" + enzymeData.getNtCleave() + "\" sence=\"N\"/>");
                        }
                    }

                    ps.println("\t<parameters>");
                    ps.println("\t\t<enzyme_constraint max_miss_cleavages=\"" + missedCleavage + "\" min_number_termini=\"" + minNumEnzTerm + "\"/>");
                    ps.println("\t\t<peptide_mass_tol value=\"" + pTolerance + "\" unit=\"" + pUnit + "\"/>");
                    ps.println("\t\t<fragment_ion_tol value=\"" + fTolerance + "\" unit=\"Da\"/>");
                    ps.println("\t\t<modified_mass_range min_value=\"" + minMM + "\" max_value=\"" + maxMM + "\"/>");
                    ps.println("\t</parameters>");

                    if (!decoyFile.isEmpty())
                        ps.println("\t<decoy_search name=\"" + decoyFile + "\" local_path=\"" + decoyPath + "\"/>");
                    else if (!decoy.isEmpty())
                        ps.println("\t<decoy_search checked=\"1\"/>");

                    if (engine != null && !engine.equals("dbond"))
                        ps.println("\t<instrument_resolution ms=\"" + msResolution + "\" msms=\"" + msmsResolution + "\"/>");

                    if (!modMap.isEmpty())
                        ps.println("\t<mod_map checked=\"1\"/>");

                    if (!multiStage.isEmpty()) {
                        ps.println("\t<multistages_search checked=\"1\" program=\"\"/>");
                        multiPath = msPath + ".fasta";
                    }

                    if (!cysteinAlkylation.isEmpty()) {
                        if (cysteinAlkylation.equals("Direct input")) {
                            ps.println("\t<cys_alkylated name=\"" + alkylationName + "\" massdiff=\"" + alkylationMass + "\"/>");
                        } else {
                            String massDiff = modificationMapper.getMassDiffByName(cysteinAlkylation);
                            if (massDiff != null) {
                                ps.println("\t<cys_alkylated name=\"" + cysteinAlkylation + "\" massdiff=\"" + massDiff + "\"/>");
                            }
                        }
                    }

                    ps.println("\t<modifications>");
                    ps.println("\t\t<fixed>");
                    List<Modification> fixedMods = modificationMapper.getModificationsByUserAndEngine(id, 0, engine.compareTo("dbond") == 0 ? 1 : 0);
                    for (Modification mod : fixedMods) {
                        ps.println("\t\t\t<mod name=\"" + mod.getName() + "\" site=\"" + mod.getResidue() + "\" position=\"" + mod.getPosition() + "\" massdiff=\"" + mod.getMassDiff() + "\"/>");
                    }
                    ps.println("\t\t</fixed>");
                    ps.println("\t\t<variable>");
                    List<Modification> variableMods = modificationMapper.getModificationsByUserAndEngine(id, 1, engine.compareTo("dbond") == 0 ? 1 : 0);
                    for (Modification mod : variableMods) {
                        ps.println("\t\t\t<mod name=\"" + mod.getName() + "\" site=\"" + mod.getResidue() + "\" position=\"" + mod.getPosition() + "\" massdiff=\"" + mod.getMassDiff() + "\"/>");
                    }
                    ps.println("\t\t</variable>");
                    ps.println("\t</modifications>");
                    ps.println("</search>");
                    ps.flush();
                    ps.close();

                    // 사용자 설정 업데이트 또는 삽입
                    boolean exist = userSettingMapper.existsByUserId(id);
                    String sql = "";

                    if ("dbond".equals(engine)) {
                        if (exist) {
                            userSettingMapper.updateDbondSetting(id, enzyme, missedCleavage, pTolerance, pUnit, fTolerance, engine, dataFormat, instrument);
                        } else {
                            userSettingMapper.insertDbondSetting(id, enzyme, missedCleavage, pTolerance, pUnit, fTolerance, engine, dataFormat, instrument);
                        }
                    } else {
                        if (exist) {
                            userSettingMapper.updateOtherSetting(id, enzyme, missedCleavage, minNumEnzTerm, pTolerance, pUnit, fTolerance, minMM, maxMM, engine, dataFormat, instrument, msResolution, msmsResolution);
                        } else {
                            userSettingMapper.insertOtherSetting(id, enzyme, missedCleavage, minNumEnzTerm, pTolerance, pUnit, fTolerance, minMM, maxMM, engine, dataFormat, instrument, msResolution, msmsResolution);
                        }
                    }

                    // 외부 프로세스 실행
//                    String[] command = {"/bin/bash", "-c", String.format("%s%s %s > %s", "java -Xmx2000M -cp /usr/local/server/apache-tomcat-8.0.14/webapps/ROOT/WEB-INF/lib/engine.jar:/usr/local/server/apache-tomcat-8.0.14/webapps/ROOT/WEB-INF/lib/jdom.jar:/usr/local/server/apache-tomcat-8.0.14/webapps/ROOT/WEB-INF/lib/jrap_StAX_v5.2.jar:/usr/local/server/apache-tomcat-8.0.14/webapps/ROOT/WEB-INF/lib/xercesImpl.jar prix.Prix_", engine, xmlPath, logPath) };
//                    Runtime.getRuntime().exec(command);

                    String classpath = "C:\\Users\\82108\\Desktop\\prix\\lib\\engine.jar;" +
                            "C:\\Users\\82108\\Desktop\\prix\\lib\\jdom.jar;" +
                            "C:\\Users\\82108\\Desktop\\prix\\lib\\jrap_StAX_v5.2.jar;" +
                            "C:\\Users\\82108\\Desktop\\prix\\lib\\xercesImpl.jar";

                    String[] command = {"cmd.exe", "/c", String.format("java -Xmx2000M -cp %s prix.Prix_%s %s > %s", classpath, engine, xmlPath, logPath)};
                    Runtime.getRuntime().exec(command);

                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    e.printStackTrace();} catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }else {
            int prixIndex = -1;

            File filePath = new File(logPath);
            // 파일의 디렉토리 경로를 가져옵니다.
            File dir = filePath.getParentFile();

            // 디렉토리가 존재하지 않으면 생성
            if (dir != null && !dir.exists()) {
                dir.mkdirs(); // 디렉토리 및 상위 디렉토리 생성
            }

            // 파일이 존재하지 않으면 파일을 생성
            if (!filePath.exists()) {
                try {
                    filePath.createNewFile(); // 빈 파일 생성
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // 로그 파일 처리
            try (FileInputStream fis = new FileInputStream(logPath);
                 StringWriter writer = new StringWriter();
                 StringWriter allWriter = new StringWriter()) {

                while (fis.available() > 0) {
                    char c = (char) fis.read();
                    allWriter.append(c);
                    if (c == '\n') {
                        if (writer.getBuffer().length() > 0) {
                            line = writer.toString();
                            writer.getBuffer().setLength(0); // StringWriter의 내용을 초기화
                            log.info("line = {}",line);
                        }
                    } else {
                        writer.append(c);
                    }
                }
                if (writer.getBuffer().length() > 0) {
                    line = writer.toString();
                }
                log.info("final line = {}",line);

                output = allWriter.toString();
                if (line.contains("Error") || line.contains("Exception")) {
                    failed = true;
                } else if (line.startsWith("Elapsed Time")) {
                    finished = true;

                    // PRIX 파일 삽입
                    String prixPath = msPath.substring(0, msPath.length() - 3) + "prix";
                    File file = new File(prixPath);
                    try (FileInputStream prixFis = new FileInputStream(file)) {
                        prixIndex = PrixDataWriter.write("prix", prixPath, prixFis);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    // 검색 기록 삽입
                    searchLogMapper.insertSearchLog(id, title.replace("'", "\\\'"), msIndex, dbIndex, prixIndex, engine);

                } else if (line.startsWith("Processing")) {
                    int split = line.indexOf("/");
                    int numerator = Integer.parseInt(line.substring(11, split));
                    int denominator = Integer.parseInt(line.substring(split + 1, line.indexOf(" ", split)));
                    rate = 100 * numerator / denominator;
                }

                if (finished) {
                    // 다중 단계 작업 처리
                    if (multiPath != null && multiPath.length() > 0 && !"null".equals(multiPath)) {
                        Integer databaseId = dBondMapper.findDatabaseIdByDataId(dbIndex);
                        if (databaseId == null) {
                            try (FileInputStream is = new FileInputStream(multiPath)) {
                                PrixDataWriter.replace(dbIndex,is);
                                is.close();
                            } catch (IOException e) {
                                log.error("Error processing multi-stage file", e);
                                throw new RuntimeException("Error processing multi-stage file", e);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    ProcessRequestDto requestDto = ProcessRequestDto.builder()
                            .id(id)
                            .finished(finished)
                            .failed(failed)
                            .title(title)
                            .dataFormat(dataFormat)
                            .instrument(instrument)
                            .msResolution(msResolution)
                            .msmsResolution(msmsResolution)
                            .database(database)
                            .decoy(decoy)
                            .fastaDecoy(fastaDecoy)
                            .enzyme(enzyme)
                            .missedCleavage(missedCleavage)
                            .minNumEnzTerm(minNumEnzTerm)
                            .pTolerance(pTolerance)
                            .pUnit(pUnit)
                            .fTolerance(fTolerance)
                            .minMM(minMM)
                            .maxMM(maxMM)
                            .modMap(modMap)
                            .multiStage(multiStage)
                            .cysteinAlkylation(cysteinAlkylation)
                            .alkylationName(alkylationName)
                            .alkylationMass(alkylationMass)
                            .engine(engine)
                            .msIndex(msIndex)
                            .dbIndex(dbIndex)
                            .logPath(logPath)
                            .xmlPath(xmlPath)
                            .msPath(msPath)
                            .dbPath(dbPath)
                            .decoyPath(decoyPath)
                            .multiPath(multiPath)
                            .execute(params.get("execute"))
                            .output(output)
                            .rate(rate)
                            .address("redirect:/dbond/result?file=" + prixIndex)
                            .build();

                    return requestDto;
                }

            } catch (IOException e) {
                log.error("Error processing log file", e);
                throw new RuntimeException("Error processing log file", e);
            }
        }
        ProcessRequestDto requestDto = ProcessRequestDto.builder()
                .id(id)
                .finished(finished)
                .failed(failed)
                .title(title)
                .dataFormat(dataFormat)
                .instrument(instrument)
                .msResolution(msResolution)
                .msmsResolution(msmsResolution)
                .database(database)
                .decoy(decoy)
                .fastaDecoy(fastaDecoy)
                .enzyme(enzyme)
                .missedCleavage(missedCleavage)
                .minNumEnzTerm(minNumEnzTerm)
                .pTolerance(pTolerance)
                .pUnit(pUnit)
                .fTolerance(fTolerance)
                .minMM(minMM)
                .maxMM(maxMM)
                .modMap(modMap)
                .multiStage(multiStage)
                .cysteinAlkylation(cysteinAlkylation)
                .alkylationName(alkylationName)
                .alkylationMass(alkylationMass)
                .engine(engine)
                .msIndex(msIndex)
                .dbIndex(dbIndex)
                .logPath(logPath)
                .xmlPath(xmlPath)
                .msPath(msPath)
                .dbPath(dbPath)
                .decoyPath(decoyPath)
                .multiPath(multiPath)
                .execute(params.get("execute"))
                .output(output)
                .rate(rate)
                .address("livesearch/process")
                .build();

        return requestDto;
    }

    private void handleFileFromDatabase(int index, String path) {
        File file = new File(path);
        if (!file.exists()) {
            byte[] fileData = dBondMapper.getFileDataById(index);
            if (fileData != null) {
                try (FileOutputStream fos = new FileOutputStream(file);
                     OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
                     InputStream is = new ByteArrayInputStream(fileData)) {

                    while (is.available() > 0) {
                        writer.write(is.read());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int writeDataToDatabase(String format, String fileName, MultipartFile file) throws IOException {
        try {
            return PrixDataWriter.write(format, fileName.replace('\\', '/'), file.getInputStream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFileToDisk(MultipartFile file, String path) throws IOException {
        // 경로에서 디렉토리 부분만 추출
        File filePath = new File(path);
        File dir = filePath.getParentFile(); // 파일의 디렉토리 경로를 가져옵니다.

        // 디렉토리가 존재하지 않으면 생성
        if (dir != null && !dir.exists()) {
            dir.mkdirs(); // 디렉토리 및 상위 디렉토리 생성
        }

        try (FileOutputStream fos = new FileOutputStream(path);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
             InputStream is = file.getInputStream()) {

            while (is.available() > 0) {
                writer.write(is.read());
            }
        }
    }
}

