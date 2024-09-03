package com.prix.homepage.backend.livesearch.mapper.PatternMatch;

import com.prix.homepage.backend.livesearch.pojo.PatternMatch.GenBankData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface GenbankMapper {
    @Select({
            "<script>",
            "SELECT gi_number, definition, species, sequence",
            "FROM general_info",
            "WHERE species = #{species}",
            "<if test='pattern != null and pattern.length > 0'>",
            "<if test='checkOrder'>",
            "AND sequence RLIKE CONCAT(",
            "<foreach collection='pattern' item='item' separator=', \".*\",'>",
            "#{item}",
            "</foreach>",
            ")",
            "</if>",
            "<if test='!checkOrder'>",
            "AND (",
            "<foreach collection='pattern' item='item' separator=' OR '>",
            "sequence RLIKE #{item}",
            "</foreach>",
            ")",
            "</if>",
            "</if>",
            "</script>"
    })
    ArrayList<GenBankData> findGenbankSequence(@Param("species") String species, @Param("pattern") String[] pattern, @Param("checkOrder") Boolean checkOrder);

    @Select({
            "<script>",
            "SELECT gi_number, definition, species, sequence",
            "FROM general_info",
            "<if test='pattern != null and pattern.length > 0'>",
            "<if test='checkOrder'>",
            "WHERE sequence RLIKE CONCAT(",
            "<foreach collection='pattern' item='item' separator=', \".*\",'>",
            "#{item}",
            "</foreach>",
            ")",
            "</if>",
            "<if test='!checkOrder'>",
            "WHERE ",
            "<foreach collection='pattern' item='item' separator=' OR '>",
            "sequence RLIKE #{item}",
            "</foreach>",
            "</if>",
            "</if>",
            "</script>"
    })
    ArrayList<GenBankData> findGenbankSequenceWithoutSpecies(@Param("pattern") String[] pattern, @Param("checkOrder") Boolean checkOrder);
}


