package com.zyw.novelGame.catagory.service;

import java.util.List;

import com.zyw.novelGame.model.Store;
import com.zyw.novelGame.model.StoreData;

public interface StoreService {
	
	List<Store> queryBookStore(Store store);
	
	int insert(Store record);
	
	int insertStoreData(StoreData storeData);

}
