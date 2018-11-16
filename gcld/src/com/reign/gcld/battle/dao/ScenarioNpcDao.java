package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("scenarioNpcDao")
public class ScenarioNpcDao extends BaseDao<ScenarioNpc> implements IScenarioNpcDao
{
    @Override
	public ScenarioNpc read(final int vId) {
        return (ScenarioNpc)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.ScenarioNpc.read", (Object)vId);
    }
    
    @Override
	public ScenarioNpc readForUpdate(final int vId) {
        return (ScenarioNpc)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.ScenarioNpc.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<ScenarioNpc> getModels() {
        return (List<ScenarioNpc>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.ScenarioNpc.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.ScenarioNpc.getModelSize");
    }
    
    @Override
	public int create(final ScenarioNpc scenarioNpc) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.ScenarioNpc.create", scenarioNpc);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.ScenarioNpc.deleteById", vId);
    }
    
    @Override
	public int batchCreate(final List<ScenarioNpc> list) {
        if (list.isEmpty()) {
            return 0;
        }
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ScenarioNpc.batchCreate", list);
    }
    
    @Override
	public int updateLocationAndState(final Integer vId, final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ScenarioNpc.updateLocationAndState", params);
    }
    
    @Override
	public int updateHpAndTacticVal(final Integer vId, final int hp, final int tacticVal) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("hp", hp).addParam("tacticVal", tacticVal);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ScenarioNpc.updateHpAndTacticVal", params);
    }
    
    @Override
	public int resetAllState() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ScenarioNpc.resetAllState");
    }
    
    @Override
	public int resetStateByLocationAndState(final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ScenarioNpc.resetStateByLocationAndState", params);
    }
    
    @Override
	public List<ScenarioNpc> getByPlayerIdLocationId(final int playerId, final int locationId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("locationId", locationId);
        return (List<ScenarioNpc>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.ScenarioNpc.getByPlayerIdLocationId", (Object)params);
    }
    
    @Override
	public int updateState(final Integer vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.ScenarioNpc.updateState", params);
    }
    
    @Override
	public int getMaxVid() {
        final Integer maxId = (Integer)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.ScenarioNpc.getMaxVid");
        return (maxId == null) ? 1 : maxId;
    }
    
    @Override
	public int deleteAllInThisCity(final int playerId, final int locationId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("locationId", locationId);
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.ScenarioNpc.deleteAllInThisCity", params);
    }
    
    @Override
	public int deleteAll(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.ScenarioNpc.deleteAll", playerId);
    }
}
