package com.prix.homepage.backend.history.mapper;


import com.prix.homepage.backend.admin.entity.SearchLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HistoryMapper {

    /**
     * 특정 사용자의 히스토리 레코드를 조회합니다.
     *
     * @param id 히스토리 레코드를 조회할 사용자의 ID.
     * @return History 객체 리스트.
     */
    @Select("SELECT l.title, l.date, " +
            "COALESCE(msData.name, '') as msFile, " +
            "COALESCE(dbData.name, '') as dbFile, " +
            "l.result, l.engine, " +
            "l.id " +
            "FROM px_search_log l " +
            "LEFT JOIN px_data msData ON l.msfile = msData.id " +
            "LEFT JOIN px_data dbData ON l.db = dbData.id " +
            "WHERE user_id = #{id} ORDER BY date DESC, id DESC LIMIT 50")
    List<SearchLog> findSearchLogs(@Param("id") int id);
}