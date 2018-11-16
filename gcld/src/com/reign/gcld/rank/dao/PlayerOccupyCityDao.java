package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerOccupyCityDao")
public class PlayerOccupyCityDao extends BaseDao<PlayerOccupyCity> implements IPlayerOccupyCityDao
{
    @Override
	public PlayerOccupyCity read(final int vid) {
        return (PlayerOccupyCity)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerOccupyCity.read", (Object)vid);
    }
    
    @Override
	public PlayerOccupyCity readForUpdate(final int vid) {
        return (PlayerOccupyCity)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerOccupyCity.readForUpdate", (Object)vid);
    }
    
    @Override
	public List<PlayerOccupyCity> getModels() {
        return (List<PlayerOccupyCity>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerOccupyCity.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerOccupyCity.getModelSize");
    }
    
    @Override
	public int create(final PlayerOccupyCity playerOccupyCity) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.PlayerOccupyCity.create", playerOccupyCity);
    }
    
    @Override
	public int deleteById(final int vid) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerOccupyCity.deleteById", vid);
    }
    
    @Override
	public int getByPlayerId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerOccupyCity.getByPlayerId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public List<PlayerOccupyCity> getListByPlayerIdOrderByNum(final int playerId) {
        return (List<PlayerOccupyCity>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerOccupyCity.getListByPlayerIdOrderByNum", (Object)playerId);
    }
    
    @Override
	public int updateVtimes(final int playerId, final int generalId, final int times) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        params.addParam("times", times);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerOccupyCity.updateVtimes", params);
    }
    
    @Override
	public PlayerOccupyCity getInfoByPAndG(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return (PlayerOccupyCity)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerOccupyCity.getInfoByPAndG", (Object)params);
    }
    
    @Override
	public int clearTodayNum() {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerOccupyCity.clearTodayNum");
    }
    
    @Override
	public int addTodayNum(final int playerId, final int generalId, final int times) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        params.addParam("times", times);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerOccupyCity.addTodayNum", params);
    }
    
    @Override
	public int getTodayNumByPlayerId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerOccupyCity.getTodayNumByPlayerId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int clearTodayNumByPlayerId(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerOccupyCity.clearTodayNumByPlayerId", playerId);
    }
    
    @Override
	public int setTodayNumOneGeneral(final int playerId, final int todayNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("todayNum", todayNum);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerOccupyCity.setTodayNumOneGeneral", params);
    }
}
