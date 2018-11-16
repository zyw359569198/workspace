package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface ITaskKillInfoDao extends IBaseDao<TaskKillInfo>
{
    TaskKillInfo read(final int p0);
    
    TaskKillInfo readForUpdate(final int p0);
    
    List<TaskKillInfo> getModels();
    
    int getModelSize();
    
    int create(final TaskKillInfo p0);
    
    int deleteById(final int p0);
    
    TaskKillInfo getTaskKillInfo(final int p0);
    
    TaskKillInfo getTaskKillInfoByPAndT(final int p0, final int p1);
    
    void updateIsRewarded(final int p0, final int p1);
    
    void updateIsRewardedTask(final int p0, final int p1, final int p2);
    
    void updateKillNum(final int p0, final int p1, final long p2);
    
    void updateKillNumTaskId(final int p0, final int p1, final int p2, final long p3);
    
    void deleteAllInfos();
    
    List<TaskKillInfo> getList();
    
    void updateTime(final int p0, final long p1);
    
    Integer getPlayerIdByDown(final int p0, final int p1);
    
    int getKillNum(final int p0);
    
    List<Integer> getPlayerIdListByUp(final int p0, final int p1);
    
    List<Integer> getPlayerIdList(final int p0, final int p1);
    
    List<Integer> getPlayerIdListByDown(final int p0, final int p1);
    
    void eraseByTaskId(final Integer p0);
    
    List<TaskKillInfo> getByTaskId(final Integer p0);
    
    List<TaskKillInfo> getByForceId(final int p0);
}
