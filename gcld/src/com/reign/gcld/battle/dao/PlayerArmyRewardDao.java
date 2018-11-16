package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerArmyRewardDao")
public class PlayerArmyRewardDao extends BaseDao<PlayerArmyReward> implements IPlayerArmyRewardDao
{
    @Override
	public PlayerArmyReward read(final int vId) {
        return (PlayerArmyReward)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmyReward.read", (Object)vId);
    }
    
    @Override
	public PlayerArmyReward readForUpdate(final int vId) {
        return (PlayerArmyReward)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmyReward.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerArmyReward> getModels() {
        return (List<PlayerArmyReward>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmyReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmyReward.getModelSize");
    }
    
    @Override
	public int create(final PlayerArmyReward playerArmyReward) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerArmyReward.create", playerArmyReward);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerArmyReward.deleteById", vId);
    }
    
    @Override
	public List<PlayerArmyReward> getPlayerArmyRewardByPowerId(final int playerId, final int powerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("powerId", powerId);
        return (List<PlayerArmyReward>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmyReward.getPlayerArmyRewardByPowerId", (Object)params);
    }
    
    @Override
	public List<PlayerArmyReward> getPlayerArmyRewardByPowerIdAndState(final int playerId, final int powerId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("powerId", powerId);
        params.addParam("state", state);
        return (List<PlayerArmyReward>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmyReward.getPlayerArmyRewardByPowerIdAndState", (Object)params);
    }
    
    @Override
	public PlayerArmyReward getPlayerArmyRewardByArmyId(final int playerId, final int armyId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        return (PlayerArmyReward)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmyReward.getPlayerArmyRewardByArmyId", (Object)params);
    }
    
    @Override
	public int updateFirst(final int playerId, final int armyId, final int first) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("first", first);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.updateFirst", params);
    }
    
    @Override
	public int updateExpireTime(final int playerId, final int armyId, final Date expireTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("expireTime", expireTime);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.updateExpireTime", params);
    }
    
    @Override
	public int updateNpcLost(final int playerId, final int armyId, final String npcLost) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("npcLost", npcLost);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.updateNpcLost", params);
    }
    
    @Override
	public int updateNpcLostHp(final int playerId, final int armyId, final String npcLost, final int hp) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("npcLost", npcLost);
        params.addParam("hp", hp);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.updateNpcLostHp", params);
    }
    
    @Override
	public int updateState(final int playerId, final int armyId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.updateState", params);
    }
    
    @Override
	public int updateStateAndExpireTime(final int playerId, final int armyId, final int state, final Date expireTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("state", state);
        params.addParam("expireTime", expireTime);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.updateStateAndExpireTime", params);
    }
    
    @Override
	public int update(final int oldvId, final PlayerArmyReward newPlayerArmyReward) {
        final Params params = new Params();
        params.addParam("vId", oldvId);
        params.addParam("first", newPlayerArmyReward.getFirst());
        params.addParam("expireTime", newPlayerArmyReward.getExpireTime());
        params.addParam("npcLost", newPlayerArmyReward.getNpcLost());
        params.addParam("hp", newPlayerArmyReward.getHp());
        params.addParam("hpMax", newPlayerArmyReward.getHpMax());
        params.addParam("state", newPlayerArmyReward.getState());
        params.addParam("buyCount", newPlayerArmyReward.getBuyCount());
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.update", params);
    }
    
    @Override
	public int updateFirstWin(final int playerId, final int armyId, final int firstWin) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("firstWin", firstWin);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.updateFirstWin", params);
    }
    
    @Override
	public List<PlayerArmyReward> getListByPlayerId(final int playerId) {
        return (List<PlayerArmyReward>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmyReward.getListByPlayerId", (Object)playerId);
    }
    
    @Override
	public int updateWinCount(final int playerId, final int armyId, final int winCount) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("winCount", winCount);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyReward.updateWinCount", params);
    }
}
