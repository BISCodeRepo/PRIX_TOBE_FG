package com.prix.homepage.backend.livesearch.service.dbond;

import com.prix.homepage.backend.livesearch.mapper.dbond.EnzymeMapper;
import com.prix.homepage.backend.livesearch.pojo.dbond.Enzyme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnzymeService {

    @Autowired
    private EnzymeMapper enzymeMapper;

    public List<Enzyme> getEnzymesByUserId(int userId) {
        return enzymeMapper.getEnzymesByUserId(userId);
    }

    public int addEnzyme(int userId, String enzymeName, String ntCleave, String ctCleave) {
        if (enzymeName.isEmpty() || enzymeName.contains("|")) {
            return 1;
        }

        if (ntCleave.isEmpty() || ctCleave.isEmpty() || !ntCleave.chars().allMatch(Character::isLetter)) {
            return 2;
        }

        if (enzymeMapper.isEnzymeNameExists(userId, enzymeName)) {
            return 3;
        }

        enzymeMapper.insertEnzyme(userId, enzymeName, ntCleave.toUpperCase(), ctCleave.toUpperCase());
        return 0;
    }

    public void deleteEnzyme(int userId, int enzymeId) {
        enzymeMapper.deleteEnzyme(userId, enzymeId);
    }
}
