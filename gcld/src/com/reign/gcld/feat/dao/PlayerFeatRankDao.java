package com.reign.gcld.feat.dao;

import com.reign.gcld.feat.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerFeatRankDao")
public class PlayerFeatRankDao extends BaseDao<PlayerFeatRank> implements IPlayerFeatRankDao
{
    @Override
	public PlayerFeatRank read(final int playerId) {
        return (PlayerFeatRank)this.getSqlSession().selectOne("com.reign.gcld.feat.domain.PlayerFeatRank.read", (Object)playerId);
    }
    
    @Override
	public PlayerFeatRank readForUpdate(final int playerId) {
        return (PlayerFeatRank)this.getSqlSession().selectOne("com.reign.gcld.feat.domain.PlayerFeatRank.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerFeatRank> getModels() {
        return (List<PlayerFeatRank>)this.getSqlSession().selectList("com.reign.gcld.feat.domain.PlayerFeatRank.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.feat.domain.PlayerFeatRank.getModelSize");
    }
    
    @Override
	public int create(final PlayerFeatRank playerFeatRank) {
        return this.getSqlSession().insert("com.reign.gcld.feat.domain.PlayerFeatRank.create", playerFeatRank);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.feat.domain.PlayerFeatRank.deleteById", playerId);
    }
    
    @Override
	public List<PlayerFeatRank> getRankList() {
        return (List<PlayerFeatRank>)this.getSqlSession().selectList("com.reign.gcld.feat.domain.PlayerFeatRank.getRankList");
    }
    
    @Override
	public int getTotalFeat(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.feat.domain.PlayerFeatRank.getTotalFeat", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int addOccupyAndFeat(final int playerId, final int feat) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("feat", feat);
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.addOccupyAndFeat", params);
    }
    
    @Override
	public int addAssistAndFeat(final int playerId, final int feat) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("feat", feat);
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.addAssistAndFeat", params);
    }
    
    @Override
	public int addCheerAndFeat(final int playerId, final int feat) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("feat", feat);
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.addCheerAndFeat", params);
    }
    
    @Override
	public int addKillNumAndFeat(final int playerId, final int killNum, final int feat) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("killNum", killNum).addParam("feat", feat);
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.addKillNumAndFeat", params);
    }
    
    @Override
	public List<PlayerFeatRank> getRewardRankList() {
        return (List<PlayerFeatRank>)this.getSqlSession().selectList("com.reign.gcld.feat.domain.PlayerFeatRank.getRewardRankList");
    }
    
    @Override
	public int clearAll() {
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.clearAll");
    }
    
    @Override
	public int clearLastRankReced() {
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.clearLastRankReced");
    }
    
    @Override
	public int updateLastRank(final int playerId, final int lastRank) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("lastRank", lastRank);
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.updateLastRank", params);
    }
    
    @Override
	public int received(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.received", playerId);
    }
    
    @Override
	public int addTotalFeat(final int playerId, final int feat) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("feat", feat);
        return this.getSqlSession().update("com.reign.gcld.feat.domain.PlayerFeatRank.addTotalFeat", params);
    }
}
