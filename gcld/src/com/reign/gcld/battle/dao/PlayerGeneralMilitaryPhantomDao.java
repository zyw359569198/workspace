package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerGeneralMilitaryPhantomDao")
public class PlayerGeneralMilitaryPhantomDao extends BaseDao<PlayerGeneralMilitaryPhantom> implements IPlayerGeneralMilitaryPhantomDao
{
    @Override
	public PlayerGeneralMilitaryPhantom read(final int vId) {
        return (PlayerGeneralMilitaryPhantom)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.read", (Object)vId);
    }
    
    @Override
	public PlayerGeneralMilitaryPhantom readForUpdate(final int vId) {
        return (PlayerGeneralMilitaryPhantom)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerGeneralMilitaryPhantom> getModels() {
        return (List<PlayerGeneralMilitaryPhantom>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.getModelSize");
    }
    
    @Override
	public int create(final PlayerGeneralMilitaryPhantom playerGeneralMilitaryPhantom) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.create", playerGeneralMilitaryPhantom);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.deleteById", vId);
    }
    
    @Override
	public List<PlayerGeneralMilitaryPhantom> getPhantomByLocationIdOrderByPlayerIdLvDesc(final int locationId) {
        return (List<PlayerGeneralMilitaryPhantom>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.getPhantomByLocationIdOrderByPlayerIdLvDesc", (Object)locationId);
    }
    
    @Override
	public List<PlayerGeneralMilitaryPhantom> getPhantomByLocationPidGid(final int locationId, final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("playerId", playerId).addParam("generalId", generalId);
        return (List<PlayerGeneralMilitaryPhantom>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.getPhantomByLocationPidGid", (Object)params);
    }
    
    @Override
	public int updateHp(final int vId, final int hp) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("hp", hp);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.updateHp", params);
    }
    
    @Override
	public int deleteByLocationId(final int LocationId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerGeneralMilitaryPhantom.deleteByLocationId", LocationId);
    }
}
