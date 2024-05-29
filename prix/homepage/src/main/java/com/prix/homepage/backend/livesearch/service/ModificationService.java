package com.prix.homepage.backend.livesearch.service;

import com.prix.homepage.backend.livesearch.mapper.ModificationMapper;
import com.prix.homepage.backend.livesearch.pojo.Modification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
