package com.prix.homepage.backend.livesearch.service.patternmatch;

import com.prix.homepage.backend.livesearch.mapper.UpdateTableMapper;
import com.prix.homepage.backend.livesearch.pojo.UpdateTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatternMatchService {

    @Autowired
    private UpdateTableMapper updateTableMapper;

    public UpdateTable getUpdateTable(String dbType) {
        String dbname = "1".equals(dbType) ? "swiss_prot" : "genbank";
        return updateTableMapper.selectByDbName(dbname);
    }

//    public String[] getPatterns(String... patterns) {
//        return patterns;
//    }



}
