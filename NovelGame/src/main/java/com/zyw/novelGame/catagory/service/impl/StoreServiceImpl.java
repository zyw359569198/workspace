package com.zyw.novelGame.catagory.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zyw.novelGame.catagory.service.StoreService;
import com.zyw.novelGame.mapper.StoreMapper;
import com.zyw.novelGame.model.Store;

@Service(value="storeService")
public class StoreServiceImpl implements StoreService{
	
public static final  Logger logger=LoggerFactory.getLogger(StoreServiceImpl.class);
	
	@Autowired
	private StoreMapper storeMapper;

	@Override
	public List<Store> queryBookStore(Store store) {
		return storeMapper.queryBookStore(store);
	}

	@Override
	public int insert(Store record) {
		return storeMapper.insert(record);
	}

}
