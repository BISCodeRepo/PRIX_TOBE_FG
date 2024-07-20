package com.prix.homepage.backend.livesearch.service;

import com.prix.homepage.backend.livesearch.mapper.ModificationMapper;
import com.prix.homepage.backend.livesearch.pojo.Modification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class ModificationService {

    private final ModificationMapper modificationMapper;

    public List<Modification> findModByUserAndCond(Integer userId, Boolean variable, Boolean engine, String sortBy){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("variable", variable);
        params.put("engine", engine);
        params.put("sortBy", sortBy);
        return modificationMapper.findModByUserAndCond(params);
    }

    public List<Modification> findModifications(Boolean var,Boolean engine,String sortBy,String filter,Integer userId){
        return modificationMapper.findModifications(var,engine,sortBy,filter,userId);
    }

    public List<Modification> getUserModifications(Integer userId, Integer var, Integer engine) {
        return modificationMapper.findUserModifications(userId, var, engine);
    }

    public List<String> getSiteOptions(Integer var) {
        if (var == 1) {
            return Arrays.asList("N-term", "C-term", "A", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "Y");
        } else {
            return Arrays.asList("A", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "Y");
        }
    }

    public List<String> getPositionOptions(Integer var) {
        if (var == 1) {
            return Arrays.asList("ANYWHERE", "ANY_N_TERM", "ANY_C_TERM", "PROTEIN_N_TERM", "PROTEIN_C_TERM");
        } else {
            return Arrays.asList("ANYWHERE");
        }
    }
}
