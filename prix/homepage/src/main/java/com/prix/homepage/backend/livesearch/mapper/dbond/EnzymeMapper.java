package com.prix.homepage.backend.livesearch.mapper.dbond;

import com.prix.homepage.backend.livesearch.pojo.dbond.Enzyme;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EnzymeMapper {

    @Select("SELECT * FROM px_enzyme WHERE user_id = #{userId}")
    List<Enzyme> getEnzymesByUserId(@Param("userId") int userId);

    @Select("SELECT COUNT(*) FROM px_enzyme WHERE (user_id = 0 OR user_id = #{userId}) AND name = #{enzymeName}")
    boolean isEnzymeNameExists(@Param("userId") int userId, @Param("enzymeName") String enzymeName);

    @Insert("INSERT INTO px_enzyme (user_id, name, nt_cleave, ct_cleave) VALUES (#{userId}, #{enzymeName}, #{ntCleave}, #{ctCleave})")
    void insertEnzyme(@Param("userId") int userId, @Param("enzymeName") String enzymeName, @Param("ntCleave") String ntCleave, @Param("ctCleave") String ctCleave);

    @Delete("DELETE FROM px_enzyme WHERE id = #{enzymeId} AND user_id = #{userId}")
    void deleteEnzyme(@Param("userId") int userId, @Param("enzymeId") int enzymeId);

    @Select("SELECT name, nt_cleave AS ntCleave, ct_cleave AS ctCleave FROM px_enzyme WHERE id = #{enzymeId}")
    Enzyme getEnzymeById(@Param("enzymeId") int enzymeId);
}
