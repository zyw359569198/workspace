package com.zyw.novelGame.catagory.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zyw.novelGame.catagory.service.CataBookRelationService;
import com.zyw.novelGame.mapper.CataBookRelationMapper;
import com.zyw.novelGame.model.CataBookRelation;

@Service(value="cataBookRelationService")
public class CataBookRelationServiceImpl implements CataBookRelationService{
	public static final  Logger logger=LoggerFactory.getLogger(CataBookRelationServiceImpl.class);
	
	@Autowired
	private CataBookRelationMapper cataBookRelationMapper;

	@Override
	public int insert(CataBookRelation record) {
		return cataBookRelationMapper.insert(record);
	}

}
