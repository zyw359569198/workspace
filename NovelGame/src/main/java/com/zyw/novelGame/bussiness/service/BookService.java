package com.zyw.novelGame.bussiness.service;

import java.util.HashMap;
import java.util.List;

import com.zyw.novelGame.model.Book;

public interface BookService {
	
	int updateHits(String  bookId);
	
	List<HashMap>  queryBook(String order,int isCompletion);
	
    List<HashMap> queryBookRelationByCataNameEn(String cataNameEn,int count);
    
    List<HashMap>  queryBookByCreateTime();
    
    List<HashMap> queryBookUpdateInfo(String cataNameEn,String order,int isCompletion);
    
    List<HashMap> queryBookInfo(String authorName,String authorNameEn,String bookName,String bookNameEn);
    
    List<HashMap> queryMobileBookInfo(String authorName,String authorNameEn,String bookName,String bookNameEn);
    
    int insert(Book record);
    
    List<Book> queryBookByHits();
    
    int updateByBookID(Book record);


}
