package com.reign.gcld.task.dao;

import com.reign.gcld.task.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerTaskDao")
public class PlayerTaskDao extends BaseDao<PlayerTask> implements IPlayerTaskDao
{
    @Override
	public PlayerTask read(final int vId) {
        return (PlayerTask)this.getSqlSession().selectOne("com.reign.gcld.task.domain.PlayerTask.read", (Object)vId);
    }
    
    @Override
	public PlayerTask readForUpdate(final int vId) {
        return (PlayerTask)this.getSqlSession().selectOne("com.reign.gcld.task.domain.PlayerTask.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerTask> getModels() {
        return (List<PlayerTask>)this.getSqlSession().selectList("com.reign.gcld.task.domain.PlayerTask.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.task.domain.PlayerTask.getModelSize");
    }
    
    @Override
	public int create(final PlayerTask playerTask) {
        return this.getSqlSession().insert("com.reign.gcld.task.domain.PlayerTask.create", playerTask);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.task.domain.PlayerTask.deleteById", vId);
    }
    
    @Override
	public int update(final PlayerTask playerTask) {
        return this.getSqlSession().update("com.reign.gcld.task.domain.PlayerTask.update", playerTask);
    }
    
    @Override
	public int addProcess(final int vId, final int process) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("process", process);
        return this.getSqlSession().update("com.reign.gcld.task.domain.PlayerTask.addProcess", params);
    }
    
    @Override
	public int clearDailyTask(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", 2);
        return this.getSqlSession().delete("com.reign.gcld.task.domain.PlayerTask.clearDailyTask", params);
    }
    
    @Override
	public List<PlayerTask> getPlayerTasks(final int playerId) {
        return (List<PlayerTask>)this.getSqlSession().selectList("com.reign.gcld.task.domain.PlayerTask.getPlayerTasks", (Object)playerId);
    }
    
    @Override
	public PlayerTask getCurMainTask(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", 1);
        return (PlayerTask)this.getSqlSession().selectOne("com.reign.gcld.task.domain.PlayerTask.getCurMainTask", (Object)params);
    }
    
    @Override
	public PlayerTask getDailyTask(final int playerId, final int group, final int index) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("group", group);
        params.addParam("index", index);
        params.addParam("type", 2);
        return (PlayerTask)this.getSqlSession().selectOne("com.reign.gcld.task.domain.PlayerTask.getDailyTask", (Object)params);
    }
    
    @Override
	public PlayerTask getBranchTask(final int playerId, final int group, final int index) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("group", group);
        params.addParam("index", index);
        params.addParam("type", 3);
        return (PlayerTask)this.getSqlSession().selectOne("com.reign.gcld.task.domain.PlayerTask.getBranchTask", (Object)params);
    }
    
    @Override
	public void resetDailyTask(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", 2);
        params.addParam("process", 0);
        params.addParam("state", 1);
        this.getSqlSession().update("com.reign.gcld.task.domain.PlayerTask.resetDailyTask", params);
    }
    
    @Override
	public void resetMainTask(final int playerId, final int taskId) {
        final Params params = new Params();
        params.addParam("type", 1);
        params.addParam("playerId", playerId);
        params.addParam("taskId", taskId);
        this.getSqlSession().update("com.reign.gcld.task.domain.PlayerTask.resetMainTask", params);
    }
    
    @Override
	public List<PlayerTask> getDisPlayPlayerTask(final int playerId) {
        final List<PlayerTask> ptList = this.getPlayerTasks(playerId);
        if (ptList.size() <= 2) {
            return ptList;
        }
        final List<PlayerTask> displayList = new ArrayList<PlayerTask>();
        PlayerTask pt = null;
        for (int i = 0; i < ptList.size(); ++i) {
            pt = ptList.get(i);
            if (1 == pt.getType()) {
                displayList.add(pt);
                break;
            }
        }
        long max = Long.MAX_VALUE;
        int index = 0;
        for (int j = 0; j < ptList.size(); ++j) {
            pt = ptList.get(j);
            if (3 == pt.getType() && pt.getStartTime() < max) {
                index = j;
                max = pt.getStartTime();
            }
        }
        displayList.add(ptList.get(index));
        return displayList;
    }
}
