package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerKillInfoDao")
public class PlayerKillInfoDao extends BaseDao<PlayerKillInfo> implements IPlayerKillInfoDao
{
    @Override
	public PlayerKillInfo read(final int vId) {
        return (PlayerKillInfo)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerKillInfo.read", (Object)vId);
    }
    
    @Override
	public PlayerKillInfo readForUpdate(final int vId) {
        return (PlayerKillInfo)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerKillInfo.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerKillInfo> getModels() {
        return (List<PlayerKillInfo>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerKillInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerKillInfo.getModelSize");
    }
    
    @Override
	public int create(final PlayerKillInfo playerKillInfo) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.PlayerKillInfo.create", playerKillInfo);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerKillInfo.deleteById", vId);
    }
    
    @Override
	public PlayerKillInfo getByTodayInfo(final int playerId, final String date) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("date", date);
        final List<PlayerKillInfo> list = (List<PlayerKillInfo>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerKillInfo.getByTodayInfo", (Object)params);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }
    
    @Override
	public List<PlayerKillInfo> getListByKillNum(final int forceId, final String date) {
        final Params params = new Params();
        params.addParam("forceId", forceId).addParam("date", date);
        return (List<PlayerKillInfo>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerKillInfo.getListByKillNum", (Object)params);
    }
    
    @Override
	public int updateKillNum(final int playerId, final int killNum, final String date) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("killNum", killNum).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerKillInfo.updateKillNum", params);
    }
    
    @Override
	public int deleteByDate(final String date) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerKillInfo.deleteByDate", date);
    }
    
    @Override
	public List<PlayerKillInfo> getListByPlayerId(final int playerId) {
        return (List<PlayerKillInfo>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerKillInfo.getListByPlayerId", (Object)playerId);
    }
    
    @Override
	public int updateBoxInfo(final int playerId, final String boxInfo2) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("boxInfo", boxInfo2);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerKillInfo.updateBoxInfo", params);
    }
    
    @Override
	public int resetBoxInfo(final String string, final String date) {
        final Params params = new Params();
        params.addParam("boxInfo", string);
        params.addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerKillInfo.resetBoxInfo", params);
    }
    
    @Override
	public List<PlayerKillInfo> getTodayInfo(final String dateStr) {
        return (List<PlayerKillInfo>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerKillInfo.getTodayInfo", (Object)dateStr);
    }
}
