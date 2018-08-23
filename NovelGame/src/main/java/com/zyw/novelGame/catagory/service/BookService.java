package com.zyw.novelGame.catagory.service;

import java.util.HashMap;
import java.util.List;

import com.zyw.novelGame.model.Book;

public interface BookService {
	
	List<Book>  queryBookByHits(int count);
	
    List<HashMap> queryBookRelationByCataID(String cataId,int count);
    
    List<HashMap>  queryBookByCreateTime();
    
    List<HashMap> queryBookUpdateInfo(String cataId);
    
    List<Book> queryBookInfo(Book book);


}
