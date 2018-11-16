package com.reign.gcld.building.dao;

import com.reign.gcld.building.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerResourceAdditionDao")
public class PlayerResourceAdditionDao extends BaseDao<PlayerResourceAddition> implements IPlayerResourceAdditionDao
{
    @Override
	public PlayerResourceAddition read(final int vId) {
        return (PlayerResourceAddition)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerResourceAddition.read", (Object)vId);
    }
    
    @Override
	public PlayerResourceAddition readForUpdate(final int vId) {
        return (PlayerResourceAddition)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerResourceAddition.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerResourceAddition> getModels() {
        return (List<PlayerResourceAddition>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerResourceAddition.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerResourceAddition.getModelSize");
    }
    
    @Override
	public int create(final PlayerResourceAddition playerResourceAddition) {
        return this.getSqlSession().insert("com.reign.gcld.building.domain.PlayerResourceAddition.create", playerResourceAddition);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.building.domain.PlayerResourceAddition.deleteById", vId);
    }
    
    @Override
	public PlayerResourceAddition getByPlayerIdAndType(final int playerId, final int resourceType) {
        final Params param = new Params();
        param.addParam("playerId", playerId);
        param.addParam("resourceType", resourceType);
        return (PlayerResourceAddition)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerResourceAddition.getByPlayerIdAndType", (Object)param);
    }
    
    @Override
	public void update(final int vId, final Date endTime, final int timeType, final int additionMode, final int taskId) {
        final Params param = new Params();
        param.addParam("vId", vId);
        param.addParam("endTime", endTime);
        param.addParam("timeType", timeType);
        param.addParam("additionMode", additionMode);
        param.addParam("taskId", taskId);
        this.getSqlSession().update("com.reign.gcld.building.domain.PlayerResourceAddition.update", param);
    }
    
    @Override
	public List<PlayerResourceAddition> getListByPlayerId(final int playerId) {
        return (List<PlayerResourceAddition>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerResourceAddition.getListByPlayerId", (Object)playerId);
    }
    
    @Override
	public List<PlayerResourceAddition> getListByTime(final Date endTime) {
        return (List<PlayerResourceAddition>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerResourceAddition.getListByTime", (Object)endTime);
    }
}
