package com.prix.homepage.backend.livesearch.service.dbond;

import com.prix.homepage.backend.livesearch.mapper.dbond.DBondMapper;
import com.prix.homepage.backend.livesearch.pojo.dbond.Database;
import com.prix.homepage.backend.livesearch.pojo.dbond.Enzyme;
import com.prix.homepage.backend.livesearch.pojo.dbond.PxData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class DBondService {

    private final DBondMapper dBondMapper;

    public List<Enzyme> getUserEnzymes(String userId) {
        return dBondMapper.findByUserId(userId);
    }
    public List<Database> getDatabaseList() {
        return dBondMapper.findAll();
    }

    public PxData getDataById(int id) {
        return dBondMapper.findDataById(id);
    }
}
