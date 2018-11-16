package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerArmyExtraDao")
public class PlayerArmyExtraDao extends BaseDao<PlayerArmyExtra> implements IPlayerArmyExtraDao
{
    @Override
	public PlayerArmyExtra read(final int vId) {
        return (PlayerArmyExtra)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmyExtra.read", (Object)vId);
    }
    
    @Override
	public PlayerArmyExtra readForUpdate(final int vId) {
        return (PlayerArmyExtra)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmyExtra.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerArmyExtra> getModels() {
        return (List<PlayerArmyExtra>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmyExtra.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmyExtra.getModelSize");
    }
    
    @Override
	public int create(final PlayerArmyExtra playerArmyExtra) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerArmyExtra.create", playerArmyExtra);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerArmyExtra.deleteById", vId);
    }
    
    @Override
	public List<PlayerArmyExtra> getArmiesByPowerId(final int playerId, final int powerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId);
        return (List<PlayerArmyExtra>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmyExtra.getArmiesByPowerId", (Object)params);
    }
    
    @Override
	public List<PlayerArmyExtra> getArmiesByPowerIdAndAttackable(final int playerId, final int powerId, final int attackable) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId).addParam("attackable", attackable);
        return (List<PlayerArmyExtra>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmyExtra.getArmiesByPowerIdAndAttackable", (Object)params);
    }
    
    @Override
	public PlayerArmyExtra getArmyByArmyId(final int playerId, final int armyId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId);
        return (PlayerArmyExtra)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerArmyExtra.getArmyByArmyId", (Object)params);
    }
    
    @Override
	public int updateAttackable(final int playerId, final int armyId, final int attackable) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("attackable", attackable);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyExtra.updateAttackable", params);
    }
    
    @Override
	public int addWinAttNumReset(final int playerId, final int armyId, final int addWinNum, final int addAttNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("addWinNum", addWinNum).addParam("addAttNum", addAttNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyExtra.addWinAttNumReset", params);
    }
    
    @Override
	public int resetFirstWin(final int playerId, final int armyId, final int firstWin) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("firstWin", firstWin);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyExtra.resetFirstWin", params);
    }
    
    @Override
	public int resetFirstOpen(final int playerId, final int armyId, final int firstOpen) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("firstOpen", firstOpen);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyExtra.resetFirstOpen", params);
    }
    
    @Override
	public int updateWinFirstWinAttNum(final int playerId, final int armyId, final int firstWin, final int addWinNum, final int addAttNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("firstWin", firstWin).addParam("addWinNum", addWinNum).addParam("addAttNum", addAttNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyExtra.updateWinFirstWinAttNum", params);
    }
    
    @Override
	public int AddAttNum(final int playerId, final int armyId, final int addAttNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId).addParam("attNum", addAttNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyExtra.AddAttNum", params);
    }
    
    @Override
	public int updateNpcLostHp(final int playerId, final int armyId, final String npcLost, final int hp) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("armyId", armyId);
        params.addParam("npcLost", npcLost);
        params.addParam("hp", hp);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerArmyExtra.updateNpcLostHp", params);
    }
    
    @Override
	public int deleteByPlayerIdArmyId(final int playerId, final int armyId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("armyId", armyId);
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerArmyExtra.deleteByPlayerIdArmyId", params);
    }
    
    @Override
	public int deleteByPlayerIdPowerId(final int playerId, final int powerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("powerId", powerId);
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerArmyExtra.deleteByPlayerIdPowerId", params);
    }
    
    @Override
	public List<PlayerArmyExtra> getListByPlayerId(final int playerId) {
        return (List<PlayerArmyExtra>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerArmyExtra.getListByPlayerId", (Object)playerId);
    }
}
