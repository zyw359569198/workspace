package com.zyw.novelGame.mapper;

import java.util.List;

import com.zyw.novelGame.model.Model;

public interface ModelMapper {
    int deleteByPrimaryKey(String id);

    int insert(Model record);

    int insertSelective(Model record);

    Model selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Model record);

    int updateByPrimaryKey(Model record);
    
    List<Model> queryModel();
}