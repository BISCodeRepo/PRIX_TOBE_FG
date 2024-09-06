package com.prix.homepage.backend.livesearch.service.patternmatch;

import com.prix.homepage.backend.livesearch.mapper.PatternMatch.GenbankMapper;
import com.prix.homepage.backend.livesearch.mapper.PatternMatch.SwissProtMapper;
import com.prix.homepage.backend.livesearch.pojo.PatternMatch.GenBankData;
import com.prix.homepage.backend.livesearch.pojo.PatternMatch.SwissProtData;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.jdbc.SQL;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Service
@MapperScan(value = "com.prix.homepage.backend.livesearch.mapper.PatternMatch")
public class PatternMatchSQL {

    private final SqlSessionTemplate sqlSessionTemplate;

    public PatternMatchSQL(SqlSessionTemplate sqlSessionTemplate){
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    public void checkSqlSessionTemplate() {
        try {
            DataSource dataSource = this.sqlSessionTemplate.getConfiguration().getEnvironment().getDataSource();
            String url = dataSource.getConnection().getMetaData().getURL();
            System.out.println("Connected to database URL: " + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<SwissProtData> getSwissProtSequence(String species, String[] patterns, boolean checkOrder) {
        SwissProtMapper swissProtMapper = sqlSessionTemplate.getMapper(SwissProtMapper.class);
        return swissProtMapper.findSwissProtSequence(species, patterns, checkOrder);
    }

    public ArrayList<SwissProtData> getSwissProtSequenceWithoutSpecies(String[] patterns, boolean checkOrder) {
        SwissProtMapper swissProtMapper = sqlSessionTemplate.getMapper(SwissProtMapper.class);
        return swissProtMapper.findSwissProtSequenceWithoutSpecies(patterns, checkOrder);
    }
    public ArrayList<GenBankData> getGenbankSequence(String species, String[] patterns, boolean checkOrder) {
        GenbankMapper genbankMapper = sqlSessionTemplate.getMapper(GenbankMapper.class);
        return genbankMapper.findGenbankSequence(species, patterns, checkOrder);
    }

    public ArrayList<GenBankData> getGenbankSequenceWithoutSpecies(String[] patterns, boolean checkOrder) {
        GenbankMapper genbankMapper = sqlSessionTemplate.getMapper(GenbankMapper.class);
        return genbankMapper.findGenbankSequenceWithoutSpecies(patterns, checkOrder);
    }
}
