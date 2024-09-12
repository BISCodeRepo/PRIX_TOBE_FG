package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.entity.*;
import com.prix.homepage.backend.account.domain.User;
import com.prix.homepage.backend.basic.utils.Mailer;
import com.prix.homepage.backend.basic.utils.PathUtil;
import com.prix.homepage.backend.basic.utils.PrixDataWriter;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.prix.homepage.backend.basic.utils.PathUtil.PATH_SW;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController extends BaseController {

    private final AdminMapper adminMapper;

    private final Mailer mailer;

    @Autowired
    public AdminController(AdminService adminService, AdminMapper adminMapper, Mailer mailer) {
        this.adminMapper = adminMapper;
        this.mailer = mailer;
    }

    @GetMapping("/configuration")
    public String configuration(Model model) {

        Database[] database = adminMapper.selectAllFile();
        Enzyme[] enzyme = adminMapper.selectAllEnzyme();
        SoftwareLog[] softwareLogs = adminMapper.selectAllSoftwareLogs();
        ModificationLog[] modificationLogs = adminMapper.selectAllModificationLogs();

        model.addAttribute("databases", database);
        model.addAttribute("enzymes", enzyme);
        model.addAttribute("softwareLogs", softwareLogs);
        model.addAttribute("modificationLogs", modificationLogs);

        model.addAttribute("modaMessage", adminMapper.getMessageBySoftware("mode"));
        model.addAttribute("dbondMessage", adminMapper.getMessageBySoftware("dbond"));
        model.addAttribute("nextsearchMessage", adminMapper.getMessageBySoftware("nextsearch"));
        model.addAttribute("signatureMessage", adminMapper.getMessageBySoftware("signature"));

        return "admin/configuration";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("db_name") String dbName,
                                   @RequestParam("db_file") MultipartFile dbFile) {
        try {
            // 파일 저장 경로 설정. 윈도우용 임시 주석처리
            String root = PathUtil.PATH_CONFIG;
//            String root = "D:\\";
            String originalFilename = dbFile.getOriginalFilename();
            String dbPath = originalFilename != null ? originalFilename.replace('\\', '/') : "";

            // 파일 저장 처리
            if (!dbFile.isEmpty()) {
                String path = root + dbPath;
                File dest = new File(path);
                dbFile.transferTo(dest);  // 파일 저장

                // 데이터베이스에 삽입할 때 파일명에서 확장자를 제거하여 이름 설정
                if (dbName == null || dbName.isEmpty()) {
                    int last = dbPath.lastIndexOf('.');
                    dbName = (last < 0) ? dbPath : dbPath.substring(0, last);
                }

                // 데이터베이스 처리
                int dataId = PrixDataWriter.write("fasta", dbPath, dbFile.getInputStream());
                adminMapper.insertDatabaseFile(dbName, dbPath, dataId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";  // 에러 발생 시 처리
        }

        return "redirect:/admin/configuration";
    }

    @PostMapping("/update_file")
    public String updateFile(
            @ModelAttribute("Database") Database database,
            @RequestParam("action") String action) {

        if (" edit name ".equals(action)) {
            adminMapper.updateDatabase(database.getId(), database.getName());
        } else if (" unlink ".equals(action)) {
            adminMapper.deleteDatabase(database.getId());
        }
        return "redirect:/admin/configuration";
    }

    @PostMapping("/update_enzyme")
    public String updateEnzymes(
            @ModelAttribute("Enzyme") Enzyme enzyme,
            @RequestParam("action") String action) {

        if (" edit name ".equals(action)) {
            adminMapper.updateEnzyme(enzyme.getId(), enzyme.getName(), enzyme.getNt_cleave(), enzyme.getCt_cleave());
        } else if (" delete ".equals(action)) {
            adminMapper.deleteEnzyme(enzyme.getId());
        }
        return "redirect:/admin/configuration";
    }

    @PostMapping("/add_enzyme")
    public String addEnzyme(
            @ModelAttribute("Enzyme") Enzyme enzyme,
            Model model) {

        int userId = (int) model.getAttribute(SESSION_KEY_ID);
        adminMapper.insertEnzyme(userId, enzyme.getName(), enzyme.getNt_cleave(), enzyme.getCt_cleave());
        return "redirect:/admin/configuration";
    }

    @PostMapping("/add_modification")
    public String addModification(@RequestParam("date") String modDate,
                                  @RequestParam("version") String modVersion,
                                  @RequestParam("file") MultipartFile file) {

        FileInputStream fis = null;

        try {
            // 파일 저장
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

        } catch (Exception e) {
            e.printStackTrace();
            return "error";  // 에러 발생 시 처리
        } finally {
            // FileInputStream 수동으로 닫기
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();  // 스트림을 닫는 중 오류가 발생할 경우 처리
                }
            }
        }

        return "redirect:/admin/configuration";
    }

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


    @PostMapping("/upload_software")
    public String handleSoftwareUpload(@RequestParam("sftw_name") String sftwName,
                                       @RequestParam("sftw_version") String sftwVersion,
                                       @RequestParam("sftw_date") String sftwDate,
                                       @RequestParam("sftw_file") MultipartFile sftwFile) {
        try {
//            String sftwRoot = "D:\\";
            String sftwRoot = PATH_SW;

            // 기존 파일을 deprecated 폴더로 이동
            File releaseDir = new File(sftwRoot + "release/");
            for (File file : releaseDir.listFiles()) {
                if (file.getName().startsWith(sftwName.toLowerCase())) {
                    // 기존 파일을 deprecated 폴더로 이동
                    file.renameTo(new File(sftwRoot + "deprecated/" + new Date().getTime() + "_" + file.getName()));
                    break;
                }
            }

            // 새로운 파일 저장
            if (!sftwFile.isEmpty()) {
                // 파일 이름 설정
                String sftwFileName = sftwName.toLowerCase() + "_v" + sftwVersion + ".zip";
                String filePath = sftwRoot + "release/" + sftwFileName;

                // 파일 저장 처리
                File dest = new File(filePath);
                sftwFile.transferTo(dest);  // 파일 저장

                // 데이터베이스에 삽입 (Mapper 호출)
                adminMapper.insertSoftwareLog(sftwName, sftwDate, sftwVersion, sftwFileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "error";  // 에러 발생 시 처리
        }

        return "redirect:/admin/configuration";  // 성공 시 리다이렉트
    }


    @PostMapping("/update_software_message")
    public String modifySoftwareMessages(
            @RequestParam(name = "modamsg", required = false) String modaMessage,
            @RequestParam(name = "dbondmsg", required = false) String dbondMessage,
            @RequestParam(name = "nextmsg", required = false) String nextMessage,
            @RequestParam(name = "signature", required = false) String signatureMessage
    ) {
        // 각 메시지를 이스케이프 처리한 후, 바로 Mapper를 통해 업데이트
        adminMapper.updateSoftwareMessage("mode", modaMessage.replace("'", "\\'"));
        adminMapper.updateSoftwareMessage("dbond", dbondMessage.replace("'", "\\'"));
        adminMapper.updateSoftwareMessage("nextsearch", nextMessage.replace("'", "\\'"));
        adminMapper.updateSoftwareMessage("signature", signatureMessage.replace("'", "\\'"));

        return "redirect:/admin/configuration";
    }

    @GetMapping("/searchlog")
    public String searchLog(@RequestParam(value = "p", defaultValue = "0") int curPage, Model model) {

        final int pageSize = 50;
        int offset = curPage * pageSize;

        List<SearchLog> searchLogs = adminMapper.findSearchLogs(offset, pageSize);
        int totalRecords = adminMapper.countSearchLogs();
        int totalPage = (totalRecords - 1) / pageSize + 1;

        model.addAttribute("searchLogs", searchLogs);
        model.addAttribute("curPage", curPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("totalPage", totalPage);

        return "admin/searchlog";
    }


    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = adminMapper.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/update_user")
    public String modifyUser(
            @RequestParam(name = "up", required = false) Integer upId,
            @RequestParam(name = "down", required = false) Integer downId,
            @RequestParam(name = "del", required = false) Integer delId) {

        if (upId != null) {
            adminMapper.updateUserLevel(upId, 2);
        } else if (downId != null) {
            adminMapper.updateUserLevel(downId, 1);
        } else if (delId != null) {
            adminMapper.deleteUserById(delId);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/requestlog")
    public String showRequestLog(Model model) {
        List<SoftwareRequest> requests = adminMapper.findAllRequests();
        model.addAttribute("requests", requests);
        model.addAttribute("totalRequests", requests.size());
        return "admin/requestlog";
    }

    @PostMapping("/requestlog/accept")
    public String updateRequest(
        @RequestParam(name = "id", required = false) Integer id,
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "software", required = false) String software
        ) {

        String sendMsg = "hello";
        String sig = "sig";
        String path = "path";

        String swroot = "/usr/local/server/apache-tomcat-8.5.100/webapps/ROOT/download/software_archive/release";
        String download_root = "https://prix.hanyang.ac.kr/download/software_archive/release";

        try {
            // 소프트웨어에 맞는 파일 검색
            File protDir = new File(swroot);  // 소프트웨어 파일 루트 디렉토리
            for (File file : protDir.listFiles()) {
                String fileLowerCase = file.getName().toLowerCase();
                if (fileLowerCase.startsWith(software.toLowerCase())) {
                    // 파일이 없는 경우 (isFile == false), 링크 메시지를 가져옴
                    String message = adminMapper.getMessageBySoftware(software.toLowerCase() + "_link");
                    String signature = adminMapper.getMessageBySoftware("signature");

                    // 링크 경로 생성
                    String linkPath = download_root + "/" + file.getName();
                    message = message.replace("<link>", linkPath);

                    // 메일 전송 (파일 없이 링크만 포함)
                    mailer.sendEmailToUser(name, email, software, message, signature, null);

                    break;  // 파일을 찾았으므로 루프 종료
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        mailer.sendEmailToUser(name, email, software, sendMsg, sig, path);
        adminMapper.updateRequestState(id, 1); // 1 for accepted
        return "redirect:/admin/requestlog";
    }

    @GetMapping("/requestlog/delete")
    public String deleteRequest(
            @RequestParam(name = "id", required = false) Integer id) {


        adminMapper.deleteRequestById(id);

        return "redirect:/admin/requestlog";
    }




}
