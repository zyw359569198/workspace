package com.reign.gcld.tech.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.tech.domain.*;
import java.util.*;

public interface IPlayerTechDao extends IBaseDao<PlayerTech>
{
    PlayerTech read(final int p0);
    
    PlayerTech readForUpdate(final int p0);
    
    List<PlayerTech> getModels();
    
    int getModelSize();
    
    int create(final PlayerTech p0);
    
    int deleteById(final int p0);
    
    List<PlayerTech> getPlayerTechList(final int p0);
    
    PlayerTech getPlayerTech(final int p0, final int p1);
    
    List<PlayerTech> getTechListByLimit(final int p0, final int p1, final int p2);
    
    int setNumAndStatus(final int p0, final int p1, final int p2);
    
    int setNum(final int p0, final int p1);
    
    int setCd(final int p0, final Date p1, final int p2, final int p3);
    
    int getSizeByPlayerId(final int p0);
    
    List<PlayerTech> getByPlayerIdAndTechKey(final int p0, final int p1);
    
    List<PlayerTech> getAllTechByKey(final int p0, final int p1);
    
    int getTechIdByVId(final int p0);
    
    List<PlayerTech> getListByTechKey(final int p0);
    
    List<PlayerTech> getByTechKeys(final int p0, final int p1, final int p2, final int p3);
    
    List<PlayerTech> getByTechKeys2(final int p0, final int p1, final int p2);
    
    List<PlayerTech> getByTechKey1(final int p0, final int p1);
    
    int setStatusAndIsNew(final int p0, final int p1, final int p2, final int p3);
    
    int setIsNewAndFinishNew(final int p0, final int p1, final int p2);
    
    int deleteByPlayerIdAndKey(final int p0, final int p1, final int p2);
    
    int setStatus(final int p0, final int p1);
    
    List<PlayerTech> getListByPlayerIdAndStatus(final int p0, final int p1);
    
    int deleteByPlayerIdAndStatus(final int p0, final int p1);
    
    int getNumDisPlayButton(final int p0);
    
    int techAll(final int p0);
    
    List<Integer> getPlayerIdListByKey(final int p0);
    
    int getEffectSizeByTechKey(final int p0, final int p1);
    
    List<Integer> getPlayerIdListByFirstWorldDramaKey(final List<Integer> p0);
}
