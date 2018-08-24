package com.zyw.novelGame.mapper;

import java.util.List;

import com.zyw.novelGame.model.Catagory;

public interface CatagoryMapper {
    int deleteByPrimaryKey(String id);

    int insert(Catagory record);

    int insertSelective(Catagory record);

    Catagory selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Catagory record);

    int updateByPrimaryKey(Catagory record);
    
    List<Catagory> queryCatagory(Catagory record);
}