package com.zyw.novelGame.bussiness.service.impl;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zyw.novelGame.bussiness.service.StoreService;
import com.zyw.novelGame.mapper.StoreDataMapper;
import com.zyw.novelGame.mapper.StoreMapper;
import com.zyw.novelGame.model.Store;
import com.zyw.novelGame.model.StoreData;

@Service(value="storeService")
public class StoreServiceImpl implements StoreService{
	
public static final  Logger logger=LoggerFactory.getLogger(StoreServiceImpl.class);
	
	@Autowired
	private StoreMapper storeMapper;
	
	@Autowired
	private StoreDataMapper storeDataMapper;

	@Override
	public List<HashMap> queryBookStore(String bookNameEn,String storeId) {
		return storeMapper.queryBookStore(bookNameEn,storeId);
	}

	@Override
	public int insert(Store record) {
		return storeMapper.insert(record);
	}

	@Override
	public int insertStoreData(StoreData storeData) {
		return storeDataMapper.insert(storeData);
	}

	@Override
	public List<HashMap> queryBookStoreData(String storeId) {
		return storeMapper.queryBookStoreData(storeId);
	}

}
