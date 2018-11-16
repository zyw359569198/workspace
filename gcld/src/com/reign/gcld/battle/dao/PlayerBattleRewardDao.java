package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerBattleRewardDao")
public class PlayerBattleRewardDao extends BaseDao<PlayerBattleReward> implements IPlayerBattleRewardDao
{
    @Override
	public PlayerBattleReward read(final int vId) {
        return (PlayerBattleReward)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleReward.read", (Object)vId);
    }
    
    @Override
	public PlayerBattleReward readForUpdate(final int vId) {
        return (PlayerBattleReward)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleReward.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerBattleReward> getModels() {
        return (List<PlayerBattleReward>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerBattleReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleReward.getModelSize");
    }
    
    @Override
	public int create(final PlayerBattleReward playerBattleReward) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerBattleReward.create", playerBattleReward);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerBattleReward.deleteById", vId);
    }
    
    @Override
	public List<PlayerBattleReward> getListByType(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type);
        return (List<PlayerBattleReward>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerBattleReward.getListByType", (Object)params);
    }
    
    @Override
	public List<PlayerBattleReward> getListBy2Type(final int playerId, final int type1, final int type2) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type1", type1).addParam("type2", type2);
        return (List<PlayerBattleReward>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerBattleReward.getListBy2Type", (Object)params);
    }
    
    @Override
	public List<PlayerBattleReward> getListByPlayerId(final int playerId) {
        return (List<PlayerBattleReward>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerBattleReward.getListByPlayerId", (Object)playerId);
    }
}
