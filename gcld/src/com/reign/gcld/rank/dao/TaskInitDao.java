package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("taskInitDao")
public class TaskInitDao extends BaseDao<TaskInit> implements ITaskInitDao
{
    @Override
	public TaskInit read(final int id) {
        return (TaskInit)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskInit.read", (Object)id);
    }
    
    @Override
	public TaskInit readForUpdate(final int id) {
        return (TaskInit)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskInit.readForUpdate", (Object)id);
    }
    
    @Override
	public List<TaskInit> getModels() {
        return (List<TaskInit>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.TaskInit.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.TaskInit.getModelSize");
    }
    
    @Override
	public int create(final TaskInit taskInit) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.TaskInit.create", taskInit);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.TaskInit.deleteById", id);
    }
    
    @Override
	public void batchCreate(final List<TaskInit> list) {
        this.getSqlSession().update("com.reign.gcld.rank.domain.TaskInit.batchCreate", list);
    }
    
    @Override
	public void deleteAlls() {
        this.getSqlSession().update("com.reign.gcld.rank.domain.TaskInit.deleteAlls");
    }
    
    @Override
	public int updateType(final int composeId, final int type) {
        final Params params = new Params();
        params.addParam("composeId", composeId);
        params.addParam("type", type);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.TaskInit.updateType", params);
    }
}
