package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("taskKillInfoDao")
public class TaskKillInfoDao extends BaseDao<TaskKillInfo> implements ITaskKillInfoDao
{
    @Override
	public TaskKillInfo read(final int vid) {
        return (TaskKillInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskKillInfo.read", (Object)vid);
    }
    
    @Override
	public TaskKillInfo readForUpdate(final int vid) {
        return (TaskKillInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskKillInfo.readForUpdate", (Object)vid);
    }
    
    @Override
	public List<TaskKillInfo> getModels() {
        return (List<TaskKillInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.TaskKillInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskKillInfo.getModelSize");
    }
    
    @Override
	public int create(final TaskKillInfo taskKillInfo) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.TaskKillInfo.create", taskKillInfo);
    }
    
    @Override
	public int deleteById(final int vid) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.TaskKillInfo.deleteById", vid);
    }
    
    @Override
	public TaskKillInfo getTaskKillInfo(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (TaskKillInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskKillInfo.getTaskKillInfo", (Object)params);
    }
    
    @Override
	public TaskKillInfo getTaskKillInfoByPAndT(final int playerId, final int taskId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("taskId", taskId);
        return (TaskKillInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskKillInfo.getTaskKillInfoByPAndT", (Object)params);
    }
    
    @Override
	public void updateIsRewarded(final int playerId, final int updateValue) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("updateValue", updateValue);
        this.getSqlSession().update("com.reign.gcld.rank.domain.TaskKillInfo.updateIsRewarded", params);
    }
    
    @Override
	public void updateIsRewardedTask(final int playerId, final int taskId, final int updateValue) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("taskId", taskId);
        params.addParam("updateValue", updateValue);
        this.getSqlSession().update("com.reign.gcld.rank.domain.TaskKillInfo.updateIsRewardedTask", params);
    }
    
    @Override
	public void updateKillNum(final int playerId, final int killTotal, final long updateTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("killTotal", killTotal);
        params.addParam("updateTime", updateTime);
        this.getSqlSession().update("com.reign.gcld.rank.domain.TaskKillInfo.updateKillNum", params);
    }
    
    @Override
	public void updateKillNumTaskId(final int playerId, final int taskId, final int killTotal, final long updateTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("killTotal", killTotal);
        params.addParam("updateTime", updateTime);
        params.addParam("taskId", taskId);
        this.getSqlSession().update("com.reign.gcld.rank.domain.TaskKillInfo.updateKillNumTaskId", params);
    }
    
    @Override
	public List<TaskKillInfo> getList() {
        return (List<TaskKillInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.TaskKillInfo.getList");
    }
    
    @Override
	public void deleteAllInfos() {
        this.getSqlSession().update("com.reign.gcld.rank.domain.TaskKillInfo.deleteAllInfos");
    }
    
    @Override
	public void updateTime(final int playerId, final long currentTimeMillis) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("updateTime", currentTimeMillis);
        this.getSqlSession().update("com.reign.gcld.rank.domain.TaskKillInfo.updateTime", params);
    }
    
    @Override
	public Integer getPlayerIdByDown(final int playerId, final int forceId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("forceId", forceId);
        return (Integer)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskKillInfo.getPlayerIdByDown", (Object)params);
    }
    
    @Override
	public int getKillNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskKillInfo.getKillNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public List<Integer> getPlayerIdListByUp(final int playerId, final int forceId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("forceId", forceId);
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.TaskKillInfo.getPlayerIdListByUp", (Object)params);
    }
    
    @Override
	public List<Integer> getPlayerIdList(final int playerId, final int forceId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("forceId", forceId);
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.TaskKillInfo.getPlayerIdList", (Object)params);
    }
    
    @Override
	public List<Integer> getPlayerIdListByDown(final int playerId, final int forceId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("forceId", forceId);
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.TaskKillInfo.getPlayerIdListByDown", (Object)params);
    }
    
    @Override
	public void eraseByTaskId(final Integer taskId) {
        this.getSqlSession().delete("com.reign.gcld.rank.domain.TaskKillInfo.eraseByForceId", taskId);
    }
    
    @Override
	public List<TaskKillInfo> getByTaskId(final Integer nationTaskId) {
        return (List<TaskKillInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.TaskKillInfo.getByTaskId", (Object)nationTaskId);
    }
    
    @Override
	public List<TaskKillInfo> getByForceId(final int forceId) {
        return (List<TaskKillInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.TaskKillInfo.getByForceId", (Object)forceId);
    }
}
