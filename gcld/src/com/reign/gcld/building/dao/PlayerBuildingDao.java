package com.reign.gcld.building.dao;

import com.reign.gcld.building.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerBuildingDao")
public class PlayerBuildingDao extends BaseDao<PlayerBuilding> implements IPlayerBuildingDao
{
    @Override
	public PlayerBuilding read(final int vId) {
        return (PlayerBuilding)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuilding.read", (Object)vId);
    }
    
    @Override
	public PlayerBuilding readForUpdate(final int vId) {
        return (PlayerBuilding)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuilding.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerBuilding> getModels() {
        return (List<PlayerBuilding>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuilding.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuilding.getModelSize");
    }
    
    @Override
	public int create(final PlayerBuilding playerBuilding) {
        return this.getSqlSession().insert("com.reign.gcld.building.domain.PlayerBuilding.create", playerBuilding);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.building.domain.PlayerBuilding.deleteById", vId);
    }
    
    @Override
	public PlayerBuilding getPlayerBuilding(final int playerId, final int buildingId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("buildingId", buildingId);
        return (PlayerBuilding)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuilding.getPlayerBuilding", (Object)params);
    }
    
    @Override
	public List<PlayerBuilding> getPlayerBuildings(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (List<PlayerBuilding>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuilding.getPlayerBuildings", (Object)params);
    }
    
    @Override
	public List<PlayerBuilding> getPlayerBuildingByAreaId(final int playerId, final int areaId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("areaId", areaId);
        return (List<PlayerBuilding>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuilding.getPlayerBuildingByAreaId", (Object)params);
    }
    
    @Override
	public List<PlayerBuilding> getPlayerBuildingByType(final int playerId, final int outputType) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("outputType", outputType);
        return (List<PlayerBuilding>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuilding.getPlayerBuildingByType", (Object)params);
    }
    
    @Override
	public int upgradeBuilding(final int playerId, final int buildingId, final int plusLv, final int speedUpNum, final int maxLv) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("buildingId", buildingId);
        params.addParam("plusLv", plusLv);
        params.addParam("state", 0);
        params.addParam("speedUpNum", speedUpNum).addParam("maxLv", maxLv);
        return this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuilding.upgradeBuilding", params);
    }
    
    @Override
	public void upgradeBuildingState(final int playerId, final int buildingId, final int state, final int speedUpNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("buildingId", buildingId);
        params.addParam("state", state);
        params.addParam("speedUpNum", speedUpNum);
        this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuilding.upgradeBuildingState", params);
    }
    
    @Override
	public void updateBuildingNewState(final int playerId, final int buildingId, final int isNew) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("buildingId", buildingId);
        params.addParam("isNew", 0);
        this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuilding.updateBuildingNewState", params);
    }
    
    @Override
	public void updateEventId(final PlayerBuilding pb, final int eventId) {
        final Params params = new Params();
        params.addParam("vId", pb.getVId());
        params.addParam("eventId", eventId);
        this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuilding.updateEventId", params);
    }
    
    @Override
	public int update(final PlayerBuilding pb) {
        final Params params = new Params();
        params.addParam("buildingId", pb.getBuildingId());
        params.addParam("playerId", pb.getPlayerId());
        params.addParam("lv", pb.getLv());
        params.addParam("updateTime", pb.getUpdateTime());
        params.addParam("state", pb.getState());
        params.addParam("isNew", pb.getIsNew());
        params.addParam("eventId", pb.getEventId());
        params.addParam("speedUpNum", pb.getSpeedUpNum());
        return this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuilding.update", params);
    }
    
    @Override
	public void upgradeBuildingLv(final int playerId, final int lv) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("lv", lv);
        this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuilding.upgradeBuildingLv", params);
    }
    
    @Override
	public List<PlayerBuilding> getPlayerBuildingWithoutEvent(final int playerId) {
        return (List<PlayerBuilding>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuilding.getPlayerBuildingWithoutEvent", (Object)playerId);
    }
    
    @Override
	public List<PlayerBuilding> getPlayerBuildingWithoutEvent2(final int playerId, final int areaId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("areaId", areaId);
        return (List<PlayerBuilding>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuilding.getPlayerBuildingWithoutEvent2", (Object)params);
    }
    
    @Override
	public List<PlayerBuilding> getPlayerBuildingWithEvent(final int playerId) {
        return (List<PlayerBuilding>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuilding.getPlayerBuildingWithEvent", (Object)playerId);
    }
    
    @Override
	public PlayerBuilding getNextBuildingWithEvent(final int playerId, final int buildingId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("buildingId", buildingId);
        return (PlayerBuilding)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuilding.getNextBuildingWithEvent", (Object)params);
    }
}
