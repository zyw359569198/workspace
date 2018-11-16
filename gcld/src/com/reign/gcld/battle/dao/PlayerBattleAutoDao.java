package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerBattleAutoDao")
public class PlayerBattleAutoDao extends BaseDao<PlayerBattleAuto> implements IPlayerBattleAutoDao
{
    @Override
	public PlayerBattleAuto read(final int playerId) {
        return (PlayerBattleAuto)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleAuto.read", (Object)playerId);
    }
    
    @Override
	public PlayerBattleAuto readForUpdate(final int playerId) {
        return (PlayerBattleAuto)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleAuto.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerBattleAuto> getModels() {
        return (List<PlayerBattleAuto>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerBattleAuto.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleAuto.getModelSize");
    }
    
    @Override
	public int create(final PlayerBattleAuto playerBattleAuto) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerBattleAuto.create", playerBattleAuto);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerBattleAuto.deleteById", playerId);
    }
    
    @Override
	public int updateTimes(final int playerId, final int reduceTimes) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("reduceTimes", reduceTimes);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAuto.updateTimes", params);
    }
    
    @Override
	public int updateTimesReport(final int playerId, final int reduceTimes, final String report) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("reduceTimes", reduceTimes).addParam("report", report);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAuto.updateTimesReport", params);
    }
}
