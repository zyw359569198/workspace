package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerArmyDao")
public class PlayerArmyDao extends BaseDao<PlayerArmy> implements IPlayerArmyDao
{
    @Override
	public PlayerArmy read(final int vId) {
        return (PlayerArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmy.read", (Object)vId);
    }
    
    @Override
	public PlayerArmy readForUpdate(final int vId) {
        return (PlayerArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmy.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerArmy> getModels() {
        return (List<PlayerArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmy.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmy.getModelSize");
    }
    
    @Override
	public int create(final PlayerArmy playerArmy) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerArmy.create", playerArmy);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerArmy.deleteById", vId);
    }
    
    @Override
	public List<PlayerArmy> getPlayerPowerArmies(final int playerId, final int powerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId);
        return (List<PlayerArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmy.getPlayerPowerArmies", (Object)params);
    }
    
    @Override
	public PlayerArmy getPlayerArmy(final int playerId, final int armyId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId);
        return (PlayerArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmy.getPlayerArmy", (Object)params);
    }
    
    @Override
	public int updateAttackable(final int playerId, final int armyId, final int attackable) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("attackable", attackable);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.updateAttackable", params);
    }
    
    @Override
	public int updateAttackWinNum(final int playerId, final int armyId, final int addWinNum, final int addAttNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("addWinNum", addWinNum).addParam("attNum", addAttNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.updateAttackWinNum", params);
    }
    
    @Override
	public int updateAttack(final int playerId, final int armyId, final int addWinNum, final int attackable) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("addWinNum", addWinNum).addParam("attackable", attackable);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.updateAttack", params);
    }
    
    @Override
	public List<PlayerArmy> getPlayerArmyList(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (List<PlayerArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmy.getPlayerArmyList", (Object)params);
    }
    
    @Override
	public int resetFirstWin(final int playerId, final int armyId, final int firstWin) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("firstWin", firstWin);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.resetFirstWin", params);
    }
    
    @Override
	public int resetFirstOpen(final int playerId, final int armyId, final int firstOpen) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("firstOpen", firstOpen);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.resetFirstOpen", params);
    }
    
    @Override
	public int updateAttackWin(final int playerId, final int armyId, final int addWinNum, final int firstWin, final int addAttNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("addWinNum", addWinNum).addParam("firstWin", firstWin).addParam("attNum", addAttNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.updateAttackWin", params);
    }
    
    @Override
	public int updateAttNum(final int playerId, final int armyId, final int addAttNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("attNum", addAttNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.updateAttNum", params);
    }
    
    @Override
	public int addDropCount(final int playerId, final int armyId, final int adddropCount) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("adddropCount", adddropCount);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.addDropCount", params);
    }
    
    @Override
	public int getLastWinArmy(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmy.getLastWinArmy", (Object)playerId);
    }
    
    @Override
	public int getMaxArmyIdByPlayerId(final int playerId) {
        final Integer armyId = (Integer)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmy.getMaxArmyIdByPlayerId", (Object)playerId);
        if (armyId == null) {
            return 0;
        }
        return armyId;
    }
    
    @Override
	public void updateGoldReward(final int playerId, final int armiesId, final int goldReward) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armiesId", armiesId);
        params.addParam("goldReward", goldReward);
        this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmy.updateGoldReward", params);
    }
    
    @Override
	public List<PlayerArmy> getPlayerArmyRewardList(final int playerId, final int goldReward) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("goldReward", goldReward);
        return (List<PlayerArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmy.getPlayerArmyRewardList", (Object)params);
    }
}
