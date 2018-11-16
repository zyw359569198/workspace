package com.reign.gcld.nation.dao;

import com.reign.gcld.nation.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerPRankDao")
public class PlayerPRankDao extends BaseDao<PlayerPRank> implements IPlayerPRankDao
{
    @Override
	public PlayerPRank read(final int playerId) {
        return (PlayerPRank)this.getSqlSession().selectOne("com.reign.gcld.nation.domain.PlayerPRank.read", (Object)playerId);
    }
    
    @Override
	public PlayerPRank readForUpdate(final int playerId) {
        return (PlayerPRank)this.getSqlSession().selectOne("com.reign.gcld.nation.domain.PlayerPRank.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerPRank> getModels() {
        return (List<PlayerPRank>)this.getSqlSession().selectList("com.reign.gcld.nation.domain.PlayerPRank.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.nation.domain.PlayerPRank.getModelSize");
    }
    
    @Override
	public int create(final PlayerPRank playerPRank) {
        return this.getSqlSession().insert("com.reign.gcld.nation.domain.PlayerPRank.create", playerPRank);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.nation.domain.PlayerPRank.deleteById", playerId);
    }
    
    @Override
	public List<PlayerPRank> getRankList() {
        return (List<PlayerPRank>)this.getSqlSession().selectList("com.reign.gcld.nation.domain.PlayerPRank.getRankList");
    }
    
    @Override
	public int addKillNum(final int playerId, final int killNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("killNum", killNum);
        return this.getSqlSession().update("com.reign.gcld.nation.domain.PlayerPRank.addKillNum", params);
    }
    
    @Override
	public int received(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.nation.domain.PlayerPRank.received", playerId);
    }
    
    @Override
	public int clear(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.nation.domain.PlayerPRank.clear", forceId);
    }
    
    @Override
	public boolean hasReward(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.nation.domain.PlayerPRank.hasReward", (Object)playerId);
        return result != null && result == 0;
    }
}
