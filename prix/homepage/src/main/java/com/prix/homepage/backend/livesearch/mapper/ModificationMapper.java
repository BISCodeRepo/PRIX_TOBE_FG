package com.prix.homepage.backend.livesearch.mapper;

import com.prix.homepage.backend.livesearch.pojo.Modification;
import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ModificationMapper {

    @SelectProvider(type = ModificationSqlProvider.class, method = "findModByUserAndCond")
    List<Modification> findModByUserAndCond(Map<String, Object> params);
    @SelectProvider(type = ModificationSqlProvider.class, method = "findModifications")
    @Results({
            @Result(property = "stringClass", column = "class")
    })
    List<Modification> findModifications(@Param("var") Boolean var,
                                         @Param("engine") Boolean engine,
                                         @Param("sortBy") String sortBy,
                                         @Param("filter") String filter,
                                         @Param("userId") Integer userId);

    @SelectProvider(type = ModificationSqlProvider.class, method = "findUserModifications")
    List<Modification> findUserModifications(@Param("userId") Integer userId, @Param("var") Integer var, @Param("engine") Integer engine);


    @Select("SELECT name, mass_diff AS massDiff, residue, position FROM px_modification WHERE id IN (SELECT mod_id FROM px_user_modification WHERE user_id = #{userId} AND variable = #{variable} AND engine = #{engine})")
    List<Modification> getModificationsByUserAndEngine(@Param("userId") int userId, @Param("variable") int variable, @Param("engine") int engine);

    @Insert("INSERT INTO px_modification (user_id, name, mass_diff, residue, position) VALUES (#{userId}, #{name}, #{massDiff}, #{residue}, #{position})")
    int insertModification(@Param("userId") Integer userId, @Param("name") String name, @Param("massDiff") double massDiff, @Param("residue") String residue, @Param("position") String position);

    @Delete("DELETE FROM px_modification WHERE id = #{id}")
    void deleteModification(@Param("id") Integer id);

    @Select("SELECT mass_diff AS massDiff FROM px_modification WHERE name = #{name}")
    String getMassDiffByName(@Param("name") String name);
}