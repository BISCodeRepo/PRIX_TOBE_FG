package com.prix.homepage.backend.livesearch.mapper.PatternMatch;

import com.prix.homepage.backend.livesearch.pojo.PatternMatch.SwissProtData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface SwissProtMapper {

    @Select({
            "<script>",
            "SELECT ac_to_species.primary_ac, description, species, sequence",
            "FROM ac_to_species, sequence_data, extra_information",
            "WHERE ac_to_species.primary_ac = sequence_data.primary_ac",
            "AND ac_to_species.primary_ac = extra_information.primary_ac",
            "<if test='pattern != null and pattern.length > 0'>",
            "<if test='checkOrder'>",
            "AND (sequence RLIKE CONCAT(",
            "<foreach collection='pattern' item='item' separator=', \".*\",'>",
            "#{item}",
            "</foreach>",
            "))",
            "</if>",
            "<if test='!checkOrder'>",
            "<foreach collection='pattern' item='item' open='AND (' separator=' OR ' close=')'>",
            "sequence RLIKE #{item}",
            "</foreach>",
            "</if>",
            "</if>",
            "AND species LIKE CONCAT(#{species}, '%')",
            "</script>"
    })
    ArrayList<SwissProtData> findSwissProtSequence(@Param("species") String species, @Param("pattern") String[] pattern, @Param("checkOrder") Boolean checkOrder);

    @Select({
            "<script>",
            "SELECT ac_to_species.primary_ac, description, species, sequence",
            "FROM ac_to_species, sequence_data, extra_information",
            "WHERE ac_to_species.primary_ac = sequence_data.primary_ac",
            "AND ac_to_species.primary_ac = extra_information.primary_ac",
            "<if test='pattern != null and pattern.length > 0'>",
            "<if test='checkOrder'>",
            "AND (sequence RLIKE CONCAT(",
            "<foreach collection='pattern' item='item' separator=', \".*\",'>",
            "#{item}",
            "</foreach>",
            "))",
            "</if>",
            "<if test='!checkOrder'>",
            "<foreach collection='pattern' item='item' open='AND (' separator=' OR ' close=')'>",
            "sequence RLIKE #{item}",
            "</foreach>",
            "</if>",
            "</if>",
            "</script>"
    })
    ArrayList<SwissProtData> findSwissProtSequenceWithoutSpecies(@Param("pattern") String[] pattern, @Param("checkOrder") Boolean checkOrder);



}
