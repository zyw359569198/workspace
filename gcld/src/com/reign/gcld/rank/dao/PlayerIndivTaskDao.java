package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerIndivTaskDao")
public class PlayerIndivTaskDao extends BaseDao<PlayerIndivTask> implements IPlayerIndivTaskDao
{
    @Override
	public PlayerIndivTask read(final int playerId) {
        return (PlayerIndivTask)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerIndivTask.read", (Object)playerId);
    }
    
    @Override
	public PlayerIndivTask readForUpdate(final int playerId) {
        return (PlayerIndivTask)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerIndivTask.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerIndivTask> getModels() {
        return (List<PlayerIndivTask>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerIndivTask.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerIndivTask.getModelSize");
    }
    
    @Override
	public int create(final PlayerIndivTask playerIndivTask) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.PlayerIndivTask.create", playerIndivTask);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerIndivTask.deleteById", playerId);
    }
    
    @Override
	public int updateTaskInfo(final String string, final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("info", string);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerIndivTask.updateTaskInfo", params);
    }
    
    @Override
	public void deleteAll() {
        this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerIndivTask.deleteAll");
    }
}
