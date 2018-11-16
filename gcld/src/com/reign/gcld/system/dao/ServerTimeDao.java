package com.reign.gcld.system.dao;

import com.reign.gcld.system.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("serverTimeDao")
public class ServerTimeDao extends BaseDao<ServerTime> implements IServerTimeDao
{
    @Override
	public ServerTime read(final int vId) {
        return (ServerTime)this.getSqlSession().selectOne("com.reign.gcld.system.domain.ServerTime.read", (Object)vId);
    }
    
    @Override
	public ServerTime readForUpdate(final int vId) {
        return (ServerTime)this.getSqlSession().selectOne("com.reign.gcld.system.domain.ServerTime.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<ServerTime> getModels() {
        return (List<ServerTime>)this.getSqlSession().selectList("com.reign.gcld.system.domain.ServerTime.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.system.domain.ServerTime.getModelSize");
    }
    
    @Override
	public int create(final ServerTime serverTime) {
        return this.getSqlSession().insert("com.reign.gcld.system.domain.ServerTime.create", serverTime);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.syste.domain.ServerTime.deleteById", vId);
    }
    
    @Override
	public int updateEndTime(final int vId, final Date endTime) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("endTime", endTime);
        return this.getSqlSession().update("com.reign.gcld.system.domain.ServerTime.updateEndTime", params);
    }
    
    @Override
	public ServerTime getLastServerTime() {
        return (ServerTime)this.getSqlSession().selectOne("com.reign.gcld.system.domain.ServerTime.getLastServerTime");
    }
}
