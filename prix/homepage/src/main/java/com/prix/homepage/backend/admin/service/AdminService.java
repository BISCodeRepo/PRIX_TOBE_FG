package com.prix.homepage.backend.admin.service;

import com.prix.homepage.backend.admin.mapper.AdminMapper;
import com.prix.homepage.backend.basic.utils.Mailer;
import com.prix.homepage.backend.basic.utils.PathUtil;
import com.prix.homepage.backend.basic.utils.PrixDataWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;

import static com.prix.homepage.backend.basic.utils.PathUtil.*;
import static com.prix.homepage.backend.basic.utils.PathUtil.PATH_CONFIG;

@Service
public class AdminService {

    // UserMapper 의존성 주입을 위한 필드 선언
    private final AdminMapper adminMapper;

    private final Mailer mailer;

    @Autowired
    public AdminService(AdminMapper adminMapper, Mailer mailer) {
        this.adminMapper = adminMapper;
        this.mailer = mailer;
    }

    public void uploadDatabase(String dbName, MultipartFile dbFile) throws Exception {
        // 파일 저장 경로 설정. 윈도우용 임시 주석처리
        String root = PathUtil.PATH_CONFIG;
        String originalFilename = dbFile.getOriginalFilename();
        String dbPath = originalFilename;
//        String dbPath = originalFilename != null ? originalFilename.replace('\\', '/') : "";

        // 파일 저장 처리
        if (!dbFile.isEmpty()) {
            int dataId = PrixDataWriter.write("fasta", dbPath, dbFile.getInputStream());

            String path = root + dbPath;
            File dest = new File(path);
            dbFile.transferTo(dest);  // 파일 저장

            // 데이터베이스에 삽입할 때 파일명에서 확장자를 제거하여 이름 설정
            if (dbName == null || dbName.isEmpty()) {
                int last = dbPath.lastIndexOf('.');
                dbName = (last < 0) ? dbPath : dbPath.substring(0, last);
            }

            // 데이터베이스 처리
            adminMapper.insertDatabaseFile(dbName, dbPath, dataId);
        }
    }


    public void addModification(String modDate, String modVersion, MultipartFile file) throws Exception {
        FileInputStream fis = null;

        // 파일 저장
        initializeDirectories();

        String root = PathUtil.PATH_CONFIG;
//            String root = "D:\\";
        String originalFilename = file.getOriginalFilename();
        String modFile = originalFilename != null ? originalFilename.replace('\\', '/') : "";
        String path = root + modFile;

        File dest = new File(path);
        file.transferTo(dest);  // 파일 저장

        // 파일 저장 후, XML 파싱
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = docFactory.newDocumentBuilder();

        fis = new FileInputStream(dest);  // FileInputStream 수동으로 열기
        Document doc = builder.parse(fis);  // XML 파싱

        // Classification 처리
        HashMap<String, Integer> classMap = new HashMap<>();
        NodeList classNodes = doc.getElementsByTagName("classificationRow");
        for (int i = 0; i < classNodes.getLength(); i++) {
            Node node = classNodes.item(i);
            NamedNodeMap attributes = node.getAttributes();
            Node classAttr = attributes.getNamedItem("classification");
            String classification = classAttr.getNodeValue();

            // 데이터베이스에서 classification 가져오기
            Integer classId = adminMapper.getClassificationId(classification);
            if (classId == null) {
                adminMapper.insertClassification(classification);
                classId = adminMapper.getClassificationId(classification);
            }
            classMap.put(classification, classId);
        }

        // PTM 처리
        NodeList ptmNodes = doc.getElementsByTagName("PTM");
        for (int i = 0; i < ptmNodes.getLength(); i++) {
            Node ptmNode = ptmNodes.item(i);
            NodeList ptmChildren = ptmNode.getChildNodes();

            String modName = "", fullName = "", classi = "", md = "", amd = "", residue = "", position = "";

            for (int j = 0; j < ptmChildren.getLength(); j++) {
                Node child = ptmChildren.item(j);
                String nodeName = child.getNodeName();

                // null 방지 처리 및 인코딩 처리
                if (nodeName.equals("name")) modName = getSafeString(child.getFirstChild());
                else if (nodeName.equals("fullName")) fullName = getSafeString(child.getFirstChild());
                else if (nodeName.equals("classification")) classi = getSafeString(child.getFirstChild());
                else if (nodeName.equals("massDifference")) md = getSafeString(child.getFirstChild());
                else if (nodeName.equals("avgMassDifference")) amd = getSafeString(child.getFirstChild());
                else if (nodeName.equals("residue")) residue = getSafeString(child.getFirstChild());
                else if (nodeName.equals("position")) position = getSafeString(child.getFirstChild());
            }

            // 데이터베이스에 PTM 정보 삽입
            try {
                adminMapper.insertPTM(modName, fullName, classMap.get(classi), md, amd, residue, position);
            } catch (Exception e) {
                e.printStackTrace();  // 오류 발생 시 오류 로그 출력
            }
        }

        // 로그 기록 (modification log 테이블)
        adminMapper.insertModificationLog(modDate, modVersion, modFile);

//        finally {
//            // FileInputStream 수동으로 닫기
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();  // 스트림을 닫는 중 오류가 발생할 경우 처리
//                }
//            }
//        }
    }

