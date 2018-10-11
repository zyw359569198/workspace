package com.zyw.novelGame.bussiness.service;

import java.util.List;

import com.zyw.novelGame.model.SearchInfo;

public interface SearchInfoService {
	
	List<SearchInfo> querySearchInfo(SearchInfo searchInfo);
	
	int updateRecord(SearchInfo searchInfo);

}
