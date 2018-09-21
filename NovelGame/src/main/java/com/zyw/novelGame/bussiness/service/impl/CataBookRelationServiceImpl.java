package com.zyw.novelGame.bussiness.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyw.novelGame.bussiness.service.CataBookRelationService;
import com.zyw.novelGame.mapper.CataBookRelationMapper;
import com.zyw.novelGame.model.CataBookRelation;

@Service(value="cataBookRelationService")
public class CataBookRelationServiceImpl implements CataBookRelationService{
	public static final  Logger logger=LoggerFactory.getLogger(CataBookRelationServiceImpl.class);
	
	@Autowired
	private CataBookRelationMapper cataBookRelationMapper;

	@Override
	@Transactional
	public int insert(CataBookRelation record) {
		return cataBookRelationMapper.insert(record);
	}

}
