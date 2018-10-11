package com.zyw.novelGame.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zyw.novelGame.model.BookData;
import com.zyw.novelGame.model.Store;

public interface StoreMapper {
    int deleteByPrimaryKey(String id);

	int insert(Store record);

	int insertSelective(Store record);

	Store selectByPrimaryKey(String id);

	int updateByStoreIdBySelective(Store record);

	int updateByPrimaryKeyWithBLOBs(Store record);

	int updateByPrimaryKey(Store record);
	
	List<HashMap> queryBookStore(@Param("bookNameEn")String bookNameEn,@Param("storeId")String storeId);
	
	List<BookData> queryBookStoreData(@Param("storeId")String storeId);
	
	int queryStoreCount(Store record);
	
	List<Store> queryLastStoreIdByBookId(@Param("bookId")String bookId);
	
	List<HashMap> queryBookStoreAll(List<String> ids);
}