package com.prix.homepage.backend.livesearch.mapper;

import org.apache.ibatis.jdbc.SQL;
import java.util.Map;

public class ModificationSqlProvider {
    
    public String findModByUserAndCond(final Map<String, Object> params) {
        return new SQL() {{
            SELECT("id, name, mass_diff, residue, position");
            FROM("px_modification");
            WHERE("id IN (SELECT mod_id FROM px_user_modification WHERE user_id = #{userId} AND variable = #{variable} AND engine = #{engine})");
            if (params.get("sortBy") != null && !params.get("sortBy").toString().isEmpty()) {
                ORDER_BY("${sortBy}");
            }
        }}.toString();
    }
    public String findModifications(Map<String, Object> params) {
        Boolean var = (Boolean) params.get("var");
        Boolean engine = (Boolean) params.get("engine");
        String sortBy = (String) params.get("sortBy");
        String filter = (String) params.get("filter");
        Integer userId = (Integer) params.get("userId");

        return new SQL() {{
            SELECT("c.class, m.id, m.name, m.mass_diff, m.residue, m.position");
            FROM("px_modification m");
            JOIN("px_classification c ON c.id = m.class");
            WHERE("m.user_id = 0");
            WHERE("m.id NOT IN (SELECT mod_id FROM px_user_modification WHERE user_id = #{userId} AND engine = #{engine})");

            if (filter != null && "default".equals(filter)) {
                if (Boolean.TRUE.equals(var)) {
                    WHERE("m.class IN (2, 3, 4, 5, 6, 13, 14)");
                } else {
                    WHERE("m.class IN (2, 3, 4, 5, 6, 11, 13, 14)");
                }
            }

            if (Boolean.FALSE.equals(var)) {
                if (Boolean.TRUE.equals(engine)) {
                    WHERE("position = 'ANYWHERE'");
                } else {
                    WHERE("(position = 'ANYWHERE' OR (position = 'ANY_N_TERM' AND residue = 'N-term') OR (position = 'ANY_C_TERM' AND residue = 'C-term'))");
                }
            }

            if (sortBy != null && !sortBy.isEmpty()) {
                ORDER_BY((sortBy.startsWith("class") ? "c." : "m.") + sortBy);
            }
        }}.toString();
    }
}
