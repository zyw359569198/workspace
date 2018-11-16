package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerBatRankDao")
public class PlayerBatRankDao extends BaseDao<PlayerBatRank> implements IPlayerBatRankDao
{
    @Override
	public PlayerBatRank read(final int playerId) {
        return (PlayerBatRank)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBatRank.read", (Object)playerId);
    }
    
    @Override
	public PlayerBatRank readForUpdate(final int playerId) {
        return (PlayerBatRank)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBatRank.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerBatRank> getModels() {
        return (List<PlayerBatRank>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerBatRank.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBatRank.getModelSize");
    }
    
    @Override
	public int create(final PlayerBatRank playerBatRank) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerBatRank.create", playerBatRank);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerBatRank.deleteById", playerId);
    }
    
    @Override
	public int resetReward() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.resetReward");
    }
    
    @Override
	public int resetOnePlayerReward(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.resetOnePlayerReward", playerId);
    }
    
    @Override
	public int resetBuyNumTimes() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.resetBuyNumTimes");
    }
    
    @Override
	public int addRankBatNumPerTwoHours(final int rankBatNumLimit) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.addRankBatNumPerTwoHours", rankBatNumLimit);
    }
    
    @Override
	public int setLastRankTimeAsNow() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.setLastRankTimeAsNow");
    }
    
    @Override
	public int updateRewardAndRank(final int playerId, final String reward, final int rank) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("reward", reward);
        params.addParam("rank", rank);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.updateRewardAndRank", params);
    }
    
    @Override
	public int updateLastRankTime(final int playerId, final Date time) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("time", time);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.updateLastRankTime", params);
    }
    
    @Override
	public int updateRankBatNum(final int playerId, final int newNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("newNum", newNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.updateRankBatNum", params);
    }
    
    @Override
	public int updateRankBatNumAndBuyTimes(final int playerId, final int newNum, final int buyTimes) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("newNum", newNum);
        params.addParam("buyTimes", buyTimes);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.updateRankBatNumAndBuyTimes", params);
    }
    
    @Override
	public int updateRankScore(final int playerId, final int score) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("score", score);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBatRank.updateRankScore", params);
    }
    
    @Override
	public int updateRankScoreBoundedByMax(final int playerId, final int score, final int max) {
        return 0;
    }
}
