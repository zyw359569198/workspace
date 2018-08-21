package com.zyw.novelGame.mapper;

import java.util.HashMap;
import java.util.List;

import com.zyw.novelGame.model.Book;

public interface BookMapper {
    int deleteByPrimaryKey(String id);

    int insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);
    
    List<Book> queryBookByHits();
        
    List<HashMap> queryBookRelationByCataID(String cataId);
    
    List<HashMap> queryBookByCreateTime();
    List<HashMap> queryBookUpdateInfo();
}