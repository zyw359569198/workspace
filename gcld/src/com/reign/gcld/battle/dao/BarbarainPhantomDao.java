package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("barbarainPhantomDao")
public class BarbarainPhantomDao extends BaseDao<BarbarainPhantom> implements IBarbarainPhantomDao
{
    @Override
	public BarbarainPhantom read(final int vId) {
        return (BarbarainPhantom)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BarbarainPhantom.read", (Object)vId);
    }
    
    @Override
	public BarbarainPhantom readForUpdate(final int vId) {
        return (BarbarainPhantom)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BarbarainPhantom.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<BarbarainPhantom> getModels() {
        return (List<BarbarainPhantom>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.BarbarainPhantom.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BarbarainPhantom.getModelSize");
    }
    
    @Override
	public int create(final BarbarainPhantom barbarainPhantom) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.BarbarainPhantom.create", barbarainPhantom);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.BarbarainPhantom.deleteById", vId);
    }
    
    @Override
	public List<BarbarainPhantom> getBarPhantomByLocationId(final int locationId) {
        return (List<BarbarainPhantom>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.BarbarainPhantom.getBarPhantomByLocationId", (Object)locationId);
    }
    
    @Override
	public List<BarbarainPhantom> getBarPhantomByForceId(final int forceId) {
        return (List<BarbarainPhantom>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.BarbarainPhantom.getBarPhantomByForceId", (Object)forceId);
    }
    
    @Override
	public int updateState(final int vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainPhantom.updateState", params);
    }
    
    @Override
	public int updateHpTacticVal(final int vId, final int hp, final int tacticVal) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("hp", hp).addParam("tacticVal", tacticVal);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainPhantom.updateHpTacticVal", params);
    }
    
    @Override
	public int resetStateByLocationAndState(final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainPhantom.resetStateByLocationAndState", params);
    }
    
    @Override
	public int resetStateAll() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainPhantom.resetStateAll");
    }
    
    @Override
	public int callProcedureBatchInsert(final int state, final int cityId, final int forceId, final int barbarainId, final String armyIds, final String hps, final int tacticval, final String name) {
        final Params params = new Params();
        params.addParam("state", state).addParam("cityId", cityId).addParam("forceId", forceId).addParam("barbarainId", barbarainId).addParam("armyIds", armyIds).addParam("hps", hps).addParam("tacticval", tacticval).addParam("name", name);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainPhantom.callProcedureBatchInsert", params);
    }
    
    @Override
	public int batchCreate(final List<BarbarainPhantom> list) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainPhantom.batchCreate", list);
    }
    
    @Override
	public int getMaxVid() {
        final Integer maxId = (Integer)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BarbarainPhantom.getMaxVid");
        return (maxId == null) ? 1 : maxId;
    }
    
    @Override
	public int removeAllInThisCity(final Integer cityId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.BarbarainPhantom.removeAllInThisCity", cityId);
    }
}
