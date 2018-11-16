package com.reign.gcld.score.dao;

import com.reign.gcld.score.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerScoreRankDao")
public class PlayerScoreRankDao extends BaseDao<PlayerScoreRank> implements IPlayerScoreRankDao
{
    @Override
	public PlayerScoreRank read(final int playerId) {
        return (PlayerScoreRank)this.getSqlSession().selectOne("com.reign.gcld.score.domain.PlayerScoreRank.read", (Object)playerId);
    }
    
    @Override
	public PlayerScoreRank readForUpdate(final int playerId) {
        return (PlayerScoreRank)this.getSqlSession().selectOne("com.reign.gcld.score.domain.PlayerScoreRank.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerScoreRank> getModels() {
        return (List<PlayerScoreRank>)this.getSqlSession().selectList("com.reign.gcld.score.domain.PlayerScoreRank.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.score.domain.PlayerScoreRank.getModelSize");
    }
    
    @Override
	public int create(final PlayerScoreRank playerScoreRank) {
        return this.getSqlSession().insert("com.reign.gcld.score.domain.PlayerScoreRank.create", playerScoreRank);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.score.domain.PlayerScoreRank.deleteById", playerId);
    }
    
    @Override
	public int updateOccupyNumAndScore(final int playerId, final int score) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("score", score);
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateOccupyNumAndScore", params);
    }
    
    @Override
	public int updateOccupyAndScore(final int playerId, final int score) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("score", score);
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateOccupyAndScore", params);
    }
    
    @Override
	public int updateOccupyNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateOccupyNum", playerId);
    }
    
    @Override
	public int updateAssistNumAndScore(final int playerId, final int score) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("score", score);
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateAssistNumAndScore", params);
    }
    
    @Override
	public int updateAssistAndScore(final int playerId, final int score) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("score", score);
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateAssistAndScore", params);
    }
    
    @Override
	public int updateAssistNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateAssistNum", playerId);
    }
    
    @Override
	public int updateCheerNumAndScore(final int playerId, final int score) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("score", score);
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateCheerNumAndScore", params);
    }
    
    @Override
	public int updateCheerAndScore(final int playerId, final int score) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("score", score);
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateCheerAndScore", params);
    }
    
    @Override
	public int updateCheerNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateCheerNum", playerId);
    }
    
    @Override
	public int getScore(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.score.domain.PlayerScoreRank.getScore", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int getScore2(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.score.domain.PlayerScoreRank.getScore2", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int clearAll() {
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.clearAll");
    }
    
    @Override
	public int clearAll2() {
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.clearAll2");
    }
    
    @Override
	public List<PlayerScoreRank> getRankList() {
        return (List<PlayerScoreRank>)this.getSqlSession().selectList("com.reign.gcld.score.domain.PlayerScoreRank.getRankList");
    }
    
    @Override
	public List<PlayerScoreRank> getRewardRankList() {
        return (List<PlayerScoreRank>)this.getSqlSession().selectList("com.reign.gcld.score.domain.PlayerScoreRank.getRewardRankList");
    }
    
    @Override
	public int updateLastRank(final int playerId, final int lastRank) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("lastRank", lastRank);
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.updateLastRank", params);
    }
    
    @Override
	public int received(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.score.domain.PlayerScoreRank.received", playerId);
    }
}
