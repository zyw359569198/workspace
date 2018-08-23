package com.zyw.novelGame.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zyw.novelGame.model.Book;

public interface BookMapper {
    int deleteByPrimaryKey(String id);

    int insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);
    
    List<Book> queryBookByHits(@Param("count")int count);
        
    List<HashMap> queryBookRelationByCataID(@Param("cataId")String cataId,@Param("count")int count);
    
    List<HashMap> queryBookByCreateTime();
    List<HashMap> queryBookUpdateInfo(@Param("cataId")String cataId);
    List<Book> queryBookInfo(Book book);
}