    public void handleSoftwareUpload(String sftwName, String sftwVersion, String sftwDate,
                                       MultipartFile sftwFile) throws Exception {
        initializeDirectories();

        // 기존 파일을 deprecated 폴더로 이동
        File releaseDir = new File(PATH_SW_RELEASE);
        for (File file : releaseDir.listFiles()) {
            if (file.getName().startsWith(sftwName.toLowerCase())) {
                // 기존 파일을 deprecated 폴더로 이동
                file.renameTo(new File(PATH_SW_DEPRECATED + new Date().getTime() + "_" + file.getName()));
                break;
            }
        }

        // 새로운 파일 저장
        if (!sftwFile.isEmpty()) {
            // 파일 이름 설정
            String sftwFileName = sftwName.toLowerCase() + "_v" + sftwVersion + ".zip";
            String filePath = PATH_SW_RELEASE + sftwFileName;

            // 파일 저장 처리
            File dest = new File(filePath);
            sftwFile.transferTo(dest);  // 파일 저장

            // 데이터베이스에 삽입 (Mapper 호출)
            adminMapper.insertSoftwareLog(sftwName, sftwDate, sftwVersion, sftwFileName);
        }

    }

    public void initializeDirectories() {
        File releaseDir = new File(PATH_SW_RELEASE);
        File deprecatedDir = new File(PATH_SW_DEPRECATED);
        File configDir = new File(PATH_CONFIG);

        // releaseDir 생성
        if (!releaseDir.exists()) {
            if (releaseDir.mkdirs()) {
                System.out.println("Release directory created: " + PATH_SW_RELEASE);
            } else {
                System.err.println("Failed to create release directory: " + PATH_SW_RELEASE);
            }
        }

        // deprecatedDir 생성
        if (!deprecatedDir.exists()) {
            if (deprecatedDir.mkdirs()) {
                System.out.println("Deprecated directory created: " + PATH_SW_DEPRECATED);
            } else {
                System.err.println("Failed to create deprecated directory: " + PATH_SW_DEPRECATED);
            }
        }

        // configDir 생성
        if (!configDir.exists()) {
            if (configDir.mkdirs()) {
                System.out.println("config directory created: " + PATH_CONFIG);
            } else {
                System.err.println("Failed to create config directory: " + PATH_CONFIG);
            }
        }
    }

    /**
     * null 방지 처리 및 인코딩 처리 함수
     */
    public static String getSafeString(Node node) {
        try {
            if (node != null) {
                return new String(node.getNodeValue().getBytes("ISO-8859-1"), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void updateRequest(Integer id, String name, String email, String software) throws Exception {
        String swroot = PATH_SW_RELEASE;
        String URL_BASE =
                "http://166.104.110.37:8081/";

//        String download_root = "https://prix.hanyang.ac.kr/download/software_archive/release";
        String download_root =  URL_BASE + "download/software";

        int find = 0;
        // 소프트웨어에 맞는 파일 검색
        File protDir = new File(swroot);  // 소프트웨어 파일 루트 디렉토리
        for (File file : protDir.listFiles()) {
            String fileLowerCase = file.getName().toLowerCase();
            if (fileLowerCase.startsWith(software.toLowerCase())) {
                String message = adminMapper.getMessageBySoftware(software.toLowerCase() + "_link");
                String signature = adminMapper.getMessageBySoftware("signature");

                // 파일 이름 포함 링크 경로 생성
                String linkPath = download_root + "/" + file.getName();
                message = message.replace("<link>", linkPath);

                // 메일 전송 (파일 없이 링크만 포함)
                mailer.sendEmailToUser(name, email, software, message, signature, null);
                adminMapper.updateRequestState(id, 1); // 1 for accepted
                find = 1;
                break;  // 파일을 찾았으므로 루프 종료
            }
        }

//        if (find == 0) {
//            log.info("file not found");
//        }

    }
}
