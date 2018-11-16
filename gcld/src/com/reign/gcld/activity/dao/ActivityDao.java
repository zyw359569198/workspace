package com.reign.gcld.activity.dao;

import com.reign.gcld.activity.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("activityDao")
public class ActivityDao extends BaseDao<Activity> implements IActivityDao
{
    @Override
	public Activity read(final int vId) {
        return (Activity)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.Activity.read", (Object)vId);
    }
    
    @Override
	public Activity readForUpdate(final int vId) {
        return (Activity)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.Activity.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<Activity> getModels() {
        return (List<Activity>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.Activity.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.Activity.getModelSize");
    }
    
    @Override
	public int create(final Activity activity) {
        return this.getSqlSession().insert("com.reign.gcld.activity.domain.Activity.create", activity);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.Activity.deleteById", vId);
    }
    
    @Override
	public int updateInfo(final int vId, final Date startTime, final Date endTime, final String paramsInfo) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("startTime", startTime).addParam("endTime", endTime).addParam("paramsInfo", paramsInfo);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.Activity.updateInfo", params);
    }
    
    @Override
	public Map<Integer, Activity> getActivityMap() {
        return (Map<Integer, Activity>)this.getSqlSession().selectMap("com.reign.gcld.activity.domain.Activity.getActivityMap", "vId");
    }
}
