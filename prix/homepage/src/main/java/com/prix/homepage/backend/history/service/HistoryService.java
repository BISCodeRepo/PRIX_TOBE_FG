package com.prix.homepage.backend.history.service;



import com.prix.homepage.backend.history.domain.SearchLog;
import com.prix.homepage.backend.history.mapper.HistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * History와 관련된 비즈니스 로직을 처리하는 서비스 클래스.
 * 이 클래스는 HistoryMapper와 상호작용하여 데이터베이스 작업을 수행합니다.
 */
@Service
public class HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    /**
     * 특정 사용자의 히스토리 레코드를 조회합니다.
     *
     * @param userId 히스토리 레코드를 조회할 사용자의 ID.
     * @return History 객체 리스트.
     */
    public List<SearchLog> getHistoryByUserId(int userId) {
        return historyMapper.findSearchLogsByUserId(userId);
    }
}
