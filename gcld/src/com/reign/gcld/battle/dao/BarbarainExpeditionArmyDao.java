package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("barbarainExpeditionArmyDao")
public class BarbarainExpeditionArmyDao extends BaseDao<BarbarainExpeditionArmy> implements IBarbarainExpeditionArmyDao
{
    @Override
	public BarbarainExpeditionArmy read(final int vId) {
        return (BarbarainExpeditionArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.read", (Object)vId);
    }
    
    @Override
	public BarbarainExpeditionArmy readForUpdate(final int vId) {
        return (BarbarainExpeditionArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<BarbarainExpeditionArmy> getModels() {
        return (List<BarbarainExpeditionArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.getModelSize");
    }
    
    @Override
	public int create(final BarbarainExpeditionArmy barbarainExpeditionArmy) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.create", barbarainExpeditionArmy);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.deleteById", vId);
    }
    
    @Override
	public int batchCreate(final List<BarbarainExpeditionArmy> list) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.batchCreate", list);
    }
    
    @Override
	public List<BarbarainExpeditionArmy> getBarEAsByLocationId(final Integer locationId) {
        return (List<BarbarainExpeditionArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.getBarEAsByLocationId", (Object)locationId);
    }
    
    @Override
	public int updateLocationAndState(final Integer vId, final Integer locationId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.updateLocationAndState", params);
    }
    
    @Override
	public int updateState(final Integer vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.updateState", params);
    }
    
    @Override
	public int updateHpAndTacticVal(final int vId, final int hp, final int tacticVal) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("hp", hp).addParam("tacticVal", tacticVal);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.updateHpAndTacticVal", params);
    }
    
    @Override
	public int resetStateByLocationAndState(final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.resetStateByLocationAndState", params);
    }
    
    @Override
	public int resetAllState() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.resetAllState");
    }
    
    @Override
	public int removeAllInThisCity(final Integer cityId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.BarbarainExpeditionArmy.removeAllInThisCity", cityId);
    }
}
