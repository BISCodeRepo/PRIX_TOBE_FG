package com.prix.homepage.backend.history.mapper;


import com.prix.homepage.backend.history.domain.SearchLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HistoryMapper {

    /**
     * 특정 사용자의 히스토리 레코드를 조회합니다.
     *
     * @param userId 히스토리 레코드를 조회할 사용자의 ID.
     * @return History 객체 리스트.
     */
    @Select("SELECT title, date, msfile, db, result, engine FROM px_search_log WHERE user_id = #{userId} ORDER BY date DESC, id DESC LIMIT 50")
    List<SearchLog> findSearchLogsByUserId(int userId);
}