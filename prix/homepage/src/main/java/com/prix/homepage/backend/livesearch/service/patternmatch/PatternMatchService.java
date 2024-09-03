package com.prix.homepage.backend.livesearch.service.patternmatch;

import com.prix.homepage.backend.livesearch.pojo.PatternMatch.GenBankData;
import com.prix.homepage.backend.livesearch.pojo.PatternMatch.SwissProtData;
import lombok.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.io.IOException;
import java.sql.SQLException;

@Service
public class PatternMatchService {


    private int[] findNumber = new int[2];
    private String dataBase;
    private String formatType;
    private String species;
    private String id;
    private String passwd;
    private String ip;
    private String[] inputPattern;
    private boolean checkSpecies;
    private boolean checkWithoutSq;
    private boolean checkOrder;
    private Make_html make_html= new Make_html();
    private Regex_Match match = new Regex_Match();
    private PatternMatchSQL db;
    private ArrayList<SwissProtData> swissProtDataList;
    private ArrayList<GenBankData> genBankDataList;

    private final SqlSessionTemplate swissProtSqlSessionTemplate;
    private final SqlSessionTemplate genbankSqlSessionTemplate;

    @Autowired
    public PatternMatchService(
            @Qualifier("swissProtSqlSessionTemplate") SqlSessionTemplate swissProtSqlSessionTemplate,
            @Qualifier("genbankSqlSessionTemplate") SqlSessionTemplate genbankSqlSessionTemplate
    ) {
        this.swissProtSqlSessionTemplate = swissProtSqlSessionTemplate;
        this.genbankSqlSessionTemplate = genbankSqlSessionTemplate;
    }

    private SqlSessionTemplate sqlSessionTemplate;


    public void setParameter(String formatType, String dataBase, String[] inputPattern, boolean checkSpecies, String species, boolean checkWithoutSq, boolean checkOrder) {
        this.formatType = formatType;
        this.dataBase = dataBase;
        this.checkSpecies = checkSpecies;
        this.species = species;
        this.inputPattern = inputPattern;
        this.checkWithoutSq = checkWithoutSq;
        this.checkOrder = checkOrder;

        }


    public void DBParameter(String id, String passwd, String ip) {
        this.id = id;
        this.passwd = passwd;
        this.ip = ip;
    }

    public void MainMethod() throws Exception {
        if (this.dataBase.equals("swiss_prot")){
            this.sqlSessionTemplate = this.swissProtSqlSessionTemplate;

        }else{
            this.sqlSessionTemplate = this.genbankSqlSessionTemplate;
        }


        this.db = new PatternMatchSQL(this.sqlSessionTemplate);

        this.db.checkSqlSessionTemplate();

        if (this.formatType.equals("1")) {
            this.inputPattern = Regex_Convert.PrositeToPerl(this.inputPattern);
        }


        for(int i=0; i<this.inputPattern.length; i++ ){
            System.out.println(this.inputPattern[i]);
        }

        if (this.checkSpecies) {
            if (this.dataBase.equals("swiss_prot")) {
                this.swissProtDataList = this.db.getSwissProtSequence(this.species, this.inputPattern, false);
            } else if (this.dataBase.equals("genbank")) {
                this.genBankDataList = this.db.getGenbankSequence(this.species, this.inputPattern, this.checkOrder);
            }
        } else if (this.dataBase.equals("swiss_prot")) {
            this.swissProtDataList = this.db.getSwissProtSequenceWithoutSpecies(this.inputPattern, this.checkOrder);
        } else if (this.dataBase.equals("genbank")) {
            this.genBankDataList = this.db.getGenbankSequenceWithoutSpecies(this.inputPattern, this.checkOrder);
        }

        System.out.println(this.swissProtDataList);
        System.out.println("여기까지");

    }

    public String getOneProtein() throws IOException, SQLException {
        StringBuilder htmlOutput = new StringBuilder();
        if (this.dataBase.equals("swiss_prot")) {
            for (SwissProtData data : this.swissProtDataList) {
                String sequence = data.getSequence();

                if (!this.checkWithoutSq)
                    sequence = this.match.Match(sequence, this.inputPattern);
                else
                    this.match.Count(sequence, this.inputPattern);

                String singleEntryHtml = make_html.getOneProtein(
                        this.dataBase,
                        data.getPrimary_ac(),
                        data.getDescription(),
                        data.getSpecies(),
                        sequence,
                        this.checkWithoutSq
                );
                htmlOutput.append(singleEntryHtml);
            }

            return htmlOutput.toString();
        } else if (this.dataBase.equals("genbank")) {
            for ( GenBankData data : this.genBankDataList ){
                String sequence = data.getSequence();

                if (!this.checkWithoutSq)
                    sequence = this.match.Match(sequence, this.inputPattern);
                else
                    this.match.Count(sequence, this.inputPattern);

                String singleEntryHtml = make_html.getOneProtein(
                        this.dataBase,
                        data.getGi_number(),
                        data.getDefinition(),
                        data.getSpecies(),
                        sequence,
                        this.checkWithoutSq
                );
                htmlOutput.append(singleEntryHtml);
            }
            return htmlOutput.toString();
        }
        else
            return null;
    }


        public String getParameter () throws IOException, SQLException {
            StringBuilder htmlOutput = new StringBuilder();
            this.findNumber = this.match.getFindNumber();

            htmlOutput.append(this.make_html.PrintParameter(this.inputPattern, this.findNumber, this.species, this.checkSpecies));
            return htmlOutput.toString();
        }



}
