package com.reign.gcld.huizhan.dao;

import com.reign.gcld.huizhan.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerHuizhanDao")
public class PlayerHuizhanDao extends BaseDao<PlayerHuizhan> implements IPlayerHuizhanDao
{
    @Override
	public PlayerHuizhan read(final int vId) {
        return (PlayerHuizhan)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.PlayerHuizhan.read", (Object)vId);
    }
    
    @Override
	public PlayerHuizhan readForUpdate(final int vId) {
        return (PlayerHuizhan)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.PlayerHuizhan.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerHuizhan> getModels() {
        return (List<PlayerHuizhan>)this.getSqlSession().selectList("com.reign.gcld.huizhan.domain.PlayerHuizhan.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.PlayerHuizhan.getModelSize");
    }
    
    @Override
	public int create(final PlayerHuizhan playerHuizhan) {
        return this.getSqlSession().insert("com.reign.gcld.huizhan.domain.PlayerHuizhan.create", playerHuizhan);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.huizhan.domain.PlayerHuizhan.deleteById", vId);
    }
    
    @Override
	public PlayerHuizhan getByhuiZhanIdAndplayerId(final int hzId, final int playerId) {
        final Params params = new Params();
        params.addParam("hzId", hzId).addParam("playerId", playerId);
        return (PlayerHuizhan)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.PlayerHuizhan.getByhuiZhanIdAndplayerId", (Object)params);
    }
    
    @Override
	public int updateForceByhzIdAndPlayerId(final int force, final int hzId, final int playerId) {
        final Params params = new Params();
        params.addParam("hzId", hzId).addParam("force", force).addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.PlayerHuizhan.updateForceByhzIdAndPlayerId", params);
    }
    
    @Override
	public int addPhantomNumByhzIdAndPlayerId(final int hzId, final int playerId) {
        final Params params = new Params();
        params.addParam("hzId", hzId).addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.PlayerHuizhan.addPhantomNumByhzIdAndPlayerId", params);
    }
    
    @Override
	public List<PlayerHuizhan> getUnReceivedRewardByPlayerIdOrderByJoinTimeDesc(final int playerId) {
        return (List<PlayerHuizhan>)this.getSqlSession().selectList("com.reign.gcld.huizhan.domain.PlayerHuizhan.getUnReceivedRewardByPlayerIdOrderByJoinTimeDesc", (Object)playerId);
    }
    
    @Override
	public List<PlayerHuizhan> getByhzIdOrderByForceDesc(final int hzId) {
        return (List<PlayerHuizhan>)this.getSqlSession().selectList("com.reign.gcld.huizhan.domain.PlayerHuizhan.getByhzIdOrderByForceDesc", (Object)hzId);
    }
    
    @Override
	public List<PlayerHuizhan> getByhzIdAndForceId(final int hzId, final int forceId) {
        final Params params = new Params();
        params.addParam("hzId", hzId).addParam("forceId", forceId);
        return (List<PlayerHuizhan>)this.getSqlSession().selectList("com.reign.gcld.huizhan.domain.PlayerHuizhan.getByhzIdAndForceId", (Object)params);
    }
    
    @Override
	public PlayerHuizhan getByhzIdAndPlayerId(final int hzId, final int playerId) {
        final Params params = new Params();
        params.addParam("hzId", hzId).addParam("playerId", playerId);
        return (PlayerHuizhan)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.PlayerHuizhan.getByhzIdAndPlayerId", (Object)params);
    }
    
    @Override
	public int updateKillNumByhzIdAndPlayerId(final int killNum, final int hzId, final int playerId) {
        final Params params = new Params();
        params.addParam("killNum", killNum).addParam("hzId", hzId).addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.PlayerHuizhan.updateKillNumByhzIdAndPlayerId", params);
    }
    
    @Override
	public List<PlayerHuizhan> getUnReceivedRewardPlayerHuizhan() {
        return (List<PlayerHuizhan>)this.getSqlSession().selectList("com.reign.gcld.huizhan.domain.PlayerHuizhan.getUnReceivedRewardPlayerHuizhan");
    }
    
    @Override
	public int addPKNumByhzIdAndPlayerId(final int hzId, final int playerId) {
        final Params params = new Params();
        params.addParam("hzId", hzId).addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.PlayerHuizhan.addPKNumByhzIdAndPlayerId", params);
    }
    
    @Override
	public int updateAwardFlagByVid(final int awardFlag, final int vId) {
        final Params params = new Params();
        params.addParam("awardFlag", awardFlag).addParam("id", vId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.PlayerHuizhan.updateAwardFlagByVid", params);
    }
    
    @Override
	public int deleteByHzId(final int hzId) {
        return this.getSqlSession().delete("com.reign.gcld.huizhan.domain.PlayerHuizhan.deleteByHzId", hzId);
    }
}
