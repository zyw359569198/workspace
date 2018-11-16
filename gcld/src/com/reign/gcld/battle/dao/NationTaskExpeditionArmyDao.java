package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("nationTaskExpeditionArmyDao")
public class NationTaskExpeditionArmyDao extends BaseDao<NationTaskExpeditionArmy> implements INationTaskExpeditionArmyDao
{
    @Override
	public NationTaskExpeditionArmy read(final int vId) {
        return (NationTaskExpeditionArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.read", (Object)vId);
    }
    
    @Override
	public NationTaskExpeditionArmy readForUpdate(final int vId) {
        return (NationTaskExpeditionArmy)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<NationTaskExpeditionArmy> getModels() {
        return (List<NationTaskExpeditionArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.getModelSize");
    }
    
    @Override
	public int create(final NationTaskExpeditionArmy nationTaskExpeditionArmy) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.create", nationTaskExpeditionArmy);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.deleteById", vId);
    }
    
    @Override
	public int batchCreate(final List<NationTaskExpeditionArmy> list) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.batchCreate", list);
    }
    
    @Override
	public List<NationTaskExpeditionArmy> getNationTaskEAsByLocationId(final Integer locationId) {
        return (List<NationTaskExpeditionArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.getNationTaskEAsByLocationId", (Object)locationId);
    }
    
    @Override
	public int updateLocationAndState(final Integer vId, final Integer locationId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.updateLocationAndState", params);
    }
    
    @Override
	public int updateState(final Integer vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.updateState", params);
    }
    
    @Override
	public int updateHpAndTacticVal(final int vId, final int hp, final int tacticVal) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("hp", hp).addParam("tacticVal", tacticVal);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.updateHpAndTacticVal", params);
    }
    
    @Override
	public int resetStateByLocationAndState(final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.resetStateByLocationAndState", params);
    }
    
    @Override
	public int resetAllState() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.resetAllState");
    }
    
    @Override
	public List<NationTaskExpeditionArmy> getNationTaskDefenceEAsByLocationId(final Integer locationId) {
        return (List<NationTaskExpeditionArmy>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.getNationTaskDefenceEAsByLocationId", (Object)locationId);
    }
    
    @Override
	public int getMaxVid() {
        final Integer maxId = (Integer)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.getMaxVid");
        return (maxId == null) ? 1 : maxId;
    }
    
    @Override
	public int deleteAllFreeTicketArmy() {
        final int type = 2;
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.NationTaskExpeditionArmy.deleteAllFreeTicketArmy", type);
    }
}
