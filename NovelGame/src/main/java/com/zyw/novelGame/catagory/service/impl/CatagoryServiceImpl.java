package com.zyw.novelGame.catagory.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zyw.novelGame.catagory.service.CatagoryService;
import com.zyw.novelGame.mapper.CatagoryMapper;
import com.zyw.novelGame.model.Catagory;

@Service(value="catagoryService")
public class CatagoryServiceImpl implements CatagoryService{
	
	public static final  Logger logger=LoggerFactory.getLogger(CatagoryServiceImpl.class);
	
	@Autowired
	private CatagoryMapper catagoryMapper;

	public  List<Catagory>  queryCatagory() {
		return catagoryMapper.queryCatagory();
		
	}

}
