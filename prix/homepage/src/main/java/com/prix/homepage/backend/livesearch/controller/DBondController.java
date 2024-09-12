package com.prix.homepage.backend.livesearch.controller;

import com.prix.homepage.backend.basic.utils.PrixDataWriter;
import com.prix.homepage.backend.livesearch.dto.UserSettingDto;
import com.prix.homepage.backend.livesearch.dto.dbond.DBondResultDto;
import com.prix.homepage.backend.livesearch.dto.dbond.ProcessRequestDto;
import com.prix.homepage.backend.livesearch.pojo.Modification;
import com.prix.homepage.backend.livesearch.pojo.dbond.Enzyme;
import com.prix.homepage.backend.livesearch.pojo.dbond.PxData;
import com.prix.homepage.backend.livesearch.service.ModificationService;
import com.prix.homepage.backend.livesearch.service.UserModificationService;
import com.prix.homepage.backend.livesearch.service.UserSettingService;
import com.prix.homepage.backend.livesearch.service.dbond.DBondResultService;
import com.prix.homepage.backend.livesearch.service.dbond.DBondService;
import com.prix.homepage.backend.livesearch.service.dbond.EnzymeService;
import com.prix.homepage.backend.account.argumentResolver.LoginUserId;
import com.prix.homepage.constants.DBond.ProteinInfo;
import com.prix.homepage.frontend.controller.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.prix.homepage.constants.prixConst.anony;

/**
 * https://prix.hanyang.ac.kr/livesearch의 DBond
 **/
@RequiredArgsConstructor
@Slf4j
@Controller
public class DBondController extends BaseController {

    private final DBondService dBondService;
    private final UserSettingService userSettingService;
    private final ModificationService modificationService;
    private final UserModificationService userModificationService;
    private final DBondResultService dBondResultService;
    private final EnzymeService enzymeService;

