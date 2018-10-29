package com.zyw.novelGame.bussiness.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyw.novelGame.bussiness.service.CatagoryService;
import com.zyw.novelGame.mapper.CatagoryMapper;
import com.zyw.novelGame.model.Catagory;

@Service(value="catagoryService")
public class CatagoryServiceImpl implements CatagoryService{
	
	public static final  Logger logger=LoggerFactory.getLogger(CatagoryServiceImpl.class);
	
	@Autowired
	private CatagoryMapper catagoryMapper;

	@Override
	public  List<Catagory>  queryCatagory(Catagory record) {
		return catagoryMapper.queryCatagory(record);
		
	}

	@Override
	@Transactional
	public int insert(Catagory record) {
		return catagoryMapper.insert(record);
	}

}
