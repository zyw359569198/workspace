package com.reign.gcld.timer.dao;

import com.reign.gcld.timer.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerJobDao")
public class PlayerJobDao extends BaseDao<PlayerJob> implements IPlayerJobDao
{
    @Override
	public PlayerJob read(final int id) {
        return (PlayerJob)this.getSqlSession().selectOne("com.reign.gcld.common.timer.domain.PlayerJob.read", (Object)id);
    }
    
    @Override
	public PlayerJob readForUpdate(final int id) {
        return (PlayerJob)this.getSqlSession().selectOne("com.reign.gcld.common.timer.domain.PlayerJob.readForUpdate", (Object)id);
    }
    
    @Override
	public List<PlayerJob> getModels() {
        return (List<PlayerJob>)this.getSqlSession().selectList("com.reign.gcld.common.timer.domain.PlayerJob.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.common.timer.domain.PlayerJob.getModelSize");
    }
    
    @Override
	public int create(final PlayerJob playerJob) {
        return this.getSqlSession().insert("com.reign.gcld.common.timer.domain.PlayerJob.create", playerJob);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.common.timer.domain.PlayerJob.deleteById", id);
    }
    
    @Override
	public int updateJobState(final int taskId, final int state) {
        final Params params = new Params();
        params.addParam("id", taskId);
        params.addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.common.timer.domain.PlayerJob.updateJobState", params);
    }
    
    @Override
	public List<PlayerJob> getJobListByState(final int state) {
        return (List<PlayerJob>)this.getSqlSession().selectList("com.reign.gcld.common.timer.domain.PlayerJob.getJobListByState", (Object)state);
    }
    
    @Override
	public int getMaxJobId() {
        final Object obj = this.getSqlSession().selectOne("com.reign.gcld.common.timer.domain.PlayerJob.getMaxJobId");
        return (int)((obj == null) ? 0 : obj);
    }
    
    @Override
	public int updateJobExeTime(final int id, final long executionTime) {
        final Params params = new Params();
        params.addParam("id", id);
        params.addParam("executionTime", executionTime);
        return this.getSqlSession().update("com.reign.gcld.common.timer.domain.PlayerJob.updateJobExeTime", params);
    }
}
