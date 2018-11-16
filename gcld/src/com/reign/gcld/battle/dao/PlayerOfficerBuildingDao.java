package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerOfficerBuildingDao")
public class PlayerOfficerBuildingDao extends BaseDao<PlayerOfficerBuilding> implements IPlayerOfficerBuildingDao
{
    @Override
	public PlayerOfficerBuilding read(final int playerId) {
        return (PlayerOfficerBuilding)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerOfficerBuilding.read", (Object)playerId);
    }
    
    @Override
	public PlayerOfficerBuilding readForUpdate(final int playerId) {
        return (PlayerOfficerBuilding)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerOfficerBuilding.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerOfficerBuilding> getModels() {
        return (List<PlayerOfficerBuilding>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerOfficerBuilding.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerOfficerBuilding.getModelSize");
    }
    
    @Override
	public int create(final PlayerOfficerBuilding playerOfficerBuilding) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerOfficerBuilding.create", playerOfficerBuilding);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerOfficerBuilding.deleteById", playerId);
    }
    
    @Override
	public List<PlayerOfficerBuilding> getBuildingMembers(final int forceId, final int buildingId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("buildingId", buildingId);
        return (List<PlayerOfficerBuilding>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerOfficerBuilding.getBuildingMembers", (Object)params);
    }
    
    @Override
	public List<PlayerOfficerBuilding> getApplyingMembers(final int forceId, final int buildingId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("buildingId", buildingId);
        return (List<PlayerOfficerBuilding>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerOfficerBuilding.getApplyingMembers", (Object)params);
    }
    
    @Override
	public void deleteByState(final int forceId, final int buildingId, final int state) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("buildingId", buildingId);
        params.addParam("state", state);
        this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerOfficerBuilding.deleteByState", params);
    }
    
    @Override
	public void changeLeader(final int playerId, final int isLeader) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("isLeader", isLeader);
        this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerOfficerBuilding.changeLeader", params);
    }
    
    @Override
	public void updateState(final int playerId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("state", state);
        this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerOfficerBuilding.updateState", params);
    }
    
    @Override
	public void deleteByBuildingId(final int forceId, final int buildingId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("buildingId", buildingId);
        this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerOfficerBuilding.deleteByBuildingId", params);
    }
    
    @Override
	public void updateIsNew(final int playerId, final int isNew) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("isNew", isNew);
        this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerOfficerBuilding.updateIsNew", params);
    }
    
    @Override
	public Integer getOwnerIdByBuilding(final Integer buildingId, final int forceId) {
        final Params params = new Params();
        params.addParam("buildingId", buildingId);
        params.addParam("forceId", forceId);
        return (Integer)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerOfficerBuilding.getOwnerIdByBuilding", (Object)params);
    }
}
