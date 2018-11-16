package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerExpandInfoDao")
public class PlayerExpandInfoDao extends BaseDao<PlayerExpandInfo> implements IPlayerExpandInfoDao
{
    @Override
	public PlayerExpandInfo read(final int playerId) {
        return (PlayerExpandInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerExpandInfo.read", (Object)playerId);
    }
    
    @Override
	public PlayerExpandInfo readForUpdate(final int playerId) {
        return (PlayerExpandInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerExpandInfo.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerExpandInfo> getModels() {
        return (List<PlayerExpandInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerExpandInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerExpandInfo.getModelSize");
    }
    
    @Override
	public int create(final PlayerExpandInfo playerExpandInfo) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.PlayerExpandInfo.create", playerExpandInfo);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerExpandInfo.deleteById", playerId);
    }
    
    @Override
	public void batchCreate(final List<PlayerExpandInfo> list) {
        this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerExpandInfo.batchCreate", list);
    }
    
    @Override
	public void deleteAll() {
        this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerExpandInfo.deleteAll");
    }
    
    @Override
	public PlayerExpandInfo getByForceAndRank(final int forceId, final int nextRank) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("nextRank", nextRank);
        final List<PlayerExpandInfo> list = (List<PlayerExpandInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerExpandInfo.getByForceAndRank", (Object)params);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
    
    @Override
	public void updateIsRewardedTask(final int playerId, final int i) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("isReward", i);
        this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerExpandInfo.updateIsRewardedTask", params);
    }
    
    @Override
	public List<PlayerExpandInfo> getByForceId(final Integer forceid) {
        return (List<PlayerExpandInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerExpandInfo.getByForceId", (Object)forceid);
    }
    
    @Override
	public void eraseByForceId(final Integer forceid) {
        this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerExpandInfo.eraseByForceId", forceid);
    }
}
