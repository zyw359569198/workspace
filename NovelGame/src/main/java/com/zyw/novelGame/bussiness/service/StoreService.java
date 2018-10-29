package com.zyw.novelGame.bussiness.service;

import java.util.HashMap;
import java.util.List;

import com.zyw.novelGame.model.BookData;
import com.zyw.novelGame.model.Store;
import com.zyw.novelGame.model.StoreData;

public interface StoreService {
	
	List<HashMap> queryBookStore(String bookNameEn,String storeId);
	
	int insert(Store record);
	
	int insertStoreData(StoreData storeData);
	
	List<BookData>  queryBookStoreData(String storeId);
	
	int queryStoreCount(Store record);
	
	List<Store> queryLastStoreIdByBookId(String bookId);
	
	int updateByStoreIdBySelective(Store record);
	
	List<HashMap> queryBookStoreAll(List<String> ids);


}
