package com.zyw.novelGame.bussiness.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zyw.novelGame.bussiness.service.ModelService;
import com.zyw.novelGame.mapper.ModelMapper;
import com.zyw.novelGame.model.Model;

@Service(value="modelService")
public class ModelServiceImpl implements ModelService{
	
	public static final  Logger logger=LoggerFactory.getLogger(ModelServiceImpl.class);
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<Model> queryModel() {
		return modelMapper.queryModel();
	}

}
