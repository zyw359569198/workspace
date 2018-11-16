package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("officerBuildingInfoDao")
public class OfficerBuildingInfoDao extends BaseDao<OfficerBuildingInfo> implements IOfficerBuildingInfoDao
{
    @Override
	public OfficerBuildingInfo read(final int vId) {
        return (OfficerBuildingInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerBuildingInfo.read", (Object)vId);
    }
    
    @Override
	public OfficerBuildingInfo readForUpdate(final int vId) {
        return (OfficerBuildingInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerBuildingInfo.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<OfficerBuildingInfo> getModels() {
        return (List<OfficerBuildingInfo>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.OfficerBuildingInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerBuildingInfo.getModelSize");
    }
    
    @Override
	public int create(final OfficerBuildingInfo officerBuildingInfo) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.OfficerBuildingInfo.create", officerBuildingInfo);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.OfficerBuildingInfo.deleteById", vId);
    }
    
    @Override
	public OfficerBuildingInfo getByBuildingId(final int forceId, final int buildingId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("buildingId", buildingId);
        return (OfficerBuildingInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerBuildingInfo.getByBuildingId", (Object)params);
    }
    
    @Override
	public OfficerBuildingInfo getByPlayerId(final int playerId) {
        return (OfficerBuildingInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerBuildingInfo.getByPlayerId", (Object)playerId);
    }
    
    @Override
	public int updateState(final int forceId, final int buildingId, final int state) {
        final Params params = new Params();
        params.addParam("buildingId", buildingId).addParam("state", state).addParam("forceId", forceId);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerBuildingInfo.updateState", params);
    }
    
    @Override
	public int updateInfo(final OfficerBuildingInfo obi) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerBuildingInfo.updateInfo", obi);
    }
    
    @Override
	public int updatePlayerId(final int playerId, final int vId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("vId", vId);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerBuildingInfo.updatePlayerId", params);
    }
    
    @Override
	public void addMemberNum(final int forceId, final int buildingId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("buildingId", buildingId);
        this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerBuildingInfo.addMemberNum", params);
    }
    
    @Override
	public void minuseMemberNum(final int forceId, final int buildingId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("buildingId", buildingId);
        this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerBuildingInfo.minuseMemberNum", params);
    }
    
    @Override
	public int update(final int forceId, final int buildingId, final int playerId, final int count) {
        final Params params = new Params();
        params.addParam("forceId", forceId).addParam("buildingId", buildingId).addParam("playerId", playerId).addParam("count", count);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerBuildingInfo.update", params);
    }
    
    @Override
	public int updateAutoPass(final int forceId, final int buildingId, final int state) {
        final Params params = new Params();
        params.addParam("forceId", forceId).addParam("buildingId", buildingId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerBuildingInfo.updateAutoPass", params);
    }
    
    @Override
	public List<OfficerBuildingInfo> getByForceId(final int forceId) {
        return (List<OfficerBuildingInfo>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.OfficerBuildingInfo.getByForceId", (Object)forceId);
    }
    
    @Override
	public int deleteByPlayerId(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.OfficerBuildingInfo.deleteByPlayerId", playerId);
    }
}
