package com.zyw.novelGame.bussiness.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xerial.snappy.Snappy;

import com.zyw.novelGame.bussiness.service.StoreService;
import com.zyw.novelGame.mapper.StoreDataMapper;
import com.zyw.novelGame.mapper.StoreMapper;
import com.zyw.novelGame.model.BookData;
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
		try {
			storeData.setStoreContent(Snappy.compress(storeData.getStoreContent()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return storeDataMapper.insert(storeData);
	}

	@Override
	public List<BookData> queryBookStoreData(String storeId) {
		List<BookData> list=storeMapper.queryBookStoreData(storeId);
		list.stream().map(o->{
			try {
				o.setStoreContent(Snappy.uncompress(o.getStoreContent()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return o;
			
		}).collect(Collectors.toList());
		return list;
	}

	@Override
	public int queryStoreCount(Store record) {
		return storeMapper.queryStoreCount(record);
	}

	@Override
	public List<Store> queryLastStoreIdByBookId(String bookId) {
		return storeMapper.queryLastStoreIdByBookId(bookId);
	}
	
	@Override
	public int updateByStoreIdBySelective(Store record) {
		return storeMapper.updateByStoreIdBySelective(record);
	}

	@Override
	public List<HashMap> queryBookStoreAll(List<String> ids) {
		return storeMapper.queryBookStoreAll(ids);
	}

}
