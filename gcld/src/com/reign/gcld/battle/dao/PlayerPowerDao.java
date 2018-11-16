package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerPowerDao")
public class PlayerPowerDao extends BaseDao<PlayerPower> implements IPlayerPowerDao
{
    @Override
	public PlayerPower read(final int vId) {
        return (PlayerPower)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerPower.read", (Object)vId);
    }
    
    @Override
	public PlayerPower readForUpdate(final int vId) {
        return (PlayerPower)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerPower.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerPower> getModels() {
        return (List<PlayerPower>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerPower.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerPower.getModelSize");
    }
    
    @Override
	public int create(final PlayerPower playerPower) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerPower.create", playerPower);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerPower.deleteById", vId);
    }
    
    @Override
	public List<PlayerPower> getFourPlayerPower(final int playerId, final int powerId, final int prePowerId, final int postPowerId, final int extraPowerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId).addParam("prePowerId", prePowerId).addParam("postPowerId", postPowerId).addParam("extraPowerId", extraPowerId);
        return (List<PlayerPower>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerPower.getFourPlayerPower", (Object)params);
    }
    
    @Override
	public PlayerPower getPlayerPower(final int playerId, final int powerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId);
        return (PlayerPower)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerPower.getPlayerPower", (Object)params);
    }
    
    @Override
	public List<PlayerPower> getPlayerPowers(final int playerId) {
        return (List<PlayerPower>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerPower.getPlayerPowers", (Object)playerId);
    }
    
    @Override
	public int updateAttackable(final int playerId, final int powerId, final int attackable) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId).addParam("attackable", attackable);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerPower.updateAttackable", params);
    }
    
    @Override
	public int updateState(final int playerId, final int powerId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerPower.updateState", params);
    }
    
    @Override
	public int updateStateAndBuyCountAndExpireTime(final int playerId, final int powerId, final int state, final int buyCount, final Date expireTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId).addParam("buyCount", buyCount).addParam("state", state).addParam("expireTime", expireTime);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerPower.updateStateAndBuyCountAndExpireTime", params);
    }
    
    @Override
	public int updateRewardState(final int playerId, final int powerId, final int reward) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId).addParam("reward", reward);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerPower.updateRewardState", params);
    }
    
    @Override
	public int getNowPowerId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerPower.getNowPowerId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int deleteByPowerId(final int powerId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerPower.deleteByPowerId", powerId);
    }
}
