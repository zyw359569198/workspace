package com.reign.gcld.slave.dao;

import com.reign.gcld.slave.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("slaveholderDao")
public class SlaveholderDao extends BaseDao<Slaveholder> implements ISlaveholderDao
{
    @Override
	public Slaveholder read(final int playerId) {
        return (Slaveholder)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.Slaveholder.read", (Object)playerId);
    }
    
    @Override
	public Slaveholder readForUpdate(final int playerId) {
        return (Slaveholder)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.Slaveholder.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<Slaveholder> getModels() {
        return (List<Slaveholder>)this.getSqlSession().selectList("com.reign.gcld.slave.domain.Slaveholder.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.Slaveholder.getModelSize");
    }
    
    @Override
	public int create(final Slaveholder slaveholder) {
        return this.getSqlSession().insert("com.reign.gcld.slave.domain.Slaveholder.create", slaveholder);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.slave.domain.Slaveholder.deleteById", playerId);
    }
    
    @Override
	public int updateLimbo(final int playerId, final int prisonLv) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("prisonLv", prisonLv);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.updateLimbo", params);
    }
    
    @Override
	public int clearSlaveDayNum() {
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.clearSlaveDayNum");
    }
    
    @Override
	public int updateLashLv(final int playerId, final int lashLv) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("lashLv", lashLv);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.updateLashLv", params);
    }
    
    @Override
	public int updateGrabNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.updateGrabNum", playerId);
    }
    
    @Override
	public int addAutoLashExp(final int playerId, final int addExp) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("addExp", addExp);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.addAutoLashExp", params);
    }
    
    @Override
	public int addPoint(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.addPoint", playerId);
    }
    
    @Override
	public int updateExpireTime(final int playerId, final Date time) {
        final Params params = new Params();
        params.addParam("time", time);
        params.addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.updateExpireTime", params);
    }
    
    @Override
	public int updateTrailGold(final int playerId, final int gold) {
        final Params params = new Params();
        params.addParam("gold", gold);
        params.addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.updateTrailGold", params);
    }
    
    @Override
	public int resetExpireTimeAndTrailGold(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.resetExpireTimeAndTrailGold", playerId);
    }
    
    @Override
	public int addExpireTimeAndTrailGold(final int playerId, final Date time, final int gold) {
        final Params params = new Params();
        params.addParam("gold", gold);
        params.addParam("time", time);
        params.addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.Slaveholder.addExpireTimeAndTrailGold", params);
    }
}
