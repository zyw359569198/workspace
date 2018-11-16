package com.reign.gcld.activity.dao;

import com.reign.gcld.activity.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("activityNpcDao")
public class ActivityNpcDao extends BaseDao<ActivityNpc> implements IActivityNpcDao
{
    @Override
	public ActivityNpc read(final int vId) {
        return (ActivityNpc)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.ActivityNpc.read", (Object)vId);
    }
    
    @Override
	public ActivityNpc readForUpdate(final int vId) {
        return (ActivityNpc)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.ActivityNpc.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<ActivityNpc> getModels() {
        return (List<ActivityNpc>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.ActivityNpc.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.ActivityNpc.getModelSize");
    }
    
    @Override
	public int create(final ActivityNpc activityNpc) {
        return this.getSqlSession().insert("com.reign.gcld.activity.domain.ActivityNpc.create", activityNpc);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.ActivityNpc.deleteById", vId);
    }
    
    @Override
	public int batchCreate(final List<ActivityNpc> list) {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.ActivityNpc.batchCreate", list);
    }
    
    @Override
	public List<ActivityNpc> getActivityNpcsByLocationId(final Integer locationId) {
        return (List<ActivityNpc>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.ActivityNpc.getActivityNpcsByLocationId", (Object)locationId);
    }
    
    @Override
	public List<ActivityNpc> getActivityNpcsByLocationIdAndForceIdExclude(final Integer locationId, final Integer forceId, final Integer type) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("forceId", forceId).addParam("type", type);
        return (List<ActivityNpc>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.ActivityNpc.getActivityNpcsByLocationIdAndForceIdExclude", (Object)params);
    }
    
    @Override
	public int updateLocationAndState(final Integer vId, final Integer locationId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.ActivityNpc.updateLocationAndState", params);
    }
    
    @Override
	public int updateState(final Integer vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.ActivityNpc.updateState", params);
    }
    
    @Override
	public int updateHpAndTacticVal(final int vId, final int hp, final int tacticVal) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("hp", hp).addParam("tacticVal", tacticVal);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.ActivityNpc.updateHpAndTacticVal", params);
    }
    
    @Override
	public int resetStateByLocationAndState(final int locationId, final int state) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.ActivityNpc.resetStateByLocationAndState", params);
    }
    
    @Override
	public int resetAllState() {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.ActivityNpc.resetAllState");
    }
    
    @Override
	public int getMaxVid() {
        final Integer maxId = (Integer)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.ActivityNpc.getMaxVid");
        return (maxId == null) ? 1 : maxId;
    }
    
    @Override
	public int deleteAll() {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.ActivityNpc.deleteAll");
    }
}
