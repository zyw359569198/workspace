package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("nationTaskDao")
public class NationTaskDao extends BaseDao<NationTask> implements INationTaskDao
{
    @Override
	public NationTask read(final int nationTaskId) {
        return (NationTask)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.NationTask.read", (Object)nationTaskId);
    }
    
    @Override
	public NationTask readForUpdate(final int nationTaskId) {
        return (NationTask)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.NationTask.readForUpdate", (Object)nationTaskId);
    }
    
    @Override
	public List<NationTask> getModels() {
        return (List<NationTask>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.NationTask.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.NationTask.getModelSize");
    }
    
    @Override
	public int create(final NationTask nationTask) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.NationTask.create", nationTask);
    }
    
    @Override
	public int deleteById(final int nationTaskId) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.NationTask.deleteById", nationTaskId);
    }
    
    @Override
	public void updateIsWin(final int taskId, final int isWin) {
        final Params params = new Params();
        params.addParam("taskId", taskId);
        params.addParam("isWin", isWin);
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.updateIsWin", params);
    }
    
    @Override
	public void updateIsWinAndFinishTime(final int taskId, final int isWin, final long finishtime) {
        final Params params = new Params();
        params.addParam("taskId", taskId);
        params.addParam("isWin", isWin);
        params.addParam("finishtime", finishtime);
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.updateIsWinAndFinishTime", params);
    }
    
    @Override
	public NationTask getByForceAndTarget(final int forceId, final int target) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("target", target);
        return (NationTask)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.NationTask.getByForceAndTarget", (Object)params);
    }
    
    @Override
	public void resetTaskIsWin(final Integer nationTaskId, final int forceId) {
        final int id = nationTaskId % 2;
        final Params params = new Params();
        params.addParam("id", id);
        params.addParam("forceId", forceId);
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.resetTaskIsWin1", params);
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.resetTaskIsWin2", params);
    }
    
    @Override
	public void deleteAllTasks() {
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.deleteAllTasks");
    }
    
    @Override
	public NationTask getByForce(final int forceId) {
        return (NationTask)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.NationTask.getByForce", (Object)forceId);
    }
    
    @Override
	public List<NationTask> getListByForce(final int forceId) {
        return (List<NationTask>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.NationTask.getByForce", (Object)forceId);
    }
    
    @Override
	public void updateTarget(final int i, final Integer cm) {
        final Params params = new Params();
        params.addParam("forceId", i);
        params.addParam("target", cm);
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.updateTarget", params);
    }
    
    @Override
	public void updateAttType(final int taskId, final int winTimes) {
        final Params params = new Params();
        params.addParam("taskId", taskId);
        params.addParam("winTimes", winTimes);
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.updateAttType", params);
    }
    
    @Override
	public void updateManZuSaoDangTaskRelateInfo(final int forceId, final String info) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("info", info);
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.updateManZuSaoDangTaskRelateInfo", params);
    }
    
    @Override
	public void updateTaskRelativeInfo(final String info) {
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.updateTaskRelativeInfo", info);
    }
    
    @Override
	public void resetTaskIsWinByForceId(final int forceId) {
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.setTaskWinByForceId", forceId);
        this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.setTaskLoseByForceId", forceId);
    }
    
    @Override
	public int updateIsWinAndEndTime(final Integer nationTaskId, final int i, final Date endtime) {
        final Params params = new Params();
        params.addParam("nationTaskId", nationTaskId).addParam("isWin", i).addParam("endTime", endtime);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.updateIsWinAndEndTime", params);
    }
    
    @Override
	public int resetIsWin(final int forceId, final int isWin) {
        final Params params = new Params();
        params.addParam("forceId", forceId).addParam("isWin", isWin);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.NationTask.resetIsWin", params);
    }
}
