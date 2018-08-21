package com.zyw.novelGame.catagory.service;

import java.util.HashMap;
import java.util.List;

import com.zyw.novelGame.model.Book;

public interface BookService {
	
	List<Book>  queryBookByHits();
	
    List<HashMap> queryBookRelationByCataID(String cataId);
    
    List<HashMap>  queryBookByCreateTime();
    
    List<HashMap> queryBookUpdateInfo();


}
