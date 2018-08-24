package com.zyw.novelGame.mapper;

import java.util.List;

import com.zyw.novelGame.model.Author;

public interface AuthorMapper {
    int deleteByPrimaryKey(String id);

    int insert(Author record);

    int insertSelective(Author record);

    Author selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Author record);

    int updateByPrimaryKey(Author record);
    
    List<Author> queryAuthorInfo(Author author);
}