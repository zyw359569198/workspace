package com.zyw.novelGame.mapper;

import com.zyw.novelGame.model.CataBookRelation;

public interface CataBookRelationMapper {
    int deleteByPrimaryKey(String id);

    int insert(CataBookRelation record);

    int insertSelective(CataBookRelation record);

    CataBookRelation selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CataBookRelation record);

    int updateByPrimaryKey(CataBookRelation record);
}