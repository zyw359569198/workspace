package com.reign.gcld.building.dao;

import com.reign.gcld.building.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerBuildingWorkDao")
public class PlayerBuildingWorkDao extends BaseDao<PlayerBuildingWork> implements IPlayerBuildingWorkDao
{
    @Override
	public PlayerBuildingWork read(final int vId) {
        return (PlayerBuildingWork)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuildingWork.read", (Object)vId);
    }
    
    @Override
	public PlayerBuildingWork readForUpdate(final int vId) {
        return (PlayerBuildingWork)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuildingWork.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerBuildingWork> getModels() {
        return (List<PlayerBuildingWork>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuildingWork.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuildingWork.getModelSize");
    }
    
    @Override
	public int create(final PlayerBuildingWork playerBuildingWork) {
        return this.getSqlSession().insert("com.reign.gcld.building.domain.PlayerBuildingWork.create", playerBuildingWork);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.building.domain.PlayerBuildingWork.deleteById", vId);
    }
    
    @Override
	public PlayerBuildingWork getFreeWork(final int playerId, final Date nowDate) {
        final List<PlayerBuildingWork> buildingWorks = this.getPlayerBuildingWork(playerId);
        for (final PlayerBuildingWork pbw : buildingWorks) {
            if (pbw.getWorkState() == 0) {
                return pbw;
            }
        }
        return null;
    }
    
    @Override
	public List<PlayerBuildingWork> getPlayerBuildingWork(final int playerId) {
        return (List<PlayerBuildingWork>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuildingWork.getPlayerBuildingWork", (Object)playerId);
    }
    
    @Override
	public int assignedWork(final PlayerBuildingWork pbw) {
        return this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuildingWork.assignedWork", pbw);
    }
    
    @Override
	public PlayerBuildingWork getPlayerBuildingWork(final int playerId, final int workId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("workId", workId);
        return (PlayerBuildingWork)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuildingWork.getPlayerBuildingWorkByWorkId", (Object)params);
    }
    
    @Override
	public int resetBuildingWork(final int playerId, final int workId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("workId", workId);
        params.addParam("workState", 0);
        return this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuildingWork.resetBuildingWork", params);
    }
    
    @Override
	public int updateEndTime(final int vId, final Date endTime) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("endTime", endTime);
        return this.getSqlSession().update("com.reign.gcld.building.domain.PlayerBuildingWork.updateEndTime", params);
    }
    
    @Override
	public List<PlayerBuildingWork> getBusyWorkList(final int playerId) {
        return (List<PlayerBuildingWork>)this.getSqlSession().selectList("com.reign.gcld.building.domain.PlayerBuildingWork.getBusyWorkList", (Object)playerId);
    }
    
    @Override
	public int getBusyWorkNum(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuildingWork.getBusyWorkNum", (Object)playerId);
    }
    
    @Override
	public int getFreeWorkNum(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.building.domain.PlayerBuildingWork.getFreeWorkNum", (Object)playerId);
    }
}
