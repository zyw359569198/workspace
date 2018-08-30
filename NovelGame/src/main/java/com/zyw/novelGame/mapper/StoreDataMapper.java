package com.zyw.novelGame.mapper;

import com.zyw.novelGame.model.StoreData;

public interface StoreDataMapper {
    int deleteByPrimaryKey(String id);

    int insert(StoreData record);

    int insertSelective(StoreData record);

    StoreData selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StoreData record);

    int updateByPrimaryKeyWithBLOBs(StoreData record);

    int updateByPrimaryKey(StoreData record);
}