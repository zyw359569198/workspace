package com.zyw.novelGame.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zyw.novelGame.model.Book;

public interface BookMapper {
	 List<Book> queryBookByHits();
	 
	 int updateHits(@Param("bookId")String  bookId);
	 
    int deleteByPrimaryKey(String id);

    int insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);
    
    int updateByBookID(Book record);
    
    List<HashMap> queryBook(@Param("order")String order,@Param("isCompletion")int isCompletion);
        
    List<HashMap> queryBookRelationByCataNameEn(@Param("cataNameEn")String cataId,@Param("count")int count);
    
    List<HashMap> queryBookByCreateTime();
    List<HashMap> queryBookUpdateInfo(@Param("cataNameEn")String cataNameEn,@Param("order") String order,@Param("isCompletion")int isCompletion);
    List<HashMap> queryBookInfo(@Param("authorName")String authorName,@Param("authorNameEn")String authorNameEn,@Param("bookName")String bookName,@Param("bookNameEn")String bookNameEn);
    List<HashMap> queryMobileBookInfo(@Param("authorName")String authorName,@Param("authorNameEn")String authorNameEn,@Param("bookName")String bookName,@Param("bookNameEn")String bookNameEn);

}