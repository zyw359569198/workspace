package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("yellowTurbansDao")
public class YellowTurbansDao extends BaseDao<YellowTurbans> implements IYellowTurbansDao
{
    @Override
	public YellowTurbans read(final int vId) {
        return (YellowTurbans)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.YellowTurbans.read", (Object)vId);
    }
    
    @Override
	public YellowTurbans readForUpdate(final int vId) {
        return (YellowTurbans)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.YellowTurbans.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<YellowTurbans> getModels() {
        return (List<YellowTurbans>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.YellowTurbans.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.YellowTurbans.getModelSize");
    }
    
    @Override
	public int create(final YellowTurbans yellowTurbans) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.YellowTurbans.create", yellowTurbans);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.YellowTurbans.deleteById", vId);
    }
    
    @Override
	public int batchCreate(final List<YellowTurbans> list) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.YellowTurbans.batchCreate", list);
    }
    
    @Override
	public List<YellowTurbans> getYellowTurbansByCityId(final int locationId) {
        return (List<YellowTurbans>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.YellowTurbans.getYellowTurbansByCityId", (Object)locationId);
    }
    
    @Override
	public int updateState(final Integer vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.YellowTurbans.updateState", params);
    }
    
    @Override
	public int updateHpAndTacticVal(final Integer vId, final int hp, final int tacticVal) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("hp", hp).addParam("tacticVal", tacticVal);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.YellowTurbans.updateHpAndTacticVal", params);
    }
    
    @Override
	public int deleteByCityId(final int locationId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.YellowTurbans.deleteByCityId", locationId);
    }
    
    @Override
	public int getMaxVid() {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.YellowTurbans.getMaxVid");
    }
    
    @Override
	public int resetStateByLocationAndState(final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.YellowTurbans.resetStateByLocationAndState", params);
    }
    
    @Override
	public int resetAllState() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.YellowTurbans.resetAllState");
    }
}
