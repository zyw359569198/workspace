package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerMistLostDao")
public class PlayerMistLostDao extends BaseDao<PlayerMistLost> implements IPlayerMistLostDao
{
    @Override
	public PlayerMistLost read(final int playerId) {
        return (PlayerMistLost)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerMistLost.read", (Object)playerId);
    }
    
    @Override
	public PlayerMistLost readForUpdate(final int playerId) {
        return (PlayerMistLost)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerMistLost.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerMistLost> getModels() {
        return (List<PlayerMistLost>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerMistLost.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerMistLost.getModelSize");
    }
    
    @Override
	public int create(final PlayerMistLost playerMistLost) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerMistLost.create", playerMistLost);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerMistLost.deleteById", playerId);
    }
    
    @Override
	public PlayerMistLost getMist(final int playerId, final int areaId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("areaId", areaId);
        return (PlayerMistLost)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerMistLost.getMist", (Object)params);
    }
    
    @Override
	public List<PlayerMistLost> getMistList(final int playerId) {
        return (List<PlayerMistLost>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerMistLost.getMistList", (Object)playerId);
    }
    
    @Override
	public int updateNpcLostDetail(final int playerId, final int areaId, final String losts) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("areaId", areaId);
        params.addParam("losts", losts);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerMistLost.updateNpcLostDetail", params);
    }
    
    @Override
	public int deleteByPlayerIdAreaId(final int playerId, final int areaId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("areaId", areaId);
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerMistLost.deleteByPlayerIdAreaId", params);
    }
}
