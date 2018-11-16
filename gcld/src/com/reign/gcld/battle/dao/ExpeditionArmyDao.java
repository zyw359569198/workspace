package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("expeditionArmyDao")
public class ExpeditionArmyDao extends BaseDao<ExpeditionArmy> implements IExpeditionArmyDao
{
    @Override
	public ExpeditionArmy read(final int vId) {
        return (ExpeditionArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.ExpeditionArmy.read", (Object)vId);
    }
    
    @Override
	public ExpeditionArmy readForUpdate(final int vId) {
        return (ExpeditionArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.ExpeditionArmy.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<ExpeditionArmy> getModels() {
        return (List<ExpeditionArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.ExpeditionArmy.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.ExpeditionArmy.getModelSize");
    }
    
    @Override
	public int create(final ExpeditionArmy expeditionArmy) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.ExpeditionArmy.create", expeditionArmy);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.ExpeditionArmy.deleteById", vId);
    }
    
    @Override
	public List<ExpeditionArmy> getEAsByLocationId(final int locationId) {
        return (List<ExpeditionArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.ExpeditionArmy.getEAsByLocationId", (Object)locationId);
    }
    
    @Override
	public int updateState(final Integer vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ExpeditionArmy.updateState", params);
    }
    
    @Override
	public int updateLocationAndState(final Integer vId, final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ExpeditionArmy.updateLocationAndState", params);
    }
    
    @Override
	public int resetStateByLocationAndState(final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ExpeditionArmy.resetStateByLocationAndState", params);
    }
    
    @Override
	public int updateHpAndTacticVal(final Integer vId, final int hp, final int tacticVal) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("hp", hp).addParam("tacticVal", tacticVal);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ExpeditionArmy.updateHpAndTacticVal", params);
    }
    
    @Override
	public int resetAllState() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ExpeditionArmy.resetAllState");
    }
    
    @Override
	public int deleteByLocationId(final int locationId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.ExpeditionArmy.deleteByLocationId", locationId);
    }
    
    @Override
	public int batchCreate(final List<ExpeditionArmy> list) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ExpeditionArmy.batchCreate", list);
    }
}
