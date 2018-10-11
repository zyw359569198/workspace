package com.zyw.novelGame.bussiness.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zyw.novelGame.bussiness.service.SearchInfoService;
import com.zyw.novelGame.mapper.SearchInfoMapper;
import com.zyw.novelGame.model.SearchInfo;

@Service(value="searchInfoService")
public class SearchInfoServiceImpl implements SearchInfoService{
	
	public static final  Logger logger=LoggerFactory.getLogger(SearchInfoServiceImpl.class);
	
	@Autowired
	private SearchInfoMapper searchInfoMapper;
	
	@Override
	public List<SearchInfo> querySearchInfo(SearchInfo record) {
		return searchInfoMapper.querySearchInfo(record);
	}

	@Override
	public int updateRecord(SearchInfo searchInfo) {
		List<SearchInfo>  list=querySearchInfo(searchInfo);
		if(list.size()>0) {
			searchInfo.setId(list.get(0).getId());
			searchInfo.setHits(list.get(0).getHits()+1);
			return searchInfoMapper.updateById(searchInfo);
		}else {
			searchInfo.setId(UUID.randomUUID().toString());
			searchInfo.setCreateTime(new Date());
			searchInfo.setKeyvalue("/model/search/?keyword="+searchInfo.getKeyword());
			searchInfo.setHits(0L);
			return searchInfoMapper.insert(searchInfo);
		}
	}

}
