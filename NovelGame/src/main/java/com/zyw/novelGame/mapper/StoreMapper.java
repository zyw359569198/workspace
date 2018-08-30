package com.zyw.novelGame.mapper;

import java.util.List;

import com.zyw.novelGame.model.Store;

public interface StoreMapper {
    int deleteByPrimaryKey(String id);

	int insert(Store record);

	int insertSelective(Store record);

	Store selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(Store record);

	int updateByPrimaryKeyWithBLOBs(Store record);

	int updateByPrimaryKey(Store record);
	
	List<Store> queryBookStore(Store store);
}