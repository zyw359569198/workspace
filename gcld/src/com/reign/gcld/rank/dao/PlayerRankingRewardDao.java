package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerRankingRewardDao")
public class PlayerRankingRewardDao extends BaseDao<PlayerRankingReward> implements IPlayerRankingRewardDao
{
    @Override
	public PlayerRankingReward read(final int vid) {
        return (PlayerRankingReward)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerRankingReward.read", (Object)vid);
    }
    
    @Override
	public PlayerRankingReward readForUpdate(final int vid) {
        return (PlayerRankingReward)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerRankingReward.readForUpdate", (Object)vid);
    }
    
    @Override
	public List<PlayerRankingReward> getModels() {
        return (List<PlayerRankingReward>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerRankingReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerRankingReward.getModelSize");
    }
    
    @Override
	public int create(final PlayerRankingReward playerRankingReward) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.PlayerRankingReward.create", playerRankingReward);
    }
    
    @Override
	public int deleteById(final int vid) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerRankingReward.deleteById", vid);
    }
    
    @Override
	public PlayerRankingReward getByTypeAndPlayerId(final Integer playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", type);
        return (PlayerRankingReward)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerRankingReward.getByTypeAndPlayerId", (Object)params);
    }
    
    @Override
	public void updateReward(final Integer vid, final String rewardString) {
        final Params params = new Params();
        params.addParam("vid", vid);
        params.addParam("reward", rewardString);
        this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerRankingReward.updateReward", params);
    }
}
