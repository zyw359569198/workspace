package com.reign.gcld.nation.dao;

import com.reign.gcld.nation.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerTryRankDao")
public class PlayerTryRankDao extends BaseDao<PlayerTryRank> implements IPlayerTryRankDao
{
    @Override
	public PlayerTryRank read(final int playerId) {
        return (PlayerTryRank)this.getSqlSession().selectOne("com.reign.gcld.nation.domain.PlayerTryRank.read", (Object)playerId);
    }
    
    @Override
	public PlayerTryRank readForUpdate(final int playerId) {
        return (PlayerTryRank)this.getSqlSession().selectOne("com.reign.gcld.nation.domain.PlayerTryRank.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerTryRank> getModels() {
        return (List<PlayerTryRank>)this.getSqlSession().selectList("com.reign.gcld.nation.domain.PlayerTryRank.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.nation.domain.PlayerTryRank.getModelSize");
    }
    
    @Override
	public int create(final PlayerTryRank playerTryRank) {
        return this.getSqlSession().insert("com.reign.gcld.nation.domain.PlayerTryRank.create", playerTryRank);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.nation.domain.PlayerTryRank.deleteById", playerId);
    }
    
    @Override
	public List<PlayerTryRank> getRankList() {
        return (List<PlayerTryRank>)this.getSqlSession().selectList("com.reign.gcld.nation.domain.PlayerTryRank.getRankList");
    }
    
    @Override
	public int addKillNum(final int playerId, final int killNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("killNum", killNum);
        return this.getSqlSession().update("com.reign.gcld.nation.domain.PlayerTryRank.addKillNum", params);
    }
    
    @Override
	public int received(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.nation.domain.PlayerTryRank.received", playerId);
    }
    
    @Override
	public int clear(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.nation.domain.PlayerTryRank.clear", forceId);
    }
    
    @Override
	public boolean hasReward(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.nation.domain.PlayerTryRank.hasReward", (Object)playerId);
        return result != null && result == 0;
    }
}
