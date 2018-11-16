package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerFarmDao")
public class PlayerFarmDao extends BaseDao<PlayerFarm> implements IPlayerFarmDao
{
    @Override
	public PlayerFarm read(final int vId) {
        return (PlayerFarm)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerFarm.read", (Object)vId);
    }
    
    @Override
	public PlayerFarm readForUpdate(final int vId) {
        return (PlayerFarm)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerFarm.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerFarm> getModels() {
        return (List<PlayerFarm>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerFarm.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerFarm.getModelSize");
    }
    
    @Override
	public int create(final PlayerFarm playerFarm) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.PlayerFarm.create", playerFarm);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerFarm.deleteById", vId);
    }
    
    @Override
	public PlayerFarm getByPAndGId(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return (PlayerFarm)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerFarm.getByPAndGId", (Object)params);
    }
    
    @Override
	public List<PlayerFarm> getListByPid(final int playerId) {
        return (List<PlayerFarm>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerFarm.getListByPid", (Object)playerId);
    }
    
    @Override
	public List<PlayerFarm> getFarmsAfterNow() {
        return (List<PlayerFarm>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerFarm.getFarmsAfterNow");
    }
    
    @Override
	public int deletByPAndGId(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerFarm.deletByPAndGId", params);
    }
    
    @Override
	public int deletByPlayerId(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerFarm.deletByPlayerId", playerId);
    }
}
