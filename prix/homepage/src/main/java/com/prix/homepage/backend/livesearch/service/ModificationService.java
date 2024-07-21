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

    public Integer addModification(Integer userId, String name, String mass, String residue, String position) {
        int addState = 0;

        // Check if name is empty
        if (name == null || name.trim().isEmpty()) {
            return 1; // Name is empty
        }

        // Check if name contains '|' or whitespace
        if (name.contains("|") || name.contains(" ")) {
            return 5; // Invalid character in name
        }

        // Check if mass is empty
        if (mass == null || mass.trim().isEmpty()) {
            return 2; // Mass is empty
        }

        try {
            // Try parsing the mass to a double
            double massValue = Double.parseDouble(mass);

            // Check if residue and position are consistent
            if ("N-term".equals(residue)) {
                if ("ANYWHERE".equals(position) || position.endsWith("C_TERM")) {
                    return 4; // Inconsistent site and position
                }
            } else if ("C-term".equals(residue)) {
                if ("ANYWHERE".equals(position) || position.endsWith("N_TERM")) {
                    return 4; // Inconsistent site and position
                }
            }
            log.info("insert success!");
            // If all checks pass, insert the modification
            modificationMapper.insertModification(userId, name, massValue, residue, position);
            return 0;

        } catch (NumberFormatException e) {
            return 3; // Mass difference should be a real number
        }
    }


    public void deleteModification(Integer id) {
        log.info("delete modification!");
        modificationMapper.deleteModification(id);
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