    /**
     * livesearch 탭에서 DBond를 클릭했을때
     * @return String view : "livesearch/dbond_search"
     **/
    @GetMapping("dbond/dbond_search")
    public String dbondForm(@LoginUserId Integer id,HttpSession session, Model model,
                            @RequestParam(required = false) String entry,
                            @RequestParam(required = false) String ms,
                            @RequestParam(required = false) String db,
                            @RequestParam(required = false) String msfile,
                            @RequestParam(required = false) String dbfile,
                            @RequestParam(required = false) String mstype,
                            @RequestParam(required = false) String inst,
                            @RequestParam(required = false) String[] protein_list) {


        log.info("Received dbondForm request with parameters: id={}, entry={}, ms={}, db={}, msfile={}, dbfile={}, mstype={}, inst={}, protein_list={}",
                id, entry, ms, db, msfile, dbfile, mstype, inst, protein_list);

        String userName;
        if (id == 4) userName = "annoymous";
        else userName = userSettingService.findAccountNameById(id);

        model.addAttribute("userName",userName);
        model.addAttribute("id",id);
        UserSettingDto userSetting = userSettingService.findUserSettingById(id);

        model.addAttribute("userSetting", userSetting);
        model.addAttribute("databaseList", dBondService.getDatabaseList());

        if (anony.equals(id) && entry == null) {
            userModificationService.deleteModificationsForAnonymousUser(id);
        }

        if (mstype != null) userSetting.setDataFormat(mstype.toLowerCase());
        if (inst != null) userSetting.setInstrument(inst.toUpperCase());
        model.addAttribute("userSetting", userSetting);

        int dbNewIndex = -1;

        if (ms != null) {
            String fasta = "";
            PxData pxData = dBondService.getDataById(Integer.parseInt(db));
            log.info("pxData = {}",pxData);
            if (pxData != null) {
                String pwd = pxData.getName();
                File file = new File(pwd);
                try (InputStream is = new FileInputStream(file);
                     InputStreamReader reader = new InputStreamReader(is);
                     BufferedReader bReader = new BufferedReader(reader)) {

                    String line;
                    boolean include = false;
                    while ((line = bReader.readLine()) != null) {
                        if (line.length() > 0 && line.charAt(0) == '>') {
                            int last = line.indexOf(' ');
                            String name = last < 0 ? line.substring(1) : line.substring(1, last);
                            include = Arrays.asList(protein_list).contains(name);
                            if (include) {
                                fasta += line + '\n';
                            }
                        } else if (include) {
                            fasta += line + '\n';
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fasta.length() > 0) {
                try {
                    dbNewIndex = PrixDataWriter.write("fasta", dbfile, new ByteArrayInputStream(fasta.getBytes()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // 모델에 hidden input 값 추가
        model.addAttribute("msIndex", ms);
        model.addAttribute("msFile", msfile);
        model.addAttribute("dbFile", dbfile);
        model.addAttribute("msType", mstype);
        model.addAttribute("inst", inst);
        model.addAttribute("dbNewIndex", dbNewIndex);

        return "livesearch/dbond_search";
    }

    /**
     * DBond에서 Fixed Modifications, variable Modifications 에서 추가 버튼을 클릭했을때
     * DBond/var_ptms_list에서 sorting관련 버튼을 눌렀을때
     * @return String view : "livesearch/var_ptms_list"
     **/
    @GetMapping("/dbond/var_ptms_list")
    public String varPtmsList(@LoginUserId Integer id,HttpSession session,
            @RequestParam(defaultValue = "1") Integer var,
            @RequestParam(defaultValue = "0") Integer engine,
            @RequestParam(required = false) String sort,
            Model model) {

        log.info("var_ptms_list id = {}",id);

        model.addAttribute("id",id);
        model.addAttribute("var", var);
        model.addAttribute("engine", engine);
        model.addAttribute("sort", sort != null ? sort : "");
        List<Modification> modifications = Optional.ofNullable(modificationService.findModByUserAndCond(id, var == 1, engine == 1, sort))
                .orElse(new ArrayList<>());
        model.addAttribute("modifications", modifications);

        return "livesearch/var_ptms_list";
    }

    /**
     * DBond/var_ptms_list에서 Remove from Search List버튼을 눌렀을때 삭제 처리
     * @return String view : "livesearch/var_ptms_list"
     **/
    @PostMapping("/dbond/var_ptms_list")
    public String varPtmsListPost(@LoginUserId Integer id,HttpSession session,
                              @RequestParam(defaultValue = "1") Integer var,
                              @RequestParam(defaultValue = "0") Integer engine,
                              @RequestParam(required = false) String sort,
                              @RequestParam(value = "mod", required = false) List<String> modValues,
                              Model model) {
        log.info("var_ptms_list Post id = {}",id);

        if (id != null && modValues != null) {
            List<Integer> modValuesList = modValues.stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            userModificationService.deleteModifications(id, engine, modValuesList);
            model.addAttribute("reloadParent", true);
            log.info("var_ptms_list Post :: delete");
        }

        model.addAttribute("id",id);
        model.addAttribute("var", var);
        model.addAttribute("engine", engine);
        model.addAttribute("sort", sort != null ? sort : "");
        List<Modification> modifications = Optional.ofNullable(modificationService.findModByUserAndCond(id, var == 1, engine == 1, sort))
                .orElse(new ArrayList<>());
        model.addAttribute("modifications", modifications);

        return "livesearch/var_ptms_list";
    }

    /**
     * DBond/var_ptms_list에서 Add From Unimod 버튼을 눌렀을때
     * DBond/unimod_ptms_list에서 정렬 관련 버튼을 눌렀을때
     * @return String view : "livesearch/unimod_ptms_list"
     **/
    @GetMapping("/dbond/unimod_ptms_list")
    public String unimodPtmsList(@LoginUserId Integer id,HttpSession session,
            @RequestParam(defaultValue = "1") Integer var,
            @RequestParam(defaultValue = "0") Integer engine,
            @RequestParam(defaultValue = "name asc") String sort,
            @RequestParam(defaultValue = "default") String filter,
            @RequestParam(value = "mod", required = false) List<String> modValues,
            Model model
    ){

        boolean finished = false;
        ModFinder modFinder = new ModFinder(modValues);

        model.addAttribute("finished", finished);
        model.addAttribute("id", id);
        model.addAttribute("var", var);
        model.addAttribute("engine", engine);
        model.addAttribute("sortBy", sort);
        model.addAttribute("filter", filter);
        model.addAttribute("modFinder", modFinder);

        List<Modification> modifications = modificationService.findModifications(var==1,engine==1,sort,filter,id);

        log.info("필터 = {}",filter);
        model.addAttribute("modifications", modifications);

        return "livesearch/unimod_ptms_list";
    }

    /**
     * DBond/unimod_ptms_list에서 Add to Search List버튼을 눌렀을 때 Insert 처리
     * @return String view : "livesearch/unimod_ptms_list"
     **/
    @PostMapping("/dbond/unimod_ptms_list")
    public String unimodPtmsListPost(@LoginUserId Integer id,HttpSession session,
         @RequestParam(defaultValue = "1") Integer var,
         @RequestParam(defaultValue = "0") Integer engine,
         @RequestParam(defaultValue = "name asc") String sort,
         @RequestParam(defaultValue = "default") String filter,
         @RequestParam(value = "mod", required = false) List<String> modValues,
         @RequestParam(required = false) String submit,
         Model model
    ){

        boolean finished = false;
        ModFinder modFinder = new ModFinder(modValues);
        log.info("필터 = {}",filter);

        if (id != null && modValues != null && submit != null) {
            List<Integer> modValuesList = modValues.stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            boolean variable = var == 1;
            boolean engineFlag = engine == 1;
            userModificationService.insertModifications(id, modValuesList, variable, engineFlag);
            finished = true;
            log.info("Insert success!");
        }
        log.info("unimod_ptms_list id = {}",id);

        model.addAttribute("finished", finished);
        model.addAttribute("id", id);
        model.addAttribute("var", var);
        model.addAttribute("engine", engine);
        model.addAttribute("sortBy", sort);
        model.addAttribute("filter", filter);
        model.addAttribute("modFinder", modFinder);

        List<Modification> modifications = modificationService.findModifications(var==1,engine==1,sort,filter,id);

        model.addAttribute("modifications", modifications);

        return "livesearch/unimod_ptms_list";
    }

    class ModFinder {
        public ModFinder(List<String> values)
        {
            modValues = values;
        }
        public boolean findMod(String mod)
        {
            if (modValues == null)
                return false;
            for (int i = 0; i < modValues.size(); i++)
                if (mod.compareTo(modValues.get(i)) == 0)
                    return true;
            return false;
        }
        private List<String> modValues;
    }

    /**
     * DBond/var_ptms_list에서 Add from User List버튼을 눌렀을 때
     * @return String view : "livesearch/user_ptms_list"
     **/
    @GetMapping("/dbond/user_ptms_list")
    public String getUserModifications(@LoginUserId Integer id,
                                       HttpSession session, Model model,
                                       @RequestParam(defaultValue = "1") Integer var,
                                       @RequestParam(defaultValue = "0") Integer engine) {

        List<Modification> modifications = modificationService.getUserModifications(id, var, engine);
        model.addAttribute("modifications", modifications);
        model.addAttribute("var", var);
        model.addAttribute("engine", engine);
        model.addAttribute("sites", modificationService.getSiteOptions(var));
        model.addAttribute("positions", modificationService.getPositionOptions(var));

        return "livesearch/user_ptms_list";
    }

    /**
     * DBond/user_ptms_list에서 add 버튼 눌렀을 때 Insert 처리
     * @return Redirect String view : "livesearch/user_ptms_list"
     **/
    @PostMapping("/addModification")
    public String addModification(@LoginUserId Integer id,HttpSession session,
                                  @RequestParam String name,
                                  @RequestParam String mass,
                                  @RequestParam String residue,
                                  @RequestParam String position,
                                  @RequestParam(defaultValue = "1") Integer var,
                                  @RequestParam(defaultValue = "0") Integer engine,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        int addState = modificationService.addModification(id, name, mass, residue, position);
        redirectAttributes.addFlashAttribute("addState", addState);
        return "redirect:/dbond/user_ptms_list?var=" + var + "&engine=" + engine;
    }

    /**
     * DBond/user_ptms_list에서 del 버튼 눌렀을 때 Delete 처리
     * @return Redirect String view : "livesearch/user_ptms_list"
     **/
    @PostMapping("/deleteModification")
    public String deleteModification(HttpSession session,
                                     @RequestParam Integer id,
                                     @RequestParam(defaultValue = "1") Integer var,
                                     @RequestParam(defaultValue = "0") Integer engine) {

        modificationService.deleteModification(id);
        return "redirect:/dbond/user_ptms_list?var=" + var + "&engine=" + engine;
    }

    /**
     * DBond/user_ptms_list에서 Add to Search List 버튼 눌렀을 때 Insert 처리
     * @return Redirect String view : "livesearch/user_ptms_list"
     **/
    @PostMapping("/submitModifications")
    public String submitModifications(@LoginUserId Integer id,HttpSession session,
                                      @RequestParam("mod") List<Integer> modValues,
                                      @RequestParam(defaultValue = "1") Integer var,
                                      @RequestParam(defaultValue = "0") Integer engine) {

        userModificationService.insertModifications(id, modValues, var==1, engine==1);
        return "redirect:/dbond/user_ptms_list?var=" + var + "&engine=" + engine;
    }

    /**
     * DBond에서 Enzyme 추가 버튼 눌렀을 때
     * @return String view : "livesearch/enzyme_list"
     **/
    @GetMapping("dbond/enzyme_list")
    public String getEnzymeList(@LoginUserId Integer id,HttpSession session, Model model) {

        List<Enzyme> enzymes = enzymeService.getEnzymesByUserId(id);
        for(Enzyme enzyme : enzymes){
            log.info("enzyme = {}",enzyme);
        }
        model.addAttribute("enzymes", enzymes);
        model.addAttribute("id", id);

        return "livesearch/enzyme_list";
    }

    /**
     * DBond/enzyme_list에서 add 버튼 눌렀을 때 Insert 처리
     * @return Redirect String view : "livesearch/enzyme_list"
     **/
    @PostMapping("/enzyme/add")
    public String addEnzyme(@LoginUserId Integer id, HttpSession session, @RequestParam String enzyme_name,
                            @RequestParam String nt_cleave, @RequestParam String ct_cleave,
                            Model model, RedirectAttributes redirectAttributes) {

        int addState = enzymeService.addEnzyme(id, enzyme_name, nt_cleave, ct_cleave);
        redirectAttributes.addFlashAttribute("addState", addState);

        return "redirect:/dbond/enzyme_list";
    }

    /**
     * DBond/enzyme_list에서 del 버튼 눌렀을 때 Delete 처리
     * @return Redirect String view : "livesearch/enzyme_list"
     **/
    @PostMapping("/enzyme/delete")
    public String deleteEnzyme(@LoginUserId Integer id,HttpSession session, @RequestParam("enzyme_id") int enzymeId) {
        log.info("enzymeId = {}",enzymeId);
        enzymeService.deleteEnzyme(id, enzymeId);

        return "redirect:/dbond/enzyme_list";
    }

    /**
     * DBond에서 Enzyme 드롭다운 눌렀을 때 User의 Enzyme List를 요청할때
     * @return String enzymeList
     **/
    @GetMapping("/user_enzyme")
    @ResponseBody
    public String getUserEnzymes(@LoginUserId Integer id, HttpSession session) {
        log.info("Get user_enzyme");
        List<Enzyme> adminEnzymes = dBondService.getUserEnzymes(0);
        List<Enzyme> userEnzymes = dBondService.getUserEnzymes(id);
        List<Enzyme> enzymes = new ArrayList<>(adminEnzymes);

        enzymes.addAll(userEnzymes);
        return enzymes.stream()
                .map(e -> e.getName() + ":" + e.getId())
                .collect(Collectors.joining(","));
    }

    /**
     * DBond에서 process에서 진행사항을 알기위해 timer에 의해 3초마다 새로고침 요청할때
     * @return String address
     **/
    @GetMapping("/dbond/process")
    public String processData2(@LoginUserId Integer id,
                              HttpServletRequest request,
                              Model model) {

        if(id == 4) return "redirect:/login/user?url=modi/search";

        Map<String, String> paramsMap = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (value.length > 0) {
                paramsMap.put(key, value[0]);
            }
        });
//
        log.info("ParamsMap Contents: ");
        paramsMap.forEach((key, value) -> {
            log.info(key + " : " + value);
        });

        ProcessRequestDto dto = dBondService.processFilesAndData(id, paramsMap, new MultipartFile[]{});

        log.info("Dto = {}",dto.toString());

        model.addAttribute("process",dto);

        return dto.getAddress();
    }


    /**
     * DBond에서 submit버튼을 처음누를때 process창으로 이동
     * @return String adress
     **/
    @PostMapping("/dbond/process")
    public String processData(@LoginUserId Integer id,
                              HttpServletRequest request,
                              @RequestParam("ms_file") MultipartFile msFile,
                              @RequestParam("fasta") MultipartFile fasta,
                              Model model) {

        if(id == 4) return "redirect:/login/user?url=modi/search";

        Map<String, String> paramsMap = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (value.length > 0) {
                paramsMap.put(key, value[0]);
            }
        });

//        log.info("ParamsMap Contents: ");
//        paramsMap.forEach((key, value) -> {
//            log.info(key + " : " + value);
//        });
//
//        log.info("hasMsFile = {}",!msFile.isEmpty());
//        log.info("hasFasta = {}",!fasta.isEmpty());

        ProcessRequestDto dto = dBondService.processFilesAndData(id, paramsMap, new MultipartFile[]{msFile, fasta});

        log.info("Dto = {}",dto.toString());

        model.addAttribute("process",dto);

        return dto.getAddress();
    }

    /**
     * DBond에서 process가 끝나고 결과창을 처음 보여줄때
     * @return String "livesearch/result"
     **/
    @GetMapping("/dbond/result")
    public String getDBondResultPage(@LoginUserId Integer id,Model model, HttpServletRequest request) {
        DBondResultDto result = dBondResultService.getResult(id, request);
        log.info("summary ={}",result.getSummary());
        log.info("result ={}",result);
        model.addAttribute("result", result);

        return "livesearch/result";
    }

    /**
     * DBond에서 결과창에서 View_As등을 눌러서 다시 렌더링할때
     * @return String "livesearch/result"
     **/
    @PostMapping("/dbond/result")
    public String postDBondResultPage(@LoginUserId Integer id,Model model, HttpServletRequest request) {
        DBondResultDto result = dBondResultService.getResult(id, request);
        log.info("summary ={}",result.getSummary());
        log.info("result ={}",result);
        model.addAttribute("result", result);

        return "livesearch/result";
    }

}


