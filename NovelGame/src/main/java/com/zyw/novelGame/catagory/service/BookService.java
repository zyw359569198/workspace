package com.zyw.novelGame.catagory.service;

import java.util.HashMap;
import java.util.List;

import com.zyw.novelGame.model.Book;

public interface BookService {
	
	List<Book>  queryBookByHits(int count);
	
    List<HashMap> queryBookRelationByCataNameEn(String cataNameEn,int count);
    
    List<HashMap>  queryBookByCreateTime();
    
    List<HashMap> queryBookUpdateInfo(String cataNameEn);
    
    List<HashMap> queryBookInfo(String authorName,String authorNameEn,String bookName,String bookNameEn);
    
    int insert(Book record);


}
