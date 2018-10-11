package com.zyw.novelGame.mapper;

import java.util.List;

import com.zyw.novelGame.model.SearchInfo;

public interface SearchInfoMapper {
    int insert(SearchInfo record);

    int insertSelective(SearchInfo record);
    
    List<SearchInfo> querySearchInfo(SearchInfo record);
    
    int updateById(SearchInfo record);
}