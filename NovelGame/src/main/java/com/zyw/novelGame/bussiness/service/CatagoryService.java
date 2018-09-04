package com.zyw.novelGame.bussiness.service;

import java.util.List;

import com.zyw.novelGame.model.Catagory;

public interface CatagoryService {
	
	public  List<Catagory>  queryCatagory(Catagory record);
	
	 int insert(Catagory record);

}
