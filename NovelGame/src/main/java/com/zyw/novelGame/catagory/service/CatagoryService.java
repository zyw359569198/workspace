package com.zyw.novelGame.catagory.service;

import java.util.List;

import com.zyw.novelGame.model.Catagory;

public interface CatagoryService {
	
	public  List<Catagory>  queryCatagory(Catagory record);
	
	 int insert(Catagory record);

}
