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
        // 1. enzymeName이 비어있거나 파이프 문자가 포함된 경우
        if (enzymeName.isEmpty()) {
            return 1; // addState = 1
        }
        if (enzymeName.contains("|")) {
            return 5; // addState = 5
        }

        // 2. ntCleave 또는 ctCleave가 비어있거나, 문자가 아닌 문자가 포함된 경우
//        if (ntCleave.isEmpty() || ctCleave.isEmpty()) {
//            return 0; // addState = 0
//        }
        if (!ctCleave.chars().allMatch(Character::isLetter)) {
            return 3; // addState = 3
        }

        // 3. enzymeName이 이미 존재하는 경우
        if (enzymeMapper.isEnzymeNameExists(userId, enzymeName)) {
            return 4; // addState = 4
        }

        // 4. 새로운 엔자임 추가
        enzymeMapper.insertEnzyme(userId, enzymeName, ntCleave.toUpperCase(), ctCleave.toUpperCase());
        return 0; // 성공 시 addState = 0
    }

    public void deleteEnzyme(int userId, int enzymeId) {
        enzymeMapper.deleteEnzyme(userId, enzymeId);
    }
}